package iuh.fit.gui.page;

import iuh.fit.dto.DanhSachKhachHangCriteria;
import iuh.fit.dto.DanhSachXeCriteria;
import iuh.fit.dto.TiepNhanFormData;
import iuh.fit.entity.DoiXe;
import iuh.fit.entity.DongXe;
import iuh.fit.entity.HangXe;
import iuh.fit.entity.KhachHang;
import iuh.fit.entity.Xe;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.theme.Theme;
import iuh.fit.gui.component.ComboTimKiem;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.service.DanhMucXeService;
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
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TiepNhanKhachHangPage extends AbstractPage {

  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = Logger.getLogger(TiepNhanKhachHangPage.class.getName());

  private static final int SO_GOI_Y = 10;

  private final KhachHangService khachHangService = new KhachHangService();
  private final XeService xeService = new XeService();
  private final DanhMucXeService danhMucXeService = new DanhMucXeService();
  private final TiepNhanService tiepNhanService = new TiepNhanService();

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
          kh -> kh.getHoTen() + " - " + kh.getSdt(),
          KhachHang::getSdt);

  private final JTextField hoTenField = new JTextField();
  private final ComboTimKiem<Xe> bienSoField = new ComboTimKiem<>(
          tuKhoa -> this.xeService.search(new DanhSachXeCriteria(
                  tuKhoa,
                  null,
                  null,
                  null,
                  null,
                  null,
                  null,
                  null,
                  1,
                  SO_GOI_Y)).items(),
          xe -> xe.getBienSo() + " - " + xe.getTenHangXe() + " - " + xe.getTenDongXe() + " - " + xe.getNamSX(),
          Xe::getBienSo);

  private final JComboBox<HangXe> hangXeCombo = new JComboBox<>();
  private final JComboBox<DongXe> dongXeCombo = new JComboBox<>();
  private final JComboBox<DoiXe> doiXeCombo = new JComboBox<>();
  private final JTextField odoField = new JTextField();
  private final JTextArea moTaArea = new JTextArea(6, 60);

  private final JButton lamMoiButton = new JButton();
  private final JButton luuButton = new JButton();

  private boolean dangCapNhat;
  private String hangXeDaNap = "";

  public TiepNhanKhachHangPage() {
    this.setLayout(new BorderLayout());
    this.setBackground(Theme.BG);
    this.dangKyDocumentListener();
  }

  @Override
  public void render() {
    this.removeAll();
    this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    this.setBackground(Theme.BG);
    this.setLayout(new BorderLayout(16, 16));

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

    this.addFormField(pnlKhachHang, I18nReaderUtil.get("app.tiepNhan.sdt"), this.sdtField, gbcKH, 0);
    this.addFormField(pnlKhachHang, I18nReaderUtil.get("app.tiepNhan.hoTen"), this.hoTenField, gbcKH, 1);
    pnlTop.add(pnlKhachHang);

    // 2. Thông tin Phương Tiện (Xe)
    JPanel pnlPhuongTien = this.createTitledPanel("app.tiepNhan.tieuDeXe");
    pnlPhuongTien.setLayout(new GridBagLayout());
    GridBagConstraints gbcXe = this.createGbc();

    this.addFormField(pnlPhuongTien, I18nReaderUtil.get("app.tiepNhan.bienSo"), this.bienSoField, gbcXe, 0);

    JPanel pnlChiTietXe = new JPanel(new GridLayout(1, 4, 16, 0));
    pnlChiTietXe.setOpaque(false);
    pnlChiTietXe.add(this.createFieldWithLabel(I18nReaderUtil.get("app.xe.hangXe") + ":", this.hangXeCombo));
    pnlChiTietXe.add(this.createFieldWithLabel(I18nReaderUtil.get("app.xe.dongXe") + ":", this.dongXeCombo));
    pnlChiTietXe.add(this.createFieldWithLabel(I18nReaderUtil.get("app.xe.doiXe") + " (*):", this.doiXeCombo));
    pnlChiTietXe.add(this.createFieldWithLabel(I18nReaderUtil.get("app.tiepNhan.odo"), this.odoField));

    gbcXe.gridx = 0;
    gbcXe.gridy = 1;
    gbcXe.gridwidth = 3;
    gbcXe.fill = GridBagConstraints.HORIZONTAL;
    pnlPhuongTien.add(pnlChiTietXe, gbcXe);
    pnlTop.add(pnlPhuongTien);

    // Center Panel: Mô tả tình trạng xe
    JPanel pnlMoTa = new JPanel(new BorderLayout(0, 5));
    Theme.applyCardStyle(pnlMoTa);

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

    this.lamMoiButton.setPreferredSize(new Dimension(160, 40));
    this.luuButton.setPreferredSize(new Dimension(160, 40));

    pnlButtons.add(this.lamMoiButton);
    pnlButtons.add(this.luuButton);

    contentPanel.add(pnlTop, BorderLayout.NORTH);
    contentPanel.add(pnlMoTa, BorderLayout.CENTER);
    contentPanel.add(pnlButtons, BorderLayout.SOUTH);

    this.add(contentPanel, BorderLayout.CENTER);

    // Nạp danh mục hãng/dòng/đời xe
    this.taiGoiYHangXe();
    this.taiGoiYDongXe();
    this.taiGoiYDoiXe();

    // Xóa hết Listener cũ tránh lặp sự kiện
    this.clearActionListeners(this.lamMoiButton, this.luuButton);
    this.clearActionListeners(this.hangXeCombo, this.dongXeCombo);

    // Đăng ký đúng Action Listeners
    this.lamMoiButton.addActionListener(e -> this.lamMoiForm());
    this.luuButton.addActionListener(e -> this.luuPhieuTiepNhan());
    this.hangXeCombo.addActionListener(e -> this.hangXeThayDoi());
    this.dongXeCombo.addActionListener(e -> this.dongXeThayDoi());

    this.revalidate();
    this.repaint();
  }

  private void dangKyDocumentListener() {
    this.sdtField.datXuLyGo(() -> {
      if (!this.dangCapNhat) {
        this.hoTenField.setText("");
      }
    });
    this.sdtField.datXuLyChon(this::dienKhachHang);

    this.bienSoField.datXuLyGo(this::xoaThongTinXeKhiBienSoThayDoi);
    this.bienSoField.datXuLyChon(this::dienThongTinXe);
  }

  private void dienKhachHang(KhachHang kh) {
    this.dangCapNhat = true;
    try {
      this.hoTenField.setText(kh.getHoTen());
    } finally {
      this.dangCapNhat = false;
    }
    this.bienSoField.requestFocusInWindow();
  }

  private void dienThongTinXe(Xe xe) {
    this.dangCapNhat = true;
    try {
      this.chonHangXe(xe.getMaHangXe());
      this.taiGoiYDongXe();
      this.chonDongXe(xe.getMaDongXe());
      this.taiGoiYDoiXe();
      this.chonDoiXe(xe.getMaDoiXe());
      this.hangXeDaNap = this.maHangDaChon();
      this.odoField.setText(String.valueOf(xe.getOdo()));
    } finally {
      this.dangCapNhat = false;
    }
    this.odoField.requestFocusInWindow();
  }

  private void xoaThongTinXeKhiBienSoThayDoi() {
    if (this.dangCapNhat) {
      return;
    }
    this.dangCapNhat = true;
    try {
      this.hangXeCombo.removeAllItems();
      this.dongXeCombo.removeAllItems();
      this.doiXeCombo.removeAllItems();
    } finally {
      this.dangCapNhat = false;
    }
    this.hangXeDaNap = "";
    this.odoField.setText("");
  }

  private void hangXeThayDoi() {
    if (this.dangCapNhat) {
      return;
    }
    String hienTai = this.maHangDaChon() == null ? "" : this.maHangDaChon();
    if (hienTai.equals(this.hangXeDaNap)) {
      return;
    }
    this.hangXeDaNap = hienTai;

    this.dangCapNhat = true;
    try {
      this.dongXeCombo.removeAllItems();
      this.doiXeCombo.removeAllItems();
    } finally {
      this.dangCapNhat = false;
    }
    this.taiGoiYDongXe();
  }

  private void dongXeThayDoi() {
    if (this.dangCapNhat) {
      return;
    }
    this.doiXeCombo.removeAllItems();
    this.taiGoiYDoiXe();
  }

  private String maHangDaChon() {
    Object sel = this.hangXeCombo.getSelectedItem();
    return sel instanceof HangXe ? ((HangXe) sel).getMaHangXe() : null;
  }

  private String maDongDaChon() {
    Object sel = this.dongXeCombo.getSelectedItem();
    return sel instanceof DongXe ? ((DongXe) sel).getMaDongXe() : null;
  }

  private String maDoiDaChon() {
    Object sel = this.doiXeCombo.getSelectedItem();
    return sel instanceof DoiXe ? ((DoiXe) sel).getMaDoiXe() : null;
  }

  private void chonHangXe(String maHangXe) {
    if (maHangXe == null) {
      return;
    }
    for (int i = 0; i < this.hangXeCombo.getItemCount(); i++) {
      HangXe item = this.hangXeCombo.getItemAt(i);
      if (item != null && maHangXe.equals(item.getMaHangXe())) {
        this.hangXeCombo.setSelectedIndex(i);
        return;
      }
    }
  }

  private void chonDongXe(String maDongXe) {
    if (maDongXe == null) {
      return;
    }
    for (int i = 0; i < this.dongXeCombo.getItemCount(); i++) {
      DongXe item = this.dongXeCombo.getItemAt(i);
      if (item != null && maDongXe.equals(item.getMaDongXe())) {
        this.dongXeCombo.setSelectedIndex(i);
        return;
      }
    }
  }

  private void chonDoiXe(String maDoiXe) {
    if (maDoiXe == null) {
      return;
    }
    for (int i = 0; i < this.doiXeCombo.getItemCount(); i++) {
      DoiXe item = this.doiXeCombo.getItemAt(i);
      if (item != null && maDoiXe.equals(item.getMaDoiXe())) {
        this.doiXeCombo.setSelectedIndex(i);
        return;
      }
    }
  }

  private void taiGoiYHangXe() {
    try {
      this.hangXeCombo.removeAllItems();
      for (HangXe hangXe : this.danhMucXeService.getDanhSachHangXe()) {
        this.hangXeCombo.addItem(hangXe);
      }
      this.hangXeDaNap = this.maHangDaChon() == null ? "" : this.maHangDaChon();
    } catch (SQLException ex) {
      LOGGER.log(Level.SEVERE, "Lỗi nạp gợi ý hãng xe", ex);
      ThongBao.loi(this, "app.tiepNhan.loiKetNoiXe");
    }
  }

  private void taiGoiYDongXe() {
    try {
      this.dongXeCombo.removeAllItems();
      for (DongXe dongXe : this.danhMucXeService.getDanhSachDongXe(this.maHangDaChon())) {
        this.dongXeCombo.addItem(dongXe);
      }
    } catch (SQLException ex) {
      LOGGER.log(Level.SEVERE, "Lỗi nạp gợi ý dòng xe", ex);
      ThongBao.loi(this, "app.tiepNhan.loiKetNoiXe");
    }
  }

  private void taiGoiYDoiXe() {
    try {
      this.doiXeCombo.removeAllItems();
      for (DoiXe doiXe : this.danhMucXeService.getDanhSachDoiXe(this.maDongDaChon())) {
        this.doiXeCombo.addItem(doiXe);
      }
    } catch (SQLException ex) {
      LOGGER.log(Level.SEVERE, "Lỗi nạp gợi ý đời xe", ex);
      ThongBao.loi(this, "app.tiepNhan.loiKetNoiXe");
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
    panel.setOpaque(true);
    panel.setBackground(Theme.PANEL);
    panel.setBorder(BorderFactory.createCompoundBorder(
            Theme.titledBorder(titleKey),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
    return panel;
  }

  private GridBagConstraints createGbc() {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    return gbc;
  }

  private void addFormField(JPanel parent, String label, JComponent field, GridBagConstraints gbc, int y) {
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
      this.sdtField.xoa();
      this.hoTenField.setText("");
      this.bienSoField.xoa();
      this.hangXeCombo.removeAllItems();
      this.dongXeCombo.removeAllItems();
      this.doiXeCombo.removeAllItems();
      this.hangXeDaNap = "";
      this.odoField.setText("");
      this.moTaArea.setText("");
    } finally {
      this.dangCapNhat = false;
    }
    this.sdtField.requestFocusInWindow();
  }

  private TiepNhanFormData getFormData() {
    return new TiepNhanFormData(
            this.sdtField.getTuKhoa().trim(),
            this.hoTenField.getText().trim(),
            this.bienSoField.getTuKhoa().trim(),
            this.maHangDaChon(),
            this.maDongDaChon(),
            this.maDoiDaChon(),
            NumberParseUtil.toIntegerOrNull(this.odoField.getText()),
            this.moTaArea.getText().trim());
  }

  private boolean kiemTraForm() {
    if (this.sdtField.getTuKhoa().trim().isEmpty()) {
      ThongBao.thongTin(this, "app.tiepNhan.loiSdtTrong");
      this.sdtField.requestFocusInWindow();
      return false;
    }
    if (this.hoTenField.getText().trim().isEmpty()) {
      ThongBao.thongTin(this, "app.tiepNhan.loiHoTenTrong");
      this.hoTenField.requestFocusInWindow();
      return false;
    }
    if (this.bienSoField.getTuKhoa().trim().isEmpty()) {
      ThongBao.thongTin(this, "app.tiepNhan.loiBienSoTrong");
      this.bienSoField.requestFocusInWindow();
      return false;
    }
    if (this.maDoiDaChon() == null) {
      ThongBao.thongTin(this, "app.danhMucXe.chuaChonDoi");
      this.doiXeCombo.requestFocusInWindow();
      return false;
    }
    if (!this.odoField.getText().trim().isEmpty()) {
      try {
        long odo = Long.parseLong(this.odoField.getText().trim());
        if (odo < 0)
          throw new NumberFormatException();
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
