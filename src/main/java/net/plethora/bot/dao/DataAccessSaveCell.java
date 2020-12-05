package net.plethora.bot.dao;

import lombok.Getter;
import lombok.Setter;
import net.plethora.bot.dao.repo.PostRepositorySaveCell;
import net.plethora.bot.model.systemmodel.SaveCell;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class DataAccessSaveCell {

    private PostRepositorySaveCell postRepositorySaveCell;

    public DataAccessSaveCell(PostRepositorySaveCell postRepositorySaveCell) {
        this.postRepositorySaveCell = postRepositorySaveCell;
    }

    public SaveCell findByChatIdAndSubject(long chatId, String subject) {
        return postRepositorySaveCell.findByChatIdAndSaveSubject(chatId, subject);
    }

    public void addSaveCell(SaveCell saveCell) {
        postRepositorySaveCell.save(saveCell);
    }

    public void editSaveCell(SaveCell saveCell, String saveId) {
        SaveCell oldSave = postRepositorySaveCell.findById(saveCell.getId())
                .orElseThrow(() -> new IllegalStateException("SaveCell with id " + saveCell.getId() + "not found"));
        oldSave.setSaveId(saveId);
        postRepositorySaveCell.save(oldSave);

    }
}