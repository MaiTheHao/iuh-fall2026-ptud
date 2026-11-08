
package iuh.fit.util;

import iuh.fit.entity.ThanhToan;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class HoaDonRenderer {

    private static final int CHIEU_RONG_A4 = 595;
    private static final int CHIEU_CAO_A4 = 842;

    private static final float LE_TRANG = 42.52f;
    private static final float CHIEU_RONG_NOI_DUNG =
            CHIEU_RONG_A4 - LE_TRANG * 2;
    
    private static final int DPI_IN = 300;
    private static final double TI_LE_DPI =
            DPI_IN / 72.0;

    private static final float CHIEU_CAO_DONG = 21f;

    private static final String TEN_CUA_HANG = "GARA Ô TÔ LÚA";

    private static final Font FONT_THUONG =
            new Font("Dialog", Font.PLAIN, 9);

    private static final Font FONT_DAM =
            new Font("Dialog", Font.BOLD, 9);

    private static final Font FONT_TIEU_DE =
            new Font("SansSerif", Font.BOLD, 19);

    private static final Font FONT_TEN_CUA_HANG =
            new Font("SansSerif", Font.BOLD, 14);

    private static final Font FONT_TONG_TIEN =
            new Font("Dialog", Font.BOLD, 12);

    private static final DateTimeFormatter DINH_DANG_NGAY =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private HoaDonRenderer() {
    }

    public static BufferedImage xemTruoc(HoaDonInData data) {
        if (data == null
                || data.hoaDon() == null
                || data.phieuTiepNhan() == null) {
            throw new IllegalArgumentException(
                    I18nReaderUtil.get("app.hoaDon.in.duLieuKhongHopLe"));
        }

        BufferedImage image = new BufferedImage(
                CHIEU_RONG_A4,
                CHIEU_CAO_A4,
                BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = image.createGraphics();

        try {
            graphics.setColor(java.awt.Color.WHITE);
            graphics.fillRect(
                    0, 0, CHIEU_RONG_A4, CHIEU_CAO_A4);

            graphics.setColor(java.awt.Color.BLACK);

            graphics.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            graphics.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            graphics.setRenderingHint(
                    RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);

            veHoaDon(graphics, data);
        } finally {
            graphics.dispose();
        }

        return image;
    }

    private static void veHoaDon(
            Graphics2D graphics,
            HoaDonInData data) {

        float x = LE_TRANG;
        float y = 58f;

        graphics.setFont(FONT_TEN_CUA_HANG);
        veCanGiua(graphics, TEN_CUA_HANG, y);

        y += 35;

        graphics.setFont(FONT_TIEU_DE);
        veCanGiua(
                graphics,
                layNhan("tieuDe"),
                y);

        y += 19;
        veDuongKe(graphics, x, y, CHIEU_RONG_NOI_DUNG);
        y += 27;

        graphics.setFont(FONT_DAM);
        veChu(graphics, layNhan("maHoaDon") + ":", x, y);

        graphics.setFont(FONT_THUONG);
        veChu(
                graphics,
                giaTri(data.hoaDon().getMaHoaDon()),
                x + 75,
                y);

        veChuPhai(
                graphics,
                layNhan("ngayTao") + ": "
                        + dinhDangNgay(data.hoaDon().getNgayTao()),
                x + CHIEU_RONG_NOI_DUNG,
                y);

        y += 32;
        var phieuTiepNhan = data.phieuTiepNhan();
        var khachHang = phieuTiepNhan.getKhachHang();

        graphics.setFont(FONT_DAM);
        veChu(
                graphics,
                layNhan("thongTinKhachHang"),
                x,
                y);

        y += 19;

        graphics.setFont(FONT_THUONG);

        veChu(
                graphics,
                layNhan("khachHang") + ": "
                        + giaTri(khachHang == null
                                ? null : khachHang.getHoTen()),
                x,
                y);

        veChu(
                graphics,
                layNhan("sdt") + ": "
                        + giaTri(khachHang == null
                                ? null : khachHang.getSdt()),
                x + 285,
                y);

        y += 18;

        veChu(
                graphics,
                layNhan("bienSo") + ": "
                        + giaTri(phieuTiepNhan.getBienSo()),
                x,
                y);

        y += 18;

        var nhanVien = phieuTiepNhan.getNhanVien();

        veChu(
                graphics,
                layNhan("nhanVienPhuTrach") + ": "
                        + giaTri(nhanVien == null
                                ? null
                                : nhanVien.getHoTen()),
                x,
                y);

        y += 22;
        veDuongKe(graphics, x, y, CHIEU_RONG_NOI_DUNG);
        y += 18;

        float[] doRongCot = {
                55, 185, 65, 100, 110
        };

        veTieuDeBang(graphics, x, y, doRongCot);
        y += 25;

        y = veNoiDungBang(
                graphics,
                data,
                x,
                y,
                doRongCot);

        y += 25;

        veDuongKe(graphics, x, y, CHIEU_RONG_NOI_DUNG);
        y += 22;

        float cotGiaTri = x + CHIEU_RONG_NOI_DUNG;

        graphics.setFont(FONT_THUONG);

        veChu(
                graphics,
                layNhan("tongGoc") + ":",
                x + 220,
                y);

        veChuPhai(
                graphics,
                dinhDangTien(data.tongGoc()) + " "
                        + layNhan("donViTienTe"),
                cotGiaTri,
                y);

        y += 21;

        veChu(
                graphics,
                layNhan("giamGia") + ":",
                x + 220,
                y);

        veChuPhai(
                graphics,
                dinhDangTien(data.soTienGiam()) + " "
                        + layNhan("donViTienTe"),
                cotGiaTri,
                y);
        y += 25;

        graphics.setFont(FONT_TONG_TIEN);

        veChu(
                graphics,
                layNhan("thucThu") + ":",
                x + 220,
                y);

        veChuPhai(
                graphics,
                dinhDangTien(data.thucThu()) + " "
                        + layNhan("donViTienTe"),
                cotGiaTri,
                y);

        y += 28;

        veDuongKe(graphics, x, y, CHIEU_RONG_NOI_DUNG);
        y += 22;

        graphics.setFont(FONT_DAM);

        veChu(
                graphics,
                layNhan("thongTinThanhToan"),
                x,
                y);

        y += 20;

        if (data.thanhToan().isEmpty()) {
            graphics.setFont(FONT_DAM);

            veChu(
                    graphics,
                    layNhan("chuaThanhToan"),
                    x,
                    y);

            return;
        }

        ThanhToan thanhToan = data.thanhToan().get();

        graphics.setFont(FONT_THUONG);

        veChu(
                graphics,
                layNhan("phuongThuc") + ": "
                        + (thanhToan.getPhuongThuc() == null
                                ? ""
                                : I18nReaderUtil.get(
                                        thanhToan.getPhuongThuc().getI18nKey())),
                x,
                y);

        y += 18;

        veChu(
                graphics,
                layNhan("thoiGian") + ": "
                        + dinhDangNgay(thanhToan.getThoiGian()),
                x,
                y);

        y += 18;

        veChu(
                graphics,
                layNhan("soTienThanhToan") + ": "
                        + dinhDangTien(thanhToan.getSoTien())
                        + " " + layNhan("donViTienTe"),
                x,
                y);

        if (thanhToan.getGhiChu() != null
                && !thanhToan.getGhiChu().isBlank()) {

            y += 18;

            veChu(
                    graphics,
                    layNhan("ghiChu") + ": "
                            + catEllipsis(
                                    graphics,
                                    thanhToan.getGhiChu(),
                                    CHIEU_RONG_NOI_DUNG),
                    x,
                    y);
        }
    }

    private static void veTieuDeBang(
            Graphics2D graphics,
            float x,
            float y,
            float[] doRongCot) {

        String[] tieuDe = {
                layNhan("loai"),
                layNhan("ten"),
                layNhan("soLuong"),
                layNhan("donGia"),
                layNhan("thanhTien")
        };

        graphics.setFont(FONT_DAM);

        veDuongKe(graphics, x, y, CHIEU_RONG_NOI_DUNG);

        float viTriX = x;

        for (int i = 0; i < tieuDe.length; i++) {
            veCanGiuaTrongCot(
                    graphics,
                    tieuDe[i],
                    viTriX,
                    y + 16,
                    doRongCot[i]);

            viTriX += doRongCot[i];
        }

        veDuongKe(
                graphics,
                x,
                y + 24,
                CHIEU_RONG_NOI_DUNG);
    }

    private static float veNoiDungBang(
            Graphics2D graphics,
            HoaDonInData data,
            float x,
            float y,
            float[] doRongCot) {

        graphics.setFont(FONT_THUONG);

        for (var dong : data.danhSachDong()) {
            float viTriX = x;

            float viTriChu = y
                    + CHIEU_CAO_DONG / 2
                    + graphics.getFontMetrics().getAscent() / 2f
                    - 1;

            String[] giaTri = {
                    giaTri(dong.loai()),
                    giaTri(dong.ten()),
                    String.valueOf(dong.soLuong()),
                    dinhDangTien(dong.donGia()),
                    dinhDangTien(dong.thanhTien())
            };

            for (int i = 0; i < giaTri.length; i++) {
                String noiDung = catEllipsis(
                        graphics,
                        giaTri[i],
                        doRongCot[i] - 8);

                if (i >= 3) {
                    veChuPhai(
                            graphics,
                            noiDung,
                            viTriX + doRongCot[i] - 4,
                            viTriChu);
                } else if (i == 2) {
                    veCanGiuaTrongCot(
                            graphics,
                            noiDung,
                            viTriX,
                            viTriChu,
                            doRongCot[i]);
                } else {
                    veChu(
                            graphics,
                            noiDung,
                            viTriX + 4,
                            viTriChu);
                }

                viTriX += doRongCot[i];
            }

            y += CHIEU_CAO_DONG;

            veDuongKe(
                    graphics,
                    x,
                    y,
                    CHIEU_RONG_NOI_DUNG);
        }

        return y;
    }

    private static void veDuongKe(
            Graphics2D graphics,
            float x,
            float y,
            float chieuRong) {

        graphics.setColor(java.awt.Color.DARK_GRAY);
        graphics.drawLine(
                Math.round(x),
                Math.round(y),
                Math.round(x + chieuRong),
                Math.round(y));

        graphics.setColor(java.awt.Color.BLACK);
    }

    private static void veChu(
            Graphics2D graphics,
            String noiDung,
            float x,
            float y) {

        graphics.drawString(noiDung, x, y);
    }

    private static void veChuPhai(
            Graphics2D graphics,
            String noiDung,
            float x,
            float y) {

        FontMetrics metrics = graphics.getFontMetrics();

        graphics.drawString(
                noiDung,
                x - metrics.stringWidth(noiDung),
                y);
    }

    private static void veCanGiua(
            Graphics2D graphics,
            String noiDung,
            float y) {

        FontMetrics metrics = graphics.getFontMetrics();

        float x = (CHIEU_RONG_A4
                - metrics.stringWidth(noiDung)) / 2f;

        graphics.drawString(noiDung, x, y);
    }

    private static void veCanGiuaTrongCot(
            Graphics2D graphics,
            String noiDung,
            float x,
            float y,
            float chieuRong) {

        FontMetrics metrics = graphics.getFontMetrics();

        float viTriX = x
                + (chieuRong - metrics.stringWidth(noiDung)) / 2f;

        graphics.drawString(noiDung, viTriX, y);
    }

    private static String catEllipsis(
            Graphics2D graphics,
            String noiDung,
            float chieuRongToiDa) {

        if (noiDung == null || noiDung.isEmpty()) {
            return "";
        }

        FontMetrics metrics = graphics.getFontMetrics();

        if (metrics.stringWidth(noiDung) <= chieuRongToiDa) {
            return noiDung;
        }

        String dauBaCham = "...";
        int doDai = noiDung.length();

        while (doDai > 0) {
            String ketQua = noiDung.substring(0, doDai) + dauBaCham;

            if (metrics.stringWidth(ketQua) <= chieuRongToiDa) {
                return ketQua;
            }

            doDai--;
        }

        return dauBaCham;
    }
    
    public static BufferedImage taoAnhIn(HoaDonInData data) {
        if (data == null
                || data.hoaDon() == null
                || data.phieuTiepNhan() == null) {
            throw new IllegalArgumentException(
                    I18nReaderUtil.get("app.hoaDon.in.duLieuKhongHopLe"));
        }

        int chieuRong = (int) Math.round(
                CHIEU_RONG_A4 * TI_LE_DPI);

        int chieuCao = (int) Math.round(
                CHIEU_CAO_A4 * TI_LE_DPI);

        BufferedImage image = new BufferedImage(
                chieuRong,
                chieuCao,
                BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = image.createGraphics();

        try {
            graphics.setColor(java.awt.Color.WHITE);
            graphics.fillRect(0, 0, chieuRong, chieuCao);

            graphics.setColor(java.awt.Color.BLACK);

            graphics.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            graphics.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            graphics.setRenderingHint(
                    RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);

            graphics.scale(TI_LE_DPI, TI_LE_DPI);

            veHoaDon(graphics, data);

        } finally {
            graphics.dispose();
        }

        return image;
    }

    private static String layNhan(String key) {
        return I18nReaderUtil.get("app.hoaDon.in." + key);
    }

    private static String dinhDangNgay(LocalDateTime ngay) {
        return ngay == null
                ? ""
                : ngay.format(DINH_DANG_NGAY);
    }

    private static String dinhDangTien(Object giaTri) {
        if (giaTri == null) {
            return "0";
        }

        DecimalFormatSymbols symbols =
                new DecimalFormatSymbols(Locale.forLanguageTag("vi-VN"));

        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');

        DecimalFormat format = new DecimalFormat("#,##0", symbols);
        format.setGroupingUsed(true);

        if (giaTri instanceof BigDecimal decimal) {
            return format.format(decimal);
        }

        if (giaTri instanceof Number number) {
            return format.format(number);
        }

        return giaTri.toString();
    }

    private static String giaTri(Object giaTri) {
        return giaTri == null ? "" : giaTri.toString();
    }
}
