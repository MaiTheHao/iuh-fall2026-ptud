package iuh.fit.gui.page;

import iuh.fit.dto.DanhSachCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.PhieuNhapPhuTung;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.component.ManagementFormPanel;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.PhieuNhapPhuTungService;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.NumberParseUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class QuanLyPhieuNhapPage extends AbstractPage {

    private final PhieuNhapPhuTungService phieuNhapService = new PhieuNhapPhuTungService();
    private final DecimalFormat currencyFormat = new DecimalFormat("#,##0");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(I18nReaderUtil.get("app.date.format") + " HH:mm");
    private int trang = 1;

    private final JTextField maPhieuNhapField = new JTextField();
    private final JTextField moTaField = new JTextField();
    private final JTextField thuKhoField = new JTextField();
    private final JTextField ngayTaoField = new JTextField();
    private final JTextField tongChiPhiField = new JTextField();

    public QuanLyPhieuNhapPage() {
        this.setLayout(new BorderLayout());
    }

    @Override
    public void render() {
        this.removeAll();
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        this.setBackground(Theme.BG);

        ManagementPanel panel = new ManagementPanel();
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
                new ThaoTac("trash-2", "app.common.delete", ThaoTac.KieuThaoTac.NGUY_HIEM,
                        row -> this.xoaTheoDong(panel, row)));

        panel.addTextFilter("app.phieuNhap.maPhieuNhap", 1);
        panel.addTextFilter("app.phieuNhap.moTa", 2);
        panel.addTextFilter("app.phieuNhap.thuKho", 3);
        panel.addDateRangeFilter("app.phieuNhap.ngayTao", 4);
        panel.addRangeFilter("app.phieuNhap.tongChiPhi", 5);

        ManagementFormPanel form = new ManagementFormPanel("app.phieuNhap.thongTinTitle");

        Theme.applyReadOnly(this.maPhieuNhapField);
        Theme.applyReadOnly(this.moTaField);
        Theme.applyReadOnly(this.thuKhoField);
        Theme.applyReadOnly(this.ngayTaoField);
        Theme.applyReadOnly(this.tongChiPhiField);

        form.addField("app.phieuNhap.maPhieuNhap", this.maPhieuNhapField);
        form.addField("app.phieuNhap.moTa", this.moTaField);
        form.addField("app.phieuNhap.thuKho", this.thuKhoField);
        form.addField("app.phieuNhap.ngayTao", this.ngayTaoField);
        form.addField("app.phieuNhap.tongChiPhi", this.tongChiPhiField);

        JButton taoMoi = new JButton(I18nReaderUtil.get("app.phieuNhap.taoPhieuMoi"));
        Theme.applyPrimary(taoMoi);
        taoMoi.addActionListener(e -> this.goTo(LapPhieuNhapPage.class));

        JButton xoa = new JButton(I18nReaderUtil.get("app.common.delete"));
        Theme.applyDanger(xoa);
        xoa.addActionListener(e -> this.xoaTheoDongChon(panel));

        form.thayNut(taoMoi, xoa);

        panel.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }

            int row = panel.getSelectedRow();

            if (row < 0) {
                return;
            }

            this.maPhieuNhapField.setText(NumberParseUtil.textOf(panel.getModel().getValueAt(row, 1)));
            this.moTaField.setText(NumberParseUtil.textOf(panel.getModel().getValueAt(row, 2)));
            this.thuKhoField.setText(NumberParseUtil.textOf(panel.getModel().getValueAt(row, 3)));
            this.ngayTaoField.setText(NumberParseUtil.textOf(panel.getModel().getValueAt(row, 4)));
            this.tongChiPhiField.setText(NumberParseUtil.textOf(panel.getModel().getValueAt(row, 5)));
        });

        panel.setFilterChangeListener(() -> this.taiLai(panel, 1));
        panel.getClearFilterButton().addActionListener(e -> panel.clearFilters());
        panel.getPrevButton().addActionListener(e -> this.taiLai(panel, this.trang - 1));
        panel.getNextButton().addActionListener(e -> this.taiLai(panel, this.trang + 1));

        JTable table = panel.getTable();
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setRowHeight(34);

        this.taiLai(panel, 1);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, form, panel);
        split.setDividerLocation(380);
        split.setOpaque(false);
        split.setBorder(null);
        this.add(split, BorderLayout.CENTER);

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

            Double minCost = this.parseCost(panel.getTextFilter(5));
            Double maxCost = this.parseCost(panel.getTextFilter(6));

            if ((minCost != null && minCost.isNaN()) || (maxCost != null && maxCost.isNaN())) {
                return;
            }

            if (minCost != null && maxCost != null && minCost > maxCost) {
                return;
            }

            PaginatedResult<PhieuNhapPhuTung> res = this.phieuNhapService.search(
                    criteria,
                    panel.getQuickFilter(),
                    panel.getTextFilter(1),
                    panel.getTextFilter(3),
                    panel.getTextFilter(2),
                    panel.getDateFilterFrom(4),
                    panel.getDateFilterTo(4),
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

            this.maPhieuNhapField.setText("");
            this.moTaField.setText("");
            this.thuKhoField.setText("");
            this.ngayTaoField.setText("");
            this.tongChiPhiField.setText("");

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

    private void xoaTheoDong(ManagementPanel panel, int row) {
        if (row < 0 || row >= panel.getModel().getRowCount()) {
            return;
        }
        this.thucHienXoa(panel, NumberParseUtil.textOf(panel.getModel().getValueAt(row, 1)));
    }

    private void xoaTheoDongChon(ManagementPanel panel) {
        int row = panel.getSelectedRow();
        if (row < 0) {
            ThongBao.canhBao(this, "app.phieuNhap.chuaChonXoa");
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