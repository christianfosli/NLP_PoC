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

		public async Task TestInsert()
		{
			var nvc = new List<KeyValuePair<string, string>>();

			nvc.Add(new KeyValuePair<string, string>("update", @"INSERT DATA { GRAPH <urn:x-evn-master:nlppoctestontology> { <http://test.org/test1> <http://www.w3.org/2000/01/rdf-schema%23comment> 'Test 11 by Lars' . } }"));

			var request = new HttpRequestMessage(
				HttpMethod.Post,
				@"https://sdir-d-apim-common.azure-api.net/core-topbraid-edg/tbl/sparql")
			{
				Content = new FormUrlEncodedContent(nvc)
			};

			var client = _clientFactory.CreateClient();

			// Initialization  
			string authorizationToken = "";

			// Setting Authorization.  
			client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", authorizationToken);

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
