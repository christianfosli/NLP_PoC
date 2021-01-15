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

    public static void vesselLengthScope(String filename) throws IOException {
        String content = Utils.readFromFile(filename);
        String[] tmp = content.split(",");

        Model model = new LinkedHashModel();

        for (int i = 0; i < tmp.length; i++) {
            if (tmp[i].equals("less than")) {
                String value = tmp[i+1];
                IRI subject = Vocabulary.vf.createIRI(Vocabulary.NS_SCOPE + "PS_maxLOA_" + Utils.removeDecimal(value));
                createValueScope(model, value, subject,
                        SHACL.MAX_EXCLUSIVE,
                        "Virkeområde største lengde " + value,
                        "Scope of length overall " + value,
                        Vocabulary.vf.createIRI(Utils.NS_UNIT + "M"));
            }
            if (tmp[i].equals("to")) {
                String min = tmp[i+1];
                String max = tmp[i+2];
                IRI subject = Vocabulary.vf.createIRI(Vocabulary.NS_SCOPE + "PS_minLOA_" + Utils.removeDecimal(min) + "_maxLOA_" + Utils.removeDecimal(max));
                createValueRangeScope(model, min, max, subject,
                        "Virkeområde største lengde mellom " + min + " og " + max + ".",
                        "Scope of length overall between " + min + " and " + max + ".",
                        Vocabulary.vf.createIRI(Utils.NS_UNIT + "M"));
            }
        }
        scopeModel.addAll(model);
    }

    public static void builtDateScope(String filename) throws IOException {
        String content = Utils.readFromFile(filename);
        String[] tmp = content.split(",");

        Model model = new LinkedHashModel();

        for (int i = 0; i < tmp.length; i++) {
            if(tmp[i].equals("before")) {
                LocalDate date = getLocalDate(tmp[i+1].split("\n")[0]);
                IRI subject = Vocabulary.vf.createIRI(Vocabulary.NS_SCOPE + "PS_BuiltDate_b" + date.toString().replace("-", ""));
                createValueScope(model, date.toString(), subject,
                        SHACL.MAX_EXCLUSIVE,
                        "Virkeområde byggeår før " + date,
                        "Scope of built date before " + date,
                        XSD.DATE);
            }
            if(tmp[i].equals("after")) {
                LocalDate date = getLocalDate(tmp[i+1].split("\n")[0]);
                IRI subject = Vocabulary.vf.createIRI(Vocabulary.NS_SCOPE + "PS_BuiltDate_a" + date.toString().replace("-", ""));
                createValueScope(model, date.toString(), subject,
                        SHACL.MIN_INCLUSIVE,
                        "Virkeområde byggeår etter " + date, "Scope of built date after " + date,
                        XSD.DATE);
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

    public static void writeScopeModelToFile() throws FileNotFoundException {
        FileOutputStream out = new FileOutputStream("src/main/resources/output/scope.ttl");
        Rio.write(scopeModel, out, RDFFormat.TURTLE);
    }
}
