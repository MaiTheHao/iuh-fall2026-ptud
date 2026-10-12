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

    private static final String SQL_SELECT = "SELECT maKhachHang, sdt, hoTen, ngayTao FROM KhachHang";

    public KhachHang findById(String maKhachHang) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection()) {
            KhachHang kh = findOne(conn, SQL_SELECT + " WHERE maKhachHang = ?", maKhachHang);
            if (kh == null) {
                return null;
            }
            kh.setDanhSachXe(loadDanhSachXe(conn, maKhachHang));
            return kh;
        }
    }

    public List<KhachHang> findAll() throws SQLException {
        return findAll(SQL_SELECT);
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
