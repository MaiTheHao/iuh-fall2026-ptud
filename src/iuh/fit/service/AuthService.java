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

    private static final int DO_DAI_MAT_KHAU_TOI_THIEU = 8;

    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

    public NhanVien login(LoginDTO loginDTO) throws SQLException {
        if (loginDTO == null
                || loginDTO.tenDangNhap() == null || loginDTO.tenDangNhap().isBlank()
                || loginDTO.matKhau() == null || loginDTO.matKhau().isBlank()) {
            throw new CredentialsInvalidException();
        }
        NhanVien nhanVien = nhanVienDAO.login(loginDTO.tenDangNhap(), loginDTO.matKhau());
        if (nhanVien == null) {
            throw new CredentialsInvalidException();
        }
        AppContext.getInstance().setCurrentUser(nhanVien);
        return nhanVien;
    }

    public NhanVien register(RegisterDTO registerDTO) throws SQLException {
        if (registerDTO == null || registerDTO.tenDangNhap() == null || registerDTO.tenDangNhap().isBlank()) {
            throw new CredentialsInvalidException();
        }
        if (!hopLeMatKhau(registerDTO.matKhau())) {
            throw new PasswordInvalidException();
        }
        if (nhanVienDAO.findByTenDangNhap(registerDTO.tenDangNhap()) != null) {
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
        kiemTraTonTai(nhanVien);
        nhanVienDAO.update(nhanVien);
    }

    public void updateProfile(NhanVien nhanVien) throws SQLException {
        kiemTraTonTai(nhanVien);
        nhanVienDAO.updateProfile(nhanVien);
    }

    public void updatePassword(NhanVien nhanVien) throws SQLException {
        if (nhanVien == null || !hopLeMatKhau(nhanVien.getMatKhau())) {
            throw new PasswordInvalidException();
        }
        kiemTraTonTai(nhanVien);
        nhanVienDAO.updatePassword(nhanVien);
    }

    private void kiemTraTonTai(NhanVien nhanVien) throws SQLException {
        if (nhanVien == null || nhanVien.getMaNhanVien() == null
                || nhanVienDAO.findById(nhanVien.getMaNhanVien()) == null) {
            throw new UserNotFoundException();
        }
    }

    private boolean hopLeMatKhau(String matKhau) {
        return matKhau != null
                && matKhau.length() >= DO_DAI_MAT_KHAU_TOI_THIEU
                && matKhau.matches(".*[A-Za-z].*")
                && matKhau.matches(".*\\d.*")
                && matKhau.matches(".*[^A-Za-z0-9\\s].*");
    }
}
