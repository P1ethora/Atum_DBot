package net.plethora.bot.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "areacode")
public class Area {

    @Id
    private String id;
    @Field("code")
    private int code;
    @Field("area")
    private String area;

    public Area(int code,String area) {
        this.area = area;
        this.code = code;
    }

    public Area() {
    }
}