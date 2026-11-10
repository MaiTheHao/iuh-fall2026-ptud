package iuh.fit.gui.page;

import iuh.fit.dto.DanhSachKhachHangCriteria;
import iuh.fit.dto.KhieuNaiFormData;
import iuh.fit.dto.PhieuTiepNhanView;
import iuh.fit.entity.KhachHang;
import iuh.fit.entity.KhieuNai;
import iuh.fit.entity.PhieuTiepNhan;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.Theme;
import iuh.fit.gui.UiConstants;
import iuh.fit.gui.component.ComboTimKiem;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.service.KhachHangService;
import iuh.fit.service.KhieuNaiService;
import iuh.fit.service.PhieuTiepNhanService;
import iuh.fit.util.I18nReaderUtil;

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

public class TiepNhanKhieuNaiPage extends AbstractPage {

    

    private static final Logger LOGGER = Logger.getLogger(TiepNhanKhieuNaiPage.class.getName());


    private final KhachHangService khachHangService = new KhachHangService();
    private final KhieuNaiService khieuNaiService = new KhieuNaiService();
    private final PhieuTiepNhanService phieuTiepNhanService = new PhieuTiepNhanService();
    private KhachHang khachHangDangChon;

    private final ComboTimKiem<KhachHang> cmbSdt = new ComboTimKiem<>(
            tuKhoa -> this.khachHangService.search(new DanhSachKhachHangCriteria(
                    null,
                    tuKhoa,
                    null,
                    null,
                    null,
                    null,
                    1,
                    UiConstants.SO_GOI_Y)).items(),
            khachHang -> khachHang.getHoTen() + " - " + khachHang.getSdt(),
            KhachHang::getSdt);
    private final JTextField txtHoTen = new JTextField();

    private final ComboTimKiem<PhieuTiepNhanView> cmbPhieu = new ComboTimKiem<>(
            this::timPhieuTheoKhachHang,
            phieu -> phieu.maPhieuTiepNhan() + " - " + phieu.bienSo(),
            PhieuTiepNhanView::maPhieuTiepNhan);
    private PhieuTiepNhanView phieuDangChon;
    private final JTextField txtNoiDungBanDau = new JTextField();

    private final JTextArea txaNoiDungKhieuNai = new JTextArea(8, 30);

    private final JButton btnLamMoi = new JButton();
    private final JButton btnLuu = new JButton();

    private boolean dangCapNhat;

    public TiepNhanKhieuNaiPage() {
        this.setLayout(new BorderLayout());
        this.setBackground(Theme.BG);
        this.dangKySuKienTimKiem();
    }

