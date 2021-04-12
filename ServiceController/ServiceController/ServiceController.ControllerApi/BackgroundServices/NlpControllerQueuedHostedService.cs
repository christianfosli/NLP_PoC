﻿using System;
using System.Threading;
using System.Threading.Tasks;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;

namespace ServiceController.ControllerApi.BackgroundServices
{
	public class NlpControllerQueuedHostedService : BackgroundService
	{
		// TODO add api's here
		private readonly ILogger<NlpControllerQueuedHostedService> _logger;

		public NlpControllerQueuedHostedService(
			INlpControllerBackgroundTaskQueue taskQueue,
			ILogger<NlpControllerQueuedHostedService> logger)
		{
			TaskQueue = taskQueue;
			_logger = logger;
		}

		public INlpControllerBackgroundTaskQueue TaskQueue { get; }

		protected override async Task ExecuteAsync(CancellationToken stoppingToken)
		{
			_logger.LogInformation(
				$"Queued Hosted Service is running.{Environment.NewLine}" +
				$"{Environment.NewLine}Tap W to add a work item to the " +
				$"background queue.{Environment.NewLine}");

			await BackgroundProcessing(stoppingToken);
		}

		private async Task BackgroundProcessing(CancellationToken stoppingToken)
		{
			while (!stoppingToken.IsCancellationRequested)
			{
				var workItem =
					await TaskQueue.DequeueAsync(stoppingToken);

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
