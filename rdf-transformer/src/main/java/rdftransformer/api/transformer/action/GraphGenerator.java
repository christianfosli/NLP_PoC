package rdftransformer.api.transformer.action;

import com.google.gson.JsonArray;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.model.vocabulary.SHACL;
import org.eclipse.rdf4j.model.vocabulary.XSD;
import rdftransformer.api.transformer.scope.*;
import rdftransformer.api.transformer.utils.Utils;
import rdftransformer.api.transformer.utils.Vocabulary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GraphGenerator {

    public static Model scopeModel = Utils.initModel();
    public static Model requirementModel = Utils.initModel();

    public static List<String> ottrInstances = new ArrayList<>();

    /** vesselLengthScope
     *
     * Build scope for vessel length, appending results to global scopeModel.
     *
     * @throws IOException if input file not read
     */
    public static void vesselLengthScope(JsonArray array) throws IOException {

        List<LOA> loaList = JSONHandler.vesselLengthOverall(array);
        Model model = new LinkedHashModel();

        for (LOA l : loaList) {

            IRI subject = Vocabulary.vf.createIRI(Vocabulary.NS_SCOPE + l.getSubject());
            if (!l.getConstraint().equals(SHACL.CONSTRAINT_COMPONENT)) {
                createValueScope(model, l.getValue1(), subject, Vocabulary.vesselLengthOverall,
                        l.getConstraint(),
                        "Virkeområde største lengde " + l.getValue1(),
                        "Scope of length overall " + l.getValue1(),
                        Vocabulary.vf.createIRI(Utils.NS_UNIT + "M")
                );
            } else {
                createValueRangeScope(model, l.getValue1(), l.getValue2(), subject, Vocabulary.vesselLengthOverall,
                        "Virkeområde største lengde mellom " + l.getValue1() + " og " + l.getValue2(),
                        "Scope of length overall between " + l.getValue1() + " and " + l.getValue2(),
                        Vocabulary.vf.createIRI(Utils.NS_UNIT + "M")
                );
            }
            addRequirement(l.getRequirement(), subject);
            ottrInstances.add(l.getOTTRInstance());
        }
        scopeModel.addAll(model);
    }

    /** builtDateScope
     *
     * Build scope for built date, appending results to global scopeModel.
     *
     * @throws IOException if input file not read
     */
    public static void builtDateScope(JsonArray array) throws IOException {

        List<BuiltDate> bdList = JSONHandler.builtDate(array);
        Model model = new LinkedHashModel();

        for (BuiltDate bd : bdList) {

            IRI subject = Vocabulary.vf.createIRI(Vocabulary.NS_SCOPE + bd.getSubject());

            if (!bd.getConstraint().equals(SHACL.CONSTRAINT_COMPONENT)) {
                createValueScope(model, bd.getValue1(), subject, Vocabulary.builtDate,
                        bd.getConstraint(),
                        "Virkeområde byggedato " + bd.datePrefixNo() + " " + bd.getValue1(),
                        "Scope of built date " + bd.datePrefixEn() + " " + bd.getValue1(),
                        XSD.DATE
                        );
            } else {
                createValueRangeScope(model, bd.getValue1(), bd.getValue2(), subject, Vocabulary.builtDate,
                        "Virkeområde byggedato " + bd.datePrefixNo() + " " + bd.getValue1() + " og " + bd.getValue2(),
                        "Scope of built date " + bd.datePrefixEn() + " " + bd.getValue1() + " and " + bd.getValue2(),
                        XSD.DATE
                );
            }
            addRequirement(bd.getRequirement(), subject);
            ottrInstances.add(bd.getOTTRInstance());
        }
        scopeModel.addAll(model);
    }

    /** machinePowerScope
     *
     * Build scope for machine power, appending results to global scopeModel.
     *
     * @throws IOException if input file not read
     */
    public static void electricalInstallationScope(JsonArray array) throws IOException {

        List<ElectricalInstallation> elList = JSONHandler.electricalInstallations(array);
        Model model = new LinkedHashModel();

        for (ElectricalInstallation el : elList) {

            IRI subject = Vocabulary.vf.createIRI(Vocabulary.NS_SCOPE + el.getSubject());

            createValueScope(model, el.getValue(), subject, Vocabulary.electricalInstallation,
                    el.getConstraint(),
                    "Virkeområde elektrisk innstallasjon " + el.getElPrefixNo() + " " + el.getValue(),
                    "Scope of electrical installation " + el.getElPrefixEn() + " " + el.getValue(),
                    Vocabulary.vf.createIRI(Utils.NS_UNIT + "V")
                    );

            addRequirement(el.getRequirement(), subject);
            ottrInstances.add(el.getOTTRInstance());
        }

        scopeModel.addAll(model);
    }

    public static void passengerScope(JsonArray array) throws IOException {
        List<Passengers> passList = JSONHandler.passengers(array);
        Model model = new LinkedHashModel();

        for (Passengers p : passList) {
            IRI subject = Vocabulary.vf.createIRI(Vocabulary.NS_SCOPE + p.getSubject());

            createValueScope(model, p.getValue(), subject, Vocabulary.passengers,
                    p.getConstraint(),
                    "Virkeområde passasjerer " + p.getPrefixNo() + " " + p.getValue(),
                    "Scope of passengers " + p.getPrefixEn() + " " + p.getValue(),
                    XSD.INTEGER
                    );

            addRequirement(p.getRequirement(), subject);
            ottrInstances.add(p.getOTTRInstance());
        }
        scopeModel.addAll(model);
    }

    public static void grossTonnageScope(JsonArray array) throws IOException {
        List<GrossTonnage> grossTonnages = JSONHandler.grossTonnage(array);
        Model model = new LinkedHashModel();

        for (GrossTonnage gt : grossTonnages) {
            IRI subject = Vocabulary.vf.createIRI(Vocabulary.NS_SCOPE + gt.getSubject());

            createValueScope(model, gt.getValue(), subject, Vocabulary.grossTonnage,
                    gt.getConstraint(),
                    "Virkeområde bruttotonnasje " + gt.getPrefixNo() + " " + gt.getValue(),
                    "Scope of gross tonnage " + gt.getPrefixEn() + " " + gt.getValue(),
                    Vocabulary.vf.createIRI(Utils.NS_UNIT + "GT")
            );

            addRequirement(gt.getRequirement(), subject);
            ottrInstances.add(gt.getOTTRInstance());
        }
        scopeModel.addAll(model);
    }

    public static void flashpointScope(JsonArray array) throws IOException {

        List<Flashpoint> fpList = JSONHandler.flashpoint(array);
        Model model = new LinkedHashModel();

        for (Flashpoint fp : fpList) {

            IRI subject = Vocabulary.vf.createIRI(Vocabulary.NS_SCOPE + fp.getSubject());

            createValueScope(model, fp.getValue(), subject, Vocabulary.flashpoint,
                    fp.getConstraint(),
                    "Virkeområde flammepunkt " + fp.getPrefixNo() + " " + fp.getValue(),
                    "Scope of flashpoint " + fp.getPrefixEn() + " " + fp.getValue(),
                    Vocabulary.vf.createIRI(Utils.NS_UNIT + "C")
            );

            addRequirement(fp.getRequirement(), subject);
            ottrInstances.add(fp.getOTTRInstance());
        }

        scopeModel.addAll(model);
    }

    public static void vesselTypeScope(JsonArray array) throws IOException {

        List<VesselType> vtList = JSONHandler.vesselType(array);
        Model model = new LinkedHashModel();

        for (VesselType vt : vtList) {

            IRI subject = Vocabulary.vf.createIRI(Vocabulary.NS_SCOPE + vt.getSubject());

            createObjectScope(model, vt.getVesselType(), subject, Vocabulary.vesselType,
                    "Virkeområde fartøytype " + vt.getValue(),
                    "Scope of vessel type " + vt.vesselTypeEn(vt.getValue())
            );

            addRequirement(vt.getRequirement(), subject);
        }

        scopeModel.addAll(model);
    }

    public static void tradeAreaScope(JsonArray array) throws IOException {

        List<TradeArea> taList = JSONHandler.tradeArea(array);
        Model model = new LinkedHashModel();

        for (TradeArea ta : taList) {

            IRI subject = Vocabulary.vf.createIRI(Vocabulary.NS_SCOPE + ta.getSubject());

            createObjectScope(model, ta.getTradeArea(), subject, Vocabulary.tradeArea,
                    "Virkeområde fartøytype " + ta.getValue(),
                    "Scope of vessel type " + ta.valueEn(ta.getValue())
            );

            addRequirement(ta.getRequirement(), subject);
        }

        scopeModel.addAll(model);
    }

    public static void cargoScope(JsonArray array) throws IOException {
        List<Cargo> cargoList = JSONHandler.cargo(array);
        Model model = new LinkedHashModel();

        for (Cargo c : cargoList) {

            IRI subject = Vocabulary.vf.createIRI(Vocabulary.NS_SCOPE + c.getSubject());

            createDefaultValueScope(model, subject, Vocabulary.cargo,
                    "Virkeområde " + c.contextNo(),
                    "Scope of " + c.contextEn()
            );

            addRequirement(c.getRequirement(), subject);
        }

        scopeModel.addAll(model);
    }

    public static void radioAreaScope(JsonArray array) throws IOException {
        List<RadioArea> cargoList = JSONHandler.radioArea(array);
        Model model = new LinkedHashModel();

        for (RadioArea ra : cargoList) {

            IRI subject = Vocabulary.vf.createIRI(Vocabulary.NS_SCOPE + ra.getSubject());

            createObjectScope(model, ra.getValue(), subject, Vocabulary.radioArea,
                    "Virkeområde radiodekningsområde " + ra.getValue().getLocalName(),
                    "Scope of radio coverage area " + ra.getValue().getLocalName()
            );

            addRequirement(ra.getRequirement(), subject);
        }

        scopeModel.addAll(model);
    }

    public static void convertedScope(JsonArray array) throws IOException {
        List<Converted> cList = JSONHandler.converted(array);
        Model model = new LinkedHashModel();

        for (Converted c : cList) {

            IRI subject = Vocabulary.vf.createIRI(Vocabulary.NS_SCOPE + c.getSubject());

            createDefaultValueScope(model, subject, Vocabulary.converted,
                    "Virkeområde " + c.contextNo() + " fartøy",
                    "Scope of " + c.contextEn() + " vessel"
            );

            addRequirement(c.getRequirement(), subject);
        }

        scopeModel.addAll(model);
    }

    public static void protectedScope(JsonArray array) throws IOException {
        List<Protected> proList = JSONHandler._protected(array);
        Model model = new LinkedHashModel();

        for (Protected p : proList) {

            IRI subject = Vocabulary.vf.createIRI(Vocabulary.NS_SCOPE + p.getSubject());

            createDefaultValueScope(model, subject, Vocabulary._protected,
                    "Virkeområde " + p.contextNo() + " fartøy",
                    "Scope of " + p.contextEn() + " vessel"
            );

            addRequirement(p.getRequirement(), subject);
        }

        scopeModel.addAll(model);
    }

    public static void loadingScope(JsonArray array) throws IOException {
        List<Loading> loList = JSONHandler.loading(array);
        Model model = new LinkedHashModel();

        for (Loading l : loList) {

            IRI subject = Vocabulary.vf.createIRI(Vocabulary.NS_SCOPE + l.getSubject());

            createDefaultValueScope(model, subject, Vocabulary.loadingUnloadingInstallation,
                    "Virkeområde " + l.contextNo(),
                    "Scope of " + l.contextEn()
            );

            addRequirement(l.getRequirement(), subject);
        }

        scopeModel.addAll(model);
    }

    public static void propulsionPowerScope(JsonArray array) throws IOException {
        List<PropulsionPower> proList = JSONHandler.propulsionPower(array);
        Model model = new LinkedHashModel();

        for (PropulsionPower pp : proList) {

            IRI subject = Vocabulary.vf.createIRI(Vocabulary.NS_SCOPE + pp.getSubject());

            createValueScope(model, pp.getValue1(), subject, Vocabulary.propulsionPower, pp.getConstraint(),
                    "Virkeområde framdriftskraft " + pp.getContext() + " " + pp.getValue1() ,
                    "Scope of propulsion power " + pp.contextEn() + " " + pp.getValue1(),
                    Vocabulary.vf.createIRI(Utils.NS_UNIT + "KiloW")

            );

            addRequirement(pp.getRequirement(), subject);
        }

        scopeModel.addAll(model);
    }

    private static void createDefaultValueScope(Model model, IRI subject, IRI path, String desNo, String desEn) {
        model.add(subject, RDF.TYPE, SHACL.PROPERTY_SHAPE);
        model.add(subject, RDF.TYPE, Vocabulary.Scope);
        model.add(subject, SHACL.DESCRIPTION, Vocabulary.vf.createLiteral(desNo, "no"));
        model.add(subject, SHACL.DESCRIPTION, Vocabulary.vf.createLiteral(desEn, "en"));
        model.add(subject, SHACL.PATH, path);

        model.add(subject, SHACL.DEFAULT_VALUE, Vocabulary.vf.createLiteral(true));
    }

    private static void createObjectScope(Model model, IRI value, IRI subject, IRI path, String desNo, String desEn) {
        model.add(subject, RDF.TYPE, SHACL.PROPERTY_SHAPE);
        model.add(subject, RDF.TYPE, Vocabulary.Scope);
        model.add(subject, SHACL.DESCRIPTION, Vocabulary.vf.createLiteral(desNo, "no"));
        model.add(subject, SHACL.DESCRIPTION, Vocabulary.vf.createLiteral(desEn, "en"));
        model.add(subject, SHACL.PATH, path);

        model.add(subject, SHACL.HAS_VALUE, value);
    }

    /** createValueScope
     *
     * Creates a value constraint component for given scope to append for the global scopeModel.
     *
     * @param model model to be appended
     * @param value value in question
     * @param subject subject property shape
     * @param valueConstraint SHACL value constraint
     * @param desNo description in Norwegian
     * @param desEn description in English
     * @param datatype datatype of value
     */
    private static void createValueScope(Model model, String value, IRI subject, IRI path, IRI valueConstraint, String desNo, String desEn, IRI datatype) {
        model.add(subject, RDF.TYPE, SHACL.PROPERTY_SHAPE);
        model.add(subject, RDF.TYPE, Vocabulary.Scope);
        model.add(subject, SHACL.DESCRIPTION, Vocabulary.vf.createLiteral(desNo, "no"));
        model.add(subject, SHACL.DESCRIPTION, Vocabulary.vf.createLiteral(desEn, "en"));
        model.add(subject, SHACL.PATH, path);

        model.add(subject, valueConstraint, Vocabulary.vf.createLiteral(value));

        model.add(subject, SHACL.DATATYPE, datatype);
    }

    /** createValueRangeScope
     *
     * Creates a value range constraint component for given scope to append for the global scopeModel.
     *
     * @param model model to be appended
     * @param min min value of range
     * @param max max value of range
     * @param subject subject property shape
     * @param desNo description in Norwegian
     * @param desEn description in English
     * @param datatype datatype of value range
     */
    private static void createValueRangeScope(Model model, String min, String max, IRI subject, IRI path, String desNo, String desEn, IRI datatype) {
        model.add(subject, RDF.TYPE, SHACL.PROPERTY_SHAPE);
        model.add(subject, RDF.TYPE, Vocabulary.Scope);
        model.add(subject, SHACL.DESCRIPTION, Vocabulary.vf.createLiteral(desNo, "no"));
        model.add(subject, SHACL.DESCRIPTION, Vocabulary.vf.createLiteral(desEn, "en"));
        model.add(subject, SHACL.PATH, path);

        model.add(subject, SHACL.MIN_INCLUSIVE, Vocabulary.vf.createLiteral(min));
        model.add(subject, SHACL.MAX_EXCLUSIVE, Vocabulary.vf.createLiteral(max));

        model.add(subject, SHACL.DATATYPE, datatype);
    }

    /** addRequirement
     *
     * Building the RDF graph of requirements, containing all needed information as defined by NMAs data model.
     * Requirements are split down to sub-parts of a regulation.
     *
     * @throws IOException if unable to read files in getSectionHeaders() and getChapterHeader()
     */
    public static void addRequirement(Requirement r, IRI scope) throws IOException {

        Model model = new LinkedHashModel();

        String localName = "REG" + r.getRegulation() + "S" + r.getParagraph() + "P" + r.getPart();

        IRI subject;
        if (r.getSubpart() != null) {
            subject = Vocabulary.vf.createIRI(Vocabulary.NS + localName + "SP" + r.getSubpart());
        } else {
            subject = Vocabulary.vf.createIRI(Vocabulary.NS + localName);
        }

        model.add(subject, RDF.TYPE, SHACL.NODE_SHAPE);
        model.add(subject, SHACL.PROPERTY, scope);
        model.add(subject, Vocabulary.regulationReference,
                Vocabulary.vf.createLiteral(r.getRegulation_id_lovdata()), XSD.ANYURI);
        model.add(subject, Vocabulary.eliReference,
                Vocabulary.vf.createLiteral(r.getRegulation_id_eli()), XSD.ANYURI);
        model.add(subject, RDFS.LABEL, Vocabulary.vf.createLiteral(r.getLabel(), "no"));
        model.add(subject, Vocabulary.theme, Vocabulary.vf.createLiteral(r.getTheme(), "no"));

        requirementModel.addAll(model);

    }
}
