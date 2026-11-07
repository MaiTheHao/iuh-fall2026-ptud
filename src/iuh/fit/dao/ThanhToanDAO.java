
package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;
import iuh.fit.entity.HoaDon;
import iuh.fit.entity.NhanVien;
import iuh.fit.entity.ThanhToan;
import iuh.fit.enumeration.PhuongThucThanhToan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.Optional;

public final class ThanhToanDAO {

    public Optional<ThanhToan> findByMaHoaDon(String maHoaDon)
            throws SQLException {
        if (maHoaDon == null || maHoaDon.isBlank()) {
            return Optional.empty();
        }

        String sql = """
                SELECT maThanhToan, maHoaDon, phuongThuc, soTien,
                       thoiGian, maNhanVien, ghiChu
                FROM ThanhToan
                WHERE maHoaDon = ?
                """;

        try (Connection connection = DatabaseConfig.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, maHoaDon);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapRow(rs));
            }
        }
    }

    public void insert(TransactionScope scope, ThanhToan thanhToan)
            throws SQLException {
        String sql = """
                INSERT INTO ThanhToan
                    (maThanhToan, maHoaDon, phuongThuc, soTien, thoiGian,
                     maNhanVien, ghiChu)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = scope.getConnection().prepareStatement(sql)) {
            ps.setString(1, thanhToan.getMaThanhToan());
            ps.setString(2, thanhToan.getHoaDon().getMaHoaDon());

            ps.setString(3, thanhToan.getPhuongThuc().name());

            if (thanhToan.getSoTien() == null) {
                ps.setNull(4, Types.DECIMAL);
            } else {
                ps.setBigDecimal(4, thanhToan.getSoTien());
            }

            LocalDateTime thoiGian = thanhToan.getThoiGian();
            ps.setTimestamp(5, Timestamp.valueOf(
                    thoiGian != null ? thoiGian : LocalDateTime.now()));

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

    private ThanhToan mapRow(ResultSet rs) throws SQLException {
        ThanhToan thanhToan = new ThanhToan();

        thanhToan.setMaThanhToan(rs.getString("maThanhToan"));
        thanhToan.setHoaDon(new HoaDon(rs.getString("maHoaDon")));

        thanhToan.setPhuongThuc(
                PhuongThucThanhToan.valueOf(rs.getString("phuongThuc")));

        thanhToan.setSoTien(rs.getBigDecimal("soTien"));

        Timestamp thoiGian = rs.getTimestamp("thoiGian");
        thanhToan.setThoiGian(
                thoiGian == null ? null : thoiGian.toLocalDateTime());

        String maNhanVien = rs.getString("maNhanVien");
        thanhToan.setNhanVien(
                maNhanVien == null ? null : new NhanVien(maNhanVien));

        thanhToan.setGhiChu(rs.getString("ghiChu"));

        return thanhToan;
    }
}
