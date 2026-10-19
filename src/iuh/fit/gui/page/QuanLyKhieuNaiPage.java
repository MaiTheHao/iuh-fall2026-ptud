package iuh.fit.gui.page;

import iuh.fit.dto.DanhSachKhieuNaiCriteria;
import iuh.fit.dto.KhieuNaiUpdateFormData;
import iuh.fit.dto.KhieuNaiView;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.enumeration.TrangThaiKhieuNai;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.KhieuNaiService;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QuanLyKhieuNaiPage extends AbstractPage {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(QuanLyKhieuNaiPage.class.getName());

    private static final int SO_DONG_MOI_TRANG = 20;

    private static final DateTimeFormatter DINH_DANG_NGAY_GIO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final KhieuNaiService khieuNaiService = new KhieuNaiService();

    private final JTextField maKhieuNaiField = new JTextField();
    private final JTextField khachHangField = new JTextField();
    private final JTextField phieuTiepNhanField = new JTextField();
    private final JTextArea noiDungArea = new JTextArea(4, 20);
    private final JComboBox<TrangThaiKhieuNai> trangThaiCombo = new JComboBox<>(TrangThaiKhieuNai.values());

    private final JButton themButton = new JButton();
    private final JButton luuButton = new JButton();
    private final JButton xoaButton = new JButton();
    private final JButton xoaTrangButton = new JButton();

    private CustomKhieuNaiFilterPanel managementPanel;
    private List<KhieuNaiView> danhSachHienTai = List.of();
    private int trang = 1;

    public QuanLyKhieuNaiPage() {
        this.setLayout(new BorderLayout());
        this.setBackground(Theme.BG);
    }

    @Override
    public void render() {
        this.removeAll();
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        this.managementPanel = new CustomKhieuNaiFilterPanel();

        TitledBorder rightBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                I18nReaderUtil.get("app.khieuNai.danhSachTitle")
        );
        rightBorder.setTitleFont(rightBorder.getTitleFont().deriveFont(Font.BOLD));
        this.managementPanel.setBorder(rightBorder);

        this.managementPanel.setHeaders(
                I18nReaderUtil.get("app.khieuNai.ma"),
                I18nReaderUtil.get("app.khieuNai.khachHang"),
                I18nReaderUtil.get("app.khieuNai.phieuTiepNhan"),
                I18nReaderUtil.get("app.khieuNai.noiDung"),
                I18nReaderUtil.get("app.khieuNai.trangThai"),
                I18nReaderUtil.get("app.khieuNai.ngayTao")
        );

        this.managementPanel.addTextFilter("app.khieuNai.ma", 0);
        this.managementPanel.addTextFilter("app.khieuNai.khachHang", 1);
        this.managementPanel.addTextFilter("app.khieuNai.phieuTiepNhan", 2);
        this.managementPanel.addTextFilter("app.khieuNai.noiDung", 3);
        this.managementPanel.addComboBoxFilter(
                "app.khieuNai.trangThai", 4, TrangThaiKhieuNai.values());
        this.managementPanel.addDateRangeFilter("app.khieuNai.ngayTaoTuDen", 5);

        this.managementPanel.setFilterChangeListener(() -> this.taiLai(1));

        if (this.managementPanel.getLayout() instanceof BorderLayout borderLayout) {
            Component west = borderLayout.getLayoutComponent(BorderLayout.WEST);
            if (west != null) {
                west.setVisible(false);
            }
        }

        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(380, 0));

        JPanel leftFormPanel = this.createLeftFormPanel();
        JPanel leftButtonPanel = this.createLeftButtonPanel();

        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setOpaque(false);
        formWrapper.add(leftFormPanel, BorderLayout.NORTH);

        JScrollPane scrollForm = new JScrollPane(formWrapper);
        scrollForm.setBorder(null);
        scrollForm.setOpaque(false);
        scrollForm.getViewport().setOpaque(false);

        leftPanel.add(scrollForm, BorderLayout.CENTER);
        leftPanel.add(leftButtonPanel, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, this.managementPanel);
        splitPane.setDividerLocation(380);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        this.clearActionListeners(
                this.themButton, this.luuButton, this.xoaButton, this.xoaTrangButton);

        this.themButton.addActionListener(e -> this.goTo(TiepNhanKhieuNaiPage.class));
        this.luuButton.addActionListener(e -> this.luuKhieuNai());
        this.xoaButton.addActionListener(e -> this.xoaKhieuNai());
        this.xoaTrangButton.addActionListener(e -> this.lamMoiForm());

        this.managementPanel.getClearFilterButton().addActionListener(
                e -> this.managementPanel.clearFilters());
        this.managementPanel.getPrevButton().addActionListener(
                e -> this.taiLai(this.trang - 1));
        this.managementPanel.getNextButton().addActionListener(
                e -> this.taiLai(this.trang + 1));

        this.managementPanel.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                this.chonDong();
            }
        });

        this.add(splitPane, BorderLayout.CENTER);

        this.lamMoiForm();
        this.taiLai(1);

        this.revalidate();
        this.repaint();
    }

    private JPanel createLeftFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                I18nReaderUtil.get("app.khieuNai.thongTinTitle")
        );
        border.setTitleFont(border.getTitleFont().deriveFont(Font.BOLD));
        panel.setBorder(border);

        JPanel formContent = new JPanel(new GridBagLayout());
        formContent.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        this.maKhieuNaiField.setEditable(false);
        this.khachHangField.setEditable(false);
        this.phieuTiepNhanField.setEditable(false);

        this.addFormRow(formContent, "app.khieuNai.ma", this.maKhieuNaiField, gbc, 0);
        this.addFormRow(formContent, "app.khieuNai.khachHang", this.khachHangField, gbc, 1);
        this.addFormRow(formContent, "app.khieuNai.phieuTiepNhan", this.phieuTiepNhanField, gbc, 2);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        JLabel lblNoiDung = new JLabel(I18nReaderUtil.get("app.khieuNai.noiDung"));
        lblNoiDung.setFont(lblNoiDung.getFont().deriveFont(Font.BOLD));
        formContent.add(lblNoiDung, gbc);

        gbc.gridy = 7;
        this.noiDungArea.setLineWrap(true);
        this.noiDungArea.setWrapStyleWord(true);
        JScrollPane scrollNoiDung = new JScrollPane(this.noiDungArea);
        scrollNoiDung.setPreferredSize(new Dimension(200, 100));
        formContent.add(scrollNoiDung, gbc);

        gbc.gridy = 8;
        JLabel lblTrangThai = new JLabel(I18nReaderUtil.get("app.khieuNai.trangThai"));
        lblTrangThai.setFont(lblTrangThai.getFont().deriveFont(Font.BOLD));
        formContent.add(lblTrangThai, gbc);

        gbc.gridy = 9;
        this.trangThaiCombo.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof TrangThaiKhieuNai trangThai) {
                    this.setText(trangThai.getI18NBusinessName());
                }
                return this;
            }
        });
        formContent.add(this.trangThaiCombo, gbc);

        panel.add(formContent, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createLeftButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
        panel.setOpaque(false);

        this.themButton.setText(I18nReaderUtil.get("app.common.add"));
        this.luuButton.setText(I18nReaderUtil.get("app.common.save"));
        this.xoaButton.setText(I18nReaderUtil.get("app.common.delete"));
        this.xoaTrangButton.setText(I18nReaderUtil.get("app.khieuNai.xoaTrang"));

        Theme.applyPrimary(this.luuButton);
        Theme.applyDanger(this.xoaButton);

        panel.add(this.themButton);
        panel.add(this.luuButton);
        panel.add(this.xoaButton);
        panel.add(this.xoaTrangButton);

        return panel;
    }

    private void addFormRow(JPanel parent, String labelKey, Component field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row * 2;
        gbc.gridwidth = 2;
        JLabel label = new JLabel(I18nReaderUtil.get(labelKey));
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        parent.add(label, gbc);

        gbc.gridy = row * 2 + 1;
        parent.add(field, gbc);
    }

    private void taiLai(int page) {
        try {
            TrangThaiKhieuNai trangThai = this.managementPanel.getComboFilterValue(4)
                    instanceof TrangThaiKhieuNai value ? value : null;

            DanhSachKhieuNaiCriteria tieuChi = new DanhSachKhieuNaiCriteria(
                    this.managementPanel.getTextFilter(0),
                    this.managementPanel.getTextFilter(1),
                    this.managementPanel.getTextFilter(2),
                    this.managementPanel.getTextFilter(3),
                    trangThai,
                    this.managementPanel.getDateFilterFrom(5),
                    this.managementPanel.getDateFilterTo(5),
                    this.managementPanel.getQuickFilter(),
                    Math.max(page, 1),
                    SO_DONG_MOI_TRANG
            );

            PaginatedResult<KhieuNaiView> ketQua = this.khieuNaiService.search(tieuChi);

            List<Object[]> rows = new ArrayList<>();
            for (KhieuNaiView view : ketQua.items()) {
                rows.add(new Object[]{
                        view.maKhieuNai(),
                        view.tenKhachHang(),
                        view.maPhieuTiepNhan(),
                        view.noiDung(),
                        view.trangThai() == null ? "" : view.trangThai().getI18NBusinessName(),
                        this.dinhDangNgayGio(view.ngayTao())
                });
            }

            this.danhSachHienTai = ketQua.items();

            this.managementPanel.setRows(rows);
            this.managementPanel.setCount(
                    ketQua.fromIndex(),
                    ketQua.toIndex(),
                    ketQua.totalItems());
            this.managementPanel.setPage(
                    ketQua.page(),
                    Math.max(ketQua.totalPages(), 1));

            this.trang = ketQua.page();

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Khong the tai danh sach khieu nai", ex);
            ThongBao.loi(this, "app.khieuNai.loiTaiDanhSach");
        }
    }

    private void chonDong() {
        int row = this.managementPanel.getSelectedRow();
        if (row < 0 || row >= this.danhSachHienTai.size()) {
            return;
        }

        KhieuNaiView view = this.danhSachHienTai.get(row);

        this.maKhieuNaiField.setText(view.maKhieuNai() == null ? "" : view.maKhieuNai());
        this.khachHangField.setText(view.tenKhachHang() == null ? "" : view.tenKhachHang());
        this.phieuTiepNhanField.setText(view.maPhieuTiepNhan() == null ? "" : view.maPhieuTiepNhan());
        this.noiDungArea.setText(view.noiDung() == null ? "" : view.noiDung());
        this.trangThaiCombo.setSelectedItem(view.trangThai());
    }

    private void lamMoiForm() {
        this.maKhieuNaiField.setText("");
        this.khachHangField.setText("");
        this.phieuTiepNhanField.setText("");
        this.noiDungArea.setText("");
        this.trangThaiCombo.setSelectedIndex(0);
        this.managementPanel.getTable().clearSelection();
    }

    private void luuKhieuNai() {
        try {
            int row = this.managementPanel.getSelectedRow();
            String maKhieuNai = this.maKhieuNaiField.getText().trim();

            if (row < 0 || maKhieuNai.isBlank()) {
                ThongBao.canhBao(this, "app.khieuNai.loiMa");
                return;
            }

            TrangThaiKhieuNai trangThai = (TrangThaiKhieuNai) this.trangThaiCombo.getSelectedItem();
            KhieuNaiUpdateFormData data = new KhieuNaiUpdateFormData(
                    maKhieuNai,
                    this.noiDungArea.getText(),
                    trangThai
            );

            this.khieuNaiService.update(data);
            ThongBao.thongTin(this, "app.common.update_succeed");

            this.taiLai(this.trang);
            this.lamMoiForm();

        } catch (IllegalArgumentException ex) {
            ThongBao.loi(this, ex);

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Khong the cap nhat khieu nai", ex);
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }

    private void xoaKhieuNai() {
        try {
            int row = this.managementPanel.getSelectedRow();
            if (row < 0) {
                ThongBao.canhBao(this, "app.khieuNai.chuaChonXoa");
                return;
            }

            String maKhieuNai = this.maKhieuNaiField.getText().trim();
            if (maKhieuNai.isBlank()) {
                ThongBao.canhBao(this, "app.khieuNai.chuaChonXoa");
                return;
            }

            if (!ThongBao.xacNhan(this, "app.khieuNai.xacNhanXoa", "app.khieuNai.tieuDeXoa")) {
                return;
            }

            this.khieuNaiService.delete(maKhieuNai);
            ThongBao.thongTin(this, "app.common.delete_succeed");

            this.taiLai(this.trang);
            this.lamMoiForm();

        } catch (IllegalArgumentException ex) {
            ThongBao.loi(this, ex);

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Khong the xoa khieu nai", ex);
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }

    private String dinhDangNgayGio(LocalDateTime thoiGian) {
        return thoiGian == null ? "" : thoiGian.format(DINH_DANG_NGAY_GIO);
    }

    @Override
    public String getTitleKey() {
        return "app.menu.complaintManagement";
    }

    @Override
    public MenuGroup getGroup() {
        return MenuGroup.TIEP_NHAN;
    }
}

/**
 * Bảng danh sách khiếu nại dùng chung ManagementPanel.
 * Khác mockup scr-16b: không có cột "Thao Tác" vì ManagementPanel không hỗ trợ nút thao tác theo từng dòng.
 */
class CustomKhieuNaiFilterPanel extends ManagementPanel {

    private static final long serialVersionUID = 1L;
}
