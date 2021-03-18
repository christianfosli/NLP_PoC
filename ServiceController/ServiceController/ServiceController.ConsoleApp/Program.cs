using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using ServiceController.TextService;
using ServiceController.NlpService;
using ServiceController.KnowledgeService;
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

                    // api
                    services.AddTransient<ITextServiceApi, TextServiceApi>();
                    services.AddTransient<INlpServiceApi, NlpServiceApi>();
                    services.AddTransient<ITopBraidEdgApi, TopBraidEdgApi>();

                    // helpers
                    services.AddTransient<ITextServiceHelper, TextServiceHelper>();
                    services.AddTransient<INlpServiceHelper, NlpServiceHelper>();

                }).UseConsoleLifetime();

            var host = builder.Build();

            using (var serviceScope = host.Services.CreateScope())
            {
                var services = serviceScope.ServiceProvider;

                // api
                var textServiceApi = services.GetRequiredService<ITextServiceApi>();
                var nlpServiceApi = services.GetRequiredService<INlpServiceApi>();
                var topBraidEdgApi = services.GetRequiredService<ITopBraidEdgApi>();

                // helpers
                var textServiceHelper = services.GetRequiredService<ITextServiceHelper>();
                var nlpServiceHelper = services.GetRequiredService<INlpServiceHelper>();

                Console.ResetColor();
                Console.WriteLine("Service Controller application started.");

                await topBraidEdgApi.TestInsert();





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
                var selectedRegulationDictionary = regulationDictionary[selectedRegulationDictionaryNumber];

                Console.ForegroundColor = ConsoleColor.Black;
                Console.BackgroundColor = ConsoleColor.Blue;
                Console.WriteLine(string.Format(
                    "Asking Text Service for regulation {0}-{1}-{2}-{3} ({4}).",
                    selectedRegulationDictionary.RegulationYear,
                    selectedRegulationDictionary.RegulationMonth,
                    selectedRegulationDictionary.RegulationDay,
                    selectedRegulationDictionary.RegulationNumber,
                    selectedRegulationDictionary.Language));

                var regulationFromTextService = await textServiceApi.GetRegulation(
                    Convert.ToInt32(selectedRegulationDictionary.RegulationYear),
                    Convert.ToInt32(selectedRegulationDictionary.RegulationMonth),
                    Convert.ToInt32(selectedRegulationDictionary.RegulationDay),
                    Convert.ToInt32(selectedRegulationDictionary.RegulationNumber));

                var chapterList = textServiceHelper.SplitRegulationResponseIntoChapterList(regulationFromTextService);

                Console.WriteLine(string.Format("{0} chapters loaded successfully.", chapterList.Count));
                Console.ResetColor();

                //
                // NLP
                //

                // Get NLP options
                var nlpResourceList = nlpServiceApi.GetNlpResourceList();
                var nlpResourceDictionary = nlpServiceHelper.AddNlpResourceListToDictionary(nlpResourceList);

                // Print NLP options
                var nlpResourceDictionaryPrinter = new NlpServiceResourceDictionaryPrinter(nlpResourceDictionary);
                nlpResourceDictionaryPrinter.PrintAllOptions();

                // Question to user
                string nlpResourceRequestedByTheUser;
                Console.Write(string.Format("Please select a NLP resource from the list ({0}-{1}):", 1, nlpResourceDictionary.Count));
                nlpResourceRequestedByTheUser = Console.ReadLine();
                int selectedNlpResourceDictionaryNumber = Convert.ToInt32(nlpResourceRequestedByTheUser);
                var selectedNlpResourceDictionary = nlpResourceDictionary[selectedNlpResourceDictionaryNumber];

                // Send requests to NLP service
                Console.BackgroundColor = ConsoleColor.Green;
                Console.ForegroundColor = ConsoleColor.Black;
                Console.WriteLine(string.Format(
                    "Asking NLP Service to identify information about {0} ({1}) in regulation {2}-{3}-{4}-{5} ({6}).",
                    selectedNlpResourceDictionary.Title,
                    selectedNlpResourceDictionary.Language,
                    selectedRegulationDictionary.RegulationYear,
                    selectedRegulationDictionary.RegulationMonth,
                    selectedRegulationDictionary.RegulationDay,
                    selectedRegulationDictionary.RegulationNumber,
                    selectedRegulationDictionary.Language));
                Console.ResetColor();

                for (int i = 0; i < chapterList.Count; i++)
                //for (int i = 0; i < 2; i++) // Limit on 2.
                {
                    Console.BackgroundColor = ConsoleColor.Yellow;
                    Console.ForegroundColor = ConsoleColor.Black;
                    var requestNumber = i + 1;
                    Console.WriteLine(string.Format(
                        "Processing chapter {0} of {1}:", 
                        requestNumber, 
                        chapterList.Count));
                    Console.ResetColor();

                    var identifiedInformationInChapterTextData =
                        await nlpServiceApi.IdentifyInformationInChapterTextData(
                            chapterList[i],
                            selectedNlpResourceDictionary.Url);

                    var itemCountOfNlpServiceResponse = 
                        nlpServiceHelper.CountItemsInNlpServiceApiResponse(
                            identifiedInformationInChapterTextData);

                    if(itemCountOfNlpServiceResponse > 0)
					{
                        Console.BackgroundColor = ConsoleColor.Green;
                        Console.ForegroundColor = ConsoleColor.Black;
                        Console.WriteLine(string.Format(
                            "{0} detections in this chapter:", 
                            itemCountOfNlpServiceResponse));
                        Console.ResetColor();

                        var nlpServiceResultPrinter = new NlpServiceResultPrinter(identifiedInformationInChapterTextData);
                        nlpServiceResultPrinter.PrintAllItems();
                    }
                    else
					{
                        Console.BackgroundColor = ConsoleColor.Green;
                        Console.ForegroundColor = ConsoleColor.Black;
                        Console.WriteLine("No detections in this chapter.");
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
