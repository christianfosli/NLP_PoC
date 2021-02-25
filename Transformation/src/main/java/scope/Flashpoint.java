package scope;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.vocabulary.SHACL;

public class Flashpoint {
    Requirement requirement;
    String context, value, metric;

    IRI constraint = SHACL.CONSTRAINT_COMPONENT;
    String subject;

    public Flashpoint(Requirement r, String c, String v, String m) {
        this.requirement = r;
        this.context = c;
        this.value = v;
        this.metric = m;

        setConstraint(context);
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

    public String celciusToFarenheit(String c) {
        return "" + ((Integer.parseInt(c)*9)/5)+32;
    }

    private void setConstraint(String c) {
        if (c.equals("under") || c.equals("less than")) {
            this.constraint = SHACL.MAX_EXCLUSIVE;
            this.subject = "PS_Flash_max" + value;
        }
        if (c.equals("eller mer") || c.equals("not less than")) {
            this.constraint = SHACL.MIN_INCLUSIVE;
            this.subject = "PS_Flash_min" + value;
        }
    }

    public String getPrefixNo() {
        if (this.constraint.equals(SHACL.MAX_EXCLUSIVE)) {
            return "mindre enn";
        } else {
            return "mer enn";
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
