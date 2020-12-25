package net.plethora.bot.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "admins")
public class UserControl {

    @Id
    private String id;
    private String login;
    private String password;

    public UserControl(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public UserControl() {
    }

}
