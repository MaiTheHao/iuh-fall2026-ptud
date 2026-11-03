package iuh.fit.gui.page;

import iuh.fit.dto.DanhSachKhachHangCriteria;
import iuh.fit.dto.KhieuNaiFormData;
import iuh.fit.dto.PhieuTiepNhanView;
import iuh.fit.entity.KhachHang;
import iuh.fit.entity.KhieuNai;
import iuh.fit.entity.PhieuTiepNhan;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.component.ComboTimKiem;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.theme.Theme;
import iuh.fit.service.KhachHangService;
import iuh.fit.service.KhieuNaiService;
import iuh.fit.service.PhieuTiepNhanService;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
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

/**
 * Tiếp nhận khiếu nại của khách hàng dựa trên số điện thoại và phiếu tiếp nhận liên quan.
 */
public class TiepNhanKhieuNaiPage extends AbstractPage {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(TiepNhanKhieuNaiPage.class.getName());

    private static final int SO_GOI_Y = 10;

    // Services
    private final KhachHangService khachHangService = new KhachHangService();
    private final KhieuNaiService khieuNaiService = new KhieuNaiService();
    private final PhieuTiepNhanService phieuTiepNhanService = new PhieuTiepNhanService();
    private KhachHang khachHangDangChon;

    // Các thành phần UI
    private final ComboTimKiem<KhachHang> sdtField = new ComboTimKiem<>(
            tuKhoa -> this.khachHangService.search(new DanhSachKhachHangCriteria(
                    null,
                    tuKhoa,
                    null,
                    null,
                    null,
                    null,
                    1,
                    SO_GOI_Y)).items(),
            khachHang -> khachHang.getHoTen() + " - " + khachHang.getSdt(),
            KhachHang::getSdt);
    private final JTextField hoTenField = new JTextField();

    private final ComboTimKiem<PhieuTiepNhanView> phieuCombo = new ComboTimKiem<>(
            this::timPhieuTheoKhachHang,
            phieu -> phieu.maPhieuTiepNhan() + " - " + phieu.bienSo(),
            PhieuTiepNhanView::maPhieuTiepNhan);
    private PhieuTiepNhanView phieuDangChon;
    private final JTextField noiDungBanDauField = new JTextField();

    private final JTextArea noiDungKhieuNaiArea = new JTextArea(8, 30);

    private final JButton lamMoiBtn = new JButton();
    private final JButton luuBtn = new JButton();

    private boolean dangCapNhat;

    public TiepNhanKhieuNaiPage() {
        this.setLayout(new BorderLayout());
        this.setBackground(Theme.BG);
        this.dangKySuKienTimKiem();
    }

