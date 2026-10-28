package iuh.fit.gui.page;

import com.formdev.flatlaf.FlatClientProperties;

import iuh.fit.dto.LoginDTO;
import iuh.fit.exception.CredentialsInvalidException;
import iuh.fit.service.AuthService;
import iuh.fit.util.I18nReaderUtil;

import iuh.fit.gui.component.LucideIcon;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.theme.Theme;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

/** Màn hình đăng nhập hệ thống. */
public class LoginPage extends AbstractPage {

    private static final int RONG_THE = 420;
    private static final int LE_THE = 28;
    private static final int CHIEU_CAO_O_NHAP = 42;
    private static final int CHIEU_CAO_NUT = 44;
    private static final int CO_ICON = 18;
    private static final int BO_GOC_THE = 14;
    private static final String DUONG_DAN_ANH_NEN = "resources/background.jpg";

    private static Image anhNen;
    private static boolean daTaiAnh;

    private final AuthService authService = new AuthService();
    private final JTextField tenDangNhapField = new JTextField();
    private final JPasswordField matKhauField = new JPasswordField();

    public LoginPage() {
        this.setLayout(new GridBagLayout());
        this.setOpaque(true);
        this.setBackground(Theme.BG);
    }

    @Override
    public void render() {
        this.removeAll();

        GridBagConstraints rangBuoc = new GridBagConstraints();
        rangBuoc.gridx = 0;
        rangBuoc.gridy = 0;
        rangBuoc.anchor = GridBagConstraints.CENTER;
        rangBuoc.fill = GridBagConstraints.NONE;
        this.add(this.taoTheDangNhap(), rangBuoc);

        this.bindEnter(this.tenDangNhapField);
        this.bindEnter(this.matKhauField);

        this.revalidate();
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D doHoa = (Graphics2D) g.create();
        try {
            doHoa.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Image anh = layAnhNen();
            if (anh == null) {
                doHoa.setPaint(new GradientPaint(0, 0, Theme.palette().ACCENT(),
                        this.getWidth(), this.getHeight(), Theme.palette().ACCENT_PRESSED()));
                doHoa.fillRect(0, 0, this.getWidth(), this.getHeight());
            } else {
                this.vePhuKin(doHoa, anh);
                doHoa.setColor(Theme.scrim());
                doHoa.fillRect(0, 0, this.getWidth(), this.getHeight());
            }
        } finally {
            doHoa.dispose();
        }
    }

    private void vePhuKin(Graphics2D doHoa, Image anh) {
        int rong = this.getWidth();
        int cao = this.getHeight();
        double tiLe = Math.max(rong / (double) anh.getWidth(null), cao / (double) anh.getHeight(null));
        int rongAnh = (int) Math.ceil(anh.getWidth(null) * tiLe);
        int caoAnh = (int) Math.ceil(anh.getHeight(null) * tiLe);
        doHoa.drawImage(anh, (rong - rongAnh) / 2, (cao - caoAnh) / 2, rongAnh, caoAnh, this);
    }

