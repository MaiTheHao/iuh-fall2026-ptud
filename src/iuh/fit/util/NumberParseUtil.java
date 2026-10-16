package iuh.fit.util;

public final class NumberParseUtil {

    private NumberParseUtil() {
        throw new AssertionError("Utility class");
    }

    public static Double toDoubleOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

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
