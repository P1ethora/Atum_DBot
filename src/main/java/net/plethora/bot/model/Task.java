package net.plethora.bot.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Data
@Document(collection = "tasks")
public class Task {

    @Id
    private String id;
    @Field("problem")
    private String problem;
    @Field("solution")
    private String solution;
    @Field("fileName")
    private String fileName;
    @Field("subject")
    private String subject;


    public Task(String problem, String solution,String fileName,String subject) {
        this.problem = problem;
        this.solution = solution;
        this.fileName = fileName;
        this.subject = subject;
    }

    public Task() {
    }

}