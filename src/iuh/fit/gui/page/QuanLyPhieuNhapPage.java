package iuh.fit.gui.page;

import iuh.fit.dto.BaseFormDialogConfig;
import iuh.fit.dto.DanhSachCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.PhieuNhapPhuTung;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.component.BaseFormDialog;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.form.PhieuNhapChiTietFormPanel;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.PhieuNhapPhuTungService;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.NumberParseUtil;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class QuanLyPhieuNhapPage extends AbstractPage {

    /** Dialog xem chi tiết phiếu nhập: 5 trường chỉ đọc. */
    private static final int CHIEU_CAO_CHI_TIET = 500;

    private final PhieuNhapPhuTungService phieuNhapService = new PhieuNhapPhuTungService();
    private final DecimalFormat currencyFormat = new DecimalFormat("#,##0");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(I18nReaderUtil.get("app.date.format") + " HH:mm");
    private int trang = 1;
    private ManagementPanel managementPanel;

    private JTextField boLocMaPhieuNhap;
    private JTextField boLocMoTa;
    private JTextField boLocThuKho;
    private ManagementPanel.BoLocNgay boLocNgayTao;
    private ManagementPanel.BoLocKhoang boLocTongChiPhi;

    public QuanLyPhieuNhapPage() {
        this.setLayout(new BorderLayout());
    }

    @Override
    public void render() {
        this.removeAll();
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        this.setBackground(Theme.BG);

        ManagementPanel panel = new ManagementPanel();
        this.managementPanel = panel;
        panel.setListTitle("app.phieuNhap.danhSachTitle");

        panel.setHeaders(
                "#",
                I18nReaderUtil.get("app.phieuNhap.maPhieuNhap"),
                I18nReaderUtil.get("app.phieuNhap.moTa"),
                I18nReaderUtil.get("app.phieuNhap.thuKho"),
                I18nReaderUtil.get("app.phieuNhap.ngayTao"),
                I18nReaderUtil.get("app.phieuNhap.tongChiPhi")
        );

        panel.addActionColumn("app.common.actions",
                new ThaoTac("eye", "app.common.view", ThaoTac.KieuThaoTac.THUONG,
                        row -> this.xemChiTiet(row)),
                new ThaoTac("trash-2", "app.common.delete", ThaoTac.KieuThaoTac.NGUY_HIEM,
                        row -> this.xoaTheoDong(panel, row)));

        panel.setAddAction("app.phieuNhap.taoPhieuMoi", () -> this.goTo(LapPhieuNhapPage.class));
        panel.setRowDoubleClickListener(this::xemChiTiet);

        this.boLocMaPhieuNhap = panel.addTextFilter("app.phieuNhap.maPhieuNhap");
        this.boLocMoTa = panel.addTextFilter("app.phieuNhap.moTa");
        this.boLocThuKho = panel.addTextFilter("app.phieuNhap.thuKho");
        this.boLocNgayTao = panel.addDateRangeFilter("app.phieuNhap.ngayTao");
        this.boLocTongChiPhi = panel.addRangeFilter("app.phieuNhap.tongChiPhi");

        panel.setFilterChangeListener(() -> this.taiLai(panel, 1));
        panel.getClearFilterButton().addActionListener(e -> panel.clearFilters());
        panel.getPrevButton().addActionListener(e -> this.taiLai(panel, this.trang - 1));
        panel.getNextButton().addActionListener(e -> this.taiLai(panel, this.trang + 1));

        JTable table = panel.getTable();
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(320);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(130);
        table.getColumnModel().getColumn(5).setPreferredWidth(160);
        table.setRowHeight(42);

        this.taiLai(panel, 1);

        this.add(panel, BorderLayout.CENTER);

        this.revalidate();
        this.repaint();
    }

    private void taiLai(ManagementPanel panel, int page) {
        try {
            DanhSachCriteria criteria = new DanhSachCriteria() {
                @Override
                public int page() {
                    return page;
                }

                @Override
                public int pageSize() {
                    return 20;
                }
            };

            Double minCost = this.parseCost(this.boLocTongChiPhi.tuText());
            Double maxCost = this.parseCost(this.boLocTongChiPhi.denText());

            if ((minCost != null && minCost.isNaN()) || (maxCost != null && maxCost.isNaN())) {
                ThongBao.canhBao(this, "app.phieuNhap.loiKhoangChiPhi");
                return;
            }

            if (minCost != null && maxCost != null && minCost > maxCost) {
                return;
            }

            PaginatedResult<PhieuNhapPhuTung> res = this.phieuNhapService.search(
                    criteria,
                    panel.getQuickFilter(),
                    this.boLocMaPhieuNhap.getText(),
                    this.boLocThuKho.getText(),
                    this.boLocMoTa.getText(),
                    this.boLocNgayTao.tuNgay(),
                    this.boLocNgayTao.denNgay(),
                    minCost,
                    maxCost
            );

            List<Object[]> rows = new ArrayList<>(res.items().size());
            int stt = res.fromIndex();

            for (PhieuNhapPhuTung item : res.items()) {
                rows.add(new Object[]{
                        stt++,
                        item.getMaPhieuNhap(),
                        item.getMoTa(),
                        item.getMaNhanVien(),
                        item.getNgayTao() != null ? item.getNgayTao().format(this.dateFormatter) : "",
                        this.currencyFormat.format(this.phieuNhapService.tinhTongChiPhi(item))
                });
            }

            panel.setRows(rows);
            panel.setCount(res.fromIndex(), res.toIndex(), res.totalItems());
            panel.setPage(res.page(), Math.max(res.totalPages(), 1));
            this.trang = res.page();
        } catch (SQLException e) {
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }

    private Double parseCost(String val) {
        if (val == null || val.isBlank()) {
            return null;
        }
        if (!val.trim().matches("\\d+")) {
            return Double.NaN;
        }
        return Double.valueOf(val.trim());
    }

    private void xemChiTiet(int row) {
        ManagementPanel panel = this.managementPanel;
        if (row < 0 || row >= panel.getModel().getRowCount()) {
            return;
        }

        PhieuNhapChiTietFormPanel form = new PhieuNhapChiTietFormPanel();
        form.napDuLieu(
                NumberParseUtil.textOf(panel.getModel().getValueAt(row, 1)),
                NumberParseUtil.textOf(panel.getModel().getValueAt(row, 2)),
                NumberParseUtil.textOf(panel.getModel().getValueAt(row, 3)),
                NumberParseUtil.textOf(panel.getModel().getValueAt(row, 4)),
                NumberParseUtil.textOf(panel.getModel().getValueAt(row, 5)));

        BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
                SwingUtilities.getWindowAncestor(this),
                "app.phieuNhap.thongTinTitle",
                form,
                null,
                CHIEU_CAO_CHI_TIET,
                true));
        dialog.showAndGet();
    }

    private void xoaTheoDong(ManagementPanel panel, int row) {
        if (row < 0 || row >= panel.getModel().getRowCount()) {
            return;
        }
        this.thucHienXoa(panel, NumberParseUtil.textOf(panel.getModel().getValueAt(row, 1)));
    }

    private void thucHienXoa(ManagementPanel panel, String maPhieu) {
        if (!ThongBao.xacNhan(this, "app.phieuNhap.xacNhanXoa", "app.phieuNhap.tieuDeXoa")) {
            return;
        }
        try {
            this.phieuNhapService.delete(maPhieu);
            ThongBao.thongTin(this, "app.common.delete_succeed");
            this.taiLai(panel, this.trang);
        } catch (IllegalArgumentException e) {
            ThongBao.loi(this, e);
        } catch (SQLException e) {
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }

    @Override
    public String getTitleKey() {
        return "app.menu.partsReceiptManagement";
    }

    @Override
    public MenuGroup getGroup() {
        return MenuGroup.NHAP_PHU_TUNG;
    }
}