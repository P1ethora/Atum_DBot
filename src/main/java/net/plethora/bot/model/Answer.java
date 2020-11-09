package net.plethora.bot.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Scanner;


@Data
@Document(collection = "answer")
public class Answer{

    @Id
    private String id;
    @Field("ask")
    private String[] ask;
    @Field("answer")
    private String answer;
    @Field("keyWords")
    private String[] keyWords;

    public Answer(String[] ask, String answer, String[] keyWords) {
        this.ask = ask;
        this.answer = answer;
        this.keyWords = keyWords;
    }

    public Answer(){}

}