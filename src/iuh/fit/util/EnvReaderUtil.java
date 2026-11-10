package iuh.fit.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Đọc cấu hình ứng dụng từ một file duy nhất resources/application.properties.
 */
public final class EnvReaderUtil {

  private static final String APPLICATION_PROPERTIES = "resources/application.properties";
  private static final Properties CAU_HINH = nap();

  private EnvReaderUtil() {
    throw new AssertionError("Utility class");
  }

  public static String getOrDefault(String khoa, String macDinh) {
    String giaTri = CAU_HINH.getProperty(khoa);
    return giaTri == null || giaTri.isBlank() ? macDinh : giaTri.trim();
  }

  public static String getOrThrow(String khoa) {
    String giaTri = CAU_HINH.getProperty(khoa);

    // Báo lỗi ngay tại nơi đọc để không mang giá trị thiếu xuống tầng sử dụng.
    if (giaTri == null || giaTri.isBlank()) {
      throw new IllegalStateException("Thiếu cấu hình bắt buộc: " + khoa);
    }
    return giaTri.trim();
  }

  private static Properties nap() {
    Properties thuocTinh = new Properties();

    // Nạp qua classpath để file cấu hình đi kèm khi build, không phụ thuộc thư mục làm việc.
    try (InputStream dauVao = EnvReaderUtil.class.getClassLoader().getResourceAsStream(APPLICATION_PROPERTIES)) {
      if (dauVao == null) {
        return thuocTinh;
      }
      try (InputStreamReader doc = new InputStreamReader(dauVao, StandardCharsets.UTF_8)) {
        thuocTinh.load(doc);
      }
      return thuocTinh;
    } catch (IOException ex) {
      throw new IllegalStateException("Không đọc được " + APPLICATION_PROPERTIES, ex);
    }
  }
}
