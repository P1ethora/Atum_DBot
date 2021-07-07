package net.plethora.bot.controlpanel.logic;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ConverterToArrayAnswer {

    public String[] convert(String str) {
        List<String> list = Arrays.asList(str.split(","));

        for (String strL : list) {
            strL = strL.trim();
        }

        list.removeIf(o -> o.equals(""));

        return list.toArray(new String[0]);
    }

}
