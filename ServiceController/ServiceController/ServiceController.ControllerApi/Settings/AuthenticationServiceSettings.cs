using System;

namespace ServiceController.ControllerApi.Settings
{
	public class AuthenticationServiceSettings
	{
		public Uri ApiBaseUrl { get; set; }
		public string ClientId { get; set; }
		public string ClientSecret { get; set; }
		public string Scope { get; set; }
	}
}
