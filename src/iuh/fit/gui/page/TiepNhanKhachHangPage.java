package iuh.fit.gui.page;

import iuh.fit.dto.DanhSachKhachHangCriteria;
import iuh.fit.dto.DanhSachXeCriteria;
import iuh.fit.entity.KhachHang;
import iuh.fit.entity.Xe;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.theme.Theme;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.service.KhachHangService;
import iuh.fit.service.PhieuTiepNhanService;
import iuh.fit.service.XeService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.SQLException;

public class TiepNhanKhachHangPage extends AbstractPage {

    private static final long serialVersionUID = 1L;

    private final KhachHangService khachHangService = new KhachHangService();
    private final XeService xeService = new XeService();
    private final PhieuTiepNhanService phieuTiepNhanService = new PhieuTiepNhanService();

    private final JTextField sdtField = new JTextField();
    private final JTextField hoTenField = new JTextField();
    private final JTextField bienSoField = new JTextField();
    private final JTextField hangXeField = new JTextField();
    private final JTextField dongXeField = new JTextField();
    private final JTextField odoField = new JTextField();
    private final JTextArea moTaArea = new JTextArea(5, 50);

    private final JButton timKhachButton = new JButton("Tìm Khách");
    private final JButton timXeButton = new JButton("Tìm Xe");
    private final JButton lamMoiButton = new JButton("Làm Mới");
    private final JButton luuButton = new JButton("Lưu Phiếu Tiếp Nhận");

    public TiepNhanKhachHangPage() {
        this.setLayout(new BorderLayout());
        this.setBackground(Theme.BG);
    }

    @Override
    public void render() {
        this.removeAll();
        this.setLayout(new BorderLayout(10, 10));

        JPanel contentPanel = Theme.contentPanel(10);
        contentPanel.setLayout(new BorderLayout(10, 10));

        // Top Panel: Khách Hàng & Phương Tiện
        JPanel pnlTop = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlTop.setOpaque(false);

        // 1. Thông tin Khách Hàng
        JPanel pnlKhachHang = this.createTitledPanel("1. THÔNG TIN KHÁCH HÀNG");
        pnlKhachHang.setLayout(new GridBagLayout());
        GridBagConstraints gbcKH = this.createGbc();

        this.addFormField(pnlKhachHang, "Số điện thoại khách hàng (*):", this.sdtField, this.timKhachButton, gbcKH, 0);
        this.addFormField(pnlKhachHang, "Họ và tên khách hàng (*):", this.hoTenField, null, gbcKH, 1);
        pnlTop.add(pnlKhachHang);

        // 2. Thông tin Phương Tiện (Xe)
        JPanel pnlPhuongTien = this.createTitledPanel("2. THÔNG TIN PHƯƠNG TIỆN (XE)");
        pnlPhuongTien.setLayout(new GridBagLayout());
        GridBagConstraints gbcXe = this.createGbc();

        this.addFormField(pnlPhuongTien, "Biển số xe (*):", this.bienSoField, this.timXeButton, gbcXe, 0);

        JPanel pnlChiTietXe = new JPanel(new GridLayout(1, 3, 10, 0));
        pnlChiTietXe.setOpaque(false);
        pnlChiTietXe.add(this.createFieldWithLabel("Hãng xe:", this.hangXeField));
        pnlChiTietXe.add(this.createFieldWithLabel("Dòng xe:", this.dongXeField));
        pnlChiTietXe.add(this.createFieldWithLabel("Số ODO hiện tại (Km) (*):", this.odoField));

        gbcXe.gridx = 0;
        gbcXe.gridy = 1;
        gbcXe.gridwidth = 3;
        gbcXe.fill = GridBagConstraints.HORIZONTAL;
        pnlPhuongTien.add(pnlChiTietXe, gbcXe);
        pnlTop.add(pnlPhuongTien);

        // Center Panel: Mô tả tình trạng xe
        JPanel pnlMoTa = new JPanel(new BorderLayout(0, 5));
        pnlMoTa.setOpaque(false);
        pnlMoTa.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel lblMoTa = new JLabel("Mô Tả Tình Trạng Xe & Yêu Cầu Của Khách Hàng (*):");
        lblMoTa.setFont(lblMoTa.getFont().deriveFont(Font.BOLD));

        this.moTaArea.setLineWrap(true);
        this.moTaArea.setWrapStyleWord(true);
        pnlMoTa.add(lblMoTa, BorderLayout.NORTH);
        pnlMoTa.add(new JScrollPane(this.moTaArea), BorderLayout.CENTER);

        // Bottom Panel: Buttons
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pnlButtons.setOpaque(false);

        this.luuButton.setBackground(Theme.ACCENT);
        this.luuButton.setForeground(Theme.PANEL);
        this.timKhachButton.setBackground(Theme.ACCENT);
        this.timKhachButton.setForeground(Theme.PANEL);
        this.timXeButton.setBackground(Theme.ACCENT);
        this.timXeButton.setForeground(Theme.PANEL);

        pnlButtons.add(this.lamMoiButton);
        pnlButtons.add(this.luuButton);

        contentPanel.add(pnlTop, BorderLayout.NORTH);
        contentPanel.add(pnlMoTa, BorderLayout.CENTER);
        contentPanel.add(pnlButtons, BorderLayout.SOUTH);

        this.add(contentPanel, BorderLayout.CENTER);

        // Xóa hết Listener cũ tránh lặp sự kiện
        this.clearListeners();

        // Đăng ký đúng Action Listeners
        this.lamMoiButton.addActionListener(e -> this.lamMoiForm());
        this.timKhachButton.addActionListener(e -> this.timKhachHang());
        this.timXeButton.addActionListener(e -> this.timXe());
        this.luuButton.addActionListener(e -> this.luuPhieuTiepNhan());

        this.revalidate();
        this.repaint();
    }

