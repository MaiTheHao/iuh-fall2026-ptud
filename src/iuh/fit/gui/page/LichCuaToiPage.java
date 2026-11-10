package iuh.fit.gui.page;

import iuh.fit.core.context.AppContext;
import iuh.fit.entity.PhanCong;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.Theme;
import iuh.fit.gui.component.ThanhDieuHuongTuan;
import iuh.fit.service.PhanCongService;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class LichCuaToiPage extends AbstractPage {

    

    private static final int SO_NGAY = 7;
    private static final int SO_CA = 3;

    private final ThanhDieuHuongTuan thanhDieuHuong = new ThanhDieuHuongTuan();

    private final JLabel lblTongCa = new JLabel();

    private final JPanel pnlBangLich = new JPanel(new GridLayout(4, 8, 0, 0));

    private final String[] khoaCa = {
        "app.schedule.morning",
        "app.schedule.afternoon",
        "app.schedule.allday"
    };
    
    private final PhanCongService phanCongService = new PhanCongService();

    private LocalDate ngayDauTuan;
    public LichCuaToiPage() {
        this.setLayout(new BorderLayout(0, 8));

        Locale.getDefault();
        this.ngayDauTuan = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        this.thanhDieuHuong.datNgayDauTuan(this.ngayDauTuan);

        this.khoiTaoSuKien();
    }

    @Override
    public void render() {
        this.removeAll();
        this.thanhDieuHuong.capNhatNgonNgu();
        this.setBorder(Theme.leDeu(8));

        JPanel pnlTieuDe = new JPanel(new BorderLayout());
        JLabel lblTieuDe = new JLabel(
                I18nReaderUtil.get("app.schedule.mySchedule"));
        lblTieuDe.setFont(lblTieuDe.getFont().deriveFont(Font.BOLD, 16));
        pnlTieuDe.add(lblTieuDe, BorderLayout.WEST);

        JPanel pnlTren = new JPanel(new BorderLayout());
        pnlTren.add(pnlTieuDe, BorderLayout.NORTH);
        pnlTren.add(this.thanhDieuHuong, BorderLayout.CENTER);

        this.pnlBangLich.setBorder(
                Theme.vienMau(Theme.LICH_VIEN));

        JPanel pnlThongKe = new JPanel(new BorderLayout());
        JLabel lblGioLam = new JLabel(
                I18nReaderUtil.get("app.schedule.morningHours")
                + " 07:30 – 11:30    "
                + I18nReaderUtil.get("app.schedule.afternoonHours")
                + " 13:00 – 17:00    "
                + I18nReaderUtil.get("app.schedule.alldayHours")
                + " 07:30 – 17:00");
        pnlThongKe.add(lblGioLam, BorderLayout.WEST);
        pnlThongKe.add(this.lblTongCa, BorderLayout.EAST);

        this.add(pnlTren, BorderLayout.NORTH);
        this.add(this.pnlBangLich, BorderLayout.CENTER);
        this.add(pnlThongKe, BorderLayout.SOUTH);

        this.capNhatBangLich();
        this.revalidate();
        this.repaint();
    }

    private void khoiTaoSuKien() {
        this.thanhDieuHuong.datXuLyDoiTuan(ngay -> { this.ngayDauTuan = ngay; this.capNhatBangLich(); });
    }

    private void capNhatBangLich() {
        this.pnlBangLich.removeAll();

        String[][] congViec = new String[SO_CA][SO_NGAY];
        int tongCa = 0;

        try {
            if (AppContext.getInstance().getCurrentUser() == null) {
                throw new IllegalArgumentException(
                        "app.schedule.employeeRequired");
            }

            String maNhanVien = AppContext.getInstance()
                    .getCurrentUser().getMaNhanVien();

            List<PhanCong> danhSach = this.phanCongService.getLichCuaNhanVien(
                    maNhanVien, this.ngayDauTuan);

            for (PhanCong phanCong : danhSach) {
                LocalDate ngayLam = LocalDate.of(
                        phanCong.getNam(),
                        phanCong.getThang(),
                        phanCong.getNgay());

                int viTriNgay = (int) java.time.temporal.ChronoUnit.DAYS.between(
                        this.ngayDauTuan, ngayLam);

                int viTriCa = switch (phanCong.getCaLamViec()) {
                    case CA_SANG -> 0;
                    case CA_CHIEU -> 1;
                    case CA_NGAY -> 2;
                };

                String ghiChu = phanCong.getGhiChu();

                congViec[viTriCa][viTriNgay] =
                        ghiChu == null || ghiChu.isBlank() ? "✓" : ghiChu;

                tongCa++;
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    I18nReaderUtil.get(ex.getMessage()),
                    I18nReaderUtil.get("app.title"),
                    JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    I18nReaderUtil.get("app.common.loiKhongXacDinh"),
                    I18nReaderUtil.get("app.title"),
                    JOptionPane.ERROR_MESSAGE);
        }

        this.themO("app.schedule.shift", true, false);

        for (int ngay = 0; ngay < SO_NGAY; ngay++) {
            LocalDate ngayHienTai = this.ngayDauTuan.plusDays(ngay);

            String khoaThu = "app.schedule.day."
                    + ngayHienTai.getDayOfWeek().name().toLowerCase(Locale.ROOT);

            JPanel oNgay = this.taoO(
                    I18nReaderUtil.get(khoaThu)
                    + "\n" + ngayHienTai.format(
                            DateTimeFormatter.ofPattern("dd/MM")),
                    true,
                    ngay % 2 == 1);

            this.pnlBangLich.add(oNgay);
        }

        for (int ca = 0; ca < SO_CA; ca++) {
            this.themO(this.khoaCa[ca], false, false);

            for (int ngay = 0; ngay < SO_NGAY; ngay++) {
                String noiDung = congViec[ca][ngay];

                JPanel oLich = this.taoO("", false, ngay % 2 == 1);
                oLich.setLayout(new BorderLayout(0, 4));

                if (noiDung == null || noiDung.isBlank()) {
                    JLabel lblTrong = new JLabel("-", SwingConstants.CENTER);
                    lblTrong.setForeground(Color.GRAY);
                    oLich.add(lblTrong, BorderLayout.NORTH);
                } else {
                    JLabel lblCongViec = new JLabel(noiDung);
                    lblCongViec.setForeground(Theme.LICH_CHU);
                    lblCongViec.setOpaque(true);
                    lblCongViec.setBackground(Theme.LICH_O_LE);
                    lblCongViec.setBorder(Theme.khungVien(Theme.LICH_HOM_NAY, new Insets(4, 6, 4, 6)));

                    oLich.add(lblCongViec, BorderLayout.NORTH);
                }

                this.pnlBangLich.add(oLich);
            }
        }

        this.lblTongCa.setText(
                I18nReaderUtil.get("app.schedule.totalShifts")
                + ": " + tongCa);

        this.pnlBangLich.revalidate();
        this.pnlBangLich.repaint();
    }

    private void themO(String khoa, boolean tieuDe, boolean le) {
        this.pnlBangLich.add(this.taoO(I18nReaderUtil.get(khoa), tieuDe, le));
    }

    private JPanel taoO(String noiDung, boolean tieuDe, boolean le) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(true);
        panel.setBackground(tieuDe ? Theme.LICH_TIEU_DE : (le ? Theme.LICH_O_LE : Color.WHITE));

        panel.setBorder(Theme.khungVien(Theme.LICH_VIEN, new Insets(6, 8, 6, 8)));

        JLabel label = new JLabel(
                "<html>" + noiDung.replace("\n", "<br>") + "</html>");
        label.setFont(label.getFont().deriveFont(
                tieuDe ? Font.BOLD : Font.PLAIN));
        panel.add(label, BorderLayout.CENTER);
        panel.setMinimumSize(new Dimension(80, 48));

        return panel;
    }
    
    @Override
    public String getTitleKey() {
        return "app.schedule.mySchedule";
    }

    @Override
    public MenuGroup getGroup() {
        return MenuGroup.KE_HOACH;
    }
}
