package net.plethora.bot.botapi.parsers;

import net.plethora.bot.cache.CacheVacancySearchUser;
import net.plethora.bot.model.Vacancy;
import net.plethora.bot.model.systemmodel.SaveVacancyCell;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProcessingVacancy {

    private SaveVacancyCell saveVacancyCell;
    private List<Vacancy> listVacancy;
    private CacheVacancySearchUser cacheVacancySearchUser;

public Vacancy createVacancy(String area, String period, String title, String address, String salary, String companyName,
                             String urlCompany,String experience, String description, String[] keySkills, String urlRespond,
                             String urlVacancy){
    Vacancy vacancy = new Vacancy();
    vacancy.setArea(area); // локация
    vacancy.setPeriod(period); //30, 7, 3, 1 суток
    vacancy.setTitle(title); //заголовок вакансии
    vacancy.setAddress(address); //фактически адресс
    vacancy.setSalary(salary); //зарплата
    vacancy.setCompany(companyName); // имя компании
    vacancy.setUrlCompany(urlCompany);//интернет адресс компании
    vacancy.setExperience(experience);// опыт
    vacancy.setDescription(description); //описание
    vacancy.setKeySkills(keySkills);   //ключевые навыки
    vacancy.setUrlRespond(urlRespond);  //откликнуться
    vacancy.setUrlVacancy(urlVacancy);  //перейти к вакансии
    return vacancy;
}
public void addVacancyToList(Vacancy vacancy){
    listVacancy.add(vacancy);
}
public void createSaveVacancyCell(long chatId,String area, String period){
    saveVacancyCell = new SaveVacancyCell();
    saveVacancyCell.setChatId(chatId);
    saveVacancyCell.setArea(area);
    saveVacancyCell.setPeriod(period);
    saveVacancyCell.setVacancies(listVacancy.toArray(new Vacancy[0]));
}
public void addSaveVacancyToCache(){
    cacheVacancySearchUser.getCache().add(saveVacancyCell);
}

}