using System.Threading.Tasks;

namespace ServiceController.KnowledgeService
{
	public interface ITopBraidEdgApi
	{
		Task TestInsert(
			string topBraidEdgOAuthAccessToken,
			string topBraidEdgSparqlInsertQuery,
			string topBraidEdgWorkflowUrn);
	}
}
