package iuh.fit.dao;

import iuh.fit.entity.Xe;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public final class XeDAO extends BaseDao<Xe> {

    private static final String SQL_SELECT = "SELECT bienSo, hangXe, dongXe, doiXe, odo FROM Xe";

    public Xe findById(String bienSo) throws SQLException {
        return findOne(SQL_SELECT + " WHERE bienSo = ?", bienSo);
    }

    public List<Xe> findAll() throws SQLException {
        return findAll(SQL_SELECT);
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
