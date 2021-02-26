package scope;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.vocabulary.SHACL;
import vocabulary.Vocabulary;

public class VesselType {
    Requirement requirement;
    String value;

    IRI constraint = SHACL.HAS_VALUE;
    String subject = "PS_";

    IRI vesselType = Vocabulary.vf.createIRI(Vocabulary.NS_VESSEL);

    public VesselType(Requirement r, String v) {
        this.requirement = r;
        this.value = v;

        setVesselType(v);
    }

    public Requirement getRequirement() {
        return requirement;
    }

    public String getValue() {
        return value;
    }

    public IRI getConstraint() {
        return constraint;
    }

    public String getSubject() {
        return subject + this.vesselType.getLocalName();
    }

    public IRI getVesselType() {
        return vesselType;
    }

    private void setVesselType(String v) {

        switch (v.toLowerCase()) {
            case "lasteskip":
                this.vesselType = Vocabulary.CargoShip; break;
            case "lekter":
                this.vesselType = Vocabulary.Barge; break;
            case "beredskapsfartøy":
                this.vesselType = Vocabulary.StandbyVessel; break;
            case "tankskip":
                this.vesselType = Vocabulary.TankerVessel; break;
            case "olje-kjemikalie tankerskip":
                this.vesselType = Vocabulary.OilChemicalTanker; break;
            case "gasstankskip":
                this.vesselType = Vocabulary.GasTanker; break;
            case "kjemikalietankskip":
                this.vesselType = Vocabulary.ChemicalTanker; break;
            case "hurtiggående lasteskip":
                this.vesselType = Vocabulary.CargoHighSpeed; break;
            case "bulkskip":
                this.vesselType = Vocabulary.BulkCarrier; break;
            case "passasjerskip":
                this.vesselType = Vocabulary.PassengerShip; break;
            case "hurtiggående passasjerskip":
                this.vesselType = Vocabulary.HighSpeedPassengerShip; break;
            case "roro-passasjerskip":
                this.vesselType = Vocabulary.RoRoPassengerShip; break;
            case "fiskefartøy":
            case "fangst- og fiskefartøy":
                this.vesselType = Vocabulary.FishingVessel; break;
            case "flyttbare innretninger":
                this.vesselType = Vocabulary.MobileOffshoreUnit; break;
            case "fritidsfartøy":
                this.vesselType = Vocabulary.LeisureBoat; break;
        }
    }

    public String vesselTypeEn(String v) {

        String vesselType = "";

        switch (v.toLowerCase()) {
            case "lasteskip":
                vesselType = "cargo ship"; break;
            case "lekter":
                vesselType = "barge"; break;
            case "beredskapsfartøy":
                vesselType = "standby vessel"; break;
            case "tankskip":
                vesselType = "oil tanker"; break;
            case "olje-kjemikalie tankerskip":
                vesselType = "oil-chemical tanker"; break;
            case "gasstankskip":
                vesselType = "gas tanker"; break;
            case "kjemikalietankskip":
                vesselType = "chemical tanker"; break;
            case "hurtiggående lasteskip":
                vesselType = "cargo high-speed craft"; break;
            case "bulkskip":
                vesselType = "bulk carrier"; break;
            case "passasjerskip":
                vesselType = "passenger ship"; break;
            case "hurtiggående passasjerskip":
                vesselType = "passenger high-speed craft"; break;
            case "roro-passasjerskip":
                vesselType = "ro-ro passenger ship"; break;
            case "fiskefartøy":
            case "fangst- og fiskefartøy":
                vesselType = "fishing vessel"; break;
            case "flyttbare innretninger":
                vesselType = "mobile offshore unit"; break;
            case "fritidsfartøy":
                vesselType = "leisure boat"; break;
        }
        return vesselType;
    }
}
