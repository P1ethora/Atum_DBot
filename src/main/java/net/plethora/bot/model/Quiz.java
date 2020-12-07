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
    private PollOption[] pollOptions;
    private int answer;

    public Quiz(String question, String[] options, PollOption[] pollOptions, int answer) {
        this.question = question;
        this.options = options;
        this.pollOptions = pollOptions;
        this.answer = answer;
    }

    public Quiz() {
    }
}
