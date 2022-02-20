package com.adamov.shortlink.statistic;

import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class StatisticOfClick {
    private final Map<String, Long> map= Collections.synchronizedMap(new HashMap<>());

    public Map<String, Long> getMap() {
        return map;
    }

    public void addClick(String shortLink){
        if (map.get(shortLink)==null) map.put(shortLink,1l);
        else map.put(shortLink,map.get(shortLink)+1);
    }

    public void deleteLink(String shortLink){
        map.remove(shortLink);
    }



}