    private static Image layAnhNen() {
        if (!daTaiAnh) {
            daTaiAnh = true;
            InputStream luong = LoginPage.class.getClassLoader()
                    .getResourceAsStream(DUONG_DAN_ANH_NEN);
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

    private JPanel taoTheDangNhap() {
        JPanel the = new TheDangNhap();
        the.setLayout(new GridBagLayout());
        the.setOpaque(false);

        JPanel noiDung = new JPanel(new GridBagLayout());
        noiDung.setOpaque(false);

        GridBagConstraints rangBuoc = new GridBagConstraints();
        rangBuoc.gridx = 0;
        rangBuoc.gridy = -1;
        rangBuoc.weightx = 1;
        rangBuoc.fill = GridBagConstraints.HORIZONTAL;

        this.addDong(noiDung, rangBuoc, this.taoNhan(I18nReaderUtil.get("app.title"),
                fontCo(26f, Font.BOLD), Theme.palette().ACCENT(), SwingConstants.CENTER), 0, 0);
        this.addDong(noiDung, rangBuoc, this.taoNhan(I18nReaderUtil.get("app.login.tagline"),
                fontCo(14f, Font.PLAIN), Theme.palette().INK_MUTED(), SwingConstants.CENTER), 6, 26);

        this.addDong(noiDung, rangBuoc, this.taoNhan(I18nReaderUtil.get("app.login.title"),
                fontCo(20f, Font.BOLD), Theme.palette().INK(), SwingConstants.LEFT), 0, 18);

        this.cauHinhONhap(this.tenDangNhapField, "user", "app.login.username");
        this.addDong(noiDung, rangBuoc, this.taoNhan(I18nReaderUtil.get("app.login.username"),
                Theme.fieldLabelFont(), Theme.INK, SwingConstants.LEFT), 0, 16);
        this.addDong(noiDung, rangBuoc, this.tenDangNhapField, 6, 14);

        this.cauHinhONhap(this.matKhauField, "lock", "app.login.password");
        this.addDong(noiDung, rangBuoc, this.taoNhan(I18nReaderUtil.get("app.login.password"),
                Theme.fieldLabelFont(), Theme.INK, SwingConstants.LEFT), 0, 8);
        this.addDong(noiDung, rangBuoc, this.matKhauField, 6, 20);

        JButton dangNhapButton = new JButton(I18nReaderUtil.get("app.login.submit"));
        Theme.applyPrimary(dangNhapButton);
        dangNhapButton.setPreferredSize(new Dimension(RONG_THE - 2 * LE_THE, CHIEU_CAO_NUT));
        dangNhapButton.setFocusPainted(false);
        dangNhapButton.addActionListener(e -> this.dangNhap());
        this.addDong(noiDung, rangBuoc, dangNhapButton, 0, 4);

        JButton thoatButton = new JButton(I18nReaderUtil.get("app.login.exit"));
        thoatButton.setContentAreaFilled(false);
        thoatButton.setBorderPainted(false);
        thoatButton.setForeground(Theme.palette().INK_SECONDARY());
        thoatButton.setFocusPainted(false);
        thoatButton.addActionListener(e -> System.exit(0));
        this.addDong(noiDung, rangBuoc, thoatButton, 0, 0);

        GridBagConstraints rangBuocThe = new GridBagConstraints();
        rangBuocThe.gridx = 0;
        rangBuocThe.gridy = 0;
        rangBuocThe.weightx = 1;
        rangBuocThe.weighty = 1;
        rangBuocThe.fill = GridBagConstraints.HORIZONTAL;
        rangBuocThe.anchor = GridBagConstraints.CENTER;
        rangBuocThe.insets = new Insets(LE_THE, LE_THE, LE_THE, LE_THE);
        the.add(noiDung, rangBuocThe);

        return the;
    }

    private void cauHinhONhap(JTextField oNhap, String iconName, String placeholderKey) {
        oNhap.setPreferredSize(new Dimension(RONG_THE - 2 * LE_THE, CHIEU_CAO_O_NHAP));
        oNhap.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,
                I18nReaderUtil.get(placeholderKey));
        oNhap.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON,
                new LucideIcon(iconName, CO_ICON, Theme.palette().INK_MUTED()));
    }

    private void addDong(JPanel noiDung, GridBagConstraints rangBuoc, JComponent thanhPhan,
            int leTren, int leDuoi) {
        rangBuoc.gridy++;
        rangBuoc.insets = new Insets(leTren, 0, leDuoi, 0);
        noiDung.add(thanhPhan, rangBuoc);
    }

    private JLabel taoNhan(String text, Font font, Color mauChu, int canChinh) {
        JLabel nhan = new JLabel(text, canChinh);
        nhan.setFont(font);
        nhan.setForeground(mauChu);
        return nhan;
    }

    private static Font fontCo(float co, int kieu) {
        Font goc = UIManager.getFont("Label.font");
        Font coSo = goc == null ? new JLabel().getFont() : goc;
        return coSo.deriveFont(kieu, co);
    }

    @Override
    public String getTitleKey() {
        return "app.login.title";
    }

    private void bindEnter(JTextField field) {
        field.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "dangNhap");
        field.getActionMap().put("dangNhap", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoginPage.this.dangNhap();
            }
        });
    }

    private void dangNhap() {
        String tenDangNhap = this.tenDangNhapField.getText();
        String matKhau = new String(this.matKhauField.getPassword());
        try {
            this.authService.login(new LoginDTO(tenDangNhap, matKhau));
            this.goTo(DashboardPage.class);
        } catch (CredentialsInvalidException e) {
            ThongBao.loi(this, "app.login.errorInvalid");
        } catch (SQLException e) {
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }

    /** Thẻ trắng bo góc đặt giữa nền ảnh gara. */
    private static final class TheDangNhap extends JPanel {

        private static final long serialVersionUID = 1L;

        private TheDangNhap() {
            this.setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D doHoa = (Graphics2D) g.create();
            try {
                doHoa.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                doHoa.setColor(Theme.palette().PANEL());
                doHoa.fillRoundRect(0, 0, this.getWidth() - 1, this.getHeight() - 1,
                        BO_GOC_THE, BO_GOC_THE);
                doHoa.setColor(Theme.palette().BORDER());
                doHoa.drawRoundRect(0, 0, this.getWidth() - 1, this.getHeight() - 1,
                        BO_GOC_THE, BO_GOC_THE);
            } finally {
                doHoa.dispose();
            }
        }
    }
}