	private void clearListeners() {
        for (var al : this.lamMoiButton.getActionListeners()) this.lamMoiButton.removeActionListener(al);
        for (var al : this.timKhachButton.getActionListeners()) this.timKhachButton.removeActionListener(al);
        for (var al : this.timXeButton.getActionListeners()) this.timXeButton.removeActionListener(al);
        for (var al : this.luuButton.getActionListeners()) this.luuButton.removeActionListener(al);
    }

    private void timKhachHang() {
        String sdt = this.sdtField.getText().trim();
        if (sdt.isEmpty()) {
            ThongBao.canhBao(this, "Vui lòng nhập số điện thoại khách hàng để tìm kiếm.");
            this.sdtField.requestFocusInWindow();
            return;
        }

        try {
            DanhSachKhachHangCriteria criteria = new DanhSachKhachHangCriteria(null, sdt, null, null, null, null, 1, 10);
            var result = this.khachHangService.search(criteria);
            
            if (result == null || result.items() == null || result.items().isEmpty()) {
                ThongBao.thongTin(this, "Không tìm thấy khách hàng. Vui lòng nhập thông tin mới.");
                this.hoTenField.setText("");
                this.hoTenField.requestFocusInWindow();
            } else {
                KhachHang khachHang = result.items().get(0);
                this.hoTenField.setText(khachHang.getHoTen());
                this.bienSoField.requestFocusInWindow();
            }
        } catch (SQLException ex) {
            ThongBao.loi(this, "Lỗi kết nối cơ sở dữ liệu khi tìm khách hàng.");
        } catch (Exception ex) {
            ThongBao.loi(this, "Lỗi hệ thống: " + ex.getMessage());
        }
    }

