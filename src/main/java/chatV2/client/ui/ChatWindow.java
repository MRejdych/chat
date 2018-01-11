package chatV2.client.ui;

import chatV2.client.Client;
import chatV2.client.Client.OnDataReceivedListener;
import chatV2.common.data.UserInfo;
import chatV2.common.messages.Message;
import chatV2.common.messages.Request;
import chatV2.common.messages.Response;
import chatV2.common.utils.Task;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class ChatWindow extends Window implements ActionListener, WindowStateListener, OnDataReceivedListener, AdjustmentListener {
    private static final String STYLE_SHEET =
            ".chat-box { margin: 2px; }"
                    + ".chat-box p { display: block; padding: 7px; margin-top: 2px; margin-bottom: 2px; word-wrap: break-word;}"
                    + ".chat-msg1 { background-color: #2ea9d1; }" + ".chat-msg2 { background-color: white; }";
    private static final String DEFAULT_HTML_FORMAT = "<style>" + STYLE_SHEET + "</style>"
            + "<div id=content class=chat-box> </div>";
    private static final String DEFAULT_FRIEND_CHAT_FORMAT = "<p class=chat-msg1>%s</p>";
    private static final String DEFAULT_WE_CHAT_FORMAT = "<p class=chat-msg2>%s</p>";
    private JTextField inputField;
    private JTextPane dispField;
    private final UserInfo yourFriend;

    @Override
    protected void initializeComponents() {
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(panel, BorderLayout.SOUTH);
        panel.setLayout(new BorderLayout(5, 5));

        inputField = new JTextField();
        inputField.setToolTipText("Enter your chat content then enter to send");
        panel.add(inputField, BorderLayout.CENTER);
        inputField.addActionListener(this);

        JButton btnSend = new JButton("");
        btnSend.setToolTipText("Press to send your chat content");
        btnSend.addActionListener(this);
        panel.add(btnSend, BorderLayout.EAST);

        dispField = new JTextPane();
        dispField.setContentType("text/html");
        dispField.setEditable(false);
        dispField.setText(DEFAULT_HTML_FORMAT);

        JScrollPane scrollPane = new JScrollPane(dispField);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(this);

        setSize(280, 400);

    }

    public ChatWindow(UserInfo yourFriend) {
        setTitle(yourFriend.getUserName());

        this.yourFriend = yourFriend;

        Client.getInstance().addOnDataReceivedListener(this);
    }

    private void appendRawHtmlChatContent(String html) {
        HTMLDocument doc = (HTMLDocument) dispField.getDocument();
        Element contentElement = doc.getElement("content");
        try {
            if (contentElement.getElementCount() > 0) {
                Element lastElement = contentElement.getElement(contentElement.getElementCount() - 1);
                doc = (HTMLDocument) contentElement.getDocument();
                doc.insertAfterEnd(lastElement, html);
            } else {
                doc.insertAfterStart(contentElement, html);
            }
        } catch (BadLocationException | IOException e) {
            MessageBox.showMessageBoxInUIThread(this, "Internal error: " + e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        performSending();
    }

    private void performSending() {
        String content = inputField.getText();
        if (content != null && (content = content.trim()).length() > 0) {
            Message message = new Message();
            message.setContent(content);
            message.setWhoId(yourFriend.getAccountId());
            final Request request = new Request(Request.CODE_CHAT_MESSAGE, message);
            Task.run(() -> Client.getInstance().request(request));
            showWeChat(content);
            inputField.setText("");
        } else {
            MessageBox.showMessageBoxInUIThread(this,
                    "What do you want to send to '" + yourFriend.getUserName() + "'?");
        }
    }

    public int getAccountId() {
        return yourFriend.getAccountId();
    }

    @Override
    protected void onWindowClosing() {
        super.onWindowClosing();
        Client.getInstance().removeOnDataReceivedListener(this);
    }

    @Override
    public void windowStateChanged(WindowEvent e) {
        if (e.getNewState() == WindowEvent.WINDOW_ACTIVATED) {
            inputField.requestFocus();
        }
    }

    private String prepareHtmlString(String rawContent) {
        return StringEscapeUtils.escapeXml(rawContent);
    }

    private void focusMe() {
        requestFocus();
        inputField.requestFocus();
    }

    private void showWeChat(String content) {
        appendRawHtmlChatContent(String.format(DEFAULT_WE_CHAT_FORMAT, prepareHtmlString(content)));
        focusMe();
    }

    private void showFriendChat(String content) {
        appendRawHtmlChatContent(String.format(DEFAULT_FRIEND_CHAT_FORMAT, prepareHtmlString(content)));
        focusMe();
    }

    private void showFriendChat(final Message message) {
        SwingUtilities.invokeLater(() -> showFriendChat(message.getContent()));
    }

    @Override
    public boolean onDataReceived(Client sender, Response receivedObject) {
        if (receivedObject.getRequestCode() == Request.CODE_CHAT_MESSAGE) {
            if (receivedObject.getExtra() instanceof Message) {
                Message message = (Message) receivedObject.getExtra();
                if (message.getWhoId() == yourFriend.getAccountId())
                    showFriendChat(message);
            } else {
                if (receivedObject.getCode() != Response.CODE_OK)
                    MessageBox.showMessageBoxInUIThread(this, receivedObject.getExtra());
            }
        }
        return true;
    }

    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
        e.getAdjustable().setValue(e.getAdjustable().getMaximum());
    }
}
