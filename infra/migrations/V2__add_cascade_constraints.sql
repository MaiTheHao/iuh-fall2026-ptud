-- ============================================================
-- MIGRATION: add ON DELETE CASCADE to every foreign key
-- Applied on top of V0..V2; ON UPDATE is intentionally omitted
-- because primary keys are immutable business codes.
-- ============================================================

-- ============================================================
-- TABLE: KhachHang_Xe
-- ============================================================
ALTER TABLE KhachHang_Xe DROP CONSTRAINT FK_KhachHang_Xe_KhachHang;
ALTER TABLE KhachHang_Xe ADD CONSTRAINT FK_KhachHang_Xe_KhachHang FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang) ON DELETE CASCADE;

ALTER TABLE KhachHang_Xe DROP CONSTRAINT FK_KhachHang_Xe_Xe;
ALTER TABLE KhachHang_Xe ADD CONSTRAINT FK_KhachHang_Xe_Xe FOREIGN KEY (bienSo) REFERENCES Xe(bienSo) ON DELETE CASCADE;

-- ============================================================
-- TABLE: PhieuNhapPhuTung
-- ============================================================
ALTER TABLE PhieuNhapPhuTung DROP CONSTRAINT FK_PhieuNhap_NhanVien;
ALTER TABLE PhieuNhapPhuTung ADD CONSTRAINT FK_PhieuNhap_NhanVien FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien) ON DELETE CASCADE;

-- ============================================================
-- TABLE: ChiTietPhieuNhapPhuTung
-- ============================================================
ALTER TABLE ChiTietPhieuNhapPhuTung DROP CONSTRAINT FK_ChiTietPhieuNhap_PhieuNhap;
ALTER TABLE ChiTietPhieuNhapPhuTung ADD CONSTRAINT FK_ChiTietPhieuNhap_PhieuNhap FOREIGN KEY (maPhieuNhap) REFERENCES PhieuNhapPhuTung(maPhieuNhap) ON DELETE CASCADE;

ALTER TABLE ChiTietPhieuNhapPhuTung DROP CONSTRAINT FK_ChiTietPhieuNhap_PhuTung;
ALTER TABLE ChiTietPhieuNhapPhuTung ADD CONSTRAINT FK_ChiTietPhieuNhap_PhuTung FOREIGN KEY (maPhuTung) REFERENCES PhuTung(maPhuTung) ON DELETE CASCADE;

-- ============================================================
-- TABLE: PhieuTiepNhan
-- ============================================================
ALTER TABLE PhieuTiepNhan DROP CONSTRAINT FK_PhieuTiepNhan_NhanVien;
ALTER TABLE PhieuTiepNhan ADD CONSTRAINT FK_PhieuTiepNhan_NhanVien FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien) ON DELETE CASCADE;

ALTER TABLE PhieuTiepNhan DROP CONSTRAINT FK_PhieuTiepNhan_KhachHang;
ALTER TABLE PhieuTiepNhan ADD CONSTRAINT FK_PhieuTiepNhan_KhachHang FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang) ON DELETE CASCADE;

ALTER TABLE PhieuTiepNhan DROP CONSTRAINT FK_PhieuTiepNhan_Xe;
ALTER TABLE PhieuTiepNhan ADD CONSTRAINT FK_PhieuTiepNhan_Xe FOREIGN KEY (bienSo) REFERENCES Xe(bienSo) ON DELETE CASCADE;

-- ============================================================
-- TABLE: PhanCong
-- ============================================================
ALTER TABLE PhanCong DROP CONSTRAINT FK_PhanCong_NhanVien;
ALTER TABLE PhanCong ADD CONSTRAINT FK_PhanCong_NhanVien FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien) ON DELETE CASCADE;

-- ============================================================
-- TABLE: HoaDon
-- ============================================================
ALTER TABLE HoaDon DROP CONSTRAINT FK_HoaDon_PhieuTiepNhan;
ALTER TABLE HoaDon ADD CONSTRAINT FK_HoaDon_PhieuTiepNhan FOREIGN KEY (maPhieuTiepNhan) REFERENCES PhieuTiepNhan(maPhieuTiepNhan) ON DELETE CASCADE;

ALTER TABLE HoaDon DROP CONSTRAINT FK_HoaDon_Voucher;
ALTER TABLE HoaDon ADD CONSTRAINT FK_HoaDon_Voucher FOREIGN KEY (maVoucher) REFERENCES Voucher(maVoucher) ON DELETE CASCADE;

-- ============================================================
-- TABLE: ChiTietHoaDon
-- ============================================================
ALTER TABLE ChiTietHoaDon DROP CONSTRAINT FK_ChiTietHoaDon_HoaDon;
ALTER TABLE ChiTietHoaDon ADD CONSTRAINT FK_ChiTietHoaDon_HoaDon FOREIGN KEY (maHoaDon) REFERENCES HoaDon(maHoaDon) ON DELETE CASCADE;

-- ============================================================
-- TABLE: VanDeKyThuat
-- ============================================================
ALTER TABLE VanDeKyThuat DROP CONSTRAINT FK_VanDeKyThuat_PhieuTiepNhan;
ALTER TABLE VanDeKyThuat ADD CONSTRAINT FK_VanDeKyThuat_PhieuTiepNhan FOREIGN KEY (maPhieuTiepNhan) REFERENCES PhieuTiepNhan(maPhieuTiepNhan) ON DELETE CASCADE;
