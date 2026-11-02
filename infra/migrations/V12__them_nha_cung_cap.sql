-- ============================================================
-- V12: Them danh muc NhaCungCap + cot PhuTung.maNhaCungCap
-- LY DO: moi phu tung gan dung mot NCC; can bang danh muc + khoa ngoai (mac dinh NO ACTION).
-- PHAM VI: chay tren DB trong (hoac bang PhuTung dang rong); khong backfill du lieu cu.
-- ROLLBACK (thu cong, theo dung thu tu):
--   ALTER TABLE PhuTung DROP CONSTRAINT IF EXISTS FK_PhuTung_NhaCungCap;
--   DROP INDEX IF EXISTS IX_PhuTung_maNhaCungCap ON PhuTung;
--   ALTER TABLE PhuTung DROP COLUMN IF EXISTS maNhaCungCap;
--   DROP TABLE IF EXISTS NhaCungCap;
-- CANH BAO: rollback chi ha schema, KHONG khoi phuc init.sql / mock.sql.
--   V12 la apply-once: khong sua file nay sau khi da apply.
-- ============================================================
SET XACT_ABORT ON;
BEGIN TRANSACTION;

CREATE TABLE NhaCungCap (
    maNhaCungCap varchar(20) NOT NULL,
    tenNhaCungCap nvarchar(100) NOT NULL,
    sdt varchar(11) NOT NULL,
    email varchar(100) NULL,
    diaChi nvarchar(255) NULL,
    trangThai varchar(20) NOT NULL,
    ghiChu nvarchar(500) NULL,
    ngayTao datetime2(3) NOT NULL CONSTRAINT DF_NhaCungCap_ngayTao DEFAULT sysdatetime(),
    CONSTRAINT PK_NhaCungCap PRIMARY KEY (maNhaCungCap),
    CONSTRAINT UQ_NhaCungCap_sdt UNIQUE (sdt),
    CONSTRAINT CK_NhaCungCap_trangThai CHECK (trangThai IN ('HOAT_DONG', 'NGUNG_HOP_TAC'))
);

ALTER TABLE PhuTung ADD maNhaCungCap varchar(20) NULL;
ALTER TABLE PhuTung ALTER COLUMN maNhaCungCap varchar(20) NOT NULL;

ALTER TABLE PhuTung ADD CONSTRAINT FK_PhuTung_NhaCungCap FOREIGN KEY (maNhaCungCap) REFERENCES NhaCungCap(maNhaCungCap);

CREATE INDEX IX_PhuTung_maNhaCungCap ON PhuTung(maNhaCungCap);

COMMIT TRANSACTION;
