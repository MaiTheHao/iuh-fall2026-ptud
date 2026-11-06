package iuh.fit.core.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public final class QrConfig {

    private static final String TAI_NGUYEN = "resources/qr.properties";

    private QrConfig() {
    }

    public static String bin() {
        return doc("bank.bin");
    }

    public static String soTk() {
        return doc("bank.accountNo");
    }

    public static String tenTk() {
        return doc("bank.accountName");
    }

    private static String doc(String khoa) {
        try (InputStream dauVao = QrConfig.class.getClassLoader().getResourceAsStream(TAI_NGUYEN)) {
            if (dauVao == null) {
                return "";
            }
            Properties thuocTinh = new Properties();
            try (InputStreamReader doc = new InputStreamReader(dauVao, StandardCharsets.UTF_8)) {
                thuocTinh.load(doc);
            }
            String giaTri = thuocTinh.getProperty(khoa);
            return giaTri == null || giaTri.isBlank() ? "" : giaTri.trim();
        } catch (IOException | RuntimeException ex) {
            return "";
        }
    }
}
