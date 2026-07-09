package com.adventofcode.y2017;

import com.adventofcode.y2017.input.Input;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

public class Day10 {
    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();
    private final String lenghts;


    public Day10() throws IOException {
        this.lenghts = Input.day10();
    }

    long part1() {
        int[] lengthsParsed = Arrays.stream(lenghts.split(","))
                .mapToInt(Integer::parseInt)
                .toArray();
        int length = 256;
        int[] list = new int[length];
        for (int i = 0; i < list.length; i++) {
            list[i] = i;
        }
        rotate(lengthsParsed, list, 1);
        return (long) list[0] * list[1];
    }

    String part2() {
        byte[] bytes = lenghts.getBytes();
        int[] lengths = new int[bytes.length + 5];
        int length = 256;
        int[] list = new int[length];
        for (int i = 0; i < list.length; i++) {
            list[i] = i;
        }
        for (int i = 0; i < bytes.length; i++) {
            lengths[i] = bytes[i];
        }
        lengths[bytes.length] = 17;
        lengths[bytes.length + 1] = 31;
        lengths[bytes.length + 2] = 73;
        lengths[bytes.length + 3] = 47;
        lengths[bytes.length + 4] = 23;
        rotate(lengths, list, 64);
        int [] denseHash = new int[16];
        for (int i = 0; i < denseHash.length; i++) {
            int start = i * 16;
            int xor = list[start];
            for (int j = start + 1; j < start + 16; j++) {
                xor ^= list[j];
            }
            denseHash[i] = xor;
        }
        return convertToHexString(denseHash);
    }

    private static void rotate(int[] lengthsParsed, int[] list, int rounds) {
        int position = 0, skip = 0;
        for (; rounds > 0; rounds--) {
            for (int lenght : lengthsParsed) {
                int[] sublist = new int[lenght];
                for (int i = position; i < position + lenght; i++) {
                    sublist[i - position] = list[i % list.length];
                }
                int[] reversed = new int[sublist.length];
                for (int i = 0; i < sublist.length; i++) {
                    reversed[i] = sublist[sublist.length - i - 1];
                }
                for (int i = position; i < position + lenght; i++) {
                    list[i % list.length] = reversed[i - position];
                }
                position += sublist.length + skip;
                position %= list.length;
                skip++;
            }
        }
    }
    private String convertToHexString(int[] denseHash) {
        char[] hex = new char[32];
        for (int i = 0, j = 0; j < 32; i++) {
            int b = denseHash[i] & 0xff;
            hex[j++] = HEX_DIGITS[b >>> 4];
            hex[j++] = HEX_DIGITS[b & 0x0f];
        }
        return new String(hex);
    }
}
