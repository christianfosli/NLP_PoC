using System;

namespace ServiceController.ServiceApp.Settings
{
	public class TransformerServiceSettings
	{
		public Uri ApiBaseUrl { get; set; }

		/// <summary>
		/// If this is true, the app will not connect to Transformer Service API.
		/// It will only return some test data.
		/// </summary>
		public bool RunAsTest { get; set; }
	}
}
