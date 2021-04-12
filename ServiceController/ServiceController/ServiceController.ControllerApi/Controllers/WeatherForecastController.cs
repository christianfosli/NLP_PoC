using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using System;
using System.Threading;
using System.Threading.Tasks;
using ServiceController.ControllerApi.BackgroundServices;

namespace ServiceController.ControllerApi.Controllers
{
	[ApiController]
	[Route("[controller]")]
	public class WeatherForecastController : ControllerBase
	{
		private readonly ILogger<WeatherForecastController> _logger;
		private readonly INlpControllerBackgroundTaskQueue _taskQueue;

		public WeatherForecastController(
			ILogger<WeatherForecastController> logger,
			INlpControllerBackgroundTaskQueue taskQueue)
		{
			_logger = logger;
			_taskQueue = taskQueue;
		}

		[HttpPost]
		public async Task<IActionResult> Post([FromForm] Uri uri)
		{
			// Enqueue a background work item
			await _taskQueue.QueueBackgroundWorkItemAsync(BuildWorkItem);
			return Ok();
		}

		private async ValueTask BuildWorkItem(CancellationToken token)
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
    }
}
