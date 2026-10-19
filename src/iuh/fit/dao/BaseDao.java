package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class BaseDao<T> {

    protected abstract T mapRow(ResultSet rs) throws SQLException;

    protected List<T> mapRows(ResultSet rs) throws SQLException {
        List<T> list = new ArrayList<>();
        while (rs.next()) {
            list.add(this.mapRow(rs));
        }
        return list;
    }

    protected Optional<T> findOne(String sql, String id) throws SQLException {
        try (Connection connection = DatabaseConfig.getConnection()) {
            return this.findOne(connection, sql, id);
        }
    }

    protected Optional<T> findOne(Connection connection, String sql, String id) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(this.mapRow(rs)) : Optional.empty();
            }
        }
    }

    protected List<T> findAll(String sql) throws SQLException {
        try (Connection connection = DatabaseConfig.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return this.mapRows(rs);
        }
    }

    protected Optional<T> findOne(TransactionScope scope, String sql, String id) throws SQLException {
        return this.findOne(scope.getConnection(), sql, id);
    }

    protected List<T> findAll(TransactionScope scope, String sql) throws SQLException {
        try (PreparedStatement ps = scope.getConnection().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            return this.mapRows(rs);
        }
    }
}
