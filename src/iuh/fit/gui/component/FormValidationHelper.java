package iuh.fit.gui.component;

import com.formdev.flatlaf.FlatClientProperties;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

public final class FormValidationHelper {

  private FormValidationHelper() {
    throw new AssertionError("Utility class");
  }

  public static void markError(JComponent comp, String tooltipMessage) {
    if (comp == null) {
      return;
    }

    comp.putClientProperty(FlatClientProperties.OUTLINE, FlatClientProperties.OUTLINE_ERROR);

    if (tooltipMessage != null && !tooltipMessage.isBlank()) {
      comp.setToolTipText(tooltipMessage);
    }
  }

  public static void clearError(JComponent comp) {
    if (comp == null) {
      return;
    }

    comp.putClientProperty(FlatClientProperties.OUTLINE, null);
    comp.setToolTipText(null);
  }

  public static boolean kiemTra(JComponent comp, boolean hopLe, String tooltipKey) {
    if (hopLe) {
      clearError(comp);
      return true;
    }

    markError(comp, I18nReaderUtil.get(tooltipKey));
    return false;
  }

  public static void autoClearOnInput(JTextComponent comp) {
    if (comp == null) {
      return;
    }

    comp.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        clearError(comp);
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        clearError(comp);
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        clearError(comp);
      }
    });
  }
}
