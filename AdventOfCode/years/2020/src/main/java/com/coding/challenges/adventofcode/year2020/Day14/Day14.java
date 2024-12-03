package com.coding.challenges.adventofcode.year2020.Day14;

import com.coding.challenges.adventofcode.utils.Day;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day14 extends Day<Long> {

    public static void main(String[] args) throws IOException {
        Day14 day = new Day14();

        day.printPart1("sample-input-1", 165L);
        day.printPart1("input", 6631883285184L);

        day.printPart2("sample-input-2", 208L);
        day.printPart2("input", 3161838538691L);
    }

    @Override
    public Long part1(List<String> lines) {
        String mask = "";
        Map<Long, Long> memory = new HashMap<>();
        for (String line : lines) {
            if (line.startsWith("mask")) {
                mask = line.substring(7);
                continue;
            }

            String[] parts = line.split(" = ");
            long value = Long.parseLong(parts[1]);
            long address = Long.parseLong(parts[0].substring(4, parts[0].length() - 1));
            memory.put(address, applyMask(value, mask));
        }

        return memory.values().stream().mapToLong(Long::longValue).sum();
    }

    @Override
    public Long part2(List<String> lines) {
        String mask = "";
        Map<Long, Long> memory = new HashMap<>();
        for (String line : lines) {
            if (line.startsWith("mask")) {
                mask = line.substring(7);
                continue;
            }

            String[] parts = line.split(" = ");
            long value = Long.parseLong(parts[1]);
            long address = Long.parseLong(parts[0].substring(4, parts[0].length() - 1));
            applyFloatingAddress(applyMaskV2(address, mask), 0, 0, value, memory);
        }

        return memory.values().stream().mapToLong(Long::longValue).sum();
    }

    public String toBinaryString(long value) {
        return String.format("%36s", Long.toBinaryString(value)).replace(" ", "0");
    }

    public void applyFloatingAddress(char[] floatingAddress, int index, long currentAddress, long value, Map<Long, Long> memory) {
        if (index == floatingAddress.length) {
            memory.put(currentAddress, value);
            return;
        }

        if (floatingAddress[index] == '1' || floatingAddress[index] == 'X') {
            applyFloatingAddress(floatingAddress,
                index + 1,
                currentAddress + (long) Math.pow(2, index),
                value,
                memory);
        }
        if (floatingAddress[index] == '0' || floatingAddress[index] == 'X') {
            applyFloatingAddress(floatingAddress,
                index + 1,
                currentAddress,
                value,
                memory);
        }
    }

    public char[] applyMaskV2(long value, String mask) {
        char[] valueAsBinary = toBinaryString(value).toCharArray();
        for (int i = 0; i < valueAsBinary.length; i++) {
            char c = mask.charAt(i);
            if (c == 'X' || c == '1') {
                valueAsBinary[i] = c;
            }
        }
        return valueAsBinary;
    }

    public long applyMask(long value, String mask) {
        char[] valueAsBinary = toBinaryString(value).toCharArray();
        for (int i = 0; i < valueAsBinary.length; i++) {
            if (mask.charAt(i) != 'X') {
                valueAsBinary[i] = mask.charAt(i);
            }
        }
        StringBuilder sb = new StringBuilder();
        for (char c : valueAsBinary) {
            sb.append(c);
        }

        return Long.parseLong(sb.toString(), 2);
    }
}
