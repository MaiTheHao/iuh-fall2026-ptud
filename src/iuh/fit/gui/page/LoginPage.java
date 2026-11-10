package iuh.fit.gui.page;

import com.formdev.flatlaf.FlatClientProperties;

import iuh.fit.dto.LoginDTO;
import iuh.fit.exception.AuthException;
import iuh.fit.service.AuthService;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.gui.Theme;
import iuh.fit.gui.component.LucideIcon;
import iuh.fit.gui.component.TheBoGoc;
import iuh.fit.gui.component.ThongBao;

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

public class LoginPage extends AbstractPage {

    private static final int RONG_THE = 420;
    private static final int LE_THE = 28;
    private static final int CHIEU_CAO_O_NHAP = 42;
    private static final int CHIEU_CAO_NUT = 44;
    private static final int CO_ICON = 18;
    private static final int BO_GOC_THE = 14;
    private static final String DUONG_DAN_ANH_NEN = "resources/background.jpg";
    private static final Image ANH_NEN = taiAnhNen();

    private final AuthService authService = new AuthService();
    private final JTextField txtTenDangNhap = new JTextField();
    private final JPasswordField txtMatKhau = new JPasswordField();

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

        this.bindEnter(this.txtTenDangNhap);
        this.bindEnter(this.txtMatKhau);

        this.revalidate();
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D doHoa = (Graphics2D) g.create();
        try {
            doHoa.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Image anh = ANH_NEN;
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

    private static Image taiAnhNen() {
        try (InputStream luong = LoginPage.class.getClassLoader()
                .getResourceAsStream(DUONG_DAN_ANH_NEN)) {
            return luong == null ? null : ImageIO.read(luong);
        } catch (IOException e) {
            return null;
        }
    }

    private JPanel taoTheDangNhap() {
        JPanel pnlThe = new TheBoGoc(BO_GOC_THE);
        pnlThe.setLayout(new GridBagLayout());

        JPanel pnlNoiDung = new JPanel(new GridBagLayout());
        pnlNoiDung.setOpaque(false);

        GridBagConstraints rangBuoc = new GridBagConstraints();
        rangBuoc.gridx = 0;
        rangBuoc.gridy = -1;
        rangBuoc.weightx = 1;
        rangBuoc.fill = GridBagConstraints.HORIZONTAL;

        this.taoPhanTieuDe(pnlNoiDung, rangBuoc);
        this.taoPhanDangNhap(pnlNoiDung, rangBuoc);
        this.taoPhanNutBam(pnlNoiDung, rangBuoc);

        GridBagConstraints rangBuocThe = new GridBagConstraints();
        rangBuocThe.gridx = 0;
        rangBuocThe.gridy = 0;
        rangBuocThe.weightx = 1;
        rangBuocThe.weighty = 1;
        rangBuocThe.fill = GridBagConstraints.HORIZONTAL;
        rangBuocThe.anchor = GridBagConstraints.CENTER;
        rangBuocThe.insets = new Insets(LE_THE, LE_THE, LE_THE, LE_THE);
        pnlThe.add(pnlNoiDung, rangBuocThe);

        return pnlThe;
    }

    private void taoPhanTieuDe(JPanel pnlNoiDung, GridBagConstraints rangBuoc) {
        this.addDong(pnlNoiDung, rangBuoc, this.taoNhan(I18nReaderUtil.get("app.title"),
                fontCo(26f, Font.BOLD), Theme.palette().ACCENT(), SwingConstants.CENTER), 0, 0);
        this.addDong(pnlNoiDung, rangBuoc, this.taoNhan(I18nReaderUtil.get("app.login.tagline"),
                fontCo(14f, Font.PLAIN), Theme.palette().INK_MUTED(), SwingConstants.CENTER), 6, 26);
        this.addDong(pnlNoiDung, rangBuoc, this.taoNhan(I18nReaderUtil.get("app.login.title"),
                fontCo(20f, Font.BOLD), Theme.palette().INK(), SwingConstants.LEFT), 0, 18);
    }

    private void taoPhanDangNhap(JPanel pnlNoiDung, GridBagConstraints rangBuoc) {
        this.cauHinhONhap(this.txtTenDangNhap, "user", "app.login.username");
        this.addDong(pnlNoiDung, rangBuoc, this.taoNhan(I18nReaderUtil.get("app.login.username"),
                Theme.fieldLabelFont(), Theme.INK, SwingConstants.LEFT), 0, 16);
        this.addDong(pnlNoiDung, rangBuoc, this.txtTenDangNhap, 6, 14);

        this.cauHinhONhap(this.txtMatKhau, "lock", "app.login.password");
        this.addDong(pnlNoiDung, rangBuoc, this.taoNhan(I18nReaderUtil.get("app.login.password"),
                Theme.fieldLabelFont(), Theme.INK, SwingConstants.LEFT), 0, 8);
        this.addDong(pnlNoiDung, rangBuoc, this.txtMatKhau, 6, 20);
    }

    private void taoPhanNutBam(JPanel pnlNoiDung, GridBagConstraints rangBuoc) {
        JButton btnDangNhap = new JButton(I18nReaderUtil.get("app.login.submit"));
        Theme.applyPrimary(btnDangNhap);
        btnDangNhap.setPreferredSize(new Dimension(RONG_THE - 2 * LE_THE, CHIEU_CAO_NUT));
        btnDangNhap.setFocusPainted(false);
        btnDangNhap.addActionListener(e -> this.dangNhap());
        this.addDong(pnlNoiDung, rangBuoc, btnDangNhap, 0, 4);

        JButton btnThoat = new JButton(I18nReaderUtil.get("app.login.exit"));
        btnThoat.setContentAreaFilled(false);
        btnThoat.setBorderPainted(false);
        btnThoat.setForeground(Theme.palette().INK_SECONDARY());
        btnThoat.setFocusPainted(false);
        btnThoat.addActionListener(e -> System.exit(0));
        this.addDong(pnlNoiDung, rangBuoc, btnThoat, 0, 0);
    }

    private void cauHinhONhap(JTextField oNhap, String iconName, String placeholderKey) {
        oNhap.setPreferredSize(new Dimension(RONG_THE - 2 * LE_THE, CHIEU_CAO_O_NHAP));
        oNhap.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,
                I18nReaderUtil.get(placeholderKey));
        oNhap.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON,
                new LucideIcon(iconName, CO_ICON, Theme.palette().INK_MUTED()));
    }

    private void addDong(JPanel pnlNoiDung, GridBagConstraints rangBuoc, JComponent thanhPhan,
            int leTren, int leDuoi) {
        rangBuoc.gridy++;
        rangBuoc.insets = new Insets(leTren, 0, leDuoi, 0);
        pnlNoiDung.add(thanhPhan, rangBuoc);
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
        String tenDangNhap = this.txtTenDangNhap.getText();
        String matKhau = new String(this.txtMatKhau.getPassword());
        try {
            this.authService.login(new LoginDTO(tenDangNhap, matKhau));
            this.goTo(DashboardPage.class);
        } catch (AuthException e) {
            ThongBao.loi(this, "app.login.errorInvalid");
        } catch (SQLException e) {
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }
}
