using System;
using System.Threading.Tasks;

namespace ServiceController.AuthenticationService
{
	public interface IAuthenticationApi
	{
		Task<string> GetAuthenticationToken(
			Uri address,
			string clientId,
			string clientSecret,
			string scope);
	}
}
