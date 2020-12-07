package net.plethora.bot.dao;

import net.plethora.bot.dao.repo.PostRepositorySaveCellQuiz;
import net.plethora.bot.model.systemmodel.SaveCellQuiz;
import org.springframework.stereotype.Component;

@Component
public class DataAccessSaveCellQuiz {

    private PostRepositorySaveCellQuiz postRepositorySaveCellQuiz;

    public DataAccessSaveCellQuiz(PostRepositorySaveCellQuiz postRepositorySaveCellQuiz) {
        this.postRepositorySaveCellQuiz = postRepositorySaveCellQuiz;
    }

    public SaveCellQuiz findById(String id){
        return postRepositorySaveCellQuiz.findById(id).orElseThrow(() -> new IllegalStateException("SaveCellQuiz with id " + id + "not found"));

    }

    public SaveCellQuiz findByChatId(long chatId){
       return postRepositorySaveCellQuiz.findByChatId(chatId);
    }

    public void editSaveCellQuiz(long chatId, String[] saveId){
        SaveCellQuiz old = findByChatId(chatId);
        old.setSaveIdQuiz(saveId);
        postRepositorySaveCellQuiz.save(old);
    }

    public void newSaveCellQuiz(SaveCellQuiz saveCellQuiz){
        postRepositorySaveCellQuiz.save(saveCellQuiz);
    }
}