    @Override
    public void render() {
        this.removeAll();
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        this.lamMoiBtn.setText(I18nReaderUtil.get("app.khieuNai.lamMoi"));
        this.luuBtn.setText(I18nReaderUtil.get("app.khieuNai.luu"));

        // Container chính bao bọc toàn bộ Form
        JPanel mainContainer = new JPanel(new BorderLayout(0, 10));
        mainContainer.setOpaque(false);
        Insets padding = Theme.fieldRowInsets();
        mainContainer.setBorder(BorderFactory.createCompoundBorder(
                Theme.titledBorder("app.khieuNai.tiepNhanTitle"),
                BorderFactory.createEmptyBorder(padding.top, padding.left, padding.bottom, padding.right)
        ));

        // -- HAI PANEL TRÊN (NORTH) --
        JPanel pnlTop = new JPanel(new GridLayout(1, 2, 16, 0));
        pnlTop.setOpaque(false);
        pnlTop.add(this.createKhachHangPanel());
        pnlTop.add(this.createPhieuTiepNhanPanel());
        mainContainer.add(pnlTop, BorderLayout.NORTH);

        // -- NỘI DUNG KHIẾU NẠI (CENTER) --
        mainContainer.add(this.createNoiDungPanel(), BorderLayout.CENTER);

        // -- VÙNG NÚT BẤM (SOUTH) --
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER)); // Đường line mờ phân cách
        buttonPanel.add(Box.createVerticalStrut(24)); // Tạo khoảng cách với đường line

        this.lamMoiBtn.setPreferredSize(new Dimension(140, 40));
        this.luuBtn.setPreferredSize(new Dimension(180, 40));
        Theme.applyPrimary(this.luuBtn);

        buttonPanel.add(this.lamMoiBtn);
        buttonPanel.add(this.luuBtn);

        mainContainer.add(buttonPanel, BorderLayout.SOUTH);

        // --- Gắn sự kiện (Action Listeners) ---
        this.clearActionListeners(this.lamMoiBtn, this.luuBtn);

        this.lamMoiBtn.addActionListener(e -> this.lamMoi());

        this.luuBtn.addActionListener(e -> this.luuKhieuNai());

        this.add(mainContainer, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

    private void dangKySuKienTimKiem() {
        this.sdtField.datXuLyGo(this::xoaKhachHangKhiSdtThayDoi);
        this.sdtField.datXuLyChon(this::dienKhachHang);

        this.phieuCombo.datXuLyGo(this::xoaPhieuKhiGo);
        this.phieuCombo.datXuLyChon(this::dienPhieu);
    }

    private List<PhieuTiepNhanView> timPhieuTheoKhachHang(String tuKhoa) throws SQLException {
        if (this.khachHangDangChon == null
                || this.khachHangDangChon.getMaKhachHang() == null
                || this.khachHangDangChon.getMaKhachHang().isBlank()) {
            return List.of();
        }

        String tuKhoaChuan = tuKhoa == null ? "" : tuKhoa.trim().toLowerCase();

        return this.phieuTiepNhanService
                .getDanhSachTheoKhachHang(this.khachHangDangChon.getMaKhachHang())
                .stream()
                .filter(phieu -> tuKhoaChuan.isEmpty() || this.khopTuKhoa(phieu, tuKhoaChuan))
                .limit(SO_GOI_Y)
                .map(this::taoPhieuView)
                .toList();
    }

    private boolean khopTuKhoa(PhieuTiepNhan phieu, String tuKhoa) {
        String maPhieu = phieu.getMaPhieuTiepNhan() == null
                ? "" : phieu.getMaPhieuTiepNhan().toLowerCase();
        String bienSo = phieu.getBienSo() == null ? "" : phieu.getBienSo().toLowerCase();

        return maPhieu.contains(tuKhoa) || bienSo.contains(tuKhoa);
    }

    private PhieuTiepNhanView taoPhieuView(PhieuTiepNhan phieu) {
        return new PhieuTiepNhanView(
                phieu.getMaPhieuTiepNhan(),
                phieu.getBienSo(),
                this.khachHangDangChon.getHoTen(),
                this.khachHangDangChon.getSdt(),
                phieu.getMoTa(),
                phieu.getTrangThai(),
                phieu.getNgayTao());
    }

    private void xoaKhachHangKhiSdtThayDoi() {
        if (this.dangCapNhat) {
            return;
        }
        this.khachHangDangChon = null;
        this.hoTenField.setText("");
        this.datLaiPhieu();
    }

    private void dienKhachHang(KhachHang khachHang) {
        this.khachHangDangChon = khachHang;
        this.hoTenField.setText(khachHang.getHoTen());
        this.datLaiPhieu();
        this.phieuCombo.napLaiGoiY();
        this.phieuCombo.requestFocusInWindow();
    }

    private void datLaiPhieu() {
        this.phieuDangChon = null;
        this.phieuCombo.xoa();
        this.noiDungBanDauField.setText("");
    }

    private void xoaPhieuKhiGo() {
        this.phieuDangChon = null;
        this.noiDungBanDauField.setText("");
    }

    private void dienPhieu(PhieuTiepNhanView phieu) {
        this.phieuDangChon = phieu;

        try {
            this.noiDungBanDauField.setText(
                    this.phieuTiepNhanService.getById(phieu.maPhieuTiepNhan()).getMoTa());
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
        if (this.phieuDangChon == null) {
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
                    this.phieuDangChon.maPhieuTiepNhan(),
                    this.noiDungKhieuNaiArea.getText().trim()));
            ThongBao.thongTinDinhDang(this, "app.khieuNai.luuThanhCong", khieuNai.getMaKhieuNai());
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
            this.sdtField.xoa();
        } finally {
            this.dangCapNhat = false;
        }
        this.hoTenField.setText("");
        this.khachHangDangChon = null;
        this.datLaiPhieu();
        this.noiDungKhieuNaiArea.setText("");
        this.sdtField.requestFocusInWindow();
    }

    private JPanel createKhachHangPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        Insets padding = Theme.fieldRowInsets();
        panel.setBorder(BorderFactory.createCompoundBorder(
                Theme.titledBorder("app.khieuNai.tieuDeKhachHang"),
                BorderFactory.createEmptyBorder(padding.top, padding.left, padding.bottom, padding.right)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.weightx = 1.0;

        // Số điện thoại (*)
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lblSdt = new JLabel(I18nReaderUtil.get("app.khieuNai.sdt"));
        lblSdt.setFont(Theme.fieldLabelFont());
        lblSdt.setForeground(Theme.INK);
        panel.add(lblSdt, gbc);

        // Nhập SĐT có gợi ý khách hàng
        gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(this.sdtField, gbc);

        // Họ tên khách hàng
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.insets = new Insets(10, 0, 5, 0);
        JLabel lblHoTen = new JLabel(I18nReaderUtil.get("app.khieuNai.hoTen"));
        lblHoTen.setFont(Theme.fieldLabelFont());
        lblHoTen.setForeground(Theme.INK);
        panel.add(lblHoTen, gbc);

        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 0, 0);
        Theme.applyReadOnly(this.hoTenField);
        panel.add(this.hoTenField, gbc);

        return panel;
    }

    private JPanel createPhieuTiepNhanPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        Insets padding = Theme.fieldRowInsets();
        panel.setBorder(BorderFactory.createCompoundBorder(
                Theme.titledBorder("app.khieuNai.tieuDePhieu"),
                BorderFactory.createEmptyBorder(padding.top, padding.left, padding.bottom, padding.right)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.weightx = 1.0;

        // Phiếu tiếp nhận khiếu nại (*)
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblPhieu = new JLabel(I18nReaderUtil.get("app.khieuNai.phieuTiepNhan") + " (*):");
        lblPhieu.setFont(Theme.fieldLabelFont());
        lblPhieu.setForeground(Theme.INK);
        panel.add(lblPhieu, gbc);

        gbc.gridy = 1;
        panel.add(this.phieuCombo, gbc);

        // Nội dung yêu cầu ban đầu
        gbc.gridy = 2; gbc.insets = new Insets(10, 0, 5, 0);
        JLabel lblNoiDung = new JLabel(I18nReaderUtil.get("app.khieuNai.noiDungBanDau"));
        lblNoiDung.setFont(Theme.fieldLabelFont());
        lblNoiDung.setForeground(Theme.INK);
        panel.add(lblNoiDung, gbc);

        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 0, 0);
        Theme.applyReadOnly(this.noiDungBanDauField);
        panel.add(this.noiDungBanDauField, gbc);

        return panel;
    }

    private JPanel createNoiDungPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);
        Insets padding = Theme.fieldRowInsets();
        panel.setBorder(BorderFactory.createCompoundBorder(
                Theme.titledBorder("app.khieuNai.tieuDeNoiDung"),
                BorderFactory.createEmptyBorder(padding.top, padding.left, padding.bottom, padding.right)
        ));

        this.noiDungKhieuNaiArea.setLineWrap(true);
        this.noiDungKhieuNaiArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(this.noiDungKhieuNaiArea);

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
