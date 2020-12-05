package net.plethora.bot.dao;

import lombok.Getter;
import lombok.Setter;
import net.plethora.bot.model.material.Task;
import net.plethora.bot.dao.repo.PostRepositoryTask;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
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
}
