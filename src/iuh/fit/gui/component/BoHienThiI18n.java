package iuh.fit.gui.component;

import iuh.fit.enumeration.I18nEnum;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import java.awt.Component;

/**
 * Renderer combo hiển thị nhãn i18n cho {@link I18nEnum} và nhãn "tất cả" cho giá trị {@code null}.
 */
public final class BoHienThiI18n {

    private BoHienThiI18n() {
        throw new AssertionError("Utility class");
    }

    public static DefaultListCellRenderer tao() {
        return new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value == null) {
                    setText(I18nReaderUtil.get("app.common.all"));
                } else if (value instanceof I18nEnum i18nEnum) {
                    setText(i18nEnum.getI18NBusinessName());
                }

                return this;
            }
        };
    }
}
