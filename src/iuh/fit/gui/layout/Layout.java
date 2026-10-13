package iuh.fit.gui.layout;

import iuh.fit.core.context.AppContext;
import iuh.fit.enumeration.AppLocale;
import iuh.fit.gui.page.AbstractPage;
import iuh.fit.gui.page.DashboardPage;
import iuh.fit.gui.page.LoginPage;
import iuh.fit.gui.page.PageRegistry;
import iuh.fit.gui.theme.Theme;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class Layout extends JPanel {

    private final PageRegistry registry = PageRegistry.getInstance();
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel content = new JPanel(cardLayout);
    private final LayoutHeader header;

    public Layout() {
        this.setBackground(Theme.BG);
        this.setLayout(new BorderLayout());
        this.header = new LayoutHeader(this.registry, this::goTo, this::toggleLocale, this::logout);
        this.content.setBackground(Theme.BG);
        this.addPages();
        this.bindShortcuts();
        this.add(this.content, BorderLayout.CENTER);
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
        AppLocale next = AppContext.getInstance().getLocale() == AppLocale.VN ? AppLocale.EN : AppLocale.VN;
        AppContext.getInstance().setLocale(next);
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
