package iuh.fit.gui.component;

import com.formdev.flatlaf.FlatClientProperties;
import com.github.lgooddatepicker.components.DatePicker;
import iuh.fit.gui.theme.Theme;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.AbstractDocument;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class ManagementPanel extends JPanel {

  private static final String THE_BANG = "bang";
  private static final String THE_TRONG = "trong";

  private static final int CO_ICON_TIM = 16;
  private static final int CO_ICON_NUT = 18;
  private static final int RONG_COT_CHON = 44;

  /** Handle của một bộ lọc chọn một giá trị; đọc mã enum hoặc giá trị thô. */
  public record BoLocChon(JComboBox<Object> combo) {

    public String ma() {
      Object chon = this.combo.getSelectedItem();

      if (chon == null) {
        return null;
      }

      return chon instanceof Enum<?> e ? e.name() : chon.toString();
    }

    public Object giaTri() {
      return this.combo.getSelectedItem();
    }
  }

  /** Handle của bộ lọc khoảng ngày. */
  public record BoLocNgay(DatePicker tu, DatePicker den) {

    public LocalDate tuNgay() {
      return this.tu.getDate();
    }

    public LocalDate denNgay() {
      return this.den.getDate();
    }
  }

  /** Handle của bộ lọc khoảng nhập tay. */
  public record BoLocKhoang(JTextField tu, JTextField den) {

    public String tuText() {
      return this.tu.getText();
    }

    public String denText() {
      return this.den.getText();
    }
  }

  private final List<JComboBox<Object>> boLocCombo = new ArrayList<>();
  private final DefaultTableModel moHinhBang = new DefaultTableModel() {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean isCellEditable(int row, int column) {
      return ManagementPanel.this.chonNhieu && column == 0;
    }

    @Override
    public Class<?> getColumnClass(int column) {
      return ManagementPanel.this.chonNhieu && column == 0
              ? Boolean.class
              : Object.class;
    }
  };
  private final JTable tblDuLieu = new JTable(this.moHinhBang) {

    private static final long serialVersionUID = 1L;

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
      Component thanhPhan = super.prepareRenderer(renderer, row, column);
      if (!this.isRowSelected(row) && !(renderer instanceof ThaoTacTableCellRenderer)) {
        thanhPhan.setBackground(row % 2 == 0
                ? this.getBackground()
                : Theme.palette().TABLE_ROW_ALT());
      }
      return thanhPhan;
    }
  };
  private final CardLayout theDanhSach = new CardLayout();
  private final JPanel pnlDanhSach = new JPanel(this.theDanhSach);

  private final JPanel pnlBoLoc = new KhungBoLoc();
  private final List<JTextField> boLocText = new ArrayList<>();
  private final List<DatePicker[]> boLocNgay = new ArrayList<>();
  private final List<JLabel> nhanBoLoc = new ArrayList<>();
  private final JTextField txtOTimNhanh = new JTextField(20);
  private final JLabel lblNhanDem = new JLabel();
  private final JButton btnXoaLoc = new JButton(I18nReaderUtil.get("app.common.clearFilter"));
  private final JButton btnTruoc = new JButton(I18nReaderUtil.get("app.common.prev"));
  private final JButton btnSau = new JButton(I18nReaderUtil.get("app.common.next"));
  private final JLabel lblNhanTrang = new JLabel();

  private final JPanel pnlThanhThaoTac = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
  private final JButton btnThem = new JButton();
  private final JButton btnBatchAction = new JButton();

  private Runnable khiDoiBoLoc;
  private boolean dangXoaLoc;
  private final Timer henGioLoc = new Timer(300, e -> this.fireFilterChange());

  private boolean chonNhieu;
  private String[] tieuDeDuLieu = new String[0];
  private String tieuDeThaoTacKey;
  private List<ThaoTac> dsThaoTacHienTai;
  private int cotThaoTacView = -1;
  private String nhanBatchKey;
  private Consumer<List<Integer>> xuLyBatch;
  private Runnable xuLyThem;
  private Consumer<Integer> khiNhapDup;

  public ManagementPanel() {
    this.henGioLoc.setRepeats(false);

    this.setLayout(new BorderLayout(4, 4));
    this.setOpaque(false);

    this.tblDuLieu.setFillsViewportHeight(true);
    this.tblDuLieu.setRowHeight(34);
    this.txtOTimNhanh.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,
            I18nReaderUtil.get("app.common.search"));
    this.txtOTimNhanh.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON,
            new LucideIcon("search", CO_ICON_TIM, Theme.palette().INK_MUTED()));

    Theme.styleTable(this.tblDuLieu);
    Theme.applySecondary(this.btnXoaLoc);
    Theme.applySecondary(this.btnTruoc);
    Theme.applySecondary(this.btnSau);

    this.attachTextListener(this.txtOTimNhanh);
    this.cauHinhThanhThaoTac();
    this.caiDatSuKienBang();

    JPanel listPanel = new JPanel(new BorderLayout(4, 4));
    listPanel.setOpaque(false);
    listPanel.add(this.buildFilterPanel(), BorderLayout.NORTH);
    listPanel.add(this.buildTablePanel(), BorderLayout.CENTER);
    listPanel.add(this.buildPaginationPanel(), BorderLayout.SOUTH);

    this.pnlDanhSach.setOpaque(false);
    this.pnlDanhSach.add(new JScrollPane(this.tblDuLieu), THE_BANG);
    this.pnlDanhSach.add(this.buildEmptyLabel(), THE_TRONG);

    this.add(listPanel, BorderLayout.CENTER);
  }

  // ==================== THANH THAO TÁC TRÊN BẢNG ====================

  private void cauHinhThanhThaoTac() {
    this.pnlThanhThaoTac.setOpaque(false);

    this.btnThem.setIcon(new LucideIcon("plus", CO_ICON_NUT, Theme.palette().ON_ACCENT()));
    this.btnThem.setIconTextGap(6);
    this.btnThem.setFocusPainted(false);
    this.btnThem.setVisible(false);
    this.btnThem.addActionListener(e -> {
      if (this.xuLyThem != null) {
        this.xuLyThem.run();
      }
    });
    Theme.applyPrimary(this.btnThem);

    this.btnBatchAction.setIcon(new LucideIcon("trash-2", CO_ICON_NUT, Theme.palette().ON_ACCENT()));
    this.btnBatchAction.setIconTextGap(6);
    this.btnBatchAction.setFocusPainted(false);
    this.btnBatchAction.setVisible(false);
    this.btnBatchAction.setEnabled(false);
    this.btnBatchAction.addActionListener(e -> {
      if (this.xuLyBatch != null) {
        this.xuLyBatch.accept(this.getSelectedRowIndices());
      }
    });

    this.pnlThanhThaoTac.add(this.btnBatchAction);
    this.pnlThanhThaoTac.add(this.btnThem);
  }

  private JPanel buildTablePanel() {
    JPanel khung = new JPanel(new BorderLayout(0, 4));
    khung.setOpaque(false);
    khung.add(this.pnlThanhThaoTac, BorderLayout.NORTH);
    khung.add(this.pnlDanhSach, BorderLayout.CENTER);
    return khung;
  }

  public void setAddAction(String labelKey, Runnable onAdd) {
    this.xuLyThem = onAdd;
    this.btnThem.setText(I18nReaderUtil.get(labelKey));
    this.btnThem.setVisible(true);
    this.pnlThanhThaoTac.revalidate();
    this.pnlThanhThaoTac.repaint();
  }

  public void setBatchAction(String labelKey,
          String iconName,
          ThaoTac.KieuThaoTac kieu,
          Consumer<List<Integer>> onBatch) {
    this.nhanBatchKey = labelKey;
    this.xuLyBatch = onBatch;

    boolean nguyHiem = kieu == ThaoTac.KieuThaoTac.NGUY_HIEM;
    if (nguyHiem) {
      Theme.applyDanger(this.btnBatchAction);
    } else {
      Theme.applySecondary(this.btnBatchAction);
    }

    this.btnBatchAction.setIcon(new LucideIcon(iconName,
            CO_ICON_NUT,
            nguyHiem ? Theme.palette().ON_ACCENT() : Theme.INK));
    this.btnBatchAction.setVisible(true);

    this.capNhatNutBatch();
    this.pnlThanhThaoTac.revalidate();
    this.pnlThanhThaoTac.repaint();
  }

  public void enableMultiSelection(boolean enable) {
    if (this.chonNhieu == enable) {
      return;
    }

    List<Object[]> duLieuCu = this.chupDuLieu();
    this.chonNhieu = enable;
    this.dungLaiCot(duLieuCu);
  }

  public List<Integer> getSelectedRowIndices() {
    List<Integer> ketQua = new ArrayList<>();

    if (!this.chonNhieu) {
      return ketQua;
    }

    for (int dong = 0; dong < this.moHinhBang.getRowCount(); dong++) {
      if (Boolean.TRUE.equals(this.moHinhBang.getValueAt(dong, 0))) {
        ketQua.add(dong);
      }
    }

    return ketQua;
  }

  public void setRowDoubleClickListener(Consumer<Integer> onDoubleClick) {
    this.khiNhapDup = onDoubleClick;
  }

  private void capNhatNutBatch() {
    this.tblDuLieu.getTableHeader().repaint();

    if (this.nhanBatchKey == null) {
      return;
    }

    int soLuong = this.getSelectedRowIndices().size();
    this.btnBatchAction.setText(I18nReaderUtil.get(this.nhanBatchKey) + " (" + soLuong + ")");
    this.btnBatchAction.setEnabled(soLuong > 0);
  }

  private boolean tatCaDuocChon() {
    int soDong = this.moHinhBang.getRowCount();

    if (!this.chonNhieu || soDong == 0) {
      return false;
    }

    for (int dong = 0; dong < soDong; dong++) {
      if (!Boolean.TRUE.equals(this.moHinhBang.getValueAt(dong, 0))) {
        return false;
      }
    }

    return true;
  }

  private void datTatCaChon(boolean chon) {
    for (int dong = 0; dong < this.moHinhBang.getRowCount(); dong++) {
      this.moHinhBang.setValueAt(chon, dong, 0);
    }
  }

  private void caiDatSuKienBang() {
    this.moHinhBang.addTableModelListener(e -> this.capNhatNutBatch());

    this.tblDuLieu.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (ManagementPanel.this.khiNhapDup == null
                || e.getClickCount() != 2
                || !SwingUtilities.isLeftMouseButton(e)) {
          return;
        }

        int dongView = ManagementPanel.this.tblDuLieu.rowAtPoint(e.getPoint());
        int cotView = ManagementPanel.this.tblDuLieu.columnAtPoint(e.getPoint());

        if (dongView < 0) {
          return;
        }

        if (ManagementPanel.this.chonNhieu && cotView == 0) {
          return;
        }

        if (cotView == ManagementPanel.this.cotThaoTacView) {
          return;
        }

        ManagementPanel.this.khiNhapDup.accept(
                ManagementPanel.this.tblDuLieu.convertRowIndexToModel(dongView));
      }
    });

    this.tblDuLieu.getTableHeader().addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (!ManagementPanel.this.chonNhieu
                || ManagementPanel.this.tblDuLieu.getColumnCount() == 0) {
          return;
        }

        int cotView = ManagementPanel.this.tblDuLieu.getColumnModel().getColumnIndexAtX(e.getX());

        if (cotView == 0) {
          ManagementPanel.this.datTatCaChon(!ManagementPanel.this.tatCaDuocChon());
        }
      }
    });
  }

  // ==================== CỘT CHỌN NHIỀU ====================

  private List<Object[]> chupDuLieu() {
    List<Object[]> ketQua = new ArrayList<>();
    int lech = this.chonNhieu ? 1 : 0;
    int soCot = this.moHinhBang.getColumnCount() - lech - (this.tieuDeThaoTacKey == null ? 0 : 1);

    if (soCot <= 0) {
      return ketQua;
    }

    for (int dong = 0; dong < this.moHinhBang.getRowCount(); dong++) {
      Object[] giaTri = new Object[soCot];

      for (int cot = 0; cot < soCot; cot++) {
        giaTri[cot] = this.moHinhBang.getValueAt(dong, cot + lech);
      }

      ketQua.add(giaTri);
    }

    return ketQua;
  }

  private void dungLaiCot(List<Object[]> duLieuCu) {
    List<String> maCot = new ArrayList<>();

    if (this.chonNhieu) {
      maCot.add("");
    }

    maCot.addAll(Arrays.asList(this.tieuDeDuLieu));

    if (this.tieuDeThaoTacKey != null) {
      maCot.add(I18nReaderUtil.get(this.tieuDeThaoTacKey));
    }

    this.moHinhBang.setColumnIdentifiers(maCot.toArray(new String[0]));

    if (this.chonNhieu) {
      this.cauHinhCotChon();
    }

    if (this.tieuDeThaoTacKey != null) {
      this.ganCotThaoTac();
    }

    this.setRows(duLieuCu);
  }

  private void cauHinhCotChon() {
    if (this.tblDuLieu.getColumnCount() == 0) {
      return;
    }

    TableColumn cotChon = this.tblDuLieu.getColumnModel().getColumn(0);
    cotChon.setMinWidth(RONG_COT_CHON);
    cotChon.setMaxWidth(RONG_COT_CHON);
    cotChon.setPreferredWidth(RONG_COT_CHON);
    cotChon.setHeaderRenderer(new TieuDeChonTatCa());
  }

  private final class TieuDeChonTatCa extends JCheckBox implements TableCellRenderer {

    private static final long serialVersionUID = 1L;

    private TieuDeChonTatCa() {
      this.setHorizontalAlignment(SwingConstants.CENTER);
      this.setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column) {
      this.setBackground(Theme.palette().TABLE_HEADER_BG());
      this.setSelected(ManagementPanel.this.tatCaDuocChon());
      this.setEnabled(ManagementPanel.this.moHinhBang.getRowCount() > 0);
      return this;
    }
  }

  private JLabel buildEmptyLabel() {
    JLabel label = new JLabel(I18nReaderUtil.get("app.common.khongCoDuLieu"), SwingConstants.CENTER);
    label.setForeground(Theme.palette().INK_MUTED());
    return label;
  }

  private void hienTrangThaiDanhSach() {
    if (this.moHinhBang.getRowCount() == 0) {
      this.theDanhSach.show(this.pnlDanhSach, THE_TRONG);
    } else {
      this.theDanhSach.show(this.pnlDanhSach, THE_BANG);
    }
  }

  private JPanel buildFilterPanel() {
    this.pnlBoLoc.setOpaque(false);

    JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
    actions.setOpaque(false);

    this.txtOTimNhanh.setPreferredSize(new Dimension(240,
            this.txtOTimNhanh.getPreferredSize().height));
    actions.add(this.txtOTimNhanh);

    this.lblNhanDem.setForeground(Theme.palette().INK_SECONDARY());
    actions.add(this.lblNhanDem);
    actions.add(this.btnXoaLoc);

    JPanel panel = new JPanel(new BorderLayout(4, 4));
    panel.setOpaque(false);
    panel.add(this.pnlBoLoc, BorderLayout.CENTER);
    panel.add(actions, BorderLayout.SOUTH);
    return panel;
  }

  private JPanel buildPaginationPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 6));
    panel.setOpaque(true);
    panel.setBackground(Theme.palette().PANEL_ALT());
    this.lblNhanTrang.setForeground(Theme.INK);
    panel.add(this.btnTruoc);
    panel.add(this.lblNhanTrang);
    panel.add(this.btnSau);
    return panel;
  }

  public JTextField addTextFilter(String labelKey) {
    JTextField field = new JTextField(14);
    this.boLocText.add(field);
    this.attachTextListener(field);
    this.pnlBoLoc.add(this.buildFilterItem(labelKey, field));
    this.pnlBoLoc.revalidate();
    return field;
  }

  public BoLocChon addComboBoxFilter(String labelKey, Object[] values) {
    JComboBox<Object> combo = new JComboBox<>();
    combo.addItem(null); // Tất cả
    for (Object value : values) {
      combo.addItem(value);
    }
    combo.setRenderer(BoHienThiI18n.tao());
    this.boLocCombo.add(combo);
    combo.addActionListener(e -> this.fireFilterChange());
    this.pnlBoLoc.add(this.buildFilterItem(labelKey, combo));
    this.pnlBoLoc.revalidate();
    return new BoLocChon(combo);
  }

  /**
   * Bộ lọc dùng combo tìm kiếm có gợi ý: gõ tới đâu lọc tới đó, chọn một gợi ý
   * thì ô nhập nhận đúng khóa duy nhất của bản ghi (mã phiếu, số điện thoại...).
   */
  public void addComboSearchFilter(String labelKey, ComboTimKiem<?> combo) {
    JTextField oNhap = (JTextField) combo.getEditor().getEditorComponent();
    this.boLocText.add(oNhap);
    this.attachTextListener(oNhap);
    this.pnlBoLoc.add(this.buildFilterItem(labelKey, combo));
    this.pnlBoLoc.revalidate();
  }

  public BoLocNgay addDateRangeFilter(String labelKey) {
    DatePicker from = BoChonNgayGio.taoBoChonNgay();
    DatePicker to = BoChonNgayGio.taoBoChonNgay();
    this.boLocNgay.add(new DatePicker[] { from, to });
    this.attachDateListener(from);
    this.attachDateListener(to);

    this.pnlBoLoc.add(this.buildRangeItem(labelKey, from, to));
    this.pnlBoLoc.revalidate();
    return new BoLocNgay(from, to);
  }

  public BoLocKhoang addRangeFilter(String labelKey) {
    JTextField fromField = new JTextField(14);
    JTextField toField = new JTextField(14);

    LocSoDocumentFilter locSo = new LocSoDocumentFilter();
    ((AbstractDocument) fromField.getDocument()).setDocumentFilter(locSo);
    ((AbstractDocument) toField.getDocument()).setDocumentFilter(locSo);

    this.boLocText.add(fromField);
    this.boLocText.add(toField);

    this.attachTextListener(fromField);
    this.attachTextListener(toField);

    this.pnlBoLoc.add(this.buildRangeItem(labelKey, fromField, toField));
    this.pnlBoLoc.revalidate();
    return new BoLocKhoang(fromField, toField);
  }

  private JPanel buildRangeItem(String labelKey, JComponent from, JComponent to) {
    JPanel range = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
    range.setOpaque(false);
    range.add(from);

    JLabel separator = new JLabel("-");
    separator.setForeground(Theme.palette().INK_MUTED());
    range.add(separator);

    range.add(to);
    return this.buildFilterItem(labelKey, range);
  }

  private JPanel buildFilterItem(String labelKey, JComponent field) {
    JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
    item.setOpaque(false);
    item.add(this.buildFieldLabel(labelKey));
    item.add(field);
    return item;
  }

  private JLabel buildFieldLabel(String labelKey) {
    JLabel label = new JLabel(I18nReaderUtil.get(labelKey), SwingConstants.RIGHT);
    label.setForeground(Theme.INK);
    this.nhanBoLoc.add(label);
    this.updateFieldLabelWidth();
    return label;
  }

  private void updateFieldLabelWidth() {
    int width = 0;
    int height = 0;

    for (JLabel label : this.nhanBoLoc) {
      width = Math.max(width, label.getFontMetrics(label.getFont()).stringWidth(label.getText()));
      height = Math.max(height, label.getPreferredSize().height);
    }

    for (JLabel label : this.nhanBoLoc) {
      label.setPreferredSize(new Dimension(width, height));
    }
  }

  public String getQuickFilter() {
    return this.txtOTimNhanh.getText();
  }

  public JButton getClearFilterButton() {
    return this.btnXoaLoc;
  }

  public JButton getPrevButton() {
    return this.btnTruoc;
  }

  public JButton getNextButton() {
    return this.btnSau;
  }

  public void setFilterChangeListener(Runnable listener) {
    this.khiDoiBoLoc = listener;
  }

  public void clearFilters() {
    this.henGioLoc.stop();
    this.dangXoaLoc = true;

    try {
      this.boLocText.forEach(field -> field.setText(""));

      for (DatePicker[] pickers : this.boLocNgay) {
        pickers[0].clear();
        pickers[1].clear();
      }

      this.txtOTimNhanh.setText("");
      this.boLocCombo.forEach(combo -> combo.setSelectedIndex(0));
    } finally {
      this.dangXoaLoc = false;
    }

    this.fireFilterChange();
  }

  public void setCount(long from, long to, long total) {
    this.lblNhanDem.setText(I18nReaderUtil.get("app.common.count") + " " + from + "-" + to + " / " + total);
  }

  public void setPage(int page, int totalPages) {
    this.lblNhanTrang.setText(I18nReaderUtil.get("app.common.pageLabel") + " " + page + "/" + totalPages);
    this.btnTruoc.setEnabled(page > 1);
    this.btnSau.setEnabled(page < totalPages);
  }

  public JTable getTable() {
    return this.tblDuLieu;
  }

  public DefaultTableModel getModel() {
    return this.moHinhBang;
  }

  public void setListTitle(String titleKey) {
    this.setBorder(Theme.titledBorder(titleKey));
  }

  public void setHeaders(String... headers) {
    List<Object[]> duLieuCu = this.chupDuLieu();
    this.tieuDeDuLieu = headers == null ? new String[0] : headers.clone();
    this.dungLaiCot(duLieuCu);
    this.hienTrangThaiDanhSach();
  }

  public void addActionColumn(String headerKey, ThaoTac... dsThaoTac) {
    if (dsThaoTac == null || dsThaoTac.length == 0) {
      return;
    }

    List<Object[]> duLieuCu = this.chupDuLieu();
    this.tieuDeThaoTacKey = headerKey;
    this.dsThaoTacHienTai = List.of(dsThaoTac);
    this.dungLaiCot(duLieuCu);
  }

  private void ganCotThaoTac() {
    int cotView = this.tblDuLieu.getColumnCount() - 1;

    if (cotView < 0 || this.dsThaoTacHienTai == null) {
      return;
    }

    this.cotThaoTacView = cotView;
    ThaoTacTableCellRenderer.ganVao(this.tblDuLieu, cotView, this.dsThaoTacHienTai);
  }

  public void setRows(List<Object[]> rows) {
    this.moHinhBang.setRowCount(0);

    if (rows != null) {
      for (Object[] row : rows) {
        if (this.chonNhieu) {
          Object[] dong = new Object[row.length + 1];
          dong[0] = Boolean.FALSE;
          System.arraycopy(row, 0, dong, 1, row.length);
          this.moHinhBang.addRow(dong);
        } else {
          this.moHinhBang.addRow(row);
        }
      }
    }

    this.capNhatNutBatch();
    this.hienTrangThaiDanhSach();
  }

  public int getSelectedRow() {
    return this.tblDuLieu.getSelectedRow();
  }

  /**
   * Khung bộ lọc dùng WrapLayout: số hàng phụ thuộc bề rộng thật, nên khi bề rộng đổi
   * phải báo parent tính lại chiều cao (BorderLayout chỉ lấy preferred size một lần).
   */
  private static final class KhungBoLoc extends JPanel {

    private static final long serialVersionUID = 1L;

    private int rongDaDo = -1;

    private KhungBoLoc() {
      super(new WrapLayout(FlowLayout.LEFT, 8, 6));
      this.addComponentListener(new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
          KhungBoLoc.this.doRongThayDoi();
        }
      });
    }

    private void doRongThayDoi() {
      int rong = this.getWidth();

      if (rong == this.rongDaDo) {
        return;
      }

      this.rongDaDo = rong;
      Container cha = this.getParent();

      if (cha != null) {
        SwingUtilities.invokeLater(cha::revalidate);
      }
    }
  }

  private void attachTextListener(JTextField field) {
    field.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        ManagementPanel.this.henGioLoc.restart();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        ManagementPanel.this.henGioLoc.restart();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        ManagementPanel.this.henGioLoc.restart();
      }
    });
  }

  private void attachDateListener(DatePicker picker) {
    picker.addDateChangeListener(event -> this.henGioLoc.restart());
  }

  @Override
  public void removeNotify() {
    this.henGioLoc.stop();
    super.removeNotify();
  }

  private void fireFilterChange() {
    if (this.dangXoaLoc || this.khiDoiBoLoc == null) {
      return;
    }

    this.khiDoiBoLoc.run();
  }
}
