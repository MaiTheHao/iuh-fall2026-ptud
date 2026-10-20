package iuh.fit.gui.page;

import iuh.fit.dto.DanhSachVoucherCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.dto.VoucherFormData;
import iuh.fit.entity.Voucher;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.enumeration.TrangThaiKichHoat;
import iuh.fit.gui.component.ManagementFormPanel;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.form.VoucherFormPanel;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.VoucherService;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.NumberParseUtil;
import iuh.fit.util.TextNormalizerUtil;

import javax.swing.BorderFactory;
import javax.swing.JSplitPane;
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
    private VoucherFormPanel formPanel;

    private JTextField boLocMa;
    private ManagementPanel.BoLocKhoang boLocMucGiam;
    private ManagementPanel.BoLocNgay boLocThoiHan;
    private ManagementPanel.BoLocChon boLocTrangThai;

    private int trang = 1;

    public VoucherPage() {
        this.setLayout(new BorderLayout());
    }

    @Override
    public void render() {
        this.removeAll();
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        this.setBackground(Theme.BG);

        ManagementPanel panel = new ManagementPanel();
        panel.setListTitle("app.voucher.danhSachTitle");

        panel.setHeaders(
                I18nReaderUtil.get("app.voucher.ma"),
                I18nReaderUtil.get("app.voucher.tenChuongTrinh"),
                I18nReaderUtil.get("app.voucher.moTa"),
                I18nReaderUtil.get("app.voucher.mucGiam"),
                I18nReaderUtil.get("app.voucher.thoiHanApDung"),
                I18nReaderUtil.get("app.voucher.trangThaiKichHoat")
        );

        panel.addActionColumn("app.common.actions",
                new ThaoTac("trash-2", "app.common.delete", ThaoTac.KieuThaoTac.NGUY_HIEM,
                        row -> this.xoa(panel, row)));

        ManagementFormPanel form =
                new ManagementFormPanel("app.voucher.thongTinTitle");

        this.formPanel = new VoucherFormPanel();
        form.addBlock(this.formPanel);

        this.boLocMa = panel.addTextFilter("app.voucher.ma");
        this.boLocMucGiam = panel.addRangeFilter("app.voucher.mucGiam");
        this.boLocThoiHan = panel.addDateRangeFilter("app.voucher.thoiHanApDung");
        this.boLocTrangThai = panel.addComboBoxFilter(
                "app.voucher.trangThaiKichHoat",
                TrangThaiKichHoat.values());
        panel.setFilterChangeListener(() -> this.taiLai(panel, 1));

        form.getNutThem().addActionListener(e -> this.them(panel));
        form.getNutLuu().addActionListener(e -> this.luu(panel));
        form.getNutXoaTrang().addActionListener(e -> this.lamMoi(panel));
        form.getNutXoa().addActionListener(e -> this.xoa(panel, panel.getSelectedRow()));

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

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                form,
                panel
        );
        split.setDividerLocation(480);
        split.setOpaque(false);
        split.setBorder(null);

        this.add(split, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

    private void taiLai(ManagementPanel panel, int page) {
        try {
            String maVoucher = this.boLocMa.getText();

            Double mucGiamTu = this.docMucGiam(this.boLocMucGiam.tuText());
            Double mucGiamDen = this.docMucGiam(this.boLocMucGiam.denText());

            LocalDate thoiHanTu = this.boLocThoiHan.tuNgay();
            LocalDate thoiHanDen = this.boLocThoiHan.denNgay();

            Object chon = this.boLocTrangThai.giaTri();
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

    private VoucherFormData getFormData() {
        return this.formPanel.layDuLieu();
    }

    private void validateFormData(VoucherFormData data) {
        if (data.mucGiam() == null || !Double.isFinite(data.mucGiam()) || data.mucGiam() < 0) {
            throw new IllegalArgumentException(
                    "app.voucher.loiMucGiam");
        }

        if (data.thoiHanApDung() == null) {
            throw new IllegalArgumentException(
                    "app.voucher.loiThoiHan");
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

        this.formPanel.napDuLieu(this.docDuLieuDong(panel, row));
    }

    private void lamMoi(ManagementPanel panel) {
        this.formPanel.lamMoi();
        panel.getTable().clearSelection();
    }

    private Voucher taoVoucher(VoucherFormData data) {
        return new Voucher(
                data.maVoucher(),
                data.tenChuongTrinh(),
                data.moTa(),
                data.mucGiam(),
                data.thoiHanApDung(),
                data.trangThaiKichHoat()
        );
    }

    private void them(ManagementPanel panel) {
        VoucherFormData data = this.getFormData();
        this.validateFormData(data);

        try {
            this.voucherService.create(this.taoVoucher(data));
            ThongBao.thongTin(this, "app.common.create_succeed");

            this.lamMoi(panel);
            this.taiLai(panel, 1);

        } catch (IllegalArgumentException e) {
            ThongBao.loi(this, e);

        } catch (SQLException e) {
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }

    private void luu(ManagementPanel panel) {
        VoucherFormData data = this.getFormData();
        this.validateFormData(data);

        Voucher voucher = this.taoVoucher(data);

        try {
            if (data.maVoucher() == null) {
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

    private VoucherFormData docDuLieuDong(ManagementPanel panel, int row) {
        String thoiHan = String.valueOf(panel.getModel().getValueAt(row, 4));
        LocalDateTime thoiHanApDung = thoiHan.isBlank()
                ? null
                : LocalDateTime.parse(thoiHan, DINH_DANG_NGAY_GIO);

        String trangThai = TextNormalizerUtil.normalize(
                String.valueOf(panel.getModel().getValueAt(row, 5)));

        return new VoucherFormData(
                String.valueOf(panel.getModel().getValueAt(row, 0)),
                String.valueOf(panel.getModel().getValueAt(row, 1)),
                String.valueOf(panel.getModel().getValueAt(row, 2)),
                NumberParseUtil.toDoubleOrNull(
                        String.valueOf(panel.getModel().getValueAt(row, 3))),
                thoiHanApDung,
                trangThai.equals(TextNormalizerUtil.normalize(
                        TrangThaiKichHoat.DANG_KICH_HOAT.getI18NBusinessName())));
    }

    private void xoa(ManagementPanel panel, int row) {
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
