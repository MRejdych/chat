package chatV2.client.ui;

import chatV2.client.Client;
import chatV2.common.messages.Request;
import chatV2.common.messages.Response;
import chatV2.common.utils.Task;

import javax.swing.*;

public abstract class ProcessingWindow extends Window implements Client.OnDataReceivedListener {
    private int requestCode;

    ProcessingWindow() {
        if (Client.getInstance() != null)
            Client.getInstance().addOnDataReceivedListener(this);
    }

    @Override
    protected void onWindowClosing() {
        super.onWindowClosing();
        if (Client.getInstance() != null)
            Client.getInstance().removeOnDataReceivedListener(this);
    }

    protected abstract void doneBackgoundTask(Response result);

    void doInBackground(Request request) {
        doInBackground(request, "Processing...");
    }

    void doInBackground(final Request request, String processingMessage) {
        this.requestCode = request.getCode();
        setVisible(false);
        ProcessingDialog.showBox(this, processingMessage);
        Task.run(() -> Client.getInstance().request(request));
    }

    @Override
    public boolean onDataReceived(Client sender, final Response receivedObject) {
        if (receivedObject.getRequestCode() == requestCode) {
            SwingUtilities.invokeLater(() -> {
                ProcessingDialog.hideBox();
                doneBackgoundTask(receivedObject);
            });

        }
        return true;
    }
}
