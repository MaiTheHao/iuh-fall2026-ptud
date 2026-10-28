package iuh.fit.gui.page;

import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.component.LucideIcon;
import iuh.fit.gui.theme.Theme;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;

/**
 * Trang giữ chỗ hiển thị tiêu đề tạm, chưa có chức năng thực.
 */
public abstract class PlaceholderPage extends AbstractPage {

    private static final int CO_ICON = 34;

    private final MenuGroup group;
    private final String titleKey;
    private final boolean separatorBefore;

    protected PlaceholderPage(MenuGroup group, String titleKey) {
        this(group, titleKey, false);
    }

    protected PlaceholderPage(MenuGroup group, String titleKey, boolean separatorBefore) {
        setLayout(new BorderLayout());
        this.group = group;
        this.titleKey = titleKey;
        this.separatorBefore = separatorBefore;
    }

    @Override
    public final void render() {
        this.removeAll();
        this.setBorder(Theme.leDeu(8));

        JPanel the = Theme.contentPanel(12);
        the.add(this.taoKhoiThongBao(), BorderLayout.CENTER);
        this.add(the, BorderLayout.CENTER);

        this.revalidate();
        this.repaint();
    }

    private JPanel taoKhoiThongBao() {
        JPanel khoi = new JPanel();
        khoi.setLayout(new BoxLayout(khoi, BoxLayout.Y_AXIS));
        khoi.setOpaque(false);

        JLabel icon = new JLabel(new LucideIcon("construction", CO_ICON, Theme.palette().INK_MUTED()),
                SwingConstants.CENTER);
        icon.setAlignmentX(CENTER_ALIGNMENT);

        JLabel tieuDe = this.taoNhanTrang(I18nReaderUtil.get(this.titleKey));
        tieuDe.setAlignmentX(CENTER_ALIGNMENT);

        khoi.add(icon);
        khoi.add(Box.createVerticalStrut(10));
        khoi.add(tieuDe);

        JPanel boc = new JPanel(new GridBagLayout());
        boc.setOpaque(false);
        boc.add(khoi);
        return boc;
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
    public final boolean hasSeparatorBefore() {
        return this.separatorBefore;
    }
}
