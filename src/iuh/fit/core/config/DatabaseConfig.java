package iuh.fit.core.config;

import iuh.fit.core.env.DatabaseEnv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConfig {

  private DatabaseConfig() {}

  public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(DatabaseEnv.URL, DatabaseEnv.USER, DatabaseEnv.PASSWORD);
  }
}


