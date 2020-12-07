package net.plethora.bot.botapi.system;

import net.plethora.bot.botapi.keyboards.kbbook.KeyboardBookSelect;
import net.plethora.bot.botapi.keyboards.kbtask.KeyboardTaskSelect;
import net.plethora.bot.dao.DataAccessMaterial;
import net.plethora.bot.dao.DataAccessSaveCell;
import net.plethora.bot.model.material.Book;
import net.plethora.bot.model.material.Material;
import net.plethora.bot.model.material.Task;
import net.plethora.bot.model.systemmodel.SaveCellMaterial;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;

import java.util.ArrayList;
import java.util.List;

@Component
public class ShiftView<T> {

    private DataAccessSaveCell dataAccessSaveCell;
    private KeyboardTaskSelect keyboardTaskSelect;
    private KeyboardBookSelect keyboardBookSelect;

    private final int NUMBER_NEXT = 1;
    private final int NUMBER_BACK = -1;

    public ShiftView(DataAccessSaveCell dataAccessSaveCell, KeyboardTaskSelect keyboardTaskSelect, KeyboardBookSelect keyboardBookSelect) {
        this.dataAccessSaveCell = dataAccessSaveCell;
        this.keyboardTaskSelect = keyboardTaskSelect;
        this.keyboardBookSelect = keyboardBookSelect;
    }

    public List<T> view(long chatId, String subject, int messageId, DataAccessMaterial dataAccessMaterial, boolean next, boolean back) {

        List<T> msgForSend = new ArrayList<>();                                         //список сообщений для отправки
        List<Material> allMaterialSubject = dataAccessMaterial.findBySubject(subject);  //взяди все задачи этой темы
        SaveCellMaterial saveCellMaterial = dataAccessSaveCell.findByChatIdAndSubject(chatId, subject); //ячейка сохранения

        if (saveCellMaterial == null) {
            saveCellMaterial = new SaveCellMaterial(chatId, allMaterialSubject.get(0).getId(), subject);
            dataAccessSaveCell.addSaveCell(saveCellMaterial);
        }
        //ТЕКУЩИЙ
        if (!next && !back) {
            msgForSend = actualMessage(messageId, chatId, subject, allMaterialSubject, saveCellMaterial);
        }
        //СЛЕДУЮЩИЙ
        else if (next && !back) {
            msgForSend = shift(chatId, messageId, subject, saveCellMaterial, NUMBER_NEXT, allMaterialSubject);
        }
        //ПРЕДЫДУЩИЙ
        else if (back && !next) {
            msgForSend = shift(chatId, messageId, subject, saveCellMaterial, NUMBER_BACK, allMaterialSubject);
        }
        return msgForSend;
    }

    private List<T> shift(long chatId, int messageId, String subject, SaveCellMaterial saveCellMaterial, int shiftNumber, List<Material> allMaterialSubject) {
        List<T> msgForSend = new ArrayList<>();
        for (int i = 0; i < allMaterialSubject.size(); i++) {
            if (allMaterialSubject.get(i).getId().equals(saveCellMaterial.getSaveId())) {
                dataAccessSaveCell.editSaveCell(saveCellMaterial, allMaterialSubject.get(i + shiftNumber).getId());
                saveCellMaterial.setSaveId(allMaterialSubject.get(i + shiftNumber).getId());
                msgForSend = actualMessage(messageId, chatId, subject, allMaterialSubject, saveCellMaterial);
                break;
            }
        }
        return msgForSend;
    }


    private List<T> actualMessage(int messageId, long chatId, String subject, List<Material> allMaterialSubject, SaveCellMaterial saveCellMaterial) {
        List<T> messageTexts = new ArrayList<>();

        int limit = allMaterialSubject.size();
        int number = 0;

        for (Material material : allMaterialSubject) {
            number++;
            if (saveCellMaterial.getSaveId().equals(material.getId())) {
                if (material instanceof Task)
                    messageTexts.add((T) editMessageTask(chatId, messageId, subject, number, limit, material));
                else if (material instanceof Book) {
                    messageTexts.add((T) editMessageBook(chatId, messageId, subject, number, limit, material));
                }
            }
        }

        return messageTexts;
    }

    //EDIT TASK
    private EditMessageText editMessageTask(long chatId, int messageId, String subject, int number, int limit, Material material) {
        EditMessageText editMessageTask = new EditMessageText();
        editMessageTask.setChatId(chatId);
        editMessageTask.setMessageId(messageId);
        editMessageTask.setText("[" + number + "/" + limit + "] " + material.getProblem());
        editMessageTask.setReplyMarkup(keyboardTaskSelect.keyboard(material.getId(), subject, limit, number));
        return editMessageTask;
    }

    //EDIT BOOK
    private EditMessageMedia editMessageBook(long chatId, int messageId, String subject, int number, int limit, Material material) {
        EditMessageMedia editMessageMedia = new EditMessageMedia();
        editMessageMedia.setChatId(chatId); //id чата
        editMessageMedia.setMessageId(messageId);  //id сообщения
        editMessageMedia.setMedia(new InputMediaPhoto()
                .setMedia(material.getUrlCoverBook()));
        editMessageMedia.setReplyMarkup(keyboardBookSelect.keyboard(number, limit, material.getUrl()));
        return editMessageMedia;
    }
}