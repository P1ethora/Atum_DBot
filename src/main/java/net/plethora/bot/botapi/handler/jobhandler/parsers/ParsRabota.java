package net.plethora.bot.botapi.handler.jobhandler.parsers;

import lombok.*;
import net.plethora.bot.model.Vacancy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ParsRabota {

    private final String webAddressRabota = "https://rabota.by/";

    private final String MONTH = "search_period=30&clusters=true&";
    private final String WEEK = "search_period=7&clusters=true&";
    private final String THREE_DAY = "search_period=3&clusters=true&";
    private final String DAY = "search_period=1&clusters=true&";
    private String text = "Java";

    @SneakyThrows
    public List<Vacancy> parse(int code, String period) {

        List<Vacancy> list = new ArrayList<>();             //Его заполним вакансиями ниже

        String linePeriod = checkPeriod(period);             //Получаем кусочек ссылки согласно периоду
        String connection = webAddressRabota + "search/vacancy?" +
                linePeriod + "area=" + code + "&text=" +
                text + "&enable_snippets=true";                //По это ссылке будем подключаться к сайту

        List<String> urls = getUrlVacancy(connection); //получаем список ссылок вакансий
int counter = -1;
        for (String urlVacancy : urls) {          //подключение и парс страницы согласно url
counter++;
            Document doc = Jsoup.connect(urlVacancy).get();//заходим на адрес вакансии

            Element title = doc.getElementsByAttributeValue("data-qa", "vacancy-title").first();              //заголовок вакансии
            Element salary = doc.getElementsByAttributeValue("class", "vacancy-salary").first();              //зарплата
            Element companyName = doc.getElementsByAttributeValue("data-qa", "vacancy-company-name").first(); //компания
            String urlCompanyName = webAddressRabota + companyName.attr("href");           //ссылка на компанию в сайте
            Element address = doc.getElementsByAttributeValue("data-qa", "vacancy-view-raw-address").first();    //адрес
            if(address==null) {
                address = doc.getElementsByAttributeValue("data-qa", "vacancy-view-location").first(); //если нулл, заглядываем в локацию
            }
//          String logo = doc.getElementsByAttributeValue("class", "vacancy-company-logo").attr("src");        //логотип картинка
            Element request = doc.getElementsByAttributeValue("data-qa", "vacancy-response-link-top").first();    //откликнуться
            String requestLink = webAddressRabota + request.attr("href");                                //ссылка откл

            Element blockDescription = doc.getElementsByAttributeValue("class", "vacancy-description").first();   //блок описание

            Element expEmp = blockDescription.getElementsByAttributeValue("class", "bloko-gap bloko-gap_bottom").first();  //участок опыт-занятость

            Element experience = expEmp.getElementsByAttributeValue("data-qa", "vacancy-experience").first();           //требуемый опыт
            Element employment = expEmp.getElementsByAttributeValue("data-qa", "vacancy-view-employment-mode").first(); //занятость
            Element tableSkills = doc.getElementsByAttributeValue("class", "bloko-tag-list").first();//ключевые навыки
            Elements allSkills = tableSkills.getElementsByAttributeValue("class","bloko-tag__section bloko-tag__section_text"); //вытягиваем навыки
//          Element data =doc.getElementsByAttributeValue("class","bloko-column bloko-column_xs-4 bloko-column_s-8 bloko-column_m-8 bloko-column_l-8vacancy-creation-time").first();
            //TODO: не могу вытянуть дату
//          Element vacancySection = blockDescription.getElementsByAttributeValue("class","vacancy-section").first(); //полное описание
            String[] skills = new String[allSkills.size()];
            for (int i = 0; i < allSkills.size(); i++) {
                skills[i] = allSkills.get(i).text();
            }

            Vacancy vacancy = new Vacancy();
            vacancy.setId(counter);
            vacancy.setTitle(title.text()); //заголовок вакансии
            vacancy.setAddress(address.text()); //фактически адресс
            vacancy.setSalary(salary.text()); //зарплата
            vacancy.setCompany(companyName.text()); // имя компании
            vacancy.setUrlCompany(urlCompanyName);//интернет адресс компании
            vacancy.setExperience(experience.text());// опыт
            vacancy.setEmployment(employment.text());
//          vacancy.setDescription(description); //описание
            vacancy.setKeySkills(skills);   //ключевые навыки
            vacancy.setUrlRespond(requestLink);  //откликнуться
            vacancy.setUrlVacancy(urlVacancy);  //перейти к вакансии

            list.add(vacancy);
        }

        return list;
    }

    private List<String> getUrlVacancy(String connection) throws IOException {
        ArrayList<String> vacancies = new ArrayList<>();
//TODO не всегд jsoup может найти список вакансий
        for (int i = 0; i < 10; i++) {  //Костыль на 10 попыток
            Document doc = Jsoup.connect(connection).get();
            Element table = doc.getElementsByAttributeValue("class", "vacancy-serp").first();  //получил таблицу
            Elements els = table.getElementsByAttributeValue("class", "bloko-link HH-LinkModifier HH-VacancySidebarTrigger-Link HH-VacancySidebarAnalytics-Link"); //блок со ссылкой

            for (Element el : els) { //перебор элементов
                String url = el.attr("href");  //получаем саму ссылку
                vacancies.add(url);
                System.out.println(url);
            }

            if (vacancies.size() != 0) {//Если нашлись элементы завершаем поиск
                break;
            }
        }
        System.out.println("Передаю список ссылок вакансий из " + vacancies.size());
        return vacancies;
    }

    /**
     * Определение участка для ссылки согласно заданному периоду
     * @param period от сообщения
     * @return участок ссылки
     */
    private String checkPeriod(String period) {
        String linePeriod;
        switch (period) {
            case "месяц":
                linePeriod = MONTH;
                break;
            case "три дня":
                linePeriod = THREE_DAY;
                break;
            case "сутки":
                linePeriod = DAY;
                break;
            default:
                linePeriod = WEEK;
        }
        return linePeriod;
    }
}