package net.plethora.bot.model.systemmodel;

import lombok.Getter;
import lombok.Setter;
import net.plethora.bot.model.Vacancy;

import java.util.Date;

@Getter
@Setter
public class SaveVacancyCell {

   private int id;
    private long chatId;
    private String area;
    private String period;
    private Vacancy[] vacancies;
    int saveIdVacancy;
    private Date dateDelete;

    public SaveVacancyCell(int id, long chatId, String area, String period, Vacancy[] vacancies, int saveIdVacancy, Date dateDelete) {
        this.id = id;
        this.chatId = chatId;
        this.area = area;
        this.period = period;
        this.vacancies = vacancies;
        this.saveIdVacancy = saveIdVacancy;
        this.dateDelete = dateDelete;
    }

    public SaveVacancyCell() {
    }
}