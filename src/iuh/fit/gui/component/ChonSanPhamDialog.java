package iuh.fit.gui.component;

import com.formdev.flatlaf.FlatClientProperties;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.gui.Theme;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

public abstract class ChonSanPhamDialog<T> extends JDialog {

  

  private static final int DO_RONG = 960;
  private static final int CHIEU_CAO = 640;
  private static final int THOI_GIAN_CHO_LOC = 250;

  protected static final int PAGE_SIZE_TAI_TOAN_BO = PaginatedResult.PAGE_SIZE_TAI_TOAN_BO;

  private final JTextField txtTimKiem = new JTextField();
  private final JLabel lblTrangThai = new JLabel();
  private final JSpinner soLuongSpinner =
          new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
  private final JPanel pnlBoLoc = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
  private final DefaultTableModel model = new DefaultTableModel(new Object[0], 0) {
    

    @Override
    public boolean isCellEditable(int row, int column) {
      return false;
    }
  };
  private final JTable tblKetQua = new JTable(this.model);
  private final Timer boDemLoc;
  private final List<T> dangHienThi = new ArrayList<>();
  private SwingWorker<KetQuaTai<T>, Void> workerHienTai;
  private int theHeTai;

  private KetQuaChon<T> ketQua;
  private boolean daTaiLanDau;
  private boolean daDong;
  private final boolean coSoLuong;

  protected ChonSanPhamDialog(Window owner, String titleKey) {
    this(owner, titleKey, true);
  }

  protected ChonSanPhamDialog(Window owner, String titleKey, boolean coSoLuong) {
    super(owner, I18nReaderUtil.get(titleKey), ModalityType.APPLICATION_MODAL);

    this.coSoLuong = coSoLuong;

    this.setLayout(new BorderLayout());
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.setSize(DO_RONG, CHIEU_CAO);
    this.setLocationRelativeTo(owner);
    this.getContentPane().setBackground(Theme.BG);

    this.pnlBoLoc.setOpaque(false);
    this.tblKetQua.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    this.tblKetQua.setRowHeight(34);
    this.tblKetQua.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    Theme.styleTable(this.tblKetQua);

    this.txtTimKiem.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON,
            new LucideIcon("search", 16, Theme.palette().INK_MUTED()));

    this.boDemLoc = new Timer(THOI_GIAN_CHO_LOC, e -> this.taiLai());
    this.boDemLoc.setRepeats(false);

