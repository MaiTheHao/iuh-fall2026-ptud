package iuh.fit.util;

import iuh.fit.core.config.CuaHangConfig;
import iuh.fit.entity.KhachHang;
import iuh.fit.entity.NhanVien;
import iuh.fit.entity.ThanhToan;
import iuh.fit.enumeration.TrangThaiHoaDon;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Dựng hoá đơn khổ A4 bằng Java2D, hỗ trợ nhiều trang cho hoá đơn dài.
 */
public final class HoaDonRenderer {

    private static final int A4_RONG = 595;
    private static final int A4_CAO = 842;

    private static final float LE = 42.52f;
    private static final float RONG = A4_RONG - LE * 2;

    private static final float LE_TREN = 42f;
    private static final float DAY_NOI_DUNG = A4_CAO - 46f;

    private static final int DPI_IN = 300;
    private static final double TI_LE_IN = DPI_IN / 72.0;

    private static final String FONT = "SansSerif";

    private static final Font F_TEN_CUA_HANG = new Font(FONT, Font.BOLD, 15);
    private static final Font F_TIEU_DE = new Font(FONT, Font.BOLD, 17);
    private static final Font F_TEN_PHU = new Font(FONT, Font.BOLD, 10);
    private static final Font F_MUC = new Font(FONT, Font.BOLD, 9);
    private static final Font F_THUONG = new Font(FONT, Font.PLAIN, 9);
    private static final Font F_NHO = new Font(FONT, Font.PLAIN, 8);
    private static final Font F_NHO_DAM = new Font(FONT, Font.BOLD, 8);
    private static final Font F_TONG = new Font(FONT, Font.BOLD, 12);
    private static final Font F_GHI_CHU = new Font(FONT, Font.ITALIC, 8);

    private static final Color MAU_CHU = new Color(0x111827);
    private static final Color MAU_CHU_MO = new Color(0x6B7280);
    private static final Color MAU_VIEN = new Color(0xE5E7EB);
    private static final Color MAU_NEN = new Color(0xF6, 0xF8, 0xFB);
    private static final Color MAU_NEN_DAU_BANG = new Color(0xEE, 0xF2, 0xF7);
    private static final Color MAU_TRANG = Color.WHITE;

    private static final Color MAU_NEN_DA_THANH_TOAN = new Color(0xDC, 0xFC, 0xE7);
    private static final Color MAU_CHU_DA_THANH_TOAN = new Color(0x16, 0x65, 0x34);
    private static final Color MAU_NEN_CHO_THANH_TOAN = new Color(0xFE, 0xF3, 0xC7);
    private static final Color MAU_CHU_CHO_THANH_TOAN = new Color(0x92, 0x40, 0x0E);
    private static final Color MAU_NEN_DA_HUY = new Color(0xFE, 0xE2, 0xE2);
    private static final Color MAU_CHU_DA_HUY = new Color(0x99, 0x1B, 0x1B);

    private static final float[] COT = {30f, 58f, 244f, 40f, 70f, 67.96f};

    private static final float CHIEU_CAO_DONG_MIN = 22f;
    private static final float DONG_CHU = 11f;

