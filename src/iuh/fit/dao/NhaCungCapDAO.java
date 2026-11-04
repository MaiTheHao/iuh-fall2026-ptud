package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;
import iuh.fit.entity.NhaCungCap;
import iuh.fit.enumeration.TrangThaiNhaCungCap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public final class NhaCungCapDAO extends BaseDao<NhaCungCap> {

  private static final String SQL_SELECT = """
          SELECT maNhaCungCap, tenNhaCungCap, sdt, email,
                diaChi, trangThai, ghiChu, ngayTao
          FROM NhaCungCap
          """;

  public Optional<NhaCungCap> findById(String maNhaCungCap) throws SQLException {
    return findOne(
            SQL_SELECT + " WHERE maNhaCungCap = ?",
            maNhaCungCap);
  }

  public List<NhaCungCap> findAll() throws SQLException {
    return findAll(SQL_SELECT);
  }

  public void insert(TransactionScope scope, NhaCungCap nhaCungCap) throws SQLException {
    String sql = """
            INSERT INTO NhaCungCap (maNhaCungCap, tenNhaCungCap, sdt, email, diaChi, trangThai, ghiChu)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

    try (PreparedStatement ps = scope.getConnection().prepareStatement(sql)) {
      ps.setString(1, nhaCungCap.getMaNhaCungCap());
      ps.setString(2, nhaCungCap.getTenNhaCungCap());
      ps.setString(3, nhaCungCap.getSdt());
      ps.setString(4, nhaCungCap.getEmail());
      ps.setString(5, nhaCungCap.getDiaChi());
      ps.setString(6, nhaCungCap.getTrangThai().name());
      ps.setString(7, nhaCungCap.getGhiChu());

      ps.executeUpdate();
    }
  }

  public void update(TransactionScope scope, NhaCungCap nhaCungCap) throws SQLException {
    String sql = """
            UPDATE NhaCungCap
            SET tenNhaCungCap = ?,
                sdt = ?,
                email = ?,
                diaChi = ?,
                trangThai = ?,
                ghiChu = ?
            WHERE maNhaCungCap = ?
            """;

    try (PreparedStatement ps = scope.getConnection().prepareStatement(sql)) {
      ps.setString(1, nhaCungCap.getTenNhaCungCap());
      ps.setString(2, nhaCungCap.getSdt());
      ps.setString(3, nhaCungCap.getEmail());
      ps.setString(4, nhaCungCap.getDiaChi());
      ps.setString(5, nhaCungCap.getTrangThai().name());
      ps.setString(6, nhaCungCap.getGhiChu());
      ps.setString(7, nhaCungCap.getMaNhaCungCap());

      ps.executeUpdate();
    }
  }

  public void delete(TransactionScope scope, String maNhaCungCap) throws SQLException {
    String sql = """
            DELETE FROM NhaCungCap
            WHERE maNhaCungCap = ?
            """;

    try (PreparedStatement ps = scope.getConnection().prepareStatement(sql)) {
      ps.setString(1, maNhaCungCap);
      ps.executeUpdate();
    }
  }

  public boolean existsSdt(String sdt, String maLoaiTru) throws SQLException {
    String sql = """
            SELECT COUNT(*) FROM NhaCungCap
            WHERE sdt = ?
            """;
    if (maLoaiTru != null) {
      sql += " AND maNhaCungCap <> ?";
    }

    try (Connection conn = DatabaseConfig.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, sdt);
      if (maLoaiTru != null) {
        ps.setString(2, maLoaiTru);
      }
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() && rs.getLong(1) > 0;
      }
    }
  }

  public boolean existsPhuTung(String maNhaCungCap) throws SQLException {
    String sql = """
            SELECT COUNT(*) FROM PhuTung
            WHERE maNhaCungCap = ?
            """;

    try (Connection conn = DatabaseConfig.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, maNhaCungCap);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() && rs.getLong(1) > 0;
      }
    }
  }

  @Override
  protected NhaCungCap mapRow(ResultSet rs) throws SQLException {
    Timestamp ts = rs.getTimestamp("ngayTao");
    return new NhaCungCap(
            rs.getString("maNhaCungCap"),
            rs.getString("tenNhaCungCap"),
            rs.getString("sdt"),
            rs.getString("email"),
            rs.getString("diaChi"),
            TrangThaiNhaCungCap.valueOf(rs.getString("trangThai")),
            rs.getString("ghiChu"),
            ts == null ? null : ts.toLocalDateTime());
  }
}
