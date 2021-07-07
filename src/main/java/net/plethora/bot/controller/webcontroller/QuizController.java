package net.plethora.bot.controller.webcontroller;

import net.plethora.bot.controlpanel.logic.ConverterToArrayAnswer;
import net.plethora.bot.dao.DataAccessQuiz;
import net.plethora.bot.model.Quiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class QuizController {

    @Autowired
    private DataAccessQuiz dataAccessQuiz;
    @Autowired
    private ConverterToArrayAnswer converterToArrayAnswer;

    @GetMapping("/quiz")
    public String quiz(Model model) {
        List<Quiz> list = dataAccessQuiz.findAll();
        model.addAttribute("quizzes", list);
        return "quiz";
    }

    @GetMapping("/quiz/{id}")
    public String getQuiz(@PathVariable("id") String id, Model model) {
        Quiz quiz = dataAccessQuiz.findById(id);
        List<Quiz> list = dataAccessQuiz.findAll();
        StringBuilder stringBuilder = new StringBuilder();
        int count = 0;
        for (String str : quiz.getOptions()) {
            count++;
            stringBuilder.append(str);
            if (count != quiz.getOptions().length) {
                stringBuilder.append(",");
            }
        }

        model.addAttribute("options", stringBuilder);
        model.addAttribute("question", quiz.getQuestion());
        model.addAttribute("answer", quiz.getAnswer());
        model.addAttribute("id", quiz.getId());
        model.addAttribute("quizzes", list);

        return "quiz";
    }

    @RequestMapping(value = "/quiz/edit", method = RequestMethod.POST, params = "action=update")
    public String update(String idQ, String quest, String answ, String option, Model model) {

        Quiz quiz = dataAccessQuiz.findById(idQ);
        quiz.setAnswer(Integer.parseInt(answ));
        quiz.setQuestion(quest);
        quiz.setOptions(converterToArrayAnswer.convert(option));

        dataAccessQuiz.save(quiz);

        List<Quiz> list = dataAccessQuiz.findAll();
        model.addAttribute("quizzes", list);
        return "quiz";
    }

    @RequestMapping(value = "/quiz/edit", method = RequestMethod.POST, params = "action=create")
    public String create(String quest, String answ, String option, Model model) {

        Quiz quiz = new Quiz();
        quiz.setAnswer(Integer.parseInt(answ));
        quiz.setQuestion(quest);
        quiz.setOptions(converterToArrayAnswer.convert(option));

        dataAccessQuiz.save(quiz);

        List<Quiz> list = dataAccessQuiz.findAll();
        model.addAttribute("quizzes", list);
        return "quiz";
    }

    @RequestMapping(value = "/quiz/edit", method = RequestMethod.POST, params = "action=delete")
    public String delete(String idQ, Model model) {

        dataAccessQuiz.delete(idQ);

        List<Quiz> list = dataAccessQuiz.findAll();
        model.addAttribute("quizzes", list);
        return "quiz";
    }

}
