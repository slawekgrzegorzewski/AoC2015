package com.adventofcode.y2016;

import com.adventofcode.y2016.input.Input;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Day5 {
    private final String doorId;


    public Day5() throws IOException {
        this.doorId = Input.day5();
    }

    String part1() throws NoSuchAlgorithmException {
        StringBuilder password = new StringBuilder();
        int index = 0;
        while (password.length() < 8) {
            byte[] digest = md5(index);
            if (digest[0] == 0 && digest[1] == 0 && (digest[2] & 0xF0) == 0) {
                password.append(Character.forDigit(digest[2] & 0x0F, 16));
            }
            index++;
        }
        return password.toString();
    }

    String part2() throws NoSuchAlgorithmException {
        StringBuilder password = new StringBuilder("________");
        int index = 0;
        while (!completed(password)) {
            byte[] digest = md5(index);
            if (digest[0] == 0 && digest[1] == 0 && (digest[2] & 0xF0) == 0) {
                int position = digest[2] & 0x0F;
                if (position < 8) {
                    char c = Character.forDigit((digest[3] & 0xF0) >> 4 , 16);
                    if (password.charAt(position) == '_') {
                        password.setCharAt(position, c);
                    }
                }
            }
            index++;
        }
        return password.toString();
    }

    private byte[] md5(int index) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update((doorId + index).getBytes(StandardCharsets.UTF_8));
        return md.digest();
    }

    private boolean completed(StringBuilder password) {
        for (int i = 0; i < 8; i++)
            if (password.charAt(i) == '_')
                return false;
        return true;
    }
}
