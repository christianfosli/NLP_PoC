﻿using Microsoft.Extensions.DependencyInjection;
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
                var regulationDictionary = textServiceHelper.MapRegulationResources(regulationListFromTextService);
                var regulationDictionaryPrinter = new TextServiceRegulationDictionaryPrinter(regulationDictionary);
                regulationDictionaryPrinter.PrintAllOptions();

                // Question to user
                string regulationRequestedByTheUser;
                Console.Write(string.Format("Please select a regulation from the list ({0}-{1}):", 1, regulationDictionary.Count));
                regulationRequestedByTheUser = Console.ReadLine();
                int selectedRegulationDictionaryNumber = Convert.ToInt32(regulationRequestedByTheUser);

                // Get selected regulation
                var selectedRegulation = regulationDictionary[selectedRegulationDictionaryNumber];

                Console.ForegroundColor = ConsoleColor.Black;
                Console.BackgroundColor = ConsoleColor.Blue;
                Console.WriteLine(string.Format(
                    "Asking Text Service for a regulation {0}-{1}-{2}-{3} ({4}).",
                    selectedRegulation.RegulationYear,
                    selectedRegulation.RegulationMonth,
                    selectedRegulation.RegulationDay,
                    selectedRegulation.RegulationNumber,
                    selectedRegulation.Language));

                var regulationFromTextService = await textServiceApi.GetRegulation(
                    Convert.ToInt32(selectedRegulation.RegulationYear),
                    Convert.ToInt32(selectedRegulation.RegulationMonth),
                    Convert.ToInt32(selectedRegulation.RegulationDay),
                    Convert.ToInt32(selectedRegulation.RegulationNumber));

                var chapterList = textServiceHelper.SplitRegulationResponseIntoChapterList(regulationFromTextService);

                Console.WriteLine(string.Format("{0} chapters loaded successfully.", chapterList.Count));
                Console.ResetColor();

                //
                // NLP
                //

                // List NLP options
                var nlpResourceList = nlpServiceApi.GetNlpResourceList();
                var nlpResourceDictionary = nlpServiceHelper.AddNlpResourceListToDictionary(nlpResourceList);






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
