package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;
import iuh.fit.entity.Xe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public final class XeDAO extends BaseDao<Xe> {

    private static final String SQL_SELECT = "SELECT bienSo, hangXe, dongXe, doiXe, odo FROM Xe";

    public Optional<Xe> findById(String bienSo) throws SQLException {
        return findOne(SQL_SELECT + " WHERE bienSo = ?", bienSo);
    }

    public List<Xe> findAll() throws SQLException {
        return findAll(SQL_SELECT);
    }

    public boolean existsByBienSo(String bienSo) throws SQLException {
        String sql = "SELECT 1 FROM Xe WHERE bienSo = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bienSo);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean insert(Xe xe) throws SQLException {
        String sql = """
                INSERT INTO Xe (bienSo, hangXe, dongXe, doiXe, odo)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, xe.getBienSo());
            ps.setString(2, xe.getHangXe());
            ps.setString(3, xe.getDongXe());
            ps.setInt(4, xe.getDoiXe());
            ps.setInt(5, xe.getOdo());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(Xe xe) throws SQLException {
        String sql = """
                UPDATE Xe
                SET hangXe = ?, dongXe = ?, doiXe = ?, odo = ?
                WHERE bienSo = ?
                """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, xe.getHangXe());
            ps.setString(2, xe.getDongXe());
            ps.setInt(3, xe.getDoiXe());
            ps.setInt(4, xe.getOdo());
            ps.setString(5, xe.getBienSo());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean existsInKhachHangXe(String bienSo)
            throws SQLException {
        String sql =
                "SELECT 1 FROM KhachHang_Xe WHERE bienSo = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bienSo);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean delete(String bienSo) throws SQLException {
        String sql = "DELETE FROM Xe WHERE bienSo = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bienSo);

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    protected Xe mapRow(ResultSet rs) throws SQLException {
        return new Xe(
                rs.getString("bienSo"),
                rs.getString("hangXe"),
                rs.getString("dongXe"),
                rs.getInt("doiXe"),
                rs.getInt("odo"));
    }
}
