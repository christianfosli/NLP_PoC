using System.Text.Json;

namespace ServiceController.NlpService
{
	public interface INlpServiceHelper
	{
		public int CountItemsInNlpServiceApiResponse(JsonElement responseFromNlpService);
	}
}
