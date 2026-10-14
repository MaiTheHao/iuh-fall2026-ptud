package iuh.fit.util;

import java.text.Normalizer;
import java.util.Locale;

/**
 * Chuẩn hóa văn bản để so khớp không phân biệt hoa/thường và bỏ dấu tiếng Việt.
 */
public final class TextNormalizerUtil {

    private TextNormalizerUtil() {
        throw new AssertionError("Utility class");
    }

    public static String normalize(String value) {
        if (value == null) {
            return "";
        }

        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace('đ', 'd')
                .replace('Đ', 'D')
                .toLowerCase(Locale.ROOT)
                .trim();
    }
}
