using System;
using System.Threading;
using System.Threading.Tasks;

namespace ServiceController.ControllerApi.BackgroundServices
{
	public interface INlpBackgroundTaskQueue
	{
		ValueTask QueueBackgroundWorkItem(Func<CancellationToken, ValueTask> workItem);

		ValueTask<Func<CancellationToken, ValueTask>> DequeueBackgroundWorkItem(CancellationToken cancellationToken);
	}
}
