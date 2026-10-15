package iuh.fit.gui.page;

import iuh.fit.dto.DanhSachVoucherCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.Voucher;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.service.VoucherService;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.TextNormalizerUtil;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class VoucherPage extends AbstractPage {

    private static final int SO_DONG_MOI_TRANG = 10;

    private static final DateTimeFormatter DINH_DANG_NGAY_GIO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final VoucherService voucherService = new VoucherService();

    private final JTextField maField = new JTextField(14);
    private final JTextField tenChuongTrinhField = new JTextField(14);
    private final JTextField maVoucherFilterField = new JTextField(14);
    private final JTextField moTaField = new JTextField(14);
    private final JTextField mucGiamField = new JTextField(14);
    private final JTextField thoiHanField = new JTextField(14);
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

        this.addFormRow(panel, "app.voucher.ma", this.maField);
        this.addFormRow(
                panel, "app.voucher.tenChuongTrinh",
                this.tenChuongTrinhField);
        this.addFormRow(panel, "app.voucher.moTa", this.moTaField);
        this.addFormRow(panel, "app.voucher.mucGiam", this.mucGiamField);
        JLabel huongDan = new JLabel(
       		 I18nReaderUtil.get("app.voucher.huongDanMucGiam")
       	);

       	huongDan.setForeground(Color.GRAY);

       	panel.getFormPanel().add(huongDan);
       	
        this.addFormRow(
                panel, "app.voucher.thoiHanApDung",
                this.thoiHanField);
         huongDan = new JLabel(
        		 I18nReaderUtil.get("app.voucher.huongDanThoiGian")
        	);

        	huongDan.setForeground(Color.GRAY);

        	panel.getFormPanel().add(huongDan);
        this.addFormRow(
                panel, "app.voucher.trangThaiKichHoat",
                this.trangThaiField);
        
        

        panel.addTextFilter("app.voucher.ma", 3);
        panel.addRangeFilter("app.voucher.mucGiam", 4);
        panel.addDateRangeFilter("app.voucher.thoiHanApDung", 6);
        panel.addTextFilter("app.voucher.trangThaiKichHoat", 7);
        panel.setFilterChangeListener(() -> this.taiLai(panel, 1));

        panel.getThemButton().addActionListener(e -> this.lamMoi(panel));
        panel.getLuuButton().addActionListener(e -> this.luu(panel));
        panel.getXoaTrangButton().addActionListener(e -> this.lamMoi(panel));
        panel.getXoaButton().addActionListener(e -> this.xoa(panel));

        panel.getClearFilterButton().addActionListener(e -> {
            panel.clearFilters();
            this.taiLai(panel, 1);
        });

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

    /**
     * Thêm một dòng trường nhập liệu vào form.
     */
    private void addFormRow(
            ManagementPanel panel,
            String labelKey,
            java.awt.Component field) {

        JPanel row = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 6, 2));

        row.setOpaque(false);
        row.add(new JLabel(I18nReaderUtil.get(labelKey)));
        row.add(field);

        panel.getFormPanel().add(row);
    }

    /**
     * Tải danh sách voucher theo điều kiện lọc và phân trang.
     */
    private void taiLai(ManagementPanel panel, int page) {
        try {

			String maVoucher = panel.getTextFilter(3);
			
			Double mucGiamTu = this.docMucGiam(
			        panel.getTextFilter(4));
			
			Double mucGiamDen = this.docMucGiam(
			        panel.getTextFilter(5));
			
			LocalDate thoiHanTu = panel.getDateFilterFrom(6);
			LocalDate thoiHanDen = panel.getDateFilterTo(6);
			
			Boolean trangThai = this.docTrangThai(
			        panel.getTextFilter(7));


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
                        this.dinhDangNgayGio(
                                voucher.getThoiHanApDung()),
                        voucher.isTrangThaiKichHoat()
                                ? I18nReaderUtil.get(
                                        "app.voucher.dangKichHoat")
                                : I18nReaderUtil.get(
                                        "app.voucher.ngungKichHoat")
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
            panel.setRows(java.util.Collections.emptyList());
            panel.setCount(0, 0, 0);
            panel.setPage(1, 1);
            this.trang = 1;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    I18nReaderUtil.get("app.login.errorSystem"));
        }
    }

    /**
     * Đọc mức giảm từ ô lọc.
     */
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
                    "voucher.loiMucGiam");
        }
    }

    /**
     * Đọc trạng thái từ ô lọc.
     * Để trống: không lọc trạng thái.
     */
    private Boolean docTrangThai(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String keyword = TextNormalizerUtil.normalize(value);

        if (keyword.equals("true")
                || keyword.equals("1")
                || keyword.equals("co")
                || keyword.equals("dang kich hoat")
                || keyword.equals("kich hoat")) {
            return true;
        }

        if (keyword.equals("false")
                || keyword.equals("0")
                || keyword.equals("khong")
                || keyword.equals("ngung kich hoat")
                || keyword.equals("vo hieu")) {
            return false;
        }

        throw new IllegalArgumentException(
                "voucher.loiTrangThai");
    }

    /**
     * Định dạng thời hạn áp dụng.
     */
    private String dinhDangNgayGio(LocalDateTime thoiGian) {
        if (thoiGian == null) {
            return "";
        }

        return thoiGian.format(DINH_DANG_NGAY_GIO);
    }

    /**
     * Đổ dữ liệu voucher được chọn lên form.
     */
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

        this.thoiHanField.setText(thoiHan);

        String trangThai = TextNormalizerUtil.normalize(
                String.valueOf(panel.getModel().getValueAt(row, 5)));

        this.trangThaiField.setSelected(
                trangThai.equals(TextNormalizerUtil.normalize(
                        I18nReaderUtil.get(
                                "app.voucher.dangKichHoat"))));
    }

    /**
     * Xóa dữ liệu trên form để thêm voucher mới.
     */
    private void lamMoi(ManagementPanel panel) {
        this.maField.setText("");
        this.tenChuongTrinhField.setText("");
        this.moTaField.setText("");
        this.mucGiamField.setText("");
        this.thoiHanField.setText("");
        this.trangThaiField.setSelected(true);

        panel.getTable().clearSelection();
        this.tenChuongTrinhField.requestFocusInWindow();
    }

    /**
     * Thêm mới hoặc cập nhật voucher.
     */
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

            LocalDateTime thoiHan = this.docThoiHan(
                    this.thoiHanField.getText());

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

                JOptionPane.showMessageDialog(
                        this,
                        I18nReaderUtil.get(
                                "app.common.create_succeed"));
            } else {
                this.voucherService.update(voucher);

                JOptionPane.showMessageDialog(
                        this,
                        I18nReaderUtil.get(
                                "app.common.update_succeed"));
            }

            this.lamMoi(panel);
            this.taiLai(panel, 1);

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

    /**
     * Đọc thời hạn theo định dạng dd/MM/yyyy HH:mm.
     */

	private LocalDateTime docThoiHan(String giaTri) {
	    if (giaTri == null || giaTri.trim().isEmpty()) {
	        throw new IllegalArgumentException("voucher.loiDinhDangThoiHan");
	    }
	
	    try {
	        return LocalDateTime.parse(
	            giaTri.trim(),
	            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
	        );
	    } catch (DateTimeParseException e) {
	        throw new IllegalArgumentException("voucher.loiDinhDangThoiHan");
	    }
	}


    /**
     * Xóa voucher được chọn.
     */
    private void xoa(ManagementPanel panel) {
        int row = panel.getSelectedRow();

        try {
            if (row < 0) {
                throw new IllegalArgumentException(
                        "app.voucher.chuaChonXoa");
            }

            String maVoucher = String.valueOf(
                    panel.getModel().getValueAt(row, 0));

            int luaChon = JOptionPane.showConfirmDialog(
                    this,
                    I18nReaderUtil.get(
                            "app.voucher.xacNhanXoa"),
                    I18nReaderUtil.get(
                            "app.voucher.tieuDeXoa"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (luaChon != JOptionPane.YES_OPTION) {
                return;
            }

            this.voucherService.delete(maVoucher);

            JOptionPane.showMessageDialog(
                    this,
                    I18nReaderUtil.get(
                            "app.common.delete_succeed"));

            this.lamMoi(panel);
            this.taiLai(panel, this.trang);

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    this,
                    I18nReaderUtil.get(e.getMessage()));

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    I18nReaderUtil.get(
                            "app.login.errorSystem"));
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