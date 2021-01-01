package net.plethora.bot.controller.webcontroller;

import net.plethora.bot.dao.DataAccessMaterialBook;
import net.plethora.bot.model.material.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class BookController {

    @Autowired
    private DataAccessMaterialBook dataAccessMaterialBook;

    @GetMapping(value = "book/{id}")
    public String getBook(@PathVariable("id") String id, Model model) {
        Book book = dataAccessMaterialBook.findById(id);
        List<Book> books = dataAccessMaterialBook.findAll();

        model.addAttribute("idBook", book.getId());
        model.addAttribute("urlBook", book.getUrl());
        model.addAttribute("urlCover", book.getUrlCoverBook());
        model.addAttribute("subjectBook", book.getSubject());
        model.addAttribute("description", book.getDescription());
        model.addAttribute("books", books);

        return "book";
    }

}
