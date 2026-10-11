package iuh.fit.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class BaseDao<T> {

    protected abstract T mapRow(ResultSet rs) throws SQLException;

    protected abstract List<T> mapRows(ResultSet rs) throws SQLException;
}
