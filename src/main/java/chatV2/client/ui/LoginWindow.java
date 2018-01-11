package chatV2.client.ui;

import chatV2.client.Client;
import chatV2.client.ChatClient;
import chatV2.common.messages.Request;
import chatV2.common.messages.Response;
import chatV2.common.data.User;
import chatV2.common.utils.Validator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginWindow extends ProcessingWindow implements ActionListener {
    private JTextField usernameField;
    private JButton btnRegister;

    private boolean checkInput() {
        String username = usernameField.getText();
        return Validator.checkValidUsername(username);
    }

    @Override
    protected void initializeComponents() {
        setResizable(false);
        setTitle("Login");
        getContentPane().setLayout(null);

        usernameField = new JTextField();
        usernameField.setBounds(451, 92, 112, 20);
        usernameField.addActionListener(this);
        getContentPane().add(usernameField);
        usernameField.setColumns(10);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setBounds(384, 95, 57, 14);
        getContentPane().add(lblUsername);
        lblUsername.requestFocus();

        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(451, 161, 112, 23);
        btnLogin.addActionListener(this);
        getContentPane().add(btnLogin);

        JLabel lblDontHaveAn = new JLabel("Don't have an account?");
        lblDontHaveAn.setBounds(384, 210, 127, 14);
        getContentPane().add(lblDontHaveAn);

        btnRegister = new JButton("Register");
        btnRegister.setBounds(451, 235, 112, 23);
        btnRegister.addActionListener(this);
        getContentPane().add(btnRegister);

        JLabel lblServerAddress = new JLabel("Server address...");
        lblServerAddress.setForeground(Color.GRAY);
        lblServerAddress.setBounds(10, 309, 201, 14);
        lblServerAddress.setText("Server at " + Client.getInstance().getRemoteAddress());
        getContentPane().add(lblServerAddress);

        setSize(586, 363);
    }

    @Override
    protected void onWindowClosing() {
        super.onWindowClosing();
        ChatClient.exitIfNotWindowActived();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnRegister)) {
            ChatClient.showWindow(RegisterWindow.class);
            dispose();
        } else {
            if (checkInput()) {
                String username = usernameField.getText();
                User user = new User();
                user.setUsername(username);
                Request request = new Request(Request.CODE_LOGIN, user);
                doInBackground(request, "Authenticating...");
            } else {
                JOptionPane.showMessageDialog(LoginWindow.this, "Username should have between 3 and 15 chars!");
            }
        }
    }

    @Override
    protected void doneBackgoundTask(Response result) {
        if (result.getCode() == Response.CODE_OK) {
            Client.getInstance().setMyUsername(usernameField.getText());
            ChatClient.showWindow(FriendsWindow.class);
            dispose();
        } else {
            MessageBox.showMessageBoxInUIThread(LoginWindow.this, "Login error: " + result.getExtra());
            setVisible(true);
        }
    }
}
