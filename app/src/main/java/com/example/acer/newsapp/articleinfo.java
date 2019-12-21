package com.example.acer.newsapp;


public class articleinfo {

    private String aTitle;
    private String aDate;
    private String aSection;
    private String aauthor;

    public articleinfo(String aTitle, String aDate, String aSection,String aauthor) {
        this.aTitle = aTitle;
        this.aDate = aDate;
        this.aSection = aSection;
        this.aauthor = aauthor;
    }

    public String getaTitle() {
        return aTitle;
    }

    public String getaDate() {
        return aDate;
    }

    public String getaSection() {
        return aSection;
    }

    public String getaauthor() {
        return aauthor;
    }
}
