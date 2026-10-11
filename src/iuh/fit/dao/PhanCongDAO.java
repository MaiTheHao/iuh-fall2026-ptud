package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;
import iuh.fit.entity.PhanCong;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class PhanCongDAO extends BaseDao<PhanCong> {

    public PhanCong findById(String maPhanCong) throws SQLException {
        String sql = "SELECT maPhanCong, maNhanVien, ngay, thang, nam, caLamViec, ghiChu FROM PhanCong WHERE maPhanCong = ?";
        try (Connection connection = DatabaseConfig.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, maPhanCong);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        }
    }

    public List<PhanCong> findAll() throws SQLException {
        String sql = "SELECT maPhanCong, maNhanVien, ngay, thang, nam, caLamViec, ghiChu FROM PhanCong";
        try (Connection connection = DatabaseConfig.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return mapRows(rs);
        }
    }

    @Override
    protected PhanCong mapRow(ResultSet rs) throws SQLException {
        PhanCong phanCong = new PhanCong();
        phanCong.setMaPhanCong(rs.getString("maPhanCong"));
        phanCong.setMaNhanVien(rs.getString("maNhanVien"));
        phanCong.setNgay(rs.getInt("ngay"));
        phanCong.setThang(rs.getInt("thang"));
        phanCong.setNam(rs.getInt("nam"));
        phanCong.setCaLamViec(rs.getString("caLamViec"));
        phanCong.setGhiChu(rs.getString("ghiChu"));
        return phanCong;
    }

    @Override
    protected List<PhanCong> mapRows(ResultSet rs) throws SQLException {
        List<PhanCong> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }
}
