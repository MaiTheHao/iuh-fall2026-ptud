package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;
import iuh.fit.entity.Voucher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class VoucherDAO extends BaseDao<Voucher> {

    public Voucher findById(String maVoucher) throws SQLException {
        String sql = "SELECT maVoucher, tenChuongTrinh, moTa, mucGiam, thoiHanApDung, trangThaiKichHoat FROM Voucher WHERE maVoucher = ?";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maVoucher);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        }
    }

    public List<Voucher> findAll() throws SQLException {
        String sql = "SELECT maVoucher, tenChuongTrinh, moTa, mucGiam, thoiHanApDung, trangThaiKichHoat FROM Voucher";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return mapRows(rs);
        }
    }

    @Override
    protected List<Voucher> mapRows(ResultSet rs) throws SQLException {
        List<Voucher> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    protected Voucher mapRow(ResultSet rs) throws SQLException {
        return new Voucher(
                rs.getString("maVoucher"),
                rs.getString("tenChuongTrinh"),
                rs.getString("moTa"),
                rs.getDouble("mucGiam"),
                rs.getObject("thoiHanApDung", LocalDateTime.class),
                rs.getBoolean("trangThaiKichHoat"));
    }
}
