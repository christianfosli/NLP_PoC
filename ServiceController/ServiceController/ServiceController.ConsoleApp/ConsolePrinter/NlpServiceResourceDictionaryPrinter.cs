using System;
using System.Collections.Generic;
using ServiceController.Entities.NlpService;

namespace ServiceController.ConsoleApp.ConsolePrinter
{
	public class NlpServiceResourceDictionaryPrinter
	{
		private Dictionary<int, NlpResource> NlpResourceDictionary { get; }

		public NlpServiceResourceDictionaryPrinter(Dictionary<int, NlpResource> nlpResourceDictionary)
		{
			NlpResourceDictionary = nlpResourceDictionary;
		}

		public void PrintAllOptions()
		{
			Console.BackgroundColor = ConsoleColor.Green;
			Console.ForegroundColor = ConsoleColor.Black;

			Console.WriteLine(string.Format("NLP Service has {0} rule-based patterns:", NlpResourceDictionary.Count));
			Console.BackgroundColor = ConsoleColor.Yellow;

			foreach (var dictionary in NlpResourceDictionary)
			{
				Console.WriteLine(string.Format(
					"{0}. {1} ({2})",
					dictionary.Key,
					dictionary.Value.Title,
					dictionary.Value.Language));
			}

			Console.ResetColor();
		}
	}
}
