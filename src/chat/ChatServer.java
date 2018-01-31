package chat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ChatServer {
    private DatagramSocket socket;
    private HashMap<Session, Session> sessions;
    private HashMap<User, User> users;
    private static Charset utf8 = Charset.forName("UTF-8");

    public ChatServer(int port) {
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            throw new RuntimeException("Couldn't start chat server.", e);
        }
        users = new HashMap<>();
        sessions = new HashMap<>();
    }

    public void run() {
        byte[] buffer = new byte[1 << 10];
        while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                System.err.println("[couldn't get packet]");
                continue;
            }
            int len = packet.getLength();
            String message = new String(buffer, 0, len, utf8);
            Session session = new Session(packet.getAddress(), packet.getPort());
            session = sessions.getOrDefault(session, session);
            log(session, message);

            if (message.startsWith("/auth")) {
                auth(session, message);
            } else if (message.startsWith("/register")) {
                register(session, message);
                auth(session, message);
            } else if (message.startsWith("/change")) {
                changePassword(session, message);
            } else if (message.startsWith("/quit")) {
                quit(session, message);
            } else {
                sendAll(session, message);
            }
        }
    }

    private void register(Session session, String msg) {
        String[] tokens = msg.split(" ");
        if (tokens.length != 3) {
            send(session, "/register <login> <password>");
            return;
        }
        User user = new User(tokens[1], tokens[2]);
        if (users.containsKey(user)) {
            send(session, "User already registered");
            return;
        }
        users.put(user, user);
        log(session, String.format("[new user]: %s", tokens[1]));
    }

    private void auth(Session session, String msg) {
        kick(session);

        String[] tokens = msg.split(" ");
        if (tokens.length != 3) {
            send(session, "/auth <login> <password>");
            return;
        }
        User user = new User(tokens[1], tokens[2]);
        if (!users.containsKey(user)) {
            send(session, "No such user in chat. Register first.");
            return;
        }
        User originalUser = users.get(user);
        if (originalUser.getPasswordHash() != user.getPasswordHash()) {
            send(session, "Wrong password.");
            return;
        }
        send(session, String.format("Welcome, %s.", originalUser.getAlias()));
        sendAll(String.format("%s has connected to chat.", originalUser.getAlias()));

        session = new Session(session.getIa(), session.getPort(), user);
        sessions.put(session, session);
        log(session, "[auth]");
    }

    private void changePassword(Session session, String msg) {
        String[] tokens = msg.split(" ");
        if (tokens.length != 2) {
            send(session, "/change <new_password>");
            return;
        }
        if (!sessions.containsKey(session)) {
            send(session, "Error: authenticate first.");
            return;
        }
        User user = users.get(session.getUser());
        user.setPassword(tokens[1]);

        Iterator<Map.Entry<Session, Session>> it = sessions.entrySet().iterator();
        while (it.hasNext()) {
            Session ses = it.next().getKey();
            if (!ses.equals(session) && ses.getUser().equals(user)) {
                send(ses, "You're kicked.");
                it.remove();
            }
        }
        send(session, "Password was successfully changed.");
        log(session, "[change_password]");
    }

    private void quit(Session session, String message) {
        if (!sessions.containsKey(session)) {
            send(session, "You're not authenticated to quit.");
            return;
        }
        sessions.remove(session);
        sendAll(String.format("%s has left chat.", session.getUser().getAlias()));
        send(session, String.format("Bye, %s.", session.getUser().getAlias()));
        log(session, "[quit]");
    }

    private void kick(Session session) {
        sessions.remove(session);
        log(session, "[kick]");
    }

    private void sendDirectBytes(Session to, byte[] bytes) {
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, to.getIa(), to.getPort());
        try {
            socket.send(packet);
        } catch (IOException e) {
            log(to, "[couldn't send message]");
        }
    }

    private void send(Session session, String msg) {
        sendDirectBytes(session, msg.getBytes(utf8));
    }

    private void sendAll(String msg) {
        byte[] bMsg = msg.getBytes(utf8);
        for (Session ia : sessions.keySet()) {
            sendDirectBytes(ia, bMsg);
        }
    }

    private void sendAll(Session from, String msg) {
        if (!sessions.containsKey(from)) {
            send(from, "You don't have permissions to send messages. Login first.");
            return;
        }
        msg = String.format("[%s]: %s", from.getUser().getAlias(), msg);

        byte[] bMsg = msg.getBytes(utf8);
        for (Session ia : sessions.keySet()) {
            if (!ia.equals(from)) {
                sendDirectBytes(ia, bMsg);
            }
        }
    }

    private void log(Session session, String msg) {
        String alias = session.getUser() == null ? "null" : session.getUser().getAlias();
        System.err.printf("[%s:%d, %s]: %s\n", session.getIa(), session.getPort(), alias, msg);
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Usage: javac ChatServer <port>");
            return;
        }
        new ChatServer(Integer.parseInt(args[0])).run();
    }
}

class Session {
    private final InetAddress ia;
    private final int port;
    private final User user;

    public Session(InetAddress ia, int port) {
        this.ia = ia;
        this.port = port;
        this.user = null;
    }

    public Session(InetAddress ia, int port, User user) {
        this.ia = ia;
        this.port = port;
        this.user = user;
    }

    public InetAddress getIa() {
        return ia;
    }

    public int getPort() {
        return port;
    }

    public User getUser() {
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Session session = (Session) o;

        if (port != session.port) return false;
        return ia.equals(session.ia);
    }

    @Override
    public int hashCode() {
        int result = ia.hashCode();
        result = 31 * result + port;
        return result;
    }
}