using System;
using System.Threading.Tasks;

namespace ServiceController.KnowledgeService
{
	public interface ITopBraidEdgApi
	{
		Task TestInsert(
			Uri apiBaseUrl,
			string topBraidEdgOAuthAccessToken,
			string topBraidEdgSparqlInsertQuery,
			string topBraidEdgWorkflowUrn);
	}
}
