package org.example;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

import static org.example.BitcoinUtils.ascii2hex;

public class Main {
    public static void main(String[] args) {
//            String name = "Ademola";
//
//            // Convert to ASCII values
//            System.out.println("ASCII values:");
//            for (char c : name.toCharArray()) {
//                System.out.printf("%d ", (int) c);
//            }
//            System.out.println();
//            System.out.println();
//
//            // Convert to byte array and print as hex
//            byte[] bytes = name.getBytes();
//            System.out.println("Hexadecimal representation:");
//            for (byte b : bytes) {
//                System.out.printf("%02X ", b);
//            }
//            System.out.println();

        System.out.println("Result = " + ascii2hex("Name"));

    }
}