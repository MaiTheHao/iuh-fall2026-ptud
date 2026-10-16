package iuh.fit.enumeration;

import iuh.fit.core.context.AppContext;
import iuh.fit.util.I18nReaderUtil;

public interface I18nEnum {

    default String getI18nKey() {
        Enum<?> value = (Enum<?>) this;
        return "app.enum." + value.getDeclaringClass().getSimpleName() + "." + value.name();
    }

    default String getI18NBusinessName() {
        Enum<?> value = (Enum<?>) this;
        return I18nReaderUtil.get(getI18nKey(), AppContext.getInstance().getLocale(), value.name());
    }
}
