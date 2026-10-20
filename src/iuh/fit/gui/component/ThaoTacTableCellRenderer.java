package iuh.fit.gui.component;

import iuh.fit.gui.theme.Theme;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Vẽ các nút icon trong một ô và xử lý click theo tọa độ ô.
 */
public final class ThaoTacTableCellRenderer extends JPanel implements TableCellRenderer {

    private static final long serialVersionUID = 1L;

    private static final int KHOANG_CACH = 4;
    private static final int KICH_THUOC_ICON = 14;

    private final List<ThaoTac> dsThaoTac;
    private final List<JButton> dsNut;

    private int dongDangTro = -1;
    private int thaoTacDangTro = -1;

    public ThaoTacTableCellRenderer(List<ThaoTac> dsThaoTac) {
        this.dsThaoTac = List.copyOf(dsThaoTac);
        this.dsNut = new ArrayList<>(this.dsThaoTac.size());

        this.setLayout(new GridLayout(1, Math.max(1, this.dsThaoTac.size()), KHOANG_CACH, 0));
        this.setOpaque(true);

        for (ThaoTac thaoTac : this.dsThaoTac) {
            JButton nut = new JButton();
            nut.setFocusable(false);
            nut.setBorder(BorderFactory.createEmptyBorder());
            nut.setBorderPainted(false);
            nut.setContentAreaFilled(true);
            nut.setOpaque(true);
            nut.setMargin(new Insets(0, 0, 0, 0));
            nut.setRolloverEnabled(false);
            nut.setPreferredSize(new Dimension(28, 24));
            nut.getAccessibleContext().setAccessibleName(
                    I18nReaderUtil.get(thaoTac.tooltipKey()));
            this.dsNut.add(nut);
            this.add(nut);
        }
    }

    public static void ganVao(JTable table, int cot, List<ThaoTac> dsThaoTac) {
        if (table == null || dsThaoTac == null || dsThaoTac.isEmpty()
                || cot < 0 || cot >= table.getColumnCount()) {
            return;
        }

        ThaoTacTableCellRenderer renderer = new ThaoTacTableCellRenderer(dsThaoTac);
        TableColumn cotThaoTac = table.getColumnModel().getColumn(cot);

        cotThaoTac.setCellRenderer(renderer);
        cotThaoTac.setPreferredWidth(32 * dsThaoTac.size() + 8);
        cotThaoTac.setMinWidth(28 * dsThaoTac.size() + 4);
        cotThaoTac.setMaxWidth(40 * dsThaoTac.size() + 12);

        table.setRowHeight(Math.max(table.getRowHeight(), 30));

        table.addMouseListener(new MouseAdapter() {
            private int dongNhan = -1;
            private int thaoTacNhan = -1;

            @Override
            public void mousePressed(MouseEvent e) {
                if (!SwingUtilities.isLeftMouseButton(e)) {
                    return;
                }

                this.dongNhan = table.rowAtPoint(e.getPoint());
                this.thaoTacNhan = timThaoTac(table, e, cotThaoTac, dsThaoTac.size());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!SwingUtilities.isLeftMouseButton(e)) {
                    return;
                }

                int dong = table.rowAtPoint(e.getPoint());
                int thaoTac = timThaoTac(table, e, cotThaoTac, dsThaoTac.size());

                if (dong >= 0 && dong == this.dongNhan
                        && thaoTac >= 0 && thaoTac == this.thaoTacNhan) {
                    table.setRowSelectionInterval(dong, dong);
                    dsThaoTac.get(thaoTac).khiChon()
                            .accept(table.convertRowIndexToModel(dong));
                }

                this.dongNhan = -1;
                this.thaoTacNhan = -1;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                renderer.dongDangTro = -1;
                renderer.thaoTacDangTro = -1;
                table.repaint();
                table.setToolTipText(null);
                table.setCursor(null);
            }
        });

        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int dong = table.rowAtPoint(e.getPoint());
                int thaoTac = timThaoTac(table, e, cotThaoTac, dsThaoTac.size());

                renderer.dongDangTro = thaoTac >= 0 ? dong : -1;
                renderer.thaoTacDangTro = thaoTac;
                table.repaint();

                table.setToolTipText(thaoTac >= 0
                        ? I18nReaderUtil.get(dsThaoTac.get(thaoTac).tooltipKey())
                        : null);

                table.setCursor(thaoTac >= 0
                        ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                        : null);
            }
        });
    }

    private static int timThaoTac(JTable table, MouseEvent e, TableColumn cot, int soLuong) {
        int cotView = table.columnAtPoint(e.getPoint());

        if (cotView < 0 || table.getColumnModel().getColumn(cotView) != cot) {
            return -1;
        }

        int dong = table.rowAtPoint(e.getPoint());

        if (dong < 0) {
            return -1;
        }

        Rectangle o = table.getCellRect(dong, cotView, false);
        int x = e.getX() - o.x;
        int trongSo = o.width - KHOANG_CACH * (soLuong - 1);

        if (x < 0 || trongSo <= 0) {
            return -1;
        }

        int rong = trongSo / soLuong;

        for (int i = 0; i < soLuong; i++) {
            int batDau = i * (rong + KHOANG_CACH);
            if (x >= batDau && x < batDau + rong) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        Color nen = isSelected ? table.getSelectionBackground() : table.getBackground();
        this.setBackground(nen);

        for (int i = 0; i < this.dsThaoTac.size(); i++) {
            ThaoTac thaoTac = this.dsThaoTac.get(i);
            JButton nut = this.dsNut.get(i);

            Color mauIcon = thaoTac.kieu() == ThaoTac.KieuThaoTac.NGUY_HIEM
                    ? Theme.palette().DANGER()
                    : Theme.palette().INK();

            nut.setIcon(new LucideIcon(thaoTac.iconName(), KICH_THUOC_ICON, mauIcon));

            boolean dangTro = row == this.dongDangTro && i == this.thaoTacDangTro;

            if (dangTro) {
                nut.setBackground(thaoTac.kieu() == ThaoTac.KieuThaoTac.NGUY_HIEM
                        ? Theme.palette().DANGER_BG()
                        : Theme.palette().ACCENT_SOFT());
            } else {
                nut.setBackground(nen);
            }
        }

        return this;
    }
}
