package net.plethora.bot.botapi.parsers;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.IOException;
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
            case "3 дня":
                linePeriod = THREE_DAY;
                break;
            case "сутки":
                linePeriod = DAY;
                break;
            default:
                linePeriod = WEEK;
        }
        String connection = webAddressRabota + "search/vacancy?" + linePeriod + "area=" + code + "&fromSearchLine=true&st=searchVacancy&text=" + text;
        StringBuilder stringBuilder = new StringBuilder();
        List<SendMessage> listMessage = new ArrayList<>();   //список на отправку
        int numberVacancy = getUrlVacancy(connection).size();  //количество вакансий
        int countVacGlobal = 0; //счетчик вакансий
        int countVac = 0;  //счетчик вакансий до 5
        int countMsg = 0;  //счетчик сообщени

        int fullMessage = numberVacancy / 5;  //количество полных сообщений

        for (String urlVacancy : getUrlVacancy(connection)) {          //подключение и парс страницы согласно url

            Document doc = Jsoup.connect(urlVacancy).get();                                           //заходим на адрес вакансии
            Element title = doc.getElementsByAttributeValue("data-qa", "vacancy-title").first();              //заголовок вакансии
            Element salary = doc.getElementsByAttributeValue("class", "vacancy-salary").first();              //зарплата
            Element companyName = doc.getElementsByAttributeValue("data-qa", "vacancy-company-name").first(); //компания
            String urlCompanyName = webAddressRabota + companyName.attr("href");           //ссылка на компанию в сайте
            Element address = doc.getElementsByAttributeValue("data-qa", "vacancy-view-location").first();    //адрес
            String logo = doc.getElementsByAttributeValue("class", "vacancy-company-logo")
                    .attr("src");                                                         //логотип картинка
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
                countMsg++;
                countVac = 0;
                stringBuilder.delete(0, stringBuilder.length());
            }
        }
        return listMessage;
    }

    private List<String> getUrlVacancy(String connection) throws IOException {
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

    private byte[] logoHandler() {
        //TODO Скачать картинку и тут обработать ее map
        byte[] bytes = new byte[0];

        return bytes;
    }

//    public void downloadFiles(String strURL, String strPath, int buffSize) {
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