package iuh.fit.core.config;

import iuh.fit.util.EnvReaderUtil;

/**
 * Thông tin cửa hàng in trên hoá đơn, đọc từ application.properties.
 */
public final class CuaHangConfig {

    private CuaHangConfig() {
        throw new AssertionError("Utility class");
    }

    public static String ten() {
        return EnvReaderUtil.getOrDefault("cuaHang.ten", "GARA Ô TÔ LÚA");
    }

    public static String diaChi() {
        return EnvReaderUtil.getOrDefault("cuaHang.diaChi", "");
    }

    public static String soDienThoai() {
        return EnvReaderUtil.getOrDefault("cuaHang.sdt", "");
    }

    public static String email() {
        return EnvReaderUtil.getOrDefault("cuaHang.email", "");
    }

    public static String maSoThue() {
        return EnvReaderUtil.getOrDefault("cuaHang.maSoThue", "");
    }

    public static String website() {
        return EnvReaderUtil.getOrDefault("cuaHang.website", "");
    }
}
