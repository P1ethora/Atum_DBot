package net.plethora.bot.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Document(collection = "users")
public class User {

    @Id
    private String id;
    private String firstName;
    private String LastName;
    private String userName;
    private String[] receivedTasks;

    public User(String id, String firstName, String lastName, String userName, String[] receivedTasks) {
        this.id = id;
        this.firstName = firstName;
        LastName = lastName;
        this.userName = userName;
        this.receivedTasks = receivedTasks;
    }

    public User(){}

}