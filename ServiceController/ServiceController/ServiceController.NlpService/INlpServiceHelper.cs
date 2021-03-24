using ServiceController.Entities.NlpService;
using System.Collections.Generic;
using System.Text.Json;

namespace ServiceController.NlpService
{
	public interface INlpServiceHelper
	{
		public int CountItemsInNlpServiceApiResponse(JsonElement responseFromNlpService);
		public Dictionary<int, NlpResource> MapNlpResources(JsonElement nlpResourceListFromNlpService);
	}
}
