package net.plethora.bot.botapi.parsers;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Component
public class ParsRabota {

    private final String webAddressRabota = "https://rabota.by/";

    private final String MONTH = "search_period=30&clusters=true&";
    private final String WEEK = "search_period=7&clusters=true&";
    private final String THREE_DAY = "search_period=3&clusters=true&";
    private final String DAY = "search_period=1&clusters=true&";
    private String text = "Java";

    public List<SendMessage> vacancyListMsg(long idChat, int code, String period) {
        return parse(idChat, code, period);
    }

    @SneakyThrows
    private List<SendMessage> parse(long idChat, int code, String period) {
        //TODO: возможно стоит перенести или в метод или в другой класс
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
        //https://rabota.by/search/vacancy?area=1006&text=Java&enable_snippets=true
        //https://rabota.by/search/vacancy?search_period=3&clusters=true&area=1003&text=Java&enable_snippets=true
        String connection = webAddressRabota + "search/vacancy?" + linePeriod + "area=" + code +"&text=" + text +"&enable_snippets=true";
        //String connection = webAddressRabota + "search/vacancy?" + linePeriod + "area=" + code + "&fromSearchLine=true&st=searchVacancy&text=" + text;
        StringBuilder stringBuilder = new StringBuilder();
        List<SendMessage> listMessage = new ArrayList<>();   //список на отправку
        List<String> urls = getUrlVacancy(connection);
        int numberVacancy = urls.size();  //количество вакансий
        int countVacGlobal = 0; //счетчик вакансий
        int countVac = 0;  //счетчик вакансий до 5
        int countMsg = 0;  //счетчик сообщени

        int fullMessage = numberVacancy / 5;  //количество полных сообщений

        for (String urlVacancy : urls) {          //подключение и парс страницы согласно url

            Document doc = Jsoup.connect(urlVacancy).get();//заходим на адрес вакансии
            System.out.println("Подключился, обрабатываю вакансию");
            Element title = doc.getElementsByAttributeValue("data-qa", "vacancy-title").first();              //заголовок вакансии
            Element salary = doc.getElementsByAttributeValue("class", "vacancy-salary").first();              //зарплата
            Element companyName = doc.getElementsByAttributeValue("data-qa", "vacancy-company-name").first(); //компания
            String urlCompanyName = webAddressRabota + companyName.attr("href");           //ссылка на компанию в сайте
            Element address = doc.getElementsByAttributeValue("data-qa", "vacancy-view-location").first();    //адрес
            //TODO не могу понять как отправить текст и картинку вместе одним сообщением. У сендФОто есть поле caption -описание но из за ограничения 30 сообщений в минуту скорее всего реализация невозможна
//            String logo = doc.getElementsByAttributeValue("class", "vacancy-company-logo")
//                    .attr("src");                                                         //логотип картинка
            Element request = doc.getElementsByAttributeValue("data-qa", "vacancy-response-link-top").first();    //откликнуться
            String requestLink = webAddressRabota + request.attr("href");                                //ссылка откл

            Element blockDescription = doc.getElementsByAttributeValue("class", "vacancy-description").first();   //блок описание

            Element expEmp = blockDescription.getElementsByAttributeValue("class", "bloko-gap bloko-gap_bottom").first();  //участок опыт-занятость
            Element experience = expEmp.getElementsByAttributeValue("data-qa", "vacancy-experience").first();           //требуемый опыт
            Element employment = expEmp.getElementsByAttributeValue("data-qa", "vacancy-view-employment-mode").first(); //занятость
            Elements mainSkills = blockDescription.getElementsByAttributeValue("class", "bloko-tag__section bloko-tag__section_text");//ключевые навыки
            // Element data =doc.getElementsByAttributeValue("class","bloko-column bloko-column_xs-4 bloko-column_s-8 bloko-column_m-8 bloko-column_l-8vacancy-creation-time").first();
            //TODO: не могу вытянуть дату
            //Element vacancySection = blockDescription.getElementsByAttributeValue("class","vacancy-section").first(); //полное описание
            stringBuilder.append("<b>").append(title.text()).append("</b>").append("\n");
            stringBuilder.append(salary.text()).append("\n");
            //TODO в html длинновато както попробовать через маркдоун
            stringBuilder.append("<a href=\"").append(urlCompanyName).append("\">").append(companyName.text()).append("</a>").append("\n"); //гиперссылка html
            stringBuilder.append(address.text()).append("\n");
            stringBuilder.append("<b>Требуемый опыт: </b>").append(experience.text()).append("\n");
            stringBuilder.append(employment.text()).append("\n");
            //stringBuilder.append(vacancySection.text()).append("\n"); //слишком много
            stringBuilder.append("<b>Ключевые навыки:</b>").append("\n");

            for (Element element : mainSkills) {
                stringBuilder.append(element.text()).append("\n");
            }
            stringBuilder.append("<a href=\"").append(requestLink).append("\">").append("Откликнуться").append("</a>").append("                ")
                    .append("<a href=\"").append(urlVacancy).append("\">").append("Подробнее...").append("</a>").append("\n");  //гиперссылка html
//stringBuilder.append(data.text()).append("\n");
            countVac++;
            countVacGlobal++;

            if (countVac != 5 && countMsg != fullMessage || countMsg == fullMessage && countVacGlobal != numberVacancy) {
                stringBuilder.append("--------------------------------------------------------------").append("\n"); //раздилитель вакансий
            }
            if (countVac == 5 || countMsg == fullMessage && countVacGlobal == numberVacancy) {
                listMessage.add(new SendMessage(idChat, stringBuilder.toString()).enableHtml(true).disableWebPagePreview());  //включаем обработку html и выключаем представление сайта
                System.out.println("Сообщение добавлено в лист отправки");
                countMsg++;
                countVac = 0;
                stringBuilder.delete(0, stringBuilder.length());
                System.out.println("Стринг билдер очищен");
                //stringBuilder = new StringBuilder();
            }
        }
        System.out.println("Передаю готовый лист сообщений из " + listMessage.size());
        return listMessage;
    }

