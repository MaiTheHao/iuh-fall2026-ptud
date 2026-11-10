package iuh.fit.gui.component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicComboBoxEditor;

import java.awt.Component;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ComboBox có ô nhập cho phép gõ từ khóa để tìm kiếm.
 */
public final class ComboTimKiem<T> extends JComboBox<T> {

  private static final Logger LOGGER = Logger.getLogger(ComboTimKiem.class.getName());
  private static final int DO_TRE_SAU_KHI_GO_MS = 300;
  private static final int SO_GOI_Y_HIEN_TOI_DA = 10;

  /** Nguồn dữ liệu: nhận từ khóa, trả về danh sách gợi ý. */
  @FunctionalInterface
  public interface NguonTim<T> {
    List<T> tim(String tuKhoa) throws SQLException;
  }

  // Cấu hình bất biến.
  private final NguonTim<T> nguonTimKiem;
  private final Function<T, String> hamLayNhanHienThi;
  private final Function<T, String> hamLayChuoiNhap;
  private final JTextField txtONhapLieu;
  private final Timer boHenGioTim = new Timer(DO_TRE_SAU_KHI_GO_MS, e -> this.batDauTimGoiY(false));

  // Trạng thái tìm kiếm.
  private SwingWorker<List<T>, Void> tacVuTimDangChay;
  private int soThuTuLanTim;

  // Cờ chống đệ quy sự kiện.
  private boolean dangCapNhatModel;
  private boolean dangDatTextBangCode;

  // Callback do bên ngoài đặt.
  private Consumer<T> xuLyKhiChon;
  private Runnable xuLyKhiGo;

  public ComboTimKiem(NguonTim<T> nguon,
          Function<T, String> nhanHienThi,
          Function<T, String> giaTriNhap) {
    this.nguonTimKiem = Objects.requireNonNull(nguon, "nguon");
    this.hamLayNhanHienThi = Objects.requireNonNull(nhanHienThi, "nhanHienThi");
    this.hamLayChuoiNhap = Objects.requireNonNull(giaTriNhap, "giaTriNhap");

    this.setEditable(true);
    this.setMaximumRowCount(SO_GOI_Y_HIEN_TOI_DA);
    this.boHenGioTim.setRepeats(false);

    this.setEditor(new EditorHienChuoiNhap());
    this.setRenderer(new RendererHienNhan());

    this.txtONhapLieu = (JTextField) this.getEditor().getEditorComponent();
    this.txtONhapLieu.getDocument().addDocumentListener(new LangNgheNoiDungOChu());

    this.addActionListener(e -> this.khiNguoiDungChonGoiY());
  }

  public String getTuKhoa() {
    return this.txtONhapLieu.getText();
  }

  /** Đặt từ khóa bằng code: hủy tìm kiếm đang chạy và không kích hoạt tìm mới. */
  public void datTuKhoa(String tuKhoa) {
    this.huyTimKiemDangChay();
    this.boHenGioTim.stop();
    this.datTextKhongKichHoatTim(tuKhoa);
  }

  public void datChoPhepNhap(boolean choPhep) {
    this.setEnabled(choPhep);
    this.txtONhapLieu.setEnabled(choPhep);
    this.txtONhapLieu.setEditable(choPhep);
  }

  public void xoa() {
    this.datTuKhoa("");
    this.xoaToanBoGoiY();
  }

  public void datXuLyChon(Consumer<T> xuLy) {
    this.xuLyKhiChon = xuLy;
  }

  public void datXuLyGo(Runnable xuLy) {
    this.xuLyKhiGo = xuLy;
  }

  /** Tải lại gợi ý ngay (bỏ chờ), cho phép cả khi từ khóa rỗng. */
  public void napLaiGoiY() {
    this.boHenGioTim.stop();
    this.batDauTimGoiY(true);
  }

  // Sự kiện từ người dùng.
  private void khiNguoiDungGoPhim() {
    if (this.dangDatTextBangCode || this.dangCapNhatModel) {
      return;
    }
    this.huyTimKiemDangChay();
    this.boHenGioTim.restart();
    if (this.xuLyKhiGo != null) {
      this.xuLyKhiGo.run();
    }
  }

  private void khiNguoiDungChonGoiY() {
    if (this.dangCapNhatModel) {
      return;
    }
    Object mucDangChon = this.getSelectedItem();
    if (mucDangChon == null) {
      return;
    }
    T mucGoiY = this.timMucTrongModel(mucDangChon);
    if (mucGoiY == null) {
      return;
    }

    this.chayKhiCapNhatModel(() -> {
      this.datTuKhoa(this.hamLayChuoiNhap.apply(mucGoiY));
      this.setSelectedItem(null);
      this.setPopupVisible(false);
      if (this.xuLyKhiChon != null) {
        this.xuLyKhiChon.accept(mucGoiY);
      }
    });
  }

  private T timMucTrongModel(Object mucCanTim) {
    for (int i = 0; i < this.getItemCount(); i++) {
      T muc = this.getItemAt(i);
      if (muc == mucCanTim || muc.equals(mucCanTim)) {
        return muc;
      }
    }
    return null;
  }

  // Tìm kiếm ở luồng nền.
  private void batDauTimGoiY(boolean choPhepTuKhoaRong) {
    this.huyTimKiemDangChay();

    String tuKhoa = this.getTuKhoa();
    if (tuKhoa.isBlank() && !choPhepTuKhoaRong) {
      this.xoaToanBoGoiY();
      return;
    }

    this.tacVuTimDangChay = new TacVuTimKiem(tuKhoa, this.soThuTuLanTim);
    this.tacVuTimDangChay.execute();
  }

