namespace ServiceController.Entities.KnowledgeService
{
	public class TopBraidEdgSparqlInsertBuilder
	{
		// Example: nlppoctestontology
		public string OntologyName { get; internal set; }

		// Example: nlpknowledgefromappworkflow
		public string WorkflowName { get; internal set; }

		// Example: ontologist
		public string Username { get; internal set; }

		public TopBraidEdgSparqlInsertBuilder(
			string ontologyName,
			string workflowName,
			string username)
		{
			OntologyName = ontologyName;
			WorkflowName = workflowName;
			Username = username;
		}

		// Example: urn:x-evn-tag:nlppoctestontology:nlpknowledgefromappworkflow:ontologist
		public string Urn => $"urn:x-evn-tag:{OntologyName}:{WorkflowName}:{Username}";
	}
}
