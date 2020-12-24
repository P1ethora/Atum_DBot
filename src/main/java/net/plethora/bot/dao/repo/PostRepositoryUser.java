package net.plethora.bot.dao.repo;

import net.plethora.bot.model.UserTelegram;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepositoryUser extends MongoRepository<UserTelegram, String> {

    public UserTelegram findByIdUser(int idUser);

}