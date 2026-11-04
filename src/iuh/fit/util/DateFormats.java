package iuh.fit.util;

import java.time.format.DateTimeFormatter;

public final class DateFormats {

    public static final DateTimeFormatter NGAY_GIO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public static final DateTimeFormatter THANG_NAM = DateTimeFormatter.ofPattern("MM/yyyy");

    private DateFormats() {
    }
}
