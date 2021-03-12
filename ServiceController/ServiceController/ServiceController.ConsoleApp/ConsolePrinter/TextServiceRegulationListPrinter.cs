using System;
using System.Collections.Generic;
using ServiceController.Entities.TextService;

namespace ServiceController.ConsoleApp.ConsolePrinter
{
	public class TextServiceRegulationListPrinter
	{
		private Dictionary<int, RegulationResource> RegulationResourceDictionary { get; }

		public TextServiceRegulationListPrinter(Dictionary<int, RegulationResource> regulationResourceDictionary)
		{
			RegulationResourceDictionary = regulationResourceDictionary;
		}

		public void PrintRegulationList()
		{
			Console.ForegroundColor = ConsoleColor.Black;
			Console.BackgroundColor = ConsoleColor.Blue;

			Console.WriteLine(string.Format("Text Service has {0} regulations:", RegulationResourceDictionary.Count));
			Console.BackgroundColor = ConsoleColor.Yellow;

			foreach (var dictionary in RegulationResourceDictionary)
			{
				Console.WriteLine(string.Format(
					"{0}. {1}-{2}-{3}-{4} ({5}): {6}",
					dictionary.Key,
					dictionary.Value.RegulationYear,
					dictionary.Value.RegulationMonth,
					dictionary.Value.RegulationDay,
					dictionary.Value.RegulationNumber,
					dictionary.Value.Language,
					dictionary.Value.Title));
			}

			Console.ResetColor();
		}
	}
}
