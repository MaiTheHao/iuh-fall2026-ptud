package iuh.fit.service;

import iuh.fit.core.context.AppContext;
import iuh.fit.dao.NhanVienDAO;
import iuh.fit.dto.LoginDTO;
import iuh.fit.dto.RegisterDTO;
import iuh.fit.entity.NhanVien;
import iuh.fit.enumeration.TrangThaiNhanVien;
import iuh.fit.exception.CredentialsInvalidException;
import iuh.fit.exception.PasswordInvalidException;
import iuh.fit.exception.PhoneInvalidException;
import iuh.fit.exception.UserNameExistsException;
import iuh.fit.exception.UserNotFoundException;
import iuh.fit.util.MaGeneratorUtil;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class AuthService {

    private static final int MIN_PASSWORD_LENGTH = 8;

    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

    public NhanVien login(LoginDTO loginDTO) throws SQLException {
        if (loginDTO == null
                || loginDTO.tenDangNhap() == null || loginDTO.tenDangNhap().isBlank()
                || loginDTO.matKhau() == null || loginDTO.matKhau().isBlank()) {
            throw new CredentialsInvalidException();
        }
        NhanVien nhanVien = nhanVienDAO.login(loginDTO.tenDangNhap(), loginDTO.matKhau())
                .orElseThrow(CredentialsInvalidException::new);
        AppContext.getInstance().setCurrentUser(nhanVien);
        return nhanVien;
    }

    public NhanVien register(RegisterDTO registerDTO) throws SQLException {
        if (registerDTO == null || registerDTO.tenDangNhap() == null || registerDTO.tenDangNhap().isBlank()) {
            throw new CredentialsInvalidException();
        }
        if (!this.isValidPassword(registerDTO.matKhau())) {
            throw new PasswordInvalidException();
        }
        if (nhanVienDAO.findByTenDangNhap(registerDTO.tenDangNhap()).isPresent()) {
            throw new UserNameExistsException();
        }

        NhanVien nhanVien = new NhanVien();
        String soDienThoai = registerDTO.soDienThoai();
        if (soDienThoai == null || !soDienThoai.trim().matches("\\d{9,10}")) {
            throw new PhoneInvalidException();
        }
        LocalDateTime now = LocalDateTime.now();
        nhanVien.setMaNhanVien(MaGeneratorUtil.sinhMaNhanVien(soDienThoai, now));
        nhanVien.setTenDangNhap(registerDTO.tenDangNhap());
        nhanVien.setMatKhau(registerDTO.matKhau());
        nhanVien.setHoTen(registerDTO.hoTen());
        nhanVien.setSoDienThoai(soDienThoai.trim());
        nhanVien.setAnhDaiDien(null);
        nhanVien.setVaiTro(registerDTO.vaiTro());
        nhanVien.setTrangThai(TrangThaiNhanVien.HOAT_DONG);
        nhanVien.setNgayTao(now);
        nhanVienDAO.register(nhanVien);
        return nhanVien;
    }

    public void update(NhanVien nhanVien) throws SQLException {
        this.requireExisting(nhanVien);
        nhanVienDAO.update(nhanVien);
    }

    public void updateProfile(NhanVien nhanVien) throws SQLException {
        this.requireExisting(nhanVien);
        nhanVienDAO.updateProfile(nhanVien);
    }

    public void updatePassword(String matKhauHienTai, String matKhauMoi, String xacNhan) throws SQLException {
        NhanVien nguoiDung = AppContext.getInstance().getCurrentUser();
        if (nguoiDung == null || nguoiDung.getMaNhanVien() == null || nguoiDung.getMaNhanVien().isBlank()
                || nguoiDung.getTenDangNhap() == null || nguoiDung.getTenDangNhap().isBlank()) {
            throw new UserNotFoundException("app.common.notLoggedIn");
        }
        if (matKhauHienTai == null || matKhauHienTai.isBlank()) {
            throw new CredentialsInvalidException("app.doiMatKhau.loiMatKhauHienTai");
        }

        this.nhanVienDAO.login(nguoiDung.getTenDangNhap(), matKhauHienTai)
                .orElseThrow(() -> new CredentialsInvalidException("app.doiMatKhau.loiMatKhauHienTai"));

        if (matKhauMoi == null || !matKhauMoi.equals(xacNhan)) {
            throw new PasswordInvalidException("app.doiMatKhau.loiKhongTrung");
        }
        if (!this.isValidPassword(matKhauMoi)) {
            throw new PasswordInvalidException("app.doiMatKhau.loiDoManh");
        }

        NhanVien capNhat = new NhanVien();
        capNhat.setMaNhanVien(nguoiDung.getMaNhanVien());
        capNhat.setMatKhau(matKhauMoi);
        this.nhanVienDAO.updatePassword(capNhat);

        nguoiDung.setMatKhau(matKhauMoi);
    }

    private void requireExisting(NhanVien nhanVien) throws SQLException {
        if (nhanVien == null || nhanVien.getMaNhanVien() == null
                || nhanVienDAO.findById(nhanVien.getMaNhanVien()).isEmpty()) {
            throw new UserNotFoundException();
        }
    }

    private boolean isValidPassword(String matKhau) {
        return matKhau != null
                && matKhau.length() >= MIN_PASSWORD_LENGTH
                && matKhau.matches(".*[A-Za-z].*")
                && matKhau.matches(".*\\d.*")
                && matKhau.matches(".*[^A-Za-z0-9\\s].*");
    }
}
