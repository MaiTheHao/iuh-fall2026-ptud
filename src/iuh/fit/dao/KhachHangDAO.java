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

public final class KhachHangDAO extends BaseDao<KhachHang> {

    public KhachHang findById(String maKhachHang) throws SQLException {
        String sql = "SELECT maKhachHang, sdt, hoTen, anhDaiDien, ngayTao FROM KhachHang WHERE maKhachHang = ?";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maKhachHang);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                KhachHang kh = mapRow(rs);
                kh.setDanhSachXe(loadDanhSachXe(conn, maKhachHang));
                return kh;
            }
        }
    }

    public List<KhachHang> findAll() throws SQLException {
        String sql = "SELECT maKhachHang, sdt, hoTen, anhDaiDien, ngayTao FROM KhachHang";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return mapRows(rs);
        }
    }

    @Override
    protected List<KhachHang> mapRows(ResultSet rs) throws SQLException {
        List<KhachHang> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    private List<Xe> loadDanhSachXe(Connection conn, String maKhachHang) throws SQLException {
        String sql = "SELECT x.bienSo, x.hangXe, x.dongXe, x.doiXe, x.odo FROM Xe x JOIN KhachHang_Xe khx ON khx.bienSo = x.bienSo WHERE khx.maKhachHang = ? ORDER BY x.bienSo";
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

    @Override
    protected KhachHang mapRow(ResultSet rs) throws SQLException {
        return new KhachHang(
                rs.getString("maKhachHang"),
                rs.getString("sdt"),
                rs.getString("hoTen"),
                rs.getString("anhDaiDien"),
                rs.getObject("ngayTao", LocalDateTime.class));
    }
}
