package net.plethora.bot.model.system;

import lombok.Getter;
import lombok.Setter;

/**
 * Хранятся у пользователя
 * Сохраняют состояние каждой темы задачи и её последего варианта
 */
@Getter
@Setter
public class SubjectTaskUser {

    private String idTask;
    private String subjectTask;

    public SubjectTaskUser(String idTask, String subjectTask) {
        this.idTask = idTask;
        this.subjectTask = subjectTask;
    }

    public String getIdTask() {
        return idTask;
    }

    public void setIdTask(String idTask) {
        this.idTask = idTask;
    }

    public String getSubjectTask() {
        return subjectTask;
    }

    public void setSubjectTask(String subjectTask) {
        this.subjectTask = subjectTask;
    }
}
