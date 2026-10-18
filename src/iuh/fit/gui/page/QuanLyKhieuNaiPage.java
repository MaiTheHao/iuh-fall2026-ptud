package iuh.fit.gui.page;

import com.github.lgooddatepicker.components.DatePicker;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.enumeration.TrangThaiKhieuNai;
import iuh.fit.gui.component.BoChonNgayGio;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.theme.Theme;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class QuanLyKhieuNaiPage extends AbstractPage {

    private static final long serialVersionUID = 1L;

    // Form fields (Left Panel)
    private final JTextField maKhieuNaiField = new JTextField();
    private final JTextField khachHangField = new JTextField();
    private final JComboBox<String> phieuTiepNhanCombo = new JComboBox<>();
    private final JTextArea noiDungArea = new JTextArea(4, 20);
    private final JComboBox<TrangThaiKhieuNai> trangThaiCombo = new JComboBox<>(TrangThaiKhieuNai.values());

    // Bottom Buttons (Left Panel)
    private final JButton themButton = new JButton("Thêm");
    private final JButton luuButton = new JButton("Lưu");
    private final JButton xoaTrangButton = new JButton("Xóa Trắng");

    private CustomKhieuNaiFilterPanel managementPanel;

    public QuanLyKhieuNaiPage() {
        this.setLayout(new BorderLayout());
        this.setBackground(Theme.BG);
    }

    @Override
    public void render() {
        this.removeAll();
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // =========================================================
        // Right Panel: ManagementPanel (Bộ lọc Lưới 2x3 & Table)
        // =========================================================
        this.managementPanel = new CustomKhieuNaiFilterPanel();

        TitledBorder rightBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                "Danh Sách Khiếu Nại"
        );
        rightBorder.setTitleFont(rightBorder.getTitleFont().deriveFont(Font.BOLD));
        this.managementPanel.setBorder(rightBorder);

        this.managementPanel.setHeaders(
                "Mã Khiếu Nại",
                "Khách Hàng",
                "Phiếu Tiếp Nhận",
                "Nội Dung",
                "Trạng Thái",
                "Ngày Tạo",
                "Thao Tác"
        );

        // =========================================================
        // Left Panel (Form + Action Buttons)
        // =========================================================
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

        // =========================================================
        // SplitPane Setup
        // =========================================================
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, this.managementPanel);
        splitPane.setDividerLocation(380);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        // Wiring listeners
        this.lamMoiForm();
        this.xoaTrangButton.addActionListener(e -> this.lamMoiForm());
        this.managementPanel.getClearFilterButton().addActionListener(e -> this.managementPanel.clearFilters());

        this.add(splitPane, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

    private JPanel createLeftFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                "Thông Tin Khiếu Nại"
        );
        border.setTitleFont(border.getTitleFont().deriveFont(Font.BOLD));
        panel.setBorder(border);

        JPanel formContent = new JPanel(new GridBagLayout());
        formContent.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        this.maKhieuNaiField.setEnabled(false);

        this.addFormRow(formContent, "Mã Khiếu Nại:", this.maKhieuNaiField, gbc, 0);
        this.addFormRow(formContent, "Khách Hàng:", this.khachHangField, gbc, 1);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JLabel lblPhieu = new JLabel("Phiếu Tiếp Nhận:");
        lblPhieu.setFont(lblPhieu.getFont().deriveFont(Font.BOLD));
        formContent.add(lblPhieu, gbc);
        gbc.gridy = 5;
        formContent.add(this.phieuTiepNhanCombo, gbc);

        gbc.gridy = 6;
        JLabel lblMoTa = new JLabel("Nội Dung Khiếu Nại:");
        lblMoTa.setFont(lblMoTa.getFont().deriveFont(Font.BOLD));
        formContent.add(lblMoTa, gbc);

        gbc.gridy = 7;
        this.noiDungArea.setLineWrap(true);
        this.noiDungArea.setWrapStyleWord(true);
        JScrollPane scrollMoTa = new JScrollPane(this.noiDungArea);
        scrollMoTa.setPreferredSize(new Dimension(200, 100));
        formContent.add(scrollMoTa, gbc);

        gbc.gridy = 8;
        JLabel lblTrangThai = new JLabel("Trạng Thái:");
        lblTrangThai.setFont(lblTrangThai.getFont().deriveFont(Font.BOLD));
        formContent.add(lblTrangThai, gbc);

        gbc.gridy = 9;
        formContent.add(this.trangThaiCombo, gbc);

        panel.add(formContent, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createLeftButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panel.setOpaque(false);

        this.luuButton.setBackground(Theme.ACCENT);
        this.luuButton.setForeground(Theme.PANEL);
        this.themButton.setPreferredSize(new Dimension(100, 32));
        this.luuButton.setPreferredSize(new Dimension(120, 32));
        this.xoaTrangButton.setPreferredSize(new Dimension(100, 32));

        panel.add(this.themButton);
        panel.add(this.luuButton);
        panel.add(this.xoaTrangButton);

        return panel;
    }

    private void addFormRow(JPanel parent, String labelText, Component field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row * 2;
        gbc.gridwidth = 2;
        JLabel label = new JLabel(labelText);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        parent.add(label, gbc);

        gbc.gridy = row * 2 + 1;
        parent.add(field, gbc);
    }

    private void lamMoiForm() {
        this.maKhieuNaiField.setText("");
        this.khachHangField.setText("");
        this.phieuTiepNhanCombo.removeAllItems();
        this.noiDungArea.setText("");
        this.trangThaiCombo.setSelectedIndex(0);
        this.managementPanel.getTable().clearSelection();
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
 * Lớp cấu hình ManagementPanel chuyên biệt cho màn hình Quản Lý Khiếu Nại
 */
class CustomKhieuNaiFilterPanel extends ManagementPanel {

    private final JTextField filterMaKhieuNai = new JTextField();
    private final JTextField filterKhachHang = new JTextField();
    private final JTextField filterPhieu = new JTextField();
    private final JTextField filterNoiDung = new JTextField();
    private final JComboBox<Object> filterTrangThaiCombo = new JComboBox<>();
    private final DatePicker filterTuNgay = BoChonNgayGio.taoBoChonNgay();
    private final DatePicker filterDenNgay = BoChonNgayGio.taoBoChonNgay();

    public CustomKhieuNaiFilterPanel() {
        super();
        this.buildCustomFilterLayout();
    }

    private void buildCustomFilterLayout() {
        // 1. Ẩn toàn bộ form nhập liệu bên trái (BorderLayout.WEST) để nhường chỗ cho Form tùy chỉnh của Page
        if (this.getLayout() instanceof BorderLayout bl) {
            Component west = bl.getLayoutComponent(BorderLayout.WEST);
            if (west != null) {
                west.setVisible(false);
            }
            
            // 2. Chèn Lưới 2x3 vào phần NORTH của Bảng
            Component center = bl.getLayoutComponent(BorderLayout.CENTER);
            if (center instanceof JPanel listPanel && listPanel.getLayout() instanceof BorderLayout bl2) {
                Component north = bl2.getLayoutComponent(BorderLayout.NORTH);
                
                if (north instanceof JPanel filterPanel && filterPanel.getLayout() instanceof BorderLayout bl3) {
                    // Xóa khu vực filter cũ, giữ lại thanh tìm nhanh ở SOUTH
                    Component oldCenter = bl3.getLayoutComponent(BorderLayout.CENTER);
                    if (oldCenter != null) {
                        filterPanel.remove(oldCenter);
                    }
                    
                    // Tạo Lưới 2 dòng x 3 cột
                    JPanel grid = new JPanel(new GridBagLayout());
                    grid.setOpaque(false);
                    grid.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.insets = new Insets(4, 4, 4, 4);
                    gbc.fill = GridBagConstraints.HORIZONTAL;
                    gbc.weightx = 1.0;

                    // --- HÀNG 1 ---
                    gbc.gridx = 0; gbc.gridy = 0;
                    grid.add(new JLabel("Mã Khiếu Nại"), gbc);
                    gbc.gridy = 1;
                    this.filterMaKhieuNai.putClientProperty("JTextField.placeholderText", "Nhập...");
                    grid.add(this.filterMaKhieuNai, gbc);

                    gbc.gridx = 1; gbc.gridy = 0;
                    grid.add(new JLabel("Khách Hàng"), gbc);
                    gbc.gridy = 1;
                    this.filterKhachHang.putClientProperty("JTextField.placeholderText", "Nhập...");
                    grid.add(this.filterKhachHang, gbc);

                    gbc.gridx = 2; gbc.gridy = 0;
                    grid.add(new JLabel("Phiếu Tiếp Nhận"), gbc);
                    gbc.gridy = 1;
                    this.filterPhieu.putClientProperty("JTextField.placeholderText", "Nhập...");
                    grid.add(this.filterPhieu, gbc);

                    // --- HÀNG 2 ---
                    gbc.gridx = 0; gbc.gridy = 2;
                    grid.add(new JLabel("Nội Dung"), gbc);
                    gbc.gridy = 3;
                    this.filterNoiDung.putClientProperty("JTextField.placeholderText", "Nhập...");
                    grid.add(this.filterNoiDung, gbc);

                    gbc.gridx = 1; gbc.gridy = 2;
                    grid.add(new JLabel("Trạng Thái"), gbc);
                    gbc.gridy = 3;
                    this.filterTrangThaiCombo.removeAllItems();
                    this.filterTrangThaiCombo.addItem("Tất cả");
                    for (TrangThaiKhieuNai tt : TrangThaiKhieuNai.values()) {
                        this.filterTrangThaiCombo.addItem(tt);
                    }
                    grid.add(this.filterTrangThaiCombo, gbc);

                    gbc.gridx = 2; gbc.gridy = 2;
                    grid.add(new JLabel("Ngày Tạo (từ - đến)"), gbc);
                    gbc.gridy = 3;
                    JPanel dateRange = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
                    dateRange.setOpaque(false);
                    dateRange.add(this.filterTuNgay);
                    dateRange.add(new JLabel(" - "));
                    dateRange.add(this.filterDenNgay);
                    grid.add(dateRange, gbc);

                    // Đưa lưới filter 2x3 vào vị trí CENTER của filterPanel
                    filterPanel.add(grid, BorderLayout.CENTER);
                }
            }
        }
    }

    @Override
    public void clearFilters() {
        super.clearFilters();
        this.filterMaKhieuNai.setText("");
        this.filterKhachHang.setText("");
        this.filterPhieu.setText("");
        this.filterNoiDung.setText("");
        this.filterTuNgay.clear();
        this.filterDenNgay.clear();
        if (this.filterTrangThaiCombo.getItemCount() > 0) {
            this.filterTrangThaiCombo.setSelectedIndex(0);
        }
    }
}