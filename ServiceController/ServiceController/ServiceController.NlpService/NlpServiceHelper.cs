using System;
using System.Collections.Generic;
using Newtonsoft.Json.Linq;
using System.Text.Json;
using Newtonsoft.Json;
using ServiceController.Entities.NlpService;

namespace ServiceController.NlpService
{
	public class NlpServiceHelper : INlpServiceHelper
	{
        public int CountItemsInNlpServiceApiResponse(JsonElement responseFromNlpService)
		{
            var jObject = JObject.Parse(responseFromNlpService.ToString());
            var firstObject = jObject.First; // Example: {"identified_build_dates": []}
            if (firstObject == null) return 0;
            var childrenOfFirstObject = firstObject.First; // Example: {[]}
            var childrenOfFirstObjectInArray = (JArray)childrenOfFirstObject;
            return childrenOfFirstObjectInArray?.Count ?? 0;
		}

        public Dictionary<int, NlpResource> MapNlpResources(
			Uri apiBaseUrl,
			JsonElement nlpResourceListFromNlpService)
        {
	        var dictionary = new Dictionary<int, NlpResource>();
	        using var document = JsonDocument.Parse(nlpResourceListFromNlpService.ToString());
	        var arrayEnumerator = document.RootElement.EnumerateArray();
	        var counter = 1;

	        while (arrayEnumerator.MoveNext())
	        {
		        var i = arrayEnumerator.Current;
		        i.TryGetProperty("title", out var title);
		        i.TryGetProperty("language", out var language);
		        i.TryGetProperty("url", out var url);

		        var item = new NlpResource
		        {
			        Title = title.GetString(),
			        Language = language.GetString(),
			        Url = new Uri($@"{apiBaseUrl}{url.GetString()}")
		        };

		        dictionary.Add(counter, item);
		        counter++;
	        }

	        return dictionary;
        }

		//
		// Test helpers
		//

        public Dictionary<int, NlpResource> GetNlpResourceTestDictionary()
        {
	        var dictionary = new Dictionary<int, NlpResource>
	        {
		        {
			        1, new NlpResource
			        {
				        Title = "vessel length overall",
				        Language = "no",
				        Url = new Uri("/identify-VESSEL-LENGTH-OVERALL-in-text-service-norwegian-chapter")
			        }
		        },
		        {
			        2, new NlpResource
			        {
				        Title = "electrical installation",
				        Language = "no",
				        Url = new Uri("/identify-ELECTRICAL-INSTALLATION-in-text-service-norwegian-chapter")
			        }
		        },
		        {
			        3, new NlpResource
			        {
				        Title = "build date",
				        Language = "no",
				        Url = new Uri("/identify-BUILD-DATE-in-text-service-norwegian-chapter")
			        }
		        },
		        {
			        4, new NlpResource
			        {
				        Title = "build date",
				        Language = "en",
				        Url = new Uri("/identify-BUILD-DATE-in-text-service-english-chapter")
			        }
		        },
		        {
			        5, new NlpResource
			        {
				        Title = "alternative reference",
				        Language = "no",
				        Url = new Uri("/identify-ALTERNATIVE-REFERENCE-in-text-service-norwegian-chapter")
			        }
		        },
		        {
			        6, new NlpResource
			        {
				        Title = "passenger",
				        Language = "no",
				        Url = new Uri("/identify-PASSENGER-in-text-service-norwegian-chapter")
			        }
		        },
		        {
			        7, new NlpResource
			        {
				        Title = "gross tonnage",
				        Language = "no",
				        Url = new Uri("/identify-GROSS-TONNAGE-in-text-service-norwegian-chapter")
			        }
		        },
		        {
			        8, new NlpResource
			        {
				        Title = "vessel",
				        Language = "no",
				        Url = new Uri("/identify-VESSEL-in-text-service-norwegian-chapter")
			        }
		        },
		        {
			        9, new NlpResource
			        {
				        Title = "flashpoint",
				        Language = "no",
				        Url = new Uri("/identify-FLASHPOINT-in-text-service-norwegian-chapter")
			        }
		        },
		        {
			        10, new NlpResource
			        {
				        Title = "vessel type",
				        Language = "no",
				        Url = new Uri("/identify-VESSEL-TYPE-in-text-service-norwegian-chapter")
			        }
		        },
		        {
			        11, new NlpResource
			        {
				        Title = "mobile unit",
				        Language = "no",
				        Url = new Uri("/identify-MOBILE-UNIT-in-text-service-norwegian-chapter")
			        }
		        },
		        {
			        12, new NlpResource
			        {
				        Title = "cargo",
				        Language = "no",
				        Url = new Uri("/identify-CARGO-in-text-service-norwegian-chapter")
			        }
		        },
		        {
			        13, new NlpResource
			        {
				        Title = "trade area",
				        Language = "no",
				        Url = new Uri("/identify-TRADE-AREA-in-text-service-norwegian-chapter")
			        }
		        },
		        {
			        14, new NlpResource
			        {
				        Title = "radio area",
				        Language = "no",
				        Url = new Uri("/identify-RADIO-AREA-in-text-service-norwegian-chapter")
			        }
		        },
		        {
			        15, new NlpResource
			        {
				        Title = "conversion",
				        Language = "no",
				        Url = new Uri("/identify-CONVERSION-in-text-service-norwegian-chapter")
			        }
		        },
		        {
			        16, new NlpResource
			        {
				        Title = "protected",
				        Language = "no",
				        Url = new Uri("/identify-PROTECTED-in-text-service-norwegian-chapter")
			        }
		        },
		        {
			        17, new NlpResource
			        {
				        Title = "load installation",
				        Language = "no",
				        Url = new Uri("/identify-LOAD-INSTALLATION-in-text-service-norwegian-chapter")
			        }
		        },
		        {
			        18, new NlpResource
			        {
				        Title = "propulsion power",
				        Language = "no",
				        Url = new Uri("/identify-PROPULSION-POWER-in-text-service-norwegian-chapter")
			        }
		        },
		        {
			        19, new NlpResource
			        {
				        Title = "keel laid",
				        Language = "no",
				        Url = new Uri("/identify-KEEL-LAID-in-text-service-norwegian-chapter")
			        }
		        }
			};

            return dictionary;
        }

        public JsonElement GetTestDataForIdentifyInformationInChapterTextData()
        {
	        var jsonResponse = 
		        @"{
			        ""identified_RADIO_AREA"": [
			        {
				        ""chapter_number"": ""1"",
				        ""part_number"": ""1"",
				        ""radio_area_type_text"": ""A3"",
				        ""regulation_day"": ""16"",
				        ""regulation_id"": ""1770"",
				        ""regulation_month"": ""12"",
				        ""regulation_year"": ""2016"",
				        ""section_number"": ""7""
			        },
			        {
				        ""chapter_number"": ""1"",
				        ""part_number"": ""1"",
				        ""radio_area_type_text"": ""A4"",
				        ""regulation_day"": ""16"",
				        ""regulation_id"": ""1770"",
				        ""regulation_month"": ""12"",
				        ""regulation_year"": ""2016"",
				        ""section_number"": ""7""
			        }]
				}";

	        var jsonResponseDeserialized = // UTF-8 fix
		        JsonConvert.DeserializeObject(jsonResponse)?.ToString();
	        using var doc = JsonDocument.Parse(jsonResponseDeserialized);
	        return doc.RootElement.Clone();
        }
	}
}
