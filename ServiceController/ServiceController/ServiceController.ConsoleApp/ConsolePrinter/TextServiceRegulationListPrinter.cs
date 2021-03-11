using System;
using System.Collections.Generic;
using ServiceController.Entities.TextService;

namespace ServiceController.ConsoleApp.ConsolePrinter
{
	public class TextServiceRegulationListPrinter
	{
		private List<RegulationResource> RegulationResourceList { get; }

		public TextServiceRegulationListPrinter(List<RegulationResource> regulationResourceList)
		{
			RegulationResourceList = regulationResourceList;
		}

		public void PrintReferenceId()
		{
			Console.ForegroundColor = ConsoleColor.Black;
			Console.BackgroundColor = ConsoleColor.Blue;

			Console.WriteLine(string.Format("Text Service has {0} regulations:", RegulationResourceList.Count));
			Console.BackgroundColor = ConsoleColor.Yellow;

			foreach (var i in RegulationResourceList)
			{
				Console.WriteLine(i.ReferenceId);
			}

			Console.ResetColor();
		}
	}
}
