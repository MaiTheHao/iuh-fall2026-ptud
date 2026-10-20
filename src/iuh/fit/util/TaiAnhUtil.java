package iuh.fit.util;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;

/**
 * Đọc ảnh từ đường dẫn cục bộ hoặc URL mạng cho các thành phần hiển thị ảnh.
 */
public final class TaiAnhUtil {

    private TaiAnhUtil() {
        throw new AssertionError("Utility class");
    }

    /**
     * Đọc ảnh tại {@code duongDan}. Trả về {@code null} khi nội dung không phải ảnh;
     * ném {@link IOException} khi không mở được đường dẫn.
     */
    public static BufferedImage docAnh(String duongDan, int thoiGianKetNoi, int thoiGianDoc)
            throws IOException {
        String dia = duongDan.trim();

        if (dia.startsWith("https://") || dia.startsWith("http://")) {
            URLConnection connection = URI.create(dia).toURL().openConnection();
            connection.setConnectTimeout(thoiGianKetNoi);
            connection.setReadTimeout(thoiGianDoc);

            try (InputStream input = connection.getInputStream()) {
                return ImageIO.read(input);
            }
        }

        return ImageIO.read(new File(dia));
    }
}
