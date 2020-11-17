package net.plethora.bot.botapi.parsers;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Component
public class ParsRabota {

private final String webAddressRabota ="https://rabota.by/";
private String area = "1003";  //код города на сайте
private String text = "Java";
private String connection = webAddressRabota +"search/vacancy?area="+area+"&fromSearchLine=true&st=searchVacancy&text=" + text;

public List<SendMessage> vacancyListMsg(long idChat,String area){
    this.area = area;
    return parse(idChat);
}

@SneakyThrows
    private List<SendMessage> parse(long idChat) {

    //StringBuilder stringBuilder = new StringBuilder();
    List<SendMessage> listMessage = new ArrayList<>();

    for(String urlVacancy : getUrlVacancy()){          //перебор страниц вакансий
        StringBuilder stringBuilder = new StringBuilder();
        Document doc = Jsoup.connect(urlVacancy).get();                                           //заходим на адрес вакансии
        Element title = doc.getElementsByAttributeValue("data-qa","vacancy-title").first();              //заголовок вакансии
        Element salary = doc.getElementsByAttributeValue("class","vacancy-salary").first();              //зарплата
        Element companyName = doc.getElementsByAttributeValue("data-qa","vacancy-company-name").first(); //компания
        String urlCompanyName =webAddressRabota + companyName.attr("href");           //ссылка на компанию в сайте
        Element address = doc.getElementsByAttributeValue("data-qa","vacancy-view-location").first();    //адрес
        String logo = doc.getElementsByAttributeValue("class","vacancy-company-logo")
                .attr("src");                                                         //логотип картинка
        Element request = doc.getElementsByAttributeValue("data-qa","vacancy-response-link-top").first();    //откликнуться
        String requestLink = webAddressRabota + request.attr("href");                                //ссылка откл

        Element blockDescription = doc.getElementsByAttributeValue("class","vacancy-description").first();   //блок описание

        Element expEmp = blockDescription.getElementsByAttributeValue("class","bloko-gap bloko-gap_bottom").first();  //участок опыт-занятость
        Element experience = expEmp.getElementsByAttributeValue("data-qa","vacancy-experience").first();           //требуемый опыт
        Element employment = expEmp.getElementsByAttributeValue("data-qa","vacancy-view-employment-mode").first(); //занятость
        Element vacancySection = blockDescription.getElementsByAttributeValue("class","vacancy-section").first(); //полное описание
//TODO проблема в ограничении на передачу символов в телеграм сообщении
        stringBuilder.append(title.text()).append("\n");
        stringBuilder.append(salary.text()).append("\n");
        //TODO в html длинновато както попробовать через маркдоун
        stringBuilder.append("<a href=\"").append(urlCompanyName).append("\">").append(companyName.text()).append("</a>").append("\n");
        stringBuilder.append(address.text()).append("\n");
        stringBuilder.append("Требуемый опыт: ").append(experience.text()).append("\n");
        stringBuilder.append(employment.text()).append("\n");
        //stringBuilder.append(vacancySection.text()).append("\n"); //слишком много текста
        stringBuilder.append("<a href=\"").append(requestLink).append("\">").append("Откликнуться").append("</a>");
        stringBuilder.append("\n");
        stringBuilder.append("\n");

        listMessage.add(new SendMessage(idChat,stringBuilder.toString()).enableHtml(true).disableWebPagePreview());
//        System.out.println(title.text());//
//        System.out.println(salary.text());//
//        System.out.println(companyName.text()+" " + urlCompanyName);//
//        System.out.println(address.text());//
//        System.out.println("Требуемый опыт: " + experience.text());//
//        System.out.println(employment.text());//
//        System.out.println(vacancySection.text());
//        System.out.println("-----------------------------------------------");
    }
    return listMessage;
}

    private List<String> getUrlVacancy() throws IOException {
        ArrayList<String> vacancy = new ArrayList<>();

        Document doc = Jsoup.connect(connection).get();
        Elements tabl = doc.getElementsByAttributeValue("class", "vacancy-serp");  //получил таблицу
        Elements els = tabl.get(0).getElementsByAttributeValue("class", "bloko-link HH-LinkModifier"); //блок со ссылкой

        for (Element el : els) {
            String url = el.attr("href");  //получил саму ссылку
            vacancy.add(url);
        }

        return vacancy;
    }

    private byte[] logoHandler(){
        //TODO Скачать картинку и тут обработать ее map
        byte[] bytes = new byte[0];

        return bytes;
    }

}