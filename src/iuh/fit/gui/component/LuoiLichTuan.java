package iuh.fit.gui.component;

import iuh.fit.enumeration.CaLamViec;
import iuh.fit.gui.theme.Theme;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Lưới lịch tuần 7 ngày × 3 ca, mỗi ô hiển thị nhân viên đã phân công kèm nút thêm.
 */
public final class LuoiLichTuan extends JPanel {

  private static final long serialVersionUID = 1L;

  private static final int SO_NGAY = 7;
  private static final int CHIEU_CAO_THE = 28;
  private static final int KICH_THUOC_NUT = 28;
  private static final String TEN_THE = "thePhanCong";

  private static final DateTimeFormatter DINH_DANG_NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  private static final CaLamViec[] CAC_CA = {
      CaLamViec.CA_SANG,
      CaLamViec.CA_CHIEU,
      CaLamViec.CA_NGAY
  };

  private static final String[] KHOA_CA = {
      "app.schedule.morning",
      "app.schedule.afternoon",
      "app.schedule.allday"
  };

  private static final String[] KHOA_THU = {
      "app.schedule.day.monday",
      "app.schedule.day.tuesday",
      "app.schedule.day.wednesday",
      "app.schedule.day.thursday",
      "app.schedule.day.friday",
      "app.schedule.day.saturday",
      "app.schedule.day.sunday"
  };

  private LocalDate ngayDauTuan;
  private Map<LocalDate, Map<CaLamViec, List<MucPhanCong>>> duLieu = Map.of();

  private BiConsumer<LocalDate, CaLamViec> suKienThem;
  private Consumer<MucPhanCong> suKienSua;
  private Consumer<MucPhanCong> suKienXoa;

  public LuoiLichTuan() {
    super(new GridLayout(4, 8, 0, 0));

    this.setBackground(Theme.palette().BORDER());
    this.ngayDauTuan = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    this.dungLaiLuoi();
  }

  public void datTuan(LocalDate ngayThuHai) {
    this.ngayDauTuan = this.chuanHoaTuan(ngayThuHai);
    this.dungLaiLuoi();
  }

  public LocalDate layNgayDauTuan() {
    return this.ngayDauTuan;
  }

  public void datDuLieu(Map<LocalDate, Map<CaLamViec, List<MucPhanCong>>> duLieu) {
    this.duLieu = duLieu == null ? Map.of() : duLieu;
    this.dungLaiLuoi();
  }

  public void datXuLyThem(BiConsumer<LocalDate, CaLamViec> xuLy) {
    this.suKienThem = xuLy;
  }

  public void datXuLySua(Consumer<MucPhanCong> xuLy) {
    this.suKienSua = xuLy;
  }

  public void datXuLyXoa(Consumer<MucPhanCong> xuLy) {
    this.suKienXoa = xuLy;
  }

  private LocalDate chuanHoaTuan(LocalDate ngay) {
    LocalDate goc = ngay == null ? LocalDate.now() : ngay;

    return goc.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
  }

  private void dungLaiLuoi() {
    this.removeAll();

    this.add(this.taoOHeader(""));

    for (int i = 0; i < SO_NGAY; i++) {
      LocalDate ngay = this.ngayDauTuan.plusDays(i);
      this.add(this.taoOHeader(I18nReaderUtil.get(KHOA_THU[i]) + "\n" + ngay.format(DINH_DANG_NGAY)));
    }

    for (int ca = 0; ca < CAC_CA.length; ca++) {
      this.add(this.taoOHeader(I18nReaderUtil.get(KHOA_CA[ca])));

      for (int i = 0; i < SO_NGAY; i++) {
        this.add(this.taoOCa(this.ngayDauTuan.plusDays(i), CAC_CA[ca]));
      }
    }

    this.revalidate();
    this.repaint();
  }

  private JPanel taoOHeader(String noiDung) {
    JPanel o = Theme.contentPanel(6);
    o.setBackground(Theme.palette().PANEL_ALT());

    JLabel nhan = new JLabel("<html>" + noiDung.replace("\n", "<br>") + "</html>");
    nhan.setFont(Theme.fieldLabelFont());
    nhan.setForeground(Theme.palette().INK());
    o.add(nhan, BorderLayout.CENTER);

    return o;
  }

  private JPanel taoOCa(LocalDate ngay, CaLamViec ca) {
    JPanel o = Theme.contentPanel(6);

    JPanel cotNhanVien = new JPanel();
    cotNhanVien.setLayout(new BoxLayout(cotNhanVien, BoxLayout.Y_AXIS));
    cotNhanVien.setOpaque(false);

    List<MucPhanCong> danhSach = this.layDanhSach(ngay, ca);

    if (danhSach.isEmpty()) {
      JLabel lblTrong = new JLabel("–", SwingConstants.CENTER);
      lblTrong.setForeground(Theme.palette().INK_MUTED());
      lblTrong.setAlignmentX(Component.LEFT_ALIGNMENT);
      lblTrong.setMaximumSize(new Dimension(Integer.MAX_VALUE, CHIEU_CAO_THE));
      cotNhanVien.add(lblTrong);
    } else {
      for (MucPhanCong muc : danhSach) {
        cotNhanVien.add(this.taoThe(muc));
        cotNhanVien.add(Box.createVerticalStrut(4));
      }
    }

    JScrollPane cuon = new JScrollPane(cotNhanVien);
    cuon.setBorder(null);
    cuon.setOpaque(false);
    cuon.getViewport().setOpaque(false);
    cuon.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    cuon.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    cuon.getVerticalScrollBar().setUnitIncrement(16);

    JButton nutThem = new JButton("+");
    nutThem.setToolTipText(I18nReaderUtil.get("app.schedule.addEmployeeToCell"));
    nutThem.setFont(Theme.fieldLabelFont());
    Theme.applySecondary(nutThem);
    nutThem.setPreferredSize(new Dimension(KICH_THUOC_NUT, KICH_THUOC_NUT));
    nutThem.addActionListener(e -> this.kichHoatThem(ngay, ca));

    JPanel hangThem = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
    hangThem.setOpaque(false);
    hangThem.add(nutThem);

    o.add(cuon, BorderLayout.CENTER);
    o.add(hangThem, BorderLayout.SOUTH);

    this.ganNhayDupThem(o, ngay, ca);

    return o;
  }

  private List<MucPhanCong> layDanhSach(LocalDate ngay, CaLamViec ca) {
    Map<CaLamViec, List<MucPhanCong>> theoCa = this.duLieu.get(ngay);

    if (theoCa == null) {
      return List.of();
    }

    return theoCa.getOrDefault(ca, List.of()).stream()
        .sorted(Comparator.comparing(MucPhanCong::tenNhanVien, String.CASE_INSENSITIVE_ORDER))
        .toList();
  }

  private JPanel taoThe(MucPhanCong muc) {
    JPanel the = new JPanel(new BorderLayout(4, 0));
    the.setName(TEN_THE);
    the.setOpaque(false);
    the.setAlignmentX(Component.LEFT_ALIGNMENT);
    the.setMaximumSize(new Dimension(Integer.MAX_VALUE, CHIEU_CAO_THE));

    JButton nutTen = new JButton(muc.tenNhanVien());
    nutTen.setHorizontalAlignment(SwingConstants.LEFT);
    nutTen.setToolTipText(I18nReaderUtil.get("app.common.edit"));
    Theme.applySecondary(nutTen);
    nutTen.addActionListener(e -> {
      if (this.suKienSua != null) {
        this.suKienSua.accept(muc);
      }
    });

    JButton nutXoa = new JButton(new LucideIcon("trash-2", 14, Theme.palette().DANGER()));
    nutXoa.setToolTipText(I18nReaderUtil.get("app.schedule.removeAssignment"));
    nutXoa.setBorderPainted(false);
    Theme.applySecondary(nutXoa);
    nutXoa.setPreferredSize(new Dimension(KICH_THUOC_NUT, KICH_THUOC_NUT));
    nutXoa.addActionListener(e -> {
      if (this.suKienXoa != null) {
        this.suKienXoa.accept(muc);
      }
    });

    the.add(nutTen, BorderLayout.CENTER);
    the.add(nutXoa, BorderLayout.EAST);

    return the;
  }

  private void ganNhayDupThem(Component thanhPhan, LocalDate ngay, CaLamViec ca) {
    if (thanhPhan instanceof JButton || thanhPhan instanceof JScrollBar
        || TEN_THE.equals(thanhPhan.getName())) {
      return;
    }

    thanhPhan.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
          LuoiLichTuan.this.kichHoatThem(ngay, ca);
        }
      }
    });

    if (thanhPhan instanceof Container container) {
      for (Component con : container.getComponents()) {
        this.ganNhayDupThem(con, ngay, ca);
      }
    }
  }

  private void kichHoatThem(LocalDate ngay, CaLamViec ca) {
    if (this.suKienThem != null) {
      this.suKienThem.accept(ngay, ca);
    }
  }

  /** Một lượt phân công hiển thị trong ô lịch. */
  public record MucPhanCong(String maPhanCong, String maNhanVien, String tenNhanVien) {

    public MucPhanCong {
      maPhanCong = maPhanCong == null ? "" : maPhanCong;
      maNhanVien = maNhanVien == null ? "" : maNhanVien;
      tenNhanVien = tenNhanVien == null || tenNhanVien.isBlank() ? maNhanVien : tenNhanVien;
    }
  }
}