    private List<String> getUrlVacancy(String connection) throws IOException {
        ArrayList<String> vacancy = new ArrayList<>();

        for (int i = 0; i < 10; i++) {  //Костыль на 5 попыток
            System.out.println("Круг " + i);
            Document doc = Jsoup.connect(connection).get();
            System.out.println("Подключился к " + connection);
            Element tabl = doc.getElementsByAttributeValue("class", "vacancy-serp").first();  //получил таблицу
            Elements elements = tabl.getElementsByAttributeValue("data-qa", "vacancy-serp__vacancy");

            //Elements els = tabl.get(0).getElementsByAttributeValue("class", "bloko-link HH-LinkModifier"); //блок со ссылкой

            for (Element el : elements) {

                Element title = el.getElementsByAttributeValue("class", "vacancy-serp-item__row vacancy-serp-item__row_header").first();
                Element name = title.getElementsByAttributeValue("class", "vacancy-serp-item__info").first();
                Element link = name.getElementsByAttributeValue("class", "bloko-link HH-LinkModifier").first();
                String url = link.attr("href");  //получил саму ссылку
                vacancy.add(url);
                System.out.println(url);
            }
            System.out.println("Передаю список ссылок вакансий из " + vacancy.size());
           if(vacancy.size()!=0){return vacancy;}
        }
        System.out.println("Передаю список ссылок вакансий из " + vacancy.size());
        return vacancy;
    }
//Загрузка картинки
//    public void downloadFiles(String strURL, String strPath, int buffSize) {
    //создать тут File file  ретерн готовыйй
//        try {
//            URL connection = new URL(strURL);
//            HttpURLConnection urlconn;
//            urlconn = (HttpURLConnection) connection.openConnection();
//            urlconn.setRequestMethod("GET");
//            urlconn.connect();
//            InputStream in = null;
//            in = urlconn.getInputStream();
//            OutputStream writer = new FileOutputStream(strPath);
//            byte buffer[] = new byte[buffSize];
//            int c = in.read(buffer);
//            while (c > 0) {
//                writer.write(buffer, 0, c);
//                c = in.read(buffer);
//            }
//            writer.flush();
//            writer.close();
//            in.close();
//        } catch (IOException e) {
//            System.out.println(e);
//        }
//    }

}