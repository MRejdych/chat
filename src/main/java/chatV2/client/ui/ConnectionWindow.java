package chatV2.client.ui;

import chatV2.client.ChatClient;
import chatV2.client.Client;
import chatV2.common.utils.Task;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ConnectionWindow extends Window implements ActionListener {
    private JTextField portField;
    private JTextField ipField;
    private OnCreatedClientListener mOnCreatedClientListener = null;

    public void setOnCreatedClientListener(OnCreatedClientListener listener) {
        mOnCreatedClientListener = listener;
    }

    @Override
    protected void initializeComponents() {
        setResizable(false);
        getContentPane().setLayout(null);

        JLabel lblNewLabel = new JLabel("Address:");
        lblNewLabel.setBounds(384, 85, 43, 23);
        getContentPane().add(lblNewLabel);

        ipField = new JTextField();
        ipField.setBounds(437, 85, 99, 23);
        ipField.setText("localhost");
        ipField.addActionListener(this);
        getContentPane().add(ipField);
        ipField.setColumns(10);

        JLabel lblPort = new JLabel("Port:");
        lblPort.setBounds(384, 119, 43, 23);
        getContentPane().add(lblPort);

        portField = new JTextField();
        portField.setBounds(437, 119, 99, 23);
        portField.setText("3393");
        portField.addActionListener(this);
        getContentPane().add(portField);
        portField.setColumns(10);

        JButton btnConnect = new JButton("Connect");
        btnConnect.setBounds(437, 153, 99, 23);
        btnConnect.addActionListener(this);
        getContentPane().add(btnConnect);

        setSize(560, 397);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (checkInput())
            startConnect();
        else
            JOptionPane.showMessageDialog(ConnectionWindow.this, "Re-enter IP address or port number!");
    }

    private boolean checkInput() {
        String port = portField.getText();
        String ip = ipField.getText();
        try {
            int portNumber = Integer.parseInt(port);
            return portNumber > 0 && ip != null && ip.length() > 0;
        } catch (NumberFormatException e) {
        }
        return false;
    }

    public ConnectionWindow() {
        setTitle("Welcome to Homechat");
    }

    @Override
    protected void onWindowClosing() {
        super.onWindowClosing();
        ChatClient.exitIfNotWindowActived();
    }

    private void startConnect() {
        ProcessingDialog.showBox(this, "Connecting...");
        Task.run(() -> {
            try {
                Client.createInstance(ipField.getText(), Integer.parseInt(portField.getText()));
                if (mOnCreatedClientListener != null)
                    mOnCreatedClientListener.onCreatedClient(Client.getInstance());
                SwingUtilities.invokeLater(() -> {
                    ChatClient.showWindow(LoginWindow.class);
                    dispose();
                });
            } catch (NumberFormatException | IOException e) {
                MessageBox.showMessageBoxInUIThread(ConnectionWindow.this, "Has problem: " + e.getMessage());
            }
            SwingUtilities.invokeLater(ProcessingDialog::hideBox);
        });
    }

    public interface OnCreatedClientListener {
        void onCreatedClient(Client client);
    }
}
