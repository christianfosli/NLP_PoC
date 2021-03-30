using System;
using System.Text.Json;
using System.Threading.Tasks;

namespace ServiceController.NlpService
{
    public interface INlpServiceApi
    {
	    Task<JsonElement> IdentifyInformationInChapterTextData(
            JsonElement chapterTextFromTextService,
            Uri nlpServiceApiResourceUrl);

	    Task<JsonElement> GetNlpResourceList(Uri apiBaseUrl);
    }
}
