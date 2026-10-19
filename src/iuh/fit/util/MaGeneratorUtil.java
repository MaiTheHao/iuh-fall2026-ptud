package iuh.fit.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class MaGeneratorUtil {

    public static final String TIEN_TO_NHAN_VIEN = "NV";

    private MaGeneratorUtil() {
        throw new AssertionError("Utility class");
    }

    public static String sinhMa(String tienTo, LocalDateTime thoiDiem) {
        return tienTo + thoiDiem(thoiDiem);
    }

    public static String sinhMaNhanVien(String soDienThoai, LocalDateTime thoiDiem) {
        String sdt = soDienThoai == null ? "" : soDienThoai.trim();
        return TIEN_TO_NHAN_VIEN + sdt + thoiDiem(thoiDiem);
    }

    public static String sinhMaPhieuTiepNhan(String maNhanVien, String bienSo, LocalDateTime thoiDiem) {
        return "TN" + maNhanVien + bienSo + thoiDiem(thoiDiem);
    }

    public static String sinhMaKhieuNai(String maNhanVien, LocalDateTime thoiDiem) {
        return "KN" + maNhanVien + thoiDiem(thoiDiem);
    }

    private static String thoiDiem(LocalDateTime thoiDiem) {
        return thoiDiem.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                + String.format("%05d", TimeUtils.toSecondsOfDay(thoiDiem.toLocalTime()));
    }
}
