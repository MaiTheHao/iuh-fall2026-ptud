package iuh.fit.dao;

import iuh.fit.entity.PhuTung;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public final class PhuTungDAO extends BaseDao<PhuTung> {

    private static final String SQL_SELECT = "SELECT maPhuTung, tenPhuTung, moTa, donGiaNhap, soLuongTon FROM PhuTung";

    public PhuTung findById(String maPhuTung) throws SQLException {
        return findOne(SQL_SELECT + " WHERE maPhuTung = ?", maPhuTung);
    }

    public List<PhuTung> findAll() throws SQLException {
        return findAll(SQL_SELECT);
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
