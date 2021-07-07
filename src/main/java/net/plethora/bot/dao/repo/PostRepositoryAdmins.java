package net.plethora.bot.dao.repo;

import net.plethora.bot.model.UserControl;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepositoryAdmins extends MongoRepository<UserControl, String> {

    public UserControl findByLoginAndPassword(String login, String password);

}
