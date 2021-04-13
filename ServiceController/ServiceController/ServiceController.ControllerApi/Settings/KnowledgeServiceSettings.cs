using System;

namespace ServiceController.ControllerApi.Settings
{
	public class KnowledgeServiceSettings
	{
		public Uri ApiBaseUrl { get; set; }
		public string TopBraidEdgOntologyId { get; set; }
		public string TopBraidEdgWorkflowId { get; set; }
		public string TopBraidEdgUserId { get; set; }
	}
}
