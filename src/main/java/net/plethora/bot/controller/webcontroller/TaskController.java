package net.plethora.bot.controller.webcontroller;

import net.plethora.bot.dao.DataAccessMaterialTask;
import net.plethora.bot.model.material.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@Controller
public class TaskController {

    @Autowired
    private DataAccessMaterialTask dataAccessMaterialTask;

    @GetMapping("/task")
    public String task(Model model) {
        List<Task> list = dataAccessMaterialTask.findAll();
        model.addAttribute("listSubject", getSubjectList(list));
        return "task";
    }

    @GetMapping("/task/{sub}")
    public String useSubj(@PathVariable("sub") String sub, Model model) {
        List<Task> list = dataAccessMaterialTask.findAll();
        List<Task> tasks = new ArrayList<>();
        for (Task task : list) {
            if (task.getSubject().equals(sub)) {
                tasks.add(task);
            }
        }
        model.addAttribute("tasks", tasks);
        model.addAttribute("listSubject", getSubjectList(list));
        return "task";
    }

    @GetMapping("/task/{sub}/{id}")
    public String getTask(@PathVariable("sub") String sub, @PathVariable("id") String id, Model model) {
        Task taskObj = dataAccessMaterialTask.findById(id);
        List<Task> list = dataAccessMaterialTask.findAll();

        List<Task> tasks = new ArrayList<>();
        for (Task task : list) {
            if (task.getSubject().equals(sub)) {
                tasks.add(task);
            }
        }

        model.addAttribute("problem", taskObj.getProblem());
        model.addAttribute("idTask", taskObj.getId());
        model.addAttribute("urlSolution", taskObj.getSolution());
        model.addAttribute("nameTask", taskObj.getFileName());
        model.addAttribute("subjectTask", taskObj.getSubject());
        model.addAttribute("tasks", tasks);
        model.addAttribute("listSubject", getSubjectList(list));
        return "task";
    }

    private List<String> getSubjectList(List<Task> list) {
        List<String> listSubject = new ArrayList<>();
        for (Task task : list) {
            String subj = task.getSubject();
            if (listSubject.size() > 0) {
                boolean check = false;
                for (String str : listSubject) {
                    if (str.equals(subj)) {
                        check = true;
                        break;
                    }
                }
                if (!check) {
                    listSubject.add(subj);
                }
            } else {
                listSubject.add(subj);
            }
        }
        return listSubject;
    }
}