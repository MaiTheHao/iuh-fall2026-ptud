package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;
import iuh.fit.enumeration.LoaiMa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;

public final class DayMetricDAO {

  private static final String SQL_UPSERT_LAY_SO_LUONG_TIEP_THEO = """
      SET NOCOUNT ON;
      DECLARE @kq TABLE (amount bigint);

      UPDATE DayMetric
        SET amount = amount + 1
        OUTPUT INSERTED.amount INTO @kq
      WHERE ngay = ? AND target = ?;

      IF NOT EXISTS (SELECT 1 FROM @kq)
      BEGIN
          INSERT INTO DayMetric (ngay, target, amount) VALUES (?, ?, 1);
          INSERT INTO @kq VALUES (1);
      END

      SELECT amount FROM @kq;
      """;

  private static final String SQL_TAI_THEO_NGAY = """
      SELECT target, amount
      FROM DayMetric
      WHERE ngay = ?
      """;

  public long tiepTheo(TransactionScope scope, LocalDate ngay, LoaiMa loai) throws SQLException {
    return this.tiepTheoCoThuLai(scope.getConnection(), ngay, loai);
  }

  public long tiepTheo(LocalDate ngay, LoaiMa loai) throws SQLException {
    try (Connection connection = DatabaseConfig.getConnection()) {
      return this.tiepTheoCoThuLai(connection, ngay, loai);
    }
  }

  public Map<LoaiMa, Long> taiTheoNgay(LocalDate ngay) throws SQLException {
    try (Connection connection = DatabaseConfig.getConnection()) {
      return this.taiTheoNgay(connection, ngay);
    }
  }

  public Map<LoaiMa, Long> taiTheoNgay(TransactionScope scope, LocalDate ngay) throws SQLException {
    return this.taiTheoNgay(scope.getConnection(), ngay);
  }

  private long tiepTheoCoThuLai(Connection connection, LocalDate ngay, LoaiMa loai) throws SQLException {
    try {
      return this.capSo(connection, ngay, loai);
    } catch (SQLException ex) {
      if (!this.laTrungKhoa(ex)) {
        throw ex;
      }
      return this.capSo(connection, ngay, loai);
    }
  }

  private long capSo(Connection connection, LocalDate ngay, LoaiMa loai) throws SQLException {
    try (PreparedStatement ps = connection.prepareStatement(SQL_UPSERT_LAY_SO_LUONG_TIEP_THEO)) {
      ps.setDate(1, java.sql.Date.valueOf(ngay));
      ps.setString(2, loai.name());
      ps.setDate(3, java.sql.Date.valueOf(ngay));
      ps.setString(4, loai.name());
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          throw new SQLException("Khong doc duoc so thu tu tu DayMetric");
        }
        return rs.getLong(1);
      }
    }
  }

  private Map<LoaiMa, Long> taiTheoNgay(Connection connection, LocalDate ngay) throws SQLException {
    Map<LoaiMa, Long> ketQua = new EnumMap<>(LoaiMa.class);
    try (PreparedStatement ps = connection.prepareStatement(SQL_TAI_THEO_NGAY)) {
      ps.setDate(1, java.sql.Date.valueOf(ngay));
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          LoaiMa loai = this.docLoai(rs.getString("target"));
          if (loai != null) {
            ketQua.put(loai, rs.getLong("amount"));
          }
        }
      }
    }
    return ketQua;
  }

  private LoaiMa docLoai(String target) {
    try {
      return LoaiMa.valueOf(target);
    } catch (IllegalArgumentException | NullPointerException ex) {
      return null;
    }
  }

  private boolean laTrungKhoa(SQLException ex) {
    int errorCode = ex.getErrorCode();
    return errorCode == 2627 || errorCode == 2601;
  }
}
