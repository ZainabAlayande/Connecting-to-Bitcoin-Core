package org.example;

import static org.example.BitcoinUtils.*;

public class Payload {

    public static String createPayload() {
        // Create the payload for a version message
        String payload = "";
        payload  = reverseBytes(size(convertIntegerToStringHex(70014), 4));
        payload += reverseBytes(size(convertIntegerToStringHex(0), 8)); // services e.g. (1<<3 | 1<<2 | 1<<0)
        payload += reverseBytes(size(convertIntegerToStringHex(1640961477), 8)); // time
        payload += reverseBytes(size(convertIntegerToStringHex(0), 8)); // remote node services
        payload += "00000000000000000000ffff2e13894a";   // remote node ipv6 (https://dnschecker.org/ipv4-to-ipv6.php)
        payload += size(convertIntegerToStringHex(8333), 2); // remote node port
        payload += reverseBytes(size(convertIntegerToStringHex(0), 8)); // local node services
        payload += "00000000000000000000ffff7f000001";    // local node ipv6
        payload += size(convertIntegerToStringHex(8333), 2);     // local node port
        payload += reverseBytes(size(convertIntegerToStringHex(0), 8));    // nonce
        payload += "00";   // user agent (compact_size, followed by ascii bytes)
        payload += reverseBytes(size(convertIntegerToStringHex(0), 4));
        System.out.println("This is the Payload == " + payload);
        return payload;
    }


}
