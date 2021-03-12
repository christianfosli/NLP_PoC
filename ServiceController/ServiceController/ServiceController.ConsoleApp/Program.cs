using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using ServiceController.NlpService;
using ServiceController.TextService;
using System;
using System.Threading.Tasks;
using ServiceController.ConsoleApp.ConsolePrinter;

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
                    services.AddTransient<ITextServiceHelper, TextServiceHelper>();
                    services.AddTransient<INlpServiceApi, NlpServiceApi>();
                    services.AddTransient<INlpServiceHelper, NlpServiceHelper>();
                }).UseConsoleLifetime();

            var host = builder.Build();

            using (var serviceScope = host.Services.CreateScope())
            {
                var services = serviceScope.ServiceProvider;

                // api
                var textServiceApi = services.GetRequiredService<ITextServiceApi>();
                var nlpServiceApi = services.GetRequiredService<INlpServiceApi>();

                // helpers
                var textServiceHelper = services.GetRequiredService<ITextServiceHelper>();
                var nlpServiceHelper = services.GetRequiredService<INlpServiceHelper>();

                Console.ResetColor();
                Console.WriteLine("Service Controller application started.");

                /*
                Console.ForegroundColor = ConsoleColor.Black;
                Console.BackgroundColor = ConsoleColor.Blue;
                Console.WriteLine("Asking Text Service for a chapter.");
				var regulationChapterFromTextService = 
                    await textServiceApi.GetRegulationChapter(2013, 11, 22, 1404, 4);
                Console.WriteLine("Data from Text Service loaded successfully!");
                Console.ResetColor();
                */

                var regulationListFromTextService = await textServiceApi.GetRegulationList();
                var regulationList = textServiceHelper.MapRegulationResources(regulationListFromTextService);
                var regulationListPrinter = new TextServiceRegulationListPrinter(regulationList);
                regulationListPrinter.PrintRegulationList();

                Console.ForegroundColor = ConsoleColor.Black;
                Console.BackgroundColor = ConsoleColor.Blue;
                Console.WriteLine("Asking Text Service for a regulation.");

                var regulationFromTextService = await textServiceApi.GetRegulation(2013, 11, 22, 1404);
                var chapterList = textServiceHelper.SplitRegulationResponseIntoChapterList(regulationFromTextService);

                Console.WriteLine(string.Format("{0} chapters loaded successfully.", chapterList.Count));
                Console.ResetColor();

                Console.BackgroundColor = ConsoleColor.Green;
                Console.ForegroundColor = ConsoleColor.Black;
                Console.WriteLine("Asking NLP Service for information.");
                Console.ResetColor();

                //for (int i = 0; i < chapterList.Count; i++)
                for (int i = 0; i < 2; i++) // TODO: Remove for limit
                {
                    Console.BackgroundColor = ConsoleColor.Yellow;
                    Console.ForegroundColor = ConsoleColor.Black;
                    var requestNumber = i + 1;
                    Console.WriteLine(string.Format("Asking for BUILD_DATE ({0}:{1}):", requestNumber, chapterList.Count));
                    Console.ResetColor();

                    var identified_BUILD_DATE_In_NO_ChapterText =
                        await nlpServiceApi.Identify_BUILD_DATE_In_NO_ChapterText(chapterList[i]);

                    var itemCountOfNlpServiceResponse = 
                        nlpServiceHelper.CountItemsInNlpServiceApiResponse(
                            identified_BUILD_DATE_In_NO_ChapterText);

                    if(itemCountOfNlpServiceResponse > 0)
					{
                        Console.BackgroundColor = ConsoleColor.Green;
                        Console.ForegroundColor = ConsoleColor.Black;
                        Console.WriteLine(string.Format("{0} detections by NLP Service.", itemCountOfNlpServiceResponse));
                        Console.ResetColor();

                        var nlpServiceResultPrinter = new NlpServiceResultPrinter(identified_BUILD_DATE_In_NO_ChapterText);
                        nlpServiceResultPrinter.ExampleOnPrintingBuildDateResult();
                    }
                    else
					{
                        Console.BackgroundColor = ConsoleColor.Green;
                        Console.ForegroundColor = ConsoleColor.Black;
                        Console.WriteLine("No detections by NLP Service.");
                        Console.ResetColor();
                    }
                }
                
                /* TODO maby include later
                try
                {

                }
                catch (Exception ex)
                {
                    var logger = services.GetRequiredService<ILogger<Program>>();

                    logger.LogError(ex, "An error occurred.");
                }
                */
            }

            return 0;
        }
    }
}
