package net.plethora.bot.dao;

import net.plethora.bot.model.User;
import net.plethora.bot.repo.PostRepositoryUser;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataAccessUser {

    private PostRepositoryUser postRepositoryUser;

    public DataAccessUser(PostRepositoryUser postRepositoryUser) {
        this.postRepositoryUser = postRepositoryUser;
    }

    public Optional<User> findUser(String id) {
        return postRepositoryUser.findById(id);
    }

    public User findUserByName(String userName){
        return postRepositoryUser.findByUserName(userName);
    }

    public void addUser(User user){
        postRepositoryUser.save(user);
    }

}
