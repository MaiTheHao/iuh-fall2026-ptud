package iuh.fit.gui.page;

import iuh.fit.dto.LoginDTO;
import iuh.fit.exception.CredentialsInvalidException;
import iuh.fit.service.AuthService;
import iuh.fit.util.I18nReaderUtil;

import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.theme.Theme;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
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

    private static final int RONG_COT_NOI_DUNG = 350;

    private final AuthService authService = new AuthService();
    private final JTextField tenDangNhapField = new JTextField(24);
    private final JPasswordField matKhauField = new JPasswordField(24);

    public LoginPage() {
        this.setLayout(new GridBagLayout());
    }

    @Override
    public void render() {
        this.removeAll();

        GridBagConstraints rangBuoc = new GridBagConstraints();
        rangBuoc.gridy = 0;
        rangBuoc.weighty = 1;
        rangBuoc.fill = GridBagConstraints.BOTH;

        rangBuoc.gridx = 0;
        rangBuoc.weightx = 0.70;
        this.add(new BannerDangNhap(), rangBuoc);

        rangBuoc.gridx = 1;
        rangBuoc.weightx = 0.30;
        this.add(this.taoKhuVucForm(), rangBuoc);

        this.bindEnter(this.tenDangNhapField);
        this.bindEnter(this.matKhauField);

        this.revalidate();
        this.repaint();
    }

    private JPanel taoKhuVucForm() {
        JPanel khuVuc = new JPanel(new GridBagLayout());
        khuVuc.setOpaque(true);
        khuVuc.setBackground(Theme.palette().PANEL());

        JPanel cot = new JPanel(new GridBagLayout());
        cot.setOpaque(false);

        GridBagConstraints rangBuoc = new GridBagConstraints();
        rangBuoc.gridx = 0;
        rangBuoc.weightx = 1;
        rangBuoc.fill = GridBagConstraints.HORIZONTAL;

        JLabel tieuDe = new JLabel(I18nReaderUtil.get("app.login.title"));
        tieuDe.setFont(new Font("SansSerif", Font.BOLD, 24));
        tieuDe.setForeground(Theme.palette().INK());
        rangBuoc.gridy = 0;
        rangBuoc.insets = new Insets(0, 0, 28, 0);
        cot.add(tieuDe, rangBuoc);

        JLabel nhanTenDangNhap = new JLabel(I18nReaderUtil.get("app.login.username"));
        nhanTenDangNhap.setFont(new Font("SansSerif", Font.BOLD, 13));
        nhanTenDangNhap.setForeground(Theme.palette().INK_SECONDARY());
        rangBuoc.gridy = 1;
        rangBuoc.insets = new Insets(0, 0, 8, 0);
        cot.add(nhanTenDangNhap, rangBuoc);

        this.tenDangNhapField.setPreferredSize(
                new Dimension(this.tenDangNhapField.getPreferredSize().width, 42));
        rangBuoc.gridy = 2;
        rangBuoc.insets = new Insets(0, 0, 8, 0);
        cot.add(this.tenDangNhapField, rangBuoc);

        JLabel nhanMatKhau = new JLabel(I18nReaderUtil.get("app.login.password"));
        nhanMatKhau.setFont(new Font("SansSerif", Font.BOLD, 13));
        nhanMatKhau.setForeground(Theme.palette().INK_SECONDARY());
        rangBuoc.gridy = 3;
        rangBuoc.insets = new Insets(0, 0, 8, 0);
        cot.add(nhanMatKhau, rangBuoc);

        this.matKhauField.setPreferredSize(
                new Dimension(this.matKhauField.getPreferredSize().width, 42));
        rangBuoc.gridy = 4;
        rangBuoc.insets = new Insets(0, 0, 20, 0);
        cot.add(this.matKhauField, rangBuoc);

        JButton dangNhapButton = new JButton(I18nReaderUtil.get("app.login.submit"));
        dangNhapButton.setPreferredSize(
                new Dimension(dangNhapButton.getPreferredSize().width, 44));
        Theme.applyPrimary(dangNhapButton);
        dangNhapButton.addActionListener(e -> this.dangNhap());
        rangBuoc.gridy = 5;
        rangBuoc.insets = new Insets(0, 0, 8, 0);
        cot.add(dangNhapButton, rangBuoc);

        JButton thoatButton = new JButton(I18nReaderUtil.get("app.login.exit"));
        thoatButton.setPreferredSize(
                new Dimension(thoatButton.getPreferredSize().width, 40));
        thoatButton.setOpaque(true);
        thoatButton.setContentAreaFilled(true);
        thoatButton.setBackground(Theme.palette().PANEL());
        thoatButton.setForeground(Theme.palette().INK());
        thoatButton.setBorder(BorderFactory.createLineBorder(Theme.palette().BORDER()));
        thoatButton.setFocusPainted(false);
        thoatButton.addActionListener(e -> System.exit(0));
        rangBuoc.gridy = 6;
        rangBuoc.insets = new Insets(0, 0, 0, 0);
        cot.add(thoatButton, rangBuoc);

        cot.setPreferredSize(new Dimension(RONG_COT_NOI_DUNG, cot.getPreferredSize().height));

        GridBagConstraints rangBuocKhuVuc = new GridBagConstraints();
        rangBuocKhuVuc.gridx = 0;
        rangBuocKhuVuc.gridy = 0;
        rangBuocKhuVuc.weightx = 1;
        rangBuocKhuVuc.weighty = 1;
        rangBuocKhuVuc.anchor = GridBagConstraints.CENTER;
        rangBuocKhuVuc.fill = GridBagConstraints.NONE;
        khuVuc.add(cot, rangBuocKhuVuc);
        return khuVuc;
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

    private static final class BannerDangNhap extends JPanel {

        private static final long serialVersionUID = 1L;
        private static final String DUONG_DAN_ANH_NEN = "resources/background.jpg";

        private static Image anhNen;
        private static boolean daTaiAnh;

        private BannerDangNhap() {
            this.setOpaque(true);
            this.setLayout(new GridBagLayout());

            JPanel noiDung = new JPanel(new GridBagLayout());
            noiDung.setOpaque(false);
            noiDung.setBorder(BorderFactory.createEmptyBorder(0, 48, 0, 48));

            GridBagConstraints rangBuoc = new GridBagConstraints();
            rangBuoc.gridx = 0;
            rangBuoc.weightx = 1;
            rangBuoc.fill = GridBagConstraints.HORIZONTAL;

            Color mauChu = Theme.chuTrenAnh();
            Color mauBong = new Color(0, 20, 70, 190);
            Color mauQuangSang = voiDoMo(Theme.palette().FOCUS(), 110);

            JLabel tenUngDung = new JLabel(I18nReaderUtil.get("app.title"), SwingConstants.CENTER) {

                private static final long serialVersionUID = 1L;

                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D doHoa = (Graphics2D) g.create();
                    try {
                        doHoa.setRenderingHint(
                                RenderingHints.KEY_TEXT_ANTIALIASING,
                                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                        doHoa.setFont(this.getFont());
                        FontMetrics soDo = doHoa.getFontMetrics();
                        String chu = this.getText();
                        int x = (this.getWidth() - soDo.stringWidth(chu)) / 2;
                        int y = (this.getHeight() - soDo.getHeight()) / 2 + soDo.getAscent();

                        doHoa.setColor(mauBong);
                        doHoa.drawString(chu, x + 4, y + 5);

                        doHoa.setColor(mauQuangSang);
                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dy = -1; dy <= 1; dy++) {
                                if (dx != 0 || dy != 0) {
                                    doHoa.drawString(chu, x + dx, y + dy);
                                }
                            }
                        }

                        doHoa.setColor(mauChu);
                        doHoa.drawString(chu, x, y);
                    } finally {
                        doHoa.dispose();
                    }
                }
            };
            tenUngDung.setFont(new Font("SansSerif", Font.BOLD, 48));
            tenUngDung.setForeground(mauChu);
            tenUngDung.setPreferredSize(new Dimension(650, 80));
            rangBuoc.gridy = 0;
            rangBuoc.insets = new Insets(0, 0, 0, 0);
            noiDung.add(tenUngDung, rangBuoc);

            JPanel thanhNhan = new JPanel();
            thanhNhan.setPreferredSize(new Dimension(72, 4));
            thanhNhan.setOpaque(true);
            thanhNhan.setBackground(Theme.palette().ACCENT());
            rangBuoc.gridy = 1;
            rangBuoc.weightx = 0;
            rangBuoc.fill = GridBagConstraints.NONE;
            rangBuoc.anchor = GridBagConstraints.CENTER;
            rangBuoc.insets = new Insets(22, 0, 0, 0);
            noiDung.add(thanhNhan, rangBuoc);

            JLabel dongMoTa = new JLabel(I18nReaderUtil.get("app.login.tagline"), SwingConstants.CENTER);
            dongMoTa.setFont(new Font("SansSerif", Font.PLAIN, 15));
            dongMoTa.setForeground(voiDoMo(mauChu, 200));
            rangBuoc.gridy = 2;
            rangBuoc.insets = new Insets(14, 0, 0, 0);
            noiDung.add(dongMoTa, rangBuoc);

            this.add(noiDung);
        }

        private static Color voiDoMo(Color mau, int doMo) {
            return new Color(mau.getRed(), mau.getGreen(), mau.getBlue(), doMo);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D doHoa = (Graphics2D) g.create();
            try {
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
                InputStream luong = BannerDangNhap.class.getClassLoader()
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
    }
}
