package scope;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.vocabulary.SHACL;

public class GrossTonnage {

    Requirement requirement;
    String context, value;

    IRI constraint = SHACL.CONSTRAINT_COMPONENT;
    String subject;

    public GrossTonnage(Requirement r, String c, String v) {
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
        if (c.equals("under") || c.equals("less than") ||
        c.equals("opp til") || c.equals("up to")) {
            this.constraint = SHACL.MAX_EXCLUSIVE;
            this.subject = "PS_GT_max" + value;
        }
        if (c.equals("eller mer") || c.equals("more than")) {
            this.constraint = SHACL.MIN_INCLUSIVE;
            this.subject = "PS_GT_min" + value;
        }
    }

    public String getPrefixNo() {
        if (this.constraint.equals(SHACL.MAX_EXCLUSIVE)) {
            return "under";
        } else {
            return "over";
        }
    }

    public String getPrefixEn() {
        if (this.constraint.equals(SHACL.MAX_EXCLUSIVE)) {
            return "less than";
        } else {
            return "more than";
        }
    }

}
