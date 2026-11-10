package iuh.fit.util;

/**
 * Đọc biến môi trường cho cấu hình kết nối và dịch vụ ngoài.
 */
public final class EnvReaderUtil {

    public static String getEnv(String name) {
        return System.getenv(name);
    }

    public static String getEnv(String name, String defaultValue) {
        return System.getenv().getOrDefault(name, defaultValue);
    }

    public static String requireEnv(String name) {
        String value = System.getenv(name);

        // Báo lỗi ngay khi khởi động thay vì để cấu hình thiếu lỗi rải rác về sau.
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Required environment variable is missing: " + name);
        }

        return value;
    }
}
