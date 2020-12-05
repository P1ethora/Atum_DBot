package net.plethora.bot.model.systemmodel;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Хранятся у пользователя
 * Сохраняют состояние каждой темы задачи и её последего варианта
 */
@Getter
@Setter
@Document(collection = "save_cell")
public class SaveCell {

    @Id
    private String id;
    private long chatId;
    private String saveId;
    private String saveSubject;

    public SaveCell(long chatId, String saveId, String saveSubject) {
        this.chatId = chatId;
        this.saveId = saveId;
        this.saveSubject = saveSubject;
    }

    public SaveCell(){}

    public long getChatId() {
        return chatId;
    }
}