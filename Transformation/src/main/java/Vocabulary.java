import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public class Vocabulary {

    public static ValueFactory vf = SimpleValueFactory.getInstance();
    public static final String NS = "https://www.sdir.no/SDIR_Simulator#";
    public static final String NS_SCOPE = "https://www.sdir.no/SDIR_Simulator/shapes/scope#";

    public static final IRI Scope = vf.createIRI(NS_SCOPE + "Scope");
    public static final IRI builtDate = vf.createIRI(NS + "builtDate");
}
