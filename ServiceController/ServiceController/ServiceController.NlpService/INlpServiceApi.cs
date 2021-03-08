using System.Text.Json;
using System.Threading.Tasks;

namespace ServiceController.NlpService
{
    public interface INlpServiceApi
    {
        Task<JsonElement?> Identify_BUILD_DATE_In_NO_ChapterText(
            JsonElement chapterTextFromTextService);
    }
}
