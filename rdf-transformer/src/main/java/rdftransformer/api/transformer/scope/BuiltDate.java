package rdftransformer.api.transformer.scope;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.vocabulary.SHACL;
import rdftransformer.api.transformer.ottr.OTTRUtils;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class BuiltDate {

    Requirement requirement;
    String context, separator, value1, value2;
    IRI constraint = SHACL.CONSTRAINT_COMPONENT;

    String subject;

    public BuiltDate(Requirement r, String c, String s, String v1, String v2) {
        this.requirement = r;
        this.context = c.toLowerCase();
        this.separator = s;
        this.value1 = getLocalDate(translateDate(v1)).toString();
        this.value2 = getLocalDate(translateDate(v2)).toString();

        this.subject = "PS_BuiltDate_" + value1 + "_to_" + value2;
        this.subject = this.subject.replace("-", "");
    }

    public BuiltDate(Requirement r, String c, String v1) {
        this.requirement = r;
        this.context = c.toLowerCase();
        this.value1 = getLocalDate(translateDate(v1)).toString();

        setConstraint(c);
        this.subject = this.subject.replace("-", "");

    }

    public Requirement getRequirement() {
        return requirement;
    }

    public String getContext() {
        return context;
    }

    public String getSeparator() {
        return separator;
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

    public String datePrefixNo() {
        String prefix = this.context;
        switch (prefix) {
            case "before":
                prefix = "før"; break;
            case "after":
                prefix = "etter"; break;
            case "between":
                prefix = "mellom"; break;
        }
        return prefix;
    }

    public String datePrefixEn() {
        String prefix = this.context;
        switch (prefix) {
            case "før":
                prefix = "before"; break;
            case "etter":
                prefix = "after"; break;
            case "mellom":
                prefix = "between"; break;
        }
        return prefix;
    }

    public void printInformation() {
        requirement.printRequirementData();
        if (this.value2 == null) {
            System.out.print(": " + this.context + " " + this.value1);
        } else {
            System.out.print(": " + this.context + " " + this.value1 + " " + this.separator + " " + this.value2);
        }
    }

    private void setConstraint(String c) {
        if (c.equals("før") || c.equals("before")) {
            this.constraint = SHACL.MAX_EXCLUSIVE;
            this.subject = "PS_BuiltDate_b" + value1;
        }
        if (c.equals("etter") || c.equals("after")) {
            this.constraint = SHACL.MIN_INCLUSIVE;
            this.subject = "PS_BuiltDate_a" + value1;
        }
    }

    private LocalDate getLocalDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH);
        return LocalDate.parse(dateStr, formatter);
    }

    private String translateDate(String date) {
        date = date.replace(".", "").trim().toLowerCase();

        String[] tmp = date.split(" ");

        String day = "", month = "", year = "";

        if (tmp.length == 3) {
            day = tmp[0];
            month = tmp[1];
            year = tmp[2];
        } if (tmp.length == 1 && (tmp[0].length() == 4) ) {
            if (this.value1 != null) {
                String[] split = value1.split("-");
                day = split[2];
                int m = Integer.parseInt(split[1]);
                month = Month.of(m).getDisplayName(TextStyle.FULL_STANDALONE, Locale.ENGLISH);
                year = tmp[0];
            }
        }

        switch (month) {
            case "januar":
                month = "january"; break;
            case "februar":
                month = "february"; break;
            case "mars":
                month = "march"; break;
            case "mai":
                month = "may"; break;
            case "juni":
                month = "june"; break;
            case "juli":
                month = "july"; break;
            case "oktober":
                month = "october"; break;
            case "desember":
                month = "december"; break;
            default:
                month = month;
        }

        date = day + " " + month.substring(0, 1).toUpperCase() + month.substring(1) + " " + year;

        return date;
    }

    public String getOTTRInstance() {
        return OTTRUtils.getOTTRInstance(this.constraint, this.subject, this.value1, this.value2);
    }
}
