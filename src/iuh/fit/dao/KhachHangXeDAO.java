package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;
import iuh.fit.entity.KhachHangXe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public final class KhachHangXeDAO extends BaseDao<KhachHangXe> {

    public boolean exists(String maKhachHang, String bienSo) throws SQLException {
        String sql = """
                SELECT 1 FROM KhachHang_Xe
                WHERE maKhachHang = ? AND bienSo = ?
                """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKhachHang);
            ps.setString(2, bienSo);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean exists(TransactionScope scope, String maKhachHang, String bienSo) throws SQLException {
        String sql = """
                SELECT 1 FROM KhachHang_Xe
                WHERE maKhachHang = ? AND bienSo = ?
                """;

        try (PreparedStatement ps = scope.getConnection().prepareStatement(sql)) {
            ps.setString(1, maKhachHang);
            ps.setString(2, bienSo);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean insert(KhachHangXe khachHangXe) throws SQLException {
        try (TransactionScope scope = TransactionScope.begin()) {
            boolean inserted = this.insert(scope, khachHangXe);
            scope.commit();
            return inserted;
        }
    }

    public boolean insert(TransactionScope scope, KhachHangXe khachHangXe) throws SQLException {
        String sql = """
                INSERT INTO KhachHang_Xe (maKhachHang, bienSo, ngayTao)
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement ps = scope.getConnection().prepareStatement(sql)) {
            ps.setString(1, khachHangXe.getMaKhachHang());
            ps.setString(2, khachHangXe.getBienSo());
            ps.setObject(3, khachHangXe.getNgayTao() != null
                    ? khachHangXe.getNgayTao()
                    : LocalDateTime.now());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    protected KhachHangXe mapRow(ResultSet rs) throws SQLException {
        return new KhachHangXe(
                rs.getString("maKhachHang"),
                rs.getString("bienSo"),
                rs.getObject("ngayTao", LocalDateTime.class));
    }
}
