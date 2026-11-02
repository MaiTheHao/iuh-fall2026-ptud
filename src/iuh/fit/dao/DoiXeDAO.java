package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;
import iuh.fit.entity.DoiXe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class DoiXeDAO extends BaseDao<DoiXe> {

    private static final String SQL_SELECT = """
            SELECT dx.maDoiXe, dx.maDongXe, dx.namSX, d.maHangXe, h.ten AS tenHangXe, d.ten AS tenDongXe FROM DoiXe dx JOIN DongXe d ON d.maDongXe = dx.maDongXe JOIN HangXe h ON h.maHangXe = d.maHangXe""";

    public Optional<DoiXe> findById(String maDoiXe) throws SQLException {
        return findOne(SQL_SELECT + " WHERE dx.maDoiXe = ?", maDoiXe);
    }

    public List<DoiXe> findAll() throws SQLException {
        return findAll(SQL_SELECT + " ORDER BY h.ten, d.ten, dx.namSX DESC");
    }

    public List<DoiXe> findByDong(String maDongXe) throws SQLException {
        String sql = SQL_SELECT + " WHERE dx.maDongXe = ? ORDER BY dx.namSX DESC";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDongXe);
            try (ResultSet rs = ps.executeQuery()) {
                List<DoiXe> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
                return list;
            }
        }
    }

    public void insert(DoiXe doiXe) throws SQLException {
        String sql = """
                INSERT INTO DoiXe (maDoiXe, maDongXe, namSX) VALUES (?, ?, ?)""";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, doiXe.getMaDoiXe());
            ps.setString(2, doiXe.getMaDongXe());
            ps.setInt(3, doiXe.getNamSX());
            ps.executeUpdate();
        }
    }

    public void update(DoiXe doiXe) throws SQLException {
        String sql = """
                UPDATE DoiXe SET maDongXe = ?, namSX = ? WHERE maDoiXe = ?""";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, doiXe.getMaDongXe());
            ps.setInt(2, doiXe.getNamSX());
            ps.setString(3, doiXe.getMaDoiXe());
            ps.executeUpdate();
        }
    }

    public void delete(String maDoiXe) throws SQLException {
        String sql = """
                DELETE FROM DoiXe WHERE maDoiXe = ?""";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDoiXe);
            ps.executeUpdate();
        }
    }

    public boolean existsById(String maDoiXe) throws SQLException {
        String sql = """
                SELECT 1 FROM DoiXe WHERE maDoiXe = ?""";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDoiXe);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean existsByDong(String maDongXe) throws SQLException {
        String sql = """
                SELECT 1 FROM DoiXe WHERE maDongXe = ?""";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDongXe);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean existsByDongAndNam(String maDongXe, int namSX) throws SQLException {
        String sql = """
                SELECT 1 FROM DoiXe WHERE maDongXe = ? AND namSX = ?""";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDongXe);
            ps.setInt(2, namSX);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    protected DoiXe mapRow(ResultSet rs) throws SQLException {
        return new DoiXe(
                rs.getString("maDoiXe"),
                rs.getString("maDongXe"),
                rs.getInt("namSX"),
                rs.getString("maHangXe"),
                rs.getString("tenHangXe"),
                rs.getString("tenDongXe"));
    }
}
