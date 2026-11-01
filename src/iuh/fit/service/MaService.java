package iuh.fit.service;

import iuh.fit.dao.TransactionScope;
import iuh.fit.enumeration.LoaiMa;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MaService {

  private static final DateTimeFormatter NGAY = DateTimeFormatter.ofPattern("yyyyMMdd", Locale.ROOT);

  public String genMa(LoaiMa loai) throws SQLException {
    LocalDate ngay = LocalDate.now(DayMetricService.MUI_GIO);
    return this.formatMa(loai, ngay, DayMetricService.getInstance().tiepTheo(loai, ngay));
  }

  public String genMa(TransactionScope scope, LoaiMa loai) throws SQLException {
    LocalDate ngay = LocalDate.now(DayMetricService.MUI_GIO);
    return this.formatMa(loai, ngay, DayMetricService.getInstance().tiepTheo(scope, loai, ngay));
  }

  private String formatMa(LoaiMa loai, LocalDate ngay, long soThuTu) {
    if (loai == null) {
      throw new IllegalArgumentException("loai must not be null");
    }
    if (soThuTu <= 0) {
      throw new IllegalArgumentException("soThuTu must be positive");
    }
    return loai.getTienTo() + "-" + ngay.format(NGAY) + "-"
            + String.format(Locale.ROOT, "%06d", soThuTu);
  }
}
