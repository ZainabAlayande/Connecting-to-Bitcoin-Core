package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.NoSuchAlgorithmException;

import static org.example.BitcoinUtils.*;

public class MessageHandler {

    public static void main(String[] args) throws NoSuchAlgorithmException {
        try {
            // Open or Establish a TCP Connection
            Socket socket = new Socket("45.144.112.208", Integer.parseInt("8333"));
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            // Create a Version message. Version message is the first version in Bitcoin
            String versionMessage = Version.createVersionMessage();

            // Write the message to the socket (the protocol sends and receives messages in raw bytes)
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
                        return;
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


//            // 2. Receive Version MessageHandler
//
//            // a. Read the message header response from the socket
//            byte[] magicBytes = new byte[MAGIC_BYTES_FOUR];
//            byte[] command = new byte[COMMAND_LENGTH];
//            byte[] size = new byte[SIZE_LENGTH];
//            byte[] checksum = new byte[CHECKSUM_LENGTH];
//
//            inputStream.read(magicBytes);
//            inputStream.read(command);
//            inputStream.read(size);
//            inputStream.read(checksum);
//
//
//            // b. View the message header
//            System.out.println("<-version");
//            System.out.println("magic_bytes: " + bytesToHex(magicBytes));  // Convert raw bytes to hexadecimal characters
//            System.out.println("command:     " + new String(command).trim());  // Convert raw bytes to ASCII characters
//            int payloadSize = ByteBuffer.wrap(size).order(ByteOrder.LITTLE_ENDIAN).getInt();  // Convert to a 32-byte unsigned, little-endian
//            System.out.println("size:        " + payloadSize);
//            System.out.println("checksum:    " + bytesToHex(checksum));  // Convert raw bytes to hexadecimal characters
//
//
//            // c. Read the payload from the socket
//            byte[] payloadBytes = new byte[payloadSize];
//            inputStream.read(payloadBytes);
//
//            // Print the payload
//            System.out.println("payload:     " + bytesToHex(payloadBytes));
//
//
//
//            // 3. Receive Verack MessageHandler (verack = version acknowledged)
//            // a. Read the message header response from the socket
//            byte[] verackMagicBytes = new byte[MAGIC_BYTES_FOUR];
//            byte[] verackCommand = new byte[COMMAND_LENGTH];
//            byte[] verackSize = new byte[SIZE_LENGTH];
//            byte[] verackChecksum = new byte[CHECKSUM_LENGTH];
//
//            inputStream.read(verackMagicBytes);
//            inputStream.read(verackCommand);
//            inputStream.read(verackSize);
//            inputStream.read(verackChecksum);
//
//            // Print the received parts for debugging
//            System.out.println();
//            System.out.println("<-verack");
//            System.out.println("magic_bytes: " + bytesToHex(verackMagicBytes));  // Convert raw bytes to hexadecimal characters
//            System.out.println("command:     " + new String(verackCommand).trim());  // Convert raw bytes to ASCII characters
//            int verackPayloadSize = ByteBuffer.wrap(verackSize).order(ByteOrder.LITTLE_ENDIAN).getInt();  // Convert to a 32-byte unsigned, little-endian
//            System.out.println("size:        " + verackPayloadSize);
//            System.out.println("checksum:    " + bytesToHex(verackChecksum));  // Convert raw bytes to hexadecimal characters
//
//            // Read the payload for the verack message
//            byte[] verackPayloadBytes = new byte[verackPayloadSize];
//            inputStream.read(verackPayloadBytes);
//
//            // Print the verack payload
//            System.out.println("payload:     " + bytesToHex(verackPayloadBytes));
//
//
//
//            // 4. Send Verack MessageHandler
//            // a. Create verack message
//            String verackMessage = Verack.createVerackMessage();
//
//            // b. Write the message to the socket
//            byte[] verackBytes = hexStringToByteArray(verackMessage);
//            outputStream.write(verackBytes);
//
//            System.out.println();
//            System.out.println("verack->");
//            System.out.println("magic_bytes: " + "f9beb4d9");
//            System.out.println("command:     " + "verack");
//            System.out.println("size:        " + 0);
//            System.out.println("checksum:    " + verackMessage.substring(40, 48));
//            System.out.println("payload:     ");



        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}




