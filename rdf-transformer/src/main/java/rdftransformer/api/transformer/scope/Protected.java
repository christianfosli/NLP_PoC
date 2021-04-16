package rdftransformer.api.transformer.scope;

public class Protected {

    Requirement requirement;
    String value, subject;

    public Protected(Requirement r, String v) {
        this.requirement = r;
        this.value = v;
        this.subject = "PS_Protected";
    }

    public Requirement getRequirement() {
        return requirement;
    }

    public String getValue() {
        return value;
    }

    public String getSubject() {
        return subject;
    }

    public String contextNo() {
        return "fredet";
    }
    public String contextEn() {
        return "protected";
    }
}
