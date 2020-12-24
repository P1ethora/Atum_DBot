package net.plethora.bot.dao;

import net.plethora.bot.dao.repo.PostRepositoryAdmins;
import org.springframework.stereotype.Component;

@Component
public class DataAccessAdmins {

    private final PostRepositoryAdmins postRepositoryAdmins;

    public DataAccessAdmins(PostRepositoryAdmins postRepositoryAdmins) {
        this.postRepositoryAdmins = postRepositoryAdmins;
    }
}