package iuh.fit.gui.page;

import iuh.fit.core.context.AppContext;
import iuh.fit.entity.NhanVien;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.gui.theme.Theme;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Font;

public class DashboardPage extends AbstractPage {

    public DashboardPage() {
        setLayout(new BorderLayout());
    }

    @Override
    public void render() {
        removeAll();
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        NhanVien nhanVien = AppContext.getInstance().getCurrentUser();

        JPanel card = Theme.contentPanel(12);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        if (nhanVien != null) {
            card.add(dong(I18nReaderUtil.get("app.dashboard.greeting") + ": " + nhanVien.getHoTen(), true));
            card.add(dong(I18nReaderUtil.get("app.dashboard.role") + ": "
                    + (nhanVien.getVaiTro() == null ? "" : nhanVien.getVaiTro().getI18NBusinessName()), false));
            card.add(dong(I18nReaderUtil.get("app.dashboard.code") + ": " + nhanVien.getMaNhanVien(), false));
        }

        this.add(card, BorderLayout.NORTH);
        revalidate();
        repaint();
    }

    @Override
    public String getTitleKey() {
        return "app.menu.home";
    }

    @Override
    public MenuGroup getGroup() {
        return MenuGroup.TRANG_CHU;
    }

    private JLabel dong(String text, boolean bold) {
        JLabel label = new JLabel(text);
        if (bold) {
            label.setFont(label.getFont().deriveFont(Font.BOLD));
        }
        return label;
    }
}
