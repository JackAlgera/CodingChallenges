package com.coding.challenges.hackerrank.interview_2022;

public class Exercise2 {

  public static void main(String[] args) {
    System.out.println(getMin("()))"));   // = 2
    System.out.println(getMin("()()"));   // = 0
    System.out.println(getMin("))))()")); // = 4
  }

  public static int getMin(String s) {
    int balance = 0;  // We don't need to figure out how many '(' and ')', just the total missing chars per prefix
    int minChars = 0; // Total missing chars

    for (int i = 0; i < s.length(); i++) {
      char currentChar = s.charAt(i);

      switch (currentChar) {
        case '(':
          balance += 1;
          break;
        case ')':
          balance -= 1;
          break;
      }

      if (balance == -1) {
        minChars++;
        balance++;
      }
    }

    return minChars;
  }
}
