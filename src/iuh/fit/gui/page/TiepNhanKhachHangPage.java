package iuh.fit.gui.page;

import iuh.fit.dto.TiepNhanFormData;
import iuh.fit.entity.Xe;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.theme.Theme;
import iuh.fit.gui.component.ComboGoiY;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.service.KhachHangService;
import iuh.fit.service.TiepNhanService;
import iuh.fit.service.XeService;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.NumberParseUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TiepNhanKhachHangPage extends AbstractPage {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(TiepNhanKhachHangPage.class.getName());

    private final KhachHangService khachHangService = new KhachHangService();
    private final XeService xeService = new XeService();
    private final TiepNhanService tiepNhanService = new TiepNhanService();

    private final JTextField sdtField = new JTextField();
    private final JTextField hoTenField = new JTextField();
    private final JTextField bienSoField = new JTextField();
    private final JComboBox<String> hangXeCombo = new JComboBox<>();
    private final JComboBox<String> dongXeCombo = new JComboBox<>();
    private final JTextField doiXeField = new JTextField();
    private final JTextField odoField = new JTextField();
    private final JTextArea moTaArea = new JTextArea(6, 60);

    private final JButton timKhachButton = new JButton();
    private final JButton timXeButton = new JButton();
    private final JButton lamMoiButton = new JButton();
    private final JButton luuButton = new JButton();

    private boolean dangCapNhat;
    private String hangXeDaNap = "";

    public TiepNhanKhachHangPage() {
        this.setLayout(new BorderLayout());
        this.setBackground(Theme.BG);
        this.hangXeCombo.setEditable(true);
        this.dongXeCombo.setEditable(true);
        this.dangKyDocumentListener();
    }

    @Override
    public void render() {
        this.removeAll();
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        this.setLayout(new BorderLayout(16, 16));

        this.timKhachButton.setText(I18nReaderUtil.get("app.tiepNhan.timKhach"));
        this.timXeButton.setText(I18nReaderUtil.get("app.tiepNhan.timXe"));
        this.lamMoiButton.setText(I18nReaderUtil.get("app.tiepNhan.lamMoi"));
        this.luuButton.setText(I18nReaderUtil.get("app.tiepNhan.luu"));

        JPanel contentPanel = new JPanel(new BorderLayout(16, 16));
        contentPanel.setOpaque(false);

        // Top Panel: Khách Hàng & Phương Tiện
        JPanel pnlTop = new JPanel(new GridLayout(1, 2, 16, 0));
        pnlTop.setOpaque(false);

        // 1. Thông tin Khách Hàng
        JPanel pnlKhachHang = this.createTitledPanel("app.tiepNhan.tieuDeKhachHang");
        pnlKhachHang.setLayout(new GridBagLayout());
        GridBagConstraints gbcKH = this.createGbc();

        this.addFormField(pnlKhachHang, I18nReaderUtil.get("app.tiepNhan.sdt"), this.sdtField, this.timKhachButton, gbcKH, 0);
        this.addFormField(pnlKhachHang, I18nReaderUtil.get("app.tiepNhan.hoTen"), this.hoTenField, null, gbcKH, 1);
        pnlTop.add(pnlKhachHang);

        // 2. Thông tin Phương Tiện (Xe)
        JPanel pnlPhuongTien = this.createTitledPanel("app.tiepNhan.tieuDeXe");
        pnlPhuongTien.setLayout(new GridBagLayout());
        GridBagConstraints gbcXe = this.createGbc();

        this.addFormField(pnlPhuongTien, I18nReaderUtil.get("app.tiepNhan.bienSo"), this.bienSoField, this.timXeButton, gbcXe, 0);

        JPanel pnlChiTietXe = new JPanel(new GridLayout(1, 4, 16, 0));
        pnlChiTietXe.setOpaque(false);
        pnlChiTietXe.add(this.createFieldWithLabel(I18nReaderUtil.get("app.xe.hangXe") + ":", this.hangXeCombo));
        pnlChiTietXe.add(this.createFieldWithLabel(I18nReaderUtil.get("app.xe.dongXe") + ":", this.dongXeCombo));
        pnlChiTietXe.add(this.createFieldWithLabel(I18nReaderUtil.get("app.xe.doiXe") + " (*):", this.doiXeField));
        pnlChiTietXe.add(this.createFieldWithLabel(I18nReaderUtil.get("app.tiepNhan.odo"), this.odoField));

        gbcXe.gridx = 0;
        gbcXe.gridy = 1;
        gbcXe.gridwidth = 3;
        gbcXe.fill = GridBagConstraints.HORIZONTAL;
        pnlPhuongTien.add(pnlChiTietXe, gbcXe);
        pnlTop.add(pnlPhuongTien);

        // Center Panel: Mô tả tình trạng xe
        JPanel pnlMoTa = new JPanel(new BorderLayout(0, 5));
        pnlMoTa.setOpaque(false);
        pnlMoTa.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        JLabel lblMoTa = new JLabel(I18nReaderUtil.get("app.tiepNhan.moTa"));
        lblMoTa.setFont(Theme.fieldLabelFont());
        lblMoTa.setForeground(Theme.INK);

        this.moTaArea.setLineWrap(true);
        this.moTaArea.setWrapStyleWord(true);
        pnlMoTa.add(lblMoTa, BorderLayout.NORTH);
        pnlMoTa.add(new JScrollPane(this.moTaArea), BorderLayout.CENTER);

        // Bottom Panel: Buttons
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pnlButtons.setOpaque(false);

        Theme.applyPrimary(this.luuButton);
        Theme.applyPrimary(this.timKhachButton);
        Theme.applyPrimary(this.timXeButton);

        this.timKhachButton.setPreferredSize(new Dimension(140, 40));
        this.timXeButton.setPreferredSize(new Dimension(140, 40));
        this.lamMoiButton.setPreferredSize(new Dimension(160, 40));
        this.luuButton.setPreferredSize(new Dimension(160, 40));

        pnlButtons.add(this.lamMoiButton);
        pnlButtons.add(this.luuButton);

        contentPanel.add(pnlTop, BorderLayout.NORTH);
        contentPanel.add(pnlMoTa, BorderLayout.CENTER);
        contentPanel.add(pnlButtons, BorderLayout.SOUTH);

        this.add(contentPanel, BorderLayout.CENTER);

        // Nạp gợi ý hãng/dòng xe từ dữ liệu đã có (combo vẫn cho nhập giá trị mới)
        this.taiGoiYHangXe();
        this.taiGoiYDongXe();

        // Xóa hết Listener cũ tránh lặp sự kiện
        this.clearActionListeners(this.lamMoiButton, this.timKhachButton, this.timXeButton, this.luuButton);
        this.clearActionListeners(this.hangXeCombo);

        // Đăng ký đúng Action Listeners
        this.lamMoiButton.addActionListener(e -> this.lamMoiForm());
        this.timKhachButton.addActionListener(e -> this.timKhachHang());
        this.timXeButton.addActionListener(e -> this.timXe());
        this.luuButton.addActionListener(e -> this.luuPhieuTiepNhan());
        this.hangXeCombo.addActionListener(e -> this.hangXeThayDoi());

        this.revalidate();
        this.repaint();
    }

    private void dangKyDocumentListener() {
        this.sdtField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                TiepNhanKhachHangPage.this.xoaHoTenKhiSdtThayDoi();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                TiepNhanKhachHangPage.this.xoaHoTenKhiSdtThayDoi();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                TiepNhanKhachHangPage.this.xoaHoTenKhiSdtThayDoi();
            }
        });

        this.bienSoField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                TiepNhanKhachHangPage.this.xoaThongTinXeKhiBienSoThayDoi();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                TiepNhanKhachHangPage.this.xoaThongTinXeKhiBienSoThayDoi();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                TiepNhanKhachHangPage.this.xoaThongTinXeKhiBienSoThayDoi();
            }
        });
    }

    private void xoaHoTenKhiSdtThayDoi() {
        if (this.dangCapNhat) {
            return;
        }
        this.hoTenField.setText("");
    }

    private void xoaThongTinXeKhiBienSoThayDoi() {
        if (this.dangCapNhat) {
            return;
        }
        ComboGoiY.xoa(this.hangXeCombo);
        ComboGoiY.xoa(this.dongXeCombo);
        this.doiXeField.setText("");
        this.odoField.setText("");
    }

    private void hangXeThayDoi() {
        if (this.dangCapNhat) {
            return;
        }
        String hienTai = ComboGoiY.giaTri(this.hangXeCombo);
        if (hienTai.equals(this.hangXeDaNap)) {
            return;
        }
        this.hangXeDaNap = hienTai;

        this.dangCapNhat = true;
        try {
            ComboGoiY.xoa(this.dongXeCombo);
        } finally {
            this.dangCapNhat = false;
        }
        this.taiGoiYDongXe();
    }

    private void taiGoiYHangXe() {
        try {
            ComboGoiY.nap(this.hangXeCombo, this.xeService.getDanhSachHangXe(), null);
            this.hangXeDaNap = ComboGoiY.giaTri(this.hangXeCombo);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi nạp gợi ý hãng xe", ex);
            ThongBao.loi(this, "app.tiepNhan.loiKetNoiXe");
        }
    }

    private void taiGoiYDongXe() {
        try {
            ComboGoiY.nap(this.dongXeCombo,
                    this.xeService.getDanhSachDongXe(ComboGoiY.giaTri(this.hangXeCombo)),
                    null);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi nạp gợi ý dòng xe", ex);
            ThongBao.loi(this, "app.tiepNhan.loiKetNoiXe");
        }
    }

    private void timKhachHang() {
        String sdt = this.sdtField.getText().trim();
        if (sdt.isEmpty()) {
            ThongBao.canhBao(this, "app.tiepNhan.loiSdtTrong");
            this.sdtField.requestFocusInWindow();
            return;
        }

        try {
            var found = this.khachHangService.getBySdt(sdt);
            this.dangCapNhat = true;
            try {
                if (found.isEmpty()) {
                    ThongBao.thongTin(this, "app.tiepNhan.khongTimThayKhach");
                    this.hoTenField.setText("");
                    this.hoTenField.requestFocusInWindow();
                } else {
                    this.hoTenField.setText(found.get().getHoTen());
                    this.bienSoField.requestFocusInWindow();
                }
            } finally {
                this.dangCapNhat = false;
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi tìm khách hàng theo số điện thoại", ex);
            ThongBao.loi(this, "app.tiepNhan.loiKetNoiKhach");
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Lỗi hệ thống khi tìm khách hàng", ex);
            ThongBao.loi(this, "app.tiepNhan.loiHeThong");
        }
    }

    private void timXe() {
        String rawBienSo = this.bienSoField.getText().trim();
        if (rawBienSo.isEmpty()) {
            ThongBao.canhBao(this, "app.tiepNhan.loiBienSoTrong");
            this.bienSoField.requestFocusInWindow();
            return;
        }

        // Chuẩn hóa biển số: Viết hoa, xóa khoảng trắng thừa
        String bienSoChuanHoa = rawBienSo.toUpperCase().replaceAll("\\s+", "");

        try {
            var found = this.xeService.getByBienSo(rawBienSo);
            this.dangCapNhat = true;
            try {
                if (found.isEmpty()) {
                    ThongBao.thongTinDinhDang(this, "app.tiepNhan.khongTimThayXe", bienSoChuanHoa);
                    this.bienSoField.setText(bienSoChuanHoa);
                    ComboGoiY.xoa(this.hangXeCombo);
                    ComboGoiY.xoa(this.dongXeCombo);
                    this.doiXeField.setText("");
                    this.odoField.setText("");
                    this.hangXeCombo.requestFocusInWindow();
                } else {
                    Xe xe = found.get();
                    this.bienSoField.setText(xe.getBienSo() != null ? xe.getBienSo() : bienSoChuanHoa);
                    this.hangXeCombo.setSelectedItem(xe.getHangXe() != null ? xe.getHangXe() : "");
                    this.dongXeCombo.setSelectedItem(xe.getDongXe() != null ? xe.getDongXe() : "");
                    this.hangXeDaNap = ComboGoiY.giaTri(this.hangXeCombo);
                    this.doiXeField.setText(String.valueOf(xe.getDoiXe()));
                    this.odoField.setText(String.valueOf(xe.getOdo()));
                    this.odoField.requestFocusInWindow();
                }
            } finally {
                this.dangCapNhat = false;
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi tìm xe theo biển số", ex);
            ThongBao.loi(this, "app.tiepNhan.loiKetNoiXe");
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Lỗi hệ thống khi tìm xe", ex);
            ThongBao.loi(this, "app.tiepNhan.loiHeThong");
        }
    }

    private void luuPhieuTiepNhan() {
        try {
            if (!this.kiemTraForm()) {
                return;
            }
            this.tiepNhanService.tiepNhanKhachHang(this.getFormData());
            ThongBao.thongTin(this, "app.common.create_succeed");
            this.lamMoiForm();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            ThongBao.loi(this, ex);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi lưu phiếu tiếp nhận", ex);
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }

    private JPanel createTitledPanel(String titleKey) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(Theme.titledBorder(titleKey));
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
        lbl.setFont(Theme.fieldLabelFont());
        lbl.setForeground(Theme.INK);
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

    private JPanel createFieldWithLabel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(Theme.fieldLabelFont());
        lbl.setForeground(Theme.INK);
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private void lamMoiForm() {
        this.dangCapNhat = true;
        try {
            this.sdtField.setText("");
            this.hoTenField.setText("");
            this.bienSoField.setText("");
            ComboGoiY.xoa(this.hangXeCombo);
            ComboGoiY.xoa(this.dongXeCombo);
            this.doiXeField.setText("");
            this.odoField.setText("");
            this.moTaArea.setText("");
        } finally {
            this.dangCapNhat = false;
        }
        this.sdtField.requestFocusInWindow();
    }

    private TiepNhanFormData getFormData() {
        return new TiepNhanFormData(
                this.sdtField.getText().trim(),
                this.hoTenField.getText().trim(),
                this.bienSoField.getText().trim(),
                ComboGoiY.giaTri(this.hangXeCombo),
                ComboGoiY.giaTri(this.dongXeCombo),
                NumberParseUtil.toIntegerOrNull(this.doiXeField.getText()),
                NumberParseUtil.toIntegerOrNull(this.odoField.getText()),
                this.moTaArea.getText().trim());
    }

    private boolean kiemTraForm() {
        if (this.sdtField.getText().trim().isEmpty()) {
            ThongBao.thongTin(this, "app.tiepNhan.loiSdtTrong");
            this.sdtField.requestFocusInWindow();
            return false;
        }
        if (this.hoTenField.getText().trim().isEmpty()) {
            ThongBao.thongTin(this, "app.tiepNhan.loiHoTenTrong");
            this.hoTenField.requestFocusInWindow();
            return false;
        }
        if (this.bienSoField.getText().trim().isEmpty()) {
            ThongBao.thongTin(this, "app.tiepNhan.loiBienSoTrong");
            this.bienSoField.requestFocusInWindow();
            return false;
        }
        try {
            Integer.valueOf(this.doiXeField.getText().trim());
        } catch (NumberFormatException e) {
            ThongBao.thongTin(this, "app.xe.loiDoiXe");
            this.doiXeField.requestFocusInWindow();
            return false;
        }
        if (!this.odoField.getText().trim().isEmpty()) {
            try {
                long odo = Long.parseLong(this.odoField.getText().trim());
                if (odo < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                ThongBao.thongTin(this, "app.tiepNhan.loiOdo");
                this.odoField.requestFocusInWindow();
                return false;
            }
        }
        if (this.moTaArea.getText().trim().isEmpty()) {
            ThongBao.thongTin(this, "app.tiepNhan.loiMoTa");
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
