package rdftransformer.api.transformer.scope;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.vocabulary.SHACL;
import rdftransformer.api.transformer.utils.Vocabulary;


public class TradeArea {
    Requirement requirement;
    String value, subject;

    IRI tradeArea = Vocabulary.vf.createIRI(Vocabulary.NS_TRADE);

    IRI constraint = SHACL.HAS_VALUE;

    public TradeArea(Requirement r, String v) {
            this.requirement = r;
            this.value = v;

            setTradeArea(v);
            setSubject();

    }

    public IRI getConstraint() {
        return constraint;
    }

    public String getValue() {
        return value;
    }

    public Requirement getRequirement() {
        return requirement;
    }

    public String getSubject() {
        return subject;
    }

    public IRI getTradeArea() {
        return tradeArea;
    }

    public String valueEn(String v) {

        switch (v) {
            case "bankfiske":       v = "bank fishing"; break;
            case "bankfiske I":     v = "bank fishing I"; break;
            case "bankfiske II":    v = "bank fishing II"; break;
            case "havfiske":        v = "deepsea fishing"; break;
            case "havfiske I":      v = "deepsea fishing I"; break;
            case "havfiske II":     v = "deepsea fishing II"; break;
            case "fjordfiske":      v = "fjord fishing"; break;
            case "isfarvann I":     v = "ice-covered waters I"; break;
            case "isfarvann II":    v = "ice-covered waters II"; break;
            case "kystfiske":       v = "in-shore fishing"; break;
            case "fartsområde 1":   v = "trade area 1"; break;
            case "fartsområde 2":   v = "trade area 2"; break;
            case "fartsområde 3":   v = "trade area 3"; break;
            case "fartsområde 4":   v = "trade area 4"; break;
            case "fartsområde 5":   v = "trade area 5"; break;
            case "europeisk fart":  v = "european trade"; break;
            case "stor kystfart":   v = "great coasting"; break;
            case "internasjonal reise": v = "international voyage"; break;
            case "intent sertifisert fartsområde": v = "no certified area"; break;
            case "nord- og østersjøfart": v = "North Sea and Baltic trade"; break;
            case "oversjøiskfart":  v = "overseas voyage"; break;
            case "kort internasjonal reise": v = "short international voyage"; break;
            case "fart på innsjøer og elver": v = "trade on lakes and rivers"; break;
            case "uinnskrenket fart": v = "unrestricted voyages"; break;
        }
        return v;
    }

    public void setSubject() {
        this.subject = "PS_" + this.tradeArea.getLocalName();
    }

    public void setTradeArea(String v) {
        switch (v) {
            case "bankfiske":       this.tradeArea = Vocabulary.BankFishing; break;
            case "bankfiske I":     this.tradeArea = Vocabulary.BankFishingI; break;
            case "bankfiske II":    this.tradeArea = Vocabulary.BankFishingII; break;
            case "havfiske":        this.tradeArea = Vocabulary.DeepseaFishing; break;
            case "havfiske I":      this.tradeArea = Vocabulary.DeepseaFishingI; break;
            case "havfiske II":     this.tradeArea = Vocabulary.DeepseaFishingII; break;
            case "fjordfiske":      this.tradeArea = Vocabulary.FjordFishing; break;
            case "isfarvann I":     this.tradeArea = Vocabulary.IceCoveredWatersI; break;
            case "isfarvann II":    this.tradeArea = Vocabulary.IceCoveredWatersII; break;
            case "kystfiske":       this.tradeArea = Vocabulary.InShoreFishing; break;
            case "fartsområde 1":   this.tradeArea = Vocabulary.TradeArea1; break;
            case "fartsområde 2":   this.tradeArea = Vocabulary.TradeArea2; break;
            case "fartsområde 3":   this.tradeArea = Vocabulary.TradeArea3; break;
            case "fartsområde 4":   this.tradeArea = Vocabulary.TradeArea4; break;
            case "fartsområde 5":   this.tradeArea = Vocabulary.TradeArea5; break;
            case "europeisk fart":  this.tradeArea = Vocabulary.EuropeanTrade; break;
            case "stor kystfart":   this.tradeArea = Vocabulary.GreatCoasting; break;
            case "internasjonal reise": this.tradeArea = Vocabulary.InternationalVoyage; break;
            case "intent sertifisert fartsområde":
            case "intent sertifisert": this.tradeArea = Vocabulary.NoCertifiedArea; break;
            case "nord- og østersjøfart": this.tradeArea = Vocabulary.NorthSeaAndBalticTrade; break;
            case "oversjøiskfart":  this.tradeArea = Vocabulary.OverseasVoyage; break;
            case "kort internasjonal reise": this.tradeArea = Vocabulary.ShortInternationalVoyage; break;
            case "fart på innsjøer og elver": this.tradeArea = Vocabulary.TradeOnLakesAndRivers; break;
            case "uinnskrenket fart": this.tradeArea = Vocabulary.UnrestrictedVoyages; break;
        }
    }
}
