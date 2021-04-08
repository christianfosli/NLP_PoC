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
using ServiceController.Entities.NlpService;
using ServiceController.Entities.TextService;
using ServiceController.ServiceApp.Settings;
using ServiceController.TransformerService;

namespace ServiceController.ServiceApp
{
	public class Program
	{
		//
		// App settings
		//

		private static Uri TextServiceRequestedRegulation { get; set; }
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

					TextServiceRequestedRegulation = new Uri(Configuration["AppInput:RequestedRegulationIri"]);
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

				var regulationResource = new RegulationResource
				{
					Url = TextServiceRequestedRegulation
				};

				Console.WriteLine($"Asking Text Service for regulation {regulationResource.RegulationYear}-{regulationResource.RegulationMonth}-{regulationResource.RegulationDay}-{regulationResource.RegulationNumber}.");
				
				var regulationFromTextService = await textServiceApi.GetRegulation(
					TextServiceSettings.ApiBaseUrl,
					Convert.ToInt32(regulationResource.RegulationYear),
					Convert.ToInt32(regulationResource.RegulationMonth),
					Convert.ToInt32(regulationResource.RegulationDay),
					Convert.ToInt32(regulationResource.RegulationNumber));

				var chapterList = textServiceHelper.SplitRegulationResponseIntoChapterList(regulationFromTextService);

				Console.WriteLine($"{chapterList.Count} chapters loaded successfully.");

				//
				// NLP Service
				//

				// Get NLP options

				Dictionary<int, NlpResource> nlpResourceDictionary;

				if (NlpServiceSettings.RunAsTest)
				{
					nlpResourceDictionary =
						nlpServiceHelper.GetNlpResourceTestDictionary(
							NlpServiceSettings.ApiBaseUrl);
				}
				else // send request to NLP Service API
				{
					var nlpResourceListFromNlpService =
						await nlpServiceApi.GetNlpResourceList(
							NlpServiceSettings.ApiBaseUrl);

					nlpResourceDictionary = 
						nlpServiceHelper.MapNlpResources(
							nlpResourceListFromNlpService);
				}

				foreach (var nlpResourceDictionaryItem in nlpResourceDictionary)
				{
					var selectedNlpResourceDictionary = nlpResourceDictionaryItem.Value;

					Console.WriteLine($"Asking NLP Service to identify information about {selectedNlpResourceDictionary.Title} ({selectedNlpResourceDictionary.Language}) in regulation {regulationResource.RegulationYear}-{regulationResource.RegulationMonth}-{regulationResource.RegulationDay}-{regulationResource.RegulationNumber}.");

					for (var i = 0; i < chapterList.Count; i++)
					{
						var requestNumber = i + 1;

						Console.WriteLine($"Processing chapter {requestNumber} of {chapterList.Count}:");

						JsonElement identifiedInformationInChapterTextData;

						if (NlpServiceSettings.RunAsTest)
						{
							identifiedInformationInChapterTextData =
								nlpServiceHelper.GetTestDataForIdentifyInformationInChapterTextData();
						}
						else // send request to NLP Service API
						{
							identifiedInformationInChapterTextData =
								await nlpServiceApi.IdentifyInformationInChapterTextData(
									chapterList[i],
									selectedNlpResourceDictionary.Url);
						}

						var itemCountOfNlpServiceResponse =
							nlpServiceHelper.CountItemsInNlpServiceApiResponse(
								identifiedInformationInChapterTextData);

						if (itemCountOfNlpServiceResponse > 0)
						{
							Console.WriteLine($"{itemCountOfNlpServiceResponse} detections in this chapter.");

							IdentifiedInformationInChapterTextDataList.Add(identifiedInformationInChapterTextData);
						}
						else
						{
							Console.WriteLine("No detections in this chapter.");
						}
					}
				}

				//
				// Transformer Service
				//

				Console.WriteLine("TODO: Asking Transformer Service to transform information.");

				foreach (var identifiedInformationInChapterTextData in IdentifiedInformationInChapterTextDataList)
				{
					var transformedRdfKnowledge =
						await transformerServiceApi.TransformNlpInformationToRdfKnowledge(
							TransformerServiceSettings.ApiBaseUrl,
							identifiedInformationInChapterTextData);

					TransformedRdfKnowledgeList.Add(transformedRdfKnowledge);
				}

				//
				// Authentication Service
				//

				try
				{
					Console.WriteLine("Asking Authentication Service for access token.");

					TopBraidEdgOAuthAccessToken = await authenticationApi.GetAuthenticationToken(
						AuthenticationServiceSettings.ApiBaseUrl,
						AuthenticationServiceSettings.ClientId,
						AuthenticationServiceSettings.ClientSecret,
						AuthenticationServiceSettings.Scope);

					Console.WriteLine("Successfully loaded access token.");
				}
				catch (Exception e)
				{
					Console.WriteLine("Oh no.. :-O Something went wrong. Here is an error message:");
					Console.WriteLine(e);
					Console.WriteLine("Service Controller application ended.");
					return 0;
				}

				//
				// Knowledge Service
				//

				foreach (var transformedRdfKnowledge in TransformedRdfKnowledgeList)
				{
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
						Console.WriteLine("Oh no.. :-O Something went wrong. Here is an error message:");
						Console.WriteLine(e);
						Console.WriteLine("Service Controller application ended.");
						Console.ResetColor();
						return 0;
					}

					Console.WriteLine("Successfully loaded knowledge.");
				}
			}

			Console.WriteLine("Service Controller application ended.");
			Console.ResetColor();
			return 0;
		}
	}
}
