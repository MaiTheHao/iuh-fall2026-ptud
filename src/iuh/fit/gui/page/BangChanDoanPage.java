package iuh.fit.gui.page;

import iuh.fit.core.context.AppContext;
import iuh.fit.dto.BaseFormDialogConfig;
import iuh.fit.dto.ChanDoanDong;
import iuh.fit.dto.PhieuTiepNhanView;
import iuh.fit.entity.VanDeKyThuat;
import iuh.fit.enumeration.TrangThaiPhieuTiepNhan;
import iuh.fit.enumeration.TrangThaiVanDeKyThuat;
import iuh.fit.gui.Theme;
import iuh.fit.gui.component.BaseFormDialog;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.gui.component.ThaoTac;
import iuh.fit.gui.component.ThongBao;
import iuh.fit.gui.form.ChanDoanDongFormPanel;
import iuh.fit.service.VanDeKyThuatService;
import iuh.fit.util.I18nReaderUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Màn lập/sửa chẩn đoán đa dòng cho một phiếu tiếp nhận.
 */
public class BangChanDoanPage extends AbstractPage {

    

    private static final Logger LOGGER = Logger.getLogger(BangChanDoanPage.class.getName());

    private static final int CHIEU_CAO_DONG = 420;

    private final VanDeKyThuatService vanDeKyThuatService = new VanDeKyThuatService();

    private final List<ChanDoanDong> buffer = new ArrayList<>();
    private final List<String> maDaXoa = new ArrayList<>();
    private final Map<String, TrangThaiVanDeKyThuat> trangThaiDongYTheoMa = new HashMap<>();

    private ManagementPanel pnlDanhSach;
    private JLabel lblMaPhieu;
    private JLabel lblBienSo;
    private JLabel lblKhachHang;
    private JLabel lblSdt;
    private JLabel lblTrangThai;
    private JButton btnThem;
    private JButton btnLuu;

    private PhieuTiepNhanView phieu;
    private String maPhieuDaNap;
    private boolean coThayDoi;

    public BangChanDoanPage() {
        this.setLayout(new BorderLayout(0, 8));
        this.setBackground(Theme.BG);
    }

    @Override
    public void render() {
        this.removeAll();
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        this.setBackground(Theme.BG);

        this.pnlDanhSach = new ManagementPanel();
        this.pnlDanhSach.setListTitle("app.chanDoan.bangTitle");
        this.pnlDanhSach.setHeaders(
                I18nReaderUtil.get("app.chanDoan.tieuDeCot"),
                I18nReaderUtil.get("app.chanDoan.mucDoUuTien"),
                I18nReaderUtil.get("app.chanDoan.moTa"),
                I18nReaderUtil.get("app.chanDoan.giaiPhap"),
                I18nReaderUtil.get("app.chanDoan.trangThaiDongY"));

        this.pnlDanhSach.addActionColumn("app.common.actions",
                new ThaoTac("pencil", "app.chanDoan.suaDong", ThaoTac.KieuThaoTac.THUONG,
                        this::suaDong, row -> this.choPhepSua()),
                new ThaoTac("trash-2", "app.chanDoan.xoaDongDaChon", ThaoTac.KieuThaoTac.NGUY_HIEM,
                        this::xoaDong, row -> this.choPhepSua()));

        this.btnThem = new JButton(I18nReaderUtil.get("app.chanDoan.themDong"));
        Theme.applySecondary(this.btnThem);
        this.btnThem.addActionListener(e -> this.themDong());

        JButton btnHuy = new JButton(I18nReaderUtil.get("app.common.cancel"));
        Theme.applySecondary(btnHuy);
        btnHuy.addActionListener(e -> this.quayLai());

        this.btnLuu = new JButton(I18nReaderUtil.get("app.chanDoan.luuTatCa"));
        Theme.applyPrimary(this.btnLuu);
        this.btnLuu.addActionListener(e -> this.luu());

        JPanel thanhNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        thanhNut.setOpaque(false);
        thanhNut.add(this.btnThem);
        thanhNut.add(btnHuy);
        thanhNut.add(this.btnLuu);

        this.add(this.taoThanhThongTin(), BorderLayout.NORTH);
        this.add(this.pnlDanhSach, BorderLayout.CENTER);
        this.add(thanhNut, BorderLayout.SOUTH);

        this.napDuLieuBanDau();
        this.revalidate();
        this.repaint();
    }

