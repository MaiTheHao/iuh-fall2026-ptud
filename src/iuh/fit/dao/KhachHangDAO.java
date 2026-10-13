package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;
import iuh.fit.entity.KhachHang;
import iuh.fit.entity.Xe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class KhachHangDAO extends BaseDao<KhachHang> {

    private static final String SQL_SELECT = "SELECT maKhachHang, sdt, hoTen, ngayTao FROM KhachHang";

    public Optional<KhachHang> findById(String maKhachHang) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection()) {
            Optional<KhachHang> found = findOne(conn, SQL_SELECT + " WHERE maKhachHang = ?", maKhachHang);
            if (found.isEmpty()) {
                return Optional.empty();
            }
            KhachHang khachHang = found.get();
            khachHang.setDanhSachXe(loadDanhSachXe(conn, maKhachHang));
            return Optional.of(khachHang);
        }
    }

    public List<KhachHang> findAll() throws SQLException {
        return findAll(SQL_SELECT);
    }

    public void insert(KhachHang khachHang) throws SQLException {
        String sql = "INSERT INTO KhachHang (maKhachHang, sdt, hoTen, ngayTao) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, khachHang.getMaKhachHang());
            ps.setString(2, khachHang.getSdt());
            ps.setString(3, khachHang.getHoTen());
            ps.setObject(4, khachHang.getNgayTao());
            ps.executeUpdate();
        }
    }

    public void update(KhachHang khachHang) throws SQLException {
        String sql = "UPDATE KhachHang SET sdt = ?, hoTen = ? WHERE maKhachHang = ?";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, khachHang.getSdt());
            ps.setString(2, khachHang.getHoTen());
            ps.setString(3, khachHang.getMaKhachHang());
            ps.executeUpdate();
        }
    }

    public void delete(String maKhachHang) throws SQLException {
        String sql = "DELETE FROM KhachHang WHERE maKhachHang = ?";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKhachHang);
            ps.executeUpdate();
        }
    }

    @Override
    protected KhachHang mapRow(ResultSet rs) throws SQLException {
        return new KhachHang(
                rs.getString("maKhachHang"),
                rs.getString("sdt"),
                rs.getString("hoTen"),
                rs.getObject("ngayTao", LocalDateTime.class));
    }

    private List<Xe> loadDanhSachXe(Connection conn, String maKhachHang) throws SQLException {
        String sql = "SELECT x.bienSo, x.hangXe, x.dongXe, x.doiXe, x.odo FROM Xe x JOIN KhachHang_Xe khx ON khx.bienSo = x.bienSo WHERE khx.maKhachHang = ?";
        List<Xe> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKhachHang);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Xe(
                            rs.getString("bienSo"),
                            rs.getString("hangXe"),
                            rs.getString("dongXe"),
                            rs.getInt("doiXe"),
                            rs.getInt("odo")));
                }
            }
        }
        return list;
    }
}
