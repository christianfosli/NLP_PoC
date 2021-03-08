using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using ServiceController.TextService;
using System;
using System.Threading.Tasks;

namespace ServiceController.ConsoleApp
{
    public class Program
    {
        static async Task<int> Main(string[] args)
        {
            var builder = new HostBuilder()
                .ConfigureServices((hostContext, services) =>
                {
                    services.AddHttpClient();
                    services.AddTransient<ITextServiceApi, TextServiceApi>();
                }).UseConsoleLifetime();

            var host = builder.Build();

            using (var serviceScope = host.Services.CreateScope())
            {
                var services = serviceScope.ServiceProvider;

                try
                {
                    var textServiceApi = services.GetRequiredService<ITextServiceApi>();
					var regulationChapterAsJson = await textServiceApi.GetRegulationChapterAsJson(2013, 11, 22, 1404, 4);
                    Console.BackgroundColor = ConsoleColor.Blue;
                    Console.WriteLine("Data from Text Service loaded successfully!");
                    Console.ResetColor();
                }
                catch (Exception ex)
                {
                    var logger = services.GetRequiredService<ILogger<Program>>();

                    logger.LogError(ex, "An error occurred.");
                }
            }

            return 0;
        }
    }
}
