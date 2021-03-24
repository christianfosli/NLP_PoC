﻿using System;
using System.Net.Http;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;
using Newtonsoft.Json;

namespace ServiceController.NlpService
{
    public class NlpServiceApi : INlpServiceApi
    {
        private readonly IHttpClientFactory _clientFactory;

        public NlpServiceApi(IHttpClientFactory clientFactory)
        {
            _clientFactory = clientFactory;
        }

        //TODO remove
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

            if (!response.IsSuccessStatusCode) throw new Exception();

            var jsonResponse = await response.Content.ReadAsStringAsync();
            var jsonResponseDeserialized = // UTF-8 fix
	            JsonConvert.DeserializeObject(jsonResponse)?.ToString();
            using var doc = JsonDocument.Parse(jsonResponseDeserialized);

            return doc.RootElement.Clone();
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

            if (!response.IsSuccessStatusCode) throw new Exception();

            var jsonResponse = await response.Content.ReadAsStringAsync();
            var jsonResponseDeserialized = // UTF-8 fix
	            JsonConvert.DeserializeObject(jsonResponse)?.ToString();
            using var doc = JsonDocument.Parse(jsonResponseDeserialized);

            return doc.RootElement.Clone();
        }

        public async Task<JsonElement> GetNlpResourceList()
		{
			var request = new HttpRequestMessage(
				HttpMethod.Get,
                @"http://localhost:5000/nlp-rule-based-matching-options");
			var client = _clientFactory.CreateClient();
			var response = await client.SendAsync(request, HttpCompletionOption.ResponseHeadersRead);

			if (!response.IsSuccessStatusCode) throw new Exception();

			var jsonResponse = await response.Content.ReadAsStringAsync();
			var jsonResponseDeserialized = // UTF-8 fix
				JsonConvert.DeserializeObject(jsonResponse)?.ToString();
			using var doc = JsonDocument.Parse(jsonResponseDeserialized);

			return doc.RootElement.Clone();
		}
    }
}
