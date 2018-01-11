package chatV2.common.utils;

import java.io.Closeable;
import java.io.IOException;

public final class StreamUtilities {
    public static void tryCloseStream(Closeable... obj) {
        try {
            for (Closeable closeable : obj) {
                closeable.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
