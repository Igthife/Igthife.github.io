package com.example.cs360project3samuelhemond;

import org.junit.Test;

import static org.junit.Assert.*;

import android.util.Log;

import java.util.Random;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void encryptionSHA() {
        EncryptionAlgorithm encryptionAlgorithm = new EncryptionAlgorithm();

        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength;
        Random random = new Random();
        for(int i = 0; i < 100000; i++) {
            //Log.i("TEST", "Number" + i);
            targetStringLength = 1 + (i % 128);
            String generatedString = random.ints(leftLimit, rightLimit + 1)
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();

            //test MessageDigest hash vs my hashSHA256
            assertEquals( encryptionAlgorithm.hashMessageDigest(generatedString ), encryptionAlgorithm.hashSHA256(generatedString));
        }

        assertEquals( encryptionAlgorithm.hashMessageDigest("asdfhjlljh"), encryptionAlgorithm.hashSHA256("asdfhjlljh"));
    }
}