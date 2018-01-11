package chatV2.client.ui;

import javax.swing.*;
import java.awt.*;

public class ProcessingDialog extends Window {
    private static ProcessingDialog instance = null;

    public static void showBox(Component owner, String message) {
        if (instance != null && !instance.isVisible())
            hideBox();
        instance = new ProcessingDialog();
        instance.setTitle(message);
        instance.setLocationRelativeTo(owner);
        instance.setVisible(true);
    }

    public static void hideBox() {
        if (instance != null) {
            instance.dispose();
            instance = null;
        }
    }

    @Override
    protected void initializeComponents() {
        setResizable(false);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        getContentPane().add(progressBar, BorderLayout.CENTER);

        setSize(350, 50);
    }
}
