package iuh.fit.dao;

import iuh.fit.entity.NhanVien;
import iuh.fit.enumeration.TrangThaiNhanVien;
import iuh.fit.enumeration.VaiTro;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public final class NhanVienDAO extends BaseDao<NhanVien> {

    private static final String SQL_SELECT = "SELECT maNhanVien, tenDangNhap, matKhau, hoTen, anhDaiDien, vaiTro, trangThai, ngayTao FROM NhanVien";

    public NhanVien findById(String maNhanVien) throws SQLException {
        return findOne(SQL_SELECT + " WHERE maNhanVien = ?", maNhanVien);
    }

    public List<NhanVien> findAll() throws SQLException {
        return findAll(SQL_SELECT);
    }

    @Override
    protected NhanVien mapRow(ResultSet rs) throws SQLException {
        return new NhanVien(
                rs.getString("maNhanVien"),
                rs.getString("tenDangNhap"),
                rs.getString("matKhau"),
                rs.getString("hoTen"),
                rs.getString("anhDaiDien"),
                VaiTro.valueOf(rs.getString("vaiTro")),
                TrangThaiNhanVien.valueOf(rs.getString("trangThai")),
                rs.getObject("ngayTao", LocalDateTime.class));
    }
}
