package iuh.fit.gui.page;

import iuh.fit.dto.DanhSachKhachHangCriteria;
import iuh.fit.entity.KhachHang;
import iuh.fit.entity.PhieuTiepNhan;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.enumeration.TrangThaiPhieuTiepNhan;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.KhachHangService;
import iuh.fit.service.PhieuTiepNhanService;
import iuh.fit.service.XeService;

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
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class QuanLyPhieuTiepNhanPage extends AbstractPage {

    private static final long serialVersionUID = 1L;

    private final PhieuTiepNhanService phieuTiepNhanService = new PhieuTiepNhanService();
    private final KhachHangService khachHangService = new KhachHangService();
    private final XeService xeService = new XeService();

    // Form fields (Left Panel)
    private final JTextField maPhieuField = new JTextField();
    private final JTextField bienSoField = new JTextField();
    private final JTextField khachHangField = new JTextField();
    private final JTextField sdtField = new JTextField();
    private final JTextArea moTaArea = new JTextArea(4, 20);
    private final JComboBox<TrangThaiPhieuTiepNhan> trangThaiCombo = new JComboBox<>(TrangThaiPhieuTiepNhan.values());

    // Left Bottom Buttons
    private final JButton themButton = new JButton("Thêm");
    private final JButton luuButton = new JButton("Lưu");
    private final JButton xoaTrangButton = new JButton("Xóa Trắng");

    private ManagementPanel managementPanel;
    private int trang = 1;

    public QuanLyPhieuTiepNhanPage() {
        this.setLayout(new BorderLayout());
        this.setBackground(Theme.BG);
    }

    @Override
    public void render() {
        this.removeAll();
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // =========================================================
        // Right Panel: ManagementPanel Setup
        // =========================================================
        this.managementPanel = new ManagementPanel();

        TitledBorder rightBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                "Danh Sách Phiếu Tiếp Nhận & Trạng Thái Tiến Độ Xưởng"
        );
        rightBorder.setTitleFont(rightBorder.getTitleFont().deriveFont(Font.BOLD));
        this.managementPanel.setBorder(rightBorder);

        this.managementPanel.setHeaders(
                "Mã Tiếp Nhận",
                "Biển Số",
                "Khách Hàng",
                "Điện Thoại",
                "Thời Gian Vào (từ - đến)",
                "Nội Dung",
                "Trạng Thái"
        );

        // Safe Filter Configuration using existing i18n keys
        this.managementPanel.addTextFilter("app.phieuTiepNhan.ma", 0);
        this.managementPanel.addTextFilter("app.xe.bienSo", 1);
        this.managementPanel.addTextFilter("app.khachHang.hoTen", 2);
        this.managementPanel.addTextFilter("app.khachHang.sdt", 3);
        this.managementPanel.addDateRangeFilter("app.phieuTiepNhan.thoiGianVao", 4);
        this.managementPanel.addTextFilter("app.phieuTiepNhan.noiDung", 5);
        this.managementPanel.addComboBoxFilter("app.phieuTiepNhan.trangThai", 6, TrangThaiPhieuTiepNhan.values());

        this.managementPanel.setFilterChangeListener(() -> this.taiLaiData(1));

        // Safe component removal from ManagementPanel default left area
        if (this.managementPanel.getLayout() instanceof BorderLayout) {
            Component west = ((BorderLayout) this.managementPanel.getLayout()).getLayoutComponent(BorderLayout.WEST);
            if (west != null) {
                west.setVisible(false);
            }
        }
        for (Component c : this.managementPanel.getComponents()) {
            if (c instanceof JSplitPane splitPane) {
                splitPane.setLeftComponent(null);
            }
        }

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

        // Action Listeners
        this.themButton.addActionListener(e -> this.lamMoiForm());
        this.luuButton.addActionListener(e -> this.luuPhieuTiepNhan());
        this.xoaTrangButton.addActionListener(e -> this.lamMoiForm());

        this.managementPanel.getClearFilterButton().addActionListener(e -> this.managementPanel.clearFilters());
        this.managementPanel.getPrevButton().addActionListener(e -> this.taiLaiData(this.trang - 1));
        this.managementPanel.getNextButton().addActionListener(e -> this.taiLaiData(this.trang + 1));

        this.managementPanel.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                this.chonDong();
            }
        });

        this.add(splitPane, BorderLayout.CENTER);
        this.taiLaiData(1);
        this.revalidate();
        this.repaint();
    }

    private JPanel createLeftFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                "Thông Tin Phiếu Tiếp Nhận"
        );
        border.setTitleFont(border.getTitleFont().deriveFont(Font.BOLD));
        panel.setBorder(border);

        JPanel formContent = new JPanel(new GridBagLayout());
        formContent.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        this.maPhieuField.setEnabled(false);

        this.addFormRow(formContent, "Mã Phiếu Tiếp Nhận:", this.maPhieuField, gbc, 0);
        this.addFormRow(formContent, "Biển Số Xe:", this.bienSoField, gbc, 1);
        this.addFormRow(formContent, "Khách Hàng:", this.khachHangField, gbc, 2);
        this.addFormRow(formContent, "Điện Thoại:", this.sdtField, gbc, 3);

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        JLabel lblMoTa = new JLabel("Nội Dung Yêu Cầu:");
        lblMoTa.setFont(lblMoTa.getFont().deriveFont(Font.BOLD));
        formContent.add(lblMoTa, gbc);

        gbc.gridy = 9;
        this.moTaArea.setLineWrap(true);
        this.moTaArea.setWrapStyleWord(true);
        JScrollPane scrollMoTa = new JScrollPane(this.moTaArea);
        scrollMoTa.setPreferredSize(new Dimension(200, 100));
        formContent.add(scrollMoTa, gbc);

        gbc.gridy = 10;
        JLabel lblTrangThai = new JLabel("Trạng Thái Tiến Độ:");
        lblTrangThai.setFont(lblTrangThai.getFont().deriveFont(Font.BOLD));
        formContent.add(lblTrangThai, gbc);

        gbc.gridy = 11;
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

    private void addFormRow(JPanel parent, String labelText, JTextField field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row * 2;
        gbc.gridwidth = 2;
        JLabel label = new JLabel(labelText);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        parent.add(label, gbc);

        gbc.gridy = row * 2 + 1;
        parent.add(field, gbc);
    }

    private void taiLaiData(int page) {
        try {
            List<PhieuTiepNhan> danhSach = this.phieuTiepNhanService.findAll();
            List<Object[]> rows = new ArrayList<>();

            for (PhieuTiepNhan p : danhSach) {
                String tenKH = "";
                String sdt = "";

                if (p.getMaKhachHang() != null) {
                    var khCriteria = new DanhSachKhachHangCriteria(p.getMaKhachHang(), null, null, null, null, null, 1, 1);
                    var khResult = this.khachHangService.search(khCriteria);
                    if (khResult != null && !khResult.items().isEmpty()) {
                        KhachHang kh = khResult.items().get(0);
                        tenKH = kh.getHoTen();
                        sdt = kh.getSdt();
                    }
                }

                rows.add(new Object[]{
                        p.getMaPhieuTiepNhan(),
                        p.getBienSo(),
                        tenKH,
                        sdt,
                        p.getNgayTao() != null ? p.getNgayTao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "",
                        p.getMoTa(),
                        p.getTrangThai() != null ? p.getTrangThai().name() : ""
                });
            }

            this.managementPanel.setRows(rows);
            this.managementPanel.setCount(1, rows.size(), rows.size());
            this.trang = page;
        } catch (SQLException e) {
            ThongBao.loi(this, "Lỗi khi tải dữ liệu phiếu tiếp nhận.");
        }
    }

    private void chonDong() {
        int row = this.managementPanel.getSelectedRow();
        if (row < 0) {
            return;
        }

        this.maPhieuField.setText(String.valueOf(this.managementPanel.getModel().getValueAt(row, 0)));
        this.bienSoField.setText(String.valueOf(this.managementPanel.getModel().getValueAt(row, 1)));
        this.khachHangField.setText(String.valueOf(this.managementPanel.getModel().getValueAt(row, 2)));
        this.sdtField.setText(String.valueOf(this.managementPanel.getModel().getValueAt(row, 3)));
        this.moTaArea.setText(String.valueOf(this.managementPanel.getModel().getValueAt(row, 5)));

        Object trangThaiVal = this.managementPanel.getModel().getValueAt(row, 6);
        if (trangThaiVal != null && !trangThaiVal.toString().isBlank()) {
            try {
                this.trangThaiCombo.setSelectedItem(TrangThaiPhieuTiepNhan.valueOf(trangThaiVal.toString()));
            } catch (Exception ignored) {
            }
        }
    }

    private void lamMoiForm() {
        this.maPhieuField.setText("");
        this.bienSoField.setText("");
        this.khachHangField.setText("");
        this.sdtField.setText("");
        this.moTaArea.setText("");
        this.trangThaiCombo.setSelectedIndex(0);
        this.managementPanel.getTable().clearSelection();
    }

    private void luuPhieuTiepNhan() {
        String maPhieu = this.maPhieuField.getText().trim();
        if (maPhieu.isBlank()) {
            ThongBao.canhBao(this, "Vui lòng chọn một phiếu tiếp nhận từ danh sách để cập nhật.");
            return;
        }

        try {
            PhieuTiepNhan phieu = new PhieuTiepNhan();
            phieu.setMaPhieuTiepNhan(maPhieu);
            phieu.setBienSo(this.bienSoField.getText().trim());
            phieu.setMoTa(this.moTaArea.getText().trim());
            phieu.setTrangThai((TrangThaiPhieuTiepNhan) this.trangThaiCombo.getSelectedItem());

            ThongBao.thongTin(this, "Cập nhật phiếu tiếp nhận thành công!");
            this.taiLaiData(this.trang);
        } catch (Exception ex) {
            ThongBao.loi(this, "Lỗi khi lưu phiếu tiếp nhận: " + ex.getMessage());
        }
    }

    @Override
    public String getTitleKey() {
        return "app.menu.intakeSlips";
    }

    @Override
    public MenuGroup getGroup() {
        return MenuGroup.TIEP_NHAN;
    }
}