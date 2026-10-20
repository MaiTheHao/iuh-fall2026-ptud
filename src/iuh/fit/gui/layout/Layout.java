package iuh.fit.gui.layout;

import iuh.fit.core.context.AppContext;
import iuh.fit.enumeration.AppLocale;
import iuh.fit.gui.page.AbstractPage;
import iuh.fit.gui.page.DashboardPage;
import iuh.fit.gui.page.LoginPage;
import iuh.fit.gui.page.PageRegistry;
import iuh.fit.gui.theme.Theme;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;

public class Layout extends JPanel {

    private static Image anhNen;
    private static boolean daTaiAnh;

    private final PageRegistry registry = PageRegistry.getInstance();
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel content = new JPanel(cardLayout);
    private final LayoutHeader header;

    public Layout() {
        this.setOpaque(true);
        this.setBackground(Theme.BG);
        this.setLayout(new BorderLayout());
        this.header = new LayoutHeader(this.registry, this::goTo, this::toggleLocale, this::logout);
        this.content.setOpaque(false);
        this.addPages();
        this.bindShortcuts();
        this.add(this.content, BorderLayout.CENTER);
    }

    private static Image layAnhNen() {
        if (!daTaiAnh) {
            daTaiAnh = true;
            InputStream luong = Layout.class.getClassLoader().getResourceAsStream("resources/background.jpg");
            if (luong != null) {
                try (luong) {
                    anhNen = ImageIO.read(luong);
                } catch (IOException e) {
                    anhNen = null;
                }
            }
        }
        return anhNen;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Image anh = layAnhNen();
        if (anh != null) {
            this.veAnhNen(g, anh);
        }
        g.setColor(Theme.phuSangAnh());
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
    }

    private void veAnhNen(Graphics g, Image anh) {
        int rong = this.getWidth();
        int cao = this.getHeight();
        double tiLe = Math.max(rong / (double) anh.getWidth(null), cao / (double) anh.getHeight(null));
        int rongAnh = (int) Math.ceil(anh.getWidth(null) * tiLe);
        int caoAnh = (int) Math.ceil(anh.getHeight(null) * tiLe);
        g.drawImage(anh, (rong - rongAnh) / 2, (cao - caoAnh) / 2, rongAnh, caoAnh, this);
    }

    private void bindShortcuts() {
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), "toggleLocale");
        this.getActionMap().put("toggleLocale", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Layout.this.toggleLocale();
            }
        });
    }

    private void addPages() {
        for (AbstractPage page : this.registry.all()) {
            page.setOpaque(false);
            page.setBackground(Theme.BG);
            page.setNavigator(this::goTo);
            this.content.add(page, cardKey(page.getClass()));
        }
    }

    private static String cardKey(Class<? extends AbstractPage> type) {
        return type.getName();
    }

    public LayoutHeader getHeader() {
        return this.header;
    }

    public void goTo(Class<? extends AbstractPage> page) {
        AppContext.getInstance().setCurrentPage(page);
        this.render();
    }

    public void toggleLocale() {
        AppContext context = AppContext.getInstance();
        AppLocale next = context.getLocale() == AppLocale.VN ? AppLocale.EN : AppLocale.VN;
        context.setLocale(next);
        this.render();
    }

    public void logout() {
        AppContext.getInstance().logout();
        this.goTo(LoginPage.class);
    }

    public void render() {
        AbstractPage page = this.resolveCurrentPage();
        this.cardLayout.show(this.content, cardKey(page.getClass()));
        page.render();
        this.header.refreshText();
        this.header.setActive(page.getClass());
        this.header.setMenuVisible(AppContext.getInstance().getCurrentUser() != null);
        this.updateWindowState();
    }

    private void updateWindowState() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof JFrame frame) {
            boolean loggedIn = AppContext.getInstance().getCurrentUser() != null;
            frame.setJMenuBar(loggedIn ? this.header : null);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
    }

    private AbstractPage resolveCurrentPage() {
        AbstractPage page = this.registry.get(AppContext.getInstance().getCurrentPage());
        if (page == null) {
            AppContext.getInstance().setCurrentPage(DashboardPage.class);
            page = this.registry.get(DashboardPage.class);
        }
        return page;
    }
}
