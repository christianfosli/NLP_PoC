using System.Collections.Generic;
using Newtonsoft.Json.Linq;
using System.Text.Json;
using ServiceController.Entities.NlpService;

namespace ServiceController.NlpService
{
	public class NlpServiceHelper : INlpServiceHelper
	{
        public int CountItemsInNlpServiceApiResponse(JsonElement responseFromNlpService)
		{
            var jObject = JObject.Parse(responseFromNlpService.ToString());
            var firstObject = jObject.First; // Example: {"identified_build_dates": []}
            if (firstObject == null) return 0;
            var childrenOfFirstObject = firstObject.First; // Example: {[]}
            var childrenOfFirstObjectInArray = (JArray)childrenOfFirstObject;
            return childrenOfFirstObjectInArray?.Count ?? 0;
		}

        public Dictionary<int, NlpResource> MapNlpResources(JsonElement nlpResourceListFromNlpService)
        {
	        var dictionary = new Dictionary<int, NlpResource>();
	        using var document = JsonDocument.Parse(nlpResourceListFromNlpService.ToString());
	        var arrayEnumerator = document.RootElement.EnumerateArray();
	        var counter = 1;

	        while (arrayEnumerator.MoveNext())
	        {
		        var i = arrayEnumerator.Current;
		        i.TryGetProperty("title", out var title);
		        i.TryGetProperty("language", out var language);
		        i.TryGetProperty("url", out var url);

		        var item = new NlpResource
		        {
			        Title = title.GetString(),
			        Language = language.GetString(),
			        Url = new System.Uri(url.GetString())
		        };

		        dictionary.Add(counter, item);
		        counter++;
	        }

	        return dictionary;
        }
    }
}
