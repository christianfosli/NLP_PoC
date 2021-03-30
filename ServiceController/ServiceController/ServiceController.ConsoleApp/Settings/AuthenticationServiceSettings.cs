﻿using System;

namespace ServiceController.ConsoleApp.Settings
{
	public class AuthenticationServiceSettings
	{
		public Uri ApiUrl { get; set; }
		public string ClientId { get; set; }
		public string ClientSecret { get; set; }
		public string Scope { get; set; }
	}
}