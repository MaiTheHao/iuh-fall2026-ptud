package iuh.fit.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import iuh.fit.core.context.AppContext;
import iuh.fit.enumeration.AppLocale;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Nạp tài nguyên ngôn ngữ một lần cho mỗi locale và lưu bản dịch vào map phẳng.
 * Ví dụ: {"menu": {"home": "Home"}} trở thành "menu.home" -> "Home".
 */
public final class I18nReaderUtil {

    private static final Logger LOGGER = Logger.getLogger(I18nReaderUtil.class.getName());

    private static final Map<AppLocale, Map<String, String>> CACHE = new ConcurrentHashMap<>();

    private I18nReaderUtil() {
        throw new AssertionError("Utility class");
    }

    public static String get(String path) {
        return get(path, AppContext.getInstance().getLocale());
    }

    public static String get(String path, AppLocale locale) {
        return getOrNull(path, locale);
    }

    public static String get(String path, AppLocale locale, String defaultValue) {
        String value = getOrNull(path, locale);
        return value != null ? value : defaultValue;
    }

    private static String getOrNull(String path, AppLocale locale) {
        if (path == null || path.isBlank() || locale == null) {
            return null;
        }

        Map<String, String> translations =
                CACHE.computeIfAbsent(locale, I18nReaderUtil::loadTranslations);

        return translations.get(path);
    }

    private static Map<String, String> loadTranslations(AppLocale locale) {
        String resource = "resources/" + locale.getFileName();
        ClassLoader classLoader = I18nReaderUtil.class.getClassLoader();

        try (InputStream stream = classLoader.getResourceAsStream(resource)) {
            if (stream == null) {
                LOGGER.warning(() -> "I18n resource not found: " + resource);
                return Collections.emptyMap();
            }

            try (InputStreamReader reader =
                    new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                JsonElement root = JsonParser.parseReader(reader);

                if (root == null || !root.isJsonObject()) {
                    LOGGER.warning(() -> "I18n resource must contain a JSON object: " + resource);
                    return Collections.emptyMap();
                }

                Map<String, String> result = new HashMap<>();
                flatten(root.getAsJsonObject(), "", result);
                return Collections.unmodifiableMap(result);
            }
        } catch (IOException | RuntimeException e) {
            LOGGER.log(Level.WARNING, "Unable to load i18n resource: " + resource, e);
            return Collections.emptyMap();
        }
    }

    private static void flatten(JsonObject object, String prefix, Map<String, String> result) {
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            String key = prefix.isEmpty()
                    ? entry.getKey()
                    : prefix + "." + entry.getKey();

            JsonElement value = entry.getValue();

            if (value == null || value.isJsonNull()) {
                continue;
            }

            if (value.isJsonObject()) {
                flatten(value.getAsJsonObject(), key, result);
            } else if (value.isJsonPrimitive()) {
                result.put(key, value.getAsString());
            }
            // Bỏ qua mảng và các giá trị không phải kiểu nguyên thủy.
        }
    }

    public static void clearCache() {
        CACHE.clear();
    }
}
