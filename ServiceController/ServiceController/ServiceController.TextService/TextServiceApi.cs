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
         * https://sdir-d-apim-common.azure-api.net/core-text-internal/regulation/2013/11/22/1404/chapter/4
         */
        public async Task<JsonElement?> GetRegulationChapterAsJson(
            int regulationYear,
            int regulationMonth,
            int regulationDay,
            int regulationNumber,
            int regulationChapterNumber)
        {
            string apiUrl = string.Format(
                @"https://sdir-d-apim-common.azure-api.net/core-text-internal/regulation/{0}/{1}/{2}/{3}/chapter/{4}",
                regulationYear,
                regulationMonth,
                regulationDay,
                regulationNumber,
                regulationChapterNumber);

            var request = new HttpRequestMessage(HttpMethod.Get, apiUrl);
            var client = _clientFactory.CreateClient();
            var response = await client.SendAsync(request, HttpCompletionOption.ResponseHeadersRead);

            if (response.IsSuccessStatusCode)
            {
                var jsonResponse = await response.Content.ReadAsStringAsync();
                var jsonResponseDeserialized = // UTF-8 fix
                    JsonConvert.DeserializeObject(jsonResponse).ToString();
                using JsonDocument doc = JsonDocument.Parse(jsonResponseDeserialized);
                return doc.RootElement.Clone();
            }
            else
            {
                return null;
            }
        }
    }
}
