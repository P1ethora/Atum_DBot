package net.plethora.bot.botapi.system;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Cutter {

    private List<String> list;

    public Cutter() {
        initialList();
    }

    public String cut(String msgUser) {
        String result = msgUser.replaceAll("[^A-Za-zА-Яа-я ]", "");
        StringBuilder request = new StringBuilder();
        for (String str : result.split(" ")) {
            boolean chk = false;
            for (String toDelete : list) {
                if (toDelete.equals(str)) {
                    chk = true;
                    break;
                }
            }

            if (!chk) {
                request.append(str).append(" ");
            }
        }
        return request.toString().trim();
    }

    private void initialList() {
        list = new ArrayList<>();
        list.add("как");
        list.add("что");
        list.add("такое");
        list.add("значит");
        list.add("это");
        list.add("кто");
        list.add("определение");
    }
}