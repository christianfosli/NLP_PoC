using System;
using System.Net.Http;
using System.Threading.Tasks;
using IdentityModel.Client;

namespace ServiceController.AuthenticationService
{
	public class AuthenticationApi : IAuthenticationApi
	{
		private readonly IHttpClientFactory _clientFactory;

		public AuthenticationApi(IHttpClientFactory clientFactory)
		{
			_clientFactory = clientFactory;
		}

		public async Task<string> GetAuthenticationToken(
			Uri apiBaseUrl, // For example: https://sdir-d-apim-common.azure-api.net/core-identityserver
			string clientId,
			string clientSecret,
			string scope)
		{
			var client = _clientFactory.CreateClient();
			var response = await client.RequestClientCredentialsTokenAsync(new ClientCredentialsTokenRequest
			{
				Address = $@"{apiBaseUrl}/connect/token",
				ClientId = clientId,
				ClientSecret = clientSecret,
				Scope = scope
			});

			if (response.Error == null)
				return response.AccessToken;

			throw new Exception();
		}
	}
}
