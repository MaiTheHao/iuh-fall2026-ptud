package iuh.fit.service;

import iuh.fit.dao.DayMetricDAO;
import iuh.fit.dao.TransactionScope;
import iuh.fit.enumeration.LoaiMa;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bộ đếm số thứ tự theo ngày và số liệu nhanh cho dashboard.
 * Nguồn sự thật là bảng DayMetric; RAM giữ số hiện tại để đọc tức thời.
 * Việc định dạng mã nằm ở MaService.
 */
public final class DayMetricService {

    public static final ZoneId MUI_GIO = ZoneId.of("Asia/Ho_Chi_Minh");

    private static final DayMetricService INSTANCE = new DayMetricService();

    private final DayMetricDAO dayMetricDAO = new DayMetricDAO();
    private final Map<LoaiMa, Long> soHienTai = new ConcurrentHashMap<>();

    private DayMetricService() {
    }

    public static DayMetricService getInstance() {
        return INSTANCE;
    }

    public void nap() throws SQLException {
        Map<LoaiMa, Long> soTrongNgay = this.dayMetricDAO.taiTheoNgay(LocalDate.now(MUI_GIO));
        this.soHienTai.clear();
        this.soHienTai.putAll(soTrongNgay);
    }

    public long tiepTheo(TransactionScope scope, LoaiMa loai, LocalDate ngay) throws SQLException {
        long so = this.dayMetricDAO.tiepTheo(scope, ngay, loai);
        this.soHienTai.put(loai, so);
        return so;
    }

    public long tiepTheo(LoaiMa loai, LocalDate ngay) throws SQLException {
        long so = this.dayMetricDAO.tiepTheo(ngay, loai);
        this.soHienTai.put(loai, so);
        return so;
    }

    public long dem(LoaiMa loai) {
        Long so = this.soHienTai.get(loai);
        return so == null ? 0L : so;
    }

    public Map<LoaiMa, Long> demTatCa() {
        return Map.copyOf(this.soHienTai);
    }
}
