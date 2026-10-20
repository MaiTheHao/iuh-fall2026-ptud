package iuh.fit.gui.page;

import iuh.fit.core.context.AppContext;
import iuh.fit.dto.ThongKeDashboard;
import iuh.fit.entity.NhanVien;
import iuh.fit.entity.PhanCong;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.component.LucideIcon;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.DashboardService;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Trang chủ: thông tin nhân viên, ca làm việc và các chỉ số nhanh trong ngày.
 */
public class DashboardPage extends AbstractPage {

    private static final int RONG_THE = 800;
    private static final DecimalFormat DINH_DANG_TIEN = new DecimalFormat("#,##0");

    private static final String BIEU_TUONG_TIEN = "banknote";
    private static final String BIEU_TUONG_PHIEU = "clipboard-list";
    private static final String BIEU_TUONG_GIAO = "truck";
    private static final String BIEU_TUONG_CANH_BAO = "triangle-alert";
    private static final String BIEU_TUONG_HOA_DON = "receipt";

    private final DashboardService dashboardService = new DashboardService();

    public DashboardPage() {
        this.setLayout(new BorderLayout());
    }

    @Override
    public void render() {
        this.removeAll();
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        this.setBackground(Theme.BG);

        NhanVien nhanVien = AppContext.getInstance().getCurrentUser();
        ThongKeDashboard thongKe;

        try {
            thongKe = this.dashboardService.thongKe(nhanVien == null ? null : nhanVien.getMaNhanVien());
        } catch (SQLException ex) {
            ThongBao.loi(this, "app.login.errorSystem");
            thongKe = ThongKeDashboard.rong();
        }

        this.add(new KhungCanPhai(this.taoThe(nhanVien, thongKe)), BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

    @Override
    public String getTitleKey() {
        return "app.menu.home";
    }

    @Override
    public MenuGroup getGroup() {
        return MenuGroup.TRANG_CHU;
    }

    JPanel taoThe(NhanVien nhanVien, ThongKeDashboard thongKe) {
        JPanel the = new JPanel(new GridBagLayout());
        the.setBackground(Theme.PANEL);
        the.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                BorderFactory.createEmptyBorder(16, 12, 12, 12)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        the.add(this.taoDauTrang(nhanVien), gbc);

        gbc.gridy++;
        gbc.insets = new Insets(8, 0, 0, 0);
        the.add(this.taoDongCa(thongKe.caHomNay()), gbc);

        gbc.insets = new Insets(4, 0, 0, 0);
        gbc.gridy++;
        the.add(this.taoDongKpi(BIEU_TUONG_TIEN, "app.dashboard.revenueToday",
                this.dinhDangTien(thongKe.doanhThuHomNay()), null, false), gbc);

        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.gridy++;
        the.add(this.taoDongKpi(BIEU_TUONG_PHIEU, "app.dashboard.ticketsInProgress",
                String.valueOf(thongKe.phieuDangXuLy()),
                I18nReaderUtil.get("app.dashboard.ticketsInProgressSub"), false), gbc);

        gbc.gridy++;
        the.add(this.taoDongKpi(BIEU_TUONG_GIAO, "app.dashboard.ticketsAwaitingHandover",
                String.valueOf(thongKe.phieuChoBanGiao()),
                I18nReaderUtil.get("app.dashboard.ticketsAwaitingHandoverSub"), true), gbc);

        gbc.gridy++;
        the.add(this.taoDongKpi(BIEU_TUONG_CANH_BAO, "app.dashboard.complaintsPending",
                String.valueOf(thongKe.khieuNaiChuaXuLy()),
                I18nReaderUtil.get("app.dashboard.complaintsPendingSub"), true), gbc);

        gbc.gridy++;
        the.add(this.taoDongKpi(BIEU_TUONG_HOA_DON, "app.dashboard.invoicesUnpaid",
                String.valueOf(thongKe.hoaDonChuaThanhToan()),
                I18nReaderUtil.get("app.dashboard.invoicesUnpaidSub") + " "
                        + this.dinhDangTien(thongKe.tongHoaDonChuaThanhToan()), true), gbc);

        gbc.gridy++;
        gbc.weighty = 1;
        the.add(Box.createGlue(), gbc);

        return the;
    }

    private JPanel taoDauTrang(NhanVien nhanVien) {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setOpaque(false);
        panel.add(this.taoAnhDaiDien(nhanVien), BorderLayout.WEST);
        panel.add(this.taoThongTinNhanVien(nhanVien), BorderLayout.CENTER);
        return panel;
    }

    private JLabel taoAnhDaiDien(NhanVien nhanVien) {
        JLabel anh = new JLabel(this.layChuDau(nhanVien), SwingConstants.CENTER);
        anh.setPreferredSize(new Dimension(64, 84));
        anh.setOpaque(true);
        anh.setBackground(Theme.palette().PANEL_ALT());
        anh.setForeground(Theme.palette().INK_SECONDARY());
        anh.setFont(this.fontChu(24f, Font.BOLD));
        anh.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        return anh;
    }

    private JPanel taoThongTinNhanVien(NhanVien nhanVien) {
        JPanel thongTin = new JPanel();
        thongTin.setLayout(new BoxLayout(thongTin, BoxLayout.Y_AXIS));
        thongTin.setOpaque(false);
        thongTin.add(this.taoNhan(this.layHoTen(nhanVien), 20f, Font.BOLD, Theme.INK, false));
        thongTin.add(Box.createVerticalStrut(4));
        thongTin.add(this.taoNhan(this.layVaiTro(nhanVien), 14f, Font.PLAIN, Theme.palette().INK_SECONDARY(), false));
        thongTin.add(Box.createVerticalStrut(2));
        thongTin.add(this.taoNhan(this.layMaNhanVien(nhanVien), 14f, Font.PLAIN, Theme.palette().INK_SECONDARY(), true));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(thongTin, BorderLayout.NORTH);
        return panel;
    }

    private JPanel taoDongCa(Optional<PhanCong> ca) {
        JPanel dong = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        dong.setOpaque(false);
        dong.add(this.taoNhan(I18nReaderUtil.get("app.dashboard.shift") + ":",
                14f, Font.BOLD, Theme.palette().INK_SECONDARY(), false));
        dong.add(this.taoNhan(this.layNoiDungCa(ca), 14f, Font.BOLD, Theme.INK, true));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(8, 0, 0, 0)));
        panel.add(dong, BorderLayout.WEST);
        return panel;
    }

