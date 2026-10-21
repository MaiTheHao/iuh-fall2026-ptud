package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;
import iuh.fit.entity.VanDeKyThuat;
import iuh.fit.enumeration.TrangThaiVanDeKyThuat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public final class VanDeKyThuatDAO extends BaseDao<VanDeKyThuat> {

    private static final String SQL_SELECT = """
            SELECT maVanDe, maPhieuTiepNhan, tieuDe, moTa, mucDoUuTien, giaiPhap, khachHangDongY, ngayTao
            FROM VanDeKyThuat
            """;

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
            ps.setString(7, vanDe.getKhachHangDongY() != null ? vanDe.getKhachHangDongY().name() : TrangThaiVanDeKyThuat.CHUA_XAC_NHAN.name());
            ps.setTimestamp(8, Timestamp.valueOf(vanDe.getNgayTao()));

            return ps.executeUpdate() > 0;
        }
    }

    public List<VanDeKyThuat> findByPhieuTiepNhan(String maPhieuTiepNhan) throws SQLException {
        String sql = SQL_SELECT + " WHERE maPhieuTiepNhan = ? ORDER BY mucDoUuTien ASC, ngayTao DESC";
        List<VanDeKyThuat> list = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhieuTiepNhan);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    public List<VanDeKyThuat> findByPhieuTiepNhan(TransactionScope scope, String maPhieuTiepNhan) throws SQLException {
        String sql = SQL_SELECT + " WHERE maPhieuTiepNhan = ? ORDER BY mucDoUuTien ASC, ngayTao DESC";
        List<VanDeKyThuat> list = new ArrayList<>();

        try (PreparedStatement ps = scope.getConnection().prepareStatement(sql)) {
            ps.setString(1, maPhieuTiepNhan);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    @Override
    protected VanDeKyThuat mapRow(ResultSet rs) throws SQLException {
        Timestamp timestamp = rs.getTimestamp("ngayTao");
        String trangThaiStr = rs.getString("khachHangDongY");
        TrangThaiVanDeKyThuat trangThai = trangThaiStr != null ? TrangThaiVanDeKyThuat.valueOf(trangThaiStr) : TrangThaiVanDeKyThuat.CHUA_XAC_NHAN;

        return new VanDeKyThuat(
                rs.getString("maVanDe"),
                rs.getString("maPhieuTiepNhan"),
                rs.getString("tieuDe"),
                rs.getString("moTa"),
                rs.getInt("mucDoUuTien"),
                rs.getString("giaiPhap"),
                trangThai,
                timestamp != null ? timestamp.toLocalDateTime() : null
        );
    }
}