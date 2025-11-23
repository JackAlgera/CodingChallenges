package com.coding.challenges.leetcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Easy {
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
