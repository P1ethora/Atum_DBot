package net.plethora.bot.dao;

import net.plethora.bot.dao.repo.PostRepositoryBook;
import net.plethora.bot.model.Book;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataAccessBook {

    private PostRepositoryBook postRepositoryBook;

    public DataAccessBook(PostRepositoryBook postRepositoryBook) {
        this.postRepositoryBook = postRepositoryBook;
    }

    public Book findById(String id){
        return  postRepositoryBook.findById(id).orElseThrow(() -> new IllegalStateException("Book with id " +id + " not found"));
    }

    public List<Book> findAll(){
        return postRepositoryBook.findAll();
    }

}
