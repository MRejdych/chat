package chatV2.server;

import chatV2.common.utils.StreamUtilities;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public final class ChatServer {

    public static void main(String[] args) {
        try {
            new ChatServer();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }


    private static final int MAX_CONNECTIONS = 100;
    private Server server = null;

    private ChatServer() throws SocketException {
        List<String> ips = getAvailableIPs();
        Scanner scanner = new Scanner(System.in);

        int count = -1;
        for (String ip : ips) {
            count++;
            System.out.println(count + ": " + ip);
        }

        int selectedIp;
        String port;

        while (true) {
            System.out.println("Select server ip (option from 0 to " + count + ") ...");
            selectedIp = scanner.nextInt();
            System.out.println("Select port under which server will be started ...");
            port = scanner.next();
            if (isNumber(port) && (0 < selectedIp && selectedIp < count)) {
                break;
            }
            System.out.println("Entered data is invalid, try again ...");
        }
        startServer(ips.get(selectedIp), port);
    }


    private void startServer(String ip, String selectedPort) {
        final int port = Integer.parseInt(selectedPort);
        UserManager.createInstance("./workingdir/");

        try {
            server = new Server(port, ip, MAX_CONNECTIONS);
            server.waitForConnection();
        } catch (IOException e) {
            System.out.println("Server creation error!");
            e.printStackTrace();
            stopServer();
        }

    }


    private void stopServer() {
        if (server != null) {
            StreamUtilities.tryCloseStream(server);
            server = null;
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private boolean isNumber(String s) {
        try {
            if (s == null) return false;
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private List<String> getAvailableIPs() throws SocketException {
        return Collections.list(NetworkInterface.getNetworkInterfaces())
                .stream()
                .map(it -> Collections.list(it.getInetAddresses()))
                .flatMap(List::stream)
                .filter(it -> it.getAddress().length == 4)
                .map(InetAddress::getHostAddress)
                .collect(Collectors.toList());
    }
}
