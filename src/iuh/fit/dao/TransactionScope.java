
package iuh.fit.dao;

import iuh.fit.core.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.SQLException;

public final class TransactionScope implements AutoCloseable {

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
      return new TransactionScope(connection);
    } catch (SQLException ex) {
      try {
        connection.close();
      } catch (SQLException closeEx) {
        ex.addSuppressed(closeEx);
      }
      throw ex;
    }
  }

  Connection getConnection() {
    return connection;
  }

  public void commit() throws SQLException {
    connection.commit();
    committed = true;
  }

  public void rollback() throws SQLException {
    if (committed || rolledBack || connection.isClosed()) {
      return;
    }

    connection.rollback();
    rolledBack = true;
  }

  @Override
  public void close() throws SQLException {
    try {
      if (!committed && !rolledBack) {
        rollback();
      }
    } finally {
      connection.close();
    }
  }
}
