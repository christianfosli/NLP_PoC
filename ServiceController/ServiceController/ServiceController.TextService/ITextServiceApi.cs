using System.Text.Json;
using System.Threading.Tasks;

namespace ServiceController.TextService
{
    public interface ITextServiceApi
    {
        Task<JsonElement> GetRegulationChapter(
            int regulationYear,
            int regulationMonth,
            int regulationDay,
            int regulationNumber,
            int regulationChapterNumber);

        Task<JsonElement> GetRegulation(
            int regulationYear,
            int regulationMonth,
            int regulationDay,
            int regulationNumber);
    }
}
