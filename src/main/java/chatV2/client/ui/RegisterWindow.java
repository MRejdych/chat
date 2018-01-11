package chatV2.client.ui;

import chatV2.client.ChatClient;
import chatV2.common.data.RegisterInfo;
import chatV2.common.messages.Request;
import chatV2.common.messages.Response;
import chatV2.common.utils.Validator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterWindow extends ProcessingWindow implements ActionListener {
    private JTextField usernameField;
    private JButton btnCancel;

    public RegisterWindow() {
    }

    @Override
    protected void onWindowClosing() {
        super.onWindowClosing();
        ChatClient.showWindow(LoginWindow.class);
    }

    @Override
    protected void initializeComponents() {
        setResizable(false);
        setTitle("Register");

        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(null);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setBounds(20, 60, 77, 14);
        panel.add(lblUsername);

        usernameField = new JTextField();
        usernameField.setBounds(20, 80, 160, 26);
        panel.add(usernameField);
        usernameField.requestFocus();
        usernameField.setColumns(10);
        usernameField.addActionListener(this);

        JButton btnTake = new JButton("Take");
        btnTake.setBounds(20, 175, 70, 23);
        btnTake.addActionListener(this);
        panel.add(btnTake);

        btnCancel = new JButton("Cancel");
        btnCancel.setBounds(110, 175, 70, 23);
        btnCancel.addActionListener(this);
        panel.add(btnCancel);

        setSize(200, 250);
    }

    private boolean checkInput() {
        String username = usernameField.getText();
        return Validator.checkValidUsername(username);
    }

    private Request getChatRequest(final String username) {
        RegisterInfo info = new RegisterInfo();
        info.setUsername(username);
        return new Request(Request.CODE_REGISTER, info);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnCancel)) {
            dispose();
        } else {
            if (checkInput()) {
                Request request = getChatRequest(usernameField.getText().trim());
                doInBackground(request);
            } else {
                MessageBox.showMessageBoxInUIThread(this, "Re-enter valid value into fields!");
            }
        }
    }

    @Override
    protected void doneBackgoundTask(Response result) {
        if (result.getCode() == Response.CODE_OK) {
            MessageBox.showMessageBoxInUIThread(this, "Register is successful!");
            dispose();
        } else {
            MessageBox.showMessageBoxInUIThread(this, "Register is unsuccessful!");
            setVisible(true);
        }
    }
}
