package org.example;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class BitcoinUtils {

    public static final String MAGIC_BYTES = "f9beb4d9";
    public static final int MAGIC_BYTES_LENGTH = 8;
    public static final int MAGIC_BYTES_FOUR = 4;
    public static final int COMMAND_LENGTH = 12;
    public static final int SIZE_LENGTH = 4;
    public static final int CHECKSUM_LENGTH = 4;

    public static String convertIntegerToStringHex(int number) {
        return Integer.toHexString(number);
    }


    public static String size(String data, int size) {
        int targetLength = size * 2; // 2 hexadecimal chars per byte
        return String.format("%" + targetLength + "s", data).replace(' ', '0');
    }


    // Reverse the order of bytes in a hexadecimal string
    public static String reverseBytes(String hex) {
        int len = hex.length();
        if (len % 2 != 0) {
            throw new IllegalArgumentException("Hex string must have an even length");
        }
        // Split the string into an array of 2-character (1-byte) substrings
        String[] bytes = new String[len / 2]; //2
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = hex.substring(i, i + 2);
        }
        // Reverse the array and join back into a single string
        StringBuilder reversedHex = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; i--) {
            reversedHex.append(bytes[i]);
        }
        return reversedHex.toString();
    }


    public static String ascii2hex(String str) {
        StringBuilder hexString = new StringBuilder();
        for (char c : str.toCharArray()) {
            String hex = Integer.toHexString(c);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        // Pad the string to 24 hexadecimal characters (12 bytes)
        while (hexString.length() < 24) {
            hexString.append('0');
        }
        return hexString.toString();
    }


    // Perform a double SHA-256 hash and return the first 4 bytes (8 hexadecimal characters)
    public static String checksum(String hexString) throws NoSuchAlgorithmException {
        // Convert hex string to byte array
        byte[] bytes = hexStringToByteArray(hexString);
        // Perform double SHA-256 hash
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] firstHash = sha256.digest(bytes);
        byte[] secondHash = sha256.digest(firstHash);
        // Convert the first 4 bytes of the second hash to a hexadecimal string
        StringBuilder checksum = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            String hex = Integer.toHexString(0xff & secondHash[i]);
            if (hex.length() == 1) {
                checksum.append('0');
            }
            checksum.append(hex);
        }
        return checksum.toString();
    }



    // Helper method to convert a hex string to a byte array
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }


    public static byte[] hexadecimalStringToRawBytes(String hexString) {
        hexString = hexString.trim();
        if (hexString.length() % 2 != 0) {
            throw new IllegalArgumentException("Hex string must have an even length");
        }
        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            String hexPair = hexString.substring(i, i + 2);
            bytes[i / 2] = (byte) Integer.parseInt(hexPair, 16);
        }
        return bytes;
    }


    // Helper method to convert byte array to hex string
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }


    public static int byteArrayToInt(byte[] bytes) {
        return ((bytes[3] & 0xFF) << 24) | ((bytes[2] & 0xFF) << 16) | ((bytes[1] & 0xFF) << 8) | (bytes[0] & 0xFF);
    }



}
