using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using System;
using System.Threading;
using System.Threading.Tasks;
using ServiceController.ControllerApi.BackgroundServices;
using ServiceController.ControllerApi.Settings;
using ServiceController.Entities.TextService;

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
		//private ITextServiceApi _textServiceApi;

		public ApiController(
			ILogger<ApiController> logger,
			INlpBackgroundTaskQueue taskQueue,
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

			/*
			var regulationFromTextService = await _textServiceApi.GetRegulation(
				_textServiceSettings.ApiBaseUrl,
				Convert.ToInt32(regulationResource.RegulationYear),
				Convert.ToInt32(regulationResource.RegulationMonth),
				Convert.ToInt32(regulationResource.RegulationDay),
				Convert.ToInt32(regulationResource.RegulationNumber));*/

			//var chapterList = textServiceHelper.SplitRegulationResponseIntoChapterList(regulationFromTextService);

			//Console.WriteLine($"{chapterList.Count} chapters loaded successfully.");


			// TODO

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
