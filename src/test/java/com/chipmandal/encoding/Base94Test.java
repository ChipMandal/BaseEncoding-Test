package com.chipmandal.encoding;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class Base94Test {
    static byte[] standardAlphabet =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz~`!@#$%^&*-_=+|\\;:\"'/?.>,<(){}[]".getBytes(StandardCharsets.UTF_8);


    @Test
//    @RepeatedTest(100)
    public void encodeDecode() {
        Base94 base94 = BaseEncoding.Base94;
        int length = 8;
        byte[] bytes = new byte[length];
//        Arrays.fill(bytes, (byte) 0xFF);
//        random.nextBytes(bytes);
        String encoded = base94.encodeString(bytes);
        byte[] decoded = base94.decodeString(encoded);
        //Take a random base94 string, encode and decode it
        assertTrue(Arrays.equals(bytes, decoded), "Failed for length " + length);
    }


    @Test
    @RepeatedTest(100)
    public void incrementalLengthencodeDecode() {
        Base94 base94 = BaseEncoding.Base94;
        for ( int length = 0; length < 2000; length++) {
            Random random = new Random();
//            int length = 164;//random.nextInt(10000);
            byte[] bytes = new byte[length];
//        Arrays.fill(bytes, (byte) 0xFF);
            random.nextBytes(bytes);
            String encoded = base94.encodeString(bytes);
            byte[] decoded = base94.decodeString(encoded);
            //Take a random base94 string, encode and decode it
            assertTrue(Arrays.equals(bytes, decoded), "Failed for length " + length);
        }
    }


    @Test
    @RepeatedTest(5000)
    public void randomLengthsRandomEncodeDecode() {
        assertEquals(94, Base94.standardAlphabet.length);
        Base94 base94 = BaseEncoding.Base94;
        Random random = new Random();
        int length = random.nextInt(5000);
        byte[] bytes = new byte[length];
//        Arrays.fill(bytes, (byte) 0xFF);
        random.nextBytes(bytes);
        String encoded = base94.encodeString(bytes);
        byte[] decoded = base94.decodeString(encoded);
        assertTrue(Arrays.equals(bytes, decoded), "Failed for length " + length);
    }
    @Test
    @DisplayName("Invalid size of alphabet")
    public void testAlphabetCheck() {

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Base94("abc".toCharArray());
        });
        assertTrue(exception.getMessage().contains("Size of alphabet"), "Size mismatch");

    }

    @Test
    @DisplayName("Duplicates in input alphabet")
    public void duplicatesInInput() {

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Base94("0123456789ABCDEFGHIJKLMNOPQRSTUVWXXZabcdefghijklmnopqrstuvwxyz~`!@#$%^&*-_=+|\\;:\"'/?.>,<(){}[]".toCharArray());
        });
        assertTrue(exception.getMessage().contains("Invalid or duplicate"), "Invalid or duplicate");

    }


}