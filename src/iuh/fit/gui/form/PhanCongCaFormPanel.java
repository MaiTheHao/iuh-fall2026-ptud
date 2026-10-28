package iuh.fit.gui.form;

import com.github.lgooddatepicker.components.DatePicker;
import iuh.fit.entity.NhanVien;
import iuh.fit.entity.PhanCong;
import iuh.fit.enumeration.CaLamViec;
import iuh.fit.gui.component.BoChonNgayGio;
import iuh.fit.gui.component.BoHienThiI18n;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.component.FormValidationHelper;
import iuh.fit.gui.theme.Theme;
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
 * Phần dùng chung của form phân công ca: trường nhập liệu, đọc/ghi dữ liệu và kiểm tra hợp lệ.
 * Lớp con quyết định bố cục cho dialog tạo mới hoặc cập nhật.
 */
public abstract class PhanCongCaFormPanel extends FormFieldPanel {

  private static final long serialVersionUID = 1L;

  protected final JTextField maField = new JTextField(14);
  protected final JComboBox<NhanVien> nhanVienCombo = new JComboBox<>();
  protected final DatePicker ngayLamPicker = BoChonNgayGio.taoBoChonNgay();
  protected final JComboBox<CaLamViec> caLamCombo = new JComboBox<>(CaLamViec.values());
  protected final JTextField ghiChuField = new JTextField(14);

  protected PhanCongCaFormPanel(List<NhanVien> danhSachNhanVien) {
    Theme.applyReadOnly(this.maField);
    this.maField.setFocusable(false);
    this.ngayLamPicker.getSettings().setFormatForDatesCommonEra("dd/MM/yyyy");
    this.caLamCombo.setRenderer(BoHienThiI18n.tao());
    this.nhanVienCombo.setRenderer(new DefaultListCellRenderer() {
      private static final long serialVersionUID = 1L;

      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index,
              boolean isSelected, boolean cellHasFocus) {
        String hienThi = value instanceof NhanVien nhanVien
                ? nhanVien.getHoTen() + " (" + nhanVien.getMaNhanVien() + ")"
                : "";
        return super.getListCellRendererComponent(list, hienThi, index, isSelected, cellHasFocus);
      }
    });

    for (NhanVien nhanVien : danhSachNhanVien) {
      this.nhanVienCombo.addItem(nhanVien);
    }

    FormValidationHelper.autoClearOnInput(this.ghiChuField);
  }

  /** Lớp con dựng bố cục trong constructor của chính nó. */
  protected abstract void dungBoCuc();

  public void napDuLieu(PhanCong phanCong) {
    this.maField.setText(phanCong.getMaPhanCong() == null ? "" : phanCong.getMaPhanCong());
    this.ngayLamPicker.setDate(LocalDate.of(
            phanCong.getNam(), phanCong.getThang(), phanCong.getNgay()));
    this.caLamCombo.setSelectedItem(phanCong.getCaLamViec());
    this.ghiChuField.setText(phanCong.getGhiChu() == null ? "" : phanCong.getGhiChu());
    this.chonNhanVien(phanCong.getMaNhanVien());
  }

  public DuLieu layDuLieu() {
    NhanVien nhanVien = (NhanVien) this.nhanVienCombo.getSelectedItem();

    return new DuLieu(
            this.maField.getText().trim(),
            nhanVien == null ? null : nhanVien.getMaNhanVien(),
            this.ngayLamPicker.getDate(),
            (CaLamViec) this.caLamCombo.getSelectedItem(),
            this.ghiChuField.getText().trim());
  }

  public boolean validateInput() {
    JComponent loiDauTien = null;

    if (this.nhanVienCombo.getSelectedItem() == null) {
      FormValidationHelper.markError(this.nhanVienCombo,
              I18nReaderUtil.get("app.schedule.employeeRequired"));
      loiDauTien = this.nhanVienCombo;
    } else {
      FormValidationHelper.clearError(this.nhanVienCombo);
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

    if (this.caLamCombo.getSelectedItem() == null) {
      FormValidationHelper.markError(this.caLamCombo,
              I18nReaderUtil.get("app.schedule.shiftRequired"));
      if (loiDauTien == null) {
        loiDauTien = this.caLamCombo;
      }
    } else {
      FormValidationHelper.clearError(this.caLamCombo);
    }

    if (loiDauTien != null) {
      loiDauTien.requestFocusInWindow();
      return false;
    }

    return true;
  }

  private void chonNhanVien(String maNhanVien) {
    for (int i = 0; i < this.nhanVienCombo.getItemCount(); i++) {
      NhanVien nhanVien = this.nhanVienCombo.getItemAt(i);

      if (nhanVien != null && nhanVien.getMaNhanVien() != null
              && nhanVien.getMaNhanVien().equals(maNhanVien)) {
        this.nhanVienCombo.setSelectedIndex(i);
        return;
      }
    }
  }

  public record DuLieu(
          String maPhanCong,
          String maNhanVien,
          LocalDate ngayLam,
          CaLamViec caLamViec,
          String ghiChu) {
  }
}
