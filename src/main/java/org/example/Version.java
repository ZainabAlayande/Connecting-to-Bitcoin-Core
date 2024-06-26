package org.example;

import java.security.NoSuchAlgorithmException;

public class Version {

    public static String createVersionMessage() throws NoSuchAlgorithmException {
        String payload = Payload.createPayload();
        String header = Header.createHeader(payload);
        String message = header + payload;
        System.out.println("This is the Version Message == " + message);
        return message;
    }
}
