using System.Collections.Generic;
using System.Text.Json;
using ServiceController.Entities.TextService;

namespace ServiceController.TextService
{
	public interface ITextServiceHelper
	{
		public List<JsonElement> SplitRegulationResponseIntoChapterList(JsonElement regulationFromTextService);
		public List<RegulationResource> MapRegulationResources(JsonElement regulationListFromTextService);
	}
}
