package iuh.fit.gui.page;

import iuh.fit.dto.DanhSachKhachHangCriteria;
import iuh.fit.entity.KhachHang;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.KhachHangService;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;

public class TiepNhanKhieuNaiPage extends AbstractPage {

    private static final long serialVersionUID = 1L;

    // Services
    private final KhachHangService khachHangService = new KhachHangService();

    // Các thành phần UI
    private final JTextField sdtField = new JTextField();
    private final JButton timKhachBtn = new JButton("Tìm Khách");
    private final JTextField hoTenField = new JTextField();
    
    private final JComboBox<String> phieuCombo = new JComboBox<>();
    private final JTextField noiDungBanDauField = new JTextField();
    
    private final JTextArea noiDungKhieuNaiArea = new JTextArea(8, 20);
    
    private final JButton lamMoiBtn = new JButton("Làm Mới");
    private final JButton luuBtn = new JButton("Lưu Khiếu Nại");

    public TiepNhanKhieuNaiPage() {
        this.setLayout(new BorderLayout());
        this.setBackground(Theme.BG);
    }

    @Override
    public void render() {
        this.removeAll();
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Container chính bao bọc toàn bộ Form
        JPanel mainContainer = new JPanel(new BorderLayout(0, 10));
        mainContainer.setBackground(Theme.BG);
        TitledBorder mainBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                "Tiếp Nhận Khiếu Nại"
        );
        mainBorder.setTitleFont(mainBorder.getTitleFont().deriveFont(Font.BOLD, 14f));
        mainContainer.setBorder(BorderFactory.createCompoundBorder(
                mainBorder,
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // -- VÙNG FORM (NORTH) --
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Theme.BG);
        GridBagConstraints gbcForm = new GridBagConstraints();
        gbcForm.fill = GridBagConstraints.BOTH;
        gbcForm.weightx = 0.5; // Chia đều 50% cho 2 cột
        gbcForm.insets = new Insets(0, 0, 10, 10);

        // 1. Cột Khách Hàng (Trái)
        gbcForm.gridx = 0;
        gbcForm.gridy = 0;
        formPanel.add(this.createKhachHangPanel(), gbcForm);

        // 2. Cột Phiếu Tiếp Nhận (Phải)
        gbcForm.gridx = 1;
        gbcForm.insets = new Insets(0, 0, 10, 0); // Bỏ margin phải ở cột cuối
        formPanel.add(this.createPhieuTiepNhanPanel(), gbcForm);

        // 3. Vùng Nội Dung Khiếu Nại (Dưới cùng, chiếm 2 cột)
        gbcForm.gridx = 0;
        gbcForm.gridy = 1;
        gbcForm.gridwidth = 2;
        gbcForm.weighty = 1.0;
        formPanel.add(this.createNoiDungPanel(), gbcForm);

        mainContainer.add(formPanel, BorderLayout.CENTER);

        // -- VÙNG NÚT BẤM (SOUTH) --
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Theme.BG);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER)); // Đường line mờ phân cách
        buttonPanel.add(Box.createVerticalStrut(50)); // Tạo khoảng cách với đường line

        this.lamMoiBtn.setPreferredSize(new Dimension(100, 32));
        this.luuBtn.setPreferredSize(new Dimension(130, 32));
        this.luuBtn.setBackground(Theme.ACCENT);
        this.luuBtn.setForeground(Theme.PANEL);

        buttonPanel.add(this.lamMoiBtn);
        buttonPanel.add(this.luuBtn);

        mainContainer.add(buttonPanel, BorderLayout.SOUTH);

        // --- Gắn sự kiện (Action Listeners) ---
        this.timKhachBtn.addActionListener(e -> this.timKhachHang());

        this.lamMoiBtn.addActionListener(e -> {
            this.sdtField.setText("");
            this.hoTenField.setText("");
            this.phieuCombo.removeAllItems();
            this.noiDungBanDauField.setText("");
            this.noiDungKhieuNaiArea.setText("");
        });

        this.add(mainContainer, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
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
            }
        } catch (SQLException ex) {
            ThongBao.loi(this, "Lỗi kết nối cơ sở dữ liệu khi tìm khách hàng.");
        } catch (Exception ex) {
            ThongBao.loi(this, "Lỗi hệ thống: " + ex.getMessage());
        }
    }

    private JPanel createKhachHangPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Theme.BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.weightx = 1.0;

        // Tiêu đề
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lblTitle = new JLabel("1. KHÁCH HÀNG KHIẾU NẠI");
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD));
        panel.add(lblTitle, gbc);

        // Khoảng cách
        gbc.gridy = 1;
        panel.add(Box.createVerticalStrut(5), gbc);

        // Số điện thoại (*)
        gbc.gridy = 2; gbc.gridwidth = 2;
        JLabel lblSdt = new JLabel("Số điện thoại khách hàng (*):");
        lblSdt.setFont(lblSdt.getFont().deriveFont(Font.BOLD));
        panel.add(lblSdt, gbc);

        // Nhập SĐT + Nút Tìm Khách
        gbc.gridy = 3; gbc.gridwidth = 1;
        panel.add(this.sdtField, gbc);

        gbc.gridx = 1; gbc.weightx = 0; gbc.insets = new Insets(0, 5, 5, 0);
        this.timKhachBtn.setBackground(Theme.ACCENT);
        this.timKhachBtn.setForeground(Theme.PANEL);
        panel.add(this.timKhachBtn, gbc);

        // Họ tên khách hàng
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.insets = new Insets(10, 0, 5, 0);
        JLabel lblHoTen = new JLabel("Họ và tên khách hàng:");
        lblHoTen.setFont(lblHoTen.getFont().deriveFont(Font.BOLD));
        panel.add(lblHoTen, gbc);

        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 0, 0);
        this.hoTenField.setEditable(false);
        panel.add(this.hoTenField, gbc);

        return panel;
    }

    private JPanel createPhieuTiepNhanPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Theme.BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.weightx = 1.0;

        // Tiêu đề
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblTitle = new JLabel("2. PHIẾU TIẾP NHẬN LIÊN QUAN");
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD));
        panel.add(lblTitle, gbc);

        // Khoảng cách
        gbc.gridy = 1;
        panel.add(Box.createVerticalStrut(5), gbc);

        // Phiếu tiếp nhận khiếu nại (*)
        gbc.gridy = 2;
        JLabel lblPhieu = new JLabel("Phiếu tiếp nhận khiếu nại (*):");
        lblPhieu.setFont(lblPhieu.getFont().deriveFont(Font.BOLD));
        panel.add(lblPhieu, gbc);

        gbc.gridy = 3;
        panel.add(this.phieuCombo, gbc);

        // Nội dung yêu cầu ban đầu
        gbc.gridy = 4; gbc.insets = new Insets(10, 0, 5, 0);
        JLabel lblNoiDung = new JLabel("Nội dung yêu cầu ban đầu:");
        lblNoiDung.setFont(lblNoiDung.getFont().deriveFont(Font.BOLD));
        panel.add(lblNoiDung, gbc);

        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 0, 0);
        this.noiDungBanDauField.setEditable(false);
        panel.add(this.noiDungBanDauField, gbc);

        return panel;
    }

    private JPanel createNoiDungPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(Theme.BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel lblNoiDungKhieuNai = new JLabel("Nội Dung Khiếu Nại (*):");
        lblNoiDungKhieuNai.setFont(lblNoiDungKhieuNai.getFont().deriveFont(Font.BOLD));
        
        this.noiDungKhieuNaiArea.setLineWrap(true);
        this.noiDungKhieuNaiArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(this.noiDungKhieuNaiArea);
        
        panel.add(lblNoiDungKhieuNai, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    @Override
    public String getTitleKey() {
        return "app.menu.complaints";
    }
    
    @Override
    public MenuGroup getGroup() {
        return MenuGroup.TIEP_NHAN;
    }
}