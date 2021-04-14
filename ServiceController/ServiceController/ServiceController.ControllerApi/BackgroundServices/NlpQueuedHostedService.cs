using System;
using System.Threading;
using System.Threading.Tasks;
using AngleSharp.Common;
using Microsoft.Extensions.DependencyInjection;
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
					// Same as "NlpBackgroundTask".
					// Did not manage to get this to return any value directly.
					await dequeuedBackgroundTask(stoppingToken);

					// Extracting IRI from dequeuedBackgroundTask
					var target = dequeuedBackgroundTask.Target;
					var objectWithIri = target?.GetType().GetField("uri")?.GetValue(target);
					if (objectWithIri == null) throw new Exception("Did not find input IRI.");
					var requestedTextServiceRegulationIri = new Uri(objectWithIri.ToString() ?? string.Empty);

					//
					// Services
					//

					var textServiceApiResponse = await DoooWork(requestedTextServiceRegulationIri, stoppingToken);
					

					var f = "";

					//TODO
				}
				catch (Exception ex)
				{
					_logger.LogError(ex, "Error occurred executing {WorkItem}.", nameof(dequeuedBackgroundTask));
				}
			}
		}

		private async Task<string> DoooWork(
			Uri requestedTextServiceRegulationIri,
			CancellationToken stoppingToken)
		{
			_logger.LogInformation("Consume Scoped Service Hosted Service is working.");
			using var scope = _serviceProvider.CreateScope();
			var scopedProcessingService = scope.ServiceProvider.GetRequiredService<ITestingScopedProcessingService>();

			await scopedProcessingService.DoWork(stoppingToken);

			return "test return string";
		}

		public override async Task StopAsync(CancellationToken stoppingToken)
		{
			_logger.LogInformation("Queued Hosted Service is stopping.");
			await base.StopAsync(stoppingToken);
		}
	}
}
