using System.Collections.Generic;
using System.Net.Http;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;
using Newtonsoft.Json;
using ServiceController.Entities.NlpService;

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
                Encoding.UTF8,
                "application/json");

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
                throw new System.Exception();
            }
        }

        // TODO: move this list to the api
        public List<NlpResource> GetNlpResourceList()
		{
            var list = new List<NlpResource>();

            var urlPrefix = @"http://localhost:5000/";

            list.Add(new NlpResource
            {
                Title = "vessel length overall",
                Language = "no",
                Url = new System.Uri(urlPrefix + "identify-vessel-length-overall-in-text-service-norwegian-chapter")
            });

            list.Add(new NlpResource
            {
                Title = "electrical installation",
                Language = "no",
                Url = new System.Uri(urlPrefix + "identify-electrical-installation-in-text-service-norwegian-chapter")
            });

            list.Add(new NlpResource {
                Title = "build date",
                Language = "no",
                Url = new System.Uri(urlPrefix + "identify-build-date-in-text-service-norwegian-chapter")
            });

            list.Add(new NlpResource
            {
                Title = "build date",
                Language = "en",
                Url = new System.Uri(urlPrefix + "identify-build-date-in-text-service-english-chapter")
            });

            //TODO add all

            return list;
		}
    }
}
