package iuh.fit.core.config;

import iuh.fit.util.EnvReaderUtil;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Tạo kết nối Microsoft SQL Server từ cấu hình trong application.properties.
 */
public final class DatabaseConfig {

  private static final String MAU_URL = "jdbc:sqlserver://%s:%s;databaseName=%s;encrypt=true;trustServerCertificate=true";

  private DatabaseConfig() {
    throw new AssertionError("Utility class");
  }

  public static Connection getConnection() throws SQLException {
    String url = taoUrl();
    String user = EnvReaderUtil.getOrThrow("db.user");
    String password = EnvReaderUtil.getOrThrow("db.password");
    return DriverManager.getConnection(url, user, password);
  }

  private static String taoUrl() {
    return String.format(
            MAU_URL,
            EnvReaderUtil.getOrDefault("db.host", "localhost"),
            EnvReaderUtil.getOrDefault("db.port", "1433"),
            EnvReaderUtil.getOrDefault("db.name", "iuh_ptud_db"));
  }
}
