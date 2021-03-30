using System;
using System.Net.Http;
using System.Text.Json;
using System.Threading.Tasks;
using Newtonsoft.Json;

namespace ServiceController.TextService
{
    public class TextServiceApi : ITextServiceApi
    {
        private readonly IHttpClientFactory _clientFactory;

        public TextServiceApi(IHttpClientFactory clientFactory)
        {
            _clientFactory = clientFactory;
        }

        /*
         * Example: https://sdir-d-apim-common.azure-api.net/core-text-internal/regulation/2013/11/22/1404/chapter/4
         */
        public async Task<JsonElement> GetRegulationChapter(
            Uri apiBaseUrl, // For example: https://sdir-d-apim-common.azure-api.net/core-text-internal
            int regulationYear,
            int regulationMonth,
            int regulationDay,
            int regulationNumber,
            int regulationChapterNumber)
        {
            var apiUrl = $@"{apiBaseUrl}/regulation/{regulationYear}/{regulationMonth}/{regulationDay}/{regulationNumber}/chapter/{regulationChapterNumber}";
            var request = new HttpRequestMessage(HttpMethod.Get, apiUrl);
            var client = _clientFactory.CreateClient();
            var response = await client.SendAsync(request, HttpCompletionOption.ResponseHeadersRead);
            if (!response.IsSuccessStatusCode) throw new System.Exception();
            var jsonResponse = await response.Content.ReadAsStringAsync();
            var jsonResponseDeserialized = // UTF-8 fix
	            JsonConvert.DeserializeObject(jsonResponse)?.ToString();
            using var doc = JsonDocument.Parse(jsonResponseDeserialized);
            return doc.RootElement.Clone();
        }

        /*
         * Example: https://sdir-d-apim-common.azure-api.net/core-text-internal/regulation/2013/11/22/1404
         */
        public async Task<JsonElement> GetRegulation(
	        Uri apiBaseUrl, // For example: https://sdir-d-apim-common.azure-api.net/core-text-internal
            int regulationYear,
            int regulationMonth,
            int regulationDay,
            int regulationNumber)
        {
            var apiUrl = $@"{apiBaseUrl}/regulation/{regulationYear}/{regulationMonth}/{regulationDay}/{regulationNumber}";
            var request = new HttpRequestMessage(HttpMethod.Get, apiUrl);
            var client = _clientFactory.CreateClient();
            var response = await client.SendAsync(request, HttpCompletionOption.ResponseHeadersRead);
            if (!response.IsSuccessStatusCode) throw new System.Exception();
            var jsonResponse = await response.Content.ReadAsStringAsync();
            var jsonResponseDeserialized = // UTF-8 fix
	            JsonConvert.DeserializeObject(jsonResponse)?.ToString();
            using var doc = JsonDocument.Parse(jsonResponseDeserialized);
            return doc.RootElement.Clone();
        }

        public async Task<JsonElement> GetRegulationList(Uri apiBaseUrl)
        {
            var request = new HttpRequestMessage(
                HttpMethod.Get,
                $@"{apiBaseUrl}/regulations");
            var client = _clientFactory.CreateClient();
            var response = await client.SendAsync(request, HttpCompletionOption.ResponseHeadersRead);
            if (!response.IsSuccessStatusCode) throw new System.Exception();
            var jsonResponse = await response.Content.ReadAsStringAsync();
            var jsonResponseDeserialized = // UTF-8 fix
	            JsonConvert.DeserializeObject(jsonResponse)?.ToString();
            using var doc = JsonDocument.Parse(jsonResponseDeserialized);
            return doc.RootElement.Clone();
        }
    }
}
