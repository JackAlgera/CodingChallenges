package Year2022.Day11;

import utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class Day11 {

    private static final String INPUT_NAME = "AdventOfCode/Year2022/Day11/input.txt";

    public static void main(String[] args) throws IOException {
        Day11 day = new Day11();
//        day.part1();
        day.part2();
    }

    private void part1() throws IOException {
        BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);

        List<Monkey> monkeys = new ArrayList<>();

        while (br.ready()) {
            String line = br.readLine();
            String[] sections = line.split(" ");

            if (sections[0].equals("Monkey")) {
                Monkey monkey = new Monkey();

                String[] startingItems = br.readLine().split(":");
                for (String item : startingItems[1].split(",")) {
                    monkey.getItems().add(Integer.parseInt(item.strip()));
                }

                String operation = br.readLine();
                monkey.setType(operation.contains("*") ? Type.MULTIPLICATION : Type.ADDITION);

                String operationValue = operation.split(" ")[7].strip();
                monkey.setOperationValue(operationValue.contains("old") ? -1 : Integer.parseInt(operationValue));

                String divisibleBy = br.readLine();
                monkey.setDivisibleBy(Integer.parseInt(divisibleBy.split("by")[1].strip()));

                monkey.setIfTrueMonkeyIndex(Integer.parseInt(br.readLine().split("monkey")[1].strip()));
                monkey.setIfFalseMonkeyIndex(Integer.parseInt(br.readLine().split("monkey")[1].strip()));
                monkeys.add(monkey);
            }
        }

        for (int i = 0; i < 20; i++) {
//            System.out.println("");
//            System.out.println("Round " + i);
//            System.out.println("");

            for (int j = 0; j < monkeys.size(); j++) {
                Monkey monkey = monkeys.get(j);

                while (!monkey.getItems().isEmpty()) {
                    int item = monkey.playNextItem();
//                    System.out.print(item);

                    if (item < 0) {
                        break;
                    }

//                    item /= 3;
//                    System.out.print(" -> " + item);

                    if (monkey.isDivisibleBy(item)) {
//                        System.out.println(" -> throw to monkey " + monkey.getIfTrueMonkeyIndex());
                        monkeys.get(monkey.getIfTrueMonkeyIndex()).getItems().add(item);
                    } else {
//                        System.out.println(" -> throw to monkey " + monkey.getIfFalseMonkeyIndex());
                        monkeys.get(monkey.getIfFalseMonkeyIndex()).getItems().add(item);
                    }
                }
            }

//            for (int k = 0; k < monkeys.size(); k++) {
//                System.out.println("Monkey " + k + " inspected items " + monkeys.get(k).getTotalItemsInspected() + " times.");
//                monkeys.get(k).print(k);
//            }
        }

        for (int i = 0; i < monkeys.size(); i++) {
//            monkeys.get(i).print(i);
            System.out.println("Monkey " + i + " inspected items " + monkeys.get(i).getTotalItemsInspected() + " times.");
        }

        System.out.println("-------- Day 5 - Part 1 --------");
        System.out.print("Top layer : ");
        System.out.println("\nExpected top layer: RFFFWBPNS");
    }

    private void part2() throws IOException {
        BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);

        List<Monkey> monkeys = new ArrayList<>();

        while (br.ready()) {
            String line = br.readLine();
            String[] sections = line.split(" ");

            if (sections[0].equals("Monkey")) {
                Monkey monkey = new Monkey();

                String[] startingItems = br.readLine().split(":");
                for (String item : startingItems[1].split(",")) {
                    monkey.getItems().add(Integer.parseInt(item.strip()));
                }

                String operation = br.readLine();
                monkey.setType(operation.contains("*") ? Type.MULTIPLICATION : Type.ADDITION);

                String operationValue = operation.split(" ")[7].strip();
                monkey.setOperationValue(operationValue.contains("old") ? -1 : Integer.parseInt(operationValue));

                String divisibleBy = br.readLine();
                monkey.setDivisibleBy(Integer.parseInt(divisibleBy.split("by")[1].strip()));

                monkey.setIfTrueMonkeyIndex(Integer.parseInt(br.readLine().split("monkey")[1].strip()));
                monkey.setIfFalseMonkeyIndex(Integer.parseInt(br.readLine().split("monkey")[1].strip()));
                monkeys.add(monkey);
            }
        }

        for (int i = 0; i < 10000; i++) {
//            System.out.println("");
//            System.out.println("Round " + i);
//            System.out.println("");

            for (int j = 0; j < monkeys.size(); j++) {
                Monkey monkey = monkeys.get(j);

                while (!monkey.getItems().isEmpty()) {
                    int item = monkey.playNextItem();
//                    System.out.print(item);

                    if (item < 0) {
                        break;
                    }

//                    item /= 3;
//                    System.out.print(" -> " + item);

                    item = item % 96577;

                    if (monkey.isDivisibleBy(item)) {
//                        System.out.println(" -> throw to monkey " + monkey.getIfTrueMonkeyIndex());
                        monkeys.get(monkey.getIfTrueMonkeyIndex()).getItems().add(item);
                    } else {
//                        System.out.println(" -> throw to monkey " + monkey.getIfFalseMonkeyIndex());
                        monkeys.get(monkey.getIfFalseMonkeyIndex()).getItems().add(item);
                    }
                }
            }

//            for (int k = 0; k < monkeys.size(); k++) {
//                System.out.println("Monkey " + k + " inspected items " + monkeys.get(k).getTotalItemsInspected() + " times.");
//                monkeys.get(k).print(k);
//            }
        }

        for (int i = 0; i < monkeys.size(); i++) {
//            monkeys.get(i).print(i);
            System.out.println("Monkey " + i + " inspected items " + monkeys.get(i).getTotalItemsInspected() + " times.");
        }

        System.out.println("-------- Day 5 - Part 1 --------");
        System.out.print("Top layer : ");
        System.out.println("\nExpected top layer: RFFFWBPNS");
    }

    enum Type {
        MULTIPLICATION,
        ADDITION;
    }

    public class Monkey {
        Queue<Integer> items;
        Type type;
        int operationValue;
        int divisibleBy;
        int ifTrueMonkeyIndex, ifFalseMonkeyIndex;
        int totalItemsInspected;
        int remainder;

        public Monkey() {
            this.items = new LinkedList<>();
            this.totalItemsInspected = 0;
            this.remainder = 0;
        }

        public int playNextItem() {
            if (items.isEmpty()) {
                return Integer.MIN_VALUE;
            }

            int item = items.poll();
            totalItemsInspected++;

            switch (type) {
                case ADDITION -> {
                    return item + (operationValue < 0 ? item : operationValue);
                }
                case MULTIPLICATION -> {
                    return item * (operationValue < 0 ? item : operationValue);
                }
            }

            return Integer.MIN_VALUE;
        }

        public boolean isDivisibleBy(int value) {
//            System.out.print(" (" + value + " % " + divisibleBy + "= " +  (value % divisibleBy) + ") ");
            return value % 96577 == 0;
        }

        public int getDivisibleByValueLol(int value) {
//            System.out.print(" (" + value + " % " + divisibleBy + "= " +  (value % divisibleBy) + ") ");
            return value % 96577;
        }

        public void print(int index) {
            System.out.println("Monkey " + index);
            System.out.print("  Starting items:");
            for (Integer item: items) {
                System.out.print(" " + item);
            }
            System.out.println("");
            System.out.println("  Operation: new = old " + (type.equals(Type.MULTIPLICATION) ? "*" : "+") + " " + operationValue);
            System.out.println("  Test: divisible by " + divisibleBy);
            System.out.println("    If true: throw to monkey " + ifTrueMonkeyIndex);
            System.out.println("    If false: throw to monkey " + ifFalseMonkeyIndex);
            System.out.println("");
        }

        public int getTotalItemsInspected() {
            return totalItemsInspected;
        }

        public int getIfTrueMonkeyIndex() {
            return ifTrueMonkeyIndex;
        }

        public void setIfTrueMonkeyIndex(int ifTrueMonkeyIndex) {
            this.ifTrueMonkeyIndex = ifTrueMonkeyIndex;
        }

        public int getIfFalseMonkeyIndex() {
            return ifFalseMonkeyIndex;
        }

        public void setIfFalseMonkeyIndex(int ifFalseMonkeyIndex) {
            this.ifFalseMonkeyIndex = ifFalseMonkeyIndex;
        }

        public Queue<Integer> getItems() {
            return items;
        }

        public void setItems(Queue<Integer> items) {
            this.items = items;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public int getOperationValue() {
            return operationValue;
        }

        public void setOperationValue(int operationValue) {
            this.operationValue = operationValue;
        }

        public int getDivisibleBy() {
            return divisibleBy;
        }

        public void setDivisibleBy(int divisibleBy) {
            this.divisibleBy = divisibleBy;
        }
    }
}
