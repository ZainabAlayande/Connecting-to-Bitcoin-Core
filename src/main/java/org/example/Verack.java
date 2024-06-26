package org.example;

import java.security.NoSuchAlgorithmException;

import static org.example.BitcoinUtils.*;

public class Verack {

    public static String createVerackMessage() throws NoSuchAlgorithmException {
        String payload = ""; // verack has no payload, it's just a message header
        String magicBytes = "f9beb4d9";
        String command = ascii2hex("verack");
        String size = reverseBytes(size(convertIntegerToStringHex(payload.length() / 2), 4));
        String checksum = checksum(payload);
        return magicBytes + command + size + checksum + payload;
    }
}
