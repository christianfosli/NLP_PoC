using System.Net.Http;
using System.Text.Json;
using System.Threading.Tasks;

namespace ServiceController.NlpService
{
    public class NlpServiceApi : INlpServiceApi
    {
        private readonly IHttpClientFactory _clientFactory;

        public NlpServiceApi(IHttpClientFactory clientFactory)
        {
            _clientFactory = clientFactory;
        }

        public async Task<JsonElement> Identify_BUILD_DATE_In_NO_ChapterText(
            JsonElement chapterTextFromTextService)
        {
            var request = new HttpRequestMessage(
                HttpMethod.Post, 
                @"http://localhost:5000/identify-build-date-in-text-service-norwegian-chapter");

            request.Content = new StringContent(
                chapterTextFromTextService.ToString(),
                System.Text.Encoding.UTF8,
                "application/json");

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
                throw new System.Exception();
            }
        }
    }
}
