package com.skilfully.aetherfantasy.aetherfantasy_auth.config.entity;

import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;

public class StringList {

    public final List<String> value;

    public StringList(YamlConfiguration config, String node) {
        Object temp = config.get(node);
        if (temp == null) {
            value = new ArrayList<>();
        }
        else if (temp instanceof String v) {
            value = List.of(v);
        } else if (temp instanceof List<?>) {
            List<String> tempList = new ArrayList<>();
            for (Object o : (List<?>) temp) {
                if (o == null) {
                    tempList.add(null);
                } else {
                    tempList.add(o.toString());
                }
            }
            value = tempList;
        } else {
            throw new ClassCastException("不支持的由 " + temp.getClass().getSimpleName() + " 转换为 ListString");
        }
    }

}
