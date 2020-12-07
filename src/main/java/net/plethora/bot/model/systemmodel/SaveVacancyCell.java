package net.plethora.bot.model.systemmodel;

import lombok.Getter;
import lombok.Setter;
import net.plethora.bot.model.Vacancy;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "save_cell_vacancy")
public class SaveVacancyCell {

    @Id
    private String id;
    private long chatId;
    private String area;
    private String period;
    private Vacancy[] vacancies;

    public SaveVacancyCell(long chatId, String area, String period, Vacancy[] vacancies) {
        this.chatId = chatId;
        this.area = area;
        this.period = period;
        this.vacancies = vacancies;
    }

    public SaveVacancyCell() {
    }
}