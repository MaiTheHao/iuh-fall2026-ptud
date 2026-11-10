package iuh.fit.gui.component;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.Normalizer;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import iuh.fit.core.config.QrConfig;
import iuh.fit.enumeration.PhuongThucThanhToan;
import iuh.fit.gui.Theme;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.VietQrClient;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ThanhToanDialog extends JDialog {

  private static final Logger LOGGER = Logger.getLogger(ThanhToanDialog.class.getName());

  private static final int CHIEU_RONG_QR = 280;
  private static final int CHIEU_CAO_QR = 280;

  private final long soTien;
  private final String noiDungChuyenKhoan;

  private final JRadioButton radChuyenKhoan;
  private final JRadioButton radTienMat;

  private final JLabel lblAnhQr;
  private final JLabel lblTrangThaiQr;
  private final JLabel lblNganHang = new JLabel();
  private final JLabel lblSoTaiKhoan = new JLabel();
  private final JLabel lblTenChuTaiKhoan = new JLabel();

  private final JTextField txtGhiChu;

  private final JPanel pnlKhuVucQr;

  private SwingWorker<BufferedImage, Void> qrWorker;

  private boolean daTaiQr;
  private boolean duocXacNhan;

  public ThanhToanDialog(Window owner, long soTien, String maHoaDon) {
    super(owner, I18nReaderUtil.get("app.thanhToan.dialogTitle"), ModalityType.APPLICATION_MODAL);

    this.soTien = soTien;
    this.noiDungChuyenKhoan = taoNoiDungChuyenKhoan(maHoaDon);
    this.txtGhiChu = new JTextField(24);

    this.radChuyenKhoan = new JRadioButton(I18nReaderUtil.get("app.thanhToan.phuongThucThanhToan.chuyenKhoan"));

    this.radTienMat = new JRadioButton(I18nReaderUtil.get("app.thanhToan.phuongThucThanhToan.tienMat"));

    this.lblAnhQr = new JLabel("", SwingConstants.CENTER);
    this.lblTrangThaiQr = new JLabel("", SwingConstants.CENTER);

    this.pnlKhuVucQr = new JPanel(new BorderLayout(8, 8));

    this.khoiTao();
  }

  public boolean duocXacNhan() {
    return this.duocXacNhan;
  }

  public PhuongThucThanhToan phuongThuc() {
    return this.radChuyenKhoan.isSelected() ? PhuongThucThanhToan.CHUYEN_KHOAN : PhuongThucThanhToan.TIEN_MAT;
  }

  public String ghiChu() {
    if (!this.radChuyenKhoan.isSelected()) {
      return null;
    }

    String ghiChu = this.txtGhiChu.getText();

    return ghiChu == null || ghiChu.isBlank()
            ? null
            : ghiChu.trim();
  }

  private JPanel taoThongTinChuyenKhoan() {
    JPanel pnlThongTinChuyenKhoan = new JPanel(new GridLayout(0, 1, 4, 4));
    pnlThongTinChuyenKhoan.setOpaque(false);

    pnlThongTinChuyenKhoan.add(
            taoDongThongTin("app.thanhToan.nganHang",
                    QrConfig.bin(),
                    this.lblNganHang));

    pnlThongTinChuyenKhoan.add(
            taoDongThongTin("app.thanhToan.soTaiKhoan",
                    QrConfig.soTk(),
                    this.lblSoTaiKhoan));

    pnlThongTinChuyenKhoan.add(
            taoDongThongTin("app.thanhToan.tenChuTaiKhoan",
                    QrConfig.tenTk(),
                    this.lblTenChuTaiKhoan));

    pnlThongTinChuyenKhoan.add(
            taoDongThongTin("app.thanhToan.soTien",
                    new DecimalFormat("#,##0").format(this.soTien) + " đ",
                    new JLabel()));

    pnlThongTinChuyenKhoan.add(
            taoDongThongTin("app.thanhToan.noiDung",
                    this.noiDungChuyenKhoan,
                    new JLabel()));
    JPanel pnlGhiChu = new JPanel(new BorderLayout(8, 0));
    pnlGhiChu.setOpaque(false);

    JLabel lblGhiChu = new JLabel(
            I18nReaderUtil.get("app.thanhToan.ghiChu"));

    pnlGhiChu.add(lblGhiChu, BorderLayout.WEST);
    pnlGhiChu.add(this.txtGhiChu, BorderLayout.CENTER);

    pnlThongTinChuyenKhoan.add(pnlGhiChu);

    return pnlThongTinChuyenKhoan;
  }

  private JPanel taoDongThongTin(String key, String giaTri, JLabel lblGiaTri) {
    JPanel pnlDongThongTin = new JPanel(new BorderLayout(8, 0));
    pnlDongThongTin.setOpaque(false);

    JLabel lblNhan = new JLabel(I18nReaderUtil.get(key));
    lblGiaTri.setText(giaTri == null || giaTri.isBlank() ? "-" : giaTri);

    pnlDongThongTin.add(lblNhan, BorderLayout.WEST);
    pnlDongThongTin.add(lblGiaTri, BorderLayout.CENTER);

    return pnlDongThongTin;
  }

  private void khoiTao() {
    this.cauHinhCuaSo();
    this.taoNoiDung();
    this.ganSuKien();
  }

  private void cauHinhCuaSo() {
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.setLayout(new BorderLayout());
    this.getContentPane().setBackground(Theme.BG);

    this.setSize(560, 680);
    this.setMinimumSize(new Dimension(520, 600));
    this.setResizable(false);
    this.setLocationRelativeTo(getOwner());
  }

  private void taoNoiDung() {
    JPanel pnlNoiDung = Theme.contentPanel(12);
    pnlNoiDung.add(this.taoPhanDau(), BorderLayout.NORTH);

    JScrollPane scrNoiDung = new JScrollPane(this.taoThan());
    scrNoiDung.setBorder(null);
    scrNoiDung.setOpaque(false);
    scrNoiDung.getViewport().setOpaque(false);
    scrNoiDung.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrNoiDung.setVerticalScrollBarPolicy(
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

    pnlNoiDung.add(scrNoiDung, BorderLayout.CENTER);
    pnlNoiDung.add(this.taoThanhNut(), BorderLayout.SOUTH);

    this.add(pnlNoiDung, BorderLayout.CENTER);
  }

  private void ganSuKien() {
    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowOpened(WindowEvent e) {
        taiQrNeuCan();
      }

      @Override
      public void windowClosed(WindowEvent e) {
        huyQrWorker();
      }
    });

    this.getRootPane().registerKeyboardAction(e -> this.dispose(),
            KeyStroke.getKeyStroke("ESCAPE"),
            JComponent.WHEN_IN_FOCUSED_WINDOW);

    this.radChuyenKhoan.addActionListener(e -> {
      this.pnlKhuVucQr.setVisible(true);
      this.taiQrNeuCan();
      this.revalidate();
      this.repaint();
    });

    this.radTienMat.addActionListener(e -> {
      this.pnlKhuVucQr.setVisible(false);
      this.huyQrWorker();
      this.revalidate();
      this.repaint();
    });
  }

  private JPanel taoPhanDau() {
    JLabel lblTieuDe = Theme.tieuDeDialog("app.thanhToan.dialogTitle");

    JPanel pnlPhanDau = new JPanel(new BorderLayout());
    pnlPhanDau.setOpaque(false);
    pnlPhanDau.add(lblTieuDe, BorderLayout.CENTER);

    return pnlPhanDau;
  }

  private JPanel taoThan() {
    JPanel pnlThan = new JPanel();
    pnlThan.setOpaque(false);
    pnlThan.setLayout(new BoxLayout(pnlThan, BoxLayout.Y_AXIS));

    pnlThan.add(this.taoKhoiSoTien());
    pnlThan.add(Box.createVerticalStrut(12));
    pnlThan.add(this.taoKhoiPhuongThuc());
    pnlThan.add(Box.createVerticalStrut(8));
    pnlThan.add(this.pnlKhuVucQr);

    return pnlThan;
  }

  private JPanel taoKhoiSoTien() {
    JLabel lblNhanSoTien = new JLabel(I18nReaderUtil.get("app.thanhToan.soTien"));

    JLabel lblGiaTriSoTien = new JLabel(new DecimalFormat("#,##0").format(this.soTien) + " đ");

    lblGiaTriSoTien.setForeground(Theme.INK);
    lblGiaTriSoTien.setFont(UIManager.getFont("TitledBorder.font"));

    JPanel pnlSoTien = new JPanel(new BorderLayout(8, 4));
    pnlSoTien.setOpaque(false);
    pnlSoTien.add(lblNhanSoTien, BorderLayout.NORTH);
    pnlSoTien.add(lblGiaTriSoTien, BorderLayout.CENTER);
    pnlSoTien.setBorder(Theme.leDeu(8));

    return pnlSoTien;
  }

  private JPanel taoKhoiPhuongThuc() {
    ButtonGroup nhomLuaChonPhuongThuc = new ButtonGroup();
    nhomLuaChonPhuongThuc.add(this.radChuyenKhoan);
    nhomLuaChonPhuongThuc.add(this.radTienMat);

    this.radChuyenKhoan.setOpaque(false);
    this.radTienMat.setOpaque(false);
    this.radChuyenKhoan.setSelected(true);

    JPanel pnlPhuongThuc = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));

    pnlPhuongThuc.setOpaque(false);
    pnlPhuongThuc.add(this.radChuyenKhoan);
    pnlPhuongThuc.add(this.radTienMat);

    this.pnlKhuVucQr.setOpaque(false);
    this.pnlKhuVucQr.setBorder(Theme.leDeu(8));

    this.pnlKhuVucQr.add(this.taoVungAnhQr(), BorderLayout.CENTER);
    this.pnlKhuVucQr.add(this.taoThongTinChuyenKhoan(), BorderLayout.SOUTH);

    return pnlPhuongThuc;
  }

  private JPanel taoVungAnhQr() {
    JPanel pnlVungAnhQr = new JPanel(new BorderLayout(4, 4));
    pnlVungAnhQr.setOpaque(false);

    this.lblAnhQr.setPreferredSize(new Dimension(CHIEU_RONG_QR, CHIEU_CAO_QR));

    this.lblTrangThaiQr.setForeground(Theme.palette().INK_MUTED());

    pnlVungAnhQr.add(this.lblTrangThaiQr, BorderLayout.NORTH);
    pnlVungAnhQr.add(this.lblAnhQr, BorderLayout.CENTER);

    return pnlVungAnhQr;
  }

  private JPanel taoThanhNut() {
    JButton btnHuy = new JButton(I18nReaderUtil.get("app.thanhToan.huy"));

    JButton btnXacNhan = new JButton(I18nReaderUtil.get("app.thanhToan.xacNhan"));

    Theme.applySecondary(btnHuy);
    Theme.applyPrimary(btnXacNhan);

    btnHuy.addActionListener(e -> this.dispose());

    btnXacNhan.addActionListener(e -> {
      this.duocXacNhan = true;
      this.dispose();
    });

    JPanel pnlThanhNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));

    pnlThanhNut.setOpaque(false);
    pnlThanhNut.add(btnHuy);
    pnlThanhNut.add(btnXacNhan);

    this.getRootPane().setDefaultButton(btnXacNhan);

    return pnlThanhNut;
  }

  private void taiQrNeuCan() {
    if (!this.radChuyenKhoan.isSelected() || !this.isDisplayable() || this.qrWorker != null || this.daTaiQr) {
      return;
    }

    String bin = QrConfig.bin();
    String soTk = QrConfig.soTk();
    String tenTk = QrConfig.tenTk();

    if (bin == null || bin.isBlank() || soTk == null || soTk.isBlank() || tenTk == null || tenTk.isBlank()) {

      this.daTaiQr = true;
      this.hienThiQrFallback();
      return;
    }

    this.lblTrangThaiQr.setText(I18nReaderUtil.get("app.thanhToan.dangTaoQr"));

    this.qrWorker = new SwingWorker<>() {
      @Override
      protected BufferedImage doInBackground() {
        return VietQrClient.taoQr(soTien, noiDungChuyenKhoan);
      }

      @Override
      protected void done() {
        if (qrWorker != this || !isDisplayable()) {
          return;
        }

        qrWorker = null;

        if (!radChuyenKhoan.isSelected()) {
          return;
        }

        daTaiQr = true;

        try {
          BufferedImage anhQrTao = get();

          if (anhQrTao != null) {
            lblTrangThaiQr.setText("");
            hienThiAnh(anhQrTao);
          } else {
            hienThiQrFallback();
          }
        } catch (Exception e) {
          LOGGER.log(Level.WARNING, "Không tạo được mã QR động", e);
          hienThiQrFallback();
        }
      }
    };

    this.qrWorker.execute();
  }

  private void hienThiQrFallback() {
    this.lblTrangThaiQr.setText(I18nReaderUtil.get("app.thanhToan.qrLoi"));

    try (InputStream dauVao = getClass().getClassLoader().getResourceAsStream("resources/qr.jpg")) {

      if (dauVao == null) {
        this.lblAnhQr.setIcon(null);
        this.lblAnhQr.setText("");
        return;
      }

      BufferedImage anhQrFallback = ImageIO.read(dauVao);

      if (anhQrFallback != null) {
        this.hienThiAnh(anhQrFallback);
      } else {
        this.lblAnhQr.setIcon(null);
        this.lblAnhQr.setText("");
      }

    } catch (IOException e) {
      LOGGER.log(Level.WARNING, "Không đọc được ảnh QR dự phòng", e);
      this.lblAnhQr.setIcon(null);
      this.lblAnhQr.setText("");
    }
  }

  private void hienThiAnh(BufferedImage anhQr) {
    Image anhQrThuNho = anhQr.getScaledInstance(CHIEU_RONG_QR, CHIEU_CAO_QR, Image.SCALE_SMOOTH);

    this.lblAnhQr.setText("");
    this.lblAnhQr.setIcon(new ImageIcon(anhQrThuNho));
  }

  private void huyQrWorker() {
    if (this.qrWorker != null) {
      this.qrWorker.cancel(true);
      this.qrWorker = null;
      this.daTaiQr = false;
    }
  }

  private static String taoNoiDungChuyenKhoan(String maHoaDon) {
    String noiDung = "TT " + (maHoaDon == null ? "" : maHoaDon);

    String ascii = Normalizer.normalize(noiDung, Normalizer.Form.NFD).replaceAll("\\p{M}", "")
            .replaceAll("[^\\x20-\\x7E]", "");

    return ascii.length() > 25 ? ascii.substring(0, 25) : ascii;
  }

}
