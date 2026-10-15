package iuh.fit.dao;

import iuh.fit.entity.Voucher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public final class VoucherDAO extends BaseDao<Voucher> {

    private static final String SQL_SELECT =
            "SELECT maVoucher, tenChuongTrinh, moTa, mucGiam, " +
            "thoiHanApDung, trangThaiKichHoat FROM Voucher";

    /**
     * Tìm voucher theo mã.
     */
    public Optional<Voucher> findById(String maVoucher) throws SQLException {
        return findOne(
                SQL_SELECT + " WHERE maVoucher = ?",
                maVoucher
        );
    }

    /**
     * Lấy toàn bộ voucher.
     */
    public List<Voucher> findAll() throws SQLException {
        return findAll(SQL_SELECT);
    }

    /**
     * Thêm voucher.
     */
    public void insert(Voucher voucher) throws SQLException {
        String sql = "INSERT INTO Voucher " +
                "(maVoucher, tenChuongTrinh, moTa, mucGiam, " +
                "thoiHanApDung, trangThaiKichHoat) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = iuh.fit.core.config.DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, voucher.getMaVoucher());
            ps.setString(2, voucher.getTenChuongTrinh());

            if (voucher.getMoTa() == null || voucher.getMoTa().isBlank()) {
                ps.setNull(3, Types.NVARCHAR);
            } else {
                ps.setString(3, voucher.getMoTa());
            }

            ps.setDouble(4, voucher.getMucGiam());
            ps.setObject(5, voucher.getThoiHanApDung());
            ps.setBoolean(6, voucher.isTrangThaiKichHoat());

            ps.executeUpdate();
        }
    }

    /**
     * Cập nhật voucher.
     */
    public void update(Voucher voucher) throws SQLException {
        String sql = "UPDATE Voucher SET " +
                "tenChuongTrinh = ?, " +
                "moTa = ?, " +
                "mucGiam = ?, " +
                "thoiHanApDung = ?, " +
                "trangThaiKichHoat = ? " +
                "WHERE maVoucher = ?";

        try (Connection conn = iuh.fit.core.config.DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, voucher.getTenChuongTrinh());

            if (voucher.getMoTa() == null || voucher.getMoTa().isBlank()) {
                ps.setNull(2, Types.NVARCHAR);
            } else {
                ps.setString(2, voucher.getMoTa());
            }

            ps.setDouble(3, voucher.getMucGiam());
            ps.setObject(4, voucher.getThoiHanApDung());
            ps.setBoolean(5, voucher.isTrangThaiKichHoat());
            ps.setString(6, voucher.getMaVoucher());

            ps.executeUpdate();
        }
    }

    /**
     * Xóa voucher theo mã.
     */
    public void delete(String maVoucher) throws SQLException {
        String sql = "DELETE FROM Voucher WHERE maVoucher = ?";

        try (Connection conn = iuh.fit.core.config.DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maVoucher);
            ps.executeUpdate();
        }
    }

    /**
     * Chuyển một dòng dữ liệu SQL thành đối tượng Voucher.
     */
    @Override
    protected Voucher mapRow(ResultSet rs) throws SQLException {
        return new Voucher(
                rs.getString("maVoucher"),
                rs.getString("tenChuongTrinh"),
                rs.getString("moTa"),
                rs.getDouble("mucGiam"),
                rs.getObject("thoiHanApDung", LocalDateTime.class),
                rs.getBoolean("trangThaiKichHoat")
        );
    }
}
