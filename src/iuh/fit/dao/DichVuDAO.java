package iuh.fit.dao;

import iuh.fit.entity.DichVu;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public final class DichVuDAO extends BaseDao<DichVu> {

    private static final String SQL_SELECT = "SELECT maDichVu, tenDichVu, moTa, anhUrl, donGia FROM DichVu";

    public DichVu findById(String maDichVu) throws SQLException {
        return findOne(SQL_SELECT + " WHERE maDichVu = ?", maDichVu);
    }

    public List<DichVu> findAll() throws SQLException {
        return findAll(SQL_SELECT);
    }

    @Override
    protected DichVu mapRow(ResultSet rs) throws SQLException {
        return new DichVu(
                rs.getString("maDichVu"),
                rs.getString("tenDichVu"),
                rs.getString("moTa"),
                rs.getString("anhUrl"),
                rs.getDouble("donGia"));
    }
}
