package iuh.fit.gui.form;

import com.github.lgooddatepicker.components.DatePicker;
import iuh.fit.entity.NhanVien;
import iuh.fit.entity.PhanCong;
import iuh.fit.enumeration.CaLamViec;
import iuh.fit.gui.Theme;
import iuh.fit.gui.component.BoChonNgayGio;
import iuh.fit.gui.component.BoHienThiI18n;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.component.FormValidationHelper;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTextField;

import java.awt.Component;
import java.time.LocalDate;
import java.util.List;

/**
 * Form phân công ca làm việc cho nhân viên theo ngày.
 */
public class PhanCongCaFormPanel extends FormFieldPanel {

  protected final boolean tao;
  protected final JTextField txtMa = new JTextField(14);
  protected final JComboBox<NhanVien> cmbNhanVien = new JComboBox<>();
  protected final DatePicker ngayLamPicker = BoChonNgayGio.taoBoChonNgay();
  protected final JComboBox<CaLamViec> cmbCaLam = new JComboBox<>(CaLamViec.values());
  protected final JTextField txtGhiChu = new JTextField(14);

  public PhanCongCaFormPanel(List<NhanVien> danhSachNhanVien, boolean tao) {
    this.tao = tao;
    Theme.applyReadOnly(this.txtMa);
    this.txtMa.setFocusable(false);
    this.ngayLamPicker.getSettings().setFormatForDatesCommonEra("dd/MM/yyyy");
    this.cmbCaLam.setRenderer(BoHienThiI18n.tao());
    this.cmbNhanVien.setRenderer(new DefaultListCellRenderer() {

      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index,
              boolean isSelected, boolean cellHasFocus) {
        String hienThi = value instanceof NhanVien nhanVien
                // Kèm mã để phân biệt nhân viên trùng họ tên.
                ? nhanVien.getHoTen() + " (" + nhanVien.getMaNhanVien() + ")"
                : "";
        return super.getListCellRendererComponent(list, hienThi, index, isSelected, cellHasFocus);
      }
    });

    for (NhanVien nhanVien : danhSachNhanVien) {
      this.cmbNhanVien.addItem(nhanVien);
    }

    FormValidationHelper.autoClearOnInput(this.txtGhiChu);
    this.dungBoCuc();
  }

  protected void dungBoCuc() {
    if (!this.tao) {
      this.addField("app.schedule.assignmentId", this.txtMa);
    }
    this.addField("app.schedule.employee", this.cmbNhanVien);
    this.addField("app.schedule.date", this.ngayLamPicker);
    this.addField("app.schedule.shift", this.cmbCaLam);
    this.addField("app.schedule.note", this.txtGhiChu);
  }

  public void napDuLieu(PhanCong phanCong) {
    this.txtMa.setText(phanCong.getMaPhanCong() == null ? "" : phanCong.getMaPhanCong());
    this.ngayLamPicker.setDate(LocalDate.of(
            phanCong.getNam(), phanCong.getThang(), phanCong.getNgay()));
    this.cmbCaLam.setSelectedItem(phanCong.getCaLamViec());
    this.txtGhiChu.setText(phanCong.getGhiChu() == null ? "" : phanCong.getGhiChu());
    this.chonNhanVien(phanCong.getNhanVien() == null ? null : phanCong.getNhanVien().getMaNhanVien());
  }

  public DuLieu layDuLieu() {
    NhanVien nhanVien = (NhanVien) this.cmbNhanVien.getSelectedItem();

    return new DuLieu(
            this.txtMa.getText().trim(),
            nhanVien == null ? null : nhanVien.getMaNhanVien(),
            this.ngayLamPicker.getDate(),
            (CaLamViec) this.cmbCaLam.getSelectedItem(),
            this.txtGhiChu.getText().trim());
  }

  public boolean validateInput() {
    JComponent loiDauTien = null;

    if (this.cmbNhanVien.getSelectedItem() == null) {
      FormValidationHelper.markError(this.cmbNhanVien,
              I18nReaderUtil.get("app.schedule.employeeRequired"));
      loiDauTien = this.cmbNhanVien;
    } else {
      FormValidationHelper.clearError(this.cmbNhanVien);
    }

    if (this.ngayLamPicker.getDate() == null) {
      FormValidationHelper.markError(this.ngayLamPicker,
              I18nReaderUtil.get("app.schedule.dateRequired"));
      if (loiDauTien == null) {
        loiDauTien = this.ngayLamPicker;
      }
    } else {
      FormValidationHelper.clearError(this.ngayLamPicker);
    }

    if (this.cmbCaLam.getSelectedItem() == null) {
      FormValidationHelper.markError(this.cmbCaLam,
              I18nReaderUtil.get("app.schedule.shiftRequired"));
      if (loiDauTien == null) {
        loiDauTien = this.cmbCaLam;
      }
    } else {
      FormValidationHelper.clearError(this.cmbCaLam);
    }

    if (loiDauTien != null) {
      loiDauTien.requestFocusInWindow();
      return false;
    }

    return true;
  }

  private void chonNhanVien(String maNhanVien) {
    // Tìm theo mã vì danh sách có thể được nạp lại với thứ tự khác.
    for (int i = 0; i < this.cmbNhanVien.getItemCount(); i++) {
      NhanVien nhanVien = this.cmbNhanVien.getItemAt(i);

      if (nhanVien != null && nhanVien.getMaNhanVien() != null
              && nhanVien.getMaNhanVien().equals(maNhanVien)) {
        this.cmbNhanVien.setSelectedIndex(i);
        return;
      }
    }
  }

  /**
   * Dữ liệu form trả về trước khi Service ghép thành entity.
   */
  public record DuLieu(
          String maPhanCong,
          String maNhanVien,
          LocalDate ngayLam,
          CaLamViec caLamViec,
          String ghiChu) {
  }
}
