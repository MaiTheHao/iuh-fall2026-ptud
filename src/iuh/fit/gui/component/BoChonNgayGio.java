package iuh.fit.gui.component;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.DateTimePicker;
import com.github.lgooddatepicker.components.TimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;
import iuh.fit.core.context.AppContext;
import iuh.fit.gui.theme.Theme;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.util.Locale;

public final class BoChonNgayGio {

    private BoChonNgayGio() {
        throw new AssertionError("Utility class");
    }

    public static DatePicker taoBoChonNgay() {
        DatePicker picker = new DatePicker(taoDateSettings());
        apDungMau(picker);
        return picker;
    }

    public static DateTimePicker taoBoChonNgayGio() {
        DateTimePicker picker = new DateTimePicker(taoDateSettings(), taoTimeSettings());
        apDungMau(picker);
        return picker;
    }

    private static DatePickerSettings taoDateSettings() {
        Locale locale = AppContext.getInstance().getLocale().getLocale();
        DatePickerSettings settings = new DatePickerSettings(locale);
        settings.setFormatForDatesCommonEra(I18nReaderUtil.get("app.date.format"));
        settings.setTranslationToday(I18nReaderUtil.get("app.date.today"));
        settings.setTranslationClear(I18nReaderUtil.get("app.date.clear"));
        settings.setAllowKeyboardEditing(false);
        return settings;
    }

    private static TimePickerSettings taoTimeSettings() {
        Locale locale = AppContext.getInstance().getLocale().getLocale();
        TimePickerSettings settings = new TimePickerSettings(locale);
        settings.use24HourClockFormat();
        settings.setAllowKeyboardEditing(false);
        return settings;
    }

    public static void apDungMau(DatePicker picker) {
        Theme.Palette p = Theme.palette();
        DatePickerSettings settings = picker.getSettings();

        picker.setBackground(p.INPUT_BG());
        apDungMauNut(picker.getComponentToggleCalendarButton(), p);

        apDungMauNenLich(settings, p);
        apDungMauChuLich(settings, p);
        apDungMauOText(settings, p);

        settings.setColorBackgroundWeekdayLabels(p.PANEL_ALT(), true);
        settings.setColorBackgroundWeekNumberLabels(p.PANEL_ALT(), true);
        settings.setBorderCalendarPopup(BorderFactory.createLineBorder(p.BORDER()));
    }

    private static void apDungMauNenLich(DatePickerSettings settings, Theme.Palette p) {
        settings.setColor(DatePickerSettings.DateArea.BackgroundOverallCalendarPanel, p.PANEL());
        settings.setColor(DatePickerSettings.DateArea.CalendarBackgroundNormalDates, p.PANEL());
        settings.setColor(DatePickerSettings.DateArea.CalendarBackgroundSelectedDate, p.ACCENT_SOFT());
        settings.setColor(DatePickerSettings.DateArea.CalendarBackgroundVetoedDates, p.DANGER_BG());
        settings.setColor(DatePickerSettings.DateArea.CalendarBorderSelectedDate, p.ACCENT());
        settings.setColor(DatePickerSettings.DateArea.BackgroundClearLabel, p.PANEL_ALT());
        settings.setColor(DatePickerSettings.DateArea.BackgroundTodayLabel, p.ACCENT_SOFT());
        settings.setColor(DatePickerSettings.DateArea.BackgroundMonthAndYearMenuLabels, p.PANEL_ALT());
        settings.setColor(DatePickerSettings.DateArea.BackgroundMonthAndYearNavigationButtons, p.PANEL_ALT());
        settings.setColor(DatePickerSettings.DateArea.BackgroundTopLeftLabelAboveWeekNumbers, p.PANEL_ALT());
        settings.setColor(DatePickerSettings.DateArea.BackgroundCalendarPanelLabelsOnHover, p.ACCENT_SOFT());
    }

