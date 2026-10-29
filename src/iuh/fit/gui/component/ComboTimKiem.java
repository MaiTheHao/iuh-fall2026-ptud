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
 * Combo nhập tay có gợi ý theo từ khóa, debounce 300 ms và tối đa 10 dòng.
 * Tách riêng "text đang gõ" khỏi "dòng đang chọn": gõ thì text không bị model ghi đè,
 * chọn dòng thì text được thay bằng {@code giaTriNhap} của phần tử đó.
 */
public final class ComboTimKiem<T> extends JComboBox<T> {

  private static final long serialVersionUID = 1L;

  private static final Logger LOGGER = Logger.getLogger(ComboTimKiem.class.getName());
  private static final int THOI_GIAN_CHO_LOC = 300;
  private static final int SO_DONG_TOI_DA = 10;

  /** Nguồn dữ liệu gợi ý theo từ khóa; ném {@link SQLException} khi không tải được. */
  @FunctionalInterface
  public interface NguonTim<T> {
    List<T> tim(String tuKhoa) throws SQLException;
  }

  private final NguonTim<T> nguon;
  private final Function<T, String> nhanHienThi;
  private final Function<T, String> giaTriNhap;
  private final JTextField oNhap;
  private final Timer boDem = new Timer(THOI_GIAN_CHO_LOC, e -> this.taiLai());

  private SwingWorker<List<T>, Void> workerHienTai;
  private int theHeTim;
  private boolean dangNap;
  private boolean dangLapTrinh;
  private Consumer<T> xuLyChon;
  private Runnable xuLyGo;

  public ComboTimKiem(NguonTim<T> nguon, Function<T, String> nhanHienThi,
          Function<T, String> giaTriNhap) {
    this.nguon = Objects.requireNonNull(nguon, "nguon");
    this.nhanHienThi = Objects.requireNonNull(nhanHienThi, "nhanHienThi");
    this.giaTriNhap = Objects.requireNonNull(giaTriNhap, "giaTriNhap");

    this.setEditable(true);
    this.setMaximumRowCount(SO_DONG_TOI_DA);
    this.boDem.setRepeats(false);

    this.setEditor(new BasicComboBoxEditor() {
      @Override
      public void setItem(Object anObject) {
        // Bỏ qua null và lượt nạp model để text đang gõ không bị xoá/ghi đè.
        if (anObject == null || ComboTimKiem.this.dangNap) {
          return;
        }
        if (anObject instanceof String chuoi) {
          super.setItem(chuoi);
          return;
        }
        try {
          super.setItem(ComboTimKiem.this.giaTriNhap.apply(castItem(anObject)));
        } catch (RuntimeException ex) {
          super.setItem(String.valueOf(anObject));
        }
      }
    });

    this.setRenderer(new DefaultListCellRenderer() {
      private static final long serialVersionUID = 1L;

      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index,
              boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        String nhan = "";
        if (value != null) {
          try {
            nhan = ComboTimKiem.this.nhanHienThi.apply(castItem(value));
          } catch (RuntimeException ex) {
            nhan = value.toString();
          }
        }
        this.setText(nhan == null ? "" : nhan);
        return this;
      }
    });

    this.oNhap = (JTextField) this.getEditor().getEditorComponent();
    this.oNhap.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        ComboTimKiem.this.khiGo();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        ComboTimKiem.this.khiGo();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        ComboTimKiem.this.khiGo();
      }
    });

    this.addActionListener(e -> this.khiChon());
  }

  /** Text thô đang gõ trong ô nhập (không trim). */
  public String getTuKhoa() {
    return this.oNhap.getText();
  }

  /** Đặt text theo chương trình; dừng debounce, huỷ lần tìm đang chạy và không gọi nguồn dữ liệu. */
  public void datTuKhoa(String tuKhoa) {
    this.huyTimKiem();
    this.dangLapTrinh = true;
    try {
      this.boDem.stop();
      this.oNhap.setText(tuKhoa == null ? "" : tuKhoa);
    } finally {
      this.dangLapTrinh = false;
    }
  }

  /** Xoá text và danh sách gợi ý. */
  public void xoa() {
    this.datTuKhoa("");
    this.xoaDanhSach();
  }

  /** Chỉ gọi khi người dùng chọn một dòng thật sự (không gọi khi nạp model). */
  public void datXuLyChon(Consumer<T> xuLy) {
    this.xuLyChon = xuLy;
  }

  /** Chỉ gọi khi người dùng gõ tay (không gọi khi đặt text theo chương trình). */
  public void datXuLyGo(Runnable xuLy) {
    this.xuLyGo = xuLy;
  }

  @SuppressWarnings("unchecked")
  private static <T> T castItem(Object value) {
    return (T) value;
  }

  private void khiGo() {
    if (this.dangLapTrinh || this.dangNap) {
      return;
    }
    this.huyTimKiem();
    this.boDem.restart();
    if (this.xuLyGo != null) {
      this.xuLyGo.run();
    }
  }

  private void khiChon() {
    if (this.dangNap) {
      return;
    }

    Object chon = this.getSelectedItem();
    if (chon == null) {
      return;
    }

    // Enter hoặc mất focus trên combo editable có thể đẩy text vào model;
    // chỉ dòng thật sự nằm trong danh sách gợi ý mới coi là người dùng chọn.
    T item = this.timDongTrongModel(chon);
    if (item == null) {
      return;
    }

    this.dangNap = true;
    try {
      this.datTuKhoa(this.giaTriNhap.apply(item));
      this.setSelectedItem(null);
      this.setPopupVisible(false);
      if (this.xuLyChon != null) {
        this.xuLyChon.accept(item);
      }
    } finally {
      this.dangNap = false;
    }
  }

  private T timDongTrongModel(Object giaTri) {
    for (int i = 0; i < this.getItemCount(); i++) {
      T dong = this.getItemAt(i);
      if (dong == giaTri || dong.equals(giaTri)) {
        return dong;
      }
    }
    return null;
  }

  private void taiLai() {
    // Kết quả của lần gõ trước không được nạp đè lên text hiện tại.
    this.huyTimKiem();
    final int theHe = this.theHeTim;

    final String tuKhoa = this.getTuKhoa();
    if (tuKhoa.isBlank()) {
      this.xoaDanhSach();
      return;
    }

    SwingWorker<List<T>, Void> worker = new SwingWorker<List<T>, Void>() {
      private Exception loiTim;

      @Override
      protected List<T> doInBackground() {
        try {
          return ComboTimKiem.this.nguon.tim(tuKhoa);
        } catch (SQLException | RuntimeException ex) {
          this.loiTim = ex;
          return List.of();
        }
      }

      @Override
      protected void done() {
        if (theHe != ComboTimKiem.this.theHeTim || !ComboTimKiem.this.isShowing()) {
          return;
        }

        if (this.loiTim != null) {
          LOGGER.log(Level.WARNING,
                  "Không tải được gợi ý tìm kiếm cho từ khóa: " + tuKhoa, this.loiTim);
          ComboTimKiem.this.xoaDanhSach();
          return;
        }

        List<T> danhSach;
        try {
          danhSach = this.get();
        } catch (CancellationException ex) {
          return;
        } catch (InterruptedException ex) {
          Thread.currentThread().interrupt();
          ComboTimKiem.this.xoaDanhSach();
          return;
        } catch (ExecutionException ex) {
          LOGGER.log(Level.WARNING,
                  "Không tải được gợi ý tìm kiếm cho từ khóa: " + tuKhoa, ex.getCause());
          ComboTimKiem.this.xoaDanhSach();
          return;
        }

        ComboTimKiem.this.napDanhSach(danhSach);
      }
    };

    this.workerHienTai = worker;
    worker.execute();
  }

  /** Huỷ lần tải đang chạy và vô hiệu hoá kết quả còn trên đường về. */
  private void huyTimKiem() {
    if (this.workerHienTai != null) {
      this.workerHienTai.cancel(true);
      this.workerHienTai = null;
    }
    this.theHeTim++;
  }

  private void napDanhSach(List<T> danhSach) {
    // Lấy text ngay trước khi dựng lại model để không ghi đè ký tự người dùng vừa gõ.
    String textDangGo = this.getTuKhoa();

    this.dangNap = true;
    try {
      this.removeAllItems();
      if (danhSach != null) {
        for (T item : danhSach) {
          if (item != null) {
            this.addItem(item);
          }
        }
      }
      // Model tự chọn dòng đầu khi nạp; bỏ chọn để text đang gõ không bị thay.
      this.setSelectedItem(null);
      this.khoiPhucText(textDangGo);
    } finally {
      this.dangNap = false;
    }

    this.setPopupVisible(this.getItemCount() > 0 && this.oNhap.hasFocus() && this.isShowing());
  }

  /** Đặt lại text sau khi dựng model mà không huỷ debounce đang chờ của lần gõ mới. */
  private void khoiPhucText(String text) {
    this.dangLapTrinh = true;
    try {
      this.oNhap.setText(text == null ? "" : text);
    } finally {
      this.dangLapTrinh = false;
    }
  }

  private void xoaDanhSach() {
    this.dangNap = true;
    try {
      this.removeAllItems();
      this.setSelectedItem(null);
    } finally {
      this.dangNap = false;
    }
    this.setPopupVisible(false);
  }
}
