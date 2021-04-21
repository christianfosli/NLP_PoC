using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.Json;
using System.Threading;
using System.Threading.Tasks;
using ServiceController.AuthenticationService;
using ServiceController.ControllerApi.BackgroundServices;
using ServiceController.ControllerApi.Settings;
using ServiceController.Entities.NlpService;
using ServiceController.Entities.TextService;
using ServiceController.KnowledgeService;
using ServiceController.NlpService;
using ServiceController.TextService;
using ServiceController.TransformerService;
using Microsoft.AspNetCore.Authorization;

namespace ServiceController.ControllerApi.Controllers
{
	[ApiController]
	[Authorize]
	[Route("nlp-controller")]
	public class ApiController : ControllerBase
	{
		private readonly ILogger<ApiController> _logger;
		private readonly INlpBackgroundTaskQueue _taskQueue;

		// Services
		private readonly ITextServiceApi _textServiceApi;
		private readonly INlpServiceApi _nlpServiceApi;
		private readonly ITransformerServiceApi _transformerServiceApi;
		private readonly ITopBraidEdgApi _topBraidEdgApi;
		private readonly IAuthenticationApi _authenticationApi;

		// Helpers
		private readonly ITextServiceHelper _textServiceHelper;
		private readonly INlpServiceHelper _nlpServiceHelper;
		private readonly ITransformerServiceHelper _transformerServiceHelper;

