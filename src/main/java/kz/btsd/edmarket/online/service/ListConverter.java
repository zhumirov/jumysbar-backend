package kz.btsd.edmarket.online.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListConverter {
    public final static String DELIMITER = "&#&";

    public static String listToString(List<String> list) {
        StringBuilder result = new StringBuilder();
        if (list.size() > 0) {
            result.append(list.get(0));
        }
        for (int i = 1; i < list.size(); i++) {
            result.append(DELIMITER);
            result.append(list.get(i));
        }
        return result.toString();
    }

    public static List<String> stringToList(String str) {
        if (str == null || str.length() == 0) {
            return new ArrayList<>();
        }
        return Arrays.asList(str.split(DELIMITER));
    }
}
