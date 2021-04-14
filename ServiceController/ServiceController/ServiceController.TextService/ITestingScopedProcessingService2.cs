using System.Threading;
using System.Threading.Tasks;

namespace ServiceController.TextService
{
	public interface ITestingScopedProcessingService2
	{
		Task<string> DoWork(CancellationToken stoppingToken);
	}
}
