package iuh.fit.core.config;

import iuh.fit.util.EnvReaderUtil;

/**
 * Cung cấp thông tin tài khoản nhận tiền cho mã VietQR từ application.properties.
 */
public final class QrConfig {

    private QrConfig() {
        throw new AssertionError("Utility class");
    }

    public static String bin() {
        return EnvReaderUtil.getOrDefault("qr.bank.bin", "");
    }

    public static String soTk() {
        return EnvReaderUtil.getOrDefault("qr.bank.accountNo", "");
    }

    public static String tenTk() {
        return EnvReaderUtil.getOrDefault("qr.bank.accountName", "");
    }
}
