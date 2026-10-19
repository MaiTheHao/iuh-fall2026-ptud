package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class TransactionScope implements AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger(TransactionScope.class.getName());

    private final Connection connection;
    private boolean committed;
    private boolean rolledBack;

    private TransactionScope(Connection connection) {
        this.connection = connection;
    }

    public static TransactionScope begin() throws SQLException {
        Connection connection = DatabaseConfig.getConnection();
        try {
            connection.setAutoCommit(false);
        } catch (SQLException ex) {
            connection.close();
            throw ex;
        }
        return new TransactionScope(connection);
    }

    Connection getConnection() {
        return this.connection;
    }

    public void commit() throws SQLException {
        this.connection.commit();
        this.committed = true;
    }

    public void rollback() throws SQLException {
        if (this.committed || this.rolledBack || this.connection.isClosed()) {
            return;
        }
        this.connection.rollback();
        this.rolledBack = true;
    }

    @Override
    public void close() throws SQLException {
        try {
            if (!this.committed) {
                try {
                    this.rollback();
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Khong the rollback giao dich", ex);
                }
            }
        } finally {
            this.connection.close();
        }
    }
}
