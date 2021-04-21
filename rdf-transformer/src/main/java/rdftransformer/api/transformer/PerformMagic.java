package rdftransformer.api.transformer;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import rdftransformer.api.transformer.action.GraphGenerator;
import rdftransformer.api.transformer.action.JSONHandler;
import rdftransformer.api.transformer.utils.Utils;

import java.io.IOException;
import java.io.StringWriter;

public class PerformMagic {

    public static String magic(String input) {
        try {
            JSONHandler.JSONReceiver(input);
            Model model = GraphGenerator.scopeModel;
            model.addAll(GraphGenerator.requirementModel);

            return Utils.modelToString(model, RDFFormat.TURTLE);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "{'magic':'nope'}";
    }
}
