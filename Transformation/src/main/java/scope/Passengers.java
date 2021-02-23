package scope;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.vocabulary.SHACL;

public class Passengers {
    Requirement requirement;
    String context, value;

    IRI constraint = SHACL.CONSTRAINT_COMPONENT;
    String subject;

    public Passengers(Requirement r, String c, String v) {
        this.requirement = r;
        this.context = c;
        this.value = v;

        setConstraint(c);
    }

    public Requirement getRequirement() {
        return requirement;
    }

    public String getContext() {
        return context;
    }

    public String getValue() {
        return value;
    }

    public String getSubject() {
        return subject;
    }

    public IRI getConstraint() {
        return constraint;
    }

    private void setConstraint(String c) {
        if (c.equals("eller færre") || c.equals("or less")) {
            this.constraint = SHACL.MAX_EXCLUSIVE;
            this.subject = "PS_Pass_max" + value;
        }
        if (c.equals("mer enn") || c.equals("more than")) {
            this.constraint = SHACL.MIN_INCLUSIVE;
            this.subject = "PS_Pass_min" + value;
        }
    }

    public String getPrefixNo() {
        if (this.constraint.equals(SHACL.MAX_EXCLUSIVE)) {
            return "mindre enn";
        } else {
            return "flere enn";
        }
    }

    public String getPrefixEn() {
        if (this.constraint.equals(SHACL.MIN_INCLUSIVE)) {
            return "more than";
        } else {
            return "less than";
        }
    }
}
