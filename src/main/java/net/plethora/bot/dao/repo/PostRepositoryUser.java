package net.plethora.bot.dao.repo;

import net.plethora.bot.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepositoryUser extends MongoRepository<User, String> {

    public User findByIdUser(int idUser);

}