package chatV2.common.utils;

public final class Task {
    public static void run( Runnable run) {
        Thread thread = new Thread(run);
        thread.setDaemon(true);
        thread.start();
    }
}
