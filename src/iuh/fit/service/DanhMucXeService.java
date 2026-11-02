package iuh.fit.service;

import iuh.fit.dao.DoiXeDAO;
import iuh.fit.dao.DongXeDAO;
import iuh.fit.dao.HangXeDAO;
import iuh.fit.entity.DoiXe;
import iuh.fit.entity.DongXe;
import iuh.fit.entity.HangXe;
import iuh.fit.enumeration.LoaiMa;

import java.sql.SQLException;
import java.time.Year;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

public final class DanhMucXeService {

    private static final String ERROR_TEN = "app.danhMucXe.loiTen";
    private static final String ERROR_TEN_QUA_DAI = "app.danhMucXe.loiTenQuaDai";
    private static final String ERROR_NAM_SX = "app.danhMucXe.loiNamSX";
    private static final String ERROR_KHONG_TIM_THAY = "app.danhMucXe.khongTimThay";
    private static final String ERROR_DANG_SU_DUNG = "app.danhMucXe.loiDangSuDung";
    private static final String ERROR_DU_LIEU = "app.danhMucXe.loiDuLieu";
    private static final String ERROR_TRUNG_TEN = "app.danhMucXe.loiTrungTen";

    public static final int NAM_SX_NHO_NHAT = 1886;

    public static int namSxLonNhat() {
        return Year.now(ZoneId.of("Asia/Ho_Chi_Minh")).getValue() + 1;
    }

    private final HangXeDAO hangXeDAO;
    private final DongXeDAO dongXeDAO;
    private final DoiXeDAO doiXeDAO;
    private final MaService maService = new MaService();

    public DanhMucXeService() {
        this(new HangXeDAO(), new DongXeDAO(), new DoiXeDAO());
    }

    public DanhMucXeService(HangXeDAO hangXeDAO, DongXeDAO dongXeDAO, DoiXeDAO doiXeDAO) {
        this.hangXeDAO = Objects.requireNonNull(hangXeDAO);
        this.dongXeDAO = Objects.requireNonNull(dongXeDAO);
        this.doiXeDAO = Objects.requireNonNull(doiXeDAO);
    }

    public List<HangXe> getDanhSachHangXe() throws SQLException {
        return hangXeDAO.findAll();
    }

    public List<DongXe> getDanhSachDongXe(String maHangXe) throws SQLException {
        if (maHangXe == null || maHangXe.isBlank()) {
            return dongXeDAO.findAll();
        }
        return dongXeDAO.findByHang(maHangXe);
    }

    public List<DoiXe> getDanhSachDoiXe(String maDongXe) throws SQLException {
        if (maDongXe == null || maDongXe.isBlank()) {
            return doiXeDAO.findAll();
        }
        return doiXeDAO.findByDong(maDongXe);
    }

