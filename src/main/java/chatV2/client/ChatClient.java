package chatV2.client;

import chatV2.client.ui.ChatWindow;
import chatV2.client.ui.ConnectionWindow;
import chatV2.client.ui.MessageBox;
import chatV2.client.ui.Window;
import chatV2.common.data.UserInfo;

import javax.swing.*;
import java.util.LinkedList;
import java.util.List;

public final class ChatClient {

    public static void main(String[] args) {
        ChatClient.setSystemLookAndFeel();
        ChatClient.run();
    }


    private static List<Window> windows = new LinkedList<>();

    public static void exitIfNotWindowActived() {
        if (windows.isEmpty())
            exit();
    }

    private static void exit() {
        windows.forEach(Window::dispose);
        Client.destroyInstance();
        System.exit(0);
    }

    public static void registerWindow(Window window) {
        windows.add(window);
    }

    public static void unregisterWindow(Window window) {
        windows.remove(window);
    }

    private static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    private static void run() {
        ((ConnectionWindow) ChatClient.showWindow(ConnectionWindow.class))
                .setOnCreatedClientListener(new ConnectionWindowAdapter());
    }

    public static Window showWindow(Class<? extends Window> clazz) {
        Window existsWindow = null;
        for (Window window : windows) {
            if (window.getClass().equals(clazz)) {
                existsWindow = window;
                break;
            }
        }
        if (existsWindow == null) {
            try {
                existsWindow = clazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            if (existsWindow != null) {
                existsWindow.setLocationRelativeTo(null);
                existsWindow.setVisible(true);
            }
        }
        if (existsWindow != null)
            existsWindow.toFront();
        return existsWindow;
    }

    public static ChatWindow showChatWindow( UserInfo who) {
        ChatWindow chatWindow;
        for (Window window : windows) {
            if (window instanceof ChatWindow) {
                chatWindow = (ChatWindow) window;
                if (chatWindow.getAccountId() == who.getAccountId()) {
                    chatWindow.requestFocus();
                    return chatWindow;
                }
            }
        }
        chatWindow = new ChatWindow(who);
        chatWindow.setLocationRelativeTo(null);
        chatWindow.setVisible(true);
        return chatWindow;
    }

    static void closeAllWindows() {
        while (windows.size() > 0) {
            Window window = windows.get(0);
            window.dispose();
        }
    }

    private static class ConnectionWindowAdapter implements ConnectionWindow.OnCreatedClientListener {
        @Override
        public void onCreatedClient(Client client) {
            client.setOnConnectionHasProblemListener(message -> {
                closeAllWindows();
                MessageBox.showMessageBoxInUIThread(null, "Connection has problem: " + message);
                SwingUtilities.invokeLater(() -> ((ConnectionWindow) ChatClient.showWindow(ConnectionWindow.class))
                        .setOnCreatedClientListener(new ConnectionWindowAdapter()));

            });
            client.startLooper();
        }
    }
}
