package net.plethora.bot.dao;

import net.plethora.bot.dao.repo.PostRepositoryDesignFile;
import net.plethora.bot.model.systemmodel.DesignFile;
import org.springframework.stereotype.Component;

@Component
public class DataAccessDesingFile {

    private PostRepositoryDesignFile designFile;

    public DataAccessDesingFile(PostRepositoryDesignFile designFile) {
        this.designFile = designFile;
    }

    public DesignFile findByName(String fileName){
        return designFile.findByFileName(fileName);
    }

}
