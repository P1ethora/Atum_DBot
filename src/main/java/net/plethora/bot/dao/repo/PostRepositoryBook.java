package net.plethora.bot.dao.repo;

import net.plethora.bot.model.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepositoryBook extends MongoRepository<Book,String> {

    }