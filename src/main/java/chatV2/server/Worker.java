package chatV2.server;

import chatV2.common.messages.Request;
import chatV2.common.messages.Response;
import chatV2.common.data.UserInfo;
import chatV2.common.transmission.SerializationUtils;
import chatV2.common.transmission.Protocol;
import chatV2.common.transmission.SocketTransmission;
import chatV2.common.utils.StreamUtilities;
import com.sun.istack.internal.NotNull;

import java.io.IOException;
import java.net.Socket;

public class Worker {
    private UserInfo myAccount = null;
    private SocketTransmission transmission;
    private Protocol protocol;
    private OnRequestReceivedListener mOnRequestReceivedListener = null;
    private OnAuthenticatedListener mOnAuthenticatedListener = null;

    public void setOnReceivedDataListener(OnRequestReceivedListener listener) {
        mOnRequestReceivedListener = listener;
    }

    public void setOnAuthenticatedListener(OnAuthenticatedListener listener) {
        mOnAuthenticatedListener = listener;
    }

    public void response(@NotNull Response result) throws IOException {
        protocol.sendObject(result);
    }

    public void startBridge() throws IOException {
        while (true) {
            Object receivedObject = protocol.receiveObject();
            if (receivedObject == null)
                break;
            if (receivedObject instanceof Request) {
                if (mOnRequestReceivedListener != null)
                    response(mOnRequestReceivedListener.onRequestReceived(this, (Request) receivedObject));
            }
        }
    }

    public void release() {
        StreamUtilities.tryCloseStream(transmission);
    }

    Worker(Socket socket) throws IOException {
        transmission = new SocketTransmission(socket);
        SerializationUtils serializationUtils = new SerializationUtils();
        protocol = new Protocol(serializationUtils, transmission);
    }

    public void setAccount(UserInfo userInfo) {
        this.myAccount = userInfo;
        if (userInfo != null && mOnAuthenticatedListener != null)
            mOnAuthenticatedListener.onAuthenticated(this);
    }

    public UserInfo getAccount() {
        return myAccount;
    }

    public interface OnAuthenticatedListener {
        void onAuthenticated(Worker worker);
    }

    public interface OnRequestReceivedListener {
        @NotNull
        Response onRequestReceived(Worker sender, Request request);
    }
}
