using System;

namespace ServiceController.Entities.AuthenticationService
{
	public class AuthenticationServiceSettings
	{
		public Uri ApiUrl { get; set; }
		public string ClientId { get; set; }
		public string ClientSecret { get; set; }
		public string Scope { get; set; }
	}
}
