package iuh.fit.gui.component;

import iuh.fit.gui.Theme;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.XlsxExportUtil;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

public final class BaoCaoUi {

  private BaoCaoUi() {
    throw new AssertionError("Utility class");
  }

  /**
   * Tạo thẻ thống kê: nhãn tiêu đề ở trên, giá trị số liệu ở dưới.
   */
  public static JPanel theThongKe(String key, JLabel lblGiaTri) {
    JPanel pnl = new JPanel(new BorderLayout(4, 8));

    pnl.setBorder(Theme.khungVien(10));

    JLabel lblTieuDe = new JLabel(I18nReaderUtil.get(key));
    lblGiaTri.setFont(Theme.soLieuFont());

    pnl.add(lblTieuDe, BorderLayout.NORTH);
    pnl.add(lblGiaTri, BorderLayout.CENTER);

    return pnl;
  }

  /**
   * Tạo thanh công cụ gồm nút in và nút xuất Excel.
   */
  public static JPanel thanhCongCu(String keyIn, Runnable hanhDongIn,
          String keyXuat, Runnable hanhDongXuat) {
    JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));

    JButton btnIn = new JButton(I18nReaderUtil.get(keyIn));
    JButton btnXuat = new JButton(I18nReaderUtil.get(keyXuat));

    Theme.applySecondary(btnIn);
    Theme.applySecondary(btnXuat);

    btnIn.addActionListener(e -> hanhDongIn.run());
    btnXuat.addActionListener(e -> hanhDongXuat.run());

    pnl.add(btnIn);
    pnl.add(btnXuat);

    return pnl;
  }

  /**
   * In bảng hiện tại; cảnh báo khi bảng chưa có dữ liệu.
   */
  public static void in(JTable bang, String keyKhongCoDuLieu) {
    if (bang.getRowCount() == 0) {
      ThongBao.canhBao(bang, keyKhongCoDuLieu);
      return;
    }

    try {
      bang.print(JTable.PrintMode.FIT_WIDTH, null, null);
    } catch (Exception ex) {
      ThongBao.loi(bang, "app.common.inThatBai");
    }
  }

  /**
   * Xuất bảng ra tệp XLSX; cảnh báo khi bảng chưa có dữ liệu.
   */
  public static void xuat(Component parent, TableModel moHinh, String tenFileMacDinh,
          String keyTieuDe, String keyKhongCoDuLieu) {
    if (moHinh.getRowCount() == 0) {
      ThongBao.canhBao(parent, keyKhongCoDuLieu);
      return;
    }

    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle(I18nReaderUtil.get(keyTieuDe));
    fileChooser.setSelectedFile(new File(tenFileMacDinh));
    fileChooser.setFileFilter(new FileNameExtensionFilter("XLSX (*.xlsx)", "xlsx"));

    if (fileChooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
      return;
    }

    File file = fileChooser.getSelectedFile();
    if (!file.getName().toLowerCase(Locale.ROOT).endsWith(".xlsx")) {
      file = new File(file.getAbsolutePath() + ".xlsx");
    }

    try {
      XlsxExportUtil.write(file, I18nReaderUtil.get(keyTieuDe), moHinh);
      ThongBao.thongTin(parent, "app.common.xuatThanhCong");
    } catch (IOException ex) {
      ThongBao.loi(parent, "app.common.xuatThatBai");
    }
  }
}
