package net.plethora.bot.model;

import lombok.Getter;
import lombok.Setter;
import net.plethora.bot.botapi.state.BotState;
import net.plethora.bot.botapi.state.SubState;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@Document(collection = "users")
public class UserTelegram {

    @Id
    private String id;
    private int idUser;
    private BotState state;
    private SubState subState;
    private long idChat;  //совпадает с id пользователя возможно стоит убрать
    private String firstName;
    private String lastName;
    private String userName;
    private Date date;

    public UserTelegram(int idUser, String firstName, String lastName, String userName, Date date) {
        this.idUser = idUser;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.date = date;
    }

    public UserTelegram() {
    }

}