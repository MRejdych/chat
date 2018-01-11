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
        lblNewLabel.setBounds(50, 5, 100, 40);
        getContentPane().add(lblNewLabel);

        ipField = new JTextField();
        ipField.setBounds(50, 35, 100, 26);
        ipField.setText("localhost");
        ipField.addActionListener(this);
        getContentPane().add(ipField);
        ipField.setColumns(10);

        JLabel lblPort = new JLabel("Port:");
        lblPort.setBounds(50, 69, 100, 23);
        getContentPane().add(lblPort);

        portField = new JTextField();
        portField.setBounds(50, 93, 100, 26);
        portField.setText("3393");
        portField.addActionListener(this);
        getContentPane().add(portField);
        portField.setColumns(10);

        JButton btnConnect = new JButton("Connect");
        btnConnect.setBounds(50, 133, 100, 23);
        btnConnect.addActionListener(this);
        getContentPane().add(btnConnect);

        setSize(200, 250);
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
        setTitle("JavaChat");
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
