package chatV2.client.ui;

import chatV2.client.ChatClient;

import javax.swing.*;
import java.awt.event.WindowEvent;

public abstract class Window extends JFrame {

    protected abstract void initializeComponents();

    Window() {
        ChatClient.registerWindow(this);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initializeComponents();
    }

    @Override
    public void setDefaultCloseOperation(int operation) {
        if (operation != EXIT_ON_CLOSE)
            super.setDefaultCloseOperation(operation);
    }

    protected void onWindowClosing() {
        ChatClient.unregisterWindow(this);
    }

    @Override
    public void dispose() {
        onWindowClosing();
        super.dispose();
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING && getDefaultCloseOperation() == DISPOSE_ON_CLOSE) {
            onWindowClosing();
        }
        super.processWindowEvent(e);
    }
}
