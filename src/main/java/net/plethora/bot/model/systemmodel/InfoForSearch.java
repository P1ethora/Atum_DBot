package net.plethora.bot.model.systemmodel;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class InfoForSearch {

    private int id;
    private long chatId;
    private String area;
    private String period;
    private int idVacancy;

    //private int positionCache;


    public InfoForSearch(int id, long chatId, String area, String period) {
        this.id = id;
        this.chatId = chatId;
        this.area = area;
        this.period = period;
    }
    public InfoForSearch(int id, long chatId, String area, String period, int idVacancy) {
        this.id = id;
        this.chatId = chatId;
        this.area = area;
        this.period = period;
        this.idVacancy = idVacancy;
    }
    public InfoForSearch(int id, String area, String period) {
        this.id = id;
        this.area = area;
        this.period = period;
    }

    public InfoForSearch(){}
}