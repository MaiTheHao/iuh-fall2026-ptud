package iuh.fit.gui.form;

import iuh.fit.dto.PhieuTiepNhanView;
import iuh.fit.entity.VanDeKyThuat;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.component.FormValidationHelper;
import iuh.fit.gui.theme.Theme;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Form lập chẩn đoán kỹ thuật cho một phiếu tiếp nhận.
 */
public final class ChanDoanTaoFormPanel extends FormFieldPanel {

  private static final long serialVersionUID = 1L;

  private static final int DO_DAI_TIEU_DE_TOI_DA = 200;
  private static final int DO_DAI_NOI_DUNG_TOI_DA = 1000;
  private static final int MUC_DO_NHO_NHAT = 1;
  private static final int MUC_DO_LON_NHAT = 100;

  private final JTextField maPhieuField = new JTextField(14);
  private final JTextField bienSoField = new JTextField(14);
  private final JTextField tieuDeField = new JTextField(14);
  private final JTextField mucDoField = new JTextField(14);
  private final JTextArea moTaArea = new JTextArea(4, 20);
  private final JTextArea giaiPhapArea = new JTextArea(4, 20);

  public ChanDoanTaoFormPanel() {
    Theme.applyReadOnly(this.maPhieuField);
    Theme.applyReadOnly(this.bienSoField);
    this.maPhieuField.setFocusable(false);
    this.bienSoField.setFocusable(false);
    this.mucDoField.setHorizontalAlignment(SwingConstants.RIGHT);
    this.mucDoField.setText("50");

    JScrollPane moTaScroll = FormFieldPanel.taoKhungMoTa(this.moTaArea);
    JScrollPane giaiPhapScroll = FormFieldPanel.taoKhungMoTa(this.giaiPhapArea);

    this.addField("app.chanDoan.ma", this.maPhieuField);
    this.addField("app.chanDoan.bienSo", this.bienSoField);
    this.addField("app.chanDoan.tieuDe", this.tieuDeField);
    this.addField("app.chanDoan.mucDoUuTien", this.mucDoField);
    this.addField("app.chanDoan.moTa", moTaScroll);
    this.addField("app.chanDoan.giaiPhap", giaiPhapScroll);

    FormValidationHelper.autoClearOnInput(this.tieuDeField);
    FormValidationHelper.autoClearOnInput(this.mucDoField);
  }

  public void napPhieu(PhieuTiepNhanView view) {
    this.maPhieuField.setText(view.maPhieuTiepNhan() == null ? "" : view.maPhieuTiepNhan());
    this.bienSoField.setText(view.bienSo() == null ? "" : view.bienSo());
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

  public VanDeKyThuat taoVanDe() {
    VanDeKyThuat vanDe = new VanDeKyThuat();
    vanDe.setMaPhieuTiepNhan(this.maPhieuField.getText().trim());
    vanDe.setTieuDe(this.tieuDeField.getText().trim());
    vanDe.setMucDoUuTien(this.docMucDo() == null ? 0 : this.docMucDo());
    vanDe.setMoTa(this.moTaArea.getText().trim());
    vanDe.setGiaiPhap(this.giaiPhapArea.getText().trim());
    return vanDe;
  }

  private Integer docMucDo() {
    try {
      return Integer.valueOf(this.mucDoField.getText().trim());
    } catch (NumberFormatException ex) {
      return null;
    }
  }
}