    private JPanel taoDongKpi(String bieuTuong, String keyNhan, String giaTri, String phu, boolean phuCanhBao) {
        JPanel noiDung = new JPanel();
        noiDung.setLayout(new BoxLayout(noiDung, BoxLayout.Y_AXIS));
        noiDung.setOpaque(false);
        noiDung.add(this.taoNhan(I18nReaderUtil.get(keyNhan).toUpperCase(),
                12f, Font.BOLD, Theme.palette().INK_MUTED(), false));
        noiDung.add(this.taoNhan(giaTri, 20f, Font.BOLD, Theme.INK, true));

        if (phu != null) {
            noiDung.add(this.taoNhan(phu, 12f, Font.PLAIN,
                    phuCanhBao ? Theme.WARNING_TEXT : Theme.palette().INK_MUTED(), false));
        }

        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(6, 0, 6, 0)));
        panel.add(this.taoHopBieuTuong(bieuTuong), BorderLayout.WEST);
        panel.add(noiDung, BorderLayout.CENTER);
        return panel;
    }

    private JPanel taoHopBieuTuong(String bieuTuong) {
        JLabel hop = new JLabel(
                new LucideIcon(bieuTuong, 24, Theme.palette().INK_SECONDARY()), SwingConstants.CENTER);
        hop.setPreferredSize(new Dimension(44, 44));
        hop.setOpaque(true);
        hop.setBackground(Theme.palette().PANEL_ALT());
        hop.setBorder(BorderFactory.createLineBorder(Theme.BORDER));

        JPanel boc = new JPanel(new GridBagLayout());
        boc.setOpaque(false);
        boc.add(hop);
        return boc;
    }

    private JLabel taoNhan(String text, float coChu, int kieuChu, Color mauChu, boolean coDinh) {
        JLabel label = new JLabel(text);
        label.setFont(coDinh ? this.fontMono(coChu, kieuChu) : this.fontChu(coChu, kieuChu));
        label.setForeground(mauChu);
        return label;
    }

    private Font fontChu(float coChu, int kieuChu) {
        Font font = UIManager.getFont("Label.font");
        return font == null ? new JLabel().getFont().deriveFont(kieuChu, coChu)
                : font.deriveFont(kieuChu, coChu);
    }

    private Font fontMono(float coChu, int kieuChu) {
        return new Font(Font.MONOSPACED, kieuChu, Math.round(coChu));
    }

    private String dinhDangTien(double soTien) {
        return DINH_DANG_TIEN.format(soTien) + " " + I18nReaderUtil.get("app.common.currencySymbol");
    }

    private String layHoTen(NhanVien nhanVien) {
        return nhanVien == null || nhanVien.getHoTen() == null ? "" : nhanVien.getHoTen();
    }

    private String layVaiTro(NhanVien nhanVien) {
        return nhanVien == null || nhanVien.getVaiTro() == null
                ? ""
                : nhanVien.getVaiTro().getI18NBusinessName();
    }

    private String layMaNhanVien(NhanVien nhanVien) {
        return nhanVien == null || nhanVien.getMaNhanVien() == null ? "" : nhanVien.getMaNhanVien();
    }

    private String layChuDau(NhanVien nhanVien) {
        String hoTen = this.layHoTen(nhanVien).trim();
        return hoTen.isEmpty() ? "?" : hoTen.substring(0, 1).toUpperCase();
    }

    private String layNoiDungCa(Optional<PhanCong> ca) {
        if (ca.isEmpty() || ca.get().getCaLamViec() == null) {
            return I18nReaderUtil.get("app.dashboard.noShift");
        }

        PhanCong phanCong = ca.get();
        LocalDate ngay = LocalDate.of(phanCong.getNam(), phanCong.getThang(), phanCong.getNgay());
        DateTimeFormatter dinhDang = DateTimeFormatter.ofPattern(I18nReaderUtil.get("app.date.format"));

        return phanCong.getCaLamViec().getI18NBusinessName() + " | " + ngay.format(dinhDang);
    }

    private static final class KhungCanPhai extends JPanel {

        private final JComponent the;

        private KhungCanPhai(JComponent the) {
            this.the = the;
            this.setOpaque(false);
            this.setLayout(null);
            this.add(the);
        }

        @Override
        public void doLayout() {
            Insets insets = this.getInsets();
            int rong = Math.min(RONG_THE, this.getWidth() - insets.left - insets.right);

            this.the.setBounds(
                    this.getWidth() - insets.right - rong,
                    insets.top,
                    rong,
                    this.getHeight() - insets.top - insets.bottom);
        }
    }
}
