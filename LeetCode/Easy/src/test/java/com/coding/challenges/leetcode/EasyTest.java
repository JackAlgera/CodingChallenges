package com.coding.challenges.leetcode;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class EasyTest {


  static Stream<Arguments> testIsPalindrome_0009() {
    return Stream.of(
            Arguments.of(121, true),
            Arguments.of(122, false),
            Arguments.of(123, false),
            Arguments.of(1, true)
    );
  }

  @ParameterizedTest
  @MethodSource
  void testIsPalindrome_0009(int number, boolean isPalindrome) {
    assertEquals(isPalindrome, Easy.isPalindrome_0009(number));
  }

  static Stream<Arguments> testLongestCommonPrefix_0014() {
    return Stream.of(
            Arguments.of(new String[]{"flower","flow","flight"}, "fl"),
            Arguments.of(new String[]{"dog","racecar","car"}, "")
    );
  }

  @ParameterizedTest
  @MethodSource
  void testLongestCommonPrefix_0014(String[] strings, String expectedLCP) {
    assertEquals(expectedLCP, Easy.longestCommonPrefix_0014(strings));
  }

  static Stream<Arguments> testIsValid_0020() {
    return Stream.of(
            Arguments.of("()[]{}", true),
            Arguments.of("(]", false),
            Arguments.of("()", true)
    );
  }

  @ParameterizedTest
  @MethodSource
  void testIsValid_0020(String input, boolean isValid) {
    assertEquals(isValid, Easy.isValid_0020(input));
  }

  static Stream<Arguments> testRemoveDuplicates_0026() {
    return Stream.of(
            Arguments.of(new int[]{1, 1, 2}, 2),
            Arguments.of(new int[]{0, 0, 1, 1, 1, 2, 2, 3, 3, 4}, 5)
    );
  }

  @ParameterizedTest
  @MethodSource
  void testRemoveDuplicates_0026(int[] nums, int k) {
    assertEquals(k, Easy.removeDuplicates_0026(nums));
  }

  static Stream<Arguments> testRemoveElement_0027() {
    return Stream.of(
            Arguments.of(new int[]{3, 2, 2, 3}, 3, 2),
            Arguments.of(new int[]{0,1,2,2,3,0,4,2}, 2, 5),
            Arguments.of(new int[]{1}, 1, 0)
    );
  }

  @ParameterizedTest
  @MethodSource
  void testRemoveElement_0027(int[] nums, int val, int k) {
    assertEquals(k, Easy.removeElement_0027(nums, val));
  }
}