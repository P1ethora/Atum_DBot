package net.plethora.bot.dao;

import net.plethora.bot.botapi.state.BotState;
import net.plethora.bot.botapi.state.SubState;
import net.plethora.bot.model.UserTelegram;
import net.plethora.bot.dao.repo.PostRepositoryUser;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class DataAccessUser {

    private PostRepositoryUser postRepositoryUser;

    public DataAccessUser(PostRepositoryUser postRepositoryUser) {
        this.postRepositoryUser = postRepositoryUser;
    }

    public UserTelegram findUser(int idUser) {
        return postRepositoryUser.findByIdUser(idUser);
    }

    public void addUser(UserTelegram userTelegram) {
        postRepositoryUser.save(userTelegram);
    }

    public void editUser(UserTelegram userTelegram, BotState botState) {
        UserTelegram oldUserTelegram = postRepositoryUser.findById(userTelegram.getId())
                .orElseThrow(() -> new IllegalStateException("User with id " + userTelegram.getId() + "not found"));
        oldUserTelegram.setState(botState);
        postRepositoryUser.save(oldUserTelegram);
    }

    public void editUser(UserTelegram userTelegram, SubState subState) {
        UserTelegram oldUserTelegram = postRepositoryUser.findById(userTelegram.getId())
                .orElseThrow(() -> new IllegalStateException("User with id " + userTelegram.getId() + "not found"));
        oldUserTelegram.setSubState(subState);
        postRepositoryUser.save(oldUserTelegram);
    }

    public List<UserTelegram> findAll() {
        return postRepositoryUser.findAll();
    }

}