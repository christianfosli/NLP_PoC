package rdftransformer.api.transformer.action;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.text.WordUtils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.rio.RDFFormat;
import rdftransformer.api.transformer.utils.Utils;
import rdftransformer.api.transformer.utils.Vocabulary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Classification {

    static Model model = Utils.initModel();
    static List<String> ignoredEntities = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        String s = classification(Utils.readFromFile("src/main/resources/input/classes.json"));
        System.out.println(s);
    }

    public static String classification(String s) {

        parseJson(s);
        return Utils.modelToString(model, RDFFormat.TURTLE);

    }

    public static void parseJson(String s) {
        JsonObject jsonObject = new JsonParser().parse(s).getAsJsonObject();
        JsonArray array = jsonObject.get("identified_named_entities").getAsJsonArray();

        for (int i = 0; i < array.size(); i++) {
            JsonElement element = array.get(i);
            JsonObject object = element.getAsJsonObject();

            String classLabelEn = preprocess(object.get("class label no").toString());
            String entityLabelEn = preprocess(object.get("entity en").toString());

            String classLabelNo = preprocess(object.get("class label en").toString());
            String entityLabelNo = preprocess(object.get("entity no").toString());

            classLabelNo = classLabelNo.substring(0, 1).toUpperCase() + classLabelNo.substring(1);
            entityLabelNo = entityLabelNo.substring(0, 1).toUpperCase() + entityLabelNo.substring(1);

            if (!classLabelEn.equals(entityLabelEn)) {
                if (!classLabelEn.equals("unit")) {
                    if (entityLabelEn.length() < 30 ) { // Entry point for classes
                        String classLabelEnCapitalized = WordUtils.capitalize(classLabelEn).replace(" ", "");
                        String entityLabelEnCapitalized = WordUtils.capitalize(entityLabelEn).replace(" ", "");

                        if(entityLabelEn.contains("/")) {
                            String[] split = entityLabelEn.split("/");
                            addToModel(
                                    classLabelEnCapitalized, classLabelEn,
                                    WordUtils.capitalize(split[0]).replace(" ", ""), split[0],
                                    classLabelNo, entityLabelNo
                            );
                            addToModel(
                                    classLabelEnCapitalized, classLabelEn,
                                    WordUtils.capitalize(split[1]).replace(" ", ""), split[1],
                                    classLabelNo, entityLabelNo
                            );
                        } else if (entityLabelEn.endsWith("-")) {
                            ignoredEntities.add(classLabelEn + "," + entityLabelEn + "\n");
                        } else if (entityLabelEn.matches(".*\\d.*")) {
                            ignoredEntities.add(classLabelEn + "," + entityLabelEn + "\n");
                        } else {
                            addToModel(classLabelEnCapitalized, classLabelEn, entityLabelEnCapitalized, entityLabelEn, classLabelNo, entityLabelNo);
                        }

                    } else {
                        ignoredEntities.add(classLabelEn + "," + entityLabelEn + "\n");
                        try {
                            Utils.writeListToFile(
                                    ignoredEntities,
                                    "src/main/resources/output/classification/ignoredEntities.csv"
                            );
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    // TODO: Do something with units
                }
            }
        }
    }

    private static String preprocess(String s) {
        return s.toLowerCase()
                .replace("\"", "")
                .replace(".", "")
                .replace("'", "");
    }

    public static String getIgnoredEntities() {
        StringBuilder tmp = new StringBuilder();
        for (String s : ignoredEntities) {
            tmp.append(s);
        }
        return tmp.toString();
    }

    private static void addToModel(String subjectFragment, String subjectLabel, String objFragment, String objLabel,
                                   String classLabelNo, String entitiyLabelNo) {

        subjectLabel = subjectLabel.substring(0, 1).toUpperCase() + subjectLabel.substring(1);

        IRI subject = Vocabulary.vf.createIRI(Vocabulary.NS + subjectFragment);
        model.add(subject, RDF.TYPE, OWL.CLASS);
        model.add(subject, RDFS.LABEL, Vocabulary.vf.createLiteral(subjectLabel.trim(), "en"));
        model.add(subject, RDFS.LABEL, Vocabulary.vf.createLiteral(classLabelNo.trim(), "no"));

        objLabel = objLabel.substring(0, 1).toUpperCase() + objLabel.substring(1);

        IRI o = Vocabulary.vf.createIRI(Vocabulary.NS + objFragment);
        model.add(o, RDF.TYPE, OWL.CLASS);
        model.add(o, RDFS.SUBCLASSOF, subject);
        model.add(o, RDFS.LABEL, Vocabulary.vf.createLiteral(objLabel.trim(), "en"));
        model.add(o, RDFS.LABEL, Vocabulary.vf.createLiteral(entitiyLabelNo.trim(), "no"));
    }
}