    private static final DateTimeFormatter DINH_DANG_NGAY =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Định dạng tiền dùng chung theo luồng để không tạo lại DecimalFormat cho từng dòng.
    private static final ThreadLocal<DecimalFormat> DINH_DANG_TIEN = ThreadLocal.withInitial(() -> {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.forLanguageTag("vi-VN"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DecimalFormat format = new DecimalFormat("#,##0", symbols);
        format.setGroupingUsed(true);
        return format;
    });

    private HoaDonRenderer() {
    }

    public static List<BufferedImage> xemTruoc(HoaDonInData data) {
        return veCacTrang(data, 1.0);
    }

    public static List<BufferedImage> taoAnhIn(HoaDonInData data) {
        return veCacTrang(data, TI_LE_IN);
    }

    private static List<BufferedImage> veCacTrang(HoaDonInData data, double tiLe) {
        kiemTra(data);

        List<DongInVe> danhSachDong = chuanBiDong(data.danhSachDong());
        LocalDateTime thoiGianIn = LocalDateTime.now();

        List<BufferedImage> danhSachTrang = new ArrayList<>();
        Trang trang = new Trang(tiLe);
        danhSachTrang.add(trang.anh);

        float y = veHeaderDayDu(trang.g, data);
        y = veKhachHang(trang.g, data, y + 10);
        y += 18;
        y = veHeaderBang(trang.g, y);

        int soThuTu = 1;

        for (DongInVe dong : danhSachDong) {
            if (y + dong.chieuCao > DAY_NOI_DUNG) {
                trang.dong();
                trang = new Trang(tiLe);
                danhSachTrang.add(trang.anh);
                y = veHeaderPhu(trang.g, data);
                y = veHeaderBang(trang.g, y);
            }

            veDong(trang.g, dong, soThuTu++, y);
            y += dong.chieuCao;
        }

        veDuongKe(trang.g, LE, y, RONG, 0.8f, MAU_CHU_MO);
        y += 18;

        if (y + chieuCaoKhoiCuoi() > DAY_NOI_DUNG) {
            trang.dong();
            trang = new Trang(tiLe);
            danhSachTrang.add(trang.anh);
            y = veHeaderPhu(trang.g, data) + 8;
        }

        y = veKhoiTongHop(trang.g, data, y);
        veLoiCamOn(trang.g, y + 22);
        trang.dong();

        for (int i = 0; i < danhSachTrang.size(); i++) {
            veChanTrang(danhSachTrang.get(i), tiLe, i + 1, danhSachTrang.size(), thoiGianIn);
        }

        return danhSachTrang;
    }

    private static void kiemTra(HoaDonInData data) {
        if (data == null || data.hoaDon() == null || data.phieuTiepNhan() == null) {
            throw new IllegalArgumentException(I18nReaderUtil.get("app.hoaDon.in.duLieuKhongHopLe"));
        }
    }

    private static float veHeaderDayDu(Graphics2D graphics, HoaDonInData data) {
        float y = LE_TREN;

        graphics.setColor(MAU_CHU);
        graphics.setFont(F_TEN_CUA_HANG);
        veTrai(graphics, CuaHangConfig.ten(), LE, y + 12);

        graphics.setFont(F_TIEU_DE);
        vePhai(graphics, layNhan("tieuDe"), LE + RONG, y + 12);

        graphics.setFont(F_NHO);
        graphics.setColor(MAU_CHU_MO);

        if (!CuaHangConfig.diaChi().isBlank()) {
            veTrai(graphics, CuaHangConfig.diaChi(), LE, y + 25);
        }

        String lienHe = taoDongLienHe();
        if (!lienHe.isBlank()) {
            veTrai(graphics, lienHe, LE, y + 36);
        }

        String phapLy = taoDongPhapLy();
        if (!phapLy.isBlank()) {
            veTrai(graphics, phapLy, LE, y + 47);
        }

        graphics.setFont(F_THUONG);
        graphics.setColor(MAU_CHU);
        vePhai(graphics, layNhan("maHoaDon") + ": " + giaTri(data.hoaDon().getMaHoaDon()),
                LE + RONG, y + 28);
        vePhai(graphics, layNhan("ngayTao") + ": " + dinhDangNgay(data.hoaDon().getNgayTao()),
                LE + RONG, y + 40);
        vePhai(graphics, layNhan("maPhieuTiepNhan") + ": "
                + giaTri(data.phieuTiepNhan().getMaPhieuTiepNhan()), LE + RONG, y + 52);

        TrangThaiHoaDon trangThai = data.hoaDon().getTrangThaiHoaDon();
        if (trangThai != null) {
            veNhan(graphics, trangThai.getI18NBusinessName(), LE + RONG, y + 58,
                    mauNenTrangThai(trangThai), mauChuTrangThai(trangThai));
        }

        float ke = y + 76;
        veDuongKe(graphics, LE, ke, RONG, 1.4f, MAU_CHU);
        return ke + 16;
    }

    private static float veHeaderPhu(Graphics2D graphics, HoaDonInData data) {
        float y = LE_TREN;

        graphics.setColor(MAU_CHU);
        graphics.setFont(F_TEN_PHU);
        veTrai(graphics, CuaHangConfig.ten(), LE, y + 10);

        graphics.setFont(F_NHO);
        graphics.setColor(MAU_CHU_MO);
        vePhai(graphics, layNhan("maHoaDon") + ": " + giaTri(data.hoaDon().getMaHoaDon()),
                LE + RONG, y + 10);

        veDuongKe(graphics, LE, y + 16, RONG, 0.8f, MAU_VIEN);
        graphics.setColor(MAU_CHU);
        return y + 24;
    }

    private static float veKhachHang(Graphics2D graphics, HoaDonInData data, float y) {
        float cao = 52f;
        graphics.setColor(MAU_NEN);
        graphics.fillRoundRect(r(LE), r(y), r(RONG), r(cao), 8, 8);

        KhachHang khachHang = data.phieuTiepNhan().getKhachHang();
        NhanVien nhanVien = data.phieuTiepNhan().getNhanVien();

        float x1 = LE + 14;
        float x2 = LE + RONG / 2f + 8;
        float phai1 = LE + RONG / 2f - 8;
        float phai2 = LE + RONG - 14;

        veO(graphics, layNhan("khachHang"),
                khachHang == null ? null : khachHang.getHoTen(), x1, phai1, y + 18);
        veO(graphics, layNhan("sdt"),
                khachHang == null ? null : khachHang.getSdt(), x2, phai2, y + 18);
        veO(graphics, layNhan("bienSo"),
                data.phieuTiepNhan().getBienSo(), x1, phai1, y + 38);
        veO(graphics, layNhan("nhanVienPhuTrach"),
                nhanVien == null ? null : nhanVien.getHoTen(), x2, phai2, y + 38);

        return y + cao;
    }

    private static void veO(Graphics2D graphics,
            String nhan,
            String giaTri,
            float x,
            float phai,
            float baseline) {

        String nhanText = nhan + ":";

        graphics.setFont(F_NHO);
        graphics.setColor(MAU_CHU_MO);
        graphics.drawString(nhanText, x, baseline);

        float xGiaTri = x + graphics.getFontMetrics().stringWidth(nhanText) + 6;
        graphics.setFont(F_THUONG);
        graphics.setColor(MAU_CHU);
        veTrai(graphics, catEllipsis(graphics.getFontMetrics(), giaTri, phai - xGiaTri),
                xGiaTri, baseline);
    }

    private static float veHeaderBang(Graphics2D graphics, float y) {
        graphics.setColor(MAU_NEN_DAU_BANG);
        graphics.fillRoundRect(r(LE), r(y), r(RONG), 22, 4, 4);

        graphics.setColor(MAU_CHU);
        graphics.setFont(F_NHO_DAM);

        String[] tieuDe = {
                layNhan("stt"),
                layNhan("loai"),
                layNhan("ten"),
                layNhan("soLuong"),
                layNhan("donGia"),
                layNhan("thanhTien")
        };

        float[] cot = viTriCot();
        float baseline = y + 14;

        veCanGiua(graphics, tieuDe[0], cot[0] + COT[0] / 2f, baseline);
        veTrai(graphics, tieuDe[1], cot[1] + 6, baseline);
        veTrai(graphics, tieuDe[2], cot[2] + 6, baseline);
        veCanGiua(graphics, tieuDe[3], cot[3] + COT[3] / 2f, baseline);
        vePhai(graphics, tieuDe[4], cot[4] + COT[4] - 4, baseline);
        vePhai(graphics, tieuDe[5], cot[5] + COT[5] - 2, baseline);

        veDuongKe(graphics, LE, y + 22, RONG, 0.8f, MAU_CHU_MO);
        return y + 22;
    }

    private static void veDong(Graphics2D graphics, DongInVe dongVe, int soThuTu, float y) {
        float cao = dongVe.chieuCao;
        float[] cot = viTriCot();

        if (soThuTu % 2 == 0) {
            graphics.setColor(MAU_NEN);
            graphics.fillRect(r(LE), r(y), r(RONG), r(cao));
        }

        graphics.setFont(F_NHO);
        graphics.setColor(MAU_CHU_MO);
        veCanGiua(graphics, String.valueOf(soThuTu), cot[0] + COT[0] / 2f, y + cao / 2f + 3);

        graphics.setFont(F_NHO);
        graphics.setColor(MAU_CHU);
        veTrai(graphics, catEllipsis(graphics.getFontMetrics(), dongVe.dong.loai(), COT[1] - 12),
                cot[1] + 6, y + cao / 2f + 3);

        graphics.setFont(F_THUONG);
        float top = y + (cao - dongVe.tenDong.size() * DONG_CHU) / 2f;
        float baseline = top + 8.5f;
        for (String dongChu : dongVe.tenDong) {
            veTrai(graphics, dongChu, cot[2] + 6, baseline);
            baseline += DONG_CHU;
        }

        veCanGiua(graphics, String.valueOf(dongVe.dong.soLuong()), cot[3] + COT[3] / 2f,
                y + cao / 2f + 3);
        vePhai(graphics, dinhDangTien(dongVe.dong.donGia()), cot[4] + COT[4] - 4,
                y + cao / 2f + 3);
        vePhai(graphics, dinhDangTien(dongVe.dong.thanhTien()), cot[5] + COT[5] - 2,
                y + cao / 2f + 3);

        veDuongKe(graphics, LE, y + cao, RONG, 0.5f, MAU_VIEN);
    }

    private static float veKhoiTongHop(Graphics2D graphics, HoaDonInData data, float y) {
        float cuoiThanhToan = veThanhToanGon(graphics, data, y);
        float cuoiTongTien = veTongTien(graphics, data, y);
        return Math.max(cuoiThanhToan, cuoiTongTien);
    }

    private static float veThanhToanGon(Graphics2D graphics, HoaDonInData data, float y) {
        float phai = LE + RONG - 262;

        graphics.setFont(F_NHO_DAM);
        graphics.setColor(MAU_CHU);
        veTrai(graphics, layNhan("thongTinThanhToan"), LE, y + 9);

        if (data.thanhToan().isEmpty()) {
            graphics.setFont(F_NHO_DAM);
            graphics.setColor(MAU_CHU_CHO_THANH_TOAN);
            veTrai(graphics, catEllipsis(graphics.getFontMetrics(),
                    layNhan("chuaThanhToan") + " — " + layNhan("ghiChuChuaThanhToan"),
                    phai - LE), LE, y + 26);
            return y + 40;
        }

        ThanhToan thanhToan = data.thanhToan().get();

        String phuongThuc = thanhToan.getPhuongThuc() == null
                ? ""
                : thanhToan.getPhuongThuc().getI18NBusinessName();
        String dongDau = ghep(phuongThuc, dinhDangNgay(thanhToan.getThoiGian()));

        graphics.setFont(F_NHO);
        graphics.setColor(MAU_CHU_MO);
        veTrai(graphics, catEllipsis(graphics.getFontMetrics(), dongDau, phai - LE), LE, y + 24);

        String thuNgan = thanhToan.getNhanVien() == null
                ? ""
                : thanhToan.getNhanVien().getTenHienThi();
        veTrai(graphics, layNhan("nhanVienThuTien") + ": " + thuNgan, LE, y + 36);

        if (thanhToan.getGhiChu() != null && !thanhToan.getGhiChu().isBlank()) {
            veTrai(graphics, layNhan("ghiChu") + ": "
                    + catEllipsis(graphics.getFontMetrics(), thanhToan.getGhiChu(), phai - LE),
                    LE, y + 48);
            return y + 58;
        }

        return y + 46;
    }

    private static float veTongTien(Graphics2D graphics, HoaDonInData data, float y) {
        float rong = 252f;
        float x = LE + RONG - rong;
        float nhanX = x + 12;
        float giaX = LE + RONG;

        graphics.setFont(F_THUONG);
        graphics.setColor(MAU_CHU);
        veTrai(graphics, layNhan("tongGoc"), nhanX, y + 9);
        vePhai(graphics, tien(data.tongGoc()), giaX, y + 9);

        float dong = y + 26;

        if (data.mucGiam() > 0 || data.soTienGiam().signum() > 0) {
            int phanTram = (int) Math.round(data.mucGiam() * 100);
            String nhan = layNhan("giamGia");
            if (phanTram > 0) {
                nhan += " (" + phanTram + "%" + maVoucher(data) + ")";
            }
            graphics.setColor(MAU_CHU_MO);
            veTrai(graphics, nhan, nhanX, dong);
            vePhai(graphics, "-" + tien(data.soTienGiam()), giaX, dong);
            dong += 17;
        }

        float boxY = dong + 2;
        graphics.setColor(MAU_NEN);
        graphics.fillRoundRect(r(x), r(boxY), r(rong), 30, 6, 6);

        graphics.setColor(MAU_CHU);
        graphics.setFont(F_MUC);
        veTrai(graphics, layNhan("thucThu"), nhanX, boxY + 19);

        graphics.setFont(F_TONG);
        vePhai(graphics, tien(data.thucThu()), giaX, boxY + 20);

        return boxY + 30;
    }

    private static void veLoiCamOn(Graphics2D graphics, float y) {
        graphics.setFont(F_GHI_CHU);
        graphics.setColor(MAU_CHU_MO);
        veCanGiua(graphics, layNhan("camOn"), LE + RONG / 2f, y);
    }

    private static float chieuCaoKhoiCuoi() {
        return 112f;
    }

    private static void veChanTrang(BufferedImage anh,
            double tiLe,
            int soTrang,
            int tongTrang,
            LocalDateTime thoiGianIn) {

        Graphics2D graphics = anh.createGraphics();

        try {
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            graphics.scale(tiLe, tiLe);

            graphics.setFont(F_NHO);
            graphics.setColor(MAU_CHU_MO);

            veDuongKe(graphics, LE, A4_CAO - 36, RONG, 0.5f, MAU_VIEN);
            veTrai(graphics, layNhan("thoiGianIn") + ": " + dinhDangNgay(thoiGianIn),
                    LE, A4_CAO - 24);
            vePhai(graphics, layNhan("trang") + " " + soTrang + "/" + tongTrang,
                    LE + RONG, A4_CAO - 24);
        } finally {
            graphics.dispose();
        }
    }

    private static List<DongInVe> chuanBiDong(List<HoaDonInData.DongIn> danhSach) {
        Graphics2D graphics = taoGraphicsDo();

        try {
            graphics.setFont(F_THUONG);
            FontMetrics metrics = graphics.getFontMetrics();
            List<DongInVe> ketQua = new ArrayList<>();

            for (HoaDonInData.DongIn dong : danhSach) {
                List<String> tenDong = catDong(metrics, giaTri(dong.ten()), COT[2] - 12, 2);
                float cao = Math.max(CHIEU_CAO_DONG_MIN, tenDong.size() * DONG_CHU + 11f);
                ketQua.add(new DongInVe(dong, tenDong, cao));
            }

            return ketQua;
        } finally {
            graphics.dispose();
        }
    }

    private static Graphics2D taoGraphicsDo() {
        BufferedImage anh = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = anh.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return graphics;
    }

    private static float[] viTriCot() {
        float[] cot = new float[COT.length];
        cot[0] = LE;
        for (int i = 1; i < COT.length; i++) {
            cot[i] = cot[i - 1] + COT[i - 1];
        }
        return cot;
    }

    private static List<String> catDong(FontMetrics metrics, String noiDung, float rongToiDa,
            int soDongToiDa) {

        List<String> ketQua = new ArrayList<>();

        if (noiDung == null || noiDung.isBlank()) {
            ketQua.add("");
            return ketQua;
        }

        String[] tu = noiDung.trim().split("\\s+");
        StringBuilder dong = new StringBuilder();

        for (String mot : tu) {
            String thu = dong.length() == 0 ? mot : dong + " " + mot;

            if (metrics.stringWidth(thu) <= rongToiDa) {
                dong.setLength(0);
                dong.append(thu);
                continue;
            }

            if (dong.length() > 0) {
                ketQua.add(dong.toString());
                dong.setLength(0);
            }

            if (metrics.stringWidth(mot) > rongToiDa) {
                ketQua.add(catEllipsis(metrics, mot, rongToiDa));
            } else {
                dong.append(mot);
            }
        }

        if (dong.length() > 0) {
            ketQua.add(dong.toString());
        }

        if (ketQua.size() <= soDongToiDa) {
            return ketQua;
        }

        StringBuilder phanConLai = new StringBuilder();
        for (int i = soDongToiDa - 1; i < ketQua.size(); i++) {
            if (phanConLai.length() > 0) {
                phanConLai.append(' ');
            }
            phanConLai.append(ketQua.get(i));
        }

        List<String> rutGon = new ArrayList<>(ketQua.subList(0, soDongToiDa - 1));
        rutGon.add(catEllipsis(metrics, phanConLai.toString(), rongToiDa));
        return rutGon;
    }

    private static String catEllipsis(FontMetrics metrics, String noiDung, float rongToiDa) {
        if (noiDung == null || noiDung.isEmpty()) {
            return "";
        }

        if (metrics.stringWidth(noiDung) <= rongToiDa) {
            return noiDung;
        }

        String dauBaCham = "...";
        int thap = 0;
        int cao = noiDung.length();

        while (thap < cao) {
            int giua = (thap + cao + 1) / 2;

            if (metrics.stringWidth(noiDung.substring(0, giua) + dauBaCham) <= rongToiDa) {
                thap = giua;
            } else {
                cao = giua - 1;
            }
        }

        return noiDung.substring(0, thap) + dauBaCham;
    }

    private static void veNhan(Graphics2D graphics,
            String noiDung,
            float phai,
            float top,
            Color nen,
            Color chu) {

        graphics.setFont(F_NHO_DAM);
        FontMetrics metrics = graphics.getFontMetrics();
        String text = giaTri(noiDung);
        float rong = metrics.stringWidth(text) + 14;
        float x = phai - rong;

        graphics.setColor(nen);
        graphics.fillRoundRect(r(x), r(top), r(rong), 14, 8, 8);

        graphics.setColor(chu);
        veCanGiua(graphics, text, x + rong / 2f, top + 10);
        graphics.setColor(MAU_CHU);
    }

    private static Color mauNenTrangThai(TrangThaiHoaDon trangThai) {
        return switch (trangThai) {
            case DA_THANH_TOAN -> MAU_NEN_DA_THANH_TOAN;
            case CHO_THANH_TOAN -> MAU_NEN_CHO_THANH_TOAN;
            case DA_HUY -> MAU_NEN_DA_HUY;
        };
    }

    private static Color mauChuTrangThai(TrangThaiHoaDon trangThai) {
        return switch (trangThai) {
            case DA_THANH_TOAN -> MAU_CHU_DA_THANH_TOAN;
            case CHO_THANH_TOAN -> MAU_CHU_CHO_THANH_TOAN;
            case DA_HUY -> MAU_CHU_DA_HUY;
        };
    }

    private static void veDuongKe(Graphics2D graphics,
            float x,
            float y,
            float rong,
            float doDay,
            Color mau) {

        graphics.setColor(mau);
        graphics.setStroke(new BasicStroke(doDay));
        graphics.draw(new Line2D.Float(x, y, x + rong, y));
        graphics.setStroke(new BasicStroke(1f));
        graphics.setColor(MAU_CHU);
    }

    private static void veTrai(Graphics2D graphics, String noiDung, float x, float baseline) {
        graphics.drawString(giaTri(noiDung), x, baseline);
    }

    private static void vePhai(Graphics2D graphics, String noiDung, float phai, float baseline) {
        String text = giaTri(noiDung);
        graphics.drawString(text, phai - graphics.getFontMetrics().stringWidth(text), baseline);
    }

    private static void veCanGiua(Graphics2D graphics, String noiDung, float giua, float baseline) {
        String text = giaTri(noiDung);
        graphics.drawString(text, giua - graphics.getFontMetrics().stringWidth(text) / 2f,
                baseline);
    }

    private static String maVoucher(HoaDonInData data) {
        if (data.hoaDon().getVoucher() == null
                || data.hoaDon().getVoucher().getMaVoucher() == null) {
            return "";
        }
        return " • " + data.hoaDon().getVoucher().getMaVoucher();
    }

    private static String ghep(String... phan) {
        List<String> hopLe = new ArrayList<>();

        for (String giaTri : phan) {
            if (giaTri != null && !giaTri.isBlank()) {
                hopLe.add(giaTri.trim());
            }
        }

        return String.join(" • ", hopLe);
    }

    private static String taoDongLienHe() {
        List<String> phan = new ArrayList<>();

        if (!CuaHangConfig.soDienThoai().isBlank()) {
            phan.add(layNhan("hotline") + ": " + CuaHangConfig.soDienThoai());
        }
        if (!CuaHangConfig.email().isBlank()) {
            phan.add(layNhan("email") + ": " + CuaHangConfig.email());
        }

        return String.join("   •   ", phan);
    }

    private static String taoDongPhapLy() {
        List<String> phan = new ArrayList<>();

        if (!CuaHangConfig.maSoThue().isBlank()) {
            phan.add(layNhan("maSoThue") + ": " + CuaHangConfig.maSoThue());
        }
        if (!CuaHangConfig.website().isBlank()) {
            phan.add(CuaHangConfig.website());
        }

        return String.join("   •   ", phan);
    }

    private static String tien(Object giaTri) {
        String donVi = layNhan("donViTienTe");
        return dinhDangTien(giaTri) + (donVi.isBlank() ? "" : " " + donVi);
    }

    private static String dinhDangTien(Object giaTri) {
        if (giaTri == null) {
            return "0";
        }
        if (giaTri instanceof BigDecimal decimal) {
            return DINH_DANG_TIEN.get().format(decimal);
        }
        if (giaTri instanceof Number number) {
            return DINH_DANG_TIEN.get().format(number);
        }
        return giaTri.toString();
    }

    private static String dinhDangNgay(LocalDateTime ngay) {
        return ngay == null ? "" : DINH_DANG_NGAY.format(ngay);
    }

    private static String layNhan(String key) {
        String value = I18nReaderUtil.get("app.hoaDon.in." + key);
        return value == null ? "" : value;
    }

    private static String giaTri(Object giaTri) {
        return giaTri == null ? "" : giaTri.toString();
    }

    private static int r(float giaTri) {
        return Math.round(giaTri);
    }

    private static final class Trang {

        private final BufferedImage anh;
        private final Graphics2D g;

        private Trang(double tiLe) {
            this.anh = new BufferedImage(
                    (int) Math.round(A4_RONG * tiLe),
                    (int) Math.round(A4_CAO * tiLe),
                    BufferedImage.TYPE_INT_RGB);
            this.g = this.anh.createGraphics();
            this.g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            this.g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            this.g.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            this.g.setColor(MAU_TRANG);
            this.g.fillRect(0, 0, this.anh.getWidth(), this.anh.getHeight());
            this.g.scale(tiLe, tiLe);
            this.g.setColor(MAU_CHU);
        }

        private void dong() {
            this.g.dispose();
        }
    }

    private record DongInVe(HoaDonInData.DongIn dong, List<String> tenDong, float chieuCao) {
    }
}
