package net.plethora.bot.repo;

import net.plethora.bot.model.Area;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PostRepositoryArea extends MongoRepository<Area, String> {

    /**
     * Метод поиска по базе данных
     */

    public Area findByArea(String area);

}