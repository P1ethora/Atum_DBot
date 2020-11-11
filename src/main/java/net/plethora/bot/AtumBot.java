package net.plethora.bot;


import lombok.Getter;
import lombok.Setter;
import net.plethora.bot.model.Answer;
import net.plethora.bot.repo.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class AtumBot extends TelegramWebhookBot {

    private String botPath;
    private String botUsername;
    private String botToken;
//  private HandlerMsg handlerMsg;

    private Answer answer;
    private final String NEGATIVE_ANSWER = "хм... Про это ничего не знаю";

    @Autowired
    private PostRepository postRepository;

    public AtumBot(DefaultBotOptions botOptions) {
        super(botOptions);
    }

    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {

        try {
            if (update.hasCallbackQuery()) {
                execute(sendMessage(update.getCallbackQuery().getMessage().getChatId(),
                        (update.getCallbackQuery().getData())));
            } else if (update.getMessage() != null && update.getMessage().hasText()) {

                long chatId = update.getMessage().getChatId();
                String askUser = update.getMessage().getText().toLowerCase();
                assignAnswerMsg(askUser);
                if (answer == null) {
                    execute(new SendMessage(chatId, NEGATIVE_ANSWER));
                    return null;
                }
                //execute(new SendMessage(chatId,postRepository.findAnswerByAsk(askUser).getAnswer()));
                execute(sendMessage(chatId, askUser));
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getAnswer(String ask) {
        //answer = assignAnswerMsg(ask);
       // return answer == null ? NEGATIVE_ANSWER : answer.getAnswer();
        return answer.getAnswer();
    }

    private void assignAnswerMsg(String ask) {
        answer = postRepository.findAnswerByAsk(ask);
    }

    public SendMessage sendMessage(long chatId, String ask) {
        SendMessage sendMessage = new SendMessage(chatId, getAnswer(ask));

        if (answer.getKeyWords().length > 0) {
            sendMessage.setReplyMarkup(getInlineKeyboardMarkup());
        } //добавляем клавиатуру

        return sendMessage;
    }


    private InlineKeyboardMarkup getInlineKeyboardMarkup() {

        InlineKeyboardMarkup keyBoardMsg = new InlineKeyboardMarkup();   //наша клава под msg
        List<List<InlineKeyboardButton>> rows = new ArrayList<>(); //формируем ряды

        fillRows(rows);

        int countGlobal = 0; //счетчик всех по порядку ключевых слов
        for (List<InlineKeyboardButton> list : rows) {
            int count = 0;  //внутренний счетчик на количество кнопок в ряду
            while (countGlobal < answer.getKeyWords().length) {
                if (count + 1 > 3) {
                    break;
                }   //если 4 кнопки добавлены выходим
                InlineKeyboardButton button = new InlineKeyboardButton(answer.getKeyWords()[countGlobal]);    //текст на кнопке
                button.setCallbackData(answer.getKeyWords()[countGlobal]); //текст содержащийся в колбеке
                count++;
                countGlobal++;
                list.add(button);
            }
        }

        keyBoardMsg.setKeyboard(rows);

        return keyBoardMsg;
    }


    private void fillRows(List<List<InlineKeyboardButton>> rows) {
        int numberRows = (answer.getKeyWords().length / 3) + 1;//делим на 3 и округляем в большую сторону
        for (int i = 0; i < numberRows; i++) {   //заполняем лист пустыми рядами
            rows.add(new ArrayList<>());
        }
    }
}