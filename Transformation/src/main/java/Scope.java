import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.SHACL;
import org.eclipse.rdf4j.model.vocabulary.XSD;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Scope {

    public static Model scopeModel = Utils.initModel();
    public static Model requirementModel = Utils.initModel();

    public static void vesselLengthScope(String filename) throws IOException {

        String[] content = fileContentSplitByComma(filename);

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

                addRequirement(fileContentSplitByLine(filename), subject);
            }
            if (content[i].equals("to")) {
                String min = content[i+1];
                String max = content[i+2];
                IRI subject = Vocabulary.vf.createIRI(Vocabulary.NS_SCOPE + "PS_minLOA_" + Utils.removeDecimal(min) + "_maxLOA_" + Utils.removeDecimal(max));
                createValueRangeScope(model, min, max, subject,
                        "Virkeområde største lengde mellom " + min + " og " + max + ".",
                        "Scope of length overall between " + min + " and " + max + ".",
                        Vocabulary.vf.createIRI(Utils.NS_UNIT + "M"));

                addRequirement(fileContentSplitByLine(filename), subject);
            }
        }
        scopeModel.addAll(model);
    }

    public static void builtDateScope(String filename) throws IOException {
        String[] content = fileContentSplitByComma(filename);

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

                addRequirement(fileContentSplitByLine(filename), subject);
            }
            if(content[i].equals("after")) {
                LocalDate date = getLocalDate(content[i+1].split("\n")[0]);
                IRI subject = Vocabulary.vf.createIRI(Vocabulary.NS_SCOPE + "PS_BuiltDate_a" + date.toString().replace("-", ""));
                createValueScope(model, date.toString(), subject,
                        SHACL.MIN_INCLUSIVE,
                        "Virkeområde byggeår etter " + date, "Scope of built date after " + date,
                        XSD.DATE);

                addRequirement(fileContentSplitByLine(filename), subject);
            }
        }
        scopeModel.addAll(model);
    }

    public static void machinePowerScope(String filename) throws IOException {
        String[] content = fileContentSplitByComma(filename);

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

                addRequirement(fileContentSplitByLine(filename), subject);
            }
            if (content[i].equals("up to")) {
                String value = content[i+1];
                IRI subject = Vocabulary.vf.createIRI(Vocabulary.NS_SCOPE + "PS_MP_max_" + value);
                createValueScope(model, value, subject,
                        SHACL.MAX_EXCLUSIVE,
                        "Virkeområde maskinkraft opp til " + value + " V.",
                        "Scope of machine power up to " + value + " V.",
                        Vocabulary.vf.createIRI(Utils.NS_UNIT + "V"));

                addRequirement(fileContentSplitByLine(filename), subject);
            }
        }
        scopeModel.addAll(model);
    }

    private static LocalDate getLocalDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH);
        return LocalDate.parse(dateStr, formatter);
    }

    private static void createValueScope(Model model, String value, IRI subject, IRI valueConstraint, String desNo, String desEn, IRI datatype) {
        model.add(subject, RDF.TYPE, SHACL.PROPERTY_SHAPE);
        model.add(subject, RDF.TYPE, Vocabulary.Scope);
        model.add(subject, SHACL.DESCRIPTION, Vocabulary.vf.createLiteral(desNo, "no"));
        model.add(subject, SHACL.DESCRIPTION, Vocabulary.vf.createLiteral(desEn, "en"));
        model.add(subject, SHACL.PATH, Vocabulary.builtDate);

        model.add(subject, valueConstraint, Vocabulary.vf.createLiteral(value));

        model.add(subject, SHACL.DATATYPE, datatype);
    }

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

    public static void addRequirement(String[] data, IRI scope) {

        Model model = new LinkedHashModel();

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
        }

        requirementModel.addAll(model);

    }

    public static void writeScopeModelToFile() throws FileNotFoundException {
        FileOutputStream out = new FileOutputStream("src/main/resources/output/scope.ttl");
        Rio.write(scopeModel, out, RDFFormat.TURTLE);
    }

    public static void writeRequirementModelToFile() throws FileNotFoundException {
        FileOutputStream out = new FileOutputStream("src/main/resources/output/requirement.ttl");
        Rio.write(requirementModel, out, RDFFormat.TURTLE);
    }

    public static String[] fileContentSplitByComma(String filename) throws IOException {
        String content = Utils.readFromFile(filename);
        String[] splitLine = content.split("\n");

        StringBuilder tmp = new StringBuilder();

        for (int i = 0; i < splitLine.length; i++) {
            if (i == splitLine.length-1) {
                tmp.append(splitLine[i]);
            } else {
                tmp.append(splitLine[i]).append(",");
            }
        }
        return tmp.toString().split(",");
    }

    public static String[] fileContentSplitByLine(String filename) throws IOException {
        String content = Utils.readFromFile(filename);
        return content.split("\n");
    }
}
