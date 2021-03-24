using System.Collections.Generic;
using Newtonsoft.Json.Linq;
using System.Text.Json;
using static System.Text.Json.JsonElement;
using ServiceController.Entities.NlpService;

namespace ServiceController.NlpService
{
	public class NlpServiceHelper : INlpServiceHelper
	{
        public int CountItemsInNlpServiceApiResponse(JsonElement responseFromNlpService)
		{
            JObject jObject = JObject.Parse(responseFromNlpService.ToString());
            var firstObject = jObject.First; // Example: {"identified_build_dates": []}
            var childrenOfFirstObject = firstObject.First; // Example: {[]}
            JArray childrenOfFirstObjectInArray = (JArray)childrenOfFirstObject;
            return childrenOfFirstObjectInArray.Count;
		}

        public Dictionary<int, NlpResource> MapNlpResources(JsonElement nlpResourceListFromNlpService)
        {
	        var dictionary = new Dictionary<int, NlpResource>();
	        using JsonDocument document = JsonDocument.Parse(nlpResourceListFromNlpService.ToString());
	        ArrayEnumerator arrayEnumerator = document.RootElement.EnumerateArray();
	        var counter = 1;

	        while (arrayEnumerator.MoveNext())
	        {
		        JsonElement i = arrayEnumerator.Current;
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
