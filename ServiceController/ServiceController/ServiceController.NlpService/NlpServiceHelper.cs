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
            JObject jObject = JObject.Parse(responseFromNlpService.ToString());
            var firstObject = jObject.First; // Example: {"identified_build_dates": []}
            var childrenOfFirstObject = firstObject.First; // Example: {[]}
            JArray childrenOfFirstObjectInArray = (JArray)childrenOfFirstObject;
            return childrenOfFirstObjectInArray.Count;
		}

        public Dictionary<int, NlpResource> AddNlpResourceListToDictionary(List<NlpResource> nlpResourceList)
        {
            var dictionary = new Dictionary<int, NlpResource>();

            int counter = 1;

            foreach(var nlpResource in nlpResourceList)
			{
                dictionary.Add(counter, nlpResource);
                counter++;
            }

            return dictionary;
        }
    }
}
