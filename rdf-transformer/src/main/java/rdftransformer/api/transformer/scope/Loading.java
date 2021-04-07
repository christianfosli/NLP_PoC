package rdftransformer.api.transformer.scope;

public class Loading {

    Requirement requirement;
    String value, subject;

    public Loading(Requirement r, String v) {
        this.requirement = r;
        this.value = v;
        this.subject = "PS_LoadingAndUnloadingInstallation";
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
        return "loading and unloading installations";
    }
    public String contextNo() {
        return "laste- og losseinnretninger";
    }
}
