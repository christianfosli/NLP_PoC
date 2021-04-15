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

    public static void main(String[] args) throws IOException {
        parseJson(Utils.readFromFile("src/main/resources/input/classes.json"));
    }

    public static String classification(String s) {

        parseJson(s);
        return Utils.printModel(model, RDFFormat.TURTLE);

    }

    public static void parseJson(String s) {
        JsonObject jsonObject = new JsonParser().parse(s).getAsJsonObject();
        JsonArray array = jsonObject.get("identified_named_entities").getAsJsonArray();

        List<String> ignoredEntities = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {
            JsonElement element = array.get(i);
            JsonObject object = element.getAsJsonObject();

            String class_label = object.get("class label").toString()
                    .toLowerCase()
                    .replace("\"", "")
                    .replace(".", "");
            String entity = object.get("entity").toString()
                    .toLowerCase()
                    .replace("\"", "")
                    .replace(".", "");

            if (!class_label.equals(entity)) {
                if (!class_label.equals("unit")) {
                    if (entity.length() < 30 ) { // Entry point for classes
                        String class_label_cap = WordUtils.capitalize(class_label).replace(" ", "");
                        String entity_cap = WordUtils.capitalize(entity).replace(" ", "");

                        if(entity.contains("/")) {
                            String[] split = entity.split("/");
                            addToModel(class_label_cap, class_label,
                                    WordUtils.capitalize(split[0]).replace(" ", ""), split[0]);
                            addToModel(class_label_cap, class_label,
                                    WordUtils.capitalize(split[1]).replace(" ", ""), split[1]);
                        } else {
                            addToModel(class_label_cap, class_label, entity_cap, entity);
                        }

                    } else {
                        ignoredEntities.add(class_label + "," + entity + "\n");
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

    private static void addToModel(String class_label, String classLabel, String entity, String entityLabel) {
        IRI subject = Vocabulary.vf.createIRI(Vocabulary.NS + class_label);
        model.add(subject, RDF.TYPE, OWL.CLASS);
        model.add(subject, RDFS.LABEL, Vocabulary.vf.createLiteral(classLabel, "en"));

        IRI o = Vocabulary.vf.createIRI(Vocabulary.NS + entity);
        model.add(o, RDF.TYPE, OWL.CLASS);
        model.add(o, RDFS.SUBCLASSOF, subject);
        model.add(o, RDFS.LABEL, Vocabulary.vf.createLiteral(entityLabel, "en"));
    }
}
