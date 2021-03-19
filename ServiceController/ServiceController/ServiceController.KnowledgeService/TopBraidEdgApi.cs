using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Threading.Tasks;

namespace ServiceController.KnowledgeService
{
	public class TopBraidEdgApi : ITopBraidEdgApi
	{
		private readonly IHttpClientFactory _clientFactory;

		public TopBraidEdgApi(IHttpClientFactory clientFactory)
		{
			_clientFactory = clientFactory;
		}

		public async Task TestInsert(
			string topBraidEdgOAuthAccessToken, 
			string topBraidEdgSparqlInsertQuery,
			string topBraidEdgWorkflowUrn)
		{
			var contentList = new List<string>
			{
				$"update={Uri.EscapeDataString(topBraidEdgSparqlInsertQuery)}",
				$"using-graph-uri={Uri.EscapeDataString(topBraidEdgWorkflowUrn)}"
			};

			var request = new HttpRequestMessage(
				HttpMethod.Post,
				@"https://sdir-d-apim-common.azure-api.net/core-topbraid-edg/tbl/sparql")
			{
				Content = new StringContent(string.Join("&", contentList))
			};

			//request.Content.Headers.ContentType = new MediaTypeHeaderValue("application/x-www-form-urlencoded");
			request.Content.Headers.Remove("Content-Type");
			request.Content.Headers.TryAddWithoutValidation(
				"Content-Type", 
				"application/x-www-form-urlencoded; charset=UTF-8");

			var client = _clientFactory.CreateClient();

			client.DefaultRequestHeaders.Authorization = 
				new AuthenticationHeaderValue("Bearer", topBraidEdgOAuthAccessToken);

			var response = await client.SendAsync(request, HttpCompletionOption.ResponseHeadersRead);

			if (response.IsSuccessStatusCode)
			{
				var jsonResponse = await response.Content.ReadAsStringAsync();

				// The insert is a success if jsonResponse is blank.

				if (!string.IsNullOrWhiteSpace(jsonResponse))
				{
					// Insert was not successful.
					throw new Exception();
				}
			}
			else
			{
				throw new Exception();
			}
		}
	}
}
