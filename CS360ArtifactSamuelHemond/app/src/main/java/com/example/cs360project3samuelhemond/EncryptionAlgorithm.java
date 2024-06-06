package com.example.cs360project3samuelhemond;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Arrays;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


//class containing both my hashSHA256 and hashMessageDigest to output and test hashed inputs
public class EncryptionAlgorithm {
    //charset for project
    private final Charset UTF8_CHARSET = Charset.forName("UTF-8");
    //first 32 bits of the fractional parts of the square roots of the first 8 primes 2..19
    private static final int[] h_master = {
            0x6a09e667,
            0xbb67ae85,
            0x3c6ef372,
            0xa54ff53a,
            0x510e527f,
            0x9b05688c,
            0x1f83d9ab,
            0x5be0cd19
    };
    //first 32 bits of the fractional parts of the cube roots of the first 64 primes 2..311
    private static final int[] K_ints = {
            0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
            0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
            0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
            0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
            0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
            0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
            0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
            0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
    };

    //varible for bytes being worked on
    byte[] currentBytes;

    //varible for 16 32 bit words and 48 additional words for processing
    int[] words;


    //test function
    public String hashMessageDigest(String input){

        try {

            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = messageDigest.digest(input.getBytes(StandardCharsets.UTF_8));

            return bytesToHex(encodedHash);

        } catch ( NoSuchAlgorithmException e ) {
            return null;
        }
    }


    //Pseudocode https://en.wikipedia.org/wiki/SHA-2 and explanation video https://www.youtube.com/watch?v=orIgy2MjqrA and bit by bit demonstration https://sha256algorithm.com/

    //Method to hash input string using SHA-256 and return a hashed string
    public String hashSHA256(String input){

        //copy h_master array
        int[] h_ints = new int[8];
        System.arraycopy(h_master, 0, h_ints, 0, h_master.length);

        //set variable for bytes being worked on
        currentBytes = new byte[64];

        //set variable for 16 32 bit words and 48 additional words for processing
        words = new int[64];

        //prepare registers
        int a,b,c,d,e,f,g,h;

        //prepare output buffer
        byte[] outputBytes = new byte[32];

        //convert string to byte[] and process it
        byte[] inputBytes = encodeUTF8(input);
        byte[] preProcessed = preProcess(inputBytes);

        //main loop over each 512 bit or 64 byte chunk
        for(int chunk = 0; chunk < preProcessed.length; chunk+=64 ){

            //copy working bytes to currentBytes
            currentBytes = Arrays.copyOfRange(preProcessed, chunk, chunk+64);

            //full the first 16 slots of words[] with the current bytes
            for(int i = 0; i < 16; i++){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    words[i] = new BigInteger(Arrays.copyOfRange(currentBytes, i * 4, i * 4 + 4)).intValueExact();
                }else{
                    words[i] = new BigInteger(Arrays.copyOfRange(currentBytes, i * 4, i * 4 + 4)).intValue();
                }
            }

            //load working registers
            a = h_ints[0];
            b = h_ints[1];
            c = h_ints[2];
            d = h_ints[3];
            e = h_ints[4];
            f = h_ints[5];
            g = h_ints[6];
            h = h_ints[7];

            // prepare pur words array for using existing data with a first pass
            for (int i = 16; i < 64; ++i) {
                // rotate right XOR rotate right XOR right and shift appropriate amounts
                int sigmaLower0 = Integer.rotateRight(words[i - 15], 7) ^ Integer.rotateRight(words[i - 15], 18) ^ (words[i - 15] >>> 3);
                // rotate right XOR rotate right XOR right and shift appropriate amounts
                int sigmaLower1 = Integer.rotateRight(words[i - 2], 17) ^ Integer.rotateRight(words[i - 2], 19) ^ (words[i - 2] >>> 10);
                //update target word
                words[i] = words[i - 16] + sigmaLower0 + words[i - 7] + sigmaLower1;
            }

            //process all words with a second pass
            for(int i = 0; i < 64; i++){
                // rotate right XOR rotate right XOR right and shift appropriate amounts
                int sigmaUpper0 = Integer.rotateRight(a, 2) ^ Integer.rotateRight(a, 13) ^ Integer.rotateRight(a, 22);
                // rotate right XOR rotate right XOR right and shift appropriate amounts
                int sigmaUpper1 = Integer.rotateRight(e, 6) ^ Integer.rotateRight(e, 11) ^ Integer.rotateRight(e, 25);
                //(e AND f) XOR ((NOT e) AND g)
                int choice = (e & f) ^ ((~e) & g);
                //(a AND b) XOR (a AND c) XOR (b AND c)
                int majority = (a & b) ^ (a & c) ^ (b & c);

                //combine
                int temp1 = h + sigmaUpper1 + choice + K_ints[i] + words[i];
                int temp2 = sigmaUpper0 + majority;

                //complete calculations
                h = g;
                g = f;
                f = e;
                e = d + temp1;
                d = c;
                c = b;
                b = a;
                a = temp1 + temp2;

            }

            //store completed pass to combine with next pass
            h_ints[0] = a + h_ints[0];
            h_ints[1] = b + h_ints[1];
            h_ints[2] = c + h_ints[2];
            h_ints[3] = d + h_ints[3];
            h_ints[4] = e + h_ints[4];
            h_ints[5] = f + h_ints[5];
            h_ints[6] = g + h_ints[6];
            h_ints[7] = h + h_ints[7];

        }
        //setup bits after input in the form of 10000000-(length of user input in binary) | end of array
        ByteBuffer outputBuffer = ByteBuffer.wrap(outputBytes);
        outputBuffer.putInt(h_ints[0]);
        outputBuffer.putInt(h_ints[1]);
        outputBuffer.putInt(h_ints[2]);
        outputBuffer.putInt(h_ints[3]);
        outputBuffer.putInt(h_ints[4]);
        outputBuffer.putInt(h_ints[5]);
        outputBuffer.putInt(h_ints[6]);
        outputBuffer.putInt(h_ints[7]);
        outputBytes = outputBuffer.array();

        return bytesToHex(outputBytes);
    }

    //setup padding
    private byte[] preProcess(byte[] userInput) {
        //get length for usage and number of empty bits
        int inputLength = userInput.length;
        int remainder = inputLength % 64;

        //length of total padding
        int paddingLength;
        if ((remainder < 56)) {
            paddingLength = 64 - remainder;
        } else {
            paddingLength = 128 - remainder;
        }

        //prepare output buffer and "filling" empty buffer for 0's
        byte[] outputBytes = new byte[inputLength + paddingLength];
        byte[] fillingBytes = new byte[paddingLength - 9];

        //setup bits after input in the form of 10000000-(length of user input in binary) | end of array
        ByteBuffer outputBuffer = ByteBuffer.wrap(outputBytes);
        outputBuffer.put(userInput);
        outputBuffer.put((byte) 0x80);     //setup first bit
        outputBuffer.put(fillingBytes);        //setup 0's between first bit and length
        outputBuffer.putLong(inputLength * 8); //length of bits in input
        outputBytes = outputBuffer.array();

        return outputBytes;
    }

    //helper method to print out data
    private String decodeUTF8(byte[] bytes) {
        return new String(bytes, UTF8_CHARSET);
    }

    //helper method to print out data
    private byte[] encodeUTF8(String string) {
        return string.getBytes(UTF8_CHARSET);
    }

    //helper method to print out data
    private static final byte[] HEX_ARRAY = "0123456789abcdef".getBytes(StandardCharsets.UTF_8);
    public static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }
}
