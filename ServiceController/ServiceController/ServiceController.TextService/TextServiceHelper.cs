using System.Collections.Generic;
using System.Text.Json;
using ServiceController.Entities.TextService;

namespace ServiceController.TextService
{
	public class TextServiceHelper : ITextServiceHelper
	{
		public List<JsonElement> SplitRegulationResponseIntoChapterList(JsonElement regulationFromTextService)
		{
            var chapterList = new List<JsonElement>();

            using var document = JsonDocument.Parse(regulationFromTextService.ToString());
            var current = document.RootElement;
            var chapters = current.GetProperty("chapters");
            var arrayEnumerator = chapters.EnumerateArray();

            while (arrayEnumerator.MoveNext())
            {
	            var clonedChapter = arrayEnumerator.Current.Clone();
	            chapterList.Add(clonedChapter);
            }

            return chapterList;
		}

        public Dictionary<int,RegulationResource> MapRegulationResources(JsonElement regulationListFromTextService)
		{
            var dictionary = new Dictionary<int, RegulationResource>();

            using var document = JsonDocument.Parse(regulationListFromTextService.ToString());
            var arrayEnumerator = document.RootElement.EnumerateArray();

            var counter = 1;

            while (arrayEnumerator.MoveNext())
            {
	            var i = arrayEnumerator.Current;
	            i.TryGetProperty("referenceId", out var referenceId);
	            i.TryGetProperty("title", out var title);
	            i.TryGetProperty("url", out var url);
	            i.TryGetProperty("language", out var language);

	            var item = new RegulationResource() {
		            ReferenceId = referenceId.GetString(),
		            Title = title.GetString(),
		            Url = new System.Uri(url.GetString()),
		            Language = language.GetString()
	            };

	            dictionary.Add(counter,item);
	            counter++;
            }

            return dictionary;
		}
    }
}
