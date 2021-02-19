package scope;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.vocabulary.SHACL;

public class LOA {
    Requirement requirement;
    String context;
    String value1;
    String value2;
    String metric;

    IRI constraint = SHACL.CONSTRAINT_COMPONENT;

    String subject;

    public LOA(Requirement r, String c, String v1, String v2, String m) {
        this.requirement = r;
        this.context = c;
        this.value1 = v1.replace(",", ".");
        this.value2 = v2.replace(",", ".");
        this.metric = m;

        this.subject = "PS_minLOA_" + stripDecimal(value1) + "_maxLOA_" + stripDecimal(value2);

    }

    public LOA(Requirement r, String c, String v1, String m) {
        this.requirement = r;
        this.context = c;
        this.value1 = v1.replace(",", ".");
        this.metric = m;

        setConstraint(c);

        this.subject = setSubjectPrefix() + stripDecimal(v1);
    }

    private String stripDecimal(String value) {
        if (value.contains(",")) {
            return value.split(",")[0];
        }
        if (value.contains(".")) {
            return value.split("\\.")[0];
        }
        return value;
    }

    private String setSubjectPrefix() {
        if (this.constraint.equals(SHACL.MAX_EXCLUSIVE)) {
            return "PS_maxLOA_";
        }
        return "PS_minLOA_";
    }

    private void setConstraint(String context) {
        if (context.equals("under") || context.equals("mindre enn")) {
            this.constraint = SHACL.MAX_EXCLUSIVE;
        }
    }

    public Requirement getRequirement() {
        return requirement;
    }

    public String getContext() {
        return context;
    }

    public String getValue1() {
        return value1;
    }

    public String getValue2() {
        return value2;
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

    public void printInformation() {
        requirement.printRequirementData();
        if (this.value2 == null) {
            System.out.print(": " + this.context + " " + this.value1 + " " + this.metric);
        } else {
            System.out.print(": " + this.value1 + " " + this.context + " " + this.value2 + " " + this.metric);
        }
    }
}
