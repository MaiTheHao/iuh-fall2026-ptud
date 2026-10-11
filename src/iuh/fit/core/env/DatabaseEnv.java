package iuh.fit.core.env;

import iuh.fit.util.EnvReaderUtil;

public final class DatabaseEnv {
    public static final String HOST = EnvReaderUtil.getEnv("IUH_PTUD_DB_HOST", "localhost");
    public static final String PORT = EnvReaderUtil.getEnv("IUH_PTUD_DB_PORT", "1433");
    public static final String NAME = EnvReaderUtil.getEnv("IUH_PTUD_DB_NAME", "iuh_ptud_db");
    public static final String USER = EnvReaderUtil.getEnv("IUH_PTUD_DB_USER", "sa");
    public static final String PASSWORD = EnvReaderUtil.getEnv("IUH_PTUD_DB_PASSWORD", "YourStrong!Passw0rd");
    public static final String URL = String.format("jdbc:sqlserver://%s:%s;databaseName=%s;encrypt=true;trustServerCertificate=true", HOST, PORT, NAME);
}