    private JPanel taoThanhThongTin() {
        JPanel thanh = Theme.contentPanel(8);

        JPanel dongThongTin = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        dongThongTin.setOpaque(false);

        this.lblMaPhieu = this.themThongTin(dongThongTin, "app.chanDoan.ma");
        this.lblBienSo = this.themThongTin(dongThongTin, "app.chanDoan.bienSo");
        this.lblKhachHang = this.themThongTin(dongThongTin, "app.chanDoan.khachHang");
        this.lblSdt = this.themThongTin(dongThongTin, "app.phieuTiepNhan.dienThoai");
        this.lblTrangThai = this.themThongTin(dongThongTin, "app.chanDoan.trangThai");

        JButton nutQuayLai = new JButton(I18nReaderUtil.get("app.chanDoan.quayLai"));
        Theme.applySecondary(nutQuayLai);
        nutQuayLai.addActionListener(e -> this.quayLai());

        thanh.add(dongThongTin, BorderLayout.CENTER);
        thanh.add(nutQuayLai, BorderLayout.EAST);
        return thanh;
    }

    private JLabel themThongTin(JPanel dong, String nhanKey) {
        JLabel nhan = new JLabel(I18nReaderUtil.get(nhanKey) + ":");
        nhan.setFont(Theme.fieldLabelFont());
        nhan.setForeground(Theme.INK);

        JLabel giaTri = new JLabel("");
        giaTri.setForeground(Theme.INK);

        dong.add(nhan);
        dong.add(giaTri);
        return giaTri;
    }

    private void napDuLieuBanDau() {
        PhieuTiepNhanView dangChon = AppContext.getInstance().getSelectedPhieuTiepNhan();
        String maDangChon = dangChon == null ? null : dangChon.maPhieuTiepNhan();

        if (this.coThayDoi && Objects.equals(this.maPhieuDaNap, maDangChon)) {
            this.phieu = dangChon;
            this.capNhatThongTinPhieu();
            this.veBang();
            this.capNhatQuyenSua();
            return;
        }

        this.taiLai();
    }

    private void capNhatThongTinPhieu() {
        if (this.phieu == null) {
            this.lblMaPhieu.setText("");
            this.lblBienSo.setText("");
            this.lblKhachHang.setText("");
            this.lblSdt.setText("");
            this.lblTrangThai.setText("");
            return;
        }

        this.lblMaPhieu.setText(this.phieu.maPhieuTiepNhan() == null ? "" : this.phieu.maPhieuTiepNhan());
        this.lblBienSo.setText(this.phieu.bienSo() == null ? "" : this.phieu.bienSo());
        this.lblKhachHang.setText(this.phieu.tenKhachHang() == null ? "" : this.phieu.tenKhachHang());
        this.lblSdt.setText(this.phieu.sdt() == null ? "" : this.phieu.sdt());
        this.lblTrangThai.setText(this.phieu.trangThai() == null
                ? ""
                : this.phieu.trangThai().getI18NBusinessName());
    }

