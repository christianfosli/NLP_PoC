using System;
using System.Text.Json;
using System.Threading.Tasks;

namespace ServiceController.TextService
{
    public interface ITextServiceApi
    {
        Task<JsonElement> GetRegulationChapter(
	        Uri apiBaseUrl,
            int regulationYear,
            int regulationMonth,
            int regulationDay,
            int regulationNumber,
            int regulationChapterNumber);

        Task<JsonElement> GetRegulation(
	        Uri apiBaseUrl,
            int regulationYear,
            int regulationMonth,
            int regulationDay,
            int regulationNumber);

        Task<JsonElement> GetRegulationList(Uri apiBaseUrl);
    }
}
