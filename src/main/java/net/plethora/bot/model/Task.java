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
    @Field("task")
    private String task;
    @Field("solution")
    private String solution;


    public Task(String task, String solution) {
        this.task = task;
        this.solution = solution;
    }

    public Task() {
    }

}