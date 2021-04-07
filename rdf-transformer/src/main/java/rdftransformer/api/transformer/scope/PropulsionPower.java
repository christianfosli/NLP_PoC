package rdftransformer.api.transformer.scope;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.vocabulary.SHACL;

public class PropulsionPower {

    Requirement requirement;
    String context, value1, value2, metric;

    String subject;

    IRI constraint = SHACL.CONSTRAINT_COMPONENT;

    public PropulsionPower(Requirement r, String c, String v, String m) {
        this.requirement = r;
        this.context = c;
        this.value1 = v;
        this.metric = m;

        setConstraint(c);
    }

    public PropulsionPower(Requirement r, String c, String v1, String v2, String m) {
        this.requirement = r;
        this.context = c;
        this.value1 = v1;
        this.value2 = v2;
        this.metric = m;
    }

    public Requirement getRequirement() {
        return requirement;
    }

    public String getContext() {
        return context;
    }

    public String getMetric() {
        return metric;
    }

    public String getValue1() {
        return value1;
    }

    public String getValue2() {
        return value2;
    }

    public String getSubject() {
        return subject;
    }

    public IRI getConstraint() {
        return constraint;
    }

    public String contextEn() {
        if (this.constraint.equals(SHACL.MAX_EXCLUSIVE)) {
            return "up to";
        }
        return "more than";
    }

    private void setConstraint(String c) {
        if (c.equals("eller mer")) {
            this.constraint = SHACL.MIN_INCLUSIVE;
            this.subject = "PS_PropulsionPower_min" + value1;
        }
        if (c.equals("opp til")) {
            this.constraint = SHACL.MAX_EXCLUSIVE;
            this.subject = "PS_PropulsionPower_max" + value1;
        }
    }

}
