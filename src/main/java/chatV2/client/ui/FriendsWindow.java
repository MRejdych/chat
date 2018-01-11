package chatV2.client.ui;

import chatV2.client.ChatClient;
import chatV2.client.Client;
import chatV2.client.Client.OnDataReceivedListener;
import chatV2.common.data.UserInfo;
import chatV2.common.messages.Message;
import chatV2.common.messages.Request;
import chatV2.common.messages.Response;
import chatV2.common.utils.Task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class FriendsWindow extends Window implements OnDataReceivedListener, ActionListener {
    private JList<FriendEntry> friendList;
    private final Object lock = new Object();

    @Override
    protected void initializeComponents() {
        setTitle("Friends");
        getContentPane().setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(10, 5, 10, 5));
        getContentPane().add(panel, BorderLayout.NORTH);
        panel.setLayout(new BorderLayout(0, 0));


        friendList = new JList<>();
        friendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(friendList);
        getContentPane().add(scrollPane);

        setSize(300, 450);
    }

    public FriendsWindow() {
        Client.getInstance().addOnDataReceivedListener(this);
        fetchDisplayData();
        friendList.addMouseListener(new ItemClickHandler());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    private void displayChatBox(int whoIndex) {
        if (whoIndex >= 0) {
            UserInfo friend = friendList.getModel().getElementAt(whoIndex).getUserInfo();
            if (friend.getState() == UserInfo.STATE_ONLINE)
                ChatClient.showChatWindow(friend);
            else
                MessageBox.showMessageBoxInUIThread(this,
                        String.format("'%s' has gone! wait for him online then chatV2 again.", friend.getUserName())
                );
        }
    }

    private void fetchDisplayData() {
        Task.run(() -> {
            Client.getInstance().request(new Request(Request.CODE_MY_ACCOUNT_INFO));
            Client.getInstance().request(new Request(Request.CODE_FRIENDS_LIST));
        });
    }

    @Override
    protected void onWindowClosing() {
        super.onWindowClosing();
        Client.getInstance().removeOnDataReceivedListener(this);
        ChatClient.exitIfNotWindowActived();
    }

    private UserInfo getAccountInfoById(int id) {
        DefaultListModel<FriendEntry> friendEntries = (DefaultListModel<FriendEntry>) friendList.getModel();
        synchronized (lock) {
            for (int i = 0; i < friendEntries.size(); i++) {
                FriendEntry friendEntry = friendEntries.getElementAt(i);
                if (friendEntry.getUserInfo().getAccountId() == id)
                    return friendEntry.getUserInfo();
            }
        }
        return null;
    }

    private void loadFriendsList(List<UserInfo> userInfos) {
        DefaultListModel<FriendEntry> friendEntries = new DefaultListModel<>();
        for (UserInfo userInfo : userInfos) {
            friendEntries.addElement(new FriendEntry(userInfo));
        }
        friendList.setModel(friendEntries);
    }

    private void updateFriend(final UserInfo friend) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public synchronized void run() {
                synchronized (lock) {
                    FriendEntry currentEntry = null;
                    int currentId = -1;
                    DefaultListModel<FriendEntry> friendEntries = (DefaultListModel<FriendEntry>) friendList.getModel();
                    int countOfFriend = friendEntries.getSize();
                    for (int i = 0; i < countOfFriend; i++) {
                        FriendEntry friendEntry = friendEntries.getElementAt(i);
                        if (friendEntry.getUserInfo().getAccountId() == friend.getAccountId()) {
                            currentEntry = friendEntry;
                            currentId = i;
                            break;
                        }
                    }
                    if (currentEntry != null)
                        friendEntries.setElementAt(new FriendEntry(friend), currentId);
                    else
                        friendEntries.addElement(new FriendEntry(friend));
                }
            }
        });
    }

    private void setMyUserInfo(final UserInfo userInfo) {
        SwingUtilities.invokeLater(() -> {
            setTitle(userInfo.getUserName());
            Client.getInstance().setMyId(userInfo.getAccountId());
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean onDataReceived(Client sender, Response receivedObject) {
        int requestCode = receivedObject.getRequestCode();
        switch (requestCode) {
            case Request.CODE_CHAT_MESSAGE:
                if (receivedObject.getExtra() instanceof Message) {
                    System.out.println("+ Data: chatV2 message");
                    Message message = (Message) receivedObject.getExtra();
                    ChatClient.showChatWindow(getAccountInfoById(message.getWhoId()));
                }
                break;
            case Request.CODE_FRIENDS_LIST:
                System.out.println("+ Data: friends list");
                loadFriendsList((List<UserInfo>) receivedObject.getExtra());
                break;
            case Request.CODE_FRIEND_STATE:
                System.out.println("+ Data: update friend state");
                updateFriend((UserInfo) receivedObject.getExtra());
                break;
            default:
                if (requestCode == Request.CODE_MY_ACCOUNT_INFO) {
                    System.out.println("+ Data: update my account info");
                    setMyUserInfo((UserInfo) receivedObject.getExtra());
                }
        }
        return true;
    }

    private class ItemClickHandler extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                int where = friendList.locationToIndex(e.getPoint());
                displayChatBox(where);
            }
        }
    }

    private static class FriendEntry {
        private final UserInfo userInfo;

        FriendEntry(UserInfo userInfo) {
            this.userInfo = userInfo;
        }

        public UserInfo getUserInfo() {
            return userInfo;
        }

        @Override
        public String toString() {
            return userInfo.getUserName();
        }
    }
}
