package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDao<T> {

    protected abstract T mapRow(ResultSet rs) throws SQLException;

    protected List<T> mapRows(ResultSet rs) throws SQLException {
        List<T> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapRow(rs));
        }
        return list;
    }

    protected T findOne(String sql, String id) throws SQLException {
        try (Connection connection = DatabaseConfig.getConnection()) {
            return findOne(connection, sql, id);
        }
    }

    protected T findOne(Connection connection, String sql, String id) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    protected List<T> findAll(String sql) throws SQLException {
        try (Connection connection = DatabaseConfig.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return mapRows(rs);
        }
    }
}
