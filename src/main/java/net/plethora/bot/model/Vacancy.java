package net.plethora.bot.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Vacancy {

    private int id;
    private String title;
    private String address;
    private String salary;
    private String company;
    private String UrlCompany;
    private String experience;
    private String employment;
    private String description;
    private String[] keySkills;
    private String urlRespond;
    private String urlVacancy;

    public Vacancy() {
    }

}

