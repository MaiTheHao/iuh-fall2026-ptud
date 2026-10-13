package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;
import iuh.fit.entity.NhanVien;
import iuh.fit.enumeration.TrangThaiNhanVien;
import iuh.fit.enumeration.VaiTro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public final class NhanVienDAO extends BaseDao<NhanVien> {

    private static final String SQL_SELECT = "SELECT maNhanVien, tenDangNhap, matKhau, hoTen, soDienThoai, anhDaiDien, vaiTro, trangThai, ngayTao FROM NhanVien";

    public NhanVien findById(String maNhanVien) throws SQLException {
        return findOne(SQL_SELECT + " WHERE maNhanVien = ?", maNhanVien);
    }

    public List<NhanVien> findAll() throws SQLException {
        return findAll(SQL_SELECT);
    }

    public NhanVien findByTenDangNhap(String tenDangNhap) throws SQLException {
        try (Connection connection = DatabaseConfig.getConnection();
                PreparedStatement ps = connection.prepareStatement(SQL_SELECT + " WHERE tenDangNhap = ?")) {
            ps.setString(1, tenDangNhap);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public NhanVien login(String tenDangNhap, String matKhau) throws SQLException {
        try (Connection connection = DatabaseConfig.getConnection();
                PreparedStatement ps = connection.prepareStatement(
                        SQL_SELECT + " WHERE tenDangNhap = ? AND matKhau = ? AND trangThai <> ?")) {
            ps.setString(1, tenDangNhap);
            ps.setString(2, matKhau);
            ps.setString(3, TrangThaiNhanVien.DA_KHOA.name());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    public void register(NhanVien nhanVien) throws SQLException {
        String sql = "INSERT INTO NhanVien (maNhanVien, tenDangNhap, matKhau, hoTen, soDienThoai, anhDaiDien, vaiTro, trangThai, ngayTao) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConfig.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nhanVien.getMaNhanVien());
            ps.setString(2, nhanVien.getTenDangNhap());
            ps.setString(3, nhanVien.getMatKhau());
            ps.setString(4, nhanVien.getHoTen());
            ps.setString(5, nhanVien.getSoDienThoai());
            ps.setString(6, nhanVien.getAnhDaiDien());
            ps.setString(7, nhanVien.getVaiTro().name());
            ps.setString(8, nhanVien.getTrangThai().name());
            ps.setObject(9, nhanVien.getNgayTao());
            ps.executeUpdate();
        }
    }

    public void update(NhanVien nhanVien) throws SQLException {
        String sql = "UPDATE NhanVien SET hoTen = ?, soDienThoai = ?, anhDaiDien = ?, vaiTro = ?, trangThai = ?, matKhau = ? WHERE maNhanVien = ?";
        try (Connection connection = DatabaseConfig.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nhanVien.getHoTen());
            ps.setString(2, nhanVien.getSoDienThoai());
            ps.setString(3, nhanVien.getAnhDaiDien());
            ps.setString(4, nhanVien.getVaiTro().name());
            ps.setString(5, nhanVien.getTrangThai().name());
            ps.setString(6, nhanVien.getMatKhau());
            ps.setString(7, nhanVien.getMaNhanVien());
            ps.executeUpdate();
        }
    }

    public void updateProfile(NhanVien nhanVien) throws SQLException {
        String sql = "UPDATE NhanVien SET hoTen = ?, soDienThoai = ?, anhDaiDien = ? WHERE maNhanVien = ?";
        try (Connection connection = DatabaseConfig.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nhanVien.getHoTen());
            ps.setString(2, nhanVien.getSoDienThoai());
            ps.setString(3, nhanVien.getAnhDaiDien());
            ps.setString(4, nhanVien.getMaNhanVien());
            ps.executeUpdate();
        }
    }

    public void updatePassword(NhanVien nhanVien) throws SQLException {
        String sql = "UPDATE NhanVien SET matKhau = ? WHERE maNhanVien = ?";
        try (Connection connection = DatabaseConfig.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nhanVien.getMatKhau());
            ps.setString(2, nhanVien.getMaNhanVien());
            ps.executeUpdate();
        }
    }

    @Override
    protected NhanVien mapRow(ResultSet rs) throws SQLException {
        return new NhanVien(
                rs.getString("maNhanVien"),
                rs.getString("tenDangNhap"),
                rs.getString("matKhau"),
                rs.getString("hoTen"),
                rs.getString("soDienThoai"),
                rs.getString("anhDaiDien"),
                VaiTro.valueOf(rs.getString("vaiTro")),
                TrangThaiNhanVien.valueOf(rs.getString("trangThai")),
                rs.getObject("ngayTao", LocalDateTime.class));
    }
}
