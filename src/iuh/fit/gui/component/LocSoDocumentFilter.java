package iuh.fit.gui.component;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public final class LocSoDocumentFilter extends DocumentFilter {

  public LocSoDocumentFilter() {
    super();
  }

  @Override
  public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
          throws BadLocationException {
    if (string == null) {
      return;
    }
    StringBuilder proposed = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
    proposed.insert(offset, string);
    if (proposed.toString().matches("\\d*")) {
      super.insertString(fb, offset, string, attr);
    }
  }

  @Override
  public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
          throws BadLocationException {
    StringBuilder proposed = new StringBuilder(fb.getDocument().getText(0, fb.getDocument().getLength()));
    proposed.replace(offset, offset + length, text == null ? "" : text);
    if (proposed.toString().matches("\\d*")) {
      super.replace(fb, offset, length, text, attrs);
    }
  }
}