    private void timXe() {
        String rawBienSo = this.bienSoField.getText().trim();
        if (rawBienSo.isEmpty()) {
            ThongBao.canhBao(this, "Vui lòng nhập biển số xe để tìm kiếm.");
            this.bienSoField.requestFocusInWindow();
            return;
        }

        // Chuẩn hóa biển số: Viết hoa, xóa khoảng trắng thừa
        String bienSoChuanHoa = rawBienSo.toUpperCase().replaceAll("\\s+", "");

        try {
        	DanhSachXeCriteria criteria = new DanhSachXeCriteria(rawBienSo, null, null, null, null, null, null, null, 1, 10);
            var result = this.xeService.search(criteria);
            
            if(result == null || result.items() == null || result.items().isEmpty()) {
            	ThongBao.thongTin(this, "Biển số xe (" + bienSoChuanHoa + ") chưa có trong hệ thống. Vui lòng nhập thông tin xe mới.");
                this.bienSoField.setText(bienSoChuanHoa);
                this.hangXeField.setText("");
                this.dongXeField.setText("");
                this.odoField.setText("");
                this.hangXeField.requestFocusInWindow();
            } else {
            	Xe xe = result.items().get(0);
            	this.bienSoField.setText(xe.getBienSo() != null ? xe.getBienSo() : bienSoChuanHoa);
                this.hangXeField.setText(xe.getHangXe() != null ? xe.getHangXe() : "");
                this.dongXeField.setText(xe.getDongXe() != null ? xe.getDongXe() : "");
            	this.odoField.setText(String.valueOf(xe.getOdo()));
            	this.odoField.requestFocusInWindow();
            }
        } catch (SQLException ex) {
            ThongBao.loi(this, "Lỗi kết nối cơ sở dữ liệu khi tìm xe.");
        } catch (Exception ex) {
            ThongBao.loi(this, "Lỗi hệ thống: " + ex.getMessage());
        }
    }
    
    private void luuPhieuTiepNhan() {
        if (!this.kiemTraForm()) {
            return;
        }

    }

    private JPanel createTitledPanel(String title) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Theme.BORDER), title);
        border.setTitleFont(border.getTitleFont().deriveFont(Font.BOLD));
        panel.setBorder(border);
        return panel;
    }

    private GridBagConstraints createGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private void addFormField(JPanel parent, String label, JTextField field, JButton btn, GridBagConstraints gbc, int y) {
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.weightx = 0;
        gbc.gridwidth = 1;

        JLabel lbl = new JLabel(label);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
        parent.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        parent.add(field, gbc);

        if (btn != null) {
            gbc.gridx = 2;
            gbc.weightx = 0;
            parent.add(btn, gbc);
        }
    }

    private JPanel createFieldWithLabel(String labelText, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private void lamMoiForm() {
        this.sdtField.setText("");
        this.hoTenField.setText("");
        this.bienSoField.setText("");
        this.hangXeField.setText("");
        this.dongXeField.setText("");
        this.odoField.setText("");
        this.moTaArea.setText("");
        this.sdtField.requestFocusInWindow();
    }

    private boolean kiemTraForm() {
        if (this.sdtField.getText().trim().isEmpty()) {
            ThongBao.thongTin(this, "Vui lòng nhập số điện thoại khách hàng.");
            this.sdtField.requestFocusInWindow();
            return false;
        }
        if (this.hoTenField.getText().trim().isEmpty()) {
            ThongBao.thongTin(this, "Vui lòng nhập họ tên khách hàng.");
            this.hoTenField.requestFocusInWindow();
            return false;
        }
        if (this.bienSoField.getText().trim().isEmpty()) {
            ThongBao.thongTin(this, "Vui lòng nhập biển số xe.");
            this.bienSoField.requestFocusInWindow();
            return false;
        }
        if (this.odoField.getText().trim().isEmpty()) {
            ThongBao.thongTin(this, "Vui lòng nhập số ODO hiện tại.");
            this.odoField.requestFocusInWindow();
            return false;
        }
        try {
            long odo = Long.parseLong(this.odoField.getText().trim());
            if (odo < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            ThongBao.thongTin(this, "Số ODO phải là số nguyên không âm.");
            this.odoField.requestFocusInWindow();
            return false;
        }
        if (this.moTaArea.getText().trim().isEmpty()) {
            ThongBao.thongTin(this, "Vui lòng mô tả tình trạng xe.");
            this.moTaArea.requestFocusInWindow();
            return false;
        }
        return true;
    }

    @Override
    public String getTitleKey() {
        return "app.menu.customerIntake";
    }

    @Override
    public MenuGroup getGroup() {
        return MenuGroup.TIEP_NHAN;
    }
}