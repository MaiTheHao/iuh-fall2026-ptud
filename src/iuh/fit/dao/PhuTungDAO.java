package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;
import iuh.fit.entity.PhuTung;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class PhuTungDAO extends BaseDao<PhuTung> {

    public PhuTung findById(String maPhuTung) throws SQLException {
        String sql = "SELECT maPhuTung, tenPhuTung, moTa, donGiaNhap, soLuongTon FROM PhuTung WHERE maPhuTung = ?";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maPhuTung);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        }
    }

    public List<PhuTung> findAll() throws SQLException {
        String sql = "SELECT maPhuTung, tenPhuTung, moTa, donGiaNhap, soLuongTon FROM PhuTung";
        try (Connection conn = DatabaseConfig.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return mapRows(rs);
        }
    }

    @Override
    protected List<PhuTung> mapRows(ResultSet rs) throws SQLException {
        List<PhuTung> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    @Override
    protected PhuTung mapRow(ResultSet rs) throws SQLException {
        return new PhuTung(
                rs.getString("maPhuTung"),
                rs.getString("tenPhuTung"),
                rs.getString("moTa"),
                rs.getDouble("donGiaNhap"),
                rs.getInt("soLuongTon"));
    }
}
