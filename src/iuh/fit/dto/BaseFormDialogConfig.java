package iuh.fit.dto;

import javax.swing.JComponent;

import java.awt.Window;
import java.util.function.BooleanSupplier;

public record BaseFormDialogConfig(
        Window owner,
        String titleKey,
        JComponent noiDungForm,
        BooleanSupplier xuLyLuu,
        int chieuCao,
        boolean chiDoc) {

  /** Dialog nhập liệu thông thường. */
  public BaseFormDialogConfig(Window owner,
          String titleKey,
          JComponent noiDungForm,
          BooleanSupplier xuLyLuu,
          int chieuCao) {
    this(owner, titleKey, noiDungForm, xuLyLuu, chieuCao, false);
  }
}
