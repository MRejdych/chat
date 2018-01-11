package chatV1.server;


import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    static List<String> userNames = new ArrayList<>();
    static List<PrintWriter> printWriters = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        System.out.println("Waiting for clients...");
        ServerSocket ss = new ServerSocket(9806);

        while (true) {
            Socket soc = ss.accept();
            System.out.println("Connection established");
            ConversationHandler handler = new ConversationHandler(soc, userNames, printWriters);
            handler.start();
        }
    }
}

