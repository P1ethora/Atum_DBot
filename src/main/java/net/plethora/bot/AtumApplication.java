package net.plethora.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@SpringBootApplication
public class AtumApplication  {
    public static void main(String[] args) {
        SpringApplication.run(AtumApplication.class,args);
    }
}