package ottr;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.vocabulary.SHACL;

public class OTTRUtils {

    public static final String OTTR_INSTANCES = "src/main/resources/ottr/instances.ttl";
    public static final String OTTR_TEMPLATES = "src/main/resources/ottr/o-tpl-lib.ttl";

    public static String getOTTRInstance(IRI constraint, String subject, String value) {

        if (constraint.equals(SHACL.MAX_EXCLUSIVE)) {
            return "\n" +
                    "o-sdir:Scope(scope:" + subject + ", sdir:passengers) ." + "\n" +
                    "o-sh:MaxExclusive(scope:" + subject + ", \"" + value + "\") ." + "\n" +
                    "o-sh:Datatype(scope:" + subject + ", xsd:int) ." + "\n" +
                    "\n";
        } else {
            return "\n" +
                    "o-sdir:Scope(scope:" + subject + ", sdir:passengers) ." + "\n" +
                    "o-sh:MinInclusive(scope:" + subject + ", \"" + value + "\") ." + "\n" +
                    "o-sh:Datatype(scope:" + subject + ", xsd:int) ." + "\n" +
                    "\n";
        }
    }


    public static String getOTTRInstance(IRI constraint, String subject, String value1, String value2) {
        if (constraint.equals(SHACL.CONSTRAINT_COMPONENT)) {
            return "\n" +
                    "o-sdir:Scope(scope:" + subject + ", sdir:builtDate) ." + "\n" +
                    "o-sh:MinInclusive(scope:" + subject + ", \"" + value1 + "\") ." + "\n" +
                    "o-sh:MaxExclusive(scope:" + subject + ", \"" + value2 + "\") ." + "\n" +
                    "o-sh:Datatype(scope:" + subject + ", xsd:date) ." + "\n" +
                    "\n";
        }
        if (constraint.equals(SHACL.MAX_EXCLUSIVE)) {
            return "\n" +
                    "o-sdir:Scope(scope:" + subject + ", sdir:passengers) ." + "\n" +
                    "o-sh:MaxExclusive(scope:" + subject + ", \"" + value1 + "\") ." + "\n" +
                    "o-sh:Datatype(scope:" + subject + ", xsd:int) ." + "\n" +
                    "\n";
        } else {
            return "\n" +
                    "o-sdir:Scope(scope:" + subject + ", sdir:passengers) ." + "\n" +
                    "o-sh:MinInclusive(scope:" + subject + ", \"" + value1 + "\") ." + "\n" +
                    "o-sh:Datatype(scope:" + subject + ", xsd:int) ." + "\n" +
                    "\n";
        }
    }

    public static String namespaces() {
        return "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
                "@prefix o-sh: <http://tpl.ottr.xyz/shacl/0.1/> .\n" +
                "@prefix sh: <http://www.w3.org/ns/shacl#> .\n" +
                "@prefix ottr: <http://ns.ottr.xyz/0.4/> .\n" +
                "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n" +
                "@prefix ex: <http://example.org/> .\n" +
                "\n" +
                "@prefix o-sdir: <https://www.sdir.no/SDIR_Simulator/ottr#> .\n" +
                "@prefix sdir: <https://www.sdir.no/SDIR_Simulator#> .\n" +
                "@prefix scope: <https://www.sdir.no/SDIR_Simulator/shapes/scope#> .\n" +
                "@prefix unit: <http://qudt.org/vocab/unit/> .";

    }
}
