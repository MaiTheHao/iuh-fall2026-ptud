package iuh.fit.gui.component;

import iuh.fit.util.I18nReaderUtil;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import java.awt.Component;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.net.URLConnection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AnhTableCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    private static final int GIOI_HAN_CACHE = 200;

    private static final Map<String, ImageIcon> CACHE = Collections.synchronizedMap(
            new LinkedHashMap<>(16, 0.75f, true) {
                private static final long serialVersionUID = 1L;

                @Override
                protected boolean removeEldestEntry(Map.Entry<String, ImageIcon> eldest) {
                    return size() > GIOI_HAN_CACHE;
                }
            }
    );

    private static final Set<String> DANG_TAI = ConcurrentHashMap.newKeySet();
    private static final Set<String> LOI_TAI = ConcurrentHashMap.newKeySet();

    private final String keyChuaCoAnh;
    private final String keyKhongTaiDuocAnh;

    public AnhTableCellRenderer(String keyChuaCoAnh, String keyKhongTaiDuocAnh) {
        this.keyChuaCoAnh = keyChuaCoAnh;
        this.keyKhongTaiDuocAnh = keyKhongTaiDuocAnh;
    }

    public static void ganVao(ManagementPanel panel, int cot, String keyChuaCoAnh, String keyKhongTaiDuocAnh) {
        if (panel.getTable().getColumnCount() == 0) {
            return;
        }

        TableColumn cotAnh = panel.getTable().getColumnModel().getColumn(cot);

        cotAnh.setCellRenderer(new AnhTableCellRenderer(keyChuaCoAnh, keyKhongTaiDuocAnh));
        cotAnh.setPreferredWidth(65);
        cotAnh.setMinWidth(65);
        cotAnh.setMaxWidth(80);

        panel.getTable().setRowHeight(55);
    }

    @Override
    public Component getTableCellRendererComponent(
            javax.swing.JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column) {

        JLabel label = (JLabel) super.getTableCellRendererComponent(
                table,
                value,
                isSelected,
                hasFocus,
                row,
                column
        );

        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setIcon(null);
        label.setText("");

        if (value == null || value.toString().isBlank()) {
            label.setText(I18nReaderUtil.get(this.keyChuaCoAnh));
            return label;
        }

        String duongDan = value.toString().trim();
        ImageIcon icon = CACHE.get(duongDan);

        if (icon != null) {
            label.setIcon(icon);
            return label;
        }

        if (LOI_TAI.contains(duongDan)) {
            label.setText(I18nReaderUtil.get(this.keyKhongTaiDuocAnh));
            return label;
        }

        if (DANG_TAI.add(duongDan)) {
            SwingWorker<ImageIcon, Void> worker = new SwingWorker<>() {
                @Override
                protected ImageIcon doInBackground() {
                    return taiAnh(duongDan);
                }

                @Override
                protected void done() {
                    DANG_TAI.remove(duongDan);

                    try {
                        ImageIcon taiDuoc = get();

                        if (taiDuoc != null) {
                            CACHE.put(duongDan, taiDuoc);
                        } else {
                            LOI_TAI.add(duongDan);
                        }
                    } catch (Exception e) {
                        LOI_TAI.add(duongDan);
                    }

                    table.repaint();
                }
            };

            worker.execute();
        }

        return label;
    }

    private static ImageIcon taiAnh(String duongDan) {
        try {
            BufferedImage bufferedImage;

            if (duongDan.startsWith("https://") || duongDan.startsWith("http://")) {
                URLConnection connection = URI.create(duongDan.trim()).toURL().openConnection();

                connection.setConnectTimeout(3000);
                connection.setReadTimeout(5000);

                try (var input = connection.getInputStream()) {
                    bufferedImage = ImageIO.read(input);
                }
            } else {
                bufferedImage = ImageIO.read(new File(duongDan.trim()));
            }

            if (bufferedImage == null) {
                return null;
            }

            Image image = bufferedImage.getScaledInstance(50, 45, Image.SCALE_SMOOTH);

            return new ImageIcon(image);

        } catch (Exception e) {
            return null;
        }
    }
}
