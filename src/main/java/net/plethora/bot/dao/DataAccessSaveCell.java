package net.plethora.bot.dao;

import net.plethora.bot.dao.repo.PostRepositorySaveCell;
import net.plethora.bot.model.systemmodel.SaveCellMaterial;
import org.springframework.stereotype.Component;

@Component
public class DataAccessSaveCell {

    private PostRepositorySaveCell postRepositorySaveCell;

    public DataAccessSaveCell(PostRepositorySaveCell postRepositorySaveCell) {
        this.postRepositorySaveCell = postRepositorySaveCell;
    }

    public SaveCellMaterial findByChatIdAndSubject(long chatId, String subject) {
        return postRepositorySaveCell.findByChatIdAndSaveSubject(chatId, subject);
    }

    public void addSaveCell(SaveCellMaterial saveCellMaterial) {
        postRepositorySaveCell.save(saveCellMaterial);
    }

    public void editSaveCell(SaveCellMaterial saveCellMaterial, String saveId) {
        SaveCellMaterial oldSave = postRepositorySaveCell.findById(saveCellMaterial.getId())
                .orElseThrow(() -> new IllegalStateException("SaveCell with id " + saveCellMaterial.getId() + "not found"));
        oldSave.setSaveId(saveId);
        postRepositorySaveCell.save(oldSave);

    }
}