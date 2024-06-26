package org.example;

import java.security.NoSuchAlgorithmException;

import static org.example.BitcoinUtils.*;

public class Header {

    public static String createHeader(String payload) throws NoSuchAlgorithmException {
        // Create the message header
        String magicBytes = "f9beb4d9";
        String command = ascii2hex("version"); // 76 65 72 73 69 6F 6E 00 00 00 00 00
        String size = reverseBytes(size(convertIntegerToStringHex(payload.length() / 2), 4)); // 55 00 00 00
        String checksum = checksum(payload);

        // Combine all parts to create the header
        String header = magicBytes + command + size + checksum;
        System.out.println("This is the Header == " + header);
        return header;

    }
}
