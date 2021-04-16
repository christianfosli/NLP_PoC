package rdftransformer.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.junit.Test;
import static org.junit.Assert.*;

import rdftransformer.api.transformer.action.GraphGenerator;
import rdftransformer.api.transformer.utils.Utils;

import java.io.IOException;
import java.io.StringWriter;

public class GraphGeneratorTest {

    @Test
    public void builtDateTest() throws IOException {

        JsonObject jsonObject = new JsonParser().parse(
                Utils.readFromFile("src/test/resources/builtdate/input.json")).getAsJsonObject();

        GraphGenerator.builtDateScope(jsonObject.get(Utils.BD_JSON_OBJ).getAsJsonArray());
        Model model = mergeModels(GraphGenerator.requirementModel, GraphGenerator.scopeModel);

        assertEquals(
                cleanString(modelToString(model)),
                cleanString(Utils.readFromFile("src/test/resources/builtdate/expected"))
        );
    }

    @Test
    public void cargoTest() throws IOException {

        JsonObject jsonObject = new JsonParser().parse(
                Utils.readFromFile("src/test/resources/cargo/input.json")).getAsJsonObject();


        GraphGenerator.cargoScope(jsonObject.get(Utils.CARGO_JSON_OBJ).getAsJsonArray());
        Model model = mergeModels(GraphGenerator.requirementModel, GraphGenerator.scopeModel);

        assertEquals(
                cleanString(modelToString(model)),
                cleanString(Utils.readFromFile("src/test/resources/cargo/expected"))
        );
    }

    @Test
    public void conversionTest() throws IOException {

        JsonObject jsonObject = new JsonParser().parse(
                Utils.readFromFile("src/test/resources/conversion/input.json")).getAsJsonObject();

        GraphGenerator.convertedScope(jsonObject.get(Utils.CON_JSON_OBJ).getAsJsonArray());
        Model model = mergeModels(GraphGenerator.requirementModel, GraphGenerator.scopeModel);

        assertEquals(
                cleanString(modelToString(model)),
                cleanString(Utils.readFromFile("src/test/resources/conversion/expected"))
        );
    }

    @Test
    public void electricalInstallationTest() throws IOException {

        JsonObject jsonObject = new JsonParser().parse(
                Utils.readFromFile("src/test/resources/el/input.json")).getAsJsonObject();

        GraphGenerator.electricalInstallationScope(jsonObject.get(Utils.EL_JSON_OBJ).getAsJsonArray());
        Model model = mergeModels(GraphGenerator.requirementModel, GraphGenerator.scopeModel);

        assertEquals(
                cleanString(modelToString(model)),
                cleanString(Utils.readFromFile("src/test/resources/el/expected"))
        );
    }

    @Test
    public void flashpointTest() throws IOException {

        JsonObject jsonObject = new JsonParser().parse(
                Utils.readFromFile("src/test/resources/flashpoint/input.json")).getAsJsonObject();

        GraphGenerator.flashpointScope(jsonObject.get(Utils.FP_JSON_OBJ).getAsJsonArray());
        Model model = mergeModels(GraphGenerator.requirementModel, GraphGenerator.scopeModel);

        assertEquals(
                cleanString(modelToString(model)),
                cleanString(Utils.readFromFile("src/test/resources/flashpoint/expected"))
        );
    }

    @Test
    public void grossTonnageTest() throws IOException {

        JsonObject jsonObject = new JsonParser().parse(
                Utils.readFromFile("src/test/resources/grosstonnage/input.json")).getAsJsonObject();

        GraphGenerator.grossTonnageScope(jsonObject.get(Utils.GT_JSON_OBJ).getAsJsonArray());
        Model model = mergeModels(GraphGenerator.requirementModel, GraphGenerator.scopeModel);

        assertEquals(
                cleanString(modelToString(model)),
                cleanString(Utils.readFromFile("src/test/resources/grosstonnage/expected"))
        );
    }

    @Test
    public void loaTest() throws IOException {

        JsonObject jsonObject = new JsonParser().parse(
                Utils.readFromFile("src/test/resources/loa/input.json")).getAsJsonObject();

        GraphGenerator.vesselLengthScope(jsonObject.get(Utils.LOA_JSON_OBJ).getAsJsonArray());
        Model model = mergeModels(GraphGenerator.requirementModel, GraphGenerator.scopeModel);

        assertEquals(
                cleanString(modelToString(model)),
                cleanString(Utils.readFromFile("src/test/resources/loa/expected"))
        );
    }

