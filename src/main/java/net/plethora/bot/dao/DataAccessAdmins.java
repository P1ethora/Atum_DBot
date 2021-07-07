package net.plethora.bot.dao;

import net.plethora.bot.dao.repo.PostRepositoryAdmins;
import net.plethora.bot.model.UserControl;
import org.springframework.stereotype.Component;

@Component
public class DataAccessAdmins {

    private final PostRepositoryAdmins postRepositoryAdmins;

    public DataAccessAdmins(PostRepositoryAdmins postRepositoryAdmins) {
        this.postRepositoryAdmins = postRepositoryAdmins;
    }

    public UserControl findByLoginAndPassword(String login, String password) {
        return postRepositoryAdmins.findByLoginAndPassword(login, password);
    }

}