package org.jackalgera;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MediumChallengesTest {

    static Stream<Arguments> testLengthOfLongestSubstring_0003() {
        return Stream.of(
            Arguments.of(3, "abcabccbb"),
            Arguments.of(1, "bbbbb"),
            Arguments.of(3, "pwwkew")
        );
    }

    @ParameterizedTest
    @MethodSource
    void testLengthOfLongestSubstring_0003(int expectedLength, String s) {
        assertEquals(expectedLength, MediumChallenges.lengthOfLongestSubstring(s));
    }

    static Stream<Arguments> testLongestPalindrome_0005() {
        return Stream.of(
            Arguments.of("babad", "bab"),
            Arguments.of("cbbd", "bb"),
            Arguments.of("bb", "bb"),
            Arguments.of("ccc", "ccc")
        );
    }

    @ParameterizedTest
    @MethodSource
    void testLongestPalindrome_0005(String input, String expectedValue) {
        assertEquals(expectedValue, MediumChallenges.longestPalindrome_0005(input));
    }
}
