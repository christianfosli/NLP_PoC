package vocabulary;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public class Vocabulary {

    public static ValueFactory vf = SimpleValueFactory.getInstance();
    public static final String NS = "https://www.sdir.no/SDIR_Simulator#";
    public static final String NS_VESSEL = "https://www.sdir.no/SDIR_Simulator/vesselType#";
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

}
