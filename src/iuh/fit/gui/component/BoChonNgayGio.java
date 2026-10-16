package iuh.fit.gui.component;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.DateTimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;
import iuh.fit.core.context.AppContext;
import iuh.fit.util.I18nReaderUtil;

import java.util.Locale;

public final class BoChonNgayGio {

    private BoChonNgayGio() {
        throw new AssertionError("Utility class");
    }

    public static DatePicker taoBoChonNgay() {
        Locale locale = AppContext.getInstance().getLocale().getLocale();
        DatePickerSettings settings = new DatePickerSettings(locale);
        settings.setFormatForDatesCommonEra(I18nReaderUtil.get("app.date.format"));
        settings.setTranslationToday(I18nReaderUtil.get("app.date.today"));
        settings.setTranslationClear(I18nReaderUtil.get("app.date.clear"));
        settings.setAllowKeyboardEditing(false);
        return new DatePicker(settings);
    }

    public static DateTimePicker taoBoChonNgayGio() {
        Locale locale = AppContext.getInstance().getLocale().getLocale();
        DatePickerSettings dateSettings = new DatePickerSettings(locale);
        dateSettings.setFormatForDatesCommonEra(I18nReaderUtil.get("app.date.format"));
        dateSettings.setTranslationToday(I18nReaderUtil.get("app.date.today"));
        dateSettings.setTranslationClear(I18nReaderUtil.get("app.date.clear"));
        dateSettings.setAllowKeyboardEditing(false);

        TimePickerSettings timeSettings = new TimePickerSettings(locale);
        timeSettings.use24HourClockFormat();
        timeSettings.setAllowKeyboardEditing(false);

        return new DateTimePicker(dateSettings, timeSettings);
    }
}
