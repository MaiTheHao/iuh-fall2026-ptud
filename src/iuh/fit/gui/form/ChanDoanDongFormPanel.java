package iuh.fit.gui.form;

import iuh.fit.dto.ChanDoanDong;
import iuh.fit.gui.component.FormFieldPanel;
import iuh.fit.gui.component.FormValidationHelper;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public final class ChanDoanDongFormPanel extends FormFieldPanel {

  

  private static final int DO_DAI_TIEU_DE_TOI_DA = 200;
  private static final int DO_DAI_NOI_DUNG_TOI_DA = 1000;
  private static final int MUC_DO_NHO_NHAT = 1;
  private static final int MUC_DO_LON_NHAT = 100;

  private final JTextField txtTieuDe = new JTextField(14);
  private final JTextField txtMucDo = new JTextField(14);
  private final JTextArea txaMoTa = new JTextArea(4, 40);
  private final JTextArea txaGiaiPhap = new JTextArea(4, 40);
  private String maVanDe;

  public ChanDoanDongFormPanel() {
    this.txtMucDo.setHorizontalAlignment(SwingConstants.RIGHT);
    this.txtMucDo.setText("50");

    JScrollPane moTaScroll = FormFieldPanel.taoKhungMoTa(this.txaMoTa);
    JScrollPane giaiPhapScroll = FormFieldPanel.taoKhungMoTa(this.txaGiaiPhap);

    this.addField("app.chanDoan.tieuDe", this.txtTieuDe);
    this.addField("app.chanDoan.mucDoUuTien", this.txtMucDo);
    this.addField("app.chanDoan.moTa", moTaScroll);
    this.addField("app.chanDoan.giaiPhap", giaiPhapScroll);

    FormValidationHelper.autoClearOnInput(this.txtTieuDe);
    FormValidationHelper.autoClearOnInput(this.txtMucDo);
  }

  public void napDuLieu(ChanDoanDong dong) {
    if (dong == null) {
      this.maVanDe = null;
      this.txtTieuDe.setText("");
      this.txtMucDo.setText("50");
      this.txaMoTa.setText("");
      this.txaGiaiPhap.setText("");
      return;
    }

    this.maVanDe = dong.maVanDe();
    this.txtTieuDe.setText(dong.tieuDe() == null ? "" : dong.tieuDe());
    this.txtMucDo.setText(String.valueOf(dong.mucDoUuTien()));
    this.txaMoTa.setText(dong.moTa() == null ? "" : dong.moTa());
    this.txaGiaiPhap.setText(dong.giaiPhap() == null ? "" : dong.giaiPhap());
  }

  public ChanDoanDong layDuLieu() {
    Integer mucDo = this.docMucDo();
    return new ChanDoanDong(
            this.maVanDe,
            this.txtTieuDe.getText().trim(),
            this.txaMoTa.getText().trim(),
            mucDo == null ? 0 : mucDo,
            this.txaGiaiPhap.getText().trim(),
            false);
  }

  public boolean validateInput() {
    JComponent loiDauTien = null;

    String tieuDe = this.txtTieuDe.getText().trim();

    if (tieuDe.isEmpty()) {
      FormValidationHelper.markError(this.txtTieuDe,
              I18nReaderUtil.get("app.chanDoan.loiTieuDe"));
      loiDauTien = this.txtTieuDe;
    } else if (tieuDe.length() > DO_DAI_TIEU_DE_TOI_DA) {
      FormValidationHelper.markError(this.txtTieuDe,
              I18nReaderUtil.get("app.chanDoan.loiTieuDeDai"));
      loiDauTien = this.txtTieuDe;
    } else {
      FormValidationHelper.clearError(this.txtTieuDe);
    }

    Integer mucDo = this.docMucDo();

    if (mucDo == null || mucDo < MUC_DO_NHO_NHAT || mucDo > MUC_DO_LON_NHAT) {
      FormValidationHelper.markError(this.txtMucDo,
              I18nReaderUtil.get("app.chanDoan.loiMucDoUuTien"));
      if (loiDauTien == null) {
        loiDauTien = this.txtMucDo;
      }
    } else {
      FormValidationHelper.clearError(this.txtMucDo);
    }

    if (this.txaMoTa.getText().trim().length() > DO_DAI_NOI_DUNG_TOI_DA) {
      FormValidationHelper.markError(this.txaMoTa,
              I18nReaderUtil.get("app.chanDoan.loiMoTaDai"));
      if (loiDauTien == null) {
        loiDauTien = this.txaMoTa;
      }
    } else {
      FormValidationHelper.clearError(this.txaMoTa);
    }

    if (this.txaGiaiPhap.getText().trim().length() > DO_DAI_NOI_DUNG_TOI_DA) {
      FormValidationHelper.markError(this.txaGiaiPhap,
              I18nReaderUtil.get("app.chanDoan.loiGiaiPhapDai"));
      if (loiDauTien == null) {
        loiDauTien = this.txaGiaiPhap;
      }
    } else {
      FormValidationHelper.clearError(this.txaGiaiPhap);
    }

    if (loiDauTien != null) {
      loiDauTien.requestFocusInWindow();
      return false;
    }

    return true;
  }

  private Integer docMucDo() {
    try {
      return Integer.valueOf(this.txtMucDo.getText().trim());
    } catch (NumberFormatException ex) {
      return null;
    }
  }
}
