package iuh.fit.dto;

import java.util.List;

/**
 * Kết quả phân trang: dữ liệu của một trang kèm thông tin để dựng điều hướng.
 */
public record PaginatedResult<T>(
        List<T> items,
        int page,
        int pageSize,
        long totalItems) {

    public static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * Page size cho màn hình cần tải toàn bộ kết quả (dialog chọn, combo đầy đủ).
     * Luôn dùng kèm page = 1; trần 10_000 chỉ để giới hạn số dòng dựng trên UI.
     */
    public static final int PAGE_SIZE_TAI_TOAN_BO = 10_000;

    public static <T> PaginatedResult<T> of(List<T> items, int page, int pageSize) {
        int size = normalizePageSize(pageSize);
        int totalPages = totalPages(items.size(), size);
        int currentPage = clampPage(page, totalPages);
        int from = (currentPage - 1) * size;
        int to = Math.min(from + size, items.size());
        return new PaginatedResult<>(List.copyOf(items.subList(from, to)), currentPage, size, items.size());
    }

    public static <T> PaginatedResult<T> of(List<T> items, DanhSachCriteria criteria) {
        return of(items, criteria.page(), criteria.pageSize());
    }

    public static int totalPages(long totalItems, int pageSize) {
        int size = normalizePageSize(pageSize);
        return (int) Math.ceil((double) totalItems / size);
    }

    public static int clampPage(int page, int totalPages) {
        return Math.min(Math.max(page, 1), Math.max(totalPages, 1));
    }

    private static int normalizePageSize(int pageSize) {
        return pageSize <= 0 ? DEFAULT_PAGE_SIZE : pageSize;
    }

    public int totalPages() {
        return totalPages(this.totalItems, this.pageSize);
    }

    public boolean hasPrevious() {
        return this.page > 1;
    }

    public boolean hasNext() {
        return this.page < this.totalPages();
    }

    public int fromIndex() {
        if (this.totalItems == 0) {
            return 0;
        }
        return (this.page - 1) * this.pageSize + 1;
    }

    public int toIndex() {
        return (int) Math.min((long) this.page * this.pageSize, this.totalItems);
    }
}
