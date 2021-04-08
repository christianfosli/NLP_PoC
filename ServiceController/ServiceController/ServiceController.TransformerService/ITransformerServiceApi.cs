using System;
using System.Text.Json;
using System.Threading.Tasks;

namespace ServiceController.TransformerService
{
	public interface ITransformerServiceApi
	{
		Task<string> TransformNlpInformationToRdfKnowledge(
			Uri apiBaseUrl,
			JsonElement identifiedInformationInChapterTextData);

		Task<string> ReturnTestDataAsTransformNlpInformationToRdfKnowledge(
			Uri apiBaseUrl,
			JsonElement identifiedInformationInChapterTextData);
	}
}
