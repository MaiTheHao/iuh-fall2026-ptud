-- ============================================================
-- MIGRATION: widen business-code columns to varchar(64) and
--            make NhanVien.soDienThoai mandatory
-- Applied on top of V0..V4.
--
-- PRE-CHECK PREREQUISITE / ROLLBACK WARNING: rolling back to
-- varchar(20) will FAIL once any staff or slip code longer than
-- 20 characters has been stored — there is no valid path back to
-- the narrower type in that case.
--
-- SQL Server (error 5074) forbids ALTER COLUMN while a foreign key
-- references the column, so every referencing FK is dropped first
-- and recreated afterwards with its original definition. The
-- soDienThoai backfill must run before the ALTER ... NOT NULL,
-- otherwise the NOT NULL change fails on existing NULL rows.
-- ============================================================

-- ============================================================
-- STEP 1: backfill NhanVien.soDienThoai, then make it NOT NULL
-- ============================================================
UPDATE NhanVien
SET soDienThoai = RIGHT('0000000000' + CAST(t.rn AS varchar(10)), 10)
FROM (
    SELECT maNhanVien, ROW_NUMBER() OVER (ORDER BY maNhanVien) AS rn
    FROM NhanVien
    WHERE soDienThoai IS NULL
) AS t
WHERE NhanVien.maNhanVien = t.maNhanVien;

ALTER TABLE NhanVien ALTER COLUMN soDienThoai varchar(10) NOT NULL;

-- ============================================================
-- STEP 2: drop the six foreign keys referencing NhanVien and PhieuTiepNhan
-- ============================================================
ALTER TABLE PhieuNhapPhuTung DROP CONSTRAINT FK_PhieuNhap_NhanVien;
ALTER TABLE PhieuTiepNhan DROP CONSTRAINT FK_PhieuTiepNhan_NhanVien;
ALTER TABLE PhanCong DROP CONSTRAINT FK_PhanCong_NhanVien;
ALTER TABLE HoaDon DROP CONSTRAINT FK_HoaDon_PhieuTiepNhan;
ALTER TABLE VanDeKyThuat DROP CONSTRAINT FK_VanDeKyThuat_PhieuTiepNhan;
ALTER TABLE KhieuNai DROP CONSTRAINT FK_KhieuNai_PhieuTiepNhan;

-- ============================================================
-- STEP 3: widen the eight code columns to varchar(64), preserving NOT NULL
-- ============================================================
ALTER TABLE NhanVien ALTER COLUMN maNhanVien varchar(64) NOT NULL;
ALTER TABLE PhieuNhapPhuTung ALTER COLUMN maNhanVien varchar(64) NOT NULL;
ALTER TABLE PhieuTiepNhan ALTER COLUMN maNhanVien varchar(64) NOT NULL;
ALTER TABLE PhanCong ALTER COLUMN maNhanVien varchar(64) NOT NULL;
ALTER TABLE PhieuTiepNhan ALTER COLUMN maPhieuTiepNhan varchar(64) NOT NULL;
ALTER TABLE HoaDon ALTER COLUMN maPhieuTiepNhan varchar(64) NOT NULL;
ALTER TABLE VanDeKyThuat ALTER COLUMN maPhieuTiepNhan varchar(64) NOT NULL;
ALTER TABLE KhieuNai ALTER COLUMN maPhieuTiepNhan varchar(64) NOT NULL;

-- ============================================================
-- STEP 4: recreate the six foreign keys with their original definitions
-- ============================================================
ALTER TABLE PhieuNhapPhuTung ADD CONSTRAINT FK_PhieuNhap_NhanVien FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien) ON DELETE CASCADE;
ALTER TABLE PhieuTiepNhan ADD CONSTRAINT FK_PhieuTiepNhan_NhanVien FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien) ON DELETE CASCADE;
ALTER TABLE PhanCong ADD CONSTRAINT FK_PhanCong_NhanVien FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien) ON DELETE CASCADE;
ALTER TABLE HoaDon ADD CONSTRAINT FK_HoaDon_PhieuTiepNhan FOREIGN KEY (maPhieuTiepNhan) REFERENCES PhieuTiepNhan(maPhieuTiepNhan) ON DELETE CASCADE;
ALTER TABLE VanDeKyThuat ADD CONSTRAINT FK_VanDeKyThuat_PhieuTiepNhan FOREIGN KEY (maPhieuTiepNhan) REFERENCES PhieuTiepNhan(maPhieuTiepNhan) ON DELETE CASCADE;
ALTER TABLE KhieuNai ADD CONSTRAINT FK_KhieuNai_PhieuTiepNhan FOREIGN KEY (maPhieuTiepNhan) REFERENCES PhieuTiepNhan(maPhieuTiepNhan);
