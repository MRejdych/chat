package chatV2.client;

import chatV2.common.data.User;
import chatV2.common.messages.Request;
import chatV2.common.messages.Response;
import chatV2.common.transmission.Protocol;
import chatV2.common.transmission.SerializationUtils;
import chatV2.common.transmission.SocketTransmission;
import chatV2.common.utils.StreamUtilities;
import chatV2.common.utils.Task;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public final class Client {
    private static Client instance = null;
    private SocketTransmission transmission;
    private Protocol protocol;
    private User myUser = new User();
    private List<OnDataReceivedListener> mOnDataReceivedListeners = new ArrayList<>();
    private OnConnectionHasProblemListener mOnConnectionHasProblemListener = null;
    private boolean pendingStop = false;

    public void addOnDataReceivedListener(OnDataReceivedListener listener) {
        mOnDataReceivedListeners.add(listener);
    }

    public void removeOnDataReceivedListener(OnDataReceivedListener listener) {
        mOnDataReceivedListeners.remove(listener);
    }

    public void setOnConnectionHasProblemListener(OnConnectionHasProblemListener listener) {
        mOnConnectionHasProblemListener = listener;
    }

    public static Client getInstance() {
        return instance;
    }

    public static void createInstance(String serverAddress, int serverPort) throws IOException {
        destroyInstance();
        instance = new Client(serverAddress, serverPort);
    }

    public static void destroyInstance() {
        if (instance != null) {
            instance.release();
            instance = null;
        }
    }

    private void release() {
        pendingStop = true;
        StreamUtilities.tryCloseStream(transmission);
    }

    public String getRemoteAddress() {
        if (transmission != null)
            return transmission.getSocket().getRemoteSocketAddress().toString();
        return "somewhere";
    }

    public void startLooper() {
        Task.run(() -> {
            boolean isCorruptData = false;
            do {
                try {
                    Object receivedObject = protocol.receiveObject();
                    if (receivedObject instanceof Response) {
                        for (int i = 0; i < mOnDataReceivedListeners.size(); i++) {
                            OnDataReceivedListener listener = mOnDataReceivedListeners.get(i);
                            if (!listener.onDataReceived(Client.this, (Response) receivedObject)) {
                                isCorruptData = true;
                                break;
                            }
                        }
                    } else {
                        isCorruptData = true;
                        System.out.println("Received data is NULL or corrupted");
                    }
                } catch (IOException e) {
                    if (!pendingStop && mOnConnectionHasProblemListener != null)
                        mOnConnectionHasProblemListener.onConnectionHasProblem(e.getMessage());
                }
            } while (!isCorruptData && !pendingStop);
            if (isCorruptData)
                fireConnectionHasProblemEvent("Received data has been corrupted!");
        });
    }

    private void fireConnectionHasProblemEvent(String message) {
        if (mOnConnectionHasProblemListener != null)
            mOnConnectionHasProblemListener.onConnectionHasProblem(message);
    }

    public void request(Request request) {
        try {
            protocol.sendObject(request);
        } catch (IOException e) {
            fireConnectionHasProblemEvent(e.getMessage());
        }
    }

    private Client(String serverAddress, int serverPort) throws IOException {
        Socket socket = new Socket(serverAddress, serverPort);
        transmission = new SocketTransmission(socket);
        SerializationUtils serializationUtils = new SerializationUtils();
        protocol = new Protocol(serializationUtils, transmission);
    }

    public int getMyId() {
        return myUser.getId();
    }

    public void setMyId(int id) {
        myUser.setId(id);
    }

    public String getMyUsername() {
        return myUser.getUsername();
    }

    public void setMyUsername(String myUsername) {
        myUser.setUsername(myUsername);
    }

    public interface OnDataReceivedListener {
        boolean onDataReceived(Client sender,  Response receivedObject);
    }

    public interface OnConnectionHasProblemListener {
        void onConnectionHasProblem(String message);
    }
}
