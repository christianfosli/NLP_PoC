using System.Threading;
using System.Threading.Tasks;

namespace ServiceController.TextService
{
	public interface ITestingScopedProcessingService
	{
		Task<string> DoWork(CancellationToken stoppingToken);
	}
}
