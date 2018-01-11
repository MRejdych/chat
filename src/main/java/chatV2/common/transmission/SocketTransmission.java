package chatV2.common.transmission;

import chatV2.common.utils.StreamUtilities;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketTransmission implements Closeable {
    private Socket socket;
    private InputStream in;
    private OutputStream out;

    public Socket getSocket() {
        return socket;
    }

    public SocketTransmission(Socket socket) throws IOException {
        this.socket = socket;
        in = socket.getInputStream();
        out = socket.getOutputStream();
    }

    @Override
    public void close() {
        StreamUtilities.tryCloseStream(in, out, socket);
    }

    public void sendBytes(byte[] in) throws IOException {
        out.write(in);
    }

    public int receiveBytes(byte[] out, int offset, int length) throws IOException {
        return in.read(out, offset, length);
    }

    public boolean ready() throws IOException {
        return in.available() > 0;
    }

}
