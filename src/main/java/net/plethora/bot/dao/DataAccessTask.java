package net.plethora.bot.dao;

import net.plethora.bot.model.Task;
import net.plethora.bot.repo.PostRepositoryTask;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataAccessTask {

    private final PostRepositoryTask postRepositoryTask;

    public DataAccessTask(PostRepositoryTask postRepositoryTask) {
        this.postRepositoryTask = postRepositoryTask;
    }

    public List<Task> handleRequest(String subject) {
        return postRepositoryTask.findBySubject(subject);
    }
}
