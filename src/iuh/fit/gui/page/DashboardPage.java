package iuh.fit.gui.page;

import iuh.fit.core.context.AppContext;
import iuh.fit.dto.ThongKeDashboard;
import iuh.fit.entity.NhanVien;
import iuh.fit.entity.PhanCong;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.Theme;
import iuh.fit.gui.component.LucideIcon;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.service.DashboardService;
import iuh.fit.util.I18nReaderUtil;

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
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class DashboardPage extends AbstractPage {

    private static final int RONG_COT_PHAI = 550;
    private static final int KHOANG_CACH = 12;
    private static final int CO_ICON = 22;
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
        this.setBorder(Theme.leDeu(8));
        this.setBackground(Theme.BG);

        NhanVien nhanVien = AppContext.getInstance().getCurrentUser();
        ThongKeDashboard thongKe;

        try {
            thongKe = this.dashboardService.thongKe(nhanVien == null ? null : nhanVien.getMaNhanVien());
        } catch (SQLException ex) {
            ThongBao.loi(this, "app.login.errorSystem");
            thongKe = ThongKeDashboard.rong();
        }

        this.add(this.taoBoCuc(nhanVien, thongKe), BorderLayout.CENTER);
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

    private JPanel taoBoCuc(NhanVien nhanVien, ThongKeDashboard thongKe) {
        JPanel pnlChinh = new JPanel(new BorderLayout(16, 0));
        pnlChinh.setOpaque(false);

        // Lịch chiếm toàn bộ ô bên trái, thông tin nhân viên và chỉ số dồn sang cột phải.
        pnlChinh.add(this.taoLichCuaToi(), BorderLayout.CENTER);
        pnlChinh.add(this.taoThongTin(nhanVien, thongKe), BorderLayout.EAST);
        return pnlChinh;
    }

    private JPanel taoThongTin(NhanVien nhanVien, ThongKeDashboard thongKe) {
        JPanel cot = new JPanel();
        cot.setLayout(new BoxLayout(cot, BoxLayout.Y_AXIS));
        cot.setOpaque(false);
        cot.setPreferredSize(new Dimension(RONG_COT_PHAI, 0));

        cot.add(this.taoTheNhanVien(nhanVien, thongKe.caHomNay()));
        cot.add(Box.createVerticalStrut(KHOANG_CACH));
        cot.add(this.taoLuoiChiSo(thongKe));
        cot.add(Box.createVerticalGlue());
        return cot;
    }

    private JPanel taoLichCuaToi() {
        // Tạo instance riêng vì page gốc đã nằm trong CardLayout của Layout.
        LichCuaToiPage lich = new LichCuaToiPage();
        lich.render();
        lich.setBorder(Theme.leDeu(0));

        // Bọc trong card để lịch phủ kín ô bên trái thay vì nằm rời trên nền.
        JPanel the = Theme.contentPanel(8);
        the.add(lich, BorderLayout.CENTER);
        return the;
    }

    private JPanel taoTheNhanVien(NhanVien nhanVien, Optional<PhanCong> ca) {
        JPanel the = new JPanel(new BorderLayout(14, 8));
        Theme.applyCardStyle(the);
        the.add(this.bocGiua(this.taoAnhDaiDien(nhanVien)), BorderLayout.WEST);
        the.add(this.taoThongTinNhanVien(nhanVien), BorderLayout.CENTER);
        the.add(this.taoDongCa(ca), BorderLayout.SOUTH);
        the.setAlignmentX(LEFT_ALIGNMENT);
        the.setMaximumSize(new Dimension(Integer.MAX_VALUE, the.getPreferredSize().height));
        return the;
    }

    private JPanel taoLuoiChiSo(ThongKeDashboard thongKe) {
        JPanel luoi = new JPanel(new GridLayout(0, 2, KHOANG_CACH, KHOANG_CACH));
        luoi.setOpaque(false);
        luoi.setAlignmentX(LEFT_ALIGNMENT);

        luoi.add(this.taoTheChiSo(BIEU_TUONG_TIEN, "app.dashboard.revenueToday",
                this.dinhDangTien(thongKe.doanhThuHomNay()), null, false));
        luoi.add(this.taoTheChiSo(BIEU_TUONG_PHIEU, "app.dashboard.ticketsInProgress",
                String.valueOf(thongKe.phieuDangXuLy()),
                I18nReaderUtil.get("app.dashboard.ticketsInProgressSub"), false));
        luoi.add(this.taoTheChiSo(BIEU_TUONG_GIAO, "app.dashboard.ticketsAwaitingHandover",
                String.valueOf(thongKe.phieuChoBanGiao()),
                I18nReaderUtil.get("app.dashboard.ticketsAwaitingHandoverSub"), true));
        luoi.add(this.taoTheChiSo(BIEU_TUONG_CANH_BAO, "app.dashboard.complaintsPending",
                String.valueOf(thongKe.khieuNaiChuaXuLy()),
                I18nReaderUtil.get("app.dashboard.complaintsPendingSub"), true));
        luoi.add(this.taoTheChiSo(BIEU_TUONG_HOA_DON, "app.dashboard.invoicesUnpaid",
                String.valueOf(thongKe.hoaDonChuaThanhToan()),
                I18nReaderUtil.get("app.dashboard.invoicesUnpaidSub") + " "
                        + this.dinhDangTien(thongKe.tongHoaDonChuaThanhToan()), true));
        luoi.add(new JPanel() {
            

            {
                this.setOpaque(false);
            }
        });
        // Giữ lưới chỉ số ở chiều cao tự nhiên, phần dư nhường cho khoảng trống bên dưới.
        luoi.setMaximumSize(new Dimension(Integer.MAX_VALUE, luoi.getPreferredSize().height));
        return luoi;
    }

    private JPanel bocGiua(JComponent thanhPhan) {
        JPanel boc = new JPanel(new GridBagLayout());
        boc.setOpaque(false);
        boc.add(thanhPhan);
        return boc;
    }

    private JLabel taoAnhDaiDien(NhanVien nhanVien) {
        JLabel anh = new JLabel(this.layChuDau(nhanVien), SwingConstants.CENTER);
        anh.setPreferredSize(new Dimension(60, 60));
        anh.setOpaque(true);
        anh.setBackground(Theme.palette().ACCENT_SOFT());
        anh.setForeground(Theme.palette().ACCENT());
        anh.setFont(this.fontCo(24f, Font.BOLD));
        return anh;
    }

    private JPanel taoThongTinNhanVien(NhanVien nhanVien) {
        JPanel thongTin = new JPanel();
        thongTin.setLayout(new BoxLayout(thongTin, BoxLayout.Y_AXIS));
        thongTin.setOpaque(false);
        thongTin.add(this.taoNhan(this.layHoTen(nhanVien), 20f, Font.BOLD, Theme.INK));
        thongTin.add(Box.createVerticalStrut(4));
        thongTin.add(this.taoNhan(this.layVaiTro(nhanVien), 14f, Font.PLAIN,
                Theme.palette().INK_SECONDARY()));
        thongTin.add(Box.createVerticalStrut(2));
        thongTin.add(this.taoNhan(this.layMaNhanVien(nhanVien), 14f, Font.PLAIN,
                Theme.palette().INK_MUTED()));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(thongTin, BorderLayout.NORTH);
        return panel;
    }

    private JPanel taoDongCa(Optional<PhanCong> ca) {
        JPanel dong = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        dong.setOpaque(false);
        dong.add(this.taoNhan(I18nReaderUtil.get("app.dashboard.shift") + ":",
                13f, Font.BOLD, Theme.palette().INK_SECONDARY()));
        dong.add(this.taoNhan(this.layNoiDungCa(ca), 13f, Font.BOLD, Theme.INK));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(Theme.vienTren());
        panel.add(dong, BorderLayout.WEST);
        return panel;
    }

    private JPanel taoTheChiSo(String bieuTuong, String keyNhan, String giaTri, String phu,
            boolean phuCanhBao) {
        JPanel the = Theme.contentPanel(14);
        ((BorderLayout) the.getLayout()).setHgap(14);
        the.add(this.bocGiua(this.taoHopBieuTuong(bieuTuong, phuCanhBao)), BorderLayout.WEST);

        JPanel noiDung = new JPanel();
        noiDung.setLayout(new BoxLayout(noiDung, BoxLayout.Y_AXIS));
        noiDung.setOpaque(false);
        noiDung.add(this.taoNhan(I18nReaderUtil.get(keyNhan).toUpperCase(),
                12f, Font.BOLD, Theme.palette().INK_MUTED()));
        noiDung.add(Box.createVerticalStrut(2));
        noiDung.add(this.taoNhan(giaTri, 24f, Font.BOLD, Theme.INK));

        if (phu != null) {
            noiDung.add(this.taoNhan(phu, 12f, Font.PLAIN,
                    phuCanhBao ? Theme.WARNING_TEXT : Theme.palette().INK_MUTED()));
        }

        JPanel boc = new JPanel(new BorderLayout());
        boc.setOpaque(false);
        boc.add(noiDung, BorderLayout.CENTER);
        the.add(boc, BorderLayout.CENTER);
        return the;
    }

    private JLabel taoHopBieuTuong(String bieuTuong, boolean canhBao) {
        Color mauNen = canhBao ? Theme.palette().WARNING_BG() : Theme.palette().ACCENT_SOFT();
        Color mauIcon = canhBao ? Theme.palette().WARNING() : Theme.palette().ACCENT();

        JLabel hop = new JLabel(new LucideIcon(bieuTuong, CO_ICON, mauIcon), SwingConstants.CENTER);
        hop.setPreferredSize(new Dimension(44, 44));
        hop.setOpaque(true);
        hop.setBackground(mauNen);
        return hop;
    }

    private JLabel taoNhan(String text, float coChu, int kieuChu, Color mauChu) {
        JLabel label = new JLabel(text);
        label.setFont(this.fontCo(coChu, kieuChu));
        label.setForeground(mauChu);
        return label;
    }

    private Font fontCo(float coChu, int kieuChu) {
        Font font = UIManager.getFont("Label.font");
        Font coSo = font == null ? new JLabel().getFont() : font;
        return coSo.deriveFont(kieuChu, coChu);
    }

    private String dinhDangTien(double soTien) {
        return DINH_DANG_TIEN.format(soTien) + " " + I18nReaderUtil.get("app.common.currencySymbol");
    }

    private String layHoTen(NhanVien nhanVien) {
        return nhanVien == null || nhanVien.getTenHienThi() == null ? "" : nhanVien.getTenHienThi();
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

}
