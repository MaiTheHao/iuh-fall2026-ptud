package iuh.fit.gui.component;

import com.formdev.flatlaf.FlatClientProperties;
import com.github.lgooddatepicker.components.DatePicker;

import iuh.fit.gui.Theme;
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

/**
 * Panel quản lý dạng danh sách: bộ lọc (text / combo / ngày / khoảng) + ô tìm nhanh + bảng
 * (tùy chọn cột checkbox chọn nhiều, cột thao tác) + thanh phân trang.
 */
public class ManagementPanel extends JPanel {

  private static final String THE_HIEN_BANG = "bang";
  private static final String THE_HIEN_TRONG = "trong";

  private static final int CO_ICON_TIM = 16;
  private static final int CO_ICON_NUT = 18;
  private static final int RONG_COT_CHON = 44;
  private static final int CHIEU_CAO_DONG_BANG = 34;
  private static final int RONG_O_TIM_NHANH = 240;
  private static final int DO_TRE_LOC_MS = 300;
  private static final int CHI_SO_COT_CHON = 0;

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

  public record BoLocNgay(DatePicker tu, DatePicker den) {

    public LocalDate tuNgay() {
      return this.tu.getDate();
    }

    public LocalDate denNgay() {
      return this.den.getDate();
    }
  }

  public record BoLocKhoang(JTextField tu, JTextField den) {

    public String tuText() {
      return this.tu.getText();
    }

    public String denText() {
      return this.den.getText();
    }
  }

  private final DefaultTableModel moHinhBang = new DefaultTableModel() {

    @Override
    public boolean isCellEditable(int row, int column) {
      return ManagementPanel.this.cheDoChonNhieu && column == CHI_SO_COT_CHON;
    }

    @Override
    public Class<?> getColumnClass(int column) {
      return ManagementPanel.this.cheDoChonNhieu && column == CHI_SO_COT_CHON
              ? Boolean.class
              : Object.class;
    }
  };

