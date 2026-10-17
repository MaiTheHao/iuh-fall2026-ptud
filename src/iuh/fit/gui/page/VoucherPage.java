package iuh.fit.gui.page;

import com.github.lgooddatepicker.components.DateTimePicker;
import iuh.fit.dto.DanhSachVoucherCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.Voucher;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.enumeration.TrangThaiKichHoat;
import iuh.fit.gui.component.BoChonNgayGio;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.VoucherService;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.TextNormalizerUtil;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VoucherPage extends AbstractPage {

    private static final long serialVersionUID = 1L;

    private static final int SO_DONG_MOI_TRANG = 20;

    private static final DateTimeFormatter DINH_DANG_NGAY_GIO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final VoucherService voucherService = new VoucherService();

    private final JTextField maField = new JTextField(14);
    private final JTextField tenChuongTrinhField = new JTextField(14);
    private final JTextField moTaField = new JTextField(14);
    private final JTextField mucGiamField = new JTextField(14);
    private final DateTimePicker thoiHanPicker = BoChonNgayGio.taoBoChonNgayGio();
    private final JCheckBox trangThaiField = new JCheckBox();

    private int trang = 1;

    public VoucherPage() {
        this.setLayout(new BorderLayout());
    }

    @Override
    public void render() {
        this.removeAll();
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        ManagementPanel panel = new ManagementPanel();

        panel.setHeaders(
                I18nReaderUtil.get("app.voucher.ma"),
                I18nReaderUtil.get("app.voucher.tenChuongTrinh"),
                I18nReaderUtil.get("app.voucher.moTa"),
                I18nReaderUtil.get("app.voucher.mucGiam"),
                I18nReaderUtil.get("app.voucher.thoiHanApDung"),
                I18nReaderUtil.get("app.voucher.trangThaiKichHoat")
        );

        this.maField.setEnabled(false);

        panel.addFormRow("app.voucher.ma", this.maField);
        panel.addFormRow("app.voucher.tenChuongTrinh", this.tenChuongTrinhField);
        panel.addFormRow("app.voucher.moTa", this.moTaField);
        panel.addFormRow("app.voucher.mucGiam", this.mucGiamField);

        JLabel huongDan = new JLabel(
                I18nReaderUtil.get("app.voucher.huongDanMucGiam"));
        huongDan.setForeground(Theme.INK);
        panel.getFormPanel().add(huongDan);

        panel.addFormRow("app.voucher.thoiHanApDung", this.thoiHanPicker);
        panel.addFormRow("app.voucher.trangThaiKichHoat", this.trangThaiField);

        panel.addTextFilter("app.voucher.ma", 3);
        panel.addRangeFilter("app.voucher.mucGiam", 4);
        panel.addDateRangeFilter("app.voucher.thoiHanApDung", 6);
        panel.addComboBoxFilter(
                "app.voucher.trangThaiKichHoat", 7,
                TrangThaiKichHoat.values());
        panel.setFilterChangeListener(() -> this.taiLai(panel, 1));

        panel.getThemButton().addActionListener(e -> this.lamMoi(panel));
        panel.getLuuButton().addActionListener(e -> this.luu(panel));
        panel.getXoaTrangButton().addActionListener(e -> this.lamMoi(panel));
        panel.getXoaButton().addActionListener(e -> this.xoa(panel));

        panel.getClearFilterButton().addActionListener(
                e -> panel.clearFilters());

        panel.getPrevButton().addActionListener(
                e -> this.taiLai(panel, this.trang - 1));

        panel.getNextButton().addActionListener(
                e -> this.taiLai(panel, this.trang + 1));

        panel.getTable().getSelectionModel()
                .addListSelectionListener(e -> {
                    if (!e.getValueIsAdjusting()) {
                        this.chonDong(panel);
                    }
                });

        this.taiLai(panel, 1);

        this.add(panel, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

    private void taiLai(ManagementPanel panel, int page) {
        try {
            String maVoucher = panel.getTextFilter(3);

            Double mucGiamTu = this.docMucGiam(panel.getTextFilter(4));
            Double mucGiamDen = this.docMucGiam(panel.getTextFilter(5));

            LocalDate thoiHanTu = panel.getDateFilterFrom(6);
            LocalDate thoiHanDen = panel.getDateFilterTo(6);

            Object chon = panel.getComboFilterValue(7);
            Boolean trangThai = chon == null
                    ? null
                    : ((TrangThaiKichHoat) chon).isGiaTri();

            DanhSachVoucherCriteria tieuChi =
                    new DanhSachVoucherCriteria(
                            panel.getQuickFilter(),
                            maVoucher,
                            mucGiamTu,
                            mucGiamDen,
                            thoiHanTu,
                            thoiHanDen,
                            trangThai,
                            Math.max(page, 1),
                            SO_DONG_MOI_TRANG
                    );

            PaginatedResult<Voucher> ketQua =
                    this.voucherService.search(tieuChi);

            List<Object[]> rows = new ArrayList<>();

            for (Voucher voucher : ketQua.items()) {
                rows.add(new Object[]{
                        voucher.getMaVoucher(),
                        voucher.getTenChuongTrinh(),
                        voucher.getMoTa() == null
                                ? ""
                                : voucher.getMoTa(),
                        voucher.getMucGiam(),
                        this.dinhDangNgayGio(voucher.getThoiHanApDung()),
                        TrangThaiKichHoat.tu(voucher.isTrangThaiKichHoat())
                                .getI18NBusinessName()
                });
            }

            panel.setRows(rows);

            panel.setCount(
                    ketQua.fromIndex(),
                    ketQua.toIndex(),
                    ketQua.totalItems());

            panel.setPage(
                    ketQua.page(),
                    Math.max(ketQua.totalPages(), 1));

            this.trang = ketQua.page();

        } catch (IllegalArgumentException e) {
            ThongBao.loi(this, e);
            panel.setRows(Collections.emptyList());
            panel.setCount(0, 0, 0);
            panel.setPage(1, 1);
            this.trang = 1;
        } catch (SQLException e) {
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }

    private Double docMucGiam(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            double mucGiam = Double.parseDouble(value.trim());

            if (!Double.isFinite(mucGiam) || mucGiam < 0) {
                throw new NumberFormatException();
            }

            return mucGiam;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "app.voucher.loiMucGiam");
        }
    }

    private String dinhDangNgayGio(LocalDateTime thoiGian) {
        if (thoiGian == null) {
            return "";
        }

        return thoiGian.format(DINH_DANG_NGAY_GIO);
    }

    private void chonDong(ManagementPanel panel) {
        int row = panel.getSelectedRow();

        if (row < 0) {
            return;
        }

        this.maField.setText(
                String.valueOf(panel.getModel().getValueAt(row, 0)));

        this.tenChuongTrinhField.setText(
                String.valueOf(panel.getModel().getValueAt(row, 1)));

        this.moTaField.setText(
                String.valueOf(panel.getModel().getValueAt(row, 2)));

        this.mucGiamField.setText(
                String.valueOf(panel.getModel().getValueAt(row, 3)));

        String thoiHan = String.valueOf(
                panel.getModel().getValueAt(row, 4));

        if (thoiHan.isBlank()) {
            this.thoiHanPicker.clear();
        } else {
            this.thoiHanPicker.setDateTimeStrict(
                    LocalDateTime.parse(thoiHan, DINH_DANG_NGAY_GIO));
        }

        String trangThai = TextNormalizerUtil.normalize(
                String.valueOf(panel.getModel().getValueAt(row, 5)));

        this.trangThaiField.setSelected(
                trangThai.equals(TextNormalizerUtil.normalize(
                        TrangThaiKichHoat.DANG_KICH_HOAT
                                .getI18NBusinessName())));
    }

    private void lamMoi(ManagementPanel panel) {
        this.maField.setText("");
        this.tenChuongTrinhField.setText("");
        this.moTaField.setText("");
        this.mucGiamField.setText("");
        this.thoiHanPicker.clear();
        this.trangThaiField.setSelected(true);

        panel.getTable().clearSelection();
        this.tenChuongTrinhField.requestFocusInWindow();
    }

    private void luu(ManagementPanel panel) {
        String ma = this.maField.getText().isBlank()
                ? null
                : this.maField.getText().trim();

        try {
            String tenChuongTrinh =
                    this.tenChuongTrinhField.getText().trim();

            String moTa = this.moTaField.getText().trim();

            Double mucGiam = this.docMucGiam(
                    this.mucGiamField.getText());

            LocalDateTime thoiHan = this.thoiHanPicker.getDateTimeStrict();

            if (thoiHan == null) {
                throw new IllegalArgumentException(
                        "app.voucher.loiThoiHan");
            }

            Voucher voucher = new Voucher(
                    ma,
                    tenChuongTrinh,
                    moTa.isEmpty() ? null : moTa,
                    mucGiam,
                    thoiHan,
                    this.trangThaiField.isSelected()
            );

            if (ma == null) {
                this.voucherService.create(voucher);
                ThongBao.thongTin(this, "app.common.create_succeed");
            } else {
                this.voucherService.update(voucher);
                ThongBao.thongTin(this, "app.common.update_succeed");
            }

            this.lamMoi(panel);
            this.taiLai(panel, 1);

        } catch (IllegalArgumentException e) {
            ThongBao.loi(this, e);

        } catch (SQLException e) {
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }

    private void xoa(ManagementPanel panel) {
        int row = panel.getSelectedRow();

        try {
            if (row < 0) {
                throw new IllegalArgumentException(
                        "app.voucher.chuaChonXoa");
            }

            String maVoucher = String.valueOf(
                    panel.getModel().getValueAt(row, 0));

            if (!ThongBao.xacNhan(
                    this,
                    "app.voucher.xacNhanXoa",
                    "app.voucher.tieuDeXoa")) {
                return;
            }

            this.voucherService.delete(maVoucher);

            ThongBao.thongTin(this, "app.common.delete_succeed");

            this.lamMoi(panel);
            this.taiLai(panel, this.trang);

        } catch (IllegalArgumentException e) {
            ThongBao.loi(this, e);

        } catch (SQLException e) {
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }

    @Override
    public String getTitleKey() {
        return "app.menu.vouchers";
    }

    @Override
    public MenuGroup getGroup() {
        return MenuGroup.TO_CHUC;
    }
}
