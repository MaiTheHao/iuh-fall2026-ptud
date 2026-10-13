package iuh.fit.gui.page;

import iuh.fit.enumeration.MenuGroup;
import iuh.fit.util.I18nReaderUtil;

import iuh.fit.gui.theme.Theme;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;

/**
 * Trang giữ chỗ hiển thị tiêu đề tạm, chưa có chức năng thực.
 */
public abstract class PlaceholderPage extends AbstractPage {

    private final MenuGroup group;
    private final String titleKey;
    private final KeyStroke shortcut;
    private final boolean separatorBefore;

    protected PlaceholderPage(MenuGroup group, String titleKey) {
        this(group, titleKey, null, false);
    }

    protected PlaceholderPage(MenuGroup group, String titleKey, KeyStroke shortcut) {
        this(group, titleKey, shortcut, false);
    }

    protected PlaceholderPage(MenuGroup group, String titleKey, KeyStroke shortcut, boolean separatorBefore) {
        setLayout(new BorderLayout());
        this.group = group;
        this.titleKey = titleKey;
        this.shortcut = shortcut;
        this.separatorBefore = separatorBefore;
    }

    @Override
    public final void render() {
        removeAll();
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JPanel card = Theme.contentPanel(12);
        JLabel label = new JLabel(I18nReaderUtil.get(this.titleKey), SwingConstants.CENTER);
        card.add(label, BorderLayout.CENTER);
        this.add(card, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    @Override
    public final String getTitleKey() {
        return this.titleKey;
    }

    @Override
    public final MenuGroup getGroup() {
        return this.group;
    }

    @Override
    public final KeyStroke getShortcut() {
        return this.shortcut;
    }

    @Override
    public final boolean hasSeparatorBefore() {
        return this.separatorBefore;
    }
}
