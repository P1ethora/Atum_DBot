package net.plethora.bot.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
public class Vacancy {

    private int id;
   // private String area;
   // private String period;
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

    public Vacancy() { }

 @Override
 public String toString() {
  return "Vacancy{" +
          "id=" + id +
          ", title='" + title + '\'' +
          ", address='" + address + '\'' +
          ", salary='" + salary + '\'' +
          ", company='" + company + '\'' +
          ", UrlCompany='" + UrlCompany + '\'' +
          ", experience='" + experience + '\'' +
          ", employment='" + employment + '\'' +
          ", description='" + description + '\'' +
          ", keySkills=" + Arrays.toString(keySkills) +
          ", urlRespond='" + urlRespond + '\'' +
          ", urlVacancy='" + urlVacancy + '\'' +
          '}';
 }
}

