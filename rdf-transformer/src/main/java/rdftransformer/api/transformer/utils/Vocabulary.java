package rdftransformer.api.transformer.utils;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public class Vocabulary {

    public static ValueFactory vf = SimpleValueFactory.getInstance();
    public static final String NS = "https://www.sdir.no/SDIR_Simulator#";
    public static final String NS_VESSEL = "https://www.sdir.no/SDIR_Simulator/vesselType#";
    public static final String NS_TRADE = "https://www.sdir.no/SDIR_Simulator/tradeArea#";
    public static final String NS_SCOPE = "https://www.sdir.no/SDIR_Simulator/shapes/scope#";

    public static final IRI Scope = vf.createIRI(NS_SCOPE + "Scope");
    public static final IRI Requirement = vf.createIRI(NS + "Requirement");
    public static final IRI builtDate = vf.createIRI(NS + "builtDate");
    public static final IRI regulationReference = vf.createIRI(NS + "regulationReference");
    public static final IRI eliReference = vf.createIRI(NS + "eliReference");
    public static final IRI theme = vf.createIRI(NS + "theme");
    public static final IRI grossTonnage = vf.createIRI(NS + "grossTonnage");
    public static final IRI passengers = vf.createIRI(NS + "passengers");
    public static final IRI vesselLengthOverall = vf.createIRI(NS + "vesselLengthOverall");
    public static final IRI electricalInstallation = vf.createIRI(NS + "electricalInstallation");
    public static final IRI flashpoint = vf.createIRI(NS + "flashpoint");
    public static final IRI vesselType = vf.createIRI(NS + "vesselType");
    public static final IRI tradeArea = vf.createIRI(NS + "tradeArea");
    public static final IRI cargo = vf.createIRI(NS + "cargo");
    public static final IRI radioArea = vf.createIRI(NS + "radioArea");
    public static final IRI converted = vf.createIRI(NS + "converted");
    public static final IRI _protected = vf.createIRI(NS + "protected");
    public static final IRI loadingUnloadingInstallation = vf.createIRI(NS + "loadingUnloadingInstallation");
    public static final IRI propulsionPower = vf.createIRI(NS + "propulsionPower");

    public static final IRI MobileOffshoreUnit = vf.createIRI(NS_VESSEL + "MobileOffshoreUnit");
    public static final IRI CargoShip = vf.createIRI(NS_VESSEL + "CargoShip");
    public static final IRI Barge = vf.createIRI(NS_VESSEL + "Barge");
    public static final IRI BulkCarrier = vf.createIRI(NS_VESSEL + "BulkCarrier");
    public static final IRI CargoHighSpeed = vf.createIRI(NS_VESSEL + "CargoHighSpeed");
    public static final IRI StandbyVessel = vf.createIRI(NS_VESSEL + "StandbyVessel");
    public static final IRI TankerVessel = vf.createIRI(NS_VESSEL + "TankerVessel");
    public static final IRI ChemicalTanker = vf.createIRI(NS_VESSEL + "ChemicalTanker");
    public static final IRI GasTanker = vf.createIRI(NS_VESSEL + "GasTanker");
    public static final IRI OilTanker = vf.createIRI(NS_VESSEL + "OilTanker");
    public static final IRI OilChemicalTanker = vf.createIRI(NS_VESSEL + "OilChemicalTanker");
    public static final IRI FishingVessel = vf.createIRI(NS_VESSEL + "FishingVessel");
    public static final IRI LeisureBoat = vf.createIRI(NS_VESSEL + "LeisureBoat");
    public static final IRI PassengerShip = vf.createIRI(NS_VESSEL + "PassengerShip");
    public static final IRI HighSpeedPassengerShip = vf.createIRI(NS_VESSEL + "HighSpeedPassengerShip");
    public static final IRI RoRoPassengerShip = vf.createIRI(NS_VESSEL + "RoRoPassengerShip");

    public static final IRI BankFishing = vf.createIRI(NS_TRADE + "BankFishing");
    public static final IRI BankFishingI = vf.createIRI(NS_TRADE + "BankFishingI");
    public static final IRI BankFishingII = vf.createIRI(NS_TRADE + "BankFishingII");
    public static final IRI DeepseaFishing = vf.createIRI(NS_TRADE + "DeepseaFishing");
    public static final IRI DeepseaFishingI = vf.createIRI(NS_TRADE + "DeepseaFishingI");
    public static final IRI DeepseaFishingII = vf.createIRI(NS_TRADE + "DeepseaFishingII");
    public static final IRI FjordFishing = vf.createIRI(NS_TRADE + "FjordFishing");
    public static final IRI IceCoveredWatersI = vf.createIRI(NS_TRADE + "IceCoveredWatersI");
    public static final IRI IceCoveredWatersII = vf.createIRI(NS_TRADE + "IceCoveredWatersII");
    public static final IRI InShoreFishing = vf.createIRI(NS_TRADE + "InShoreFishing");
    public static final IRI TradeArea1 = vf.createIRI(NS_TRADE + "TradeArea1");
    public static final IRI TradeArea2 = vf.createIRI(NS_TRADE + "TradeArea2");
    public static final IRI TradeArea3 = vf.createIRI(NS_TRADE + "TradeArea3");
    public static final IRI TradeArea4 = vf.createIRI(NS_TRADE + "TradeArea4");
    public static final IRI TradeArea5 = vf.createIRI(NS_TRADE + "TradeArea5");
    public static final IRI EuropeanTrade = vf.createIRI(NS_TRADE + "EuropeanTrade");
    public static final IRI GreatCoasting = vf.createIRI(NS_TRADE + "GreatCoasting");
    public static final IRI InternationalVoyage = vf.createIRI(NS_TRADE + "InternationalVoyage");
    public static final IRI NoCertifiedArea = vf.createIRI(NS_TRADE + "NoCertifiedArea");
    public static final IRI NorthSeaAndBalticTrade = vf.createIRI(NS_TRADE + "NorthSeaAndBalticTrade");
    public static final IRI OverseasVoyage = vf.createIRI(NS_TRADE + "OverseasVoyage");
    public static final IRI ShortInternationalVoyage = vf.createIRI(NS_TRADE + "ShortInternationalVoyage");
    public static final IRI TradeOnLakesAndRivers = vf.createIRI(NS_TRADE + "TradeOnLakesAndRivers");
    public static final IRI UnrestrictedVoyages = vf.createIRI(NS_TRADE + "UnrestrictedVoyages");

    public static final IRI A1 = vf.createIRI(NS + "A1");
    public static final IRI A2 = vf.createIRI(NS + "A2");
    public static final IRI A3 = vf.createIRI(NS + "A3");
    public static final IRI A4 = vf.createIRI(NS + "A4");

}
