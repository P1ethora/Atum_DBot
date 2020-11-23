package net.plethora.bot.dao;

import net.plethora.bot.model.User;
import net.plethora.bot.repo.PostRepositoryUser;
import org.springframework.stereotype.Component;


@Component
public class DataAccessUser {

    private PostRepositoryUser postRepositoryUser;

    public DataAccessUser(PostRepositoryUser postRepositoryUser) {
        this.postRepositoryUser = postRepositoryUser;
    }

    public User findUser(int idUser) {
        return postRepositoryUser.findByIdUser(idUser);
    }

    public User findUserByName(String userName){
        return postRepositoryUser.findByUserName(userName);
    }

    public void addUser(User user){
        postRepositoryUser.save(user);
    }

    public void editUser(User user, String[] newArray) {
        User oldUser = postRepositoryUser.findById(user.getId()).orElseThrow(() -> new IllegalStateException("missing"));

        oldUser.setReceivedTasks(newArray);
        postRepositoryUser.save(oldUser);
    }
}