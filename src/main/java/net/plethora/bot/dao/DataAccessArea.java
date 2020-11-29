package net.plethora.bot.dao;

import net.plethora.bot.model.Area;
import net.plethora.bot.dao.repo.PostRepositoryArea;
import org.springframework.stereotype.Component;

/**
 * Соединяется с коллекцией areacode
 */

@Component
public class DataAccessArea {

    private final PostRepositoryArea postRepositoryArea;

    public DataAccessArea(PostRepositoryArea postRepositoryArea) {
        this.postRepositoryArea = postRepositoryArea;
    }

    public Area handleRequest(String area) {
        return postRepositoryArea.findByArea(area);
    }
}