    this.txtTimKiem.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        ChonSanPhamDialog.this.boDemLoc.restart();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        ChonSanPhamDialog.this.boDemLoc.restart();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        ChonSanPhamDialog.this.boDemLoc.restart();
      }
    });

    this.tblKetQua.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          ChonSanPhamDialog.this.xacNhan();
        }
      }
    });

    JPanel noiDung = Theme.contentPanel(12);
    noiDung.add(this.taoPanelTimKiem(), BorderLayout.NORTH);
    noiDung.add(new JScrollPane(this.tblKetQua), BorderLayout.CENTER);
    noiDung.add(this.taoPanelDuoi(), BorderLayout.SOUTH);
    this.add(noiDung, BorderLayout.CENTER);

    this.getRootPane().registerKeyboardAction(
            e -> this.dispose(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW);

    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowOpened(WindowEvent e) {
        ChonSanPhamDialog.this.txtTimKiem.requestFocusInWindow();
      }
    });
  }

  protected abstract KetQuaTai<T> taiDuLieu(String tuKhoa) throws SQLException;

  protected void chuanBiTaiLieu() {
  }

  protected abstract String[] tenCot();

  protected abstract Object[] giaTriDong(T item);

  protected abstract String placeholderTimKiem();

  protected abstract String keyKhongTimThay();

  protected abstract String keyChuaChon();

  protected final void datBoLoc(JComponent boLoc) {
    if (boLoc == null) {
      return;
    }
    this.pnlBoLoc.add(boLoc);
  }

  protected final void locLai() {
    this.boDemLoc.restart();
  }

  public final Optional<KetQuaChon<T>> showDialog() {
    if (this.daDong) {
      throw new IllegalStateException("Dialog đã đóng; tạo instance mới");
    }

    if (!this.daTaiLanDau) {
      this.daTaiLanDau = true;
      this.txtTimKiem.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,
              I18nReaderUtil.get(this.placeholderTimKiem()));
      this.model.setColumnIdentifiers(this.tenCot());
    }

    this.taiLai();

    this.setVisible(true);
    return Optional.ofNullable(this.ketQua);
  }

  private JPanel taoPanelTimKiem() {
    JPanel panel = new JPanel(new BorderLayout(12, 0));
    panel.setOpaque(false);
    panel.add(this.txtTimKiem, BorderLayout.CENTER);
    panel.add(this.pnlBoLoc, BorderLayout.EAST);
    return panel;
  }

  private JPanel taoPanelDuoi() {
    JButton btnChon = new JButton(I18nReaderUtil.get("app.common.confirm"));
    JButton btnHuy = new JButton(I18nReaderUtil.get("app.common.cancel"));

    btnChon.setIcon(new LucideIcon("check", 16, Theme.palette().ON_ACCENT()));
    Theme.applyPrimary(btnChon);
    Theme.applySecondary(btnHuy);

    btnChon.addActionListener(e -> this.xacNhan());
    btnHuy.addActionListener(e -> this.dispose());
    this.getRootPane().setDefaultButton(btnChon);

    JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
    controls.setOpaque(false);

    if (this.coSoLuong) {
      this.soLuongSpinner.setPreferredSize(new Dimension(90, 32));
      controls.add(this.soLuongSpinner);
    }

    controls.add(btnChon);
    controls.add(btnHuy);

    this.lblTrangThai.setForeground(Theme.palette().INK_MUTED());

    JPanel panel = new JPanel(new BorderLayout(8, 0));
    panel.setOpaque(false);
    panel.add(this.lblTrangThai, BorderLayout.WEST);
    panel.add(controls, BorderLayout.EAST);
    return panel;
  }

  private void taiLai() {
    if (this.workerHienTai != null) {
      this.workerHienTai.cancel(true);
    }

    this.chuanBiTaiLieu();

    final int theHe = ++this.theHeTai;
    final String tuKhoa = this.txtTimKiem.getText();

    SwingWorker<KetQuaTai<T>, Void> worker = new SwingWorker<KetQuaTai<T>, Void>() {
      private Exception loiTai;

      @Override
      protected KetQuaTai<T> doInBackground() {
        try {
          return ChonSanPhamDialog.this.taiDuLieu(tuKhoa);
        } catch (SQLException | RuntimeException ex) {
          this.loiTai = ex;
          return new KetQuaTai<>(List.of(), -1);
        }
      }

      @Override
      protected void done() {
        if (!ChonSanPhamDialog.this.isDisplayable()) {
          return;
        }

        if (theHe != ChonSanPhamDialog.this.theHeTai) {
          return;
        }

        if (this.loiTai != null) {
          ChonSanPhamDialog.this.baoKhongTaiDuoc();
          return;
        }

        KetQuaTai<T> ketQuaTai;
        try {
          ketQuaTai = this.get();
        } catch (CancellationException ex) {
          return;
        } catch (InterruptedException ex) {
          Thread.currentThread().interrupt();
          ChonSanPhamDialog.this.baoKhongTaiDuoc();
          return;
        } catch (ExecutionException ex) {
          ChonSanPhamDialog.this.baoKhongTaiDuoc();
          return;
        }

        List<T> danhSach = ketQuaTai.items();

        ChonSanPhamDialog.this.dangHienThi.clear();
        ChonSanPhamDialog.this.dangHienThi.addAll(danhSach);

        Object[][] duLieu = new Object[danhSach.size()][];
        for (int i = 0; i < danhSach.size(); i++) {
          duLieu[i] = ChonSanPhamDialog.this.giaTriDong(danhSach.get(i));
        }
        ChonSanPhamDialog.this.model.setDataVector(duLieu, ChonSanPhamDialog.this.tenCot());

        ChonSanPhamDialog.this.capNhatTrangThai(danhSach.size(), ketQuaTai.tongSoKetQua());
      }
    };

    this.workerHienTai = worker;
    worker.execute();
  }

  private void capNhatTrangThai(int soKetQua, int tongSoKetQua) {
    if (tongSoKetQua > soKetQua) {
      String mau = I18nReaderUtil.get("app.common.resultCountLimited");
      this.lblTrangThai.setText(
              MessageFormat.format(mau == null ? "{0}/{1}" : mau, soKetQua, tongSoKetQua));
      return;
    }

    if (soKetQua == 0) {
      this.lblTrangThai.setText(I18nReaderUtil.get(this.keyKhongTimThay()));
      return;
    }

    String mau = I18nReaderUtil.get("app.common.resultCount");
    this.lblTrangThai.setText(MessageFormat.format(mau == null ? "{0}" : mau, soKetQua));
  }

  private void baoKhongTaiDuoc() {
    this.dangHienThi.clear();
    this.model.setDataVector(new Object[0][], this.tenCot());
    this.lblTrangThai.setText(I18nReaderUtil.get("app.common.loiKhongXacDinh"));
    ThongBao.loi(this, "app.common.loiKhongXacDinh");
  }

  protected static int tongSoKetQuaCanBao(PaginatedResult<?> ketQua) {
    if (ketQua.items().size() < ketQua.pageSize()) {
      return -1;
    }
    return (int) Math.min(ketQua.totalItems(), Integer.MAX_VALUE);
  }

  private void xacNhan() {
    int soLuong = 1;

    if (this.coSoLuong) {
      try {
        this.soLuongSpinner.commitEdit();
      } catch (ParseException ex) {
        ThongBao.canhBao(this, "app.common.soLuongKhongHopLe");
        return;
      }

      Object giaTriSoLuong = this.soLuongSpinner.getValue();
      soLuong = giaTriSoLuong instanceof Number ? ((Number) giaTriSoLuong).intValue() : 0;

      if (soLuong <= 0) {
        ThongBao.canhBao(this, "app.common.soLuongKhongHopLe");
        return;
      }
    }

    int dong = this.tblKetQua.getSelectedRow();
    if (dong < 0) {
      ThongBao.canhBao(this, this.keyChuaChon());
      return;
    }

    int index = this.tblKetQua.convertRowIndexToModel(dong);
    if (index < 0 || index >= this.dangHienThi.size()) {
      ThongBao.canhBao(this, this.keyChuaChon());
      return;
    }

    T sanPham = this.dangHienThi.get(index);
    this.ketQua = new KetQuaChon<>(sanPham, soLuong);
    this.dispose();
  }

  @Override
  public void dispose() {
    this.daDong = true;
    this.boDemLoc.stop();
    if (this.workerHienTai != null) {
      this.workerHienTai.cancel(true);
    }
    super.dispose();
  }

  public record KetQuaChon<T>(T sanPham, int soLuong) {
  }

  public record KetQuaTai<T>(List<T> items, int tongSoKetQua) {
    public KetQuaTai {
      items = items == null ? List.of() : List.copyOf(items);
    }
  }
}
