package iuh.fit.gui.form;

import iuh.fit.dto.ChanDoanUpdateFormData;
import iuh.fit.dto.ChanDoanView;
import iuh.fit.enumeration.TrangThaiVanDeKyThuat;
import iuh.fit.gui.component.BoHienThiI18n;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.component.FormValidationHelper;
import iuh.fit.gui.theme.Theme;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Form cập nhật chẩn đoán kỹ thuật đã có.
 */
public final class ChanDoanSuaFormPanel extends FormFieldPanel {

  private static final long serialVersionUID = 1L;

  private static final int DO_DAI_TIEU_DE_TOI_DA = 200;
  private static final int DO_DAI_NOI_DUNG_TOI_DA = 1000;
  private static final int MUC_DO_NHO_NHAT = 1;
  private static final int MUC_DO_LON_NHAT = 100;

  private final JTextField maVanDeField = new JTextField(14);
  private final JTextField maPhieuField = new JTextField(14);
  private final JTextField bienSoField = new JTextField(14);
  private final JTextField tieuDeField = new JTextField(14);
  private final JTextField mucDoField = new JTextField(14);
  private final JTextArea moTaArea = new JTextArea(4, 20);
  private final JTextArea giaiPhapArea = new JTextArea(4, 20);
  private final JComboBox<TrangThaiVanDeKyThuat> khachHangDongYCombo =
          new JComboBox<>(TrangThaiVanDeKyThuat.values());

  public ChanDoanSuaFormPanel() {
    Theme.applyReadOnly(this.maVanDeField);
    Theme.applyReadOnly(this.maPhieuField);
    Theme.applyReadOnly(this.bienSoField);
    this.maVanDeField.setFocusable(false);
    this.maPhieuField.setFocusable(false);
    this.bienSoField.setFocusable(false);
    this.mucDoField.setHorizontalAlignment(SwingConstants.RIGHT);
    this.khachHangDongYCombo.setRenderer(BoHienThiI18n.tao());

    JScrollPane moTaScroll = FormFieldPanel.taoKhungMoTa(this.moTaArea);
    JScrollPane giaiPhapScroll = FormFieldPanel.taoKhungMoTa(this.giaiPhapArea);

    this.addField("app.chanDoan.maVanDe", this.maVanDeField);
    this.addField("app.chanDoan.phieuTiepNhan", this.maPhieuField);
    this.addField("app.chanDoan.bienSo", this.bienSoField);
    this.addField("app.chanDoan.tieuDe", this.tieuDeField);
    this.addField("app.chanDoan.mucDoUuTien", this.mucDoField);
    this.addField("app.chanDoan.moTa", moTaScroll);
    this.addField("app.chanDoan.giaiPhap", giaiPhapScroll);
    this.addField("app.chanDoan.khachHangDongY", this.khachHangDongYCombo);

    FormValidationHelper.autoClearOnInput(this.tieuDeField);
    FormValidationHelper.autoClearOnInput(this.mucDoField);
  }

  public void napDuLieu(ChanDoanView view) {
    if (view == null) {
      return;
    }
    this.maVanDeField.setText(view.maVanDe() == null ? "" : view.maVanDe());
    this.maPhieuField.setText(view.maPhieuTiepNhan() == null ? "" : view.maPhieuTiepNhan());
    this.bienSoField.setText(view.bienSo() == null ? "" : view.bienSo());
    this.tieuDeField.setText(view.tieuDe() == null ? "" : view.tieuDe());
    this.mucDoField.setText(String.valueOf(view.mucDoUuTien()));
    this.moTaArea.setText(view.moTa() == null ? "" : view.moTa());
    this.giaiPhapArea.setText(view.giaiPhap() == null ? "" : view.giaiPhap());
    this.khachHangDongYCombo.setSelectedItem(view.khachHangDongY());
  }

  public boolean validateInput() {
    JComponent loiDauTien = null;

    String tieuDe = this.tieuDeField.getText().trim();

    if (tieuDe.isEmpty()) {
      FormValidationHelper.markError(this.tieuDeField,
              I18nReaderUtil.get("app.chanDoan.loiTieuDe"));
      loiDauTien = this.tieuDeField;
    } else if (tieuDe.length() > DO_DAI_TIEU_DE_TOI_DA) {
      FormValidationHelper.markError(this.tieuDeField,
              I18nReaderUtil.get("app.chanDoan.loiTieuDeDai"));
      loiDauTien = this.tieuDeField;
    } else {
      FormValidationHelper.clearError(this.tieuDeField);
    }

    Integer mucDo = this.docMucDo();

    if (mucDo == null || mucDo < MUC_DO_NHO_NHAT || mucDo > MUC_DO_LON_NHAT) {
      FormValidationHelper.markError(this.mucDoField,
              I18nReaderUtil.get("app.chanDoan.loiMucDoUuTien"));
      if (loiDauTien == null) {
        loiDauTien = this.mucDoField;
      }
    } else {
      FormValidationHelper.clearError(this.mucDoField);
    }

    if (this.moTaArea.getText().trim().length() > DO_DAI_NOI_DUNG_TOI_DA) {
      FormValidationHelper.markError(this.moTaArea,
              I18nReaderUtil.get("app.chanDoan.loiMoTaDai"));
      if (loiDauTien == null) {
        loiDauTien = this.moTaArea;
      }
    } else {
      FormValidationHelper.clearError(this.moTaArea);
    }

    if (this.giaiPhapArea.getText().trim().length() > DO_DAI_NOI_DUNG_TOI_DA) {
      FormValidationHelper.markError(this.giaiPhapArea,
              I18nReaderUtil.get("app.chanDoan.loiGiaiPhapDai"));
      if (loiDauTien == null) {
        loiDauTien = this.giaiPhapArea;
      }
    } else {
      FormValidationHelper.clearError(this.giaiPhapArea);
    }

    if (loiDauTien != null) {
      loiDauTien.requestFocusInWindow();
      return false;
    }

    return true;
  }

  public ChanDoanUpdateFormData layDuLieu() {
    Integer mucDo = this.docMucDo();
    return new ChanDoanUpdateFormData(
            this.maVanDeField.getText().trim(),
            this.tieuDeField.getText().trim(),
            this.moTaArea.getText().trim(),
            mucDo == null ? 0 : mucDo,
            this.giaiPhapArea.getText().trim(),
            (TrangThaiVanDeKyThuat) this.khachHangDongYCombo.getSelectedItem());
  }

  private Integer docMucDo() {
    try {
      return Integer.valueOf(this.mucDoField.getText().trim());
    } catch (NumberFormatException ex) {
      return null;
    }
  }
}
