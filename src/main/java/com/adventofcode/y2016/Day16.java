package com.adventofcode.y2016;

import com.adventofcode.y2016.input.Input;

import java.io.IOException;

public class Day16 {
    private final char[] initialData;


    public Day16() throws IOException {
        this.initialData = Input.day16();
    }

    String part1() {
        return work(272);
    }

    String part2() {
        return work(35651584);
    }

    private String work(int desiredLength) {
        char[] data = initialData;
        while (data.length < desiredLength) {
            data = applyDragonCurve(data);
        }
        data = trimTo(data, desiredLength);
        char[] checksum = checksum(data);
        return new String(checksum);
    }

    private char[] checksum(char[] data) {
        char[] checksum = data;
        while ((checksum = calculateChecksum(checksum)).length % 2 == 0) ;
        return checksum;
    }

    private char[] calculateChecksum(char[] data) {
        char[] checksum = new char[data.length / 2];
        for (int i = 0; i < data.length; i += 2) {
            checksum[i / 2] = data[i] == data[i + 1] ? '1' : '0';
        }
        return checksum;
    }

    private char[] trimTo(char[] data, int desiredLength) {
        char[] result = new char[desiredLength];
        System.arraycopy(data, 0, result, 0, desiredLength);
        return result;
    }

    private char[] applyDragonCurve(char[] data) {
        char[] result = new char[data.length * 2 + 1];
        System.arraycopy(data, 0, result, 0, data.length);
        result[data.length] = '0';
        for (int i = data.length + 1; i < result.length; i++) {
            char value = data[2 * data.length - i];
            result[i] = value == '0' ? '1' : '0';
        }
        return result;
    }
}
