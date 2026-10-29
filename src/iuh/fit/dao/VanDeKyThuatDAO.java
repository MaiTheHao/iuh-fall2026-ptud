package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;
import iuh.fit.entity.VanDeKyThuat;
import iuh.fit.enumeration.TrangThaiVanDeKyThuat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public final class VanDeKyThuatDAO extends BaseDao<VanDeKyThuat> {

    private static final String SQL_SELECT = """
            SELECT maVanDe, maPhieuTiepNhan, tieuDe, moTa, mucDoUuTien, giaiPhap, khachHangDongY, ngayTao
            FROM VanDeKyThuat
            """;

    public List<VanDeKyThuat> findAll() throws SQLException {
        return this.findAll(SQL_SELECT);
    }

    public Optional<VanDeKyThuat> findById(String maVanDe) throws SQLException {
        return this.findOne(SQL_SELECT + " WHERE maVanDe = ?", maVanDe);
    }

    public List<VanDeKyThuat> findByPhieuTiepNhan(String maPhieuTiepNhan) throws SQLException {
        String sql = SQL_SELECT + " WHERE maPhieuTiepNhan = ?";

        try (Connection connection = DatabaseConfig.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, maPhieuTiepNhan);
            try (ResultSet rs = ps.executeQuery()) {
                return this.mapRows(rs);
            }
        }
    }

    public boolean insert(TransactionScope scope, VanDeKyThuat vanDe) throws SQLException {
        String sql = """
                INSERT INTO VanDeKyThuat (maVanDe, maPhieuTiepNhan, tieuDe, moTa, mucDoUuTien, giaiPhap, khachHangDongY, ngayTao)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = scope.getConnection().prepareStatement(sql)) {
            ps.setString(1, vanDe.getMaVanDe());
            ps.setString(2, vanDe.getMaPhieuTiepNhan());
            ps.setString(3, vanDe.getTieuDe());
            ps.setString(4, vanDe.getMoTa());
            ps.setInt(5, vanDe.getMucDoUuTien());
            ps.setString(6, vanDe.getGiaiPhap());
            ps.setString(7, vanDe.getKhachHangDongY().name());
            ps.setObject(8, vanDe.getNgayTao() != null ? vanDe.getNgayTao() : LocalDateTime.now());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(TransactionScope scope, VanDeKyThuat vanDe) throws SQLException {
        String sql = """
                UPDATE VanDeKyThuat
                SET tieuDe = ?, moTa = ?, mucDoUuTien = ?, giaiPhap = ?, khachHangDongY = ?
                WHERE maVanDe = ?
                """;

        try (PreparedStatement ps = scope.getConnection().prepareStatement(sql)) {
            ps.setString(1, vanDe.getTieuDe());
            ps.setString(2, vanDe.getMoTa());
            ps.setInt(3, vanDe.getMucDoUuTien());
            ps.setString(4, vanDe.getGiaiPhap());
            ps.setString(5, vanDe.getKhachHangDongY().name());
            ps.setString(6, vanDe.getMaVanDe());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(TransactionScope scope, String maVanDe) throws SQLException {
        String sql = """
                DELETE FROM VanDeKyThuat
                WHERE maVanDe = ?
                """;

        try (PreparedStatement ps = scope.getConnection().prepareStatement(sql)) {
            ps.setString(1, maVanDe);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    protected VanDeKyThuat mapRow(ResultSet rs) throws SQLException {
        String trangThai = rs.getString("khachHangDongY");
        LocalDateTime ngayTao = rs.getObject("ngayTao", LocalDateTime.class);

        return new VanDeKyThuat(
                rs.getString("maVanDe"),
                rs.getString("maPhieuTiepNhan"),
                rs.getString("tieuDe"),
                rs.getString("moTa"),
                rs.getInt("mucDoUuTien"),
                rs.getString("giaiPhap"),
                trangThai != null ? TrangThaiVanDeKyThuat.valueOf(trangThai) : TrangThaiVanDeKyThuat.CHUA_XAC_NHAN,
                ngayTao);
    }
}
