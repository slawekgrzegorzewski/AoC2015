package com.adventofcode.y2016;

import com.adventofcode.y2016.input.Input;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Day17 {

    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();
    private final MessageDigest md = MessageDigest.getInstance("MD5");
    private final String passcode;


    public Day17() throws IOException, NoSuchAlgorithmException {
        this.passcode = Input.day17();
    }

    long part1() {
        return 0L;
    }

    long part2() {
        return 0L;
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
