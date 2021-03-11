using System.Collections.Generic;
using System.Text.Json;
using static System.Text.Json.JsonElement;
using ServiceController.Entities.TextService;

namespace ServiceController.TextService
{
	public class TextServiceHelper : ITextServiceHelper
	{
		public List<JsonElement> SplitRegulationResponseIntoChapterList(JsonElement regulationFromTextService)
		{
            var chapterList = new List<JsonElement>();

            using (JsonDocument document = JsonDocument.Parse(regulationFromTextService.ToString()))
            {
                JsonElement current = document.RootElement;
                var chapters = current.GetProperty("chapters");
                ArrayEnumerator arrayEnumerator = chapters.EnumerateArray();

                while (arrayEnumerator.MoveNext())
                {
                    JsonElement clonedChapter = arrayEnumerator.Current.Clone();
                    chapterList.Add(clonedChapter);
                }
            }

            return chapterList;
		}

        public List<RegulationResource> MapRegulationResources(JsonElement regulationListFromTextService)
		{
            var list = new List<RegulationResource>();

            using (JsonDocument document = JsonDocument.Parse(regulationListFromTextService.ToString()))
            {
                ArrayEnumerator arrayEnumerator = document.RootElement.EnumerateArray();

                while (arrayEnumerator.MoveNext())
                {
                    JsonElement i = arrayEnumerator.Current;
                    i.TryGetProperty("referenceId", out JsonElement referenceId);
                    i.TryGetProperty("title", out JsonElement title);
                    i.TryGetProperty("url", out JsonElement url);
                    i.TryGetProperty("language", out JsonElement language);

                    var item = new RegulationResource() {
                        ReferenceId = referenceId.GetString(),
                        Title = title.GetString(),
                        Url = new System.Uri(url.GetString()),
                        Language = language.GetString()
                    };

                    list.Add(item);
                }
            }

            return list;
		}
    }
}
