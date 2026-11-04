package iuh.fit.gui.form;

import iuh.fit.entity.ChiTietPhieuNhapPhuTung;
import iuh.fit.entity.PhieuNhapPhuTung;
import iuh.fit.gui.Theme;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.Dimension;
import java.text.DecimalFormat;

public final class PhieuNhapChiTietFormPanel extends FormFieldPanel {

  

  private static final DecimalFormat CURRENCY = new DecimalFormat("#,##0");

  private final JTextField txtMaPhieu = new JTextField(14);
  private final JTextField txtMoTa = new JTextField(14);
  private final JTextField txtThuKho = new JTextField(14);
  private final JTextField txtNgayTao = new JTextField(14);
  private final JTextField txtTongChiPhi = new JTextField(14);

  private final DefaultTableModel modelChiTiet = new DefaultTableModel(
      new String[]{
          I18nReaderUtil.get("app.phuTung.ma"),
          I18nReaderUtil.get("app.phieuNhap.soLuong"),
          I18nReaderUtil.get("app.phieuNhap.donGia"),
          I18nReaderUtil.get("app.phieuNhap.thanhTien")
      }, 0) {

    

    @Override
    public boolean isCellEditable(int row, int column) {
      return false;
    }
  };

  private final JTable tblChiTiet = new JTable(this.modelChiTiet);

  public PhieuNhapChiTietFormPanel() {
    for (JTextField field : new JTextField[]{
        this.txtMaPhieu, this.txtMoTa, this.txtThuKho,
        this.txtNgayTao, this.txtTongChiPhi}) {
      Theme.applyReadOnly(field);
      field.setFocusable(false);
    }

    this.addField("app.phieuNhap.maPhieuNhap", this.txtMaPhieu);
    this.addField("app.phieuNhap.moTa", this.txtMoTa);
    this.addField("app.phieuNhap.thuKho", this.txtThuKho);
    this.addField("app.phieuNhap.ngayTao", this.txtNgayTao);
    this.addField("app.phieuNhap.tongChiPhi", this.txtTongChiPhi);

    Theme.styleTable(this.tblChiTiet);
    this.tblChiTiet.setFocusable(false);
    this.tblChiTiet.setRowHeight(34);
    this.tblChiTiet.getColumnModel().getColumn(0).setPreferredWidth(180);
    this.tblChiTiet.getColumnModel().getColumn(1).setPreferredWidth(100);
    this.tblChiTiet.getColumnModel().getColumn(2).setPreferredWidth(140);
    this.tblChiTiet.getColumnModel().getColumn(3).setPreferredWidth(150);

    JScrollPane scroll = new JScrollPane(this.tblChiTiet);
    scroll.setPreferredSize(new Dimension(600, 220));
    scroll.setBorder(Theme.titledBorder("app.phieuNhap.danhSachChiTiet"));
    this.addBlock(scroll);
  }

  public void napDuLieu(PhieuNhapPhuTung phieu, String ngayTao, String tongChiPhi) {
    if (phieu == null) {
      return;
    }

    this.txtMaPhieu.setText(phieu.getMaPhieuNhap() == null ? "" : phieu.getMaPhieuNhap());
    this.txtMoTa.setText(phieu.getMoTa() == null ? "" : phieu.getMoTa());
    this.txtThuKho.setText(phieu.getNhanVien() == null ? "" : phieu.getNhanVien().getMaNhanVien());
    this.txtNgayTao.setText(ngayTao == null ? "" : ngayTao);
    this.txtTongChiPhi.setText(tongChiPhi == null ? "" : tongChiPhi);

    this.modelChiTiet.setRowCount(0);
    if (phieu.getDanhSachChiTietPhieuNhap() == null) {
      return;
    }

    for (ChiTietPhieuNhapPhuTung chiTiet : phieu.getDanhSachChiTietPhieuNhap()) {
      double donGia = chiTiet.getDonGiaNhap() == null ? 0.0 : chiTiet.getDonGiaNhap();
      this.modelChiTiet.addRow(new Object[]{
          chiTiet.getMaPhuTung(),
          chiTiet.getSoLuong(),
          CURRENCY.format(donGia),
          CURRENCY.format(chiTiet.getSoLuong() * donGia)
      });
    }
  }
}
