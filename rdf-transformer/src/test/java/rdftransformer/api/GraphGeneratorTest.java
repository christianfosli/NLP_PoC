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
import rdftransformer.api.transformer.action.JSONHandler;
import rdftransformer.api.transformer.utils.Utils;

import java.io.IOException;
import java.io.StringWriter;

public class GraphGeneratorTest {

    @Test
    public void graphGeneratorTest() throws IOException {
        JSONHandler.JSONReceiver(Utils.readFromFile(
                "src/main/resources/input/builtdate.json"
        ));
    }

    @Test
    public void propulsionPowerTest() throws IOException {

        JsonObject jsonObject = new JsonParser().parse(
                Utils.readFromFile("src/test/resources/propulsionpower/input.json")).getAsJsonObject();

        GraphGenerator.propulsionPowerScope(jsonObject.get(Utils.PROP_JOSN_OBJ).getAsJsonArray());
        Model model = GraphGenerator.requirementModel;
        model.addAll(GraphGenerator.scopeModel);

        StringWriter writer = new StringWriter();
        RDFWriter rdfWriter = Rio.createWriter(RDFFormat.TURTLE, writer);
        Rio.write(model, rdfWriter);

        assertEquals(
                writer.toString(),
                Utils.readFromFile("src/test/resources/propulsionpower/expected.ttl")
        );

    }

}
