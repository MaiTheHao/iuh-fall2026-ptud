package iuh.fit.gui.page;

import iuh.fit.dto.KhieuNaiFormData;
import iuh.fit.entity.KhachHang;
import iuh.fit.entity.KhieuNai;
import iuh.fit.entity.PhieuTiepNhan;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.KhachHangService;
import iuh.fit.service.KhieuNaiService;
import iuh.fit.service.PhieuTiepNhanService;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tiếp nhận khiếu nại của khách hàng dựa trên số điện thoại và phiếu tiếp nhận liên quan.
 */
public class TiepNhanKhieuNaiPage extends AbstractPage {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(TiepNhanKhieuNaiPage.class.getName());

    // Services
    private final KhachHangService khachHangService = new KhachHangService();
    private final KhieuNaiService khieuNaiService = new KhieuNaiService();
    private final PhieuTiepNhanService phieuTiepNhanService = new PhieuTiepNhanService();
    private KhachHang khachHangDangChon;

    // Các thành phần UI
    private final JTextField sdtField = new JTextField();
    private final JButton timKhachBtn = new JButton();
    private final JTextField hoTenField = new JTextField();

    private final JComboBox<PhieuTiepNhan> phieuCombo = new JComboBox<>();
    private final JTextField noiDungBanDauField = new JTextField();

    private final JTextArea noiDungKhieuNaiArea = new JTextArea(8, 20);

    private final JButton lamMoiBtn = new JButton();
    private final JButton luuBtn = new JButton();

    private boolean dangCapNhat;

