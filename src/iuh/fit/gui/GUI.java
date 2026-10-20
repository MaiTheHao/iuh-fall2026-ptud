
package iuh.fit.gui;

import iuh.fit.core.context.AppContext;
import iuh.fit.gui.layout.Layout;
import iuh.fit.gui.page.LoginPage;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class GUI {

    private JFrame frame;

    public void start() {
        SwingUtilities.invokeLater(this::buildWindow);
    }

    public void restart() {
        SwingUtilities.invokeLater(() -> {
            if (frame != null) {
                frame.dispose();
            }
            buildWindow();
        });
    }

    private void buildWindow() {
        Layout layout = new Layout();
        AppContext.getInstance().setCurrentPage(LoginPage.class);

        frame = new JFrame(I18nReaderUtil.get("app.title"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setContentPane(layout);

        frame.setSize(1280, 800);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);

        layout.render();
        frame.setVisible(true);

        SwingUtilities.invokeLater(() -> {
            frame.revalidate();
            frame.repaint();
        });
    }
}
