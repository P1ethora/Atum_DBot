package net.plethora.bot.dao;

import net.plethora.bot.model.material.Task;
import net.plethora.bot.dao.repo.PostRepositoryTask;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataAccessMaterialTask implements DataAccessMaterial {

    private final PostRepositoryTask postRepositoryTask;

    public DataAccessMaterialTask(PostRepositoryTask postRepositoryTask) {
        this.postRepositoryTask = postRepositoryTask;
    }

    public List<Task> findBySubject(String subject) {
        return postRepositoryTask.findBySubject(subject);
    }

    public Task findById(String id){
       return postRepositoryTask.findById(id).orElseThrow(() -> new IllegalStateException("Task with id " +id + " not found"));
    }

    public List<Task> findAll() {
        return postRepositoryTask.findAll();
    }

    public void delete(String id) {
        postRepositoryTask.deleteById(id);
    }
}
