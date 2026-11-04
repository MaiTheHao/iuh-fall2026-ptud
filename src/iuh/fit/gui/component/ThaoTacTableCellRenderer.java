package iuh.fit.gui.component;

import iuh.fit.gui.Theme;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public final class ThaoTacTableCellRenderer extends JPanel implements TableCellRenderer {

  

  private static final String CHIA_KHOA = "thaoTacTableCellRenderer";

  private static final int KHOANG_CACH = 8;
  private static final int KICH_THUOC_ICON = 20;
  private static final Dimension KICH_THUOC_NUT = new Dimension(36, 32);

  private final List<ThaoTac> dsThaoTac = new ArrayList<>();
  private final List<JButton> dsNut = new ArrayList<>();
  private final List<Integer> chiSoNutDangDung = new ArrayList<>();

  private TableColumn cotThaoTac;
  private int dongDangTro = -1;
  private int thaoTacDangTro = -1;

  private ThaoTacTableCellRenderer(List<ThaoTac> dsThaoTac) {
    this.setOpaque(true);
    this.datThaoTac(dsThaoTac);
  }

  public static void ganVao(JTable table, int cot, List<ThaoTac> dsThaoTac) {
    if (table == null || dsThaoTac == null || dsThaoTac.isEmpty()
            || cot < 0 || cot >= table.getColumnCount()) {
      return;
    }

    if (table.getClientProperty(CHIA_KHOA) instanceof ThaoTacTableCellRenderer daCo) {
      daCo.cotThaoTac = table.getColumnModel().getColumn(cot);
      daCo.cotThaoTac.setCellRenderer(daCo);
      daCo.datThaoTac(dsThaoTac);
      daCo.datKichThuocCot(dsThaoTac.size());
      table.repaint();
      return;
    }

    ThaoTacTableCellRenderer renderer = new ThaoTacTableCellRenderer(dsThaoTac);
    renderer.cotThaoTac = table.getColumnModel().getColumn(cot);
    renderer.cotThaoTac.setCellRenderer(renderer);
    renderer.datKichThuocCot(dsThaoTac.size());

    table.putClientProperty(CHIA_KHOA, renderer);
    table.setDefaultRenderer(ThaoTac.class, renderer);
    table.setRowHeight(Math.max(table.getRowHeight(), 44));

    caiDatSuKien(table, renderer);
  }

  private void datThaoTac(List<ThaoTac> dsMoi) {
    this.dsThaoTac.clear();
    this.dsThaoTac.addAll(dsMoi);
    this.dsNut.clear();
    this.chiSoNutDangDung.clear();
    this.removeAll();

    for (ThaoTac thaoTac : this.dsThaoTac) {
      JButton nut = new JButton();
      nut.setFocusable(false);
      nut.setBorderPainted(false);
      nut.setContentAreaFilled(true);
      nut.setOpaque(true);
      nut.setMargin(new Insets(0, 0, 0, 0));
      nut.setRolloverEnabled(false);
      nut.setPreferredSize(KICH_THUOC_NUT);
      nut.setMinimumSize(KICH_THUOC_NUT);
      nut.getAccessibleContext().setAccessibleName(
              I18nReaderUtil.get(thaoTac.tooltipKey()));
      this.dsNut.add(nut);
      this.add(nut);
    }

    this.setLayout(new GridLayout(1, Math.max(1, this.dsThaoTac.size()), KHOANG_CACH, 0));
    this.revalidate();
  }

  private void datKichThuocCot(int soLuong) {
    int rongTieuDe = this.doRongTieuDe();
    int toiThieu = Math.max(44 * soLuong + 6, rongTieuDe);
    int uuTien = Math.max(52 * soLuong + 10, toiThieu);
    int toiDa = Math.max(60 * soLuong + 12, uuTien);

    this.cotThaoTac.setMinWidth(toiThieu);
    this.cotThaoTac.setMaxWidth(toiDa);
    this.cotThaoTac.setPreferredWidth(uuTien);
  }

  private int doRongTieuDe() {
    Object tieuDe = this.cotThaoTac == null ? null : this.cotThaoTac.getHeaderValue();

    if (tieuDe == null) {
      return 0;
    }

    Font font = UIManager.getFont("TableHeader.font");
    FontMetrics metrics = this.getFontMetrics(font == null ? this.getFont() : font);

    return metrics.stringWidth(tieuDe.toString()) + 28;
  }

  private static void caiDatSuKien(JTable table, ThaoTacTableCellRenderer renderer) {
    var chuot = new MouseAdapter() {
      private int dongNhan = -1;
      private int thaoTacNhan = -1;

      @Override
      public void mousePressed(MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e)) {
          return;
        }

        this.dongNhan = table.rowAtPoint(e.getPoint());
        this.thaoTacNhan = renderer.timThaoTac(table, e);
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e)) {
          return;
        }

        int dong = table.rowAtPoint(e.getPoint());
        int thaoTac = renderer.timThaoTac(table, e);

        if (dong >= 0 && dong == this.dongNhan
                && thaoTac >= 0 && thaoTac == this.thaoTacNhan) {
          int dongModel = table.convertRowIndexToModel(dong);
          List<ThaoTac> hienThi = renderer.thaoTacHienThi(dongModel);

          if (thaoTac < hienThi.size()) {
            hienThi.get(thaoTac).khiChon().accept(dongModel);
          }
        }

        this.dongNhan = -1;
        this.thaoTacNhan = -1;
      }

      @Override
      public void mouseExited(MouseEvent e) {
        renderer.datDangTro(-1, -1);
        table.repaint();
        table.setToolTipText(null);
        table.setCursor(null);
      }

      @Override
      public void mouseMoved(MouseEvent e) {
        int cotView = table.convertColumnIndexToView(renderer.cotThaoTac.getModelIndex());
        int dong = table.rowAtPoint(e.getPoint());
        int thaoTac = renderer.timThaoTac(table, e);

        if (dong < 0 || thaoTac < 0) {
          dong = -1;
          thaoTac = -1;
        }

        if (dong != renderer.dongDangTro || thaoTac != renderer.thaoTacDangTro) {
          if (renderer.dongDangTro >= 0) {
            table.repaint(table.getCellRect(renderer.dongDangTro, cotView, true));
          }

          renderer.datDangTro(dong, thaoTac);

          if (dong >= 0) {
            table.repaint(table.getCellRect(dong, cotView, true));
          }
        }

        String tooltip = null;
        if (thaoTac >= 0) {
          List<ThaoTac> hienThi = renderer.thaoTacHienThi(table.convertRowIndexToModel(dong));
          if (thaoTac < hienThi.size()) {
            tooltip = I18nReaderUtil.get(hienThi.get(thaoTac).tooltipKey());
          }
        }

        table.setToolTipText(tooltip);
        table.setCursor(thaoTac >= 0
                ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                : null);
      }
    };

    table.addMouseListener(chuot);
    table.addMouseMotionListener(chuot);
    table.getModel().addTableModelListener(e -> renderer.datDangTro(-1, -1));
  }

  private void datDangTro(int dong, int thaoTac) {
    this.dongDangTro = dong;
    this.thaoTacDangTro = thaoTac;
  }

  private List<ThaoTac> thaoTacHienThi(int modelRow) {
    List<ThaoTac> hienThi = new ArrayList<>();

    for (int chiSo : this.chiSoThaoTacHienThi(modelRow)) {
      hienThi.add(this.dsThaoTac.get(chiSo));
    }

    return hienThi;
  }

  private List<Integer> chiSoThaoTacHienThi(int modelRow) {
    List<Integer> chiSo = new ArrayList<>();

    for (int i = 0; i < this.dsThaoTac.size(); i++) {
      ThaoTac thaoTac = this.dsThaoTac.get(i);

      if (thaoTac.hienThi() == null || thaoTac.hienThi().test(modelRow)) {
        chiSo.add(i);
      }
    }

    return chiSo;
  }

  private int timThaoTac(JTable table, MouseEvent e) {
    if (this.cotThaoTac == null) {
      return -1;
    }

    int cotView = table.columnAtPoint(e.getPoint());

    if (cotView < 0 || table.getColumnModel().getColumn(cotView) != this.cotThaoTac) {
      return -1;
    }

    int dong = table.rowAtPoint(e.getPoint());

    if (dong < 0) {
      return -1;
    }

    int soLuong = this.thaoTacHienThi(table.convertRowIndexToModel(dong)).size();
    Rectangle o = table.getCellRect(dong, cotView, false);
    int x = e.getX() - o.x;
    int khoangCach = ((GridLayout) this.getLayout()).getHgap();
    int trongSo = o.width - khoangCach * (soLuong - 1);

    if (soLuong <= 0 || x < 0 || trongSo <= 0) {
      return -1;
    }

    int rong = trongSo / soLuong;

    for (int i = 0; i < soLuong; i++) {
      int batDau = i * (rong + khoangCach);

      if (x >= batDau && x < batDau + rong) {
        return i;
      }
    }

    return -1;
  }

  @Override
  public Component getTableCellRendererComponent(JTable table,
          Object value,
          boolean isSelected,
          boolean hasFocus,
          int row,
          int column) {

    Color nen = isSelected ? table.getSelectionBackground() : table.getBackground();
    this.setBackground(nen);

    List<Integer> chiSoHienThi = this.chiSoThaoTacHienThi(table.convertRowIndexToModel(row));
    this.capNhatNutHienThi(chiSoHienThi);

    for (int i = 0; i < chiSoHienThi.size(); i++) {
      JButton nut = this.dsNut.get(chiSoHienThi.get(i));
      ThaoTac thaoTac = this.dsThaoTac.get(chiSoHienThi.get(i));
      boolean nguyHiem = thaoTac.kieu() == ThaoTac.KieuThaoTac.NGUY_HIEM;

      nut.setIcon(new LucideIcon(thaoTac.iconName(),
              KICH_THUOC_ICON,
              nguyHiem ? Theme.palette().DANGER() : Theme.palette().INK()));

      boolean dangTro = row == this.dongDangTro && i == this.thaoTacDangTro;
      nut.setBackground(dangTro ? mauNenKhiTro(nguyHiem) : nen);
    }

    return this;
  }

  private void capNhatNutHienThi(List<Integer> chiSoHienThi) {
    if (chiSoHienThi.equals(this.chiSoNutDangDung)) {
      return;
    }

    this.removeAll();
    this.chiSoNutDangDung.clear();
    this.chiSoNutDangDung.addAll(chiSoHienThi);

    for (int chiSo : chiSoHienThi) {
      this.add(this.dsNut.get(chiSo));
    }

    this.setLayout(new GridLayout(1, Math.max(1, chiSoHienThi.size()), KHOANG_CACH, 0));
    this.revalidate();
  }

  private static Color mauNenKhiTro(boolean nguyHiem) {
    return nguyHiem ? Theme.palette().DANGER_BG() : Theme.palette().ACCENT_SOFT();
  }
}
