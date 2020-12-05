package net.plethora.bot.dao;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface DataAccessMaterial<T> {

    public List<T> findBySubject(String subject);

}
