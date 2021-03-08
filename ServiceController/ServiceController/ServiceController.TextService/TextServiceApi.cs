using System.Net.Http;
using System.Text.Json;
using System.Threading.Tasks;

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
            //request.Headers.TryAddWithoutValidation("some-header", "some-value");

            var client = _clientFactory.CreateClient();
            var response = await client.SendAsync(request, HttpCompletionOption.ResponseHeadersRead);

            if (response.IsSuccessStatusCode)
            {
                var json = await response.Content.ReadAsStringAsync();
				using JsonDocument doc = JsonDocument.Parse(json);
				return doc.RootElement.Clone();
			}
            else
            {
                return null;
            }
        }
    }
}
