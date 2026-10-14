-- ============================================================
-- MIGRATION: add KhieuNai (customer complaint)
-- ============================================================

-- ============================================================
-- TABLE: KhieuNai
-- ============================================================
CREATE TABLE KhieuNai (
    maKhieuNai       varchar(20)     NOT NULL,
    maKhachHang      varchar(20)     NOT NULL,
    maPhieuTiepNhan  varchar(20)     NOT NULL,
    noiDung          nvarchar(1000) NOT NULL,
    trangThai        varchar(50)     NOT NULL,
    ngayTao          datetime2(3) DEFAULT sysdatetime() NOT NULL,
    CONSTRAINT PK_KhieuNai PRIMARY KEY (maKhieuNai),
    -- maKhachHang CASCADE so deleting a customer removes their complaints.
    -- maPhieuTiepNhan is intentionally left NO ACTION: PhieuTiepNhan is
    -- already ON DELETE CASCADE from KhachHang, so cascading this FK too
    -- would create a second cascade path (SQL Server error 1785).
    CONSTRAINT FK_KhieuNai_KhachHang      FOREIGN KEY (maKhachHang)     REFERENCES KhachHang(maKhachHang) ON DELETE CASCADE,
    CONSTRAINT FK_KhieuNai_PhieuTiepNhan  FOREIGN KEY (maPhieuTiepNhan) REFERENCES PhieuTiepNhan(maPhieuTiepNhan)
);
