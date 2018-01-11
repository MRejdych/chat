package chatV2.common.utils;

import com.sun.istack.internal.NotNull;

public final class Task {
    public static void run(@NotNull Runnable run) {
        Thread thread = new Thread(run);
        thread.setDaemon(true);
        thread.start();
    }
}
