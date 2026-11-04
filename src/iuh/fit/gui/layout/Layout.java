package iuh.fit.gui.layout;

import iuh.fit.core.context.AppContext;
import iuh.fit.enumeration.AppLocale;
import iuh.fit.gui.Theme;
import iuh.fit.gui.page.AbstractPage;
import iuh.fit.gui.page.DashboardPage;
import iuh.fit.gui.page.LoginPage;
import iuh.fit.gui.page.PageRegistry;

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

    private static final String DUONG_DAN_ANH_NEN = "resources/background.jpg";
    private static final Image ANH_NEN = taiAnhNen();

    private final PageRegistry registry = PageRegistry.getInstance();
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel pnlNoiDung = new JPanel(cardLayout);
    private final LayoutHeader header;
    private AppContext context;

    public Layout() {
        this.setOpaque(true);
        this.setBackground(Theme.palette().BG());
        this.setLayout(new BorderLayout());
        this.header = new LayoutHeader(this.registry, this::goTo, this::toggleLocale, this::logout);
        this.pnlNoiDung.setOpaque(false);
        this.addPages();
        this.bindShortcuts();
        this.add(this.pnlNoiDung, BorderLayout.CENTER);
    }

    private static Image taiAnhNen() {
        try (InputStream luong = Layout.class.getClassLoader().getResourceAsStream(DUONG_DAN_ANH_NEN)) {
            return luong == null ? null : ImageIO.read(luong);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (ANH_NEN != null) {
            this.veAnhNen(g, ANH_NEN);
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
            page.setNavigator(this::goTo);
            this.pnlNoiDung.add(page, cardKey(page.getClass()));
        }
    }

    private static String cardKey(Class<? extends AbstractPage> type) {
        return type.getName();
    }

    private void goTo(Class<? extends AbstractPage> page) {
        this.context.setCurrentPage(page);
        this.render();
    }

    private void toggleLocale() {
        AppLocale next = this.context.getLocale() == AppLocale.VN ? AppLocale.EN : AppLocale.VN;
        this.context.setLocale(next);
        this.render();
    }

    private void logout() {
        this.context.logout();
        this.goTo(LoginPage.class);
    }

    public void render() {
        AppContext context = AppContext.getInstance();
        this.context = context;
        AbstractPage page = this.resolveCurrentPage();
        this.cardLayout.show(this.pnlNoiDung, cardKey(page.getClass()));
        page.render();
        this.header.refreshText();
        this.header.setActive(page.getClass());
        this.header.setMenuVisible(context.getCurrentUser() != null);
        this.pnlNoiDung.revalidate();
        this.pnlNoiDung.repaint();
        this.syncMenuBar(context);
    }

    private void syncMenuBar(AppContext context) {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof JFrame frame) {
            boolean loggedIn = context.getCurrentUser() != null;
            frame.setJMenuBar(loggedIn ? this.header : null);
        }
    }

    private AbstractPage resolveCurrentPage() {
        AbstractPage page = this.registry.get(this.context.getCurrentPage());
        return page == null ? this.registry.get(DashboardPage.class) : page;
    }
}
