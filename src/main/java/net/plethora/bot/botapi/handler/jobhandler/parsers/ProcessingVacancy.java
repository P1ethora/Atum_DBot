package net.plethora.bot.botapi.handler.jobhandler.parsers;

import jdk.nashorn.internal.runtime.Context;
import net.plethora.bot.cache.CacheVacancySearchUser;
import net.plethora.bot.model.Vacancy;
import net.plethora.bot.model.systemmodel.SaveVacancyCell;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProcessingVacancy {

    private SaveVacancyCell saveVacancyCell;
    private CacheVacancySearchUser cacheVacancySearchUser;

public Vacancy createVacancy(String area, String period, String title, String address, String salary, String companyName,
                             String urlCompany,String experience,String employment, String description, String[] keySkills, String urlRespond,
                             String urlVacancy){
    Vacancy vacancy = new Vacancy();

    vacancy.setTitle(title); //заголовок вакансии
    vacancy.setAddress(address); //фактически адресс
    vacancy.setSalary(salary); //зарплата
    vacancy.setCompany(companyName); // имя компании
    vacancy.setUrlCompany(urlCompany);//интернет адресс компании
    vacancy.setExperience(experience);// опыт
    vacancy.setEmployment(employment);
    vacancy.setDescription(description); //описание
    vacancy.setKeySkills(keySkills);   //ключевые навыки
    vacancy.setUrlRespond(urlRespond);  //откликнуться
    vacancy.setUrlVacancy(urlVacancy);  //перейти к вакансии
    return vacancy;
}

public ProcessingVacancy createSaveVacancyCell(long chatId,String area, String period, List<Vacancy> listVacancy){
    saveVacancyCell = new SaveVacancyCell();
    saveVacancyCell.setChatId(chatId);
    saveVacancyCell.setArea(area);
    saveVacancyCell.setPeriod(period);
    saveVacancyCell.setVacancies(listVacancy.toArray(new Vacancy[0]));
    return this;
}
public void addSaveVacancyToCache(){
    if(saveVacancyCell==null) {
        new Context.ThrowErrorManager().error("SaveVacancyCell is null, cause \"createSaveVacancyCell(...)\"");
    } else
    cacheVacancySearchUser.getCache().add(saveVacancyCell);
}

}