  /** Hủy tác vụ đang chạy và tăng số thứ tự để kết quả cũ trả về muộn bị bỏ qua. */
  private void huyTimKiemDangChay() {
    if (this.tacVuTimDangChay != null) {
      this.tacVuTimDangChay.cancel(true);
      this.tacVuTimDangChay = null;
    }
    this.soThuTuLanTim++;
  }

  private void ghiLogLoiTim(String tuKhoa, Throwable loi) {
    LOGGER.log(Level.WARNING, "Không tải được gợi ý tìm kiếm cho từ khóa: " + tuKhoa, loi);
  }

  // Cập nhật model và ô nhập.
  private void hienThiGoiY(List<T> danhSachGoiY) {
    String textNguoiDungDangGo = this.getTuKhoa();

    this.chayKhiCapNhatModel(() -> {
      this.removeAllItems();
      if (danhSachGoiY != null) {
        danhSachGoiY.stream().filter(Objects::nonNull).forEach(this::addItem);
      }
      this.setSelectedItem(null);
      this.datTextKhongKichHoatTim(textNguoiDungDangGo);
    });

    // Chỉ mở popup khi thực sự có gợi ý và ô nhập vẫn đang được thao tác.
    boolean nenHienPopup = this.getItemCount() > 0
            && this.txtONhapLieu.hasFocus()
            && this.isShowing();
    this.setPopupVisible(nenHienPopup);
  }

  private void xoaToanBoGoiY() {
    this.chayKhiCapNhatModel(() -> {
      this.removeAllItems();
      this.setSelectedItem(null);
    });
    this.setPopupVisible(false);
  }

  // Helper điều khiển cờ trạng thái.
  private void chayKhiCapNhatModel(Runnable viec) {
    boolean giaTriCu = this.dangCapNhatModel;
    this.dangCapNhatModel = true;
    try {
      viec.run();
    } finally {
      this.dangCapNhatModel = giaTriCu;
    }
  }

  private void datTextKhongKichHoatTim(String text) {
    boolean giaTriCu = this.dangDatTextBangCode;
    this.dangDatTextBangCode = true;
    try {
      this.txtONhapLieu.setText(text == null ? "" : text);
    } finally {
      this.dangDatTextBangCode = giaTriCu;
    }
  }

  @SuppressWarnings("unchecked")
  private static <T> T epVeKieuT(Object doiTuong) {
    return (T) doiTuong;
  }

  private String layNhanHienThi(Object muc) {
    if (muc == null) {
      return "";
    }
    try {
      String nhan = this.hamLayNhanHienThi.apply(epVeKieuT(muc));
      return nhan == null ? "" : nhan;
    } catch (RuntimeException ex) {
      return muc.toString();
    }
  }

  private String layChuoiNhap(Object muc) {
    try {
      return this.hamLayChuoiNhap.apply(epVeKieuT(muc));
    } catch (RuntimeException ex) {
      return String.valueOf(muc);
    }
  }

  // Lớp nội bộ.
  private final class EditorHienChuoiNhap extends BasicComboBoxEditor {
    @Override
    public void setItem(Object muc) {
      if (muc == null || ComboTimKiem.this.dangCapNhatModel) {
        return;
      }
      if (muc instanceof String chuoi) {
        super.setItem(chuoi);
      } else {
        super.setItem(ComboTimKiem.this.layChuoiNhap(muc));
      }
    }
  }

  private final class RendererHienNhan extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      this.setText(ComboTimKiem.this.layNhanHienThi(value));
      return this;
    }
  }

  private final class LangNgheNoiDungOChu implements DocumentListener {
    @Override
    public void insertUpdate(DocumentEvent e) {
      ComboTimKiem.this.khiNguoiDungGoPhim();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
      ComboTimKiem.this.khiNguoiDungGoPhim();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
      ComboTimKiem.this.khiNguoiDungGoPhim();
    }
  }

  private final class TacVuTimKiem extends SwingWorker<List<T>, Void> {
    private final String tuKhoaDuocTim;
    private final int soThuTuLanTimNay;
    private Exception loiKhiTim;

    TacVuTimKiem(String tuKhoaDuocTim, int soThuTuLanTimNay) {
      this.tuKhoaDuocTim = tuKhoaDuocTim;
      this.soThuTuLanTimNay = soThuTuLanTimNay;
    }

    @Override
    protected List<T> doInBackground() {
      try {
        return ComboTimKiem.this.nguonTimKiem.tim(this.tuKhoaDuocTim);
      } catch (SQLException | RuntimeException ex) {
        this.loiKhiTim = ex;
        return List.of();
      }
    }

    @Override
    protected void done() {
      ComboTimKiem<T> combo = ComboTimKiem.this;

      boolean ketQuaDaCu = this.soThuTuLanTimNay != combo.soThuTuLanTim;
      if (ketQuaDaCu || !combo.isShowing()) {
        return;
      }

      if (this.loiKhiTim != null) {
        combo.ghiLogLoiTim(this.tuKhoaDuocTim, this.loiKhiTim);
        combo.xoaToanBoGoiY();
        return;
      }

      try {
        combo.hienThiGoiY(this.get());
      } catch (CancellationException ex) {
        // Tác vụ đã bị hủy nên bỏ qua kết quả này.
      } catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
        combo.xoaToanBoGoiY();
      } catch (ExecutionException ex) {
        combo.ghiLogLoiTim(this.tuKhoaDuocTim, ex.getCause());
        combo.xoaToanBoGoiY();
      }
    }
  }
}
