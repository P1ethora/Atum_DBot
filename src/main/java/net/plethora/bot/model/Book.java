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
    private String url;
    private String description;
    private String urlCoverBook;
    private String age;

    public Book(String id, String url, String description,String age) {
        this.id = id;
        this.url = url;
        this.description = description;
        this.age = age;
    }

    public Book(){}
}