    private static void apDungMauChuLich(DatePickerSettings settings, Theme.Palette p) {
        settings.setColor(DatePickerSettings.DateArea.CalendarTextNormalDates, p.INK());
        settings.setColor(DatePickerSettings.DateArea.CalendarTextWeekdays, p.INK());
        settings.setColor(DatePickerSettings.DateArea.TextClearLabel, p.INK());
        settings.setColor(DatePickerSettings.DateArea.TextTodayLabel, p.INK());
        settings.setColor(DatePickerSettings.DateArea.TextMonthAndYearMenuLabels, p.INK());
        settings.setColor(DatePickerSettings.DateArea.TextMonthAndYearNavigationButtons, p.INK());
        settings.setColor(DatePickerSettings.DateArea.TextCalendarPanelLabelsOnHover, p.INK());
        settings.setColor(DatePickerSettings.DateArea.CalendarDefaultTextHighlightedDates, p.INK());
        settings.setColor(DatePickerSettings.DateArea.CalendarTextWeekNumbers, p.INK_MUTED());
        settings.setColor(DatePickerSettings.DateArea.CalendarDefaultBackgroundHighlightedDates, p.ACCENT_SOFT());
    }

    private static void apDungMauOText(DatePickerSettings settings, Theme.Palette p) {
        settings.setColor(DatePickerSettings.DateArea.TextFieldBackgroundValidDate, p.INPUT_BG());
        settings.setColor(DatePickerSettings.DateArea.TextFieldBackgroundInvalidDate, p.INPUT_BG());
        settings.setColor(DatePickerSettings.DateArea.TextFieldBackgroundVetoedDate, p.INPUT_BG());
        settings.setColor(DatePickerSettings.DateArea.TextFieldBackgroundDisallowedEmptyDate, p.INPUT_BG());
        settings.setColor(DatePickerSettings.DateArea.TextFieldBackgroundDisabled, p.PANEL_ALT());
        settings.setColor(DatePickerSettings.DateArea.DatePickerTextValidDate, p.INK());
        settings.setColor(DatePickerSettings.DateArea.DatePickerTextInvalidDate, p.DANGER());
        settings.setColor(DatePickerSettings.DateArea.DatePickerTextVetoedDate, p.INK_MUTED());
        settings.setColor(DatePickerSettings.DateArea.DatePickerTextDisabled, p.INK_MUTED());
    }

    public static void apDungMau(TimePicker picker) {
        Theme.Palette p = Theme.palette();
        TimePickerSettings settings = picker.getSettings();

        picker.setBackground(p.INPUT_BG());
        apDungMauNut(picker.getComponentToggleTimeMenuButton(), p);
        apDungMauNut(picker.getComponentIncreaseSpinnerButton(), p);
        apDungMauNut(picker.getComponentDecreaseSpinnerButton(), p);

        settings.setColor(TimePickerSettings.TimeArea.TextFieldBackgroundValidTime, p.INPUT_BG());
        settings.setColor(TimePickerSettings.TimeArea.TextFieldBackgroundInvalidTime, p.INPUT_BG());
        settings.setColor(TimePickerSettings.TimeArea.TextFieldBackgroundVetoedTime, p.INPUT_BG());
        settings.setColor(TimePickerSettings.TimeArea.TextFieldBackgroundDisallowedEmptyTime, p.INPUT_BG());
        settings.setColor(TimePickerSettings.TimeArea.TextFieldBackgroundDisabled, p.PANEL_ALT());

        settings.setColor(TimePickerSettings.TimeArea.TimePickerTextValidTime, p.INK());
        settings.setColor(TimePickerSettings.TimeArea.TimePickerTextInvalidTime, p.DANGER());
        settings.setColor(TimePickerSettings.TimeArea.TimePickerTextVetoedTime, p.INK_MUTED());
        settings.setColor(TimePickerSettings.TimeArea.TimePickerTextDisabled, p.INK_MUTED());
    }

    public static void apDungMau(DateTimePicker picker) {
        picker.setBackground(Theme.palette().INPUT_BG());
        apDungMau(picker.getDatePicker());
        apDungMau(picker.getTimePicker());
    }

    private static void apDungMauNut(JButton nut, Theme.Palette p) {
        nut.setBackground(p.PANEL_ALT());
        nut.setForeground(p.INK());
    }
}