    @Test
    public void loadingTest() throws IOException {

        JsonObject jsonObject = new JsonParser().parse(
                Utils.readFromFile("src/test/resources/loading/input.json")).getAsJsonObject();

        GraphGenerator.loadingScope(jsonObject.get(Utils.LO_JSON_OBJ).getAsJsonArray());
        Model model = mergeModels(GraphGenerator.requirementModel, GraphGenerator.scopeModel);

        assertEquals(
                cleanString(modelToString(model)),
                cleanString(Utils.readFromFile("src/test/resources/loading/expected"))
        );
    }

    @Test
    public void passengerTest() throws IOException {

        JsonObject jsonObject = new JsonParser().parse(
                Utils.readFromFile("src/test/resources/passenger/input.json")).getAsJsonObject();



        GraphGenerator.passengerScope(jsonObject.get(Utils.PASS_JSON_OBJ).getAsJsonArray());
        Model model = mergeModels(GraphGenerator.requirementModel, GraphGenerator.scopeModel);

        assertEquals(
                cleanString(modelToString(model)),
                cleanString(Utils.readFromFile("src/test/resources/passenger/expected"))
        );
    }

    @Test
    public void propulsionPowerTest() throws IOException {

        JsonObject jsonObject = new JsonParser().parse(
                Utils.readFromFile("src/test/resources/propulsionpower/input.json")).getAsJsonObject();

        GraphGenerator.propulsionPowerScope(jsonObject.get(Utils.PROP_JOSN_OBJ).getAsJsonArray());
        Model model = mergeModels(GraphGenerator.requirementModel, GraphGenerator.scopeModel);

        assertEquals(
                cleanString(modelToString(model)),
                cleanString(Utils.readFromFile("src/test/resources/propulsionpower/expected"))
        );

    }

    @Test
    public void protectedTest() throws IOException {

        JsonObject jsonObject = new JsonParser().parse(
                Utils.readFromFile("src/test/resources/protected/input.json")).getAsJsonObject();

        GraphGenerator.protectedScope(jsonObject.get(Utils.PRO_JSON_OBJ).getAsJsonArray());
        Model model = mergeModels(GraphGenerator.requirementModel, GraphGenerator.scopeModel);

        assertEquals(
                cleanString(modelToString(model)),
                cleanString(Utils.readFromFile("src/test/resources/protected/expected"))
        );
    }

    @Test
    public void radioAreaTest() throws IOException {

        JsonObject jsonObject = new JsonParser().parse(
                Utils.readFromFile("src/test/resources/radioarea/input.json")).getAsJsonObject();

        GraphGenerator.radioAreaScope(jsonObject.get(Utils.RA_JSON_OBJ).getAsJsonArray());
        Model model = mergeModels(GraphGenerator.requirementModel, GraphGenerator.scopeModel);

        assertEquals(
                cleanString(modelToString(model)),
                cleanString(Utils.readFromFile("src/test/resources/radioarea/expected"))
        );
    }

    @Test
    public void tradeAreaTest() throws IOException {

        JsonObject jsonObject = new JsonParser().parse(
                Utils.readFromFile("src/test/resources/tradearea/input.json")).getAsJsonObject();

        GraphGenerator.tradeAreaScope(jsonObject.get(Utils.TA_JSON_OBJ).getAsJsonArray());
        Model model = mergeModels(GraphGenerator.requirementModel, GraphGenerator.scopeModel);

        assertEquals(
                cleanString(modelToString(model)),
                cleanString(Utils.readFromFile("src/test/resources/tradearea/expected"))
        );
    }

    @Test
    public void vesselTypeTest() throws IOException {

        JsonObject jsonObject = new JsonParser().parse(
                Utils.readFromFile("src/test/resources/vesseltype/input.json")).getAsJsonObject();

        GraphGenerator.vesselTypeScope(jsonObject.get(Utils.VT_JSON_OBJ).getAsJsonArray());
        Model model = mergeModels(GraphGenerator.requirementModel, GraphGenerator.scopeModel);

        assertEquals(
                cleanString(modelToString(model)),
                cleanString(Utils.readFromFile("src/test/resources/vesseltype/expected"))
        );
    }

    public static String cleanString(String s) {
        return s.trim().replace("\n", "").replace("\r", "");
    }

    public static String modelToString(Model model) {
        StringWriter writer = new StringWriter();
        RDFWriter rdfWriter = Rio.createWriter(RDFFormat.TURTLE, writer);
        Rio.write(model, rdfWriter);
        return writer.toString();
    }

    public static Model mergeModels(Model m1, Model m2) {
        m1.addAll(m2);
        return m1;
    }

}
