package chatV1.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ConversationHandler extends Thread {
    static List<String> userNames = new ArrayList<>();
    static List<PrintWriter> printWriters = new ArrayList<>();
    private Socket socket;


    public ConversationHandler(Socket socket, List<String> userNames, List<PrintWriter> printWriters) throws IOException {
        this.socket = socket;
        this.userNames = userNames;
        this.printWriters = printWriters;
    }

    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            PrintWriter logWriter = new PrintWriter(new BufferedWriter(new FileWriter("Chat-Logs.txt", true)), true);
            int count = 0;
            String name;
            while (true) {

                if (count > 0) {
                    out.println("NAMEALREADYEXISTS");
                } else {
                    out.println("NAMEREQUIRED");
                }

                name = in.readLine();

                if (name == null) {
                    return;
                }


                if (!ChatServer.userNames.contains(name)) {
                    ChatServer.userNames.add(name);
                    break;
                }

                count++;

            }

            out.println("NAMEACCEPTED" + name);
            ChatServer.printWriters.add(out);

            while (true) {
                String message = in.readLine();

                if (message == null) {
                    return;
                }

                logWriter.println(name + ": " + message);

                for (PrintWriter writer : ChatServer.printWriters) {
                    writer.println(name + ": " + message);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}