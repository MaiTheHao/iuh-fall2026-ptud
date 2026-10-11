package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;
import iuh.fit.entity.NhanVien;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class NhanVienDAO extends BaseDao<NhanVien> {

    public NhanVien findById(String maNhanVien) throws SQLException {
        String sql = "SELECT maNhanVien, tenDangNhap, matKhau, hoTen, anhDaiDien, vaiTro, trangThai, ngayTao FROM NhanVien WHERE maNhanVien = ?";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNhanVien);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        }
    }

    public List<NhanVien> findAll() throws SQLException {
        String sql = "SELECT maNhanVien, tenDangNhap, matKhau, hoTen, anhDaiDien, vaiTro, trangThai, ngayTao FROM NhanVien";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return mapRows(rs);
        }
    }

    @Override
    protected List<NhanVien> mapRows(ResultSet rs) throws SQLException {
        List<NhanVien> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    protected NhanVien mapRow(ResultSet rs) throws SQLException {
        return new NhanVien(
                rs.getString("maNhanVien"),
                rs.getString("tenDangNhap"),
                rs.getString("matKhau"),
                rs.getString("hoTen"),
                rs.getString("anhDaiDien"),
                rs.getString("vaiTro"),
                rs.getString("trangThai"),
                rs.getObject("ngayTao", LocalDateTime.class));
    }
}
