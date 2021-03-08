using System.Text.Json;
using System.Threading.Tasks;

namespace ServiceController.TextService
{
    public interface ITextServiceApi
    {
        Task<JsonElement?> GetRegulationChapterAsJson(
            int regulationYear,
            int regulationMonth,
            int regulationDay,
            int regulationNumber,
            int regulationChapterNumber);
    }
}
