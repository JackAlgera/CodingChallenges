package com.coding.challenges.google.foobar;

public class Exercise2B {
    public static void main(String[] args) {
        System.out.println(getSolarDoomsDaySolution(">----<"));
        System.out.println(getSolarDoomsDaySolution("<<>><"));
    }

    public static int getSolarDoomsDaySolution(String s) {
        int totalSalutes = 0;
        int currentEmployeesGoingRight = 0;
        for (int i = 0; i < s.length(); i++) {
            char currentChar = s.charAt(i);

            switch (currentChar) {
                case '>':
                    currentEmployeesGoingRight++;
                    break;
                case '<':
                    totalSalutes += currentEmployeesGoingRight * 2;
                    break;
            }
        }

        return totalSalutes;
    }
}
