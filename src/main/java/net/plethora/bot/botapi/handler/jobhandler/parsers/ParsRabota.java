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
    public List<Vacancy> parse(long idChat, int code, String period) {

        List<Vacancy> list = new ArrayList<>();             //Его заполним вакансиями ниже

        String linePeriod = checkPeriod(period);             //Получаем кусочек ссылки согласно периоду
        String connection = webAddressRabota + "search/vacancy?" +
                linePeriod + "area=" + code + "&text=" +
                text + "&enable_snippets=true";                //По это ссылке будем подключаться к сайту

        List<String> urls = getUrlVacancy(connection); //получаем список ссылок вакансий

        for (String urlVacancy : urls) {          //подключение и парс страницы согласно url

            Document doc = Jsoup.connect(urlVacancy).get();//заходим на адрес вакансии

            Element title = doc.getElementsByAttributeValue("data-qa", "vacancy-title").first();              //заголовок вакансии
            Element salary = doc.getElementsByAttributeValue("class", "vacancy-salary").first();              //зарплата
            Element companyName = doc.getElementsByAttributeValue("data-qa", "vacancy-company-name").first(); //компания
            String urlCompanyName = webAddressRabota + companyName.attr("href");           //ссылка на компанию в сайте
            Element address = doc.getElementsByAttributeValue("data-qa", "vacancy-view-raw-address").first();    //адрес
//          String logo = doc.getElementsByAttributeValue("class", "vacancy-company-logo").attr("src");        //логотип картинка
            Element request = doc.getElementsByAttributeValue("data-qa", "vacancy-response-link-top").first();    //откликнуться
            String requestLink = webAddressRabota + request.attr("href");                                //ссылка откл

            Element blockDescription = doc.getElementsByAttributeValue("class", "vacancy-description").first();   //блок описание

            Element expEmp = blockDescription.getElementsByAttributeValue("class", "bloko-gap bloko-gap_bottom").first();  //участок опыт-занятость

            Element experience = expEmp.getElementsByAttributeValue("data-qa", "vacancy-experience").first();           //требуемый опыт
            Element employment = expEmp.getElementsByAttributeValue("data-qa", "vacancy-view-employment-mode").first(); //занятость
            Elements mainSkills = blockDescription.getElementsByAttributeValue("class", "bloko-tag__section bloko-tag__section_text");//ключевые навыки
//          Element data =doc.getElementsByAttributeValue("class","bloko-column bloko-column_xs-4 bloko-column_s-8 bloko-column_m-8 bloko-column_l-8vacancy-creation-time").first();
            //TODO: не могу вытянуть дату
//          Element vacancySection = blockDescription.getElementsByAttributeValue("class","vacancy-section").first(); //полное описание

            String[] skills = new String[mainSkills.size()];
            for (int i = 0; i < mainSkills.size(); i++) {
                skills[i] = mainSkills.get(0).text();
            }

            Vacancy vacancy = new Vacancy();
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
        ArrayList<String> vacancy = new ArrayList<>();
//TODO не всегд jsoup может найти список вакансий
        for (int i = 0; i < 10; i++) {  //Костыль на 10 попыток
            System.out.println("Круг " + i);
            Document doc = Jsoup.connect(connection).get();
            System.out.println("Подключился к " + connection);
            Element tabl = doc.getElementsByAttributeValue("class", "vacancy-serp").first();  //получил таблицу
            Elements elements = tabl.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy");

            for (Element el : elements) {

                Element title = el.getElementsByAttributeValue("class", "vacancy-serp-item__row vacancy-serp-item__row_header").first();
                Element name = title.getElementsByAttributeValue("class", "vacancy-serp-item__info").first();
                Element link = name.getElementsByAttributeValue("class", "bloko-link HH-LinkModifier").first();
                String url = link.attr("href");  //получил саму ссылку
                vacancy.add(url);
            }
            if (vacancy.size() != 0) {
                System.out.println("Передаю список ссылок вакансий из " + vacancy.size());
                return vacancy;
            }
        }
        System.out.println("Передаю список ссылок вакансий из " + vacancy.size());
        return vacancy;
    }

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