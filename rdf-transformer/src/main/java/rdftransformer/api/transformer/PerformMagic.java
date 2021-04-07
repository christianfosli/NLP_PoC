package rdftransformer.api.transformer;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import rdftransformer.api.transformer.action.GraphGenerator;
import rdftransformer.api.transformer.action.JSONHandler;

import java.io.IOException;
import java.io.StringWriter;

public class PerformMagic {

    public static String magic(String input) {
        try {
            JSONHandler.JSONReceiver(input);
            Model model = GraphGenerator.scopeModel;
            model.addAll(GraphGenerator.requirementModel);

            StringWriter writer = new StringWriter();
            RDFWriter rdfWriter = Rio.createWriter(RDFFormat.TURTLE, writer);
            Rio.write(model, rdfWriter);
            return writer.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "{'magic':'nope'}";
    }
}
