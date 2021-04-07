package rdftransformer.api.transformer.scope;

public class Converted {

    Requirement requirement;
    String value, subject;

    public Converted(Requirement r, String v) {
        this.requirement = r;
        this.value = v;

        this.subject = "PS_Converted";
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

    public String contextEn() {
        return "converted";
    }

    public String contextNo() {
        return "ombygget";
    }
}
