package net.plethora.bot.dao;

import net.plethora.bot.dao.repo.PostRepositoryBook;
import net.plethora.bot.model.material.Book;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataAccessMaterialBook implements DataAccessMaterial {

    private PostRepositoryBook postRepositoryBook;

    public DataAccessMaterialBook(PostRepositoryBook postRepositoryBook) {
        this.postRepositoryBook = postRepositoryBook;
    }

    public Book findById(String id) {
        return postRepositoryBook.findById(id).orElseThrow(() -> new IllegalStateException("Book with id " + id + " not found"));
    }

    @Override
    public List findBySubject(String subject) {
        return postRepositoryBook.findBySubject(subject);
    }
}