    @Override
    public void render() {
        this.removeAll();
        this.setBorder(Theme.leDeu(8));

        this.btnLamMoi.setText(I18nReaderUtil.get("app.khieuNai.lamMoi"));
        this.btnLuu.setText(I18nReaderUtil.get("app.khieuNai.luu"));

        JPanel mainContainer = new JPanel(new BorderLayout(0, 10));
        mainContainer.setOpaque(false);
        mainContainer.setBorder(Theme.khungTieuDe("app.khieuNai.tiepNhanTitle", Theme.fieldRowInsets()));

        JPanel pnlTop = new JPanel(new GridLayout(1, 2, 16, 0));
        pnlTop.setOpaque(false);
        pnlTop.add(this.createKhachHangPanel());
        pnlTop.add(this.createPhieuTiepNhanPanel());
        mainContainer.add(pnlTop, BorderLayout.NORTH);

        mainContainer.add(this.createNoiDungPanel(), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(Theme.vienTren());
        buttonPanel.add(Box.createVerticalStrut(24));

        this.btnLamMoi.setPreferredSize(new Dimension(140, 40));
        this.btnLuu.setPreferredSize(new Dimension(180, 40));
        Theme.applyPrimary(this.btnLuu);

        buttonPanel.add(this.btnLamMoi);
        buttonPanel.add(this.btnLuu);

        mainContainer.add(buttonPanel, BorderLayout.SOUTH);

        this.clearActionListeners(this.btnLamMoi, this.btnLuu);

        this.btnLamMoi.addActionListener(e -> this.lamMoi());

        this.btnLuu.addActionListener(e -> this.luuKhieuNai());

        this.add(mainContainer, BorderLayout.CENTER);
        this.revalidate();
        this.repaint();
    }

    private void dangKySuKienTimKiem() {
        this.cmbSdt.datXuLyGo(this::xoaKhachHangKhiSdtThayDoi);
        this.cmbSdt.datXuLyChon(this::dienKhachHang);

        this.cmbPhieu.datXuLyGo(this::xoaPhieuKhiGo);
        this.cmbPhieu.datXuLyChon(this::dienPhieu);
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
                .limit(UiConstants.SO_GOI_Y)
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
        this.txtHoTen.setText("");
        this.datLaiPhieu();
    }

    private void dienKhachHang(KhachHang khachHang) {
        this.khachHangDangChon = khachHang;
        this.txtHoTen.setText(khachHang.getHoTen());
        this.datLaiPhieu();
        this.cmbPhieu.napLaiGoiY();
        this.cmbPhieu.requestFocusInWindow();
    }

    private void datLaiPhieu() {
        this.phieuDangChon = null;
        this.cmbPhieu.xoa();
        this.txtNoiDungBanDau.setText("");
    }

    private void xoaPhieuKhiGo() {
        this.phieuDangChon = null;
        this.txtNoiDungBanDau.setText("");
    }

    private void dienPhieu(PhieuTiepNhanView phieu) {
        this.phieuDangChon = phieu;

        try {
            this.txtNoiDungBanDau.setText(
                    this.phieuTiepNhanService.getById(phieu.maPhieuTiepNhan()).getMoTa());
        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.SEVERE, "Không tìm thấy phiếu tiếp nhận đã chọn", ex);
            this.txtNoiDungBanDau.setText("");
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tải nội dung yêu cầu ban đầu của phiếu tiếp nhận", ex);
            this.txtNoiDungBanDau.setText("");
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
        if (this.txaNoiDungKhieuNai.getText().trim().isEmpty()) {
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
                    this.txaNoiDungKhieuNai.getText().trim()));
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
            this.cmbSdt.xoa();
        } finally {
            this.dangCapNhat = false;
        }
        this.txtHoTen.setText("");
        this.khachHangDangChon = null;
        this.datLaiPhieu();
        this.txaNoiDungKhieuNai.setText("");
        this.cmbSdt.requestFocusInWindow();
    }

    private JPanel createKhachHangPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(Theme.khungTieuDe("app.khieuNai.tieuDeKhachHang", Theme.fieldRowInsets()));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lblSdt = new JLabel(I18nReaderUtil.get("app.khieuNai.sdt"));
        lblSdt.setFont(Theme.fieldLabelFont());
        lblSdt.setForeground(Theme.INK);
        panel.add(lblSdt, gbc);

        gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(this.cmbSdt, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.insets = new Insets(10, 0, 5, 0);
        JLabel lblHoTen = new JLabel(I18nReaderUtil.get("app.khieuNai.hoTen"));
        lblHoTen.setFont(Theme.fieldLabelFont());
        lblHoTen.setForeground(Theme.INK);
        panel.add(lblHoTen, gbc);

        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 0, 0);
        Theme.applyReadOnly(this.txtHoTen);
        panel.add(this.txtHoTen, gbc);

        return panel;
    }

    private JPanel createPhieuTiepNhanPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(Theme.khungTieuDe("app.khieuNai.tieuDePhieu", Theme.fieldRowInsets()));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblPhieu = new JLabel(I18nReaderUtil.get("app.khieuNai.phieuTiepNhan") + " (*):");
        lblPhieu.setFont(Theme.fieldLabelFont());
        lblPhieu.setForeground(Theme.INK);
        panel.add(lblPhieu, gbc);

        gbc.gridy = 1;
        panel.add(this.cmbPhieu, gbc);

        gbc.gridy = 2; gbc.insets = new Insets(10, 0, 5, 0);
        JLabel lblNoiDung = new JLabel(I18nReaderUtil.get("app.khieuNai.noiDungBanDau"));
        lblNoiDung.setFont(Theme.fieldLabelFont());
        lblNoiDung.setForeground(Theme.INK);
        panel.add(lblNoiDung, gbc);

        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 0, 0);
        Theme.applyReadOnly(this.txtNoiDungBanDau);
        panel.add(this.txtNoiDungBanDau, gbc);

        return panel;
    }

    private JPanel createNoiDungPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);
        panel.setBorder(Theme.khungTieuDe("app.khieuNai.tieuDeNoiDung", Theme.fieldRowInsets()));

        this.txaNoiDungKhieuNai.setLineWrap(true);
        this.txaNoiDungKhieuNai.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(this.txaNoiDungKhieuNai);

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
