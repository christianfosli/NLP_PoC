package rdftransformer.api.transformer.scope;

import org.eclipse.rdf4j.model.IRI;
import rdftransformer.api.transformer.utils.Vocabulary;

public class RadioArea {
    Requirement requirement;
    String value;

    String subject;

    public RadioArea(Requirement r, String v) {
        this.requirement = r;
        this.value = v;

        this.subject = "PS_" + this.value;
    }

    public Requirement getRequirement() {
        return requirement;
    }

    public IRI getValue() {
        switch (this.value) {
            case "A1": return Vocabulary.A1;
            case "A2": return Vocabulary.A2;
            case "A3": return Vocabulary.A3;
            case "A4": return Vocabulary.A4;
        }
        return Vocabulary.vf.createIRI(Vocabulary.NS);
    }

    public String getSubject() {
        return subject;
    }

}
