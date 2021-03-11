using System;

namespace ServiceController.Entities.TextService
{
	public class RegulationResource
	{
		public string ReferenceId { get; set; }
		public string Title { get; set; }
		public Uri Url { get; set; }
		public string Language { get; set; }
	}
}
