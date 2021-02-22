package scope;

public class Requirement {
    String regulation;
    String chapter;
    String paragraph;
    String part;
    String subpart;
    String regulation_id_eli;
    String regulation_id_lovdata;

    public Requirement(String regulation_id, String regulation, String chapter, String paragraph, String part, String subpart) {
        this.regulation = regulation;
        this.chapter = chapter;
        this.paragraph = paragraph;
        this.part = part;
        this.subpart = subpart;
        this.regulation_id_eli = createELIID(regulation_id);
        this.regulation_id_lovdata = createLovdataID(regulation_id);
    }

    public Requirement(String regulation_id, String regulation, String chapter, String paragraph, String part) {
        this.regulation = regulation;
        this.chapter = chapter;
        this.paragraph = paragraph;
        this.part = part;
        this.regulation_id_eli = createELIID(regulation_id);
        this.regulation_id_lovdata = createLovdataID(regulation_id);
    }

    private String createELIID(String id) {
        String[] split = id.split("-");
        return  "regulation/" + split[0] + "/" + split[1] + "/" + split[2] + "/" + split[3];
    }

    private String createLovdataID(String id) {
        return  "https://lovdata.no/dokument/SF/forskrift/" + id;
    }

    public String getRegulation() {
        return regulation;
    }

    public String getChapter() {
        return chapter;
    }

    public String getParagraph() {
        return paragraph;
    }

    public String getPart() {
        return part;
    }

    public String getSubpart() {
        return subpart;
    }

    public String getRegulation_id_eli() {
        return regulation_id_eli;
    }

    public String getRegulation_id_lovdata() {
        return regulation_id_lovdata;
    }

    public void printRequirementData() {
        if (this.subpart == null) {
            System.out.print(this.regulation + "/" + this.chapter + "/" + this.paragraph + "/" + this.part);
        } else {
            System.out.print(this.regulation + "/" + this.chapter + "/" + this.paragraph + "/" + this.part + "/" + this.subpart);
        }

    }
}
