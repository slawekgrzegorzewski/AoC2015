package com.adventofcode.y2016;

import com.adventofcode.y2016.input.Input;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day14 {
    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();

    private final String salt;


    public Day14() throws IOException {
        this.salt = Input.day14();
    }

    long part1() throws NoSuchAlgorithmException {
        return work(1);
    }

    long part2() {
        return work(2017);
    }

    private int work(int repeat) {
        Pattern threeCharactersInRow = Pattern.compile("([0-9a-f])\\1\\1");
        Pattern fiveCharactersInRow = Pattern.compile("([0-9a-f])\\1{4}");
        int foundKeys = 0;
        int index = -1;
        Map<Integer, HashStats> hashes = new HashMap<>(64);
        while (foundKeys < 64) {
            index++;
            String valueToFound = null;
            for (int i = index; i <= index + 1000; i++) {
                HashStats hashStats = hashes.computeIfAbsent(i, i1 -> {
                    String hash = md5(i1, repeat);
                    Matcher matcher = threeCharactersInRow.matcher(hash);
                    String three = matcher.find() ? matcher.group(1) : null;
                    matcher = fiveCharactersInRow.matcher(hash);
                    List<String> five = new ArrayList<>();
                    while (matcher.find()) {
                        five.add(matcher.group(1));
                    }
                    return new HashStats(three, five);
                });
                if (i == index) {
                    valueToFound = hashStats.threeCharacter;
                    continue;
                }
                if (valueToFound == null) break;
                if (hashStats.fiveCharacters.contains(valueToFound)) {
                    foundKeys++;
                    break;
                }
            }
        }
        return index;
    }

    private record HashStats(String threeCharacter, List<String> fiveCharacters) {
    }

    private String md5(int index, int repeat) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            String value = salt + index;
            for (int i = 0; i < repeat; i++) {
                value = md5(value, md);
            }
            return value;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String md5(String value, MessageDigest md) {
        md.reset();
        byte[] hash = md.digest(value.getBytes(StandardCharsets.UTF_8));
        char[] hex = new char[hash.length * 2];
        for (int i = 0, j = 0; i < hash.length; i++) {
            int b = hash[i] & 0xff;
            hex[j++] = HEX_DIGITS[b >>> 4];
            hex[j++] = HEX_DIGITS[b & 0x0f];
        }
        return new String(hex);
    }
}
