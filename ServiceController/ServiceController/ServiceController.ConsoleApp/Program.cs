using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using ServiceController.TextService;
using ServiceController.NlpService;
using ServiceController.KnowledgeService;
using System;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.Extensions.Configuration;
using ServiceController.AuthenticationService;
using ServiceController.ConsoleApp.ConsolePrinter;
using ServiceController.ConsoleApp.Settings;

namespace ServiceController.ConsoleApp
{
	public class Program
    {
	    public static IConfigurationRoot Configuration { get; set; }
	    private static AuthenticationServiceSettings AuthenticationServiceSettings { get; set; }
		private static TextServiceSettings TextServiceSettings { get; set; }
		private static KnowledgeServiceSettings KnowledgeServiceSettings { get; set; }
		private static string TopBraidEdgOAuthAccessToken { get; set; }

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

					// Get NLP options
					var nlpResourceListFromNlpService = await nlpServiceApi.GetNlpResourceList();
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

					for (int i = 0; i < chapterList.Count; i++)
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
						// Message to user
						Console.Write("There were no NLP findings. Please press enter to start over.");
						Console.ReadLine();
					}

					Console.WriteLine("Service Controller application is starting over.");
				} while (true);

	            //
	            // Transformer Service
	            //

	            Console.ForegroundColor = ConsoleColor.Black;
	            Console.BackgroundColor = ConsoleColor.Magenta;
	            Console.WriteLine("TODO: Asking Transformer Service to transform information.");
	            Console.ResetColor();

	            //
	            // Knowledge Service
	            //

	            Console.ForegroundColor = ConsoleColor.Black;
	            Console.BackgroundColor = ConsoleColor.Cyan;
	            Console.WriteLine("Asking Knowledge Service to construct SPARQL INSERT query.");

	            var topBraidEdgSparqlInsertBuilder = new Entities.KnowledgeService.TopBraidEdgSparqlInsertBuilder(
		            KnowledgeServiceSettings.TopBraidEdgOntologyId,
		            KnowledgeServiceSettings.TopBraidEdgWorkflowId,
		            KnowledgeServiceSettings.TopBraidEdgUserId,
		            RdfTurtleTriplesForTesting
	            );

	            Console.WriteLine($"Successfully parsed {topBraidEdgSparqlInsertBuilder.Graph.Nodes.Count()} triples from Transformer Service.");
	            var sparqlInsertQueryString = topBraidEdgSparqlInsertBuilder.BuildSparqlInsertQueryString();
	            Console.WriteLine("Successfully constructed SPARQL INSERT query.");
	            var topBraidEdgGraphUrn = topBraidEdgSparqlInsertBuilder.BuildTopBraidEdgGraphUrn();
	            Console.WriteLine($"Loading knowledge into TopBraid EDG graph: {topBraidEdgGraphUrn}");

	            try
	            {
		            Console.ForegroundColor = ConsoleColor.Black;
		            Console.BackgroundColor = ConsoleColor.DarkCyan;
		            Console.WriteLine("Asking Authentication Service for access token.");

		            TopBraidEdgOAuthAccessToken = await authenticationApi.GetAuthenticationToken(
			            AuthenticationServiceSettings.ApiUrl,
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

				try
	            {
		            await topBraidEdgApi.TestInsert(
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
	            Console.ResetColor();
	            Console.WriteLine("Service Controller application ended.");
            }

            return 0;
        }

        //
        // TEMP TODO Remove
        //

        private static string RdfTurtleTriplesForTesting => @"@prefix owl: <http://www.w3.org/2002/07/owl#> .
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .
@prefix sh: <http://www.w3.org/ns/shacl#> .
@prefix unit: <http://qudt.org/vocab/unit/> .
@prefix sdir: <https://www.sdir.no/SDIR_Simulator#> .
@prefix scope: <https://www.sdir.no/SDIR_Simulator/shapes/scope#> .
@prefix tradearea: <https://www.sdir.no/SDIR_Simulator/tradeArea#> .
@prefix vesseltype: <https://www.sdir.no/SDIR_Simulator/vesselType#> .

scope:PS_minLOA_8_maxLOA_15 a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde største lengde mellom 8 og 15""@no, ""Scope of length overall between 8 and 15""@en;
  sh:path sdir:vesselLengthOverall;
  sh:minInclusive ""8"";
  sh:maxExclusive ""15"";
  sh:datatype unit:M .

scope:PS_minLOA_10_maxLOA_15 a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde største lengde mellom 10.67 og 15""@no, ""Scope of length overall between 10.67 and 15""@en;
  sh:path sdir:vesselLengthOverall;
  sh:minInclusive ""10.67"";
  sh:maxExclusive ""15"";
  sh:datatype unit:M .

scope:PS_maxLOA_10 a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde største lengde 10.67""@no, ""Scope of length overall 10.67""@en;
  sh:path sdir:vesselLengthOverall;
  sh:maxExclusive ""10.67"";
  sh:datatype unit:M .

scope:PS_minLOA_6_maxLOA_15 a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde største lengde mellom 6 og 15""@no, ""Scope of length overall between 6 and 15""@en;
  sh:path sdir:vesselLengthOverall;
  sh:minInclusive ""6"";
  sh:maxExclusive ""15"";
  sh:datatype unit:M .

scope:PS_BuiltDate_a19880102 a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde byggedato etter 1988-01-02""@no, ""Scope of built date after 1988-01-02""@en;
  sh:path sdir:builtDate;
  sh:minInclusive ""1988-01-02"";
  sh:datatype xsd:date .

scope:PS_BuiltDate_b19880102 a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde byggedato før 1988-01-02""@no, ""Scope of built date before 1988-01-02""@en;
  sh:path sdir:builtDate;
  sh:maxExclusive ""1988-01-02"";
  sh:datatype xsd:date .

scope:PS_BuiltDate_a19920101 a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde byggedato etter 1992-01-01""@no, ""Scope of built date after 1992-01-01""@en;
  sh:path sdir:builtDate;
  sh:minInclusive ""1992-01-01"";
  sh:datatype xsd:date .

scope:PS_BuiltDate_b19920101 a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde byggedato før 1992-01-01""@no, ""Scope of built date before 1992-01-01""@en;
  sh:path sdir:builtDate;
  sh:maxExclusive ""1992-01-01"";
  sh:datatype xsd:date .

scope:PS_BuiltDate_19920101_to_20020101 a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde byggedato mellom 1992-01-01 og 2002-01-01""@no, ""Scope of built date between 1992-01-01 and 2002-01-01""@en;
  sh:path sdir:builtDate;
  sh:minInclusive ""1992-01-01"";
  sh:maxExclusive ""2002-01-01"";
  sh:datatype xsd:date .

scope:PS_BuiltDate_a20020101 a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde byggedato etter 2002-01-01""@no, ""Scope of built date after 2002-01-01""@en;
  sh:path sdir:builtDate;
  sh:minInclusive ""2002-01-01"";
  sh:datatype xsd:date .

scope:PS_ElIn_max50 a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde elektrisk innstallasjon inntil 50""@no, ""Scope of electrical installation up to 50""@en;
  sh:path sdir:electricalInstallation;
  sh:maxExclusive ""50"";
  sh:datatype unit:V .

scope:PS_ElIn_min50 a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde elektrisk innstallasjon over 50""@no, ""Scope of electrical installation more than 50""@en;
  sh:path sdir:electricalInstallation;
  sh:minInclusive ""50"";
  sh:datatype unit:V .

scope:PS_Pass_max12 a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde passasjerer mindre enn 12""@no, ""Scope of passengers less than 12""@en;
  sh:path sdir:passengers;
  sh:maxExclusive ""12"";
  sh:datatype xsd:integer .

scope:PS_Pass_max100 a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde passasjerer mindre enn 100""@no, ""Scope of passengers less than 100""@en;
  sh:path sdir:passengers;
  sh:maxExclusive ""100"";
  sh:datatype xsd:integer .

scope:PS_GT_max500 a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde bruttotonnasje under 500""@no, ""Scope of gross tonnage less than 500""@en;
  sh:path sdir:grossTonnage;
  sh:maxExclusive ""500"";
  sh:datatype unit:GT .

scope:PS_Flash_min43 a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde flammepunkt mer enn 43""@no, ""Scope of flashpoint more than 43""@en;
  sh:path sdir:flashpoint;
  sh:minInclusive ""43"";
  sh:datatype unit:C .

scope:PS_CargoShip a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde fartøytype lasteskip""@no, ""Scope of vessel type cargo ship""@en;
  sh:path sdir:vesselType;
  sh:hasValue vesseltype:CargoShip .

scope:PS_FishingVessel a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde fartøytype fiskefartøy""@no, ""Scope of vessel type fishing vessel""@en;
  sh:path sdir:vesselType;
  sh:hasValue vesseltype:FishingVessel .

scope:PS_PassengerShip a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde fartøytype passasjerskip""@no, ""Scope of vessel type passenger ship""@en;
  sh:path sdir:vesselType;
  sh:hasValue vesseltype:PassengerShip .

scope:PS_RoRoPassengerShip a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde fartøytype roro-passasjerskip""@no, ""Scope of vessel type ro-ro passenger ship""@en;
  sh:path sdir:vesselType;
  sh:hasValue vesseltype:RoRoPassengerShip .

scope:PS_BankFishing a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde fartøytype bankfiske""@no, ""Scope of vessel type bank fishing""@en;
  sh:path sdir:tradeArea;
  sh:hasValue tradearea:BankFishing .

scope:PS_BankFishingI a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde fartøytype bankfiske I""@no, ""Scope of vessel type bank fishing I""@en;
  sh:path sdir:tradeArea;
  sh:hasValue tradearea:BankFishingI .

scope:PS_BankFishingII a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde fartøytype bankfiske II""@no, ""Scope of vessel type bank fishing II""@en;
  sh:path sdir:tradeArea;
  sh:hasValue tradearea:BankFishingII .

scope:PS_DeepseaFishing a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde fartøytype havfiske""@no, ""Scope of vessel type deepsea fishing""@en;
  sh:path sdir:tradeArea;
  sh:hasValue tradearea:DeepseaFishing .

scope:PS_DeepseaFishingI a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde fartøytype havfiske I""@no, ""Scope of vessel type deepsea fishing I""@en;
  sh:path sdir:tradeArea;
  sh:hasValue tradearea:DeepseaFishingI .

scope:PS_DeepseaFishingII a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde fartøytype havfiske II""@no, ""Scope of vessel type deepsea fishing II""@en;
  sh:path sdir:tradeArea;
  sh:hasValue tradearea:DeepseaFishingII .

scope:PS_FjordFishing a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde fartøytype fjordfiske""@no, ""Scope of vessel type fjord fishing""@en;
  sh:path sdir:tradeArea;
  sh:hasValue tradearea:FjordFishing .

scope:PS_IceCoveredWatersI a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde fartøytype isfarvann I""@no, ""Scope of vessel type ice-covered waters I""@en;
  sh:path sdir:tradeArea;
  sh:hasValue tradearea:IceCoveredWatersI .

scope:PS_IceCoveredWatersII a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde fartøytype isfarvann II""@no, ""Scope of vessel type ice-covered waters II""@en;
  sh:path sdir:tradeArea;
  sh:hasValue tradearea:IceCoveredWatersII .

scope:PS_InShoreFishing a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde fartøytype kystfiske""@no, ""Scope of vessel type in-shore fishing""@en;
  sh:path sdir:tradeArea;
  sh:hasValue tradearea:InShoreFishing .

scope:PS_TradeArea1 a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde fartøytype fartsområde 1""@no, ""Scope of vessel type trade area 1""@en;
  sh:path sdir:tradeArea;
  sh:hasValue tradearea:TradeArea1 .

scope:PS_TradeArea2 a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde fartøytype fartsområde 2""@no, ""Scope of vessel type trade area 2""@en;
  sh:path sdir:tradeArea;
  sh:hasValue tradearea:TradeArea2 .

scope:PS_TradeArea3 a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde fartøytype fartsområde 3""@no, ""Scope of vessel type trade area 3""@en;
  sh:path sdir:tradeArea;
  sh:hasValue tradearea:TradeArea3 .

scope:PS_TradeArea4 a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde fartøytype fartsområde 4""@no, ""Scope of vessel type trade area 4""@en;
  sh:path sdir:tradeArea;
  sh:hasValue tradearea:TradeArea4 .

scope:PS_TradeArea5 a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde fartøytype fartsområde 5""@no, ""Scope of vessel type trade area 5""@en;
  sh:path sdir:tradeArea;
  sh:hasValue tradearea:TradeArea5 .

scope:PS_EuropeanTrade a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde fartøytype europeisk fart""@no, ""Scope of vessel type european trade""@en;
  sh:path sdir:tradeArea;
  sh:hasValue tradearea:EuropeanTrade .

scope:PS_GreatCoasting a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde fartøytype stor kystfart""@no, ""Scope of vessel type great coasting""@en;
  sh:path sdir:tradeArea;
  sh:hasValue tradearea:GreatCoasting .

scope:PS_InternationalVoyage a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde fartøytype internasjonal reise""@no, ""Scope of vessel type international voyage""@en;
  sh:path sdir:tradeArea;
  sh:hasValue tradearea:InternationalVoyage .

scope:PS_NoCertifiedArea a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde fartøytype intent sertifisert""@no, ""Scope of vessel type intent sertifisert""@en;
  sh:path sdir:tradeArea;
  sh:hasValue tradearea:NoCertifiedArea .

scope:PS_ a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde fartøytype fartsområde""@no, ""Scope of vessel type fartsområde""@en;
  sh:path sdir:tradeArea;
  sh:hasValue <https://www.sdir.no/SDIR_Simulator/tradeArea#> .

scope:PS_NorthSeaAndBalticTrade a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde fartøytype nord- og østersjøfart""@no, ""Scope of vessel type North Sea and Baltic trade""@en;
  sh:path sdir:tradeArea;
  sh:hasValue tradearea:NorthSeaAndBalticTrade .

scope:PS_OverseasVoyage a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde fartøytype oversjøiskfart""@no, ""Scope of vessel type overseas voyage""@en;
  sh:path sdir:tradeArea;
  sh:hasValue tradearea:OverseasVoyage .

scope:PS_ShortInternationalVoyage a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde fartøytype kort internasjonal reise""@no, ""Scope of vessel type short international voyage""@en;
  sh:path sdir:tradeArea;
  sh:hasValue tradearea:ShortInternationalVoyage .

scope:PS_TradeOnLakesAndRivers a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde fartøytype fart på innsjøer og elver""@no, ""Scope of vessel type trade on lakes and rivers""@en;
  sh:path sdir:tradeArea;
  sh:hasValue tradearea:TradeOnLakesAndRivers .

scope:PS_UnrestrictedVoyages a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde fartøytype uinnskrenket fart""@no, ""Scope of vessel type unrestricted voyages""@en;
  sh:path sdir:tradeArea;
  sh:hasValue tradearea:UnrestrictedVoyages .

scope:PS_Cargo a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde last""@no, ""Scope of cargo""@en;
  sh:path sdir:cargo;
  sh:defaultValue true .

scope:PS_A3 a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde radiodekningsområde A3""@no, ""Scope of radio coverage area A3""@en;
  sh:path sdir:radioArea;
  sh:hasValue sdir:A3 .

scope:PS_A4 a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde radiodekningsområde A4""@no, ""Scope of radio coverage area A4""@en;
  sh:path sdir:radioArea;
  sh:hasValue sdir:A4 .

scope:PS_A1 a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde radiodekningsområde A1""@no, ""Scope of radio coverage area A1""@en;
  sh:path sdir:radioArea;
  sh:hasValue sdir:A1 .

scope:PS_A2 a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde radiodekningsområde A2""@no, ""Scope of radio coverage area A2""@en;
  sh:path sdir:radioArea;
  sh:hasValue sdir:A2 .

scope:PS_Converted a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde ombygget fartøy""@no, ""Scope of converted vessel""@en;
  sh:path sdir:converted;
  sh:defaultValue true .

scope:PS_Protected a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde fredet fartøy""@no, ""Scope of protected vessel""@en;
  sh:path sdir:protected;
  sh:defaultValue true .

scope:PS_LoadingAndUnloadingInstallation a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde laste- og losseinnretninger""@no, ""Scope of loading and unloading installations""@en;
  sh:path sdir:loadingUnloadingInstallation;
  sh:defaultValue true .

scope:PS_PropulsionPower_min750 a sh:PropertyShape, scope:Scope;
  sh:description ""Virkeområde framdriftskraft eller mer 750""@no, ""Scope of propulsion power more than 750""@en;
  sh:path sdir:propulsionPower;
  sh:minInclusive ""750"";
  sh:datatype unit:KiloW .";
        
	}
}
