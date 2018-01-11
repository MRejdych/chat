package chatV2.server;

import chatV2.common.data.*;
import chatV2.common.messages.Message;
import chatV2.common.messages.Request;
import chatV2.common.messages.Response;
import chatV2.common.utils.StreamUtilities;
import chatV2.common.utils.Task;
import chatV2.common.utils.Validator;
import com.sun.istack.internal.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public final class Server implements Closeable, Worker.OnAuthenticatedListener {
    private ServerSocket serverSocket;
    private List<Worker> workers = new LinkedList<>();
    private final Object lock = new Object();
    private RequestHandler requestHandler = new RequestHandler();

    public Server(int port, String address, int maxConnection) throws IOException {
        System.out.println("Server starting...");
        serverSocket = new ServerSocket(port, maxConnection, InetAddress.getByName(address));
        System.out.println("Server started...");
    }

    public void waitForConnection() throws IOException {
        System.out.println("Waiting for connecting...");
        Socket socket;
        while ((socket = serverSocket.accept()) != null) {
            System.out.println("Accepted " + socket.getRemoteSocketAddress().toString());
            talkWithClient(socket);
        }
    }

    private Worker createWorker(Socket socket) {
        Worker worker = null;
        try {
            System.out.println("Creating worker...");
            worker = new Worker(socket);
        } catch (IOException | SecurityException e) {
            System.out.println("Creating was aborted!");
        }

        if (worker != null) {
            synchronized (lock) {
                workers.add(worker);
            }
        } else {
            StreamUtilities.tryCloseStream(socket);
        }
        return worker;
    }

    private void notifyAllWorker(Worker broadcastWorker) {
        synchronized (lock) {
            Response result = new Response();
            result.setCode(Response.CODE_OK);
            result.setRequestCode(Request.CODE_FRIEND_STATE);
            result.setExtra(broadcastWorker.getAccount());
            for (Worker friendWorker : workers) {
                if (!broadcastWorker.equals(friendWorker)) {
                    try {
                        friendWorker.response(result);
                    } catch (IOException e) {
                        killWorker(friendWorker);
                    }
                }
            }
        }
    }

    private void killWorker(Worker worker) {
        worker.release();
        worker.setOnReceivedDataListener(null);
        worker.setOnAuthenticatedListener(null);
        synchronized (lock) {
            workers.remove(worker);
        }
        if (worker.getAccount() != null) {
            System.out.println("Removed worker " + worker.getAccount().getAccountId());
            worker.getAccount().setState(UserInfo.STATE_OFFLINE);
            notifyAllWorker(worker);
        } else {
            System.out.println("Removed anonymous worker");
        }
    }

    private void talkWithClient(final Socket socket) {
        Task.run(() -> {
            Worker worker = createWorker(socket);
            if (worker != null) {
                try {
                    System.out.println("Start worker bridge!");
                    worker.setOnAuthenticatedListener(Server.this);
                    worker.setOnReceivedDataListener(requestHandler);
                    worker.startBridge();
                } catch (IOException ignored) {
                }
                killWorker(worker);
            } else {
                System.out.println("Createing worker: unsuccessful");
            }
        });
    }

    @Override
    public void close() throws IOException {
        serverSocket.close();
        while (!workers.isEmpty()) {
            Worker worker = workers.get(0);
            killWorker(worker);
        }
    }

    @Override
    public void onAuthenticated(Worker worker) {
        System.out.println("Broadcast new worker!");
        notifyAllWorker(worker);
    }

    private class RequestHandler implements Worker.OnRequestReceivedListener {

        @Override
        @NotNull
        public Response onRequestReceived(Worker sender, Request request) {
            Response responseObject = null;
            switch (request.getCode()) {
                case Request.CODE_FRIENDS_LIST:
                    responseObject = responseFriendsList(sender);
                    break;
                case Request.CODE_MY_ACCOUNT_INFO:
                    responseObject = responseAccountInfo(sender);
                    break;
                case Request.CODE_CHAT_MESSAGE:
                    responseObject = forwardChatMessage(sender,
                            request.getExtra() instanceof Message ? (Message) request.getExtra() : null);
                    break;
                case Request.CODE_LOGIN:
                    responseObject = responseLoginResult(sender,
                            request.getExtra() instanceof User ? (User) request.getExtra() : null);
                    break;
                case Request.CODE_REGISTER:
                    if (request.getExtra() instanceof RegisterInfo) {
                        RegisterInfo registerInfo = (RegisterInfo) request.getExtra();
                        responseObject = responseRegister(registerInfo.getUsername());
                    }
                    break;
            }
            Objects.requireNonNull(responseObject).setRequestCode(request.getCode());
            return responseObject;
        }

        private boolean isLogged(UserInfo userInfo) {
            if (userInfo == null)
                return false;
            synchronized (lock) {
                for (Worker worker : workers) {
                    UserInfo existsUserInfo = worker.getAccount();
                    if (existsUserInfo != null && existsUserInfo.getAccountId() == userInfo.getAccountId()) {
                        return true;
                    }
                }
                return false;
            }
        }

        private Response responseLoginResult(Worker sender, User user) {
            if (user == null)
                return null;
            UserInfo userInfo = UserManager.getInstance().getUserInfo(user.getUsername());
            Response result = new Response();
            boolean logged = isLogged(userInfo);
            result.setCode(Response.CODE_FAIL);
            if (userInfo != null) {
                if (logged) {
                    result.setExtra("already logged in other place");
                    System.out.println("Response login: FAIL - already logged in other place");
                } else {
                    result.setCode(Response.CODE_OK);
                    sender.setAccount(userInfo);
                    System.out.println("Response login: OK");
                }
            } else {
                result.setExtra("Wrong username or password!");
                System.out.println("Response login: FAIL - wrong username or password");
            }
            return result;
        }

        private Response responseAccountInfo(Worker sender) {
            System.out.println("Request account info for " + sender.getAccount().getAccountId());
            Response result = new Response();
            result.setCode(Response.CODE_OK);
            result.setExtra(sender.getAccount());
            return result;
        }

        private Response responseFriendsList(Worker sender) {
            System.out.println("Request all friends for " + sender.getAccount().getAccountId());
            List<UserInfo> allFriends = UserManager.getInstance().getAllUsersInfos();
            int exceptId = sender.getAccount().getAccountId();
            UserInfo exceptAccount = null;
            for (UserInfo friend : allFriends) {
                if (friend.getAccountId() == exceptId) {
                    exceptAccount = friend;
                    continue;
                }
                for (Worker worker : workers) {
                    if (worker.getAccount().equals(friend)) {
                        friend.setState(UserInfo.STATE_ONLINE);
                    }
                }
            }
            allFriends.remove(exceptAccount);
            Response result = new Response();
            result.setCode(Response.CODE_OK);
            result.setExtra(allFriends);
            return result;
        }

        private Response forwardChatMessage(Worker sender, Message message) {
            if (message == null)
                return null;
            System.out.println(String.format("%d > %d: %s", sender.getAccount().getAccountId(), message.getWhoId(),
                    message.getContent()));

            int whoReceiverId = message.getWhoId();
            Worker whoReceiver = null;

            message.setWhoId(sender.getAccount().getAccountId());

            synchronized (lock) {
                for (Worker _receiver : workers) {
                    if (_receiver.getAccount().getAccountId() == whoReceiverId) {
                        whoReceiver = _receiver;
                        break;
                    }
                }
            }

            Response result = new Response();
            result.setCode(Response.CODE_FAIL);
            if (whoReceiver != null) {
                try {
                    Response forwardResult = new Response();
                    forwardResult.setCode(Response.CODE_OK);
                    forwardResult.setRequestCode(Request.CODE_CHAT_MESSAGE);
                    forwardResult.setExtra(message);
                    whoReceiver.response(forwardResult);
                    result.setCode(Response.CODE_OK);
                } catch (IOException e) {
                    result.setExtra("Friend's connection broken down!");
                }
            } else {
                result.setExtra("Friend was offline!");
            }
            return result;
        }

        private Response responseRegister(String username) {
            System.out.println(String.format("Register: %s", username));
            Response result = new Response();
            result.setCode(Response.CODE_FAIL);
            if (Validator.checkValidUsername(username)) {
                username = username.trim();
                UserManager.getInstance().addUser(username);
                result.setCode(Response.CODE_OK);
            }
            return result;
        }
    }
}
