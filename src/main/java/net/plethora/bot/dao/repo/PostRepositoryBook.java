package net.plethora.bot.dao.repo;

import net.plethora.bot.model.material.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepositoryBook extends MongoRepository<Book, String> {

   public List<Book> findBySubject(String subject);
}