package iuh.fit.gui.component;

import java.util.function.Consumer;

/**
 * Mô tả một nút thao tác dùng trong cột thao tác của bảng.
 */
public record ThaoTac(
        String iconName,
        String tooltipKey,
        KieuThaoTac kieu,
        Consumer<Integer> khiChon) {

    public enum KieuThaoTac {
        THUONG,
        NGUY_HIEM
    }
}
