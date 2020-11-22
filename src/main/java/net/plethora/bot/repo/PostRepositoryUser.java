package net.plethora.bot.repo;

import net.plethora.bot.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepositoryUser extends MongoRepository<User,String> {


    public User findByUserName(String userName);

}
