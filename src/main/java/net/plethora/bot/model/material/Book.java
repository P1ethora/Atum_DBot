package net.plethora.bot.model.material;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "books")
public class Book extends Material {

    @Id
    private String id;
    private String url;
    private String description;
    private String urlCoverBook;
    private String subject;
    private String name;
    private String author;

    public Book(String id, String url, String description,String subject) {
        this.id = id;
        this.url = url;
        this.description = description;
        this.subject = subject;
    }

    public Book(){}
}