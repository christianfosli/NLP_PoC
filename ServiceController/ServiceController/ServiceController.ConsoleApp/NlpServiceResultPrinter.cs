using Newtonsoft.Json.Linq;
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

		public void PrintAllItems()
		{
			JObject jObject = JObject.Parse(NlpServiceResult.ToString());
			var firstObject = jObject.First; // Example: {"identified_build_dates": []}
			var childrenOfFirstObject = firstObject.First; // Example: {[]}
			JArray childrenOfFirstObjectInArray = (JArray)childrenOfFirstObject;
			foreach(var responseItem in childrenOfFirstObjectInArray)
			{
				Console.WriteLine(responseItem.ToString());
			}
		}
	}
}
