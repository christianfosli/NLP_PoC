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
	public class ApiController : ControllerBase
	{
		private readonly ILogger<ApiController> _logger;
		private readonly INlpBackgroundTaskQueue _taskQueue;

		public ApiController(
			ILogger<ApiController> logger,
			INlpBackgroundTaskQueue taskQueue)
		{
			_logger = logger;
			_taskQueue = taskQueue;
		}

		[HttpPost]
		public async Task<IActionResult> Post([FromForm] Uri uri)
		{
			await _taskQueue.QueueBackgroundWorkItem(
				cancellationToken => BuildWorkItem(uri, cancellationToken));

			return Ok();
		}

		private async ValueTask BuildWorkItem(Uri uri, CancellationToken token)
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