    public HangXe getHangXe(String maHangXe) throws SQLException {
        if (maHangXe == null || maHangXe.isBlank()) {
            throw new IllegalArgumentException(ERROR_KHONG_TIM_THAY);
        }
        return hangXeDAO.findById(maHangXe)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_KHONG_TIM_THAY));
    }

    public DongXe getDongXe(String maDongXe) throws SQLException {
        if (maDongXe == null || maDongXe.isBlank()) {
            throw new IllegalArgumentException(ERROR_KHONG_TIM_THAY);
        }
        return dongXeDAO.findById(maDongXe)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_KHONG_TIM_THAY));
    }

    public DoiXe getDoiXe(String maDoiXe) throws SQLException {
        if (maDoiXe == null || maDoiXe.isBlank()) {
            throw new IllegalArgumentException(ERROR_KHONG_TIM_THAY);
        }
        return doiXeDAO.findById(maDoiXe)
                .orElseThrow(() -> new IllegalArgumentException(ERROR_KHONG_TIM_THAY));
    }

    public HangXe taoHangXe(String ten) throws SQLException {
        if (ten == null) {
            throw new IllegalArgumentException(ERROR_TEN);
        }
        ten = ten.trim();
        if (ten.isEmpty()) {
            throw new IllegalArgumentException(ERROR_TEN);
        }
        if (ten.length() > 100) {
            throw new IllegalArgumentException(ERROR_TEN_QUA_DAI);
        }
        if (hangXeDAO.existsByTen(ten)) {
            throw new IllegalArgumentException(ERROR_TRUNG_TEN);
        }
        HangXe hangXe = new HangXe(maService.genMa(LoaiMa.HX), ten);
        hangXeDAO.insert(hangXe);
        return hangXe;
    }

    public void capNhatHangXe(HangXe hangXe) throws SQLException {
        if (hangXe == null || hangXe.getMaHangXe() == null || hangXe.getMaHangXe().isBlank()
                || hangXeDAO.findById(hangXe.getMaHangXe()).isEmpty()) {
            throw new IllegalArgumentException(ERROR_KHONG_TIM_THAY);
        }
        if (hangXe.getTen() == null) {
            throw new IllegalArgumentException(ERROR_TEN);
        }
        String ten = hangXe.getTen().trim();
        if (ten.isEmpty()) {
            throw new IllegalArgumentException(ERROR_TEN);
        }
        if (ten.length() > 100) {
            throw new IllegalArgumentException(ERROR_TEN_QUA_DAI);
        }
        for (HangXe other : hangXeDAO.findAll()) {
            if (!other.getMaHangXe().equals(hangXe.getMaHangXe()) && other.getTen().equals(ten)) {
                throw new IllegalArgumentException(ERROR_TRUNG_TEN);
            }
        }
        hangXeDAO.update(new HangXe(hangXe.getMaHangXe(), ten));
    }

    public void xoaHangXe(String maHangXe) throws SQLException {
        if (maHangXe == null || maHangXe.isBlank() || hangXeDAO.findById(maHangXe).isEmpty()) {
            throw new IllegalArgumentException(ERROR_KHONG_TIM_THAY);
        }
        if (dongXeDAO.existsByHang(maHangXe)) {
            throw new IllegalArgumentException(ERROR_DANG_SU_DUNG);
        }
        hangXeDAO.delete(maHangXe);
    }

    public DongXe taoDongXe(String maHangXe, String ten) throws SQLException {
        if (maHangXe == null || maHangXe.isBlank() || hangXeDAO.findById(maHangXe).isEmpty()) {
            throw new IllegalArgumentException(ERROR_KHONG_TIM_THAY);
        }
        if (ten == null) {
            throw new IllegalArgumentException(ERROR_TEN);
        }
        ten = ten.trim();
        if (ten.isEmpty()) {
            throw new IllegalArgumentException(ERROR_TEN);
        }
        if (ten.length() > 100) {
            throw new IllegalArgumentException(ERROR_TEN_QUA_DAI);
        }
        if (dongXeDAO.existsByHangAndTen(maHangXe, ten)) {
            throw new IllegalArgumentException(ERROR_TRUNG_TEN);
        }
        DongXe dongXe = new DongXe(maService.genMa(LoaiMa.DX), maHangXe, ten, null);
        dongXeDAO.insert(dongXe);
        return dongXe;
    }

    public void capNhatDongXe(DongXe dongXe) throws SQLException {
        if (dongXe == null || dongXe.getMaDongXe() == null || dongXe.getMaDongXe().isBlank()
                || dongXeDAO.findById(dongXe.getMaDongXe()).isEmpty()) {
            throw new IllegalArgumentException(ERROR_KHONG_TIM_THAY);
        }
        if (dongXe.getMaHangXe() == null || dongXe.getMaHangXe().isBlank()
                || hangXeDAO.findById(dongXe.getMaHangXe()).isEmpty()) {
            throw new IllegalArgumentException(ERROR_KHONG_TIM_THAY);
        }
        if (dongXe.getTen() == null) {
            throw new IllegalArgumentException(ERROR_TEN);
        }
        String ten = dongXe.getTen().trim();
        if (ten.isEmpty()) {
            throw new IllegalArgumentException(ERROR_TEN);
        }
        if (ten.length() > 100) {
            throw new IllegalArgumentException(ERROR_TEN_QUA_DAI);
        }
        for (DongXe other : dongXeDAO.findByHang(dongXe.getMaHangXe())) {
            if (!other.getMaDongXe().equals(dongXe.getMaDongXe()) && other.getTen().equals(ten)) {
                throw new IllegalArgumentException(ERROR_TRUNG_TEN);
            }
        }
        dongXeDAO.update(new DongXe(dongXe.getMaDongXe(), dongXe.getMaHangXe(), ten, null));
    }

    public void xoaDongXe(String maDongXe) throws SQLException {
        if (maDongXe == null || maDongXe.isBlank() || dongXeDAO.findById(maDongXe).isEmpty()) {
            throw new IllegalArgumentException(ERROR_KHONG_TIM_THAY);
        }
        if (doiXeDAO.existsByDong(maDongXe)) {
            throw new IllegalArgumentException(ERROR_DANG_SU_DUNG);
        }
        dongXeDAO.delete(maDongXe);
    }

    public DoiXe taoDoiXe(String maDongXe, int namSX) throws SQLException {
        if (maDongXe == null || maDongXe.isBlank() || dongXeDAO.findById(maDongXe).isEmpty()) {
            throw new IllegalArgumentException(ERROR_KHONG_TIM_THAY);
        }
        int maxNam = namSxLonNhat();
        if (namSX < NAM_SX_NHO_NHAT || namSX > maxNam) {
            throw new IllegalArgumentException(ERROR_NAM_SX);
        }
        if (doiXeDAO.existsByDongAndNam(maDongXe, namSX)) {
            throw new IllegalArgumentException(ERROR_TRUNG_TEN);
        }
        DoiXe doiXe = new DoiXe(maService.genMa(LoaiMa.DO), maDongXe, namSX, null, null, null);
        doiXeDAO.insert(doiXe);
        return doiXe;
    }

    public void capNhatDoiXe(DoiXe doiXe) throws SQLException {
        if (doiXe == null || doiXe.getMaDoiXe() == null || doiXe.getMaDoiXe().isBlank()
                || doiXeDAO.findById(doiXe.getMaDoiXe()).isEmpty()) {
            throw new IllegalArgumentException(ERROR_KHONG_TIM_THAY);
        }
        if (doiXe.getMaDongXe() == null || doiXe.getMaDongXe().isBlank()
                || dongXeDAO.findById(doiXe.getMaDongXe()).isEmpty()) {
            throw new IllegalArgumentException(ERROR_KHONG_TIM_THAY);
        }
        int maxNam = namSxLonNhat();
        if (doiXe.getNamSX() < NAM_SX_NHO_NHAT || doiXe.getNamSX() > maxNam) {
            throw new IllegalArgumentException(ERROR_NAM_SX);
        }
        for (DoiXe other : doiXeDAO.findByDong(doiXe.getMaDongXe())) {
            if (!other.getMaDoiXe().equals(doiXe.getMaDoiXe()) && other.getNamSX() == doiXe.getNamSX()) {
                throw new IllegalArgumentException(ERROR_TRUNG_TEN);
            }
        }
        doiXeDAO.update(doiXe);
    }

    public void xoaDoiXe(String maDoiXe) throws SQLException {
        try {
            doiXeDAO.delete(maDoiXe);
        } catch (SQLException e) {
            throw new IllegalArgumentException(ERROR_DANG_SU_DUNG);
        }
    }

    public boolean existsDoiXe(String maDoiXe) throws SQLException {
        return doiXeDAO.existsById(maDoiXe);
    }
}
