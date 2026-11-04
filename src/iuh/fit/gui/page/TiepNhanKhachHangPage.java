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
import iuh.fit.gui.Theme;
import iuh.fit.gui.UiConstants;
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

  

  private static final Logger LOGGER = Logger.getLogger(TiepNhanKhachHangPage.class.getName());


  private final KhachHangService khachHangService = new KhachHangService();
  private final XeService xeService = new XeService();
  private final DanhMucXeService danhMucXeService = new DanhMucXeService();
  private final TiepNhanService tiepNhanService = new TiepNhanService();

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
          kh -> kh.getHoTen() + " - " + kh.getSdt(),
          KhachHang::getSdt);

  private final JTextField txtHoTen = new JTextField();
  private final ComboTimKiem<Xe> cmbBienSo = new ComboTimKiem<>(
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
                  UiConstants.SO_GOI_Y)).items(),
          xe -> xe.getBienSo() + " - " + xe.getTenHangXe() + " - " + xe.getTenDongXe() + " - " + xe.getNamSX(),
          Xe::getBienSo);

  private final JComboBox<HangXe> cmbHangXe = new JComboBox<>();
  private final JComboBox<DongXe> cmbDongXe = new JComboBox<>();
  private final JComboBox<DoiXe> cmbDoiXe = new JComboBox<>();
  private final JTextField txtOdo = new JTextField();
  private final JTextArea txaMoTa = new JTextArea(6, 60);

  private final JButton btnLamMoi = new JButton();
  private final JButton btnLuu = new JButton();

  private boolean dangCapNhat;
  private boolean dangNapDanhSach = false;
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

    this.btnLamMoi.setText(I18nReaderUtil.get("app.tiepNhan.lamMoi"));
    this.btnLuu.setText(I18nReaderUtil.get("app.tiepNhan.luu"));

    JPanel contentPanel = new JPanel(new BorderLayout(16, 16));
    contentPanel.setOpaque(false);

    JPanel pnlTop = new JPanel(new GridLayout(1, 2, 16, 0));
    pnlTop.setOpaque(false);

    JPanel pnlKhachHang = this.createTitledPanel("app.tiepNhan.tieuDeKhachHang");
    pnlKhachHang.setLayout(new GridBagLayout());
    GridBagConstraints gbcKH = this.createGbc();

    this.addFormField(pnlKhachHang, I18nReaderUtil.get("app.tiepNhan.sdt"), this.cmbSdt, gbcKH, 0);
    this.addFormField(pnlKhachHang, I18nReaderUtil.get("app.tiepNhan.hoTen"), this.txtHoTen, gbcKH, 1);
    pnlTop.add(pnlKhachHang);

    JPanel pnlPhuongTien = this.createTitledPanel("app.tiepNhan.tieuDeXe");
    pnlPhuongTien.setLayout(new GridBagLayout());
    GridBagConstraints gbcXe = this.createGbc();

    this.addFormField(pnlPhuongTien, I18nReaderUtil.get("app.tiepNhan.bienSo"), this.cmbBienSo, gbcXe, 0);

    JPanel pnlChiTietXe = new JPanel(new GridLayout(1, 4, 16, 0));
    pnlChiTietXe.setOpaque(false);
    pnlChiTietXe.add(this.createFieldWithLabel(I18nReaderUtil.get("app.xe.hangXe") + ":", this.cmbHangXe));
    pnlChiTietXe.add(this.createFieldWithLabel(I18nReaderUtil.get("app.xe.dongXe") + ":", this.cmbDongXe));
    pnlChiTietXe.add(this.createFieldWithLabel(I18nReaderUtil.get("app.xe.doiXe") + " (*):", this.cmbDoiXe));
    pnlChiTietXe.add(this.createFieldWithLabel(I18nReaderUtil.get("app.tiepNhan.odo"), this.txtOdo));

    gbcXe.gridx = 0;
    gbcXe.gridy = 1;
    gbcXe.gridwidth = 3;
    gbcXe.fill = GridBagConstraints.HORIZONTAL;
    pnlPhuongTien.add(pnlChiTietXe, gbcXe);
    pnlTop.add(pnlPhuongTien);

    JPanel pnlMoTa = new JPanel(new BorderLayout(0, 5));
    Theme.applyCardStyle(pnlMoTa);

    JLabel lblMoTa = new JLabel(I18nReaderUtil.get("app.tiepNhan.moTa"));
    lblMoTa.setFont(Theme.fieldLabelFont());
    lblMoTa.setForeground(Theme.INK);

    this.txaMoTa.setLineWrap(true);
    this.txaMoTa.setWrapStyleWord(true);
    pnlMoTa.add(lblMoTa, BorderLayout.NORTH);
    pnlMoTa.add(new JScrollPane(this.txaMoTa), BorderLayout.CENTER);

    JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    pnlButtons.setOpaque(false);

    Theme.applyPrimary(this.btnLuu);

    this.btnLamMoi.setPreferredSize(new Dimension(160, 40));
    this.btnLuu.setPreferredSize(new Dimension(160, 40));

    pnlButtons.add(this.btnLamMoi);
    pnlButtons.add(this.btnLuu);

    contentPanel.add(pnlTop, BorderLayout.NORTH);
    contentPanel.add(pnlMoTa, BorderLayout.CENTER);
    contentPanel.add(pnlButtons, BorderLayout.SOUTH);

    this.add(contentPanel, BorderLayout.CENTER);

    this.taiGoiYHangXe();
    this.taiGoiYDongXe();
    this.taiGoiYDoiXe();

    this.clearActionListeners(this.btnLamMoi, this.btnLuu);
    this.clearActionListeners(this.cmbHangXe, this.cmbDongXe);

    this.btnLamMoi.addActionListener(e -> this.lamMoiForm());
    this.btnLuu.addActionListener(e -> this.luuPhieuTiepNhan());
    this.cmbHangXe.addActionListener(e -> this.hangXeThayDoi());
    this.cmbDongXe.addActionListener(e -> this.dongXeThayDoi());

    this.revalidate();
    this.repaint();
  }

  private void dangKyDocumentListener() {
    this.cmbSdt.datXuLyGo(() -> {
      if (!this.dangCapNhat) {
        this.txtHoTen.setText("");
      }
    });
    this.cmbSdt.datXuLyChon(this::dienKhachHang);

    this.cmbBienSo.datXuLyGo(this::xoaThongTinXeKhiBienSoThayDoi);
    this.cmbBienSo.datXuLyChon(this::dienThongTinXe);
  }

  private void dienKhachHang(KhachHang kh) {
    this.dangCapNhat = true;
    try {
      this.txtHoTen.setText(kh.getHoTen());
    } finally {
      this.dangCapNhat = false;
    }
    this.cmbBienSo.requestFocusInWindow();
  }

  private void dienThongTinXe(Xe xe) {
    this.dangCapNhat = true;
    try {
      this.taiGoiYHangXe();
      this.chonHangXe(xe.getMaHangXe());
      this.taiGoiYDongXe();
      this.chonDongXe(xe.getMaDongXe());
      this.taiGoiYDoiXe();
      this.chonDoiXe(xe.getMaDoiXe());
      this.hangXeDaNap = this.maHangDaChon();
      this.txtOdo.setText(String.valueOf(xe.getOdo()));
    } finally {
      this.dangCapNhat = false;
    }
    this.txtOdo.requestFocusInWindow();
  }

  private void xoaThongTinXeKhiBienSoThayDoi() {
    if (this.dangCapNhat) {
      return;
    }
    this.dangCapNhat = true;
    try {
      boolean cu = this.dangNapDanhSach;
      this.dangNapDanhSach = true;
      try {
        this.cmbHangXe.setSelectedIndex(-1);
      } finally {
        this.dangNapDanhSach = cu;
      }
      this.cmbDongXe.removeAllItems();
      this.cmbDoiXe.removeAllItems();
    } finally {
      this.dangCapNhat = false;
    }
    this.hangXeDaNap = "";
    this.txtOdo.setText("");
  }

  private void hangXeThayDoi() {
    if (this.dangCapNhat || this.dangNapDanhSach) {
      return;
    }
    String hienTai = this.maHangDaChon() == null ? "" : this.maHangDaChon();
    if (hienTai.equals(this.hangXeDaNap)) {
      return;
    }
    this.hangXeDaNap = hienTai;

    this.dangCapNhat = true;
    try {
      this.cmbDongXe.removeAllItems();
      this.cmbDoiXe.removeAllItems();
    } finally {
      this.dangCapNhat = false;
    }
    this.taiGoiYDongXe();
  }

  private void dongXeThayDoi() {
    if (this.dangCapNhat || this.dangNapDanhSach) {
      return;
    }
    this.cmbDoiXe.removeAllItems();
    this.taiGoiYDoiXe();
  }

  private String maHangDaChon() {
    Object sel = this.cmbHangXe.getSelectedItem();
    return sel instanceof HangXe ? ((HangXe) sel).getMaHangXe() : null;
  }

  private String maDongDaChon() {
    Object sel = this.cmbDongXe.getSelectedItem();
    return sel instanceof DongXe ? ((DongXe) sel).getMaDongXe() : null;
  }

  private String maDoiDaChon() {
    Object sel = this.cmbDoiXe.getSelectedItem();
    return sel instanceof DoiXe ? ((DoiXe) sel).getMaDoiXe() : null;
  }

  private void chonHangXe(String maHangXe) {
    if (maHangXe == null) {
      return;
    }
    for (int i = 0; i < this.cmbHangXe.getItemCount(); i++) {
      HangXe item = this.cmbHangXe.getItemAt(i);
      if (item != null && maHangXe.equals(item.getMaHangXe())) {
        this.cmbHangXe.setSelectedIndex(i);
        return;
      }
    }
  }

  private void chonDongXe(String maDongXe) {
    if (maDongXe == null) {
      return;
    }
    for (int i = 0; i < this.cmbDongXe.getItemCount(); i++) {
      DongXe item = this.cmbDongXe.getItemAt(i);
      if (item != null && maDongXe.equals(item.getMaDongXe())) {
        this.cmbDongXe.setSelectedIndex(i);
        return;
      }
    }
  }

  private void chonDoiXe(String maDoiXe) {
    if (maDoiXe == null) {
      return;
    }
    for (int i = 0; i < this.cmbDoiXe.getItemCount(); i++) {
      DoiXe item = this.cmbDoiXe.getItemAt(i);
      if (item != null && maDoiXe.equals(item.getMaDoiXe())) {
        this.cmbDoiXe.setSelectedIndex(i);
        return;
      }
    }
  }

  private void taiGoiYHangXe() {
    boolean cu = this.dangNapDanhSach;
    this.dangNapDanhSach = true;
    try {
      try {
        this.cmbHangXe.removeAllItems();
        for (HangXe hangXe : this.danhMucXeService.getDanhSachHangXe()) {
          this.cmbHangXe.addItem(hangXe);
        }
        this.cmbHangXe.setSelectedIndex(-1);
        this.hangXeDaNap = this.maHangDaChon() == null ? "" : this.maHangDaChon();
      } catch (SQLException ex) {
        LOGGER.log(Level.SEVERE, "Lỗi nạp gợi ý hãng xe", ex);
        ThongBao.loi(this, "app.tiepNhan.loiKetNoiXe");
      }
    } finally {
      this.dangNapDanhSach = cu;
    }
  }

  private void taiGoiYDongXe() {
    boolean cu = this.dangNapDanhSach;
    this.dangNapDanhSach = true;
    try {
      try {
        this.cmbDongXe.removeAllItems();
        for (DongXe dongXe : this.danhMucXeService.getDanhSachDongXe(this.maHangDaChon())) {
          this.cmbDongXe.addItem(dongXe);
        }
        this.cmbDongXe.setSelectedIndex(-1);
      } catch (SQLException ex) {
        LOGGER.log(Level.SEVERE, "Lỗi nạp gợi ý dòng xe", ex);
        ThongBao.loi(this, "app.tiepNhan.loiKetNoiXe");
      }
    } finally {
      this.dangNapDanhSach = cu;
    }
  }

  private void taiGoiYDoiXe() {
    boolean cu = this.dangNapDanhSach;
    this.dangNapDanhSach = true;
    try {
      try {
        this.cmbDoiXe.removeAllItems();
        for (DoiXe doiXe : this.danhMucXeService.getDanhSachDoiXe(this.maDongDaChon())) {
          this.cmbDoiXe.addItem(doiXe);
        }
        this.cmbDoiXe.setSelectedIndex(-1);
      } catch (SQLException ex) {
        LOGGER.log(Level.SEVERE, "Lỗi nạp gợi ý đời xe", ex);
        ThongBao.loi(this, "app.tiepNhan.loiKetNoiXe");
      }
    } finally {
      this.dangNapDanhSach = cu;
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
      this.cmbSdt.xoa();
      this.txtHoTen.setText("");
      this.cmbBienSo.xoa();
      this.cmbHangXe.removeAllItems();
      this.cmbDongXe.removeAllItems();
      this.cmbDoiXe.removeAllItems();
      this.hangXeDaNap = "";
      this.txtOdo.setText("");
      this.txaMoTa.setText("");
    } finally {
      this.dangCapNhat = false;
    }
    this.cmbSdt.requestFocusInWindow();
  }

  private TiepNhanFormData getFormData() {
    return new TiepNhanFormData(
            this.cmbSdt.getTuKhoa().trim(),
            this.txtHoTen.getText().trim(),
            this.cmbBienSo.getTuKhoa().trim(),
            this.maHangDaChon(),
            this.maDongDaChon(),
            this.maDoiDaChon(),
            NumberParseUtil.toIntegerOrNull(this.txtOdo.getText()),
            this.txaMoTa.getText().trim());
  }

  private boolean kiemTraForm() {
    if (this.cmbSdt.getTuKhoa().trim().isEmpty()) {
      ThongBao.thongTin(this, "app.tiepNhan.loiSdtTrong");
      this.cmbSdt.requestFocusInWindow();
      return false;
    }
    if (this.txtHoTen.getText().trim().isEmpty()) {
      ThongBao.thongTin(this, "app.tiepNhan.loiHoTenTrong");
      this.txtHoTen.requestFocusInWindow();
      return false;
    }
    if (this.cmbBienSo.getTuKhoa().trim().isEmpty()) {
      ThongBao.thongTin(this, "app.tiepNhan.loiBienSoTrong");
      this.cmbBienSo.requestFocusInWindow();
      return false;
    }
    if (this.maDoiDaChon() == null) {
      ThongBao.thongTin(this, "app.danhMucXe.chuaChonDoi");
      this.cmbDoiXe.requestFocusInWindow();
      return false;
    }
    if (!this.txtOdo.getText().trim().isEmpty()) {
      try {
        long odo = Long.parseLong(this.txtOdo.getText().trim());
        if (odo < 0)
          throw new NumberFormatException();
      } catch (NumberFormatException e) {
        ThongBao.thongTin(this, "app.tiepNhan.loiOdo");
        this.txtOdo.requestFocusInWindow();
        return false;
      }
    }
    if (this.txaMoTa.getText().trim().isEmpty()) {
      ThongBao.thongTin(this, "app.tiepNhan.loiMoTa");
      this.txaMoTa.requestFocusInWindow();
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
