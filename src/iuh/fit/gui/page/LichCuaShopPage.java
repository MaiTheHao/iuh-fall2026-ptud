package iuh.fit.gui.page;

import iuh.fit.enumeration.MenuGroup;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Trang lịch làm việc của cửa hàng.
 */
public class LichCuaShopPage extends AbstractPage {

    private static final long serialVersionUID = 1L;

    private static final int SO_NGAY = 7;
    private static final int SO_CA = 3;

    private static final Color MAU_TIEU_DE = new Color(232, 235, 238);
    private static final Color MAU_O_LE = new Color(234, 242, 249);
    private static final Color MAU_VIEN = new Color(195, 202, 210);
    private static final Color MAU_CHU = new Color(35, 85, 135);

    private final JButton btnTuanTruoc = new JButton("‹");
    private final JButton btnTuanSau = new JButton("›");
    private final JButton btnTuanNay = new JButton();

    private final JLabel lblKhoangTuan = new JLabel();
    private final JLabel lblTongPhanCong = new JLabel();
    private final JLabel lblTongNhanVien = new JLabel();

    private final JPanel pnlBangLich = new JPanel(
            new GridLayout(4, 8, 0, 0));

    private final String[] khoaCa = {
        "app.schedule.morning",
        "app.schedule.afternoon",
        "app.schedule.allday"
    };

    private final List<String>[][] nhanVienTheoCa = this.taoMangDuLieu();

    private LocalDate ngayDauTuan;

    public LichCuaShopPage() {
        this.setLayout(new BorderLayout(0, 8));
       
        this.ngayDauTuan = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        nhanVienTheoCa[0][0].add("Nguyễn Thị Mỹ Quốc");
        nhanVienTheoCa[0][2].add("Võ Hoàng Long");
        nhanVienTheoCa[1][1].add("Phạm Nát Bét");
        nhanVienTheoCa[1][3].add("Nguyễn Thị Mỹ Quốc");
        nhanVienTheoCa[2][0].add("Phạm Nát Bét");
        nhanVienTheoCa[2][5].add("Nguyễn Thị Mỹ Quốc");

        this.khoiTaoSuKien();
    }
   
