package iuh.fit.dao;

import iuh.fit.entity.ThanhToan;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;

public final class ThanhToanDAO {

  public void insert(TransactionScope scope, ThanhToan thanhToan) throws SQLException {
    String sql = """
            INSERT INTO ThanhToan
                (maThanhToan, maHoaDon, phuongThuc, soTien, thoiGian, maNhanVien, ghiChu)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
    try (PreparedStatement ps = scope.getConnection().prepareStatement(sql)) {
      ps.setString(1, thanhToan.getMaThanhToan());
      ps.setString(2, thanhToan.getHoaDon().getMaHoaDon());
      ps.setString(3, thanhToan.getPhuongThuc().name());
      BigDecimal soTien = thanhToan.getSoTien();
      if (soTien == null) {
        ps.setNull(4, Types.DECIMAL);
      } else {
        ps.setBigDecimal(4, soTien);
      }
      LocalDateTime thoiGian = thanhToan.getThoiGian();
      ps.setTimestamp(5, Timestamp.valueOf(thoiGian != null ? thoiGian : LocalDateTime.now()));
      String maNhanVien = thanhToan.getNhanVien() == null
              ? null
              : thanhToan.getNhanVien().getMaNhanVien();
      if (maNhanVien == null) {
        ps.setNull(6, Types.VARCHAR);
      } else {
        ps.setString(6, maNhanVien);
      }
      String ghiChu = thanhToan.getGhiChu();
      if (ghiChu == null) {
        ps.setNull(7, Types.NVARCHAR);
      } else {
        ps.setString(7, ghiChu);
      }
      ps.executeUpdate();
    }
  }
}
