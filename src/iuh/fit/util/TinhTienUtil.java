package iuh.fit.util;

import iuh.fit.entity.ChiTietHoaDon;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public final class TinhTienUtil {

  private TinhTienUtil() {}

  public static double tongGoc(List<ChiTietHoaDon> chiTiets) {
    if (chiTiets == null) {
      return 0.0;
    }
    double tong = 0.0;
    for (ChiTietHoaDon ct : chiTiets) {
      double donGia = ct.getDonGia() == null ? 0.0 : ct.getDonGia();
      tong += ct.getSoLuong() * donGia;
    }
    return tong;
  }

  public static double mucGiam(double muc) {
    if (Double.isNaN(muc)) {
      return 0.0;
    }
    return Math.min(Math.max(muc, 0.0), 1.0);
  }

  public static BigDecimal thucThu(double tongGoc, double mucGiam) {
    return BigDecimal.valueOf(tongGoc * (1.0 - mucGiam(mucGiam))).setScale(2, RoundingMode.HALF_UP);
  }
}
