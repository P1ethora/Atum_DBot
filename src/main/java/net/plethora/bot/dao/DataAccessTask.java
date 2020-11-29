package net.plethora.bot.dao;

import lombok.Getter;
import net.plethora.bot.model.Task;
import net.plethora.bot.dao.repo.PostRepositoryTask;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Component
public class DataAccessTask {

    private final PostRepositoryTask postRepositoryTask;

    public DataAccessTask(PostRepositoryTask postRepositoryTask) {
        this.postRepositoryTask = postRepositoryTask;
    }

    public List<Task> handleRequest(String subject) {
        return postRepositoryTask.findBySubject(subject);
    }

    public Task findById(String id){
       return postRepositoryTask.findById(id).orElseThrow(() -> new IllegalStateException("Task with id " +id + " not found"));
    }
}
