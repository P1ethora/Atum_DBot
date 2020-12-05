package net.plethora.bot.dao.repo;

import net.plethora.bot.model.material.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepositoryTask extends MongoRepository<Task, String> {

    public List<Task> findBySubject(String subject);

}