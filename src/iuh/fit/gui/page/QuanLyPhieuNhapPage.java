package iuh.fit.gui.page;

import iuh.fit.dto.DanhSachCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.PhieuNhapPhuTung;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.service.PhieuNhapPhuTungService;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.NumberParseUtil;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

    public QuanLyPhieuNhapPage() {
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(8, 12, 10, 12));
    }

    @Override
    public void render() {
        this.removeAll();

        ManagementPanel panel = new ManagementPanel();
        panel.getFormPanel().getParent().setVisible(false);

        panel.setHeaders(
                "#",
                I18nReaderUtil.get("app.phieuNhap.maPhieuNhap"),
                I18nReaderUtil.get("app.phieuNhap.moTa"),
                I18nReaderUtil.get("app.phieuNhap.thuKho"),
                I18nReaderUtil.get("app.phieuNhap.ngayTao"),
                I18nReaderUtil.get("app.phieuNhap.tongChiPhi"),
                I18nReaderUtil.get("app.common.delete")
        );

        panel.addTextFilter("app.phieuNhap.maPhieuNhap", 1);
        panel.addTextFilter("app.phieuNhap.moTa", 2);
        panel.addTextFilter("app.phieuNhap.thuKho", 3);

        this.chenDiemNgatHang(panel);

        panel.addDateRangeFilter("app.phieuNhap.ngayTao", 4);
        panel.addRangeFilter("app.phieuNhap.tongChiPhi", 5);

        this.tangKichThuocNhan(panel);

        panel.getThemButton().setText(I18nReaderUtil.get("app.phieuNhap.taoPhieuMoi"));
        panel.getThemButton().addActionListener(e -> this.goTo(LapPhieuNhapPage.class));
        panel.getXoaButton().addActionListener(e -> this.xoaTheoDongChon(panel));

        panel.setFilterChangeListener(() -> this.taiLai(panel, 1));
        panel.getClearFilterButton().addActionListener(e -> panel.clearFilters());
        panel.getPrevButton().addActionListener(e -> this.taiLai(panel, this.trang - 1));
        panel.getNextButton().addActionListener(e -> this.taiLai(panel, this.trang + 1));

        JTable table = panel.getTable();
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setRowHeight(34);

        this.cauHinhCotThaoTac(panel, 6);
        this.taiLai(panel, 1);

        this.add(panel, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

    private void tangKichThuocNhan(Container container) {
        for (Component c : container.getComponents()) {
            if (c instanceof JLabel lbl && lbl.getPreferredSize().width > 0) {
                Dimension d = lbl.getPreferredSize();
                lbl.setPreferredSize(new Dimension(d.width + 30, d.height));
            } else if (c instanceof Container sub) {
                this.tangKichThuocNhan(sub);
            }
        }
    }

    private void chenDiemNgatHang(Container container) {
        JPanel wrapPanel = this.timWrapPanel(container);
        if (wrapPanel != null) {
            JPanel breaker = new JPanel();
            breaker.setOpaque(false);
            breaker.setPreferredSize(new Dimension(3000, 1));
            wrapPanel.add(breaker);
        }
    }

    private JPanel timWrapPanel(Container container) {
        if (container.getLayout() instanceof iuh.fit.gui.component.WrapLayout) {
            return (JPanel) container;
        }
        for (Component child : container.getComponents()) {
            if (child instanceof Container sub) {
                JPanel res = this.timWrapPanel(sub);
                if (res != null) {
                    return res;
                }
            }
        }
        return null;
    }

    private void cauHinhCotThaoTac(ManagementPanel panel, int colXoa) {
        JTable table = panel.getTable();

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(JLabel.CENTER);

        table.getColumnModel().getColumn(colXoa).setCellRenderer(renderer);
        table.getColumnModel().getColumn(colXoa).setPreferredWidth(70);
        table.getColumnModel().getColumn(colXoa).setMaxWidth(80);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                int col = table.getSelectedColumn();

                if (row < 0) {
                    return;
                }

                if (col == colXoa) {
                    QuanLyPhieuNhapPage.this.xoaTheoDong(panel, row);
                }
            }
        });
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
            String nhanXoa = I18nReaderUtil.get("app.common.delete");

            for (PhieuNhapPhuTung item : res.items()) {
                rows.add(new Object[]{
                        stt++,
                        item.getMaPhieuNhap(),
                        item.getMoTa(),
                        item.getMaNhanVien(),
                        item.getNgayTao() != null ? item.getNgayTao().format(this.dateFormatter) : "",
                        this.currencyFormat.format(this.phieuNhapService.tinhTongChiPhi(item)),
                        nhanXoa
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