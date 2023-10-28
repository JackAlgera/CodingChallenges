package org.jackalgera;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println(romanToInt("III"));
    }

    public static int romanToInt(String s) {
        if (s.isEmpty()) {
            return 0;
        }

        Map<String, Integer> values = new HashMap<>() {{
            put("I", 1);
            put("V", 5);
            put("X", 10);
            put("L", 50);
            put("C", 100);
            put("D", 500);
            put("M", 1000);
            put("IV", 4);
            put("IX", 9);
            put("XL", 40);
            put("XC", 90);
            put("CD", 400);
            put("CM", 900);
        }};

        int i = 0;
        int total = 0;
        while (i < s.length()) {
            if (i + 1 > s.length() - 1) {
                total += values.get(String.valueOf(s.charAt(i)));
                i++;
                continue;
            }

            if (values.containsKey(s.substring(i, i + 2))) {
                total += values.get(s.substring(i, i + 2));
                i += 2;
                continue;
            }

            total += values.get(String.valueOf(s.charAt(i)));
            i++;
        }

        return total;
    }
}
