using System;

namespace ServiceController.Entities.TextService
{
	public class RegulationResource
	{
		public string ReferenceId { get; set; }
		public string Title { get; set; }
		public Uri Url { get; set; }
		public string Language { get; set; }

		//
		// Helpers
		//

		public string RegulationNumber
		{
			get
			{
				var segmentLength = Url.Segments.Length;
				var indexNumberOfTargetedSegment = segmentLength - 1;
				var selectedSegment = Url.Segments[indexNumberOfTargetedSegment];
				return selectedSegment.Replace("/", string.Empty);
			}
		}

		public string RegulationDay
		{
			get
			{
				var segmentLength = Url.Segments.Length;
				var indexNumberOfTargetedSegment = segmentLength - 2;
				var selectedSegment = Url.Segments[indexNumberOfTargetedSegment];
				return selectedSegment.Replace("/", string.Empty);
			}
		}

		public string RegulationMonth
		{
			get
			{
				var segmentLength = Url.Segments.Length;
				var indexNumberOfTargetedSegment = segmentLength - 3;
				var selectedSegment = Url.Segments[indexNumberOfTargetedSegment];
				return selectedSegment.Replace("/", string.Empty);
			}
		}

		public string RegulationYear
		{
			get
			{
				var segmentLength = Url.Segments.Length;
				var indexNumberOfTargetedSegment = segmentLength - 4;
				var selectedSegment = Url.Segments[indexNumberOfTargetedSegment];
				return selectedSegment.Replace("/", string.Empty);
			}
		}
	}
}
