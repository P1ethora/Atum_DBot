package net.plethora.bot.model.systemmodel;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "design_file")
public class DesignFile {

    @Id
    private String id;
    private String fileName;
    private String url;

}
