package iuh.fit.util;

public final class EnvReaderUtil {

    public static String getEnv(String name) {
        return System.getenv(name);
    }

    public static String getEnv(String name, String defaultValue) {
        return System.getenv().getOrDefault(name, defaultValue);
    }

    public static String requireEnv(String name) {
        String value = System.getenv(name);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Required environment variable is missing: " + name);
        }

        return value;
    }
}
