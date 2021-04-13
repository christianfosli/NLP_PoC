using System.Threading;
using System.Threading.Tasks;
using Microsoft.Extensions.Logging;

namespace ServiceController.ControllerApi.BackgroundServices
{
	internal class TestingScopedProcessingService : ITestingScopedProcessingService
	{
		private int executionCount = 0;
		private readonly ILogger _logger;

		public TestingScopedProcessingService(ILogger<TestingScopedProcessingService> logger)
		{
			_logger = logger;
		}

		public async Task DoWork(CancellationToken stoppingToken)
		{
			while (!stoppingToken.IsCancellationRequested)
			{
				executionCount++;

				_logger.LogInformation(
					"Scoped Processing Service is working. Count: {Count}", executionCount);

				await Task.Delay(10000, stoppingToken);
			}
		}
	}
}
