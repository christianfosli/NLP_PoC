package scope;

public class Requirement {
    String regulation;
    String chapter;
    String paragraph;
    String part;
    String subpart;

    public Requirement(String regulation, String chapter, String paragraph, String part, String subpart) {
        this.regulation = regulation;
        this.chapter = chapter;
        this.paragraph = paragraph;
        this.part = part;
        this.subpart = subpart;
    }

    public Requirement(String regulation, String chapter, String paragraph, String part) {
        this.regulation = regulation;
        this.chapter = chapter;
        this.paragraph = paragraph;
        this.part = part;
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

    public void printRequirementData() {
        if (this.subpart == null) {
            System.out.print(this.regulation + "/" + this.chapter + "/" + this.paragraph + "/" + this.part);
        } else {
            System.out.print(this.regulation + "/" + this.chapter + "/" + this.paragraph + "/" + this.part + "/" + this.subpart);
        }

    }
}
