using System;
using System.Text.Json;

namespace ServiceController.ConsoleApp
{
	public class NlpServiceResultPrinter
	{
		public JsonElement NlpServiceResult { get; internal set; }

		public NlpServiceResultPrinter(JsonElement nlpServiceResult)
		{
			NlpServiceResult = nlpServiceResult;
		}

		public void ExampleOnPrintingBuildDateResult()
		{
			if (NlpServiceResult.TryGetProperty(
				"identified_build_dates",
				out JsonElement jsonElement))
			{
				var jsonElementList = jsonElement.EnumerateArray();

				while (jsonElementList.MoveNext())
				{
					JsonElement currentJsonElement = jsonElementList.Current;
					Console.WriteLine(currentJsonElement.ToString());
				}
			}
		}
	}
}
