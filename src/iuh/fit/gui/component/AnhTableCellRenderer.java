package iuh.fit.gui.component;

import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.TaiAnhUtil;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AnhTableCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    private static final int GIOI_HAN_CACHE = 200;

    private static final Map<String, ImageIcon> CACHE = Collections.synchronizedMap(new LruCache<>());

    private static final Set<String> DANG_TAI = ConcurrentHashMap.newKeySet();

    private static final Map<String, Boolean> LOI_TAI = Collections.synchronizedMap(new LruCache<>());

    private final String keyChuaCoAnh;
    private final String keyKhongTaiDuocAnh;

    private AnhTableCellRenderer(String keyChuaCoAnh, String keyKhongTaiDuocAnh) {
        this.keyChuaCoAnh = keyChuaCoAnh;
        this.keyKhongTaiDuocAnh = keyKhongTaiDuocAnh;
    }

    public static void ganVao(ManagementPanel panel, int cot, String keyChuaCoAnh, String keyKhongTaiDuocAnh) {
        if (panel.getTable().getColumnCount() == 0) {
            return;
        }

        TableColumn cotAnh = panel.getTable().getColumnModel().getColumn(cot);

        cotAnh.setCellRenderer(new AnhTableCellRenderer(keyChuaCoAnh, keyKhongTaiDuocAnh));
        cotAnh.setPreferredWidth(90);
        cotAnh.setMinWidth(90);
        cotAnh.setMaxWidth(100);

        panel.getTable().setRowHeight(72);
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table,
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

        if (LOI_TAI.containsKey(duongDan)) {
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
                            LOI_TAI.put(duongDan, Boolean.TRUE);
                        }
                    } catch (Exception e) {
                        LOI_TAI.put(duongDan, Boolean.TRUE);
                    }

                    if (table.isDisplayable()) {
                        table.repaint();
                    }
                }
            };

            worker.execute();
        }

        label.setText(I18nReaderUtil.get("app.common.dangTaiAnh"));

        return label;
    }

    private static ImageIcon taiAnh(String duongDan) {
        try {
            BufferedImage bufferedImage = TaiAnhUtil.docAnh(duongDan, 3000, 5000);

            if (bufferedImage == null) {
                return null;
            }

            return taoThumb(bufferedImage);

        } catch (Exception e) {
            return null;
        }
    }

    private static ImageIcon taoThumb(BufferedImage anh) {
        BufferedImage thumb = new BufferedImage(72, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = thumb.createGraphics();

        try {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            float tiLe = Math.min(72f / anh.getWidth(), 64f / anh.getHeight());
            int rong = Math.max(1, Math.round(anh.getWidth() * tiLe));
            int cao = Math.max(1, Math.round(anh.getHeight() * tiLe));

            g2.drawImage(anh, (72 - rong) / 2, (64 - cao) / 2, rong, cao, null);
        } finally {
            g2.dispose();
        }

        return new ImageIcon(thumb);
    }

    private static final class LruCache<V> extends LinkedHashMap<String, V> {

        private static final long serialVersionUID = 1L;

        private LruCache() {
            super(16, 0.75f, true);
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, V> eldest) {
            return this.size() > GIOI_HAN_CACHE;
        }
    }
}
