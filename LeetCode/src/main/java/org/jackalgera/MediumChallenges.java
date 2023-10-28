package org.jackalgera;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MediumChallenges {
    public static int lengthOfLongestSubstring(String s) {
        if (s.isBlank()) {
            return 0;
        }

        Map<Character, Integer> letterPositions = new HashMap<>();

        int currentSize = 0;
        int maxSize = -1;
        int k = 0;

        while (k < s.length()) {
            char c = s.charAt(k);

            if (letterPositions.containsKey(c)) {
                if (currentSize > maxSize) {
                    maxSize = currentSize;
                }

                k = letterPositions.get(c);
                letterPositions = new HashMap<>();
                currentSize = 0;
            } else {
                currentSize++;
                letterPositions.put(c, k);
            }

            k++;
        }

        return Math.max(maxSize, currentSize);
    }

    public static String longestPalindrome_0005(String s) {
        String longestPalindrome = "";
        for (int i = 0; i < s.length(); i++) {
            int k = extractPalindromeSize(s, i, 0);
            if (k * 2 + 1 > longestPalindrome.length()) {
                longestPalindrome = s.substring(i - k, i + k + 1);
            }

            if (i != s.length() - 1 && s.charAt(i) == s.charAt(i + 1)) {
                k = extractPalindromeSize(s, i, 1);
                if (k * 2 + 2 > longestPalindrome.length()) {
                    longestPalindrome = s.substring(i - k, i + k + 2);
                }
            }
        }

        return longestPalindrome;
    }

    public static int extractPalindromeSize(String s, int i, int offset) {
        int k = 1;
        while (i - k >= 0 && i + offset + k < s.length()) {
            if (s.charAt(i - k) != s.charAt(i + offset + k)) {
                break;
            }
            k++;
        }

        return k - 1;
    }

    public static String convert_0006(String s, int numRows) {
        List<ArrayList<String>> rows = IntStream.range(0, numRows)
            .mapToObj(i -> new ArrayList<String>())
            .toList();

        int rowIndex = 0;
        int direction = -1;

        for (int i = 0; i < s.length(); i++) {
            rows.get(rowIndex).add("" + s.charAt(i));

            if (numRows == 1) {
                continue;
            }

            if (rowIndex + direction < 0 || rowIndex + direction >= numRows) {
                direction *= -1;
            }
            rowIndex += direction;
        }

        return rows.stream()
            .map(row -> String.join("", row))
            .collect(Collectors.joining(""));
    }

    public static int reverse_0007(int x) {
        String xStr = "" + Math.abs(x);
        String xNew = "";

        for (int i = xStr.length() - 1; i >= 0; i--) {
            xNew = xNew + xStr.charAt(i);
        }

        try {
            return Integer.parseInt(xNew) * (x < 0 ? -1 : 1);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static int myAtoi_0008(String s) {
        s = s.strip();
        if (s.isEmpty()) {
            return 0;
        }

        boolean isNegative = false;
        if (s.charAt(0) == '-' || s.charAt(0) == '+') {
            isNegative = s.charAt(0) == '-';
            s = s.substring(1);
        }

        if (s.isEmpty() || !Character.isDigit(s.charAt(0))) {
            return 0;
        }

        try {
            return Integer.parseInt(s.split("\\D+")[0]) * (isNegative ? -1 : 1);
        } catch (NumberFormatException e) {
            return isNegative ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        }
    }

    public int maxArea_0011(int[] height) {
        int maxWater = 0;
        int left = 0;
        int right = height.length - 1;
        while (left != right) {
            int size = (right - left) * Math.min(height[left], height[right]);
            if (size > maxWater) {
                maxWater = size;
            }

            if (height[left] < height[right]) {
                left++;
            } else {
                right--;
            }
        }

        return maxWater;
    }

    public static int romanToInt_0013(String s) {
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

    int[][] solution(int[][] a) {
        Map<Float, List<Integer>> means = new HashMap<>();
        for (int i = 0; i < a.length; i++) {
            System.out.println("HEre");
            Float mean = Arrays.stream(a[i]).sum() / (float) a[i].length;

            if (means.containsKey(mean)) {
                means.get(mean).add(i);
            } else {
                means.put(mean, new ArrayList<>(List.of(i)));
            }
        }

        return means.values().stream()
            .map(val -> val.stream().sorted().mapToInt(v -> v).toArray())
            .sorted((v1, v2) -> Integer.compare(v1[0], v2[0]))
            .toArray(int[][]::new);
    }

    public static List<List<Integer>> threeSum_0015(int[] nums) {
        Set<List<Integer>> triplets = new HashSet<>();
        Arrays.sort(nums);

        for (int i = 0; i < nums.length - 2; i++) {
            if (nums[i] > 0) {
                break;
            }
            int j = i + 1;
            int k = nums.length - 1;

            while (j < k) {
                int sum = nums[i] + nums[j] + nums[k];
                if (sum < 0) {
                    j++;
                    continue;
                }

                if (sum > 0) {
                    k--;
                    continue;
                }

                triplets.add(List.of(nums[i], nums[j], nums[k]));
                j++;
                k--;
            }
        }

        return new ArrayList<>(triplets);
    }
}

record Triplet(int i, int j, int k) {}
