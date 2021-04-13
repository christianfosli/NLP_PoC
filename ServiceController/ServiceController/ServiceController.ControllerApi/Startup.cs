using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using ServiceController.ControllerApi.BackgroundServices;
using ServiceController.ControllerApi.Settings;

namespace ServiceController.ControllerApi
{
	public class Startup
	{
		public Startup(IConfiguration configuration)
		{
			Configuration = configuration;
		}

		public IConfiguration Configuration { get; }

		// This method gets called by the runtime.
		// Use this method to add services to the container.
		public void ConfigureServices(IServiceCollection services)
		{
			services.AddControllers();
			services.AddHostedService<NlpQueuedHostedService>();
			services.AddSingleton<INlpBackgroundTaskQueue>(ctx => new NlpBackgroundTaskQueue(100));

			// Load settings
			services.AddSingleton(Configuration.GetSection("AuthenticationServiceSettings").Get<AuthenticationServiceSettings>());
			services.AddSingleton(Configuration.GetSection("TextServiceSettings").Get<TextServiceSettings>());
			services.AddSingleton(Configuration.GetSection("NlpServiceSettings").Get<NlpServiceSettings>());
			services.AddSingleton(Configuration.GetSection("TransformerServiceSettings").Get<TransformerServiceSettings>());
			services.AddSingleton(Configuration.GetSection("KnowledgeServiceSettings").Get<KnowledgeServiceSettings>());

			// Load secrets
			services.AddSingleton(Configuration.GetSection("AuthenticationServiceSecrets").Get<AuthenticationServiceSecrets>());
		}

		// This method gets called by the runtime.
		// Use this method to configure the HTTP request pipeline.
		public void Configure(
			IApplicationBuilder app,
			IWebHostEnvironment env)
		{
			if (env.IsDevelopment())
			{
				app.UseDeveloperExceptionPage();
			}

			app.UseHttpsRedirection();

			app.UseRouting();

			app.UseAuthorization();

			app.UseEndpoints(endpoints =>
			{
				endpoints.MapControllers();
			});
		}
	}
}
