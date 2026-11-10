package iuh.fit.util;

import java.time.format.DateTimeFormatter;

/**
 * Định dạng ngày dùng chung để các màn hình không lệch kiểu hiển thị.
 */
public final class DateFormats {

    public static final DateTimeFormatter NGAY_GIO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public static final DateTimeFormatter THANG_NAM = DateTimeFormatter.ofPattern("MM/yyyy");

    private DateFormats() {
    }
}
