package net.plethora.bot.botapi.handler.jobhandler.parsers;

import net.plethora.bot.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VacancyFormatBuilder {

    public StringBuilder getBuildTextVacancy(Vacancy vacancy) {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("<b>").append(vacancy.getTitle()).append("</b>").append("\n"); //Название

        stringBuilder.append(vacancy.getSalary()).append("\n");                           //Зарплата

        stringBuilder.append("<a href=\"")
                .append(vacancy.getUrlCompany())
                .append("\">")
                .append(vacancy.getCompany())
                .append("</a>")
                .append("\n");                                                              //гиперссылка html

        if (vacancy.getAddress() != null) {
            stringBuilder.append(vacancy.getAddress()).append("\n");                       //Адрес
        }
        if (vacancy.getExperience() != null) {
            stringBuilder.append("<b>Требуемый опыт: </b>").append(vacancy.getExperience()).append("\n"); //опыт
        }
        if (vacancy.getEmployment() != null) {
            stringBuilder.append(vacancy.getEmployment()).append("\n");                              //занятость
        }

        stringBuilder.append("<b>Ключевые навыки:</b>").append("\n");
        if (vacancy.getKeySkills() != null) {                                                      //перечисление ключевых навыков
            for (String skill : vacancy.getKeySkills()) {
                stringBuilder.append(skill).append("\n");
            }
        }

        return stringBuilder;
    }
}