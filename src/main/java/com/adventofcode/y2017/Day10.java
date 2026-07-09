package com.adventofcode.y2017;

import com.adventofcode.y2017.input.Input;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.util.Arrays;

public class Day10 {
    public static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();
    private final String lenghts;


    public Day10() throws IOException {
        this.lenghts = Input.day10();
    }

    long part1() {
        int[] lengthsParsed = Arrays.stream(lenghts.split(","))
                .mapToInt(Integer::parseInt)
                .toArray();
        int[] list = inputList();
        rotate(lengthsParsed, list, 1);
        return (long) list[0] * list[1];
    }

    String part2() {
        return knotHash(lenghts);
    }

    public static @NonNull String knotHash(String key) {
        byte[] bytes = key.getBytes();
        int[] list = inputList();
        int[] lengths = new int[bytes.length + 5];
        for (int i = 0; i < bytes.length; i++) {
            lengths[i] = bytes[i];
        }
        System.arraycopy(new int[]{17, 31, 73, 47, 23}, 0, lengths, bytes.length, 5);
        rotate(lengths, list, 64);
        byte[] denseHash = denseHash(list);
        return convertToHexString(denseHash);
    }

    private static int[] inputList() {
        int length = 256;
        int[] list = new int[length];
        for (int i = 0; i < list.length; i++) {
            list[i] = i;
        }
        return list;
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

    private static byte[] denseHash(int[] list) {
        byte[] denseHash = new byte[16];
        for (int i = 0; i < denseHash.length; i++) {
            int start = i * 16;
            int xor = list[start];
            for (int j = start + 1; j < start + 16; j++) {
                xor ^= list[j];
            }
            denseHash[i] = (byte) (xor & 0xff);
        }
        return denseHash;
    }

    private static String convertToHexString(byte[] denseHash) {
        char[] hex = new char[32];
        for (int i = 0, j = 0; j < 32; i++) {
            int b = denseHash[i] & 0xff;
            hex[j++] = HEX_DIGITS[b >>> 4];
            hex[j++] = HEX_DIGITS[b & 0x0f];
        }
        return new String(hex);
    }
}
