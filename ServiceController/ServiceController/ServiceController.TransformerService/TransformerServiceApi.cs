using System;
using System.Net.Http;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;

namespace ServiceController.TransformerService
{
	public class TransformerServiceApi : ITransformerServiceApi
	{
		private readonly IHttpClientFactory _clientFactory;

		public TransformerServiceApi(IHttpClientFactory clientFactory)
		{
			_clientFactory = clientFactory;
		}

		public async Task<string> TransformNlpInformationToRdfKnowledge(
			Uri apiBaseUrl,
			JsonElement identifiedInformationInChapterTextData)
		{
			var request = new HttpRequestMessage(
				HttpMethod.Post,
				$@"{apiBaseUrl}identifier")
			{
				Content = new StringContent(
					identifiedInformationInChapterTextData.ToString(),
					Encoding.UTF8,
					"application/json")
			};

			var client = _clientFactory.CreateClient();
			var response = await client.SendAsync(request, HttpCompletionOption.ResponseHeadersRead);
			if (!response.IsSuccessStatusCode) throw new Exception();
			return await response.Content.ReadAsStringAsync();
		}
	}
}
