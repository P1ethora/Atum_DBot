package net.plethora.bot.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.telegram.telegrambots.meta.api.objects.polls.PollOption;

@Getter
@Setter
@Document(collection = "quiz")
public class Quiz {

    @Id
    private String id;
    private String question;
    private String[] options;
    private int answer;

    public Quiz(String question, String[] options, int answer) {
        this.question = question;
        this.options = options;
        this.answer = answer;
    }

    public Quiz() {
    }
}