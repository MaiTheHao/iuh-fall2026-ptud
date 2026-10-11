package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;
import iuh.fit.entity.Xe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class XeDAO extends BaseDao<Xe> {

    public Xe findById(String bienSo) throws SQLException {
        String sql = "SELECT bienSo, hangXe, dongXe, doiXe, odo FROM Xe WHERE bienSo = ?";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, bienSo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        }
    }

    public List<Xe> findAll() throws SQLException {
        String sql = "SELECT bienSo, hangXe, dongXe, doiXe, odo FROM Xe";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return mapRows(rs);
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

    @Override
    protected List<Xe> mapRows(ResultSet rs) throws SQLException {
        List<Xe> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }
}
