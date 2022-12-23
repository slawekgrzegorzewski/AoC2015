package com.adventofcode;

import com.adventofcode.input.Input;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.Predicate;

public class Day4 {


    private final String input;

    public Day4() throws IOException {
        input = Input.day4();
    }

    long part1() throws NoSuchAlgorithmException {
        return findFirstIndex(b -> b >> 4 == (byte) 0);
    }

    long part2() throws NoSuchAlgorithmException {
        return findFirstIndex(b -> b == (byte) 0);
    }

    private long findFirstIndex(Predicate<Byte> thirdBytePredicate) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        long i = 0L;
        byte[] digest;
        do {
            String s = input + ++i;
            md.update(s.getBytes());
            digest = md.digest();
        } while (!(digest[0] == (byte) 0 && digest[1] == (byte) 0 && thirdBytePredicate.test(digest[2])));
        return i;
    }
}


