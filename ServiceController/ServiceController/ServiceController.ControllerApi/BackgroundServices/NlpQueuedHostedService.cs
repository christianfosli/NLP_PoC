using System;
using System.Threading;
using System.Threading.Tasks;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;

namespace ServiceController.ControllerApi.BackgroundServices
{
	public class NlpQueuedHostedService : BackgroundService
	{
		private readonly ILogger<NlpQueuedHostedService> _logger;
		public INlpBackgroundTaskQueue TaskQueue { get; }
		private readonly IServiceProvider _serviceProvider;

		public NlpQueuedHostedService(
			INlpBackgroundTaskQueue taskQueue,
			ILogger<NlpQueuedHostedService> logger,
			IServiceProvider serviceProvider
			)
		{
			TaskQueue = taskQueue;
			_logger = logger;
			_serviceProvider = serviceProvider;
		}

		protected override async Task ExecuteAsync(CancellationToken stoppingToken)
		{
			_logger.LogInformation(
				$"{Environment.NewLine}Queued Hosted Service is running.{Environment.NewLine}" +
				$"{Environment.NewLine}Send request to API to add a work item to the background queue.{Environment.NewLine}");

			await BackgroundProcessing(stoppingToken);
		}

		private async Task BackgroundProcessing(CancellationToken stoppingToken)
		{
			while (!stoppingToken.IsCancellationRequested)
			{
				var dequeuedBackgroundTask = await TaskQueue.DequeueBackgroundWorkItem(stoppingToken);

				try
				{
					await dequeuedBackgroundTask(stoppingToken); // Same as "NlpBackgroundTask"

					/*
					 TODO remove all this
					// Extracting IRI from dequeuedBackgroundTask
					var target = dequeuedBackgroundTask.Target;
					var objectWithIri = target?.GetType().GetField("uri")?.GetValue(target);
					if (objectWithIri == null) throw new Exception("Did not find input IRI.");
					var requestedTextServiceRegulationIri = new Uri(objectWithIri.ToString() ?? string.Empty);

					//
					// Services
					//

					//var testServiceResponse = await TestScopeWrapper(requestedTextServiceRegulationIri, stoppingToken); //TODO remove
					//var testServiceResponse2 = await TestScopeWrapper2(requestedTextServiceRegulationIri, stoppingToken); //TODO remove

					//var textServiceResponse = await TextServiceScopeWrapper(requestedTextServiceRegulationIri, stoppingToken);
					*/
				}
				catch (Exception ex)
				{
					_logger.LogError(ex, "Error occurred executing {WorkItem}.", nameof(dequeuedBackgroundTask));
				}
			}
		}

		/* TODO remove

		//
		// Tasks - Scope wrappers
		//

		private async Task<string> TextServiceScopeWrapper(
			Uri requestedTextServiceRegulationIri,
			CancellationToken stoppingToken)
		{
			_logger.LogInformation("TextServiceScopeWrapper (starting)");

			using var scope = _serviceProvider.CreateScope();
			var textServiceApi = scope.ServiceProvider.GetRequiredService<ITextServiceApi>();

			var regulationResource = new RegulationResource
			{
				Url = requestedTextServiceRegulationIri
			};

			_logger.LogInformation($"{Environment.NewLine}Asking Text Service for regulation {regulationResource.RegulationYear}-{regulationResource.RegulationMonth}-{regulationResource.RegulationDay}-{regulationResource.RegulationNumber}.{Environment.NewLine}");

			var regulationFromTextService = await textServiceApi.GetRegulation(
				new Uri(@"https://sdir-d-apim-common.azure-api.net/core-text-internal"), // TODO TextServiceSettings.ApiBaseUrl,
				Convert.ToInt32(regulationResource.RegulationYear),
				Convert.ToInt32(regulationResource.RegulationMonth),
				Convert.ToInt32(regulationResource.RegulationDay),
				Convert.ToInt32(regulationResource.RegulationNumber));

			_logger.LogInformation("TextServiceScopeWrapper (ending)");

			return "test return string";
		}
		*/

		public override async Task StopAsync(CancellationToken stoppingToken)
		{
			_logger.LogInformation("Queued Hosted Service is stopping.");
			await base.StopAsync(stoppingToken);
		}

		/* TODO remove
		private async Task<string> TestScopeWrapper(
			Uri requestedTextServiceRegulationIri,
			CancellationToken stoppingToken)
		{
			_logger.LogInformation("Consume Scoped Service Hosted Service is working.");
			using var scope = _serviceProvider.CreateScope();
			var scopedProcessingService = scope.ServiceProvider.GetRequiredService<ITestingScopedProcessingService>();

			var test = await scopedProcessingService.DoWork(stoppingToken);

			return "test return string";
		}

		private async Task<string> TestScopeWrapper2(
			Uri requestedTextServiceRegulationIri,
			CancellationToken stoppingToken)
		{
			_logger.LogInformation("2 - Consume Scoped Service Hosted Service is working.");
			using var scope = _serviceProvider.CreateScope();
			var scopedProcessingService = scope.ServiceProvider.GetRequiredService<ITestingScopedProcessingService2>();

			var test = await scopedProcessingService.DoWork(stoppingToken);

			return "2 - test return string";
		}
		*/
	}
}
