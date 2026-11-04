package iuh.fit.gui.form;

import iuh.fit.entity.NhanVien;

import java.util.List;

public final class PhanCongCaTaoFormPanel extends PhanCongCaFormPanel {

  

  public PhanCongCaTaoFormPanel(List<NhanVien> danhSachNhanVien) {
    super(danhSachNhanVien);
    this.dungBoCuc();
  }

  @Override
  protected void dungBoCuc() {
    this.addField("app.schedule.employee", this.cmbNhanVien);
    this.addField("app.schedule.date", this.ngayLamPicker);
    this.addField("app.schedule.shift", this.cmbCaLam);
    this.addField("app.schedule.note", this.txtGhiChu);
  }
}
