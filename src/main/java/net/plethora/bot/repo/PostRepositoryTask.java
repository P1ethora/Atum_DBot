package net.plethora.bot.repo;

import net.plethora.bot.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostRepositoryTask extends MongoRepository<Task,String> {
}
