package iuh.fit.gui.page;

import iuh.fit.dto.DanhSachPhieuTiepNhanCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.dto.PhieuTiepNhanView;
import iuh.fit.entity.PhieuTiepNhan;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.enumeration.TrangThaiPhieuTiepNhan;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.PhieuTiepNhanService;
import iuh.fit.util.I18nReaderUtil;

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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QuanLyPhieuTiepNhanPage extends AbstractPage {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(QuanLyPhieuTiepNhanPage.class.getName());

    private static final int SO_DONG_MOI_TRANG = 20;

    private final PhieuTiepNhanService phieuTiepNhanService = new PhieuTiepNhanService();

    // Form fields (Left Panel)
    private final JTextField maPhieuField = new JTextField();
    private final JTextField bienSoField = new JTextField();
    private final JTextField khachHangField = new JTextField();
    private final JTextField sdtField = new JTextField();
    private final JTextArea moTaArea = new JTextArea(4, 20);
    private final JComboBox<TrangThaiPhieuTiepNhan> trangThaiCombo = new JComboBox<>(TrangThaiPhieuTiepNhan.values());

    // Left Bottom Buttons
    private final JButton themButton = new JButton();
    private final JButton luuButton = new JButton();
    private final JButton huyButton = new JButton();
    private final JButton xoaTrangButton = new JButton();

    private ManagementPanel managementPanel;
    private List<PhieuTiepNhanView> danhSachHienTai = new ArrayList<>();
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
                I18nReaderUtil.get("app.phieuTiepNhan.danhSachTitle")
        );
        rightBorder.setTitleFont(rightBorder.getTitleFont().deriveFont(Font.BOLD));
        this.managementPanel.setBorder(rightBorder);

        this.managementPanel.setHeaders(
                I18nReaderUtil.get("app.phieuTiepNhan.ma"),
                I18nReaderUtil.get("app.xe.bienSo"),
                I18nReaderUtil.get("app.khachHang.hoTen"),
                I18nReaderUtil.get("app.khachHang.sdt"),
                I18nReaderUtil.get("app.phieuTiepNhan.thoiGianVao"),
                I18nReaderUtil.get("app.phieuTiepNhan.noiDung"),
                I18nReaderUtil.get("app.phieuTiepNhan.trangThai")
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
        this.themButton.setText(I18nReaderUtil.get("app.common.add"));
        this.luuButton.setText(I18nReaderUtil.get("app.phieuTiepNhan.luu"));
        this.huyButton.setText(I18nReaderUtil.get("app.phieuTiepNhan.huyPhieu"));
        this.xoaTrangButton.setText(I18nReaderUtil.get("app.phieuTiepNhan.xoaTrang"));

        this.clearActionListeners(this.themButton, this.luuButton, this.huyButton, this.xoaTrangButton);

        this.themButton.addActionListener(e -> this.goTo(TiepNhanKhachHangPage.class));
        this.luuButton.addActionListener(e -> this.luuPhieuTiepNhan());
        this.huyButton.addActionListener(e -> this.huyPhieu());
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
                I18nReaderUtil.get("app.phieuTiepNhan.thongTinTitle")
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
        this.bienSoField.setEditable(false);
        this.khachHangField.setEditable(false);
        this.sdtField.setEditable(false);

        this.addFormRow(formContent, "app.phieuTiepNhan.maPhieu", this.maPhieuField, gbc, 0);
        this.addFormRow(formContent, "app.phieuTiepNhan.bienSo", this.bienSoField, gbc, 1);
        this.addFormRow(formContent, "app.phieuTiepNhan.khachHang", this.khachHangField, gbc, 2);
        this.addFormRow(formContent, "app.phieuTiepNhan.dienThoai", this.sdtField, gbc, 3);

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        JLabel lblMoTa = new JLabel(I18nReaderUtil.get("app.phieuTiepNhan.noiDungYeuCau"));
        lblMoTa.setFont(lblMoTa.getFont().deriveFont(Font.BOLD));
        formContent.add(lblMoTa, gbc);

        gbc.gridy = 9;
        this.moTaArea.setLineWrap(true);
        this.moTaArea.setWrapStyleWord(true);
        JScrollPane scrollMoTa = new JScrollPane(this.moTaArea);
        scrollMoTa.setPreferredSize(new Dimension(200, 100));
        formContent.add(scrollMoTa, gbc);

        gbc.gridy = 10;
        JLabel lblTrangThai = new JLabel(I18nReaderUtil.get("app.phieuTiepNhan.trangThaiTienDo"));
        lblTrangThai.setFont(lblTrangThai.getFont().deriveFont(Font.BOLD));
        formContent.add(lblTrangThai, gbc);

        gbc.gridy = 11;
        formContent.add(this.trangThaiCombo, gbc);

        panel.add(formContent, BorderLayout.NORTH);
        return panel;
    }

    private JPanel createLeftButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 6, 6));
        panel.setOpaque(false);

        this.luuButton.setBackground(Theme.ACCENT);
        this.luuButton.setForeground(Theme.PANEL);

        Theme.applyDanger(this.huyButton);

        panel.add(this.themButton);
        panel.add(this.luuButton);
        panel.add(this.huyButton);
        panel.add(this.xoaTrangButton);

        return panel;
    }

    private void addFormRow(JPanel parent, String labelKey, JTextField field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0;
        gbc.gridy = row * 2;
        gbc.gridwidth = 2;
        JLabel label = new JLabel(I18nReaderUtil.get(labelKey));
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        parent.add(label, gbc);

        gbc.gridy = row * 2 + 1;
        parent.add(field, gbc);
    }

    private void taiLaiData(int page) {
        try {
            TrangThaiPhieuTiepNhan trangThai = this.managementPanel.getComboFilterValue(6)
                    instanceof TrangThaiPhieuTiepNhan value ? value : null;

            DanhSachPhieuTiepNhanCriteria tieuChi = new DanhSachPhieuTiepNhanCriteria(
                    this.managementPanel.getTextFilter(0),
                    this.managementPanel.getTextFilter(1),
                    this.managementPanel.getTextFilter(2),
                    this.managementPanel.getTextFilter(3),
                    this.managementPanel.getTextFilter(5),
                    trangThai,
                    this.managementPanel.getDateFilterFrom(4),
                    this.managementPanel.getDateFilterTo(4),
                    this.managementPanel.getQuickFilter(),
                    Math.max(page, 1),
                    SO_DONG_MOI_TRANG
            );

            PaginatedResult<PhieuTiepNhanView> ketQua = this.phieuTiepNhanService.search(tieuChi);

            List<Object[]> rows = new ArrayList<>();
            for (PhieuTiepNhanView view : ketQua.items()) {
                rows.add(new Object[]{
                        view.maPhieuTiepNhan(),
                        view.bienSo(),
                        view.tenKhachHang(),
                        view.sdt(),
                        view.ngayTao() != null ? view.ngayTao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "",
                        view.moTa(),
                        view.trangThai() == null ? "" : view.trangThai().getI18NBusinessName()
                });
            }

            this.danhSachHienTai = ketQua.items();

            this.managementPanel.setRows(rows);
            this.managementPanel.setCount(ketQua.fromIndex(), ketQua.toIndex(), ketQua.totalItems());
            this.managementPanel.setPage(ketQua.page(), Math.max(ketQua.totalPages(), 1));
            this.trang = ketQua.page();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Khong the tai danh sach phieu tiep nhan", e);
            ThongBao.loi(this, "app.phieuTiepNhan.loiTaiDanhSach");
        }
    }

    private void chonDong() {
        int row = this.managementPanel.getSelectedRow();
        if (row < 0 || row >= this.danhSachHienTai.size()) {
            return;
        }

        PhieuTiepNhanView view = this.danhSachHienTai.get(row);

        this.maPhieuField.setText(view.maPhieuTiepNhan() == null ? "" : view.maPhieuTiepNhan());
        this.bienSoField.setText(view.bienSo() == null ? "" : view.bienSo());
        this.khachHangField.setText(view.tenKhachHang() == null ? "" : view.tenKhachHang());
        this.sdtField.setText(view.sdt() == null ? "" : view.sdt());
        this.moTaArea.setText(view.moTa() == null ? "" : view.moTa());
        this.trangThaiCombo.setSelectedItem(view.trangThai());

        this.huyButton.setEnabled(view.trangThai() != TrangThaiPhieuTiepNhan.DA_HUY);
    }

    private void lamMoiForm() {
        this.maPhieuField.setText("");
        this.bienSoField.setText("");
        this.khachHangField.setText("");
        this.sdtField.setText("");
        this.moTaArea.setText("");
        this.trangThaiCombo.setSelectedIndex(0);
        this.managementPanel.getTable().clearSelection();
        this.huyButton.setEnabled(true);
    }

    private void luuPhieuTiepNhan() {
        String maPhieu = this.maPhieuField.getText().trim();
        if (maPhieu.isBlank()) {
            ThongBao.canhBao(this, "app.phieuTiepNhan.loiMa");
            return;
        }

        try {
            PhieuTiepNhan phieu = new PhieuTiepNhan();
            phieu.setMaPhieuTiepNhan(maPhieu);
            phieu.setMoTa(this.moTaArea.getText().trim());
            phieu.setTrangThai((TrangThaiPhieuTiepNhan) this.trangThaiCombo.getSelectedItem());

            this.phieuTiepNhanService.update(phieu);

            ThongBao.thongTin(this, "app.common.update_succeed");
            this.taiLaiData(this.trang);
        } catch (IllegalArgumentException ex) {
            ThongBao.loi(this, ex);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Khong the cap nhat phieu tiep nhan", ex);
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }

    private void huyPhieu() {
        if (this.maPhieuField.getText().trim().isBlank()) {
            ThongBao.canhBao(this, "app.phieuTiepNhan.loiMa");
            return;
        }

        if (!ThongBao.xacNhan(this, "app.phieuTiepNhan.xacNhanHuy", "app.phieuTiepNhan.tieuDeHuy")) {
            return;
        }

        this.trangThaiCombo.setSelectedItem(TrangThaiPhieuTiepNhan.DA_HUY);
        this.luuPhieuTiepNhan();
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
