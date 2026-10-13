package iuh.fit.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

public final class MaGeneratorUtil {

    private static final DateTimeFormatter DINH_DANG_NGAY = DateTimeFormatter.ofPattern("ddMMyyyy");
    private static final String TIEN_TO_NHAN_VIEN = "NV";

    private MaGeneratorUtil() {
        throw new AssertionError("Utility class");
    }

    public static String sinhMa(String tienTo, int soChuSo, Collection<String> danhSachMaHienCo) {
        long lonNhat = 0;
        for (String ma : danhSachMaHienCo) {
            if (ma == null || !ma.startsWith(tienTo)) {
                continue;
            }
            String phanSo = ma.substring(tienTo.length());
            if (phanSo.isEmpty() || !phanSo.chars().allMatch(Character::isDigit)) {
                continue;
            }
            lonNhat = Math.max(lonNhat, Long.parseLong(phanSo));
        }
        return tienTo + String.format("%0" + soChuSo + "d", lonNhat + 1);
    }

    public static String genMaNhanVien(String soDienThoai) {
        String chuSo = soDienThoai == null ? "" : soDienThoai.replaceAll("\\D", "");
        String bon = chuSo.length() >= 4
                ? chuSo.substring(chuSo.length() - 4)
                : "0".repeat(4 - chuSo.length()) + chuSo;
        return TIEN_TO_NHAN_VIEN + LocalDate.now().format(DINH_DANG_NGAY) + bon;
    }
}
