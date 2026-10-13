package iuh.fit.dao;

import iuh.fit.entity.Voucher;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public final class VoucherDAO extends BaseDao<Voucher> {

    private static final String SQL_SELECT = "SELECT maVoucher, tenChuongTrinh, moTa, mucGiam, thoiHanApDung, trangThaiKichHoat FROM Voucher";

    public Optional<Voucher> findById(String maVoucher) throws SQLException {
        return findOne(SQL_SELECT + " WHERE maVoucher = ?", maVoucher);
    }

    public List<Voucher> findAll() throws SQLException {
        return findAll(SQL_SELECT);
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
