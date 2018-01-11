package chatV2.client.ui;

import javax.swing.*;
import java.awt.*;

public final class MessageBox {
    public static void showMessageBoxInUIThread(final Component parentComponent, final Object message) {
        SwingUtilities.invokeLater(() -> showMessageBox(parentComponent, message));
    }

    private static void showMessageBox(final Component parentComponent, final Object message) {
        JLabel labelMessage = new JLabel(message.toString(), SwingConstants.CENTER);
        final JComponent[] components = new JComponent[]{labelMessage};
        JOptionPane.showMessageDialog(parentComponent, components, "Message", JOptionPane.PLAIN_MESSAGE);
    }
}
