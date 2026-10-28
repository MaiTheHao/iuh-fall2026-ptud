package iuh.fit.gui.form;

import iuh.fit.entity.NhanVien;

import java.util.List;

/**
 * Form tạo mới phân công ca: mã phân công do hệ thống sinh nên không hiển thị.
 */
public final class PhanCongCaTaoFormPanel extends PhanCongCaFormPanel {

  private static final long serialVersionUID = 1L;

  public PhanCongCaTaoFormPanel(List<NhanVien> danhSachNhanVien) {
    super(danhSachNhanVien);
    this.dungBoCuc();
  }

  @Override
  protected void dungBoCuc() {
    this.addField("app.schedule.employee", this.nhanVienCombo);
    this.addField("app.schedule.date", this.ngayLamPicker);
    this.addField("app.schedule.shift", this.caLamCombo);
    this.addField("app.schedule.note", this.ghiChuField);
  }
}
