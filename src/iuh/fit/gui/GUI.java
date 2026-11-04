
package iuh.fit.gui;

import iuh.fit.core.context.AppContext;
import iuh.fit.gui.layout.Layout;
import iuh.fit.gui.page.LoginPage;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class GUI {

    private JFrame frmChinh;

    public void start() {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        SwingUtilities.invokeLater(this::buildWindow);
    }

    public void restart() {
        SwingUtilities.invokeLater(() -> {
            if (frmChinh != null) {
                frmChinh.dispose();
            }
            buildWindow();
        });
    }

    private void buildWindow() {
        Layout layout = new Layout();
        AppContext.getInstance().setCurrentPage(LoginPage.class);

        frmChinh = new JFrame(I18nReaderUtil.get("app.title"));
        frmChinh.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frmChinh.setContentPane(layout);

        frmChinh.setSize(1280, 800);
        frmChinh.setResizable(true);
        frmChinh.setLocationRelativeTo(null);

        layout.render();
        frmChinh.setVisible(true);

        SwingUtilities.invokeLater(() -> {
            frmChinh.revalidate();
            frmChinh.repaint();
        });
    }
}
