-- ============================================================
-- V15: Bang ThanhToan (mot hoa don mot lan thu)
-- LY DO: ghi nhan moi lan thu tien thanh mot dong, doi trang
-- thai hoa don va insert thanh toan trong cung mot transaction.
-- PHAM VI: tao moi bang ThanhToan; khong cham cac bang cu.
-- ROLLBACK (thu cong):
--   DROP INDEX IF EXISTS IX_ThanhToan_thoiGian ON ThanhToan;
--   ALTER TABLE ThanhToan DROP CONSTRAINT IF EXISTS FK_ThanhToan_HoaDon;
--   ALTER TABLE ThanhToan DROP CONSTRAINT IF EXISTS FK_ThanhToan_NhanVien;
--   DROP TABLE IF EXISTS ThanhToan;
-- CANH BAO: V15 la apply-once: khong sua file nay sau khi da apply.
-- ============================================================
SET XACT_ABORT ON;
BEGIN TRANSACTION;

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = N'ThanhToan')
BEGIN
    CREATE TABLE ThanhToan (
        maThanhToan varchar(20) NOT NULL,
        maHoaDon varchar(20) NOT NULL,
        phuongThuc varchar(20) NOT NULL,
        soTien decimal(18,2) NOT NULL,
        thoiGian datetime2(3) NOT NULL,
        maNhanVien varchar(64) NULL,
        ghiChu nvarchar(255) NULL,
        CONSTRAINT PK_ThanhToan PRIMARY KEY (maThanhToan),
        CONSTRAINT UQ_ThanhToan_maHoaDon UNIQUE (maHoaDon),
        CONSTRAINT FK_ThanhToan_HoaDon FOREIGN KEY (maHoaDon) REFERENCES HoaDon(maHoaDon),
        CONSTRAINT FK_ThanhToan_NhanVien FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien),
        CONSTRAINT CK_ThanhToan_phuongThuc CHECK (phuongThuc IN ('CHUYEN_KHOAN', 'TIEN_MAT')),
        CONSTRAINT CK_ThanhToan_soTien CHECK (soTien >= 0)
    );
END

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_ThanhToan_thoiGian' AND object_id = OBJECT_ID(N'ThanhToan'))
BEGIN
    CREATE INDEX IX_ThanhToan_thoiGian ON ThanhToan(thoiGian);
END

COMMIT TRANSACTION;
