using System.Collections.Generic;
using System.Text.Json;
using static System.Text.Json.JsonElement;

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
	}
}
