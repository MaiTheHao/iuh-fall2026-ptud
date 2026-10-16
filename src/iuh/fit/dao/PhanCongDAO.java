package iuh.fit.dao;

import iuh.fit.entity.PhanCong;
import iuh.fit.enumeration.CaLamViec;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public final class PhanCongDAO extends BaseDao<PhanCong> {

    private static final String SQL_SELECT = """
            SELECT maPhanCong, maNhanVien, ngay, thang, nam, caLamViec, ghiChu
            FROM PhanCong
            """;

    public Optional<PhanCong> findById(String maPhanCong) throws SQLException {
        return findOne(SQL_SELECT + " WHERE maPhanCong = ?", maPhanCong);
    }

    public List<PhanCong> findAll() throws SQLException {
        return findAll(SQL_SELECT);
    }

    @Override
    protected PhanCong mapRow(ResultSet rs) throws SQLException {
        PhanCong phanCong = new PhanCong();
        phanCong.setMaPhanCong(rs.getString("maPhanCong"));
        phanCong.setMaNhanVien(rs.getString("maNhanVien"));
        phanCong.setNgay(rs.getInt("ngay"));
        phanCong.setThang(rs.getInt("thang"));
        phanCong.setNam(rs.getInt("nam"));
        phanCong.setCaLamViec(CaLamViec.valueOf(rs.getString("caLamViec")));
        phanCong.setGhiChu(rs.getString("ghiChu"));
        return phanCong;
    }
}