    public TiepNhanKhieuNaiPage() {
        this.setLayout(new BorderLayout());
        this.setBackground(Theme.BG);
        this.sdtField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                TiepNhanKhieuNaiPage.this.xuLyThayDoiSdt();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                TiepNhanKhieuNaiPage.this.xuLyThayDoiSdt();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                TiepNhanKhieuNaiPage.this.xuLyThayDoiSdt();
            }
        });
    }

    @Override
    public void render() {
        this.removeAll();
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        this.timKhachBtn.setText(I18nReaderUtil.get("app.khieuNai.timKhach"));
        this.lamMoiBtn.setText(I18nReaderUtil.get("app.khieuNai.lamMoi"));
        this.luuBtn.setText(I18nReaderUtil.get("app.khieuNai.luu"));

        this.phieuCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof PhieuTiepNhan phieu) {
                    this.setText(phieu.getMaPhieuTiepNhan() + " — " + phieu.getBienSo());
                } else {
                    this.setText("");
                }
                return this;
            }
        });

        // Container chính bao bọc toàn bộ Form
        JPanel mainContainer = new JPanel(new BorderLayout(0, 10));
        mainContainer.setOpaque(false);
        TitledBorder mainBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                I18nReaderUtil.get("app.khieuNai.tiepNhanTitle")
        );
        mainBorder.setTitleFont(mainBorder.getTitleFont().deriveFont(Font.BOLD, 14f));
        mainBorder.setTitleColor(Theme.chuTrenAnh());
        mainContainer.setBorder(BorderFactory.createCompoundBorder(
                mainBorder,
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // -- VÙNG FORM (NORTH) --
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
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
        buttonPanel.setOpaque(false);
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
        this.clearActionListeners(this.timKhachBtn, this.lamMoiBtn, this.luuBtn);
        this.clearActionListeners(this.phieuCombo);

        this.timKhachBtn.addActionListener(e -> this.timKhachHang());

        this.phieuCombo.addActionListener(e -> this.taiNoiDungBanDau());

        this.lamMoiBtn.addActionListener(e -> this.lamMoi());

        this.luuBtn.addActionListener(e -> this.luuKhieuNai());

        this.add(mainContainer, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

    private void xuLyThayDoiSdt() {
        if (this.dangCapNhat) {
            return;
        }
        this.khachHangDangChon = null;
        this.hoTenField.setText("");
        this.phieuCombo.removeAllItems();
        this.noiDungBanDauField.setText("");
    }

    private void timKhachHang() {
        String sdt = this.sdtField.getText().trim();
        if (sdt.isEmpty()) {
            ThongBao.canhBao(this, "app.khieuNai.loiSdt");
            this.sdtField.requestFocusInWindow();
            return;
        }

        try {
            var found = this.khachHangService.getBySdt(sdt);
            if (found.isEmpty()) {
                ThongBao.thongTin(this, "app.khieuNai.khongTimThayKhach");
                this.khachHangDangChon = null;
                this.hoTenField.setText("");
                this.phieuCombo.removeAllItems();
                this.noiDungBanDauField.setText("");
                this.hoTenField.requestFocusInWindow();
            } else {
                this.khachHangDangChon = found.get();
                this.hoTenField.setText(this.khachHangDangChon.getHoTen());
                this.taiDanhSachPhieu();
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tìm khách hàng theo số điện thoại", ex);
            ThongBao.loi(this, "app.khieuNai.loiKetNoi");
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Lỗi hệ thống khi tìm khách hàng", ex);
            ThongBao.loi(this, "app.tiepNhan.loiHeThong");
        }
    }

    private void taiDanhSachPhieu() {
        this.phieuCombo.removeAllItems();
        this.noiDungBanDauField.setText("");
        if (this.khachHangDangChon == null) {
            return;
        }
        try {
            for (PhieuTiepNhan p : this.phieuTiepNhanService.getDanhSachTheoKhachHang(
                    this.khachHangDangChon.getMaKhachHang())) {
                this.phieuCombo.addItem(p);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tải danh sách phiếu tiếp nhận của khách hàng", ex);
            ThongBao.loi(this, "app.khieuNai.loiTaiDanhSach");
        }
    }

    private void taiNoiDungBanDau() {
        Object chon = this.phieuCombo.getSelectedItem();
        if (!(chon instanceof PhieuTiepNhan phieu)) {
            this.noiDungBanDauField.setText("");
            return;
        }
        try {
            this.noiDungBanDauField.setText(
                    this.phieuTiepNhanService.getById(phieu.getMaPhieuTiepNhan()).getMoTa());
        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.SEVERE, "Không tìm thấy phiếu tiếp nhận đã chọn", ex);
            this.noiDungBanDauField.setText("");
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tải nội dung yêu cầu ban đầu của phiếu tiếp nhận", ex);
            this.noiDungBanDauField.setText("");
        }
    }

    private boolean kiemTraForm() {
        if (this.khachHangDangChon == null) {
            ThongBao.loi(this, "app.khieuNai.loiKhachHang");
            return false;
        }
        if (!(this.phieuCombo.getSelectedItem() instanceof PhieuTiepNhan)) {
            ThongBao.loi(this, "app.khieuNai.loiPhieu");
            return false;
        }
        if (this.noiDungKhieuNaiArea.getText().trim().isEmpty()) {
            ThongBao.loi(this, "app.khieuNai.loiNoiDung");
            return false;
        }
        return true;
    }

    private void luuKhieuNai() {
        try {
            if (!this.kiemTraForm()) {
                return;
            }
            KhieuNai khieuNai = this.khieuNaiService.create(new KhieuNaiFormData(
                    this.khachHangDangChon.getMaKhachHang(),
                    ((PhieuTiepNhan) this.phieuCombo.getSelectedItem()).getMaPhieuTiepNhan(),
                    this.noiDungKhieuNaiArea.getText().trim()));
            ThongBao.thongTin(this, "app.khieuNai.luuThanhCong", khieuNai.getMaKhieuNai());
            this.lamMoi();
        } catch (IllegalArgumentException ex) {
            ThongBao.loi(this, ex);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lưu khiếu nại", ex);
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }

    private void lamMoi() {
        this.dangCapNhat = true;
        try {
            this.sdtField.setText("");
        } finally {
            this.dangCapNhat = false;
        }
        this.hoTenField.setText("");
        this.khachHangDangChon = null;
        this.phieuCombo.removeAllItems();
        this.noiDungBanDauField.setText("");
        this.noiDungKhieuNaiArea.setText("");
    }

    private JPanel createKhachHangPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
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
        JLabel lblTitle = new JLabel(I18nReaderUtil.get("app.khieuNai.khachHangTitle"));
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD));
        lblTitle.setForeground(Theme.chuTrenAnh());
        panel.add(lblTitle, gbc);

        // Khoảng cách
        gbc.gridy = 1;
        panel.add(Box.createVerticalStrut(5), gbc);

        // Số điện thoại (*)
        gbc.gridy = 2; gbc.gridwidth = 2;
        JLabel lblSdt = new JLabel(I18nReaderUtil.get("app.khieuNai.sdt"));
        lblSdt.setFont(lblSdt.getFont().deriveFont(Font.BOLD));
        lblSdt.setForeground(Theme.chuTrenAnh());
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
        JLabel lblHoTen = new JLabel(I18nReaderUtil.get("app.khieuNai.hoTen"));
        lblHoTen.setFont(lblHoTen.getFont().deriveFont(Font.BOLD));
        lblHoTen.setForeground(Theme.chuTrenAnh());
        panel.add(lblHoTen, gbc);

        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 0, 0);
        Theme.applyReadOnly(this.hoTenField);
        panel.add(this.hoTenField, gbc);

        return panel;
    }

    private JPanel createPhieuTiepNhanPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
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
        JLabel lblTitle = new JLabel(I18nReaderUtil.get("app.khieuNai.phieuTitle"));
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD));
        lblTitle.setForeground(Theme.chuTrenAnh());
        panel.add(lblTitle, gbc);

        // Khoảng cách
        gbc.gridy = 1;
        panel.add(Box.createVerticalStrut(5), gbc);

        // Phiếu tiếp nhận khiếu nại (*)
        gbc.gridy = 2;
        JLabel lblPhieu = new JLabel(I18nReaderUtil.get("app.khieuNai.phieuTiepNhan") + " (*):");
        lblPhieu.setFont(lblPhieu.getFont().deriveFont(Font.BOLD));
        lblPhieu.setForeground(Theme.chuTrenAnh());
        panel.add(lblPhieu, gbc);

        gbc.gridy = 3;
        panel.add(this.phieuCombo, gbc);

        // Nội dung yêu cầu ban đầu
        gbc.gridy = 4; gbc.insets = new Insets(10, 0, 5, 0);
        JLabel lblNoiDung = new JLabel(I18nReaderUtil.get("app.khieuNai.noiDungBanDau"));
        lblNoiDung.setFont(lblNoiDung.getFont().deriveFont(Font.BOLD));
        lblNoiDung.setForeground(Theme.chuTrenAnh());
        panel.add(lblNoiDung, gbc);

        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 0, 0);
        Theme.applyReadOnly(this.noiDungBanDauField);
        panel.add(this.noiDungBanDauField, gbc);

        return panel;
    }

    private JPanel createNoiDungPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel lblNoiDungKhieuNai = new JLabel(I18nReaderUtil.get("app.khieuNai.noiDungKhieuNai"));
        lblNoiDungKhieuNai.setFont(lblNoiDungKhieuNai.getFont().deriveFont(Font.BOLD));
        lblNoiDungKhieuNai.setForeground(Theme.chuTrenAnh());

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

    @Override
    public boolean hasSeparatorBefore() {
        return true;
    }
}
