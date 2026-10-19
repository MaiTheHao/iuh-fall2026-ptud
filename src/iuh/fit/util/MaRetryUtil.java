package iuh.fit.util;

import java.sql.SQLException;

public final class MaRetryUtil {

    public static final int SO_LAN_THU = 3;

    private MaRetryUtil() {
        throw new AssertionError("Utility class");
    }

    @FunctionalInterface
    public interface SinhMa {
        String sinh(int lanThu) throws SQLException;
    }

    @FunctionalInterface
    public interface ThaoTac {
        void thucHien(String ma) throws SQLException;
    }

    public static void thucHien(SinhMa sinhMa, ThaoTac thaoTac) throws SQLException {
        SQLException lastException = null;
        for (int lanThu = 0; lanThu < SO_LAN_THU; lanThu++) {
            try {
                thaoTac.thucHien(sinhMa.sinh(lanThu));
                return;
            } catch (SQLException ex) {
                if (!laLoiTrungKhoa(ex)) {
                    throw ex;
                }
                lastException = ex;
            }
        }
        throw lastException;
    }

    private static boolean laLoiTrungKhoa(SQLException ex) {
        int errorCode = ex.getErrorCode();
        return errorCode == 2627 || errorCode == 2601;
    }
}
