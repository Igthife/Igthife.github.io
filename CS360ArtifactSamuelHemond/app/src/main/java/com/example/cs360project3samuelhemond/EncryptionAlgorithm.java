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

public class EncryptionAlgorithm {
    private final Charset UTF8_CHARSET = Charset.forName("UTF-8");
    private static final int[] h_ints = {
            0x6a09e667,
            0xbb67ae85,
            0x3c6ef372,
            0xa54ff53a,
            0x510e527f,
            0x9b05688c,
            0x1f83d9ab,
            0x5be0cd19
    };
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
    byte[] currentBytes = new byte[64];

    //varible for 16 32 bit words and 48 additional words for processing
    int[] words = new int[64];


    //test function
    public byte[] hashMessageDigest(String input){

        try {

            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = messageDigest.digest(input.getBytes(StandardCharsets.UTF_8));

            return encodedhash;

        } catch ( NoSuchAlgorithmException e ) {
            return null;
        }
    }


    //Psudocode https://en.wikipedia.org/wiki/SHA-2 and explanation video https://www.youtube.com/watch?v=orIgy2MjqrA and bit by bit demonstation https://sha256algorithm.com/

    @RequiresApi(api = Build.VERSION_CODES.S)
    public String hashSHA256(String input){

        if(input.length() > 128){
            return null;        //TODO log
        }else if(input.length() <=7 ){
            return null;        //TODO log
        }



        byte[] inputBytes = encodeUTF8(input);
        byte[] preProcessed = preProcess(inputBytes);

        System.out.println("input bytes:        " + bytesToHex(inputBytes));
        System.out.println("preProcessed bytes: " + bytesToHex(preProcessed));


        for(int chunk = 0; chunk < preProcessed.length; chunk+=64 ){
            currentBytes = Arrays.copyOfRange(preProcessed, chunk, chunk+64);
            System.out.println("current bytes:      " + bytesToHex(currentBytes));//TODO
            for(int i = 0; i < 16; i++){
                words[i] = new BigInteger(Arrays.copyOfRange(currentBytes, i * 4, i * 4 + 4)).intValueExact();
            }
            /*for (int i = 0; i < 64; i++) {        //debug binary/hex dump //FIXME
                //System.out.println("current word[" + i + "]:      " + Integer.toHexString(words[i]));
                System.out.println("current word[" + i + "]:      " + Integer.toBinaryString(words[i]));
            }*/

            // rotate right XOR rotate right XOR right shift 3 and add with two other values
            for (int i = 16; i < 64; ++i) {
                int sigma0 = Integer.rotateRight(words[i - 15], 7) ^ Integer.rotateRight(words[i - 15], 18) ^ (words[i - 15] >>> 3);

                int sigma1 = Integer.rotateRight(words[i - 2], 17) ^ Integer.rotateRight(words[i - 2], 19) ^ (words[i - 2] >>> 10);

                words[i] = words[i - 16] + sigma0 + words[i - 7] + sigma1;
            }
            for (int i = 0; i < 64; i++) {        //debug binary/hex dump //FIXME
                //System.out.println("current word[" + i + "]:      " + Integer.toHexString(words[i])); //FIXME
                System.out.println("current word[" + i + "]:      " + Integer.toBinaryString(words[i]));
            }

        }






        return "";
    }

    //setup padding
    private byte[] preProcess(byte[] userInput) {

        int inputLength = userInput.length;
        int remainder = inputLength % 64;

        //length of total padding
        int paddingLength;
        if ((remainder < 56)) {
            paddingLength = 64 - remainder;
        } else {
            paddingLength = 128 - remainder;
        }


        byte[] paddingBytes = new byte[paddingLength];
        byte[] fillingBytes = new byte[paddingLength - 9];

        //setup bits after input in the form of 10000000-(length of user input in binary) | end of array
        ByteBuffer paddingBuffer = ByteBuffer.wrap(paddingBytes);
        paddingBuffer.put((byte) 0x80);     //setup first bit
        paddingBuffer.put(fillingBytes);        //setup 0's between first bit and length
        paddingBuffer.putLong(inputLength * 8); //length of bits in input
        paddingBytes = paddingBuffer.array();

        //System.out.println("buffer: " + bytesToHex(paddingBytes));  //FIXME

        //combined user input with the padding bits assembled above //TODO could be simplified to a single output buffer
        byte[] output = new byte[inputLength + paddingLength];
        ByteBuffer outputBuffer = ByteBuffer.wrap(output);
        outputBuffer.put(userInput);
        outputBuffer.put(paddingBytes);
        output = outputBuffer.array();


        return output;
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
    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
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
