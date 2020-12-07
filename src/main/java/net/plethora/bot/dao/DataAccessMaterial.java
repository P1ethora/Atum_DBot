package net.plethora.bot.dao;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface DataAccessMaterial<T> {

    List<T> findBySubject(String subject);

}