using System.Collections.Generic;
using System.Text.Json;

namespace ServiceController.TextService
{
	public interface ITextServiceHelper
	{
		public List<JsonElement> SplitRegulationResponseIntoChapterList(JsonElement regulationFromTextService);
	}
}
