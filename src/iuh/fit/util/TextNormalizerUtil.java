package iuh.fit.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public final class TextNormalizerUtil {
    private static final Pattern DIACRITICS = Pattern.compile("\\p{M}+");
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private TextNormalizerUtil() { throw new AssertionError(); }

    public static String normalize(String value) {
        if (value == null || value.isBlank()) return "";
        String s = Normalizer.normalize(value, Normalizer.Form.NFD);
        s = DIACRITICS.matcher(s).replaceAll("");
        s = s.replace('đ', 'd').replace('Đ', 'D');
        s = s.toLowerCase(Locale.ROOT);
        s = WHITESPACE.matcher(s).replaceAll(" ").trim();
        return s;
    }
}
