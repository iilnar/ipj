package chat;

import java.io.IOException;
import java.net.*;
import java.nio.charset.Charset;
import java.util.Scanner;

class MessageReceiver implements Runnable {
    private DatagramSocket socket;
    private static Charset utf8 = Charset.forName("UTF-8");
    private static final int MAX_FAILS = 3;

    public MessageReceiver(DatagramSocket socket) {
        this.socket = socket;
    }

    public void run() {
        System.out.println("MessageReceiver is working");
        byte[] buffer = new byte[1 << 10];
        int fails = 0;
        while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                System.out.println("[error] Connection with the server is lost. Will retry.");
                if (++fails > MAX_FAILS) {
                    System.out.println("[error] Too many error in this life, bye.");
                    break;
                }
            }

            String s = new String(packet.getData(), 0, packet.getLength(), utf8);
            System.out.println(s);
        }
    }
}

class MessageSender implements Runnable {
    private DatagramSocket socket;
    private InetAddress serverAddress;
    private int serverPort;
    private static final Charset utf8 = Charset.forName("UTF-8");
    private static final int MAX_TRIES = 3;
    private static final int MAX_FAILS = 3;

    public MessageSender(DatagramSocket socket, InetAddress address, int port) {
        this.socket = socket;
        this.serverAddress = address;
        this.serverPort = port;
    }

    public void run() {
        System.out.println("MessageSender is working!");
        Scanner sc = new Scanner(System.in);
        int fails = 0;

        while (true) {
            String message = sc.nextLine();
            byte[] bytes = message.getBytes(utf8);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, serverAddress, serverPort);
            boolean luck = false;

            for (int i = 0; i < MAX_TRIES; i++) {
                try {
                    socket.send(packet);
                    luck = true;
                    break;
                } catch (IOException ignored) {
                }
            }
            if (!luck) {
                System.out.println("[error] Server doesnt' respond. Sorry for that.");
                if (++fails > MAX_FAILS) {
                    System.out.println("[error] Too many fails in this life, bye.");
                    break;
                }
            }
        }
    }
}

public class ChatClient {
    public final static int PORT = 1488;
    private DatagramSocket sock;
    private String hostname;
    private InetAddress address;

    public ChatClient() throws UnknownHostException, SocketException {
        address = InetAddress.getByName(hostname);
        sock = new DatagramSocket();
    }

    public void run() {
        new Thread(new MessageSender(sock, address, PORT)).start();
        new Thread(new MessageReceiver(sock)).start();
    }

    public static void main(String[] args) throws IOException {
        new ChatClient().run();
    }
}
