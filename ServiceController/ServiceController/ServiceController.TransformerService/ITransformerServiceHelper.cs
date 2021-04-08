using System.Text.Json;

namespace ServiceController.TransformerService
{
	public interface ITransformerServiceHelper
	{
		public string GetTestDataForTransformNlpInformationToRdfKnowledge(JsonElement identifiedInformationInChapterTextData);
	}
}
