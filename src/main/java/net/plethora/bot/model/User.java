package net.plethora.bot.model;

import lombok.Getter;
import lombok.Setter;
import net.plethora.bot.botapi.handler.handtask.SubjectTaskUser;
import net.plethora.bot.botapi.state.BotState;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private int idUser;
    private BotState state;
    //private Enum subState;
    private long idChat;  //совпадает с id пользователя возможно стоит убрать
    private String firstName;
    private String lastName;
    private String userName;
    private SubjectTaskUser[] subjectTask;

    public User(int idUser, String firstName, String lastName, String userName, SubjectTaskUser[] subjectTask) {
        this.idUser = idUser;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.subjectTask = subjectTask;
    }

    public User(){}

}