package com.coding.challenges.adventofcode.year2020.Day18;

import com.coding.challenges.adventofcode.utils.Day;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Day18 extends Day<Long> {

    public static void main(String[] args) throws IOException {
        Day18 day = new Day18();

        day.printPart1("sample-input", 26L + 437L + 12240L + 13632L);
        day.printPart1("input", 510009915468L);

        day.printPart2("sample-input", 46L + 1445L + 669060 + 23340L);
        day.printPart2("input", 321176691637769L);
    }

    @Override
    public Long part1(List<String> lines) {
        return evaluate(lines, true);
    }

    @Override
    public Long part2(List<String> lines) {
        return evaluate(lines, false);
    }

    public long evaluate(List<String> lines, boolean part1) {
        return lines.stream()
            .map(line -> line.replace("(", "( ")
                .replace(")", " )"))
            .map(line -> part1 ? evaluatePart1(line.split(" ")) : evaluatePart2(line))
            .reduce(0L, Long::sum);
    }

    public long evaluatePart1(String[] expression) {
        long val = 0;
        String currentOperator = "";

        for (int i = 0; i < expression.length; i++) {
            String c = expression[i];

            if (Character.isDigit(c.charAt(0))) {
                int d = Integer.parseInt(c);
                if (currentOperator.isEmpty()) {
                    val = d;
                } else {
                    switch (currentOperator) {
                        case "+" -> val += d;
                        case "*" -> val *= d;
                    }
                    currentOperator = "";
                }
                continue;
            }

            if (c.equals("+") || c.equals("*")) {
                currentOperator = c;
                continue;
            }

            if (c.equals("(")) {
                int start = i;
                int offset = 1;
                while (offset != 0) {
                    i++;
                    if (expression[i].equals("(")) {
                        offset++;
                    }
                    if (expression[i].equals(")")) {
                        offset--;
                    }
                }
                if (currentOperator.equals("*")) {
                    val *= evaluatePart1(Arrays.copyOfRange(expression, start + 1, i));
                } else {
                    val += evaluatePart1(Arrays.copyOfRange(expression, start + 1, i));
                }
            }
        }

        return val;
    }

    public long evaluatePart2(String expression) {
        while (expression.contains("(")) {
            int start = expression.indexOf("(") + 1;
            int i = start;
            int offset = 1;
            while (offset != 0) {
                i++;
                if (expression.charAt(i) == '(') {
                    offset++;
                }
                if (expression.charAt(i) == ')') {
                    offset--;
                }
            }
            long val = evaluatePart2(expression.substring(start + 1, i - 1));
            expression = expression.substring(0, start - 1) + val + expression.substring(i + 1);
        }

        while (expression.contains("+")) {
            expression = replacePlus(expression);
        }
        return Arrays.stream(expression.strip().split(" \\* "))
            .filter(s -> !s.isEmpty())
            .mapToLong(Long::parseLong)
            .reduce(1, (a, b) -> a * b);
    }

    public String replacePlus(String expression) {
        int start = expression.indexOf("+") - 2;
        int end = expression.indexOf("+") + 2;
        while (start > 0 && Character.isDigit(expression.charAt(start - 1))) {
            start--;
        }
        while ((end < expression.length() - 1) && Character.isDigit(expression.charAt(end + 1))) {
            end++;
        }
        long val = Arrays.stream(expression.substring(start, end + 1).split(" \\+ "))
            .mapToLong(Long::parseLong)
            .sum();
        return expression.substring(0, start) + val + expression.substring(end + 1);
    }
}