    @Override
    public void render() {
        this.removeAll();
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel lblTieuDe = new JLabel(
                I18nReaderUtil.get("app.schedule.shopSchedule"));
        lblTieuDe.setFont(
                lblTieuDe.getFont().deriveFont(Font.BOLD, 16f));

        JPanel pnlDieuHuong = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 4));

        this.btnTuanTruoc.setToolTipText(
                I18nReaderUtil.get("app.schedule.previousWeek"));
        this.btnTuanSau.setToolTipText(
                I18nReaderUtil.get("app.schedule.nextWeek"));
        this.btnTuanNay.setText(
                I18nReaderUtil.get("app.schedule.thisWeek"));

        pnlDieuHuong.add(this.btnTuanTruoc);
        pnlDieuHuong.add(this.lblKhoangTuan);
        pnlDieuHuong.add(this.btnTuanSau);
        pnlDieuHuong.add(this.btnTuanNay);

        JPanel pnlTren = new JPanel(new BorderLayout());
        pnlTren.add(lblTieuDe, BorderLayout.NORTH);
        pnlTren.add(pnlDieuHuong, BorderLayout.CENTER);

        this.pnlBangLich.setBorder(
                BorderFactory.createLineBorder(MAU_VIEN));

        JPanel pnlThongKe = new JPanel(new FlowLayout(
                FlowLayout.LEFT, 16, 4));
        pnlThongKe.add(this.lblTongPhanCong);
        pnlThongKe.add(this.lblTongNhanVien);

        this.add(pnlTren, BorderLayout.NORTH);
        this.add(this.pnlBangLich, BorderLayout.CENTER);
        this.add(pnlThongKe, BorderLayout.SOUTH);

        this.capNhatBangLich();

        this.revalidate();
        this.repaint();
    }

    private void khoiTaoSuKien() {
        this.btnTuanTruoc.addActionListener(e -> {
            this.ngayDauTuan = this.ngayDauTuan.minusWeeks(1);
            this.capNhatBangLich();
        });

        this.btnTuanSau.addActionListener(e -> {
            this.ngayDauTuan = this.ngayDauTuan.plusWeeks(1);
            this.capNhatBangLich();
        });

        this.btnTuanNay.addActionListener(e -> {
            this.ngayDauTuan = LocalDate.now().with(
                    TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            this.capNhatBangLich();
        });
    }

    private void capNhatBangLich() {
        DateTimeFormatter dinhDangNgay = DateTimeFormatter.ofPattern(
                I18nReaderUtil.get("app.date.format"));

        int soTuan = this.ngayDauTuan.get(
                WeekFields.of(DayOfWeek.MONDAY, 4)
                        .weekOfWeekBasedYear());

        this.lblKhoangTuan.setText(
                I18nReaderUtil.get("app.schedule.week")
                + " " + soTuan
                + " · " + this.ngayDauTuan.format(dinhDangNgay)
                + " – " + this.ngayDauTuan.plusDays(6)
                        .format(dinhDangNgay));

        this.pnlBangLich.removeAll();

        this.themO("app.schedule.shift", true, false);

        for (int ngay = 0; ngay < SO_NGAY; ngay++) {
            LocalDate ngayHienTai = this.ngayDauTuan.plusDays(ngay);

            String khoaThu = "app.schedule.day."
                    + ngayHienTai.getDayOfWeek().name().toLowerCase(
                            Locale.ROOT);

            String tieuDeNgay = I18nReaderUtil.get(khoaThu)
                    + "\n"
                    + ngayHienTai.format(
                            DateTimeFormatter.ofPattern("dd/MM"));

            this.pnlBangLich.add(this.taoO(
                    tieuDeNgay, true, ngay % 2 == 1));
        }

        int tongPhanCong = 0;
        Set<String> danhSachNhanVien = new HashSet<>();

        for (int ca = 0; ca < SO_CA; ca++) {
            this.themO(this.khoaCa[ca], true, false);

            for (int ngay = 0; ngay < SO_NGAY; ngay++) {
                JPanel oLich = this.taoO("", false, ngay % 2 == 1);

                oLich.setLayout(new BorderLayout(0, 4));

                List<String> danhSach = this.nhanVienTheoCa[ca][ngay];

                if (danhSach == null || danhSach.isEmpty()) {
                    JLabel lblTrong = new JLabel(
                            "–", SwingConstants.CENTER);
                    lblTrong.setForeground(Color.GRAY);
                    oLich.add(lblTrong, BorderLayout.NORTH);
                }else {
                    JPanel pnlNhanVien = new JPanel();
                    pnlNhanVien.setOpaque(false);
                    pnlNhanVien.setLayout(new BoxLayout(pnlNhanVien, BoxLayout.Y_AXIS));

                    for (String tenNhanVien : danhSach) {
                        JLabel lblNhanVien = new JLabel(tenNhanVien);
                        lblNhanVien.setForeground(MAU_CHU);
                        lblNhanVien.setOpaque(true);
                        lblNhanVien.setBackground(MAU_O_LE);
                        lblNhanVien.setBorder(
                                BorderFactory.createCompoundBorder(
                                        BorderFactory.createLineBorder(
                                                new Color(80, 145, 210)),
                                        BorderFactory.createEmptyBorder(
                                                4, 6, 4, 6)));

                        lblNhanVien.setAlignmentX(
                                JLabel.LEFT_ALIGNMENT);

                        pnlNhanVien.add(lblNhanVien);
                        pnlNhanVien.add(
                                Box.createVerticalStrut(4));

                        danhSachNhanVien.add(tenNhanVien);
                        tongPhanCong++;
                    }

                    oLich.add(pnlNhanVien, BorderLayout.NORTH);
                }

                this.pnlBangLich.add(oLich);
            }
        }

        this.lblTongPhanCong.setText(
                I18nReaderUtil.get("app.schedule.totalAssignments")
                + ": " + tongPhanCong);

        this.lblTongNhanVien.setText(
                I18nReaderUtil.get("app.schedule.totalEmployees")
                + ": " + danhSachNhanVien.size());

        this.pnlBangLich.revalidate();
        this.pnlBangLich.repaint();
    }

    private void themO(String khoa, boolean tieuDe, boolean le) {
        this.pnlBangLich.add(this.taoO(
                I18nReaderUtil.get(khoa), tieuDe, le));
    }

    private JPanel taoO(
            String noiDung, boolean tieuDe, boolean le) {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(true);
        panel.setBackground(tieuDe
                ? MAU_TIEU_DE
                : (le ? MAU_O_LE : Color.WHITE));

        Border vien = BorderFactory.createLineBorder(MAU_VIEN);
        Border dem = BorderFactory.createEmptyBorder(6, 8, 6, 8);

        panel.setBorder(BorderFactory.createCompoundBorder(vien, dem));

        JLabel label = new JLabel(
                "<html>" + noiDung.replace("\n", "<br>") + "</html>");
        label.setFont(label.getFont().deriveFont(
                tieuDe ? Font.BOLD : Font.PLAIN));

        panel.add(label, BorderLayout.CENTER);
        panel.setMinimumSize(new Dimension(80, 48));

        return panel;
    }

    @SuppressWarnings("unchecked")
    private List<String>[][] taoMangDuLieu() {
        List<String>[][] duLieu =
                (List<String>[][]) new List<?>[SO_CA][SO_NGAY];

        for (int ca = 0; ca < SO_CA; ca++) {
            for (int ngay = 0; ngay < SO_NGAY; ngay++) {
                duLieu[ca][ngay] = new ArrayList<>();
            }            
        }

        return duLieu;
    }
    
    @Override
    public String getTitleKey() {
        return "app.menu.shopSchedule";
    }

    @Override
    public MenuGroup getGroup() {
        return MenuGroup.KE_HOACH;
    }
}
