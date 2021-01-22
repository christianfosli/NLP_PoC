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

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;

public class GraphGenerator {

    public static Model scopeModel = Utils.initModel();
    public static Model requirementModel = Utils.initModel();

    /** vesselLengthScope
     *
     * Build scope for vessel length, appending results to global scopeModel.
     *
     * @param filename input CSV containing information about vessel length scopes
     * @throws IOException if input file not read
     */
    public static void vesselLengthScope(String filename) throws IOException {

        String[] content = Utils.fileContentSplitByComma(filename);

        Model model = new LinkedHashModel();


        for (int i = 0; i < content.length; i++) {
            if (content[i].equals("less than")) {
                String value = content[i+1];
                IRI subject = Vocabulary.vf.createIRI(Vocabulary.NS_SCOPE + "PS_maxLOA_" + Utils.removeDecimal(value));
                createValueScope(model, value, subject,
                        SHACL.MAX_EXCLUSIVE,
                        "Virkeområde største lengde " + value,
                        "Scope of length overall " + value,
                        Vocabulary.vf.createIRI(Utils.NS_UNIT + "M"));

                addRequirement(Utils.fileContentSplitByLine(filename), subject);
            }
            if (content[i].equals("to")) {
                String min = content[i+1];
                String max = content[i+2];
                IRI subject = Vocabulary.vf.createIRI(Vocabulary.NS_SCOPE + "PS_minLOA_" + Utils.removeDecimal(min) + "_maxLOA_" + Utils.removeDecimal(max));
                createValueRangeScope(model, min, max, subject,
                        "Virkeområde største lengde mellom " + min + " og " + max + ".",
                        "Scope of length overall between " + min + " and " + max + ".",
                        Vocabulary.vf.createIRI(Utils.NS_UNIT + "M"));

                addRequirement(Utils.fileContentSplitByLine(filename), subject);
            }
        }
        scopeModel.addAll(model);
    }

    /** builtDateScope
     *
     * Build scope for built date, appending results to global scopeModel.
     *
     * @param filename input CSV containing information about built date scopes
     * @throws IOException if input file not read
     */
    public static void builtDateScope(String filename) throws IOException {
        String[] content = Utils.fileContentSplitByComma(filename);

        Model model = new LinkedHashModel();

        for (int i = 0; i < content.length; i++) {
            if(content[i].equals("before")) {
                LocalDate date = getLocalDate(content[i+1].split("\n")[0]);
                IRI subject = Vocabulary.vf.createIRI(Vocabulary.NS_SCOPE + "PS_BuiltDate_b" + date.toString().replace("-", ""));
                createValueScope(model, date.toString(), subject,
                        SHACL.MAX_EXCLUSIVE,
                        "Virkeområde byggeår før " + date,
                        "Scope of built date before " + date,
                        XSD.DATE);

                addRequirement(Utils.fileContentSplitByLine(filename), subject);
            }
            if(content[i].equals("after")) {
                LocalDate date = getLocalDate(content[i+1].split("\n")[0]);
                IRI subject = Vocabulary.vf.createIRI(Vocabulary.NS_SCOPE + "PS_BuiltDate_a" + date.toString().replace("-", ""));
                createValueScope(model, date.toString(), subject,
                        SHACL.MIN_INCLUSIVE,
                        "Virkeområde byggeår etter " + date, "Scope of built date after " + date,
                        XSD.DATE);

                addRequirement(Utils.fileContentSplitByLine(filename), subject);
            }
        }
        scopeModel.addAll(model);
    }

    /** machinePowerScope
     *
     * Build scope for machine power, appending results to global scopeModel.
     *
     * @param filename input CSV containing information about machine power scopes
     * @throws IOException if input file not read
     */
    public static void machinePowerScope(String filename) throws IOException {
        String[] content = Utils.fileContentSplitByComma(filename);

        Model model = new LinkedHashModel();

        for (int i = 0; i < content.length; i++) {
            if (content[i].equals("more than")) {
                String value = content[i+1];
                IRI subject = Vocabulary.vf.createIRI(Vocabulary.NS_SCOPE + "PS_MP_min_" + value);
                createValueScope(model, value, subject,
                        SHACL.MIN_INCLUSIVE,
                        "Virkeområde maskinkraft mer enn " + value + " V.",
                        "Scope of machine power more than " + value + " V.",
                        Vocabulary.vf.createIRI(Utils.NS_UNIT + "V"));

                addRequirement(Utils.fileContentSplitByLine(filename), subject);
            }
            if (content[i].equals("up to")) {
                String value = content[i+1];
                IRI subject = Vocabulary.vf.createIRI(Vocabulary.NS_SCOPE + "PS_MP_max_" + value);
                createValueScope(model, value, subject,
                        SHACL.MAX_EXCLUSIVE,
                        "Virkeområde maskinkraft opp til " + value + " V.",
                        "Scope of machine power up to " + value + " V.",
                        Vocabulary.vf.createIRI(Utils.NS_UNIT + "V"));

                addRequirement(Utils.fileContentSplitByLine(filename), subject);
            }
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
     * @param data data from NLP module's CSV as String[], split by line
     * @param scope current scope
     * @throws IOException if unable to read files in getSectionHeaders() and getChapterHeader()
     */
    public static void addRequirement(String[] data, IRI scope) throws IOException {

        Model model = new LinkedHashModel();

        HashMap<String, String> sections = getSectionHeaders();

        String sectionNumber;
        String partNumber = "";
        String subPartNumber = "";

        for (String s : data) {
            String[] tmp = s.split(",");
            sectionNumber = tmp[0];

            if (!tmp[1].contains("none")) {
                partNumber = tmp[1];
            }
            if (!tmp[2].contains("none")) {
                subPartNumber = tmp[2];
            }

            // TODO: Update regulation no. in a more generic way.

            IRI subject;

            if (!subPartNumber.equals("")) {
                subject = Vocabulary.vf.createIRI(Vocabulary.NS + "REG1404" + "S" + sectionNumber + "P" + partNumber + "SP" + subPartNumber);
            }
            else {
                subject = Vocabulary.vf.createIRI(Vocabulary.NS + "REG1404" + "S" + sectionNumber + "P" + partNumber);
            }

            model.add(subject, RDF.TYPE, SHACL.NODE_SHAPE);
            model.add(subject, RDF.TYPE, Vocabulary.Requirement);
            model.add(subject, Vocabulary.regulationReference,
                    Vocabulary.vf.createLiteral("https://lovdata.no/forskrift/2013-11-22-1404/§" + sectionNumber)
            );
            model.add(subject, SHACL.PROPERTY, scope);
            model.add(subject, RDFS.LABEL, Vocabulary.vf.createLiteral(sections.get(sectionNumber), "en"));
            model.add(subject, Vocabulary.theme, Vocabulary.vf.createLiteral(getChapterHeader().get(sections.get(sectionNumber)), "en"));
        }

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
