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

        model.addAttribute("name", book.getName());
        model.addAttribute("author", book.getAuthor());
        model.addAttribute("idBook", book.getId());
        model.addAttribute("urlBook", book.getUrl());
        model.addAttribute("urlCover", book.getUrlCoverBook());
        model.addAttribute("subjectBook", book.getSubject());
        model.addAttribute("description", book.getDescription());
        model.addAttribute("books", books);

        return "book";
    }

    @RequestMapping(value = "/book/edit", method = RequestMethod.POST, params = "action=update")
    public String update(String urlCoverBook, String idBook, String subject, String urlBook,
                         String description, Model model, String name, String author) {
        Book book = dataAccessMaterialBook.findById(idBook);
        book.setUrlCoverBook(urlCoverBook);
        book.setUrl(urlBook);
        book.setSubject(subject);
        book.setDescription(description);
        book.setAuthor(author);
        book.setName(name);
        dataAccessMaterialBook.save(book);

        List<Book> books = dataAccessMaterialBook.findAll();
        model.addAttribute("books", books);
        return "book";
    }

    @RequestMapping(value = "/book/edit", method = RequestMethod.POST, params = "action=delete")
    public String delete(String idBook, Model model) {
        dataAccessMaterialBook.delete(idBook);
        List<Book> books = dataAccessMaterialBook.findAll();
        model.addAttribute("books", books);
        return "book";
    }

    @RequestMapping(value = "/book/edit", method = RequestMethod.POST, params = "action=create")
    public String create(String urlCoverBook, String urlBook, String subject,
                         String description, Model model, String name, String author) {
        Book book = new Book();

        book.setUrlCoverBook(urlCoverBook);
        book.setUrl(urlBook);
        book.setSubject(subject);
        book.setDescription(description);
        book.setName(name);
        book.setAuthor(author);
        dataAccessMaterialBook.save(book);

        List<Book> books = dataAccessMaterialBook.findAll();
        model.addAttribute("books", books);
        return "book";
    }

}
