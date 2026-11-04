package iuh.fit.gui.form;

import iuh.fit.entity.ChiTietPhieuNhapPhuTung;
import iuh.fit.entity.PhieuNhapPhuTung;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.theme.Theme;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.Dimension;
import java.text.DecimalFormat;

/**
 * Form chỉ đọc hiển thị chi tiết phiếu nhập: thông tin đầu phiếu và các dòng phụ tùng.
 */
public final class PhieuNhapChiTietFormPanel extends FormFieldPanel {

  private static final long serialVersionUID = 1L;

  private static final DecimalFormat CURRENCY = new DecimalFormat("#,##0");

  private final JTextField maPhieuField = new JTextField(14);
  private final JTextField moTaField = new JTextField(14);
  private final JTextField thuKhoField = new JTextField(14);
  private final JTextField ngayTaoField = new JTextField(14);
  private final JTextField tongChiPhiField = new JTextField(14);

  private final DefaultTableModel modelChiTiet = new DefaultTableModel(
      new String[]{
          I18nReaderUtil.get("app.phuTung.ma"),
          I18nReaderUtil.get("app.phieuNhap.soLuong"),
          I18nReaderUtil.get("app.phieuNhap.donGia"),
          I18nReaderUtil.get("app.phieuNhap.thanhTien")
      }, 0) {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean isCellEditable(int row, int column) {
      return false;
    }
  };

  private final JTable tableChiTiet = new JTable(this.modelChiTiet);

  public PhieuNhapChiTietFormPanel() {
    for (JTextField field : new JTextField[]{
        this.maPhieuField, this.moTaField, this.thuKhoField,
        this.ngayTaoField, this.tongChiPhiField}) {
      Theme.applyReadOnly(field);
      field.setFocusable(false);
    }

    this.addField("app.phieuNhap.maPhieuNhap", this.maPhieuField);
    this.addField("app.phieuNhap.moTa", this.moTaField);
    this.addField("app.phieuNhap.thuKho", this.thuKhoField);
    this.addField("app.phieuNhap.ngayTao", this.ngayTaoField);
    this.addField("app.phieuNhap.tongChiPhi", this.tongChiPhiField);

    Theme.styleTable(this.tableChiTiet);
    this.tableChiTiet.setFocusable(false);
    this.tableChiTiet.setRowHeight(34);
    this.tableChiTiet.getColumnModel().getColumn(0).setPreferredWidth(180);
    this.tableChiTiet.getColumnModel().getColumn(1).setPreferredWidth(100);
    this.tableChiTiet.getColumnModel().getColumn(2).setPreferredWidth(140);
    this.tableChiTiet.getColumnModel().getColumn(3).setPreferredWidth(150);

    JScrollPane scroll = new JScrollPane(this.tableChiTiet);
    scroll.setPreferredSize(new Dimension(600, 220));
    scroll.setBorder(Theme.titledBorder("app.phieuNhap.danhSachChiTiet"));
    this.addBlock(scroll);
  }

  public void napDuLieu(PhieuNhapPhuTung phieu, String ngayTao, String tongChiPhi) {
    if (phieu == null) {
      return;
    }

    this.maPhieuField.setText(phieu.getMaPhieuNhap() == null ? "" : phieu.getMaPhieuNhap());
    this.moTaField.setText(phieu.getMoTa() == null ? "" : phieu.getMoTa());
    this.thuKhoField.setText(phieu.getNhanVien() == null ? "" : phieu.getNhanVien().getMaNhanVien());
    this.ngayTaoField.setText(ngayTao == null ? "" : ngayTao);
    this.tongChiPhiField.setText(tongChiPhi == null ? "" : tongChiPhi);

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
