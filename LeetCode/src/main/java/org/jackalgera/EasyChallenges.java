package org.jackalgera;

import org.jackalgera.utils.ListNode;

import java.util.*;

class Unknown {
    public List<Integer> pattern;
    public int result;

    public Unknown(List<Integer> pattern, int result) {
        this.pattern = pattern;
        this.result = result;
    }
}

public class EasyChallenges {
    public static void main(String[] args) {
        System.out.println(solve(
          Arrays.asList(List.of(1, 1, 1), List.of(1,1,4), List.of(1,4,6)),
          List.of(new Unknown(List.of(1,1,1,1), 5), new Unknown(List.of(1,1,1,4), 6))
        ));
    }

    public static List<List<Integer>> solve(List<List<Integer>> grid, List<Unknown> rules) {
        int height = grid.size();
        int width = grid.get(0).size();

        int[] neighborsI = new int[]{0, 0, 1, 1};
        int[] neighborsJ = new int[]{0, 1, 0, 1};

        List<List<Integer>> newGrid = new ArrayList<>();

        for (int i = 0; i < height - 1; i++) {
            List<Integer> newRow = new ArrayList<>();
            for (int j = 0; j < width - 1; j++) {
                int newElement = 0;
                for (int p = 0; p < rules.size(); p++) {
                    boolean foundRule = true;
                    Unknown rule = rules.get(p);

                    for (int k = 0; k < 4; k++) {
                        int row = i + neighborsI[k];
                        int column = j + neighborsJ[k];
                        int element = grid.get(row).get(column);
                        if (!Objects.equals(rule.pattern.get(k), element)) {
                            foundRule = false;
                            break;
                        }
                    }
                    if (foundRule) {
                        newElement = rule.result;
                        break;
                    }
                }
                newRow.add(newElement);
            }
            newGrid.add(newRow);
        }

        return newGrid;
    }

    public static String decrypt(String firstA, String firstB, String secondB, String secondC, String secretC) {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        Map<Character, Character> cToB = new HashMap<>();
        Map<Character, Character> bToA = new HashMap<>();
        for (int i = 0; i < letters.length(); i++) {
            char letter = letters.charAt(i);

            int index = secondC.indexOf(letter);
            cToB.put(letter, secondB.charAt(index));

            index = firstB.indexOf(letter);
            bToA.put(letter, firstA.charAt(index));
        }
        String decryptedMessage = "";
        for (int i = 0; i < secretC.length(); i++) {
            char letter = secretC.charAt(i);
            if (letter == ' ') {
                decryptedMessage += " ";
            } else {
                decryptedMessage += bToA.get(cToB.get(letter));
            }
        }

        return decryptedMessage;
    }

    public static List<String> solve(int protonsStart, int neutronsStart, int protonsTarget, int neutronsTarget) {
        List<String> actions = new ArrayList<>();

        String test = "";
        int currentProtons = protonsStart;
        int currentNeutrons = neutronsStart;
        int deltaProtons = protonsTarget - protonsStart;
        int deltaNeutrons = neutronsTarget - neutronsStart;
        if (deltaProtons < 0 || deltaNeutrons < 0) {
            int alphas = Math.abs(Math.min(deltaProtons, deltaNeutrons) / 2);
            if (Math.abs(Math.min(deltaProtons, deltaNeutrons)) % 2 == 1) {
                alphas += 1;
            }
            for (int i = 0; i < alphas; i++) {
                actions.add("ALPHA");
                currentProtons -= 2;
                currentNeutrons -= 2;
            }
        }
        for (int i = 0; i < protonsTarget - currentProtons; i++) {
            actions.add("PROTON");
        }
        for (int i = 0; i < neutronsTarget - currentNeutrons; i++) {
            actions.add("NEUTRON");
        }
        return actions;
    }

    public static int[] twoSum_0001(int[] nums, int target) {
        for (int i = 0; i < nums.length; i++) {
            for (int j = 0; j < nums.length; j++) {
                if (i != j && nums[i] + nums[j] == target) {
                    return new int[]{ i, j };
                }
            }
        }

        return null;
    }

    public static ListNode addTwoNumbers_0002(ListNode l1, ListNode l2) {
        ListNode root = new ListNode();
        ListNode current = root;
        int remainder = 0;

        while (l1 != null || l2 != null || remainder != 0) {
            current.next = new ListNode();
            current = current.next;
            int val =
                (l1 == null ? 0 : l1.val) +
                    (l2 == null ? 0 : l2.val) + remainder;

            if (val > 9) {
                val -= 10;
                remainder = 1;
            } else {
                remainder = 0;
            }
            current.val = val;

            l1 = l1 == null ? null : l1.next;
            l2 = l2 == null ? null : l2.next;
        }

        return root.next;
    }

    public static boolean isPalindrome_0009(int x) {
        String strVal = "" + x;
        for (int i = 0; i < (strVal.length() / 2 + 1); i++) {
            if (strVal.charAt(i) != strVal.charAt(strVal.length() - i - 1)) {
                return false;
            }
        }

        return true;
    }

    public static String longestCommonPrefix_0014(String[] strs) {
        boolean end = false;
        String LCP = "";
        int i = 0;
        while (!end) {
            char currentChar = 0;
            for (String str : strs) {
                if (str.length() <= i) {
                    end = true;
                    LCP = str;
                    break;
                }

                if (currentChar == 0) {
                    currentChar = str.charAt(i);
                } else {
                    if (str.charAt(i) != currentChar) {
                        return str.substring(0, i);
                    }
                }
            }

            i++;
        }

        return LCP;
    }

    public static boolean isValid_0020(String s) {
        if (s == null || s.length() == 0) {
            return true;
        }

        Stack<Character> nextBracket = new Stack<>();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case ')' -> {
                    if (nextBracket.isEmpty() ||nextBracket.pop() != '(') {
                        return false;
                    }
                }
                case ']' -> {
                    if (nextBracket.isEmpty() || nextBracket.pop() != '[') {
                        return false;
                    }
                }
                case '}' -> {
                    if (nextBracket.isEmpty() ||nextBracket.pop() != '{') {
                        return false;
                    }
                }
                default -> nextBracket.add(c);
            }
        }

        return nextBracket.isEmpty();
    }

    public static ListNode mergeTwoLists_0021(ListNode list1, ListNode list2) {
        ListNode root = new ListNode();
        ListNode current = root;

        while (list1 != null || list2 != null) {
            if (list1 == null) {
                current.next = list2;
                break;
            }

            if (list2 == null) {
                current.next = list1;
                break;
            }

            if (list1.val <= list2.val) {
                current.next = new ListNode(list1.val);
                current = current.next;
                list1 = list1.next;
            } else {
                current.next = new ListNode(list2.val);
                current = current.next;
                list2 = list2.next;
            }
        }

        return root.next;
    }

    public static int removeDuplicates_0026(int[] nums) {
        if (nums == null || nums.length <= 1) {
            return nums == null ? 0 : nums.length;
        }

        int nextSpot = 1;
        int k = 1;
        while (k < nums.length) {
            if (nums[nextSpot - 1] == nums[k]) {
                k++;
            } else {
                nums[nextSpot] = nums[k];
                nextSpot++;
            }
        }
        return nextSpot;
    }

    public static int removeElement_0027(int[] nums, int val) {
        if (nums == null || nums.length == 0) {
            return 0;
        }

        int nextSpot = 0, k = 0;
        while (k < nums.length) {
            if (nums[k] != val) {
                nums[nextSpot] = nums[k];
                nextSpot++;
            }
            k++;
        }

        return nextSpot;
    }
}

