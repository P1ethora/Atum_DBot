package net.plethora.bot.model.systemmodel;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "save_cell_quiz")
public class SaveCellQuiz {

    @Id
    private String id;
    private long chatId;
    private String[] saveIdQuiz;

    public SaveCellQuiz(long chatId, String[] saveIdQuiz) {
        this.id = id;
        this.chatId = chatId;
        this.saveIdQuiz = saveIdQuiz;
    }

    public SaveCellQuiz() {}
}