  private final JTable tblDuLieu = new JTable(this.moHinhBang) {

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
      Component thanhPhan = super.prepareRenderer(renderer, row, column);
      boolean canTomauXenKe = !this.isRowSelected(row)
              && !(renderer instanceof ThaoTacTableCellRenderer);
      if (canTomauXenKe) {
        thanhPhan.setBackground(row % 2 == 0
                ? this.getBackground()
                : Theme.palette().TABLE_ROW_ALT());
      }
      return thanhPhan;
    }
  };

  private final CardLayout boCardDanhSach = new CardLayout();
  private final JPanel pnlDanhSach = new JPanel(this.boCardDanhSach);

  private final JPanel pnlBoLoc = new KhungBoLoc();
  private final List<JComboBox<Object>> dsBoLocCombo = new ArrayList<>();
  private final List<JTextField> dsOLocText = new ArrayList<>();
  private final List<DatePicker[]> dsCapLocNgay = new ArrayList<>(); // mỗi phần tử: {từ, đến}
  private final List<JLabel> dsNhanBoLoc = new ArrayList<>();

  private final JTextField txtOTimNhanh = new JTextField(20);
  private final JLabel lblDemKetQua = new JLabel();
  private final JButton btnXoaLoc = new JButton(I18nReaderUtil.get("app.common.clearFilter"));

  private final Timer boHenGioLoc = new Timer(DO_TRE_LOC_MS, e -> this.baoBoLocDaDoi());
  private boolean dangXoaBoLoc;
  private Runnable xuLyKhiDoiBoLoc;

  // ======================================================================
  // Phân trang
  // ======================================================================

  private final JButton btnTruoc = new JButton(I18nReaderUtil.get("app.common.prev"));
  private final JButton btnSau = new JButton(I18nReaderUtil.get("app.common.next"));
  private final JLabel lblSoTrang = new JLabel();

  // ======================================================================
  // Thanh thao tác
  // ======================================================================

  private final JPanel pnlThanhThaoTac = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
  private final JButton btnThem = new JButton();
  private final JButton btnHangLoat = new JButton();

  private Runnable xuLyKhiThem;
  private Consumer<List<Integer>> xuLyHangLoat;
  private String khoaNhanNutHangLoat;

  private Consumer<Integer> xuLyNhapDupDong;

  private boolean cheDoChonNhieu;
  private String[] tieuDeCotDuLieu = new String[0];
  private String khoaTieuDeCotThaoTac;
  private List<ThaoTac> dsThaoTac;
  private int chiSoCotThaoTac = -1;

  // ======================================================================
  // Khởi tạo
  // ======================================================================

  public ManagementPanel() {
    this.boHenGioLoc.setRepeats(false);

    this.setLayout(new BorderLayout(4, 4));
    this.setOpaque(false);

    this.caiDatBang();
    this.caiDatOTimNhanh();
    this.caiDatNutPhanTrangVaXoaLoc();

    this.theoDoiThayDoiOText(this.txtOTimNhanh);
    this.caiDatThanhThaoTac();
    this.caiDatSuKienBang();

    JPanel pnlNoiDung = new JPanel(new BorderLayout(4, 4));
    pnlNoiDung.setOpaque(false);
    pnlNoiDung.add(this.taoPanelBoLoc(), BorderLayout.NORTH);
    pnlNoiDung.add(this.taoPanelBang(), BorderLayout.CENTER);
    pnlNoiDung.add(this.taoPanelPhanTrang(), BorderLayout.SOUTH);

    this.pnlDanhSach.setOpaque(false);
    this.pnlDanhSach.add(new JScrollPane(this.tblDuLieu), THE_HIEN_BANG);
    this.pnlDanhSach.add(this.taoNhanKhongCoDuLieu(), THE_HIEN_TRONG);

    this.add(pnlNoiDung, BorderLayout.CENTER);
  }

  private void caiDatBang() {
    this.tblDuLieu.setFillsViewportHeight(true);
    this.tblDuLieu.setRowHeight(CHIEU_CAO_DONG_BANG);
    Theme.styleTable(this.tblDuLieu);
  }

  private void caiDatOTimNhanh() {
    this.txtOTimNhanh.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,
            I18nReaderUtil.get("app.common.search"));
    this.txtOTimNhanh.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON,
            new LucideIcon("search", CO_ICON_TIM, Theme.palette().INK_MUTED()));
  }

  private void caiDatNutPhanTrangVaXoaLoc() {
    Theme.applySecondary(this.btnXoaLoc);
    Theme.applySecondary(this.btnTruoc);
    Theme.applySecondary(this.btnSau);
  }

  // ======================================================================
  // Thanh thao tác
  // ======================================================================

  private void caiDatThanhThaoTac() {
    this.pnlThanhThaoTac.setOpaque(false);

    this.caiDatNutThanhThaoTac(this.btnThem, "plus");
    this.btnThem.addActionListener(e -> {
      if (this.xuLyKhiThem != null) {
        this.xuLyKhiThem.run();
      }
    });
    Theme.applyPrimary(this.btnThem);

    this.caiDatNutThanhThaoTac(this.btnHangLoat, "trash-2");
    this.btnHangLoat.setEnabled(false);
    this.btnHangLoat.addActionListener(e -> {
      if (this.xuLyHangLoat != null) {
        this.xuLyHangLoat.accept(this.getSelectedRowIndices());
      }
    });

    this.pnlThanhThaoTac.add(this.btnHangLoat);
    this.pnlThanhThaoTac.add(this.btnThem);
  }

  private void caiDatNutThanhThaoTac(JButton nut, String tenIcon) {
    nut.setIcon(new LucideIcon(tenIcon, CO_ICON_NUT, Theme.palette().ON_ACCENT()));
    nut.setIconTextGap(6);
    nut.setFocusPainted(false);
    nut.setVisible(false);
  }

  private void capNhatThanhThaoTac() {
    this.pnlThanhThaoTac.revalidate();
    this.pnlThanhThaoTac.repaint();
  }

  public void setAddAction(String labelKey, Runnable onAdd) {
    this.xuLyKhiThem = onAdd;
    this.btnThem.setText(I18nReaderUtil.get(labelKey));
    this.btnThem.setVisible(true);
    this.capNhatThanhThaoTac();
  }

  public void setBatchAction(String labelKey,
          String iconName,
          ThaoTac.KieuThaoTac kieu,
          Consumer<List<Integer>> onBatch) {
    this.khoaNhanNutHangLoat = labelKey;
    this.xuLyHangLoat = onBatch;

    boolean nguyHiem = kieu == ThaoTac.KieuThaoTac.NGUY_HIEM;
    if (nguyHiem) {
      Theme.applyDanger(this.btnHangLoat);
    } else {
      Theme.applySecondary(this.btnHangLoat);
    }

    this.btnHangLoat.setIcon(new LucideIcon(iconName,
            CO_ICON_NUT,
            nguyHiem ? Theme.palette().ON_ACCENT() : Theme.INK));
    this.btnHangLoat.setVisible(true);

    this.capNhatNutHangLoat();
    this.capNhatThanhThaoTac();
  }

  private void capNhatNutHangLoat() {
    this.tblDuLieu.getTableHeader().repaint();

    if (this.khoaNhanNutHangLoat == null) {
      return;
    }

    int soDongDaChon = this.getSelectedRowIndices().size();
    this.btnHangLoat.setText(I18nReaderUtil.get(this.khoaNhanNutHangLoat) + " (" + soDongDaChon + ")");
    this.btnHangLoat.setEnabled(soDongDaChon > 0);
  }

  // ======================================================================
  // Chọn nhiều dòng
  // ======================================================================

  public void enableMultiSelection(boolean enable) {
    if (this.cheDoChonNhieu == enable) {
      return;
    }

    List<Object[]> duLieuCu = this.laySaoDuLieuHienTai();
    this.cheDoChonNhieu = enable;
    this.dungLaiCacCot(duLieuCu);
  }

  public List<Integer> getSelectedRowIndices() {
    List<Integer> dsDongDaChon = new ArrayList<>();

    if (!this.cheDoChonNhieu) {
      return dsDongDaChon;
    }

    for (int dong = 0; dong < this.moHinhBang.getRowCount(); dong++) {
      if (this.dongDaDuocTick(dong)) {
        dsDongDaChon.add(dong);
      }
    }

    return dsDongDaChon;
  }

  private boolean dongDaDuocTick(int dongModel) {
    return Boolean.TRUE.equals(this.moHinhBang.getValueAt(dongModel, CHI_SO_COT_CHON));
  }

  private boolean tatCaDongDaChon() {
    int soDong = this.moHinhBang.getRowCount();

    if (!this.cheDoChonNhieu || soDong == 0) {
      return false;
    }

    for (int dong = 0; dong < soDong; dong++) {
      if (!this.dongDaDuocTick(dong)) {
        return false;
      }
    }

    return true;
  }

  private void datChonTatCaDong(boolean chon) {
    for (int dong = 0; dong < this.moHinhBang.getRowCount(); dong++) {
      this.moHinhBang.setValueAt(chon, dong, CHI_SO_COT_CHON);
    }
  }

  public void setRowDoubleClickListener(Consumer<Integer> onDoubleClick) {
    this.xuLyNhapDupDong = onDoubleClick;
  }

  // ======================================================================
  // Sự kiện bảng
  // ======================================================================

  private void caiDatSuKienBang() {
    this.moHinhBang.addTableModelListener(e -> this.capNhatNutHangLoat());

    this.tblDuLieu.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        ManagementPanel.this.khiClickVaoBang(e);
      }
    });

    this.tblDuLieu.getTableHeader().addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        ManagementPanel.this.khiClickVaoTieuDeBang(e);
      }
    });
  }

  private void khiClickVaoBang(MouseEvent e) {
    if (this.xuLyNhapDupDong == null
            || e.getClickCount() != 2
            || !SwingUtilities.isLeftMouseButton(e)) {
      return;
    }

    int dongView = this.tblDuLieu.rowAtPoint(e.getPoint());
    int cotView = this.tblDuLieu.columnAtPoint(e.getPoint());

    boolean clickNgoaiDong = dongView < 0;
    boolean clickCotCheckbox = this.cheDoChonNhieu && cotView == CHI_SO_COT_CHON;
    boolean clickCotThaoTac = cotView == this.chiSoCotThaoTac;
    if (clickNgoaiDong || clickCotCheckbox || clickCotThaoTac) {
      return;
    }

    this.xuLyNhapDupDong.accept(this.tblDuLieu.convertRowIndexToModel(dongView));
  }

  private void khiClickVaoTieuDeBang(MouseEvent e) {
    if (!this.cheDoChonNhieu || this.tblDuLieu.getColumnCount() == 0) {
      return;
    }

    int cotView = this.tblDuLieu.getColumnModel().getColumnIndexAtX(e.getX());
    if (cotView == CHI_SO_COT_CHON) {
      this.datChonTatCaDong(!this.tatCaDongDaChon());
    }
  }

  // ======================================================================
  // Dữ liệu và cột của bảng
  // ======================================================================

  private List<Object[]> laySaoDuLieuHienTai() {
    List<Object[]> ketQua = new ArrayList<>();

    int soCotChon = this.cheDoChonNhieu ? 1 : 0;
    int soCotThaoTac = this.khoaTieuDeCotThaoTac == null ? 0 : 1;
    int soCotDuLieu = this.moHinhBang.getColumnCount() - soCotChon - soCotThaoTac;

    if (soCotDuLieu <= 0) {
      return ketQua;
    }

    for (int dong = 0; dong < this.moHinhBang.getRowCount(); dong++) {
      Object[] giaTriDong = new Object[soCotDuLieu];

      for (int cot = 0; cot < soCotDuLieu; cot++) {
        giaTriDong[cot] = this.moHinhBang.getValueAt(dong, cot + soCotChon);
      }

      ketQua.add(giaTriDong);
    }

    return ketQua;
  }

  private void dungLaiCacCot(List<Object[]> duLieuCu) {
    List<String> tieuDeCot = new ArrayList<>();

    if (this.cheDoChonNhieu) {
      tieuDeCot.add("");
    }

    tieuDeCot.addAll(Arrays.asList(this.tieuDeCotDuLieu));

    if (this.khoaTieuDeCotThaoTac != null) {
      tieuDeCot.add(I18nReaderUtil.get(this.khoaTieuDeCotThaoTac));
    }

    this.moHinhBang.setColumnIdentifiers(tieuDeCot.toArray(new String[0]));

    if (this.cheDoChonNhieu) {
      this.caiDatCotCheckbox();
    }

    if (this.khoaTieuDeCotThaoTac != null) {
      this.ganRendererCotThaoTac();
    }

    this.setRows(duLieuCu);
  }

  private void caiDatCotCheckbox() {
    if (this.tblDuLieu.getColumnCount() == 0) {
      return;
    }

    TableColumn cotCheckbox = this.tblDuLieu.getColumnModel().getColumn(CHI_SO_COT_CHON);
    cotCheckbox.setMinWidth(RONG_COT_CHON);
    cotCheckbox.setMaxWidth(RONG_COT_CHON);
    cotCheckbox.setPreferredWidth(RONG_COT_CHON);
    cotCheckbox.setHeaderRenderer(new TieuDeCotChonTatCa());
  }

  private void ganRendererCotThaoTac() {
    int cotCuoi = this.tblDuLieu.getColumnCount() - 1;

    if (cotCuoi < 0 || this.dsThaoTac == null) {
      return;
    }

    this.chiSoCotThaoTac = cotCuoi;
    ThaoTacTableCellRenderer.ganVao(this.tblDuLieu, cotCuoi, this.dsThaoTac);
  }

  public void setHeaders(String... headers) {
    List<Object[]> duLieuCu = this.laySaoDuLieuHienTai();
    this.tieuDeCotDuLieu = headers == null ? new String[0] : headers.clone();
    this.dungLaiCacCot(duLieuCu);
    this.capNhatTheBangHoacTrong();
  }

  public void addActionColumn(String headerKey, ThaoTac... dsThaoTacMoi) {
    if (dsThaoTacMoi == null || dsThaoTacMoi.length == 0) {
      return;
    }

    List<Object[]> duLieuCu = this.laySaoDuLieuHienTai();
    this.khoaTieuDeCotThaoTac = headerKey;
    this.dsThaoTac = List.of(dsThaoTacMoi);
    this.dungLaiCacCot(duLieuCu);
  }

  public void setRows(List<Object[]> rows) {
    this.moHinhBang.setRowCount(0);

    if (rows != null) {
      for (Object[] dong : rows) {
        this.moHinhBang.addRow(this.cheDoChonNhieu ? this.themCotChonVaoDau(dong) : dong);
      }
    }

    this.capNhatNutHangLoat();
    this.capNhatTheBangHoacTrong();
  }

  private Object[] themCotChonVaoDau(Object[] dongDuLieu) {
    Object[] dongCoCotChon = new Object[dongDuLieu.length + 1];
    dongCoCotChon[CHI_SO_COT_CHON] = Boolean.FALSE;
    System.arraycopy(dongDuLieu, 0, dongCoCotChon, 1, dongDuLieu.length);
    return dongCoCotChon;
  }

  private void capNhatTheBangHoacTrong() {
    String the = this.moHinhBang.getRowCount() == 0 ? THE_HIEN_TRONG : THE_HIEN_BANG;
    this.boCardDanhSach.show(this.pnlDanhSach, the);
  }

  private JLabel taoNhanKhongCoDuLieu() {
    JLabel nhan = new JLabel(I18nReaderUtil.get("app.common.khongCoDuLieu"), SwingConstants.CENTER);
    nhan.setForeground(Theme.palette().INK_MUTED());
    return nhan;
  }

  public int getSelectedRow() {
    return this.tblDuLieu.getSelectedRow();
  }

  public JTable getTable() {
    return this.tblDuLieu;
  }

  public DefaultTableModel getModel() {
    return this.moHinhBang;
  }

  // ======================================================================
  // Dựng giao diện
  // ======================================================================

  private JPanel taoPanelBang() {
    JPanel khung = new JPanel(new BorderLayout(0, 4));
    khung.setOpaque(false);
    khung.add(this.pnlThanhThaoTac, BorderLayout.NORTH);
    khung.add(this.pnlDanhSach, BorderLayout.CENTER);
    return khung;
  }

  private JPanel taoPanelBoLoc() {
    this.pnlBoLoc.setOpaque(false);

    this.txtOTimNhanh.setPreferredSize(new Dimension(RONG_O_TIM_NHANH,
            this.txtOTimNhanh.getPreferredSize().height));
    this.lblDemKetQua.setForeground(Theme.palette().INK_SECONDARY());

    JPanel pnlHanhDong = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
    pnlHanhDong.setOpaque(false);
    pnlHanhDong.add(this.txtOTimNhanh);
    pnlHanhDong.add(this.lblDemKetQua);
    pnlHanhDong.add(this.btnXoaLoc);

    JPanel khung = new JPanel(new BorderLayout(4, 4));
    khung.setOpaque(false);
    khung.add(this.pnlBoLoc, BorderLayout.CENTER);
    khung.add(pnlHanhDong, BorderLayout.SOUTH);
    return khung;
  }

  private JPanel taoPanelPhanTrang() {
    JPanel khung = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 6));
    khung.setOpaque(true);
    khung.setBackground(Theme.palette().PANEL_ALT());
    this.lblSoTrang.setForeground(Theme.INK);
    khung.add(this.btnTruoc);
    khung.add(this.lblSoTrang);
    khung.add(this.btnSau);
    return khung;
  }

  // ======================================================================
  // Thêm bộ lọc
  // ======================================================================

  public JTextField addTextFilter(String labelKey) {
    JTextField oNhap = new JTextField(14);
    this.dsOLocText.add(oNhap);
    this.theoDoiThayDoiOText(oNhap);
    this.themMucLocVaoKhung(this.taoMucLoc(labelKey, oNhap));
    return oNhap;
  }

  public BoLocChon addComboBoxFilter(String labelKey, Object[] values) {
    JComboBox<Object> combo = new JComboBox<>();
    combo.addItem(null); // mục đầu tiên = "không lọc"
    for (Object value : values) {
      combo.addItem(value);
    }
    combo.setRenderer(BoHienThiI18n.tao());
    this.dsBoLocCombo.add(combo);
    combo.addActionListener(e -> this.baoBoLocDaDoi());
    this.themMucLocVaoKhung(this.taoMucLoc(labelKey, combo));
    return new BoLocChon(combo);
  }

  public void addComboSearchFilter(String labelKey, ComboTimKiem<?> combo) {
    JTextField oNhap = (JTextField) combo.getEditor().getEditorComponent();
    this.dsOLocText.add(oNhap);
    this.theoDoiThayDoiOText(oNhap);
    this.themMucLocVaoKhung(this.taoMucLoc(labelKey, combo));
  }

  public BoLocNgay addDateRangeFilter(String labelKey) {
    DatePicker tuNgay = BoChonNgayGio.taoBoChonNgay();
    DatePicker denNgay = BoChonNgayGio.taoBoChonNgay();
    this.dsCapLocNgay.add(new DatePicker[] { tuNgay, denNgay });
    this.theoDoiThayDoiNgay(tuNgay);
    this.theoDoiThayDoiNgay(denNgay);

    this.themMucLocVaoKhung(this.taoMucLocKhoang(labelKey, tuNgay, denNgay));
    return new BoLocNgay(tuNgay, denNgay);
  }

  public BoLocKhoang addRangeFilter(String labelKey) {
    JTextField oTu = new JTextField(14);
    JTextField oDen = new JTextField(14);

    LocSoDocumentFilter boLocChiNhapSo = new LocSoDocumentFilter();
    ((AbstractDocument) oTu.getDocument()).setDocumentFilter(boLocChiNhapSo);
    ((AbstractDocument) oDen.getDocument()).setDocumentFilter(boLocChiNhapSo);

    this.dsOLocText.add(oTu);
    this.dsOLocText.add(oDen);
    this.theoDoiThayDoiOText(oTu);
    this.theoDoiThayDoiOText(oDen);

    this.themMucLocVaoKhung(this.taoMucLocKhoang(labelKey, oTu, oDen));
    return new BoLocKhoang(oTu, oDen);
  }

  private void themMucLocVaoKhung(JPanel mucLoc) {
    this.pnlBoLoc.add(mucLoc);
    this.pnlBoLoc.revalidate();
  }

  /** Một mục lọc dạng "Nhãn: [từ] - [đến]". */
  private JPanel taoMucLocKhoang(String labelKey, JComponent tu, JComponent den) {
    JLabel nhanGach = new JLabel("-");
    nhanGach.setForeground(Theme.palette().INK_MUTED());

    JPanel khungKhoang = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
    khungKhoang.setOpaque(false);
    khungKhoang.add(tu);
    khungKhoang.add(nhanGach);
    khungKhoang.add(den);
    return this.taoMucLoc(labelKey, khungKhoang);
  }

  /** Một mục lọc dạng "Nhãn: [thành phần nhập]". */
  private JPanel taoMucLoc(String labelKey, JComponent thanhPhanNhap) {
    JPanel muc = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
    muc.setOpaque(false);
    muc.add(this.taoNhanTruongLoc(labelKey));
    muc.add(thanhPhanNhap);
    return muc;
  }

  private JLabel taoNhanTruongLoc(String labelKey) {
    JLabel nhan = new JLabel(I18nReaderUtil.get(labelKey), SwingConstants.RIGHT);
    nhan.setForeground(Theme.INK);
    this.dsNhanBoLoc.add(nhan);
    this.dongBoDoRongNhanLoc();
    return nhan;
  }

  /** Cho mọi nhãn bộ lọc cùng kích thước **/
  private void dongBoDoRongNhanLoc() {
    int rongToiDa = 0;
    int caoToiDa = 0;

    for (JLabel nhan : this.dsNhanBoLoc) {
      int rongChu = nhan.getFontMetrics(nhan.getFont()).stringWidth(nhan.getText());
      rongToiDa = Math.max(rongToiDa, rongChu);
      caoToiDa = Math.max(caoToiDa, nhan.getPreferredSize().height);
    }

    for (JLabel nhan : this.dsNhanBoLoc) {
      nhan.setPreferredSize(new Dimension(rongToiDa, caoToiDa));
    }
  }

  // ======================================================================
  // Trạng thái bộ lọc và phân trang
  // ======================================================================

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
    this.xuLyKhiDoiBoLoc = listener;
  }

  public void clearFilters() {
    this.boHenGioLoc.stop();
    this.dangXoaBoLoc = true;

    try {
      this.dsOLocText.forEach(oNhap -> oNhap.setText(""));

      for (DatePicker[] capNgay : this.dsCapLocNgay) {
        capNgay[0].clear();
        capNgay[1].clear();
      }

      this.txtOTimNhanh.setText("");
      this.dsBoLocCombo.forEach(combo -> combo.setSelectedIndex(0));
    } finally {
      this.dangXoaBoLoc = false;
    }

    this.baoBoLocDaDoi();
  }

  public void setCount(long from, long to, long total) {
    this.lblDemKetQua.setText(
            I18nReaderUtil.get("app.common.count") + " " + from + "-" + to + " / " + total);
  }

  public void setPage(int page, int totalPages) {
    this.lblSoTrang.setText(I18nReaderUtil.get("app.common.pageLabel") + " " + page + "/" + totalPages);
    this.btnTruoc.setEnabled(page > 1);
    this.btnSau.setEnabled(page < totalPages);
  }

  public void setListTitle(String titleKey) {
    this.setBorder(Theme.titledBorder(titleKey));
  }

  // ======================================================================
  // Theo dõi thay đổi bộ lọc
  // ======================================================================

  private void theoDoiThayDoiOText(JTextField oNhap) {
    oNhap.getDocument().addDocumentListener(new LangNgheNoiDung(this.boHenGioLoc::restart));
  }

  private void theoDoiThayDoiNgay(DatePicker boChonNgay) {
    boChonNgay.addDateChangeListener(event -> this.boHenGioLoc.restart());
  }

  private void baoBoLocDaDoi() {
    if (this.dangXoaBoLoc || this.xuLyKhiDoiBoLoc == null) {
      return;
    }

    this.xuLyKhiDoiBoLoc.run();
  }

  @Override
  public void removeNotify() {
    this.boHenGioLoc.stop();
    super.removeNotify();
  }

  private final class TieuDeCotChonTatCa extends JCheckBox implements TableCellRenderer {

    private TieuDeCotChonTatCa() {
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
      this.setSelected(ManagementPanel.this.tatCaDongDaChon());
      this.setEnabled(ManagementPanel.this.moHinhBang.getRowCount() > 0);
      return this;
    }
  }

  private static final class LangNgheNoiDung implements DocumentListener {

    private final Runnable hanhDong;

    private LangNgheNoiDung(Runnable hanhDong) {
      this.hanhDong = hanhDong;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
      this.hanhDong.run();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
      this.hanhDong.run();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
      this.hanhDong.run();
    }
  }

  private static final class KhungBoLoc extends JPanel {

    private int rongLanTruoc = -1;

    private KhungBoLoc() {
      super(new WrapLayout(FlowLayout.LEFT, 8, 6));
      this.addComponentListener(new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
          KhungBoLoc.this.khiDoiChieuRong();
        }
      });
    }

    private void khiDoiChieuRong() {
      int rong = this.getWidth();

      if (rong == this.rongLanTruoc) {
        return;
      }

      this.rongLanTruoc = rong;
      Container cha = this.getParent();

      if (cha != null) {
        SwingUtilities.invokeLater(cha::revalidate);
      }
    }
  }
}
