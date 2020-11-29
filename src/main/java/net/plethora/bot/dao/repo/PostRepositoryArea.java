package net.plethora.bot.dao.repo;

import net.plethora.bot.model.Area;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PostRepositoryArea extends MongoRepository<Area, String> {

    /**
     * Метод поиска по базе данных
     */

    public Area findByArea(String area);

}