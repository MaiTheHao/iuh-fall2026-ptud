package iuh.fit.util;

import iuh.fit.gui.component.ThongBao;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.awt.Component;
import java.awt.image.BufferedImage;

public final class InHoaDonUtil {

    private static final String KEY_LOI_IN =
            "app.hoaDon.inLoi";

    private InHoaDonUtil() {
        throw new AssertionError("Utility class");
    }

    public static void in(Component parent, HoaDonInData data) {
        if (data == null) {
            ThongBao.loi(parent, "app.hoaDon.in.duLieuKhongHopLe");
            return;
        }

        if (PrinterJob.lookupPrintServices().length == 0) {
            ThongBao.loi(parent, KEY_LOI_IN);
            return;
        }

        BufferedImage hinhHoaDon;

        try {
        	hinhHoaDon = HoaDonRenderer.taoAnhIn(data);
        } catch (IllegalArgumentException ex) {
            ThongBao.loi(parent, ex);
            return;
        }

        PrinterJob printerJob = PrinterJob.getPrinterJob();

        printerJob.setPrintable(
                taoNoiDungIn(hinhHoaDon));

        if (!printerJob.printDialog()) {
            return;
        }

        try {
            printerJob.print();
        } catch (PrinterException ex) {
            ThongBao.loi(parent, KEY_LOI_IN);
        }
    }

    public static Printable taoNoiDungIn(BufferedImage hinhHoaDon) {
        return (Graphics graphics, PageFormat pageFormat, int pageIndex) -> {
            if (pageIndex > 0) {
                return Printable.NO_SUCH_PAGE;
            }

            Graphics2D graphics2D =
                    (Graphics2D) graphics.create();

            try {
                double chieuRongVungIn =
                        pageFormat.getImageableWidth();

                double chieuCaoVungIn =
                        pageFormat.getImageableHeight();

                double tiLe = Math.min(
                        chieuRongVungIn / hinhHoaDon.getWidth(),
                        chieuCaoVungIn / hinhHoaDon.getHeight());

                double chieuRongAnh =
                        hinhHoaDon.getWidth() * tiLe;

                double chieuCaoAnh =
                        hinhHoaDon.getHeight() * tiLe;

                double toaDoX = pageFormat.getImageableX()
                        + (chieuRongVungIn - chieuRongAnh) / 2;

                double toaDoY = pageFormat.getImageableY()
                        + (chieuCaoVungIn - chieuCaoAnh) / 2;

                graphics2D.translate(toaDoX, toaDoY);

                graphics2D.scale(tiLe, tiLe);

                graphics2D.drawImage(
                        hinhHoaDon,
                        0,
                        0,
                        hinhHoaDon.getWidth(),
                        hinhHoaDon.getHeight(),
                        null);

                return Printable.PAGE_EXISTS;
            } finally {
                graphics2D.dispose();
            }
        };
    }
}