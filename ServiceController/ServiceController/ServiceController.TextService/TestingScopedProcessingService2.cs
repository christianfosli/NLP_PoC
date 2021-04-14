using System.Threading;
using System.Threading.Tasks;
using Microsoft.Extensions.Logging;

namespace ServiceController.TextService
{
	public class TestingScopedProcessingService2 : ITestingScopedProcessingService2
	{
		private int executionCount = 0;
		private readonly ILogger _logger;

		public TestingScopedProcessingService2(
			ILogger<TestingScopedProcessingService2> logger)
		{
			_logger = logger;
		}

		public async Task<string> DoWork(CancellationToken stoppingToken)
		{
			while (!stoppingToken.IsCancellationRequested && executionCount < 3)
			{
				executionCount++;

				_logger.LogInformation(
					"Scoped Processing Service is working. Count: {Count}", executionCount);

				await Task.Delay(10000, stoppingToken);
			}

			return "Test response from ITestingScopedProcessingService";
		}
	}
}
