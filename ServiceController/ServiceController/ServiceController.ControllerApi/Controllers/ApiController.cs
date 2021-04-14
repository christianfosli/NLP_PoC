using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using System;
using System.Collections.Generic;
using System.Text.Json;
using System.Threading;
using System.Threading.Tasks;
using ServiceController.ControllerApi.BackgroundServices;
using ServiceController.ControllerApi.Settings;
using ServiceController.Entities.NlpService;
using ServiceController.Entities.TextService;
using ServiceController.NlpService;
using ServiceController.TextService;

namespace ServiceController.ControllerApi.Controllers
{
	[ApiController]
	[Route("nlp-controller")]
	public class ApiController : ControllerBase
	{
		private readonly ILogger<ApiController> _logger;
		private readonly INlpBackgroundTaskQueue _taskQueue;

		// Settings
		private readonly AuthenticationServiceSettings _authenticationServiceSettings;
		private readonly TextServiceSettings _textServiceSettings;
		private readonly NlpServiceSettings _nlpServiceSettings;
		private readonly TransformerServiceSettings _transformerServiceSettings;
		private readonly KnowledgeServiceSettings _knowledgeServiceSettings;
		private readonly AuthenticationServiceSecrets _authenticationServiceSecrets;

		// Services
		private readonly ITextServiceApi _textServiceApi;
		private readonly INlpServiceApi _nlpServiceApi;

		// Helpers
		private readonly ITextServiceHelper _textServiceHelper;
		private readonly INlpServiceHelper _nlpServiceHelper;

		// App memory
		private static string TopBraidEdgOAuthAccessToken { get; set; }
		private static Dictionary<int, NlpResource> NlpResourceDictionary { get; set; } = new Dictionary<int, NlpResource>();
		private static List<JsonElement> IdentifiedInformationInChapterTextDataList { get; set; } = new List<JsonElement>();
		private static List<string> TransformedRdfKnowledgeList { get; set; } = new List<string>();

		public ApiController(
			ILogger<ApiController> logger,
			INlpBackgroundTaskQueue taskQueue,

			ITextServiceApi textServiceApi,
			INlpServiceApi nlpServiceApi,

			ITextServiceHelper textServiceHelper,
			INlpServiceHelper nlpServiceHelper,

			AuthenticationServiceSettings authenticationServiceSettings,
			TextServiceSettings textServiceSettings,
			NlpServiceSettings nlpServiceSettings,
			TransformerServiceSettings transformerServiceSettings,
			KnowledgeServiceSettings knowledgeServiceSettings,
			AuthenticationServiceSecrets authenticationServiceSecrets
			)
		{
			_logger = logger;
			_taskQueue = taskQueue;

			_textServiceApi = textServiceApi;
			_nlpServiceApi = nlpServiceApi;

			_textServiceHelper = textServiceHelper;
			_nlpServiceHelper = nlpServiceHelper;

			_authenticationServiceSettings = authenticationServiceSettings;
			_textServiceSettings = textServiceSettings;
			_nlpServiceSettings = nlpServiceSettings;
			_transformerServiceSettings = transformerServiceSettings;
			_knowledgeServiceSettings = knowledgeServiceSettings;
			_authenticationServiceSecrets = authenticationServiceSecrets;
		}

		[HttpGet]
		public IActionResult Get()
		{
			return Ok("NLP controller app at your service. Ready to receive a POST request.");
		}

		[HttpPost]
		public async Task<IActionResult> Post([FromForm] Uri uri)
		{
			await _taskQueue.QueueBackgroundWorkItem(cancellationToken => NlpBackgroundTask(uri, cancellationToken));

			return Ok();
		}

