using System;
using System.Text.Json;
using System.Threading.Tasks;

namespace ServiceController.NlpService
{
    public interface INlpServiceApi
    {
        //TODO remove
        Task<JsonElement> Identify_BUILD_DATE_In_NO_ChapterText(
            JsonElement chapterTextFromTextService);

        Task<JsonElement> IdentifyInformationInChapterTextData(
            JsonElement chapterTextFromTextService,
            Uri nlpServiceApiResourceUrl);

        Task<JsonElement> GetNlpResourceList();
    }
}
