package net.plethora.bot.controller.webcontroller;

import net.plethora.bot.controlpanel.logic.ConverterToArrayAnswer;
import net.plethora.bot.dao.DataAccessAnswer;
import net.plethora.bot.model.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AnswerController {

    @Autowired
    private DataAccessAnswer dataAccessAnswer;
    @Autowired
    private ConverterToArrayAnswer converter;

    @PostMapping("/answer/get")
    public String getAnswer(String ask, Model model) {
        Answer answer = dataAccessAnswer.handleRequest(ask.toLowerCase());
        if (answer == null) {

        } else {
            StringBuilder stringBuilderAsk = new StringBuilder();
            int count = 0;
            for (String ask1 : answer.getAsk()) {
                count++;
                stringBuilderAsk.append(ask1);
                if (count != answer.getAsk().length) {
                    stringBuilderAsk.append(",");
                }
            }

            StringBuilder stringBuilderKey = new StringBuilder();
            count = 0;
            for (String key : answer.getKeyWords()) {
                count++;
                stringBuilderKey.append(key);
                if (count != answer.getKeyWords().length) {
                    stringBuilderKey.append(",");
                }
            }

            model.addAttribute("answer", answer.getAnswer());
            model.addAttribute("asks", stringBuilderAsk);
            model.addAttribute("keys", stringBuilderKey);
            model.addAttribute("id", answer.getId());
        }

        return "answer";
    }

    @RequestMapping(value = "answer/get/update", method = RequestMethod.POST, params = "action=update")
    public String updateAnswer(@RequestParam String idO, String asks, String keys, String answer) {
        Answer answerOld = dataAccessAnswer.findById(idO);
        System.out.println(answerOld.getAnswer());
        answerOld.setAsk(converter.convert(asks));
        answerOld.setKeyWords(converter.convert(keys));
        answerOld.setAnswer(answer);
        dataAccessAnswer.save(answerOld);

        return "answer";
    }

    @RequestMapping(value = "answer/get/update", method = RequestMethod.POST, params = "action=delete")
    public String delete(String idO) {
        dataAccessAnswer.delete(idO);
        return "answer";
    }

    @RequestMapping(value = "answer/get/update", method = RequestMethod.POST, params = "action=create")
    public String create(String asks, String keys, String answer) {
        Answer answerObj = new Answer();
        answerObj.setAsk(converter.convert(asks));
        answerObj.setAnswer(answer);
        answerObj.setKeyWords(converter.convert(keys));
        dataAccessAnswer.save(answerObj);
        return "answer";
    }

}
