package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;
import iuh.fit.entity.DichVu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class DichVuDAO extends BaseDao<DichVu> {

    public DichVu findById(String maDichVu) throws SQLException {
        String sql = "SELECT maDichVu, tenDichVu, moTa, donGia FROM DichVu WHERE maDichVu = ?";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maDichVu);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        }
    }

    public List<DichVu> findAll() throws SQLException {
        String sql = "SELECT maDichVu, tenDichVu, moTa, donGia FROM DichVu";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return mapRows(rs);
        }
    }

    @Override
    protected List<DichVu> mapRows(ResultSet rs) throws SQLException {
        List<DichVu> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    protected DichVu mapRow(ResultSet rs) throws SQLException {
        return new DichVu(
                rs.getString("maDichVu"),
                rs.getString("tenDichVu"),
                rs.getString("moTa"),
                rs.getDouble("donGia"));
    }
}
