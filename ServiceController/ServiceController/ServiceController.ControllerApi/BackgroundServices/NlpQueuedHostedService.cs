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

		public NlpQueuedHostedService(
			INlpBackgroundTaskQueue taskQueue,
			ILogger<NlpQueuedHostedService> logger)
		{
			TaskQueue = taskQueue;
			_logger = logger;
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
				var workItem = await TaskQueue.DequeueBackgroundWorkItem(stoppingToken);

				try
				{
					await workItem(stoppingToken);
				}
				catch (Exception ex)
				{
					_logger.LogError(ex, "Error occurred executing {WorkItem}.", nameof(workItem));
				}
			}
		}

		public override async Task StopAsync(CancellationToken stoppingToken)
		{
			_logger.LogInformation("Queued Hosted Service is stopping.");
			await base.StopAsync(stoppingToken);
		}
	}
}
