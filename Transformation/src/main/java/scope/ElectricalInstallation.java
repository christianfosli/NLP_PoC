package scope;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.vocabulary.SHACL;
import ottr.OTTRUtils;

public class ElectricalInstallation {
    Requirement requirement;
    String context;
    String value;
    String metric;

    IRI constraint = SHACL.CONSTRAINT_COMPONENT;
    String subject;

    public ElectricalInstallation(Requirement r, String c, String v, String m) {
        this.requirement = r;
        this.context = c;
        this.value = v;
        this.metric = m;

        setConstraint(c);
    }

    private void setConstraint(String c) {

        if (c.equals("inntil") || c.equals("up to")) {
            this.constraint = SHACL.MAX_EXCLUSIVE;
            this.subject = "PS_ElIn_max" + this.value;
        }
        if (c.equals("over") || c.equals("more than")) {
            this.constraint = SHACL.MIN_INCLUSIVE;
            this.subject = "PS_ElIn_min" + this.value;
        }
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

    public String getMetric() {
        return metric;
    }

    public IRI getConstraint() {
        return constraint;
    }

    public String getSubject() {
        return subject;
    }

    public String getElPrefixNo() {
        String prefix = this.context;
        switch (prefix) {
            case "more than":
                prefix = "over"; break;
            case "up to":
                prefix = "inntil"; break;
        }
        return prefix;
    }

    public String getElPrefixEn() {
        String prefix = this.context;
        switch (prefix) {
            case "over":
                prefix = "more than"; break;
            case "inntil":
                prefix = "up to"; break;
        }
        return prefix;
    }

    public String getOTTRInstance() {
        return OTTRUtils.getOTTRInstance(this.constraint, this.subject, this.value);
    }
}
