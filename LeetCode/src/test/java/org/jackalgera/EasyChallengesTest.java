package org.jackalgera;

import org.jackalgera.utils.ListNode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EasyChallengesTest {

    static Stream<Arguments> addTwoNumbers_0002() {
        return Stream.of(
            Arguments.of(new ListNode(new int[]{ 2, 4, 3 }), new ListNode(new int[]{ 5, 6, 4 }), new ListNode(new int[]{ 7, 0, 8 })),
            Arguments.of(new ListNode(new int[]{ 0 }), new ListNode(new int[]{ 0 }), new ListNode(new int[]{ 0 })),
            Arguments.of(new ListNode(new int[]{ 9,9,9,9,9,9,9 }), new ListNode(new int[]{ 9,9,9,9 }), new ListNode(new int[]{ 8,9,9,9,0,0,0,1 })),
            Arguments.of(new ListNode(new int[]{ 9,9,9,9,9,9,9 }), new ListNode(new int[]{ 9,9,9,9 }), new ListNode(new int[]{ 8,9,9,9,0,0,0,1 }))
        );
    }

    @ParameterizedTest
    @MethodSource
    void addTwoNumbers_0002(ListNode l1, ListNode l2, ListNode expectedOutPut) {
        assertEquals(expectedOutPut, EasyChallenges.addTwoNumbers_0002(l1, l2));
    }

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
        assertEquals(isPalindrome, EasyChallenges.isPalindrome_0009(number));
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
        assertEquals(expectedLCP, EasyChallenges.longestCommonPrefix_0014(strings));
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
        assertEquals(isValid, EasyChallenges.isValid_0020(input));
    }

    static Stream<Arguments> testMergeTwoLists_0021() {
        return Stream.of(
            Arguments.of(new ListNode(new int[]{ 1, 2, 4 }), new ListNode(new int[]{ 1, 3, 4 }), new ListNode(new int[]{ 1, 1, 2, 3, 4, 4 }))
        );
    }

    @ParameterizedTest
    @MethodSource
    void testMergeTwoLists_0021(ListNode l1, ListNode l2, ListNode expectedOutPut) {
        assertEquals(expectedOutPut, EasyChallenges.mergeTwoLists_0021(l1, l2));
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
        assertEquals(k, EasyChallenges.removeDuplicates_0026(nums));
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
        assertEquals(k, EasyChallenges.removeElement_0027(nums, val));
    }
}
