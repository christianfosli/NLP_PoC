package rdftransformer.api.transformer.scope;

public class Cargo {

    Requirement requirement;
    String context, subject;

    public Cargo(Requirement r, String c) {
        this.requirement = r;
        this.context = c;
        this.subject = "PS_Cargo";
    }

    public Requirement getRequirement() {
        return requirement;
    }

    public String getContext() {
        return context;
    }

    public String getSubject() {
        return subject;
    }

    public String contextEn() {
        return "cargo";
    }

    public String contextNo() {
        return "last";
    }
}
