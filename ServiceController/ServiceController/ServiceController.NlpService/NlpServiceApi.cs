using System;
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
                throw new Exception();
            }
        }

        public async Task<JsonElement> IdentifyInformationInChapterTextData(
            JsonElement chapterTextFromTextService,
            Uri nlpServiceApiResourceUrl)
        {
            var request = new HttpRequestMessage(
                HttpMethod.Post,
                nlpServiceApiResourceUrl);

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
                throw new Exception();
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
                Url = new Uri(urlPrefix + "identify-VESSEL-LENGTH-OVERALL-in-text-service-norwegian-chapter")
            });

            list.Add(new NlpResource
            {
                Title = "electrical installation",
                Language = "no",
                Url = new Uri(urlPrefix + "identify-ELECTRICAL-INSTALLATION-in-text-service-norwegian-chapter")
            });

            list.Add(new NlpResource {
                Title = "build date",
                Language = "no",
                Url = new Uri(urlPrefix + "identify-BUILD-DATE-in-text-service-norwegian-chapter")
            });

            list.Add(new NlpResource
            {
                Title = "build date",
                Language = "en",
                Url = new Uri(urlPrefix + "identify-BUILD-DATE-in-text-service-english-chapter")
            });

            list.Add(new NlpResource
            {
                Title = "alternative reference",
                Language = "no",
                Url = new Uri(urlPrefix + "identify-ALTERNATIVE-REFERENCE-in-text-service-norwegian-chapter")
            });

            list.Add(new NlpResource
            {
                Title = "passenger",
                Language = "no",
                Url = new Uri(urlPrefix + "identify-PASSENGER-in-text-service-norwegian-chapter")
            });

            list.Add(new NlpResource
            {
                Title = "gross tonnage",
                Language = "no",
                Url = new Uri(urlPrefix + "identify-GROSS-TONNAGE-in-text-service-norwegian-chapter")
            });

            list.Add(new NlpResource
            {
                Title = "vessel",
                Language = "no",
                Url = new Uri(urlPrefix + "identify-VESSEL-in-text-service-norwegian-chapter")
            });

            list.Add(new NlpResource
            {
                Title = "flashpoint",
                Language = "no",
                Url = new Uri(urlPrefix + "identify-FLASHPOINT-in-text-service-norwegian-chapter")
            });

            list.Add(new NlpResource
            {
                Title = "vessel type",
                Language = "no",
                Url = new Uri(urlPrefix + "identify-VESSEL-TYPE-in-text-service-norwegian-chapter")
            });

            list.Add(new NlpResource
            {
                Title = "mobile unit",
                Language = "no",
                Url = new Uri(urlPrefix + "identify-MOBILE-UNIT-in-text-service-norwegian-chapter")
            });

            list.Add(new NlpResource
            {
                Title = "cargo",
                Language = "no",
                Url = new Uri(urlPrefix + "identify-CARGO-in-text-service-norwegian-chapter")
            });

            list.Add(new NlpResource
            {
                Title = "trade area",
                Language = "no",
                Url = new Uri(urlPrefix + "identify-TRADE-AREA-in-text-service-norwegian-chapter")
            });

            list.Add(new NlpResource
            {
                Title = "radio area",
                Language = "no",
                Url = new Uri(urlPrefix + "identify-RADIO-AREA-in-text-service-norwegian-chapter")
            });

            list.Add(new NlpResource
            {
                Title = "conversion",
                Language = "no",
                Url = new Uri(urlPrefix + "identify-CONVERSION-in-text-service-norwegian-chapter")
            });

            list.Add(new NlpResource
            {
                Title = "protected",
                Language = "no",
                Url = new Uri(urlPrefix + "identify-PROTECTED-in-text-service-norwegian-chapter")
            });

            list.Add(new NlpResource
            {
                Title = "load installation",
                Language = "no",
                Url = new Uri(urlPrefix + "identify-LOAD-INSTALLATION-in-text-service-norwegian-chapter")
            });

            list.Add(new NlpResource
            {
                Title = "propulsion power",
                Language = "no",
                Url = new Uri(urlPrefix + "identify-PROPULSION-POWER-in-text-service-norwegian-chapter")
            });

            list.Add(new NlpResource
            {
                Title = "keel laid",
                Language = "no",
                Url = new Uri(urlPrefix + "identify-KEEL-LAID-in-text-service-norwegian-chapter")
            });

            return list;
		}
    }
}
