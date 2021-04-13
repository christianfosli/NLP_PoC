using System.Threading;
using System.Threading.Tasks;

namespace ServiceController.ControllerApi.BackgroundServices
{
	public interface ITestingScopedProcessingService
	{
		Task DoWork(CancellationToken stoppingToken);
	}
}
