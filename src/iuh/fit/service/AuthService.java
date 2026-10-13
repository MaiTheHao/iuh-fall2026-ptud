package iuh.fit.service;

import iuh.fit.core.context.AppContext;
import iuh.fit.dao.NhanVienDAO;
import iuh.fit.dto.LoginDTO;
import iuh.fit.dto.RegisterDTO;
import iuh.fit.entity.NhanVien;
import iuh.fit.enumeration.TrangThaiNhanVien;
import iuh.fit.exception.CredentialsInvalidException;
import iuh.fit.exception.PasswordInvalidException;
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
        nhanVien.setMaNhanVien(MaGeneratorUtil.genMaNhanVien(registerDTO.soDienThoai()));
        nhanVien.setTenDangNhap(registerDTO.tenDangNhap());
        nhanVien.setMatKhau(registerDTO.matKhau());
        nhanVien.setHoTen(registerDTO.hoTen());
        nhanVien.setSoDienThoai(registerDTO.soDienThoai());
        nhanVien.setAnhDaiDien(null);
        nhanVien.setVaiTro(registerDTO.vaiTro());
        nhanVien.setTrangThai(TrangThaiNhanVien.HOAT_DONG);
        nhanVien.setNgayTao(LocalDateTime.now());
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

    public void updatePassword(NhanVien nhanVien) throws SQLException {
        if (nhanVien == null || !this.isValidPassword(nhanVien.getMatKhau())) {
            throw new PasswordInvalidException();
        }
        this.requireExisting(nhanVien);
        nhanVienDAO.updatePassword(nhanVien);
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
