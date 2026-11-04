package iuh.fit.util;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;

public final class TaiAnhUtil {

    private TaiAnhUtil() {
        throw new AssertionError("Utility class");
    }

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
