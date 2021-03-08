using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using ServiceController.NlpService;
using ServiceController.TextService;
using System;
using System.Text.Json;
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
                    services.AddTransient<INlpServiceApi, NlpServiceApi>();
                }).UseConsoleLifetime();

            var host = builder.Build();

            using (var serviceScope = host.Services.CreateScope())
            {
                var services = serviceScope.ServiceProvider;

                try
                {
                    Console.ResetColor();
                    Console.WriteLine("Service Controller application started.");
                    Console.ForegroundColor = ConsoleColor.Black;
                    Console.BackgroundColor = ConsoleColor.Blue;
                    Console.WriteLine("Asking Text Service for data.");
                    var textServiceApi = services.GetRequiredService<ITextServiceApi>();
					var regulationChapterAsJson = await textServiceApi.GetRegulationChapterAsJson(2013, 11, 22, 1404, 4);
                    Console.WriteLine("Data from Text Service loaded successfully!");
                    Console.ResetColor();

                    if (regulationChapterAsJson != null)
					{
                        Console.BackgroundColor = ConsoleColor.Green;
                        Console.ForegroundColor = ConsoleColor.Black;
                        Console.WriteLine("Asking NLP Service for data.");
                        var nlpServiceApi = services.GetRequiredService<INlpServiceApi>();
                        var identified_BUILD_DATE_In_NO_ChapterText =
                            await nlpServiceApi.Identify_BUILD_DATE_In_NO_ChapterText(
                                (JsonElement)regulationChapterAsJson);
                        Console.WriteLine("Data from NLP Service loaded successfully!");
                        Console.ResetColor();
                    }
                    else
					{
                        Console.BackgroundColor = ConsoleColor.Red;
                        Console.ForegroundColor = ConsoleColor.Black;
                        Console.WriteLine("Whoops!");
                        Console.ResetColor();
                    }
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
