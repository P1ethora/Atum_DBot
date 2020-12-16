package net.plethora.bot.model.systemmodel;

import lombok.Getter;
import lombok.Setter;
import net.plethora.bot.model.Vacancy;

@Getter
@Setter
public class SaveVacancyCell {

   private int id;
    private long chatId;
    private String area;
    private String period;
    private Vacancy[] vacancies;
    int saveIdVacancy;

    public SaveVacancyCell(int id,long chatId,String area, String period, Vacancy[] vacancies, int saveIdVacancy) {
        this.id = id;
        this.chatId = chatId;
        this.area = area;
        this.period = period;
        this.vacancies = vacancies;
        this.saveIdVacancy = saveIdVacancy;
    }

    public SaveVacancyCell() {
    }
}