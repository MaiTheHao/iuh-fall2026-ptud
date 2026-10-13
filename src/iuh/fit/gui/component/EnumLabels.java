package iuh.fit.gui.component;

import iuh.fit.core.context.AppContext;
import iuh.fit.util.I18nReaderUtil;

public final class EnumLabels {

    private EnumLabels() {
        throw new AssertionError("Utility class");
    }

    public static String text(Enum<?> value) {
        if (value == null) {
            return "";
        }
        String key = "app.enum." + value.getDeclaringClass().getSimpleName() + "." + value.name();
        return I18nReaderUtil.get(key, AppContext.getInstance().getLocale(), value.name());
    }
}