		// Settings
		private readonly AuthenticationServiceSettings _authenticationServiceSettings;
		private readonly TextServiceSettings _textServiceSettings;
		private readonly NlpServiceSettings _nlpServiceSettings;
		private readonly TransformerServiceSettings _transformerServiceSettings;
		private readonly KnowledgeServiceSettings _knowledgeServiceSettings;

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
			ITransformerServiceApi transformerServiceApi,
			ITopBraidEdgApi topBraidEdgApi,
			IAuthenticationApi authenticationApi,
			ITextServiceHelper textServiceHelper,
			INlpServiceHelper nlpServiceHelper,
			ITransformerServiceHelper transformerServiceHelper,
			AuthenticationServiceSettings authenticationServiceSettings,
			TextServiceSettings textServiceSettings,
			NlpServiceSettings nlpServiceSettings,
			TransformerServiceSettings transformerServiceSettings,
			KnowledgeServiceSettings knowledgeServiceSettings
		)
		{
			_logger = logger;
			_taskQueue = taskQueue;
			_textServiceApi = textServiceApi;
			_nlpServiceApi = nlpServiceApi;
			_transformerServiceApi = transformerServiceApi;
			_topBraidEdgApi = topBraidEdgApi;
			_authenticationApi = authenticationApi;
			_textServiceHelper = textServiceHelper;
			_nlpServiceHelper = nlpServiceHelper;
			_transformerServiceHelper = transformerServiceHelper;
			_authenticationServiceSettings = authenticationServiceSettings;
			_textServiceSettings = textServiceSettings;
			_nlpServiceSettings = nlpServiceSettings;
			_transformerServiceSettings = transformerServiceSettings;
			_knowledgeServiceSettings = knowledgeServiceSettings;
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
			if (stoppingToken.IsCancellationRequested)
			{
				_logger.LogInformation($"{Environment.NewLine}Stopped because of CancellationToken.{Environment.NewLine}");
				return;
			}

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
						_logger.LogInformation($"{Environment.NewLine}No detections in this chapter.{Environment.NewLine}");
					}
				}
			}

			//
			// Transformer Service
			//

			if (_transformerServiceSettings.RunAsTest)
			{
				_logger.LogInformation($"{Environment.NewLine}RUN AS TEST -> Asking Transformer Service to transform information into knowledge.{Environment.NewLine}");

				var transformerCounter = 0;
				foreach (var identifiedInformationInChapterTextData in IdentifiedInformationInChapterTextDataList)
				{
					var transformedRdfKnowledge =
						_transformerServiceHelper.GetTestDataForTransformNlpInformationToRdfKnowledge(
							identifiedInformationInChapterTextData);

					TransformedRdfKnowledgeList.Add(transformedRdfKnowledge);

					transformerCounter++;
					_logger.LogInformation($"{Environment.NewLine}Transformation {transformerCounter}. Success!{Environment.NewLine}");
				}
			}
			else // send request to Transformer Service API
			{
				_logger.LogInformation($"{Environment.NewLine}Asking Transformer Service to transform information into knowledge.{Environment.NewLine}");

				var transformerCounter = 0;
				foreach (var identifiedInformationInChapterTextData in IdentifiedInformationInChapterTextDataList)
				{
					var transformedRdfKnowledge =
						await _transformerServiceApi.TransformNlpInformationToRdfKnowledge(
							_transformerServiceSettings.ApiBaseUrl,
							identifiedInformationInChapterTextData);

					TransformedRdfKnowledgeList.Add(transformedRdfKnowledge);

					transformerCounter++;
					_logger.LogInformation($"{Environment.NewLine}Transformation {transformerCounter}. Success!{Environment.NewLine}");
				}
			}

			//
			// Authentication Service
			//

			try
			{
				_logger.LogInformation($"{Environment.NewLine}Asking Authentication Service for access token.{Environment.NewLine}");

				TopBraidEdgOAuthAccessToken = await _authenticationApi.GetAuthenticationToken(
					_authenticationServiceSettings.ApiBaseUrl,
					_authenticationServiceSettings.ClientId,
					_authenticationServiceSettings.ClientSecret,
					_authenticationServiceSettings.Scope);

				_logger.LogInformation($"{Environment.NewLine}Successfully loaded access token.{Environment.NewLine}");
			}
			catch (Exception e)
			{
				_logger.LogInformation($"{Environment.NewLine}Oh no.. :-O Something went wrong. Here is an error message:{Environment.NewLine}");
				_logger.LogInformation(e.ToString());
				_logger.LogInformation($"{Environment.NewLine}Service Controller application ended.{Environment.NewLine}");
				return;
			}

			//
			// Knowledge Service
			//

			foreach (var transformedRdfKnowledge in TransformedRdfKnowledgeList)
			{
				_logger.LogInformation($"{Environment.NewLine}Asking Knowledge Service to construct SPARQL INSERT query.{Environment.NewLine}");

				var topBraidEdgSparqlInsertBuilder = new Entities.KnowledgeService.TopBraidEdgSparqlInsertBuilder(
					_knowledgeServiceSettings.TopBraidEdgOntologyId,
					_knowledgeServiceSettings.TopBraidEdgWorkflowId,
					_knowledgeServiceSettings.TopBraidEdgUserId,
					transformedRdfKnowledge
				);

				_logger.LogInformation($"{Environment.NewLine}Successfully parsed {topBraidEdgSparqlInsertBuilder.Graph.Nodes.Count()} triples from Transformer Service.{Environment.NewLine}");
				var sparqlInsertQueryString = topBraidEdgSparqlInsertBuilder.BuildSparqlInsertQueryString();
				_logger.LogInformation($"{Environment.NewLine}Successfully constructed SPARQL INSERT query.{Environment.NewLine}");

				var topBraidEdgGraphUrn = topBraidEdgSparqlInsertBuilder.BuildTopBraidEdgGraphUrn();
				_logger.LogInformation($"{Environment.NewLine}Loading knowledge into TopBraid EDG graph: {topBraidEdgGraphUrn}{Environment.NewLine}");

				try
				{
					await _topBraidEdgApi.TestInsert(
						_knowledgeServiceSettings.ApiBaseUrl,
						TopBraidEdgOAuthAccessToken,
						sparqlInsertQueryString,
						topBraidEdgGraphUrn);
				}
				catch (Exception e)
				{
					_logger.LogInformation($"{Environment.NewLine}Oh no.. :-O Something went wrong. Here is an error message:{Environment.NewLine}");
					_logger.LogInformation(e.ToString());
					_logger.LogInformation($"{Environment.NewLine}Service Controller application ended.{Environment.NewLine}");
					return;
				}

				_logger.LogInformation($"{Environment.NewLine}Successfully loaded knowledge.{Environment.NewLine}");
			}

			_logger.LogInformation($"{Environment.NewLine}Queued Background Task (completed): {requestedTextServiceRegulationIri}{Environment.NewLine}");
		}
	}
}
