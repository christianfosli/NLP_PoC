using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using ServiceController.TextService;
using ServiceController.NlpService;
using ServiceController.KnowledgeService;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.Json;
using System.Threading.Tasks;
using Microsoft.Extensions.Configuration;
using ServiceController.AuthenticationService;
using ServiceController.ConsoleApp.ConsolePrinter;
using ServiceController.ConsoleApp.Settings;
using ServiceController.TransformerService;

namespace ServiceController.ConsoleApp
{
	public class Program
    {
		//
		// App settings
		//

	    public static IConfigurationRoot Configuration { get; set; }
	    private static AuthenticationServiceSettings AuthenticationServiceSettings { get; set; }
		private static TextServiceSettings TextServiceSettings { get; set; }
		private static NlpServiceSettings NlpServiceSettings { get; set; }
		private static TransformerServiceSettings TransformerServiceSettings { get; set; }
		private static KnowledgeServiceSettings KnowledgeServiceSettings { get; set; }

		//
		// App memory
		//

		private static string TopBraidEdgOAuthAccessToken { get; set; }
		private static List<JsonElement> IdentifiedInformationInChapterTextDataList { get; set; } = new List<JsonElement>();
		private static List<string> TransformedRdfKnowledgeList { get; set; } = new List<string>();

		static async Task<int> Main(string[] args)
        {
	        var hostBuilder = new HostBuilder()
	            .ConfigureAppConfiguration((hostContext, configurationBuilder) =>
	            {
		            configurationBuilder.AddUserSecrets<Program>();
                    Configuration = configurationBuilder.Build();

					//
					// Load settings
					//

					AuthenticationServiceSettings =
						Configuration.GetSection("AuthenticationServiceSettings")
							.Get<AuthenticationServiceSettings>();

					TextServiceSettings =
						Configuration.GetSection("TextServiceSettings")
							.Get<TextServiceSettings>();

					NlpServiceSettings =
						Configuration.GetSection("NlpServiceSettings")
							.Get<NlpServiceSettings>();

					TransformerServiceSettings =
						Configuration.GetSection("TransformerServiceSettings")
							.Get<TransformerServiceSettings>();

					KnowledgeServiceSettings =
						Configuration.GetSection("KnowledgeServiceSettings")
							.Get<KnowledgeServiceSettings>();
	            })
                .ConfigureServices((hostContext, services) =>
                {
                    services.AddHttpClient();

                    // api
                    services.AddTransient<ITextServiceApi, TextServiceApi>();
                    services.AddTransient<INlpServiceApi, NlpServiceApi>();
                    services.AddTransient<ITransformerServiceApi, TransformerServiceApi>();
					services.AddTransient<ITopBraidEdgApi, TopBraidEdgApi>();
                    services.AddTransient<IAuthenticationApi, AuthenticationApi>();

					// helpers
					services.AddTransient<ITextServiceHelper, TextServiceHelper>();
                    services.AddTransient<INlpServiceHelper, NlpServiceHelper>();
                })
                .UseConsoleLifetime();

            var host = hostBuilder.Build();

            using var serviceScope = host.Services.CreateScope();
            {
	            var services = serviceScope.ServiceProvider;

	            // api
	            var textServiceApi = services.GetRequiredService<ITextServiceApi>();
	            var nlpServiceApi = services.GetRequiredService<INlpServiceApi>();
	            var transformerServiceApi = services.GetRequiredService<ITransformerServiceApi>();
				var topBraidEdgApi = services.GetRequiredService<ITopBraidEdgApi>();
				var authenticationApi = services.GetRequiredService<IAuthenticationApi>();

				// helpers
				var textServiceHelper = services.GetRequiredService<ITextServiceHelper>();
	            var nlpServiceHelper = services.GetRequiredService<INlpServiceHelper>();

	            Console.ResetColor();
	            Console.WriteLine("Service Controller application started.");

	            //
				// Text Service
				//

				// Get regulation options
				var regulationListFromTextService = 
					await textServiceApi.GetRegulationList(TextServiceSettings.ApiBaseUrl);
	            var regulationDictionary =
		            textServiceHelper.MapRegulationResources(regulationListFromTextService);

                do
                {
	                // Print regulation options
					var regulationDictionaryPrinter = new TextServiceRegulationDictionaryPrinter(regulationDictionary);
					regulationDictionaryPrinter.PrintAllOptions();

					// Question to user
					Console.Write($"Please select a regulation from the list ({1}-{regulationDictionary.Count}):");
					var regulationRequestedByTheUser = Console.ReadLine();
					var selectedRegulationDictionaryNumber = Convert.ToInt32(regulationRequestedByTheUser);

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
						TextServiceSettings.ApiBaseUrl,
						Convert.ToInt32(selectedRegulationDictionary.RegulationYear),
						Convert.ToInt32(selectedRegulationDictionary.RegulationMonth),
						Convert.ToInt32(selectedRegulationDictionary.RegulationDay),
						Convert.ToInt32(selectedRegulationDictionary.RegulationNumber));

					var chapterList = textServiceHelper.SplitRegulationResponseIntoChapterList(regulationFromTextService);

					Console.WriteLine($"{chapterList.Count} chapters loaded successfully.");
					Console.ResetColor();

					//
					// NLP Service
					//

					// TODO
					if (NlpServiceSettings.RunAsTest)
					{

					}
					else
					{
						
					}

					// Get NLP options
					var nlpResourceListFromNlpService =
					await nlpServiceApi.GetNlpResourceList(NlpServiceSettings.ApiBaseUrl);
					var nlpResourceDictionary = nlpServiceHelper.MapNlpResources(nlpResourceListFromNlpService);

					// Print NLP options
					var nlpResourceDictionaryPrinter = new NlpServiceResourceDictionaryPrinter(nlpResourceDictionary);
					nlpResourceDictionaryPrinter.PrintAllOptions();

					// Question to user
					Console.Write($"Please select a NLP resource from the list ({1}-{nlpResourceDictionary.Count}):");
					var nlpResourceRequestedByTheUser = Console.ReadLine();
					var selectedNlpResourceDictionaryNumber = Convert.ToInt32(nlpResourceRequestedByTheUser);
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

					var totalNlpFindingCounter = 0;

					for (var i = 0; i < chapterList.Count; i++)
					{
						Console.BackgroundColor = ConsoleColor.Yellow;
						Console.ForegroundColor = ConsoleColor.Black;
						var requestNumber = i + 1;
						Console.WriteLine($"Processing chapter {requestNumber} of {chapterList.Count}:");
						Console.ResetColor();

						var identifiedInformationInChapterTextData =
							await nlpServiceApi.IdentifyInformationInChapterTextData(
								chapterList[i],
								selectedNlpResourceDictionary.Url);

						var itemCountOfNlpServiceResponse =
							nlpServiceHelper.CountItemsInNlpServiceApiResponse(
								identifiedInformationInChapterTextData);

						if (itemCountOfNlpServiceResponse > 0)
						{
							Console.BackgroundColor = ConsoleColor.Green;
							Console.ForegroundColor = ConsoleColor.Black;
							Console.WriteLine($"{itemCountOfNlpServiceResponse} detections in this chapter:");
							Console.ResetColor();

							var nlpServiceResultPrinter = new NlpServiceResultPrinter(identifiedInformationInChapterTextData);
							nlpServiceResultPrinter.PrintAllItems();

							totalNlpFindingCounter += itemCountOfNlpServiceResponse;

							IdentifiedInformationInChapterTextDataList.Add(identifiedInformationInChapterTextData);
						}
						else
						{
							Console.BackgroundColor = ConsoleColor.Green;
							Console.ForegroundColor = ConsoleColor.Black;
							Console.WriteLine("No detections in this chapter.");
							Console.ResetColor();
						}
					}

					if (totalNlpFindingCounter > 0)
					{
						// Question to user:
						Console.Write($"Would you like to proceed with the {totalNlpFindingCounter} NLP findings or start over? Continue (c) or Start over (s): ");
						var userInput = Console.ReadLine();
						var userInputToLower = userInput?.ToLower();
						if (userInputToLower == "c")
							break;
					}
					else
					{
						Console.Write("There were no NLP findings. Please press enter to start over.");
						Console.ReadLine();

						Console.Write("Resetting memory.");
						IdentifiedInformationInChapterTextDataList = new List<JsonElement>();
					}

					Console.WriteLine("Service Controller application is starting over.");
				} while (true);

				//
				// Transformer Service
				//

				// TODO
				if (TransformerServiceSettings.RunAsTest)
				{

				}
				else
				{
					
				}

	            Console.ForegroundColor = ConsoleColor.Black;
                Console.BackgroundColor = ConsoleColor.Magenta;
                Console.WriteLine("TODO: Asking Transformer Service to transform information.");

				foreach (var identifiedInformationInChapterTextData in IdentifiedInformationInChapterTextDataList)
                {
					var transformedRdfKnowledge =
						await transformerServiceApi.TransformNlpInformationToRdfKnowledge(
							TransformerServiceSettings.ApiBaseUrl,
							identifiedInformationInChapterTextData);

					TransformedRdfKnowledgeList.Add(transformedRdfKnowledge);
				}

				Console.ResetColor();

				//
				// Authentication Service
				//

				try
				{
					Console.ForegroundColor = ConsoleColor.Black;
					Console.BackgroundColor = ConsoleColor.DarkCyan;
					Console.WriteLine("Asking Authentication Service for access token.");

					TopBraidEdgOAuthAccessToken = await authenticationApi.GetAuthenticationToken(
						AuthenticationServiceSettings.ApiBaseUrl,
						AuthenticationServiceSettings.ClientId,
						AuthenticationServiceSettings.ClientSecret,
						AuthenticationServiceSettings.Scope);

					Console.WriteLine("Successfully loaded access token.");
					Console.ResetColor();
				}
				catch (Exception e)
				{
					Console.ForegroundColor = ConsoleColor.Black;
					Console.BackgroundColor = ConsoleColor.Red;
					Console.WriteLine("Oh no.. :-O Something went wrong. Here is an error message:");
					Console.WriteLine(e);
					Console.ResetColor();
					Console.WriteLine("Service Controller application ended.");
					return 0;
				}

				//
				// Knowledge Service
				//

				foreach (var transformedRdfKnowledge in TransformedRdfKnowledgeList)
				{
					Console.ForegroundColor = ConsoleColor.Black;
					Console.BackgroundColor = ConsoleColor.Cyan;
					Console.WriteLine("Asking Knowledge Service to construct SPARQL INSERT query.");

					var topBraidEdgSparqlInsertBuilder = new Entities.KnowledgeService.TopBraidEdgSparqlInsertBuilder(
						KnowledgeServiceSettings.TopBraidEdgOntologyId,
						KnowledgeServiceSettings.TopBraidEdgWorkflowId,
						KnowledgeServiceSettings.TopBraidEdgUserId,
						transformedRdfKnowledge
					);

					Console.WriteLine($"Successfully parsed {topBraidEdgSparqlInsertBuilder.Graph.Nodes.Count()} triples from Transformer Service.");
					var sparqlInsertQueryString = topBraidEdgSparqlInsertBuilder.BuildSparqlInsertQueryString();
					Console.WriteLine("Successfully constructed SPARQL INSERT query.");
					var topBraidEdgGraphUrn = topBraidEdgSparqlInsertBuilder.BuildTopBraidEdgGraphUrn();
					Console.WriteLine($"Loading knowledge into TopBraid EDG graph: {topBraidEdgGraphUrn}");

					try
					{
						await topBraidEdgApi.TestInsert(
							KnowledgeServiceSettings.ApiBaseUrl,
							TopBraidEdgOAuthAccessToken,
							sparqlInsertQueryString,
							topBraidEdgGraphUrn);
					}
					catch (Exception e)
					{
						Console.ForegroundColor = ConsoleColor.Black;
						Console.BackgroundColor = ConsoleColor.Red;
						Console.WriteLine("Oh no.. :-O Something went wrong. Here is an error message:");
						Console.WriteLine(e);
						Console.ResetColor();
						Console.WriteLine("Service Controller application ended.");
						return 0;
					}

					Console.ForegroundColor = ConsoleColor.Black;
					Console.BackgroundColor = ConsoleColor.Cyan;
					Console.WriteLine($"Successfully loaded knowledge. Please visit TopBraid EDG (workflow: {topBraidEdgSparqlInsertBuilder.WorkflowId}) to review the result.");
				}

	            Console.ResetColor();
	            Console.WriteLine("Service Controller application ended.");
            }

            return 0;
        }
    }
}
