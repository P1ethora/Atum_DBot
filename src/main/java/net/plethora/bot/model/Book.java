package net.plethora.bot.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "books")
public class Book {

    @Id
    private String id;
    private String fileName;
    private String url;
    private String description;
    private String urlCoverBook;

    public Book(String id, String fileName, String url, String description) {
        this.id = id;
        this.fileName = fileName;
        this.url = url;
        this.description = description;
    }

    public Book(){}
}