    private void taiLai() {
        this.buffer.clear();
        this.maDaXoa.clear();
        this.trangThaiDongYTheoMa.clear();
        this.coThayDoi = false;
        this.phieu = AppContext.getInstance().getSelectedPhieuTiepNhan();
        this.maPhieuDaNap = this.phieu == null ? null : this.phieu.maPhieuTiepNhan();

        this.capNhatThongTinPhieu();

        if (this.phieu == null) {
            this.veBang();
            this.capNhatQuyenSua();
            return;
        }

        try {
            for (VanDeKyThuat vanDe : this.vanDeKyThuatService.layDanhSachTheoPhieu(
                    this.phieu.maPhieuTiepNhan(), null)) {
                this.buffer.add(new ChanDoanDong(
                        vanDe.getMaVanDe(),
                        vanDe.getTieuDe(),
                        vanDe.getMoTa(),
                        vanDe.getMucDoUuTien(),
                        vanDe.getGiaiPhap(),
                        false));
                if (vanDe.getMaVanDe() != null) {
                    TrangThaiVanDeKyThuat dongY = vanDe.getKhachHangDongY() == null
                            ? TrangThaiVanDeKyThuat.CHUA_XAC_NHAN
                            : vanDe.getKhachHangDongY();
                    this.trangThaiDongYTheoMa.put(vanDe.getMaVanDe().trim(), dongY);
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Khong the tai danh sach chan doan cua phieu", ex);
            ThongBao.loi(this, "app.chanDoan.loiTaiDanhSach");
        }

        this.veBang();
        this.capNhatQuyenSua();
    }

    private void veBang() {
        List<Object[]> rows = new ArrayList<>();

        for (ChanDoanDong dong : this.buffer) {
            TrangThaiVanDeKyThuat dongY = TrangThaiVanDeKyThuat.CHUA_XAC_NHAN;
            if (dong.maVanDe() != null) {
                dongY = this.trangThaiDongYTheoMa.getOrDefault(
                        dong.maVanDe().trim(), TrangThaiVanDeKyThuat.CHUA_XAC_NHAN);
            }
            rows.add(new Object[]{
                    dong.tieuDe(),
                    dong.mucDoUuTien(),
                    dong.moTa(),
                    dong.giaiPhap(),
                    dongY.getI18NBusinessName()
            });
        }

        int dongDau = rows.isEmpty() ? 0 : 1;
        this.pnlDanhSach.setRows(rows);
        this.pnlDanhSach.setCount(dongDau, rows.size(), rows.size());
        this.pnlDanhSach.setPage(1, 1);
    }

    private boolean choPhepSua() {
        return this.phieu != null && this.phieu.trangThai() == TrangThaiPhieuTiepNhan.TIEP_NHAN;
    }

    private void capNhatQuyenSua() {
        boolean choPhep = this.choPhepSua();
        this.btnThem.setEnabled(choPhep);
        this.btnLuu.setEnabled(choPhep);
    }

    private void themDong() {
        if (!this.choPhepSua()) {
            ThongBao.canhBao(this, "app.chanDoan.chiSuaKhiMoi");
            return;
        }

        ChanDoanDongFormPanel form = new ChanDoanDongFormPanel();
        form.napDuLieu(null);

        BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
                SwingUtilities.getWindowAncestor(this),
                "app.chanDoan.themDong",
                form,
                () -> {
                    if (!form.validateInput()) {
                        return false;
                    }
                    this.buffer.add(form.layDuLieu());
                    return true;
                },
                CHIEU_CAO_DONG));

        if (dialog.showAndGet()) {
            this.coThayDoi = true;
            this.veBang();
        }
    }

    private void suaDong(int row) {
        if (!this.choPhepSua() || row < 0 || row >= this.buffer.size()) {
            return;
        }

        ChanDoanDongFormPanel form = new ChanDoanDongFormPanel();
        form.napDuLieu(this.buffer.get(row));

        BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
                SwingUtilities.getWindowAncestor(this),
                "app.chanDoan.suaDong",
                form,
                () -> {
                    if (!form.validateInput()) {
                        return false;
                    }
                    this.buffer.set(row, form.layDuLieu());
                    return true;
                },
                CHIEU_CAO_DONG));

        if (dialog.showAndGet()) {
            this.coThayDoi = true;
            this.veBang();
        }
    }

    private void xoaDong(int row) {
        if (!this.choPhepSua() || row < 0 || row >= this.buffer.size()) {
            return;
        }

        if (!ThongBao.xacNhan(this, "app.chanDoan.xacNhanXoa", "app.chanDoan.tieuDeXoa")) {
            return;
        }

        ChanDoanDong dong = this.buffer.remove(row);
        if (dong.maVanDe() != null) {
            this.maDaXoa.add(dong.maVanDe());
        }

        this.coThayDoi = true;
        this.veBang();
    }

    private void quayLai() {
        if (this.coThayDoi
                && !ThongBao.xacNhan(this, "app.chanDoan.xacNhanBoThayDoi",
                        "app.chanDoan.tieuDeBoThayDoi")) {
            return;
        }

        this.boBanNhap();
        this.goTo(QuanLyChanDoanPage.class);
    }

    private void boBanNhap() {
        this.buffer.clear();
        this.maDaXoa.clear();
        this.trangThaiDongYTheoMa.clear();
        this.coThayDoi = false;
        this.maPhieuDaNap = null;
        this.veBang();
    }

    private void luu() {
        if (this.phieu == null) {
            return;
        }

        if (!this.choPhepSua()) {
            ThongBao.canhBao(this, "app.chanDoan.chiSuaKhiMoi");
            return;
        }

        List<ChanDoanDong> danhSach = new ArrayList<>(this.buffer);
        for (String maVanDe : this.maDaXoa) {
            danhSach.add(new ChanDoanDong(maVanDe, null, null, 0, null, true));
        }

        try {
            this.vanDeKyThuatService.luuChanDoan(this.phieu.maPhieuTiepNhan(), danhSach);
            ThongBao.thongTin(this, "app.common.update_succeed");
            this.taiLai();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            ThongBao.loi(this, ex);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Khong the luu chan doan", ex);
            ThongBao.loi(this, "app.login.errorSystem");
        }
    }

    @Override
    public String getTitleKey() {
        return "app.chanDoan.bangTitle";
    }
}
