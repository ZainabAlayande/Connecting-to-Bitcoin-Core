package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.NoSuchAlgorithmException;

import static org.example.BitcoinUtils.*;
import static org.example.BitcoinUtils.hexStringToByteArray;

public class MessageHandlerCopy {
        public static void main(String[] args) throws NoSuchAlgorithmException {
            while (true) {
                try (Socket socket = new Socket("45.144.112.208", 8333)) {
                    InputStream inputStream = socket.getInputStream();
                    OutputStream outputStream = socket.getOutputStream();

                    // Create a Version message. Version message is the first message in Bitcoin
                    String versionMessage = Version.createVersionMessage();
                    byte[] versionBytes = hexStringToByteArray(versionMessage);
                    outputStream.write(versionBytes);
                    System.out.println("Version message sent: " + versionMessage);

                    while (true) {
                        StringBuilder buffer = new StringBuilder();

                        while (true) {
                            int readByte = inputStream.read();
                            if (readByte == -1) {
                                System.out.println("Read a nil byte from the socket. Looks like the remote node has disconnected from us. " +
                                        "We probably failed the handshake too many times, or didn't respond to enough pings. " +
                                        "No worries, try connecting to another node for the time being instead.");
                                break;
                            }

                            buffer.append(String.format("%02X", readByte));

                            if (buffer.length() == MAGIC_BYTES_LENGTH) {
                                if (buffer.toString().equalsIgnoreCase(MAGIC_BYTES)) {
                                    byte[] commandBytes = new byte[COMMAND_LENGTH];
                                    inputStream.read(commandBytes);
                                    String command = new String(commandBytes).replaceAll("\0", "");

                                    byte[] sizeBytes = new byte[SIZE_LENGTH];
                                    inputStream.read(sizeBytes);
                                    int size = ByteBuffer.wrap(sizeBytes).order(ByteOrder.LITTLE_ENDIAN).getInt();

                                    byte[] checksumBytes = new byte[CHECKSUM_LENGTH];
                                    inputStream.read(checksumBytes);
                                    String checksum = bytesToHex(checksumBytes);

                                    byte[] payloadBytes = new byte[size];
                                    inputStream.read(payloadBytes);
                                    String payload = bytesToHex(payloadBytes);

                                    System.out.println("<-" + command);
                                    System.out.println("magic_bytes: " + buffer);
                                    System.out.println("command:     " + command);
                                    System.out.println("size:        " + size);
                                    System.out.println("checksum:    " + checksum);
                                    System.out.println("payload:     " + payload);
                                    System.out.println();

                                    if (command.equalsIgnoreCase("inv")) {
                                        command = "getdata";

                                        String magic = "f9beb4d9";
                                        String commandHex = ascii2hex(command);
                                        String sizeHex = reverseBytes(size(convertIntegerToStringHex(payload.length() / 2), 4));
                                        String checksumHex = checksum(payload);
                                        String message = magic + commandHex + sizeHex + checksumHex + payload;

                                        System.out.println(command + "->");
                                        System.out.println("magic_bytes: " + magic);
                                        System.out.println("command:     " + command);
                                        System.out.println("size:        " + (payload.length() / 2));
                                        System.out.println("checksum:    " + checksumHex);
                                        System.out.println("payload:     " + payload);
                                        System.out.println();

                                        outputStream.write(hexStringToByteArray(message));
                                    }

                                    // Handle "ping" message by responding with "pong"
                                    if (command.equalsIgnoreCase("ping")) {
                                        command = "pong";

                                        String magic = "f9beb4d9";
                                        String commandHex = ascii2hex(command);
                                        String sizeHex = reverseBytes(size(convertIntegerToStringHex(payload.length() / 2), 4));
                                        String checksumHex = checksum(payload);
                                        String message = magic + commandHex + sizeHex + checksumHex + payload;

                                        System.out.println(command + "->");
                                        System.out.println("magic_bytes: " + magic);
                                        System.out.println("command:     " + command);
                                        System.out.println("size:        " + (payload.length() / 2));
                                        System.out.println("checksum:    " + checksumHex);
                                        System.out.println("payload:     " + payload);
                                        System.out.println();

                                        outputStream.write(hexStringToByteArray(message));
                                    }

                                    break;
                                }

                                buffer.setLength(0); // reset the buffer if it does not match the magic bytes
                            }
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Connection error: " + e.getMessage());
                    try {
                        // Wait for 5 seconds before attempting to reconnect
                        Thread.sleep(5000);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
            }
        }
}
