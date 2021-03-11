using Newtonsoft.Json.Linq;
using System.Text.Json;

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
	}
}
