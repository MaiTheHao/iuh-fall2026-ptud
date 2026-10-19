package iuh.fit.gui.component;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Icon Lucide nạp từ PNG trong resources và tô màu theo theme tại thời điểm vẽ.
 */
public final class LucideIcon implements Icon {

    private static final Logger LOGGER = Logger.getLogger(LucideIcon.class.getName());

    private static final Map<String, Optional<BufferedImage>> KHO_ANH = new ConcurrentHashMap<>();
    private static final Map<String, BufferedImage> KHO_TO_MAU = new ConcurrentHashMap<>();

    private final String ten;
    private final int kichThuoc;
    private final Color mau;

    public LucideIcon(String ten, int kichThuoc, Color mau) {
        this.ten = ten;
        this.kichThuoc = kichThuoc;
        this.mau = mau;
    }

    @Override
    public int getIconWidth() {
        return this.kichThuoc;
    }

    @Override
    public int getIconHeight() {
        return this.kichThuoc;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        BufferedImage anh = this.layAnhToMau();

        if (anh == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.drawImage(anh, x, y, this.kichThuoc, this.kichThuoc, null);
        g2.dispose();
    }

    private BufferedImage layAnhToMau() {
        return KHO_TO_MAU.computeIfAbsent(
                this.ten + "#" + this.mau.getRGB(),
                khoa -> this.taoAnhToMau());
    }

    private BufferedImage taoAnhToMau() {
        BufferedImage goc = KHO_ANH
                .computeIfAbsent(this.ten, ten -> Optional.ofNullable(taiAnh(ten)))
                .orElse(null);

        if (goc == null) {
            return null;
        }

        BufferedImage toMau = new BufferedImage(
                goc.getWidth(), goc.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = toMau.createGraphics();
        g2.drawImage(goc, 0, 0, null);
        g2.setComposite(AlphaComposite.SrcIn);
        g2.setColor(this.mau);
        g2.fillRect(0, 0, toMau.getWidth(), toMau.getHeight());
        g2.dispose();

        return toMau;
    }

    private static BufferedImage taiAnh(String ten) {
        String duongDan = "resources/icons/" + ten + ".png";
        InputStream stream = LucideIcon.class.getClassLoader().getResourceAsStream(duongDan);

        if (stream == null) {
            LOGGER.warning(() -> "Icon resource not found: " + duongDan);
            return null;
        }

        try (stream) {
            return ImageIO.read(stream);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Cannot read icon resource: " + duongDan, e);
            return null;
        }
    }
}
