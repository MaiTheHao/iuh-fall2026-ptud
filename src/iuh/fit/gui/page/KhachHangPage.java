package iuh.fit.gui.page;

import iuh.fit.dto.DanhSachKhachHangCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.KhachHang;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.service.KhachHangService;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class KhachHangPage extends AbstractPage {

    private static final int SO_DONG_MOI_TRANG = 10;

    private final KhachHangService khachHangService = new KhachHangService();
    private final JTextField maField = new JTextField(14);
    private final JTextField sdtField = new JTextField(14);
    private final JTextField hoTenField = new JTextField(14);
    private int trang = 1;

    public KhachHangPage() {
        this.setLayout(new BorderLayout());
    }

    @Override
    public void render() {
        this.removeAll();
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        ManagementPanel panel = new ManagementPanel();
        panel.setHeaders(
                I18nReaderUtil.get("app.khachHang.ma"),
                I18nReaderUtil.get("app.khachHang.sdt"),
                I18nReaderUtil.get("app.khachHang.hoTen"),
                I18nReaderUtil.get("app.khachHang.ngayTao"));

        this.maField.setEnabled(false);
        this.addFormRow(panel, "app.khachHang.ma", this.maField);
        this.addFormRow(panel, "app.khachHang.sdt", this.sdtField);
        this.addFormRow(panel, "app.khachHang.hoTen", this.hoTenField);
        

        panel.addTextFilter("app.khachHang.ma", 0);
        panel.addTextFilter("app.khachHang.sdt", 1);
        panel.addTextFilter("app.khachHang.hoTen", 2);
        panel.addDateRangeFilter("app.khachHang.ngayTao", 3);
        panel.setFilterChangeListener(() -> this.taiLai(panel, 1));

        panel.getThemButton().addActionListener(e -> this.lamMoi(panel));
        panel.getLuuButton().addActionListener(e -> this.luu(panel));
        panel.getXoaTrangButton().addActionListener(e -> this.lamMoi(panel));
        panel.getXoaButton().addActionListener(e -> this.xoa(panel));
        panel.getClearFilterButton().addActionListener(e -> panel.clearFilters());
        panel.getPrevButton().addActionListener(e -> this.taiLai(panel, this.trang - 1));
        panel.getNextButton().addActionListener(e -> this.taiLai(panel, this.trang + 1));
        panel.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                this.chonDong(panel);
            }
        });

        this.taiLai(panel, 1);
        this.add(panel, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

    
    private void xoa(ManagementPanel panel) {
	    int row = panel.getSelectedRow();
	
	    try {
	        if (row < 0) {
	            throw new IllegalArgumentException(
	                    "app.khachHang.chuaChonXoa");
	        }
	
	        String maKhachHang = String.valueOf(
	                panel.getModel().getValueAt(row, 0));
	
	        int luaChon = JOptionPane.showConfirmDialog(
	                this,
	                I18nReaderUtil.get("app.khachHang.xacNhanXoa"),
	                I18nReaderUtil.get("app.khachHang.tieuDeXoa"),
	                JOptionPane.YES_NO_OPTION,
	                JOptionPane.WARNING_MESSAGE);
	
	        if (luaChon != JOptionPane.YES_OPTION) {
	            return;
	        }
	
	        this.khachHangService.delete(maKhachHang);
	        JOptionPane.showMessageDialog(
	                this,
	                I18nReaderUtil.get("app.common.delete_succeed"));
	        this.lamMoi(panel);
	        this.taiLai(panel, this.trang);
	
	    } catch (IllegalArgumentException e) {
	        JOptionPane.showMessageDialog(
	                this,
	                I18nReaderUtil.get(e.getMessage()));
	
	    } catch (SQLException e) {
	        JOptionPane.showMessageDialog(
	                this,
	                I18nReaderUtil.get("app.login.errorSystem"));
	    }
	}


	private void addFormRow(ManagementPanel panel, String labelKey, JTextField field) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        row.setOpaque(false);
        row.add(new JLabel(I18nReaderUtil.get(labelKey)));
        row.add(field);
        panel.getFormPanel().add(row);
    }

    private void taiLai(ManagementPanel panel, int page) {
        DanhSachKhachHangCriteria tieuChi = new DanhSachKhachHangCriteria(
                panel.getTextFilter(0),
                panel.getTextFilter(1),
                panel.getTextFilter(2),
                panel.getDateFilterFrom(3),
                panel.getDateFilterTo(3),
                panel.getQuickFilter(),
                page,
                SO_DONG_MOI_TRANG);
        try {
            PaginatedResult<KhachHang> ketQua = this.khachHangService.search(tieuChi);
            List<Object[]> rows = new ArrayList<>();
            for (KhachHang khachHang : ketQua.items()) {
                rows.add(new Object[] {
                        khachHang.getMaKhachHang(),
                        khachHang.getSdt(),
                        khachHang.getHoTen(),
                        this.dinhDangNgay(khachHang.getNgayTao())
                });
            }
            panel.setRows(rows);
            panel.setCount(ketQua.fromIndex(), ketQua.toIndex(), ketQua.totalItems());
            panel.setPage(ketQua.page(), Math.max(ketQua.totalPages(), 1));
            this.trang = ketQua.page();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, I18nReaderUtil.get("app.login.errorSystem"));
        }
    }

    private String dinhDangNgay(LocalDateTime thoiGian) {
        if (thoiGian == null) {
            return "";
        }
        return thoiGian.format(DateTimeFormatter.ofPattern(I18nReaderUtil.get("app.date.format")));
    }

    private void chonDong(ManagementPanel panel) {
        int row = panel.getSelectedRow();
        if (row < 0) {
            return;
        }
        this.maField.setText(String.valueOf(panel.getModel().getValueAt(row, 0)));
        this.sdtField.setText(String.valueOf(panel.getModel().getValueAt(row, 1)));
        this.hoTenField.setText(String.valueOf(panel.getModel().getValueAt(row, 2)));
    }

    private void lamMoi(ManagementPanel panel) {
        this.maField.setText("");
        this.sdtField.setText("");
        this.hoTenField.setText("");
        panel.getTable().clearSelection();
        this.sdtField.requestFocusInWindow();
    }

    private void luu(ManagementPanel panel) {
        KhachHang khachHang = new KhachHang();
        String ma = this.maField.getText().isBlank() ? null : this.maField.getText();
        khachHang.setMaKhachHang(ma);
        khachHang.setSdt(this.sdtField.getText().trim());
        khachHang.setHoTen(this.hoTenField.getText().trim());
        try {
            if (ma == null) {
                this.khachHangService.create(khachHang);
                JOptionPane.showMessageDialog(
                        this, I18nReaderUtil.get("app.common.create_succeed"));
            } else {
                this.khachHangService.update(khachHang);
                JOptionPane.showMessageDialog(
                		this, I18nReaderUtil.get("app.common.update_succeed"));
            }
            this.lamMoi(panel);
            this.taiLai(panel, 1);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, I18nReaderUtil.get(e.getMessage()));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, I18nReaderUtil.get("app.login.errorSystem"));
        }
    }

    @Override
    public String getTitleKey() {
        return "app.menu.customers";
    }

    @Override
    public MenuGroup getGroup() {
        return MenuGroup.TO_CHUC;
    }
}
