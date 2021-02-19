import com.google.gson.*;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.model.vocabulary.SHACL;
import org.eclipse.rdf4j.model.vocabulary.XSD;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import scope.BuiltDate;
import scope.ElectricalInstallation;
import scope.LOA;
import scope.Requirement;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class GraphGenerator {

    public static Model scopeModel = Utils.initModel();
    public static Model requirementModel = Utils.initModel();

    /** vesselLengthScope
     *
     * Build scope for vessel length, appending results to global scopeModel.
     *
     * @throws IOException if input file not read
     */
    public static void vesselLengthScope() throws IOException {

        List<LOA> loaList = JSONHandler.vesselLengthOverall();
        Model model = new LinkedHashModel();

        for (LOA l : loaList) {

            IRI subject = Vocabulary.vf.createIRI(Vocabulary.NS_SCOPE + l.getSubject());
            if (!l.getConstraint().equals(SHACL.CONSTRAINT_COMPONENT)) {
                createValueScope(model, l.getValue1(), subject,
                        l.getConstraint(),
                        "Virkeområde største lengde " + l.getValue1(),
                        "Scope of length overall " + l.getValue1(),
                        Vocabulary.vf.createIRI(Utils.NS_UNIT + "M")
                );
            } else {
                createValueRangeScope(model, l.getValue1(), l.getValue2(), subject,
                        "Virkeområde største lengde mellom " + l.getValue1() + " og " + l.getValue2(),
                        "Scope of length overall between " + l.getValue1() + " and " + l.getValue2(),
                        Vocabulary.vf.createIRI(Utils.NS_UNIT + "M")
                );
            }
            addRequirement(l.getRequirement(), subject);
        }
        scopeModel.addAll(model);
    }

    /** builtDateScope
     *
     * Build scope for built date, appending results to global scopeModel.
     *
     * @throws IOException if input file not read
     */
    public static void builtDateScope() throws IOException {

        List<BuiltDate> bdList = JSONHandler.builtDate();
        Model model = new LinkedHashModel();

        for (BuiltDate bd : bdList) {

            IRI subject = Vocabulary.vf.createIRI(Vocabulary.NS_SCOPE + bd.getSubject());

            if (!bd.getConstraint().equals(SHACL.CONSTRAINT_COMPONENT)) {
                createValueScope(model, bd.getValue1(), subject,
                        bd.getConstraint(),
                        "Virkeområde byggedato " + bd.datePrefixNo() + " " + bd.getValue1(),
                        "Scope of built date " + bd.datePrefixEn() + " " + bd.getValue1(),
                        XSD.DATE
                        );
            } else {
                createValueRangeScope(model, bd.getValue1(), bd.getValue2(), subject,
                        "Virkeområde byggedato " + bd.datePrefixNo() + " " + bd.getValue1() + " og " + bd.getValue2(),
                        "Scope of built date " + bd.datePrefixEn() + " " + bd.getValue1() + " and " + bd.getValue2(),
                        XSD.DATE
                );
            }
        }
        scopeModel.addAll(model);
    }

    /** machinePowerScope
     *
     * Build scope for machine power, appending results to global scopeModel.
     *
     * @throws IOException if input file not read
     */
    public static void electricalInstallationScope() throws IOException {

        List<ElectricalInstallation> elList = JSONHandler.electricalInstallations();
        Model model = new LinkedHashModel();

        for (ElectricalInstallation el : elList) {

            IRI subject = Vocabulary.vf.createIRI(Vocabulary.NS_SCOPE + el.getSubject());

            createValueScope(model, el.getValue(), subject,
                    el.getConstraint(),
                    "Virkeområde elektrisk innstallasjon " + el.getElPrefixNo() + " " + el.getValue(),
                    "Scope of electrical installation " + el.getElPrefixEn() + " " + el.getValue(),
                    Vocabulary.vf.createIRI(Utils.NS_UNIT + "V")
                    );
        }

        scopeModel.addAll(model);
    }

    /** getLocalDate
     *
     * Helper method for formatting date in the correct manner.
     *
     * @param dateStr input date as String
     * @return correctly parsed date
     */
    private static LocalDate getLocalDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH);
        return LocalDate.parse(dateStr, formatter);
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
    private static void createValueScope(Model model, String value, IRI subject, IRI valueConstraint, String desNo, String desEn, IRI datatype) {
        model.add(subject, RDF.TYPE, SHACL.PROPERTY_SHAPE);
        model.add(subject, RDF.TYPE, Vocabulary.Scope);
        model.add(subject, SHACL.DESCRIPTION, Vocabulary.vf.createLiteral(desNo, "no"));
        model.add(subject, SHACL.DESCRIPTION, Vocabulary.vf.createLiteral(desEn, "en"));
        model.add(subject, SHACL.PATH, Vocabulary.builtDate);

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
    private static void createValueRangeScope(Model model, String min, String max, IRI subject, String desNo, String desEn, IRI datatype) {
        model.add(subject, RDF.TYPE, SHACL.PROPERTY_SHAPE);
        model.add(subject, RDF.TYPE, Vocabulary.Scope);
        model.add(subject, SHACL.DESCRIPTION, Vocabulary.vf.createLiteral(desNo, "no"));
        model.add(subject, SHACL.DESCRIPTION, Vocabulary.vf.createLiteral(desEn, "en"));
        model.add(subject, SHACL.PATH, Vocabulary.builtDate);

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

        requirementModel.addAll(model);

    }

    /** getSectionHeaders
     *
     * Parse JSON input and store section number and section title in HashMap.
     * Used for rdfs:label for a requirement.
     *
     * @return HashMap containing section number, section title
     * @throws IOException if unable to read file
     */
    public static HashMap<String, String> getSectionHeaders() throws IOException {

        // TODO: retrieve JSON from API instead of file
        String content = Utils.readFromFile("src/main/resources/headers_test_file.json");

        JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
        JsonElement jsonElement = jsonObject.get("identified_sentence_type");
        JsonArray array = jsonElement.getAsJsonArray();

        HashMap<String, String> sections = new HashMap<>();

        for (int i = 0; i < array.size(); i++) {

            JsonElement element = array.get(i);
            JsonObject object = element.getAsJsonObject();

            if (object.get("sentence_type").getAsString().equals("headline-section")) {
                String sectionTitle = object.get("sentence_type_text").getAsString();
                String sectionNumber = object.get("sentence_type_value").getAsString();

                sections.put(sectionNumber, sectionTitle);
            }
        }
        return sections;
    }

    /** getChapterHeaders
     *
     * Parse JSON and store section title and chapter title in HashMap.
     * Chapter title is retrieved by getting section title. It therefore tracks chapter title for any section.
     * Used for sdir:theme for a requirement.
     *
     * @return HashMap containing section title, chapter title
     * @throws IOException if unable to read file
     */
    public static HashMap<String, String> getChapterHeader() throws IOException {

        HashMap<String, String> chapter = new HashMap<>();

        // TODO: retrieve JSON from API instead of file
        String content = Utils.readFromFile("src/main/resources/headers_test_file.json");

        JsonObject jsonObject = new JsonParser().parse(content).getAsJsonObject();
        JsonElement jsonElement = jsonObject.get("identified_sentence_type");
        JsonArray array = jsonElement.getAsJsonArray();

        String chapterTitle = "";

        for (int i = 0; i < array.size(); i++) {

            JsonElement element = array.get(i);
            JsonObject object = element.getAsJsonObject();

            if (object.get("sentence_type").getAsString().equals("headline-chapter")) {
                chapterTitle = object.get("sentence_type_text").getAsString();
            }

            if (object.get("sentence_type").getAsString().equals("headline-section")) {
                String sectionTitle = object.get("sentence_type_text").getAsString();

                chapter.put(sectionTitle, chapterTitle);
            }
        }
        return chapter;
    }

    /** writeScopeModelToFile
     *
     * Writes the global scope model to local file.
     * Scope model is built through createXXXScope()-methods.
     *
     * @throws FileNotFoundException if output file not found
     */
    public static void writeScopeModelToFile() throws FileNotFoundException {
        FileOutputStream out = new FileOutputStream("src/main/resources/output/scope.ttl");
        Rio.write(scopeModel, out, RDFFormat.TURTLE);
    }

    /** writeRequirementModelToFile
     *
     * Writes the global requirement model to local file.
     * Requirement model is built through addRequirement().
     *
     * @throws FileNotFoundException if output file not found
     */
    public static void writeRequirementModelToFile() throws FileNotFoundException {
        FileOutputStream out = new FileOutputStream("src/main/resources/output/requirement.ttl");
        Rio.write(requirementModel, out, RDFFormat.TURTLE);
    }
}