		private async ValueTask NlpBackgroundTask(
			Uri requestedTextServiceRegulationIri,
			CancellationToken stoppingToken)
		{
			//if (token.IsCancellationRequested) return;

			_logger.LogInformation($"{Environment.NewLine}Queued Background Task (starting): {requestedTextServiceRegulationIri}{Environment.NewLine}");

			//
			// Text Service
			//

			var regulationResource = new RegulationResource
			{
				Url = requestedTextServiceRegulationIri
			};

			_logger.LogInformation($"{Environment.NewLine}Asking Text Service for regulation {regulationResource.RegulationYear}-{regulationResource.RegulationMonth}-{regulationResource.RegulationDay}-{regulationResource.RegulationNumber}.{Environment.NewLine}");

			var regulationFromTextService = await _textServiceApi.GetRegulation(
				_textServiceSettings.ApiBaseUrl,
				Convert.ToInt32(regulationResource.RegulationYear),
				Convert.ToInt32(regulationResource.RegulationMonth),
				Convert.ToInt32(regulationResource.RegulationDay),
				Convert.ToInt32(regulationResource.RegulationNumber));

			var chapterList = _textServiceHelper.SplitRegulationResponseIntoChapterList(regulationFromTextService);

			_logger.LogInformation($"{Environment.NewLine}{chapterList.Count} chapters loaded successfully.{Environment.NewLine}");

			//
			// NLP Service
			//

			// Load NLP options
			if (_nlpServiceSettings.RunAsTest)
			{ // Just load test data
				NlpResourceDictionary =
					_nlpServiceHelper.GetNlpResourceTestDictionary(
						_nlpServiceSettings.ApiBaseUrl);
			}
			else // Send request to NLP Service API
			{
				var nlpResourceListFromNlpService =
					await _nlpServiceApi.GetNlpResourceList(
						_nlpServiceSettings.ApiBaseUrl);

				NlpResourceDictionary =
					_nlpServiceHelper.MapNlpResources(
						nlpResourceListFromNlpService);
			}

			foreach (var nlpResourceDictionaryItem in NlpResourceDictionary)
			{
				var selectedNlpResourceDictionary = nlpResourceDictionaryItem.Value;

				_logger.LogInformation($"{Environment.NewLine}Asking NLP Service to identify information about {selectedNlpResourceDictionary.Title} ({selectedNlpResourceDictionary.Language}) in regulation {regulationResource.RegulationYear}-{regulationResource.RegulationMonth}-{regulationResource.RegulationDay}-{regulationResource.RegulationNumber}.{Environment.NewLine}");

				for (var i = 0; i < chapterList.Count; i++)
				{
					var requestNumber = i + 1;

					_logger.LogInformation($"{Environment.NewLine}Processing chapter {requestNumber} of {chapterList.Count}:{Environment.NewLine}");

					JsonElement identifiedInformationInChapterTextData;

					if (_nlpServiceSettings.RunAsTest)
					{
						identifiedInformationInChapterTextData =
							_nlpServiceHelper.GetTestDataForIdentifyInformationInChapterTextData();
					}
					else // Send request to NLP Service API
					{
						identifiedInformationInChapterTextData =
							await _nlpServiceApi.IdentifyInformationInChapterTextData(
								chapterList[i],
								selectedNlpResourceDictionary.Url);
					}

					var itemCountOfNlpServiceResponse =
						_nlpServiceHelper.CountItemsInNlpServiceApiResponse(
							identifiedInformationInChapterTextData);

					if (itemCountOfNlpServiceResponse > 0)
					{
						_logger.LogInformation($"{Environment.NewLine}{itemCountOfNlpServiceResponse} detections in this chapter.{Environment.NewLine}");

						IdentifiedInformationInChapterTextDataList.Add(identifiedInformationInChapterTextData);
					}
					else
					{
						_logger.LogInformation("{Environment.NewLine}No detections in this chapter.{Environment.NewLine}");
					}
				}
			}











			_logger.LogInformation($"{Environment.NewLine}Queued Background Task (completed): {requestedTextServiceRegulationIri}{Environment.NewLine}");
		}

		/*
		// This is nice to have when testing how a queue is working on a hosted background service.
		private async ValueTask TestWorkItem(Uri uri, CancellationToken token)
		{
			// Simulate three 5-second tasks to complete
			// for each enqueued work item

			var delayLoop = 0;
			var guid = Guid.NewGuid().ToString();

			_logger.LogInformation("Queued Background Task {Guid} is starting.", guid);

			while (!token.IsCancellationRequested && delayLoop < 3)
			{
				try
				{
					await Task.Delay(TimeSpan.FromSeconds(5), token);
				}
				catch (OperationCanceledException)
				{
					// Prevent throwing if the Delay is cancelled
				}

				delayLoop++;

				_logger.LogInformation(
					"Queued Background Task {Guid} is running. " + "{DelayLoop}/3", 
					guid, delayLoop);
			}

			_logger.LogInformation(
				delayLoop == 3
					? "Queued Background Task {Guid} is complete."
					: "Queued Background Task {Guid} was cancelled.", guid);
		}
		*/
	}
}
