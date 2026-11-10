package iuh.fit.util;

/**
 * Chuyển chuỗi nhập từ giao diện sang số, cho phép bỏ trống.
 */
public final class NumberParseUtil {

    private NumberParseUtil() {
        throw new AssertionError("Utility class");
    }

    public static Double toDoubleOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        // Chuỗi rỗng là "không nhập"; chuỗi sai định dạng vẫn ném NumberFormatException
        // để lớp gọi hiển thị lỗi thay vì âm thầm coi như rỗng.
        return Double.valueOf(value.trim());
    }

    public static Integer toIntegerOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return Integer.valueOf(value.trim());
    }

    public static String textOf(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
