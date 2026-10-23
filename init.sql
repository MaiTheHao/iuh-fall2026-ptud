-- ============================================================
-- TABLE: DichVu
-- ============================================================
CREATE TABLE DichVu (
    maDichVu    varchar(20)   NOT NULL,
    tenDichVu   nvarchar(100) NOT NULL,
    moTa        nvarchar(500) NULL,
    anhUrl      varchar(500)  NULL,
    donGia      decimal(18,2) NOT NULL,
    CONSTRAINT PK_DichVu PRIMARY KEY (maDichVu)
);

-- ============================================================
-- TABLE: KhachHang
-- ============================================================
CREATE TABLE KhachHang (
    maKhachHang  varchar(20)  NOT NULL,
    sdt          varchar(10)  NOT NULL,
    hoTen        nvarchar(50) NOT NULL,
    ngayTao      datetime2(3) DEFAULT sysdatetime() NOT NULL,
    CONSTRAINT PK_KhachHang PRIMARY KEY (maKhachHang),
    CONSTRAINT UQ_KhachHang_sdt UNIQUE (sdt)
);

-- ============================================================
-- TABLE: NhanVien
-- ============================================================
CREATE TABLE NhanVien (
    maNhanVien    varchar(64)  NOT NULL,
    tenDangNhap   varchar(30)  NOT NULL,
    matKhau       varchar(255) NOT NULL,
    hoTen         nvarchar(50) NOT NULL,
    anhDaiDien    varchar(500) NULL,
    soDienThoai varchar(10) NOT NULL,
    vaiTro        varchar(50)  NOT NULL,
    trangThai     varchar(50)  NOT NULL,
    ngayTao       datetime2(3) DEFAULT sysdatetime() NOT NULL,
    CONSTRAINT PK_NhanVien PRIMARY KEY (maNhanVien)
);

-- ============================================================
-- TABLE: PhuTung
-- ============================================================
CREATE TABLE PhuTung (
    maPhuTung    varchar(20)   NOT NULL,
    tenPhuTung   nvarchar(100) NOT NULL,
    moTa         nvarchar(500) NULL,
    anhUrl       varchar(500)  NULL,
    donGiaNhap   decimal(18,2) NOT NULL,
    soLuongTon   int DEFAULT 0 NOT NULL,
    CONSTRAINT PK_PhuTung PRIMARY KEY (maPhuTung)
);

-- ============================================================
-- TABLE: Voucher
-- ============================================================
CREATE TABLE Voucher (
    maVoucher          varchar(20)   NOT NULL,
    tenChuongTrinh     nvarchar(100) NOT NULL,
    moTa               nvarchar(500) NULL,
    mucGiam            decimal(5,4)  NOT NULL,
    thoiHanApDung      datetime2(3)  NOT NULL,
    trangThaiKichHoat  bit DEFAULT 1  NOT NULL,
    CONSTRAINT PK_Voucher PRIMARY KEY (maVoucher)
);

-- ============================================================
-- TABLE: Xe
-- ============================================================
CREATE TABLE Xe (
    bienSo    varchar(15)  NOT NULL,
    hangXe    nvarchar(50) NOT NULL,
    dongXe    nvarchar(50) NOT NULL,
    doiXe     int          NOT NULL,
    odo       int DEFAULT 0 NOT NULL,
    CONSTRAINT PK_Xe PRIMARY KEY (bienSo)
);

-- ============================================================
-- TABLE: KhachHang_Xe
-- ============================================================
CREATE TABLE KhachHang_Xe (
    maKhachHang  varchar(20) NOT NULL,
    bienSo       varchar(15) NOT NULL,
    ngayTao      datetime2(3) DEFAULT sysdatetime() NOT NULL,
    CONSTRAINT PK_KhachHang_Xe PRIMARY KEY (maKhachHang, bienSo),
    CONSTRAINT FK_KhachHang_Xe_KhachHang FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang) ON DELETE CASCADE,
    CONSTRAINT FK_KhachHang_Xe_Xe        FOREIGN KEY (bienSo)      REFERENCES Xe(bienSo)          ON DELETE CASCADE
);

-- ============================================================
-- TABLE: PhieuNhapPhuTung
-- ============================================================
CREATE TABLE PhieuNhapPhuTung (
    maPhieuNhap  varchar(20)   NOT NULL,
    ngayTao      datetime2(3)  DEFAULT sysdatetime() NOT NULL,
    maNhanVien   varchar(64)   NOT NULL,
    moTa         nvarchar(255) NULL,
    CONSTRAINT PK_PhieuNhapPhuTung PRIMARY KEY (maPhieuNhap),
    CONSTRAINT FK_PhieuNhap_NhanVien FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien) ON DELETE CASCADE
);

-- ============================================================
-- TABLE: ChiTietPhieuNhapPhuTung
-- ============================================================
CREATE TABLE ChiTietPhieuNhapPhuTung (
    maPhieuNhap  varchar(20)   NOT NULL,
    maPhuTung    varchar(20)   NOT NULL,
    soLuong      int           NOT NULL,
    donGiaNhap   decimal(18,2) NOT NULL,
    CONSTRAINT PK_ChiTietPhieuNhapPhuTung PRIMARY KEY (maPhieuNhap, maPhuTung),
    CONSTRAINT FK_ChiTietPhieuNhap_PhieuNhap FOREIGN KEY (maPhieuNhap) REFERENCES PhieuNhapPhuTung(maPhieuNhap) ON DELETE CASCADE,
    CONSTRAINT FK_ChiTietPhieuNhap_PhuTung   FOREIGN KEY (maPhuTung)   REFERENCES PhuTung(maPhuTung)           ON DELETE CASCADE
);

-- ============================================================
-- TABLE: PhieuTiepNhan
-- ============================================================
CREATE TABLE PhieuTiepNhan (
    maPhieuTiepNhan  varchar(64)   NOT NULL,
    ngayTao          datetime2(3)  DEFAULT sysdatetime() NOT NULL,
    moTa             nvarchar(max) NOT NULL,
    trangThai        varchar(50)   NOT NULL,
    maNhanVien       varchar(64)   NOT NULL,
    maKhachHang      varchar(20)   NOT NULL,
    bienSo           varchar(15)   NOT NULL,
    CONSTRAINT PK_PhieuTiepNhan PRIMARY KEY (maPhieuTiepNhan),
    CONSTRAINT FK_PhieuTiepNhan_NhanVien   FOREIGN KEY (maNhanVien)  REFERENCES NhanVien(maNhanVien)   ON DELETE CASCADE,
    CONSTRAINT FK_PhieuTiepNhan_KhachHang  FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang) ON DELETE CASCADE,
    CONSTRAINT FK_PhieuTiepNhan_Xe         FOREIGN KEY (bienSo)      REFERENCES Xe(bienSo)             ON DELETE CASCADE
);

-- ============================================================
-- TABLE: PhanCong
-- ============================================================
CREATE TABLE PhanCong (
    maPhanCong   varchar(20)   NOT NULL,
    maNhanVien   varchar(64)   NOT NULL,
    ngay         int           NOT NULL,
    thang        int           NOT NULL,
    nam          int           NOT NULL,
    caLamViec    varchar(50)   NOT NULL,
    ghiChu       nvarchar(255) NULL,
    CONSTRAINT PK_PhanCong PRIMARY KEY (maPhanCong),
    CONSTRAINT FK_PhanCong_NhanVien FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien) ON DELETE CASCADE
);

-- ============================================================
-- TABLE: HoaDon
-- ============================================================
CREATE TABLE HoaDon (
    maHoaDon           varchar(20)   NOT NULL,
    maPhieuTiepNhan    varchar(64)   NOT NULL,
    maVoucher          varchar(20)   NULL,
    trangThaiHoaDon    varchar(50)   NOT NULL,
    lyDoHuy            nvarchar(255) NULL,
    ghiChu             nvarchar(255) NULL,
    ngayTao            datetime2(3)  DEFAULT sysdatetime() NOT NULL,
    thoiGianThanhToan  datetime2(3)  NULL,
    mucGiamApDung      decimal(5,4)  NULL,
    CONSTRAINT PK_HoaDon PRIMARY KEY (maHoaDon),
    CONSTRAINT UQ_HoaDon_maPhieuTiepNhan UNIQUE (maPhieuTiepNhan),
    CONSTRAINT FK_HoaDon_PhieuTiepNhan FOREIGN KEY (maPhieuTiepNhan) REFERENCES PhieuTiepNhan(maPhieuTiepNhan) ON DELETE CASCADE,
    CONSTRAINT FK_HoaDon_Voucher       FOREIGN KEY (maVoucher)       REFERENCES Voucher(maVoucher)             ON DELETE CASCADE
);

-- ============================================================
-- TABLE: ChiTietHoaDon
-- ============================================================
CREATE TABLE ChiTietHoaDon (
    maHoaDon      varchar(20)   NOT NULL,
    loaiSanPham   varchar(50)   NOT NULL,
    maSanPham     nvarchar(255) NOT NULL,
    soLuong       int           NOT NULL,
    donGia        decimal(18,2) NOT NULL,
    CONSTRAINT PK_ChiTietHoaDon PRIMARY KEY (maHoaDon, loaiSanPham, maSanPham),
    CONSTRAINT FK_ChiTietHoaDon_HoaDon FOREIGN KEY (maHoaDon) REFERENCES HoaDon(maHoaDon) ON DELETE CASCADE
);

-- ============================================================
-- TABLE: VanDeKyThuat
-- ============================================================
CREATE TABLE VanDeKyThuat (
    maVanDe           varchar(20)   NOT NULL,
    maPhieuTiepNhan   varchar(64)   NOT NULL,
    tieuDe            nvarchar(200) NOT NULL,
    moTa              nvarchar(1000) NULL,
    mucDoUuTien       int           NOT NULL,
    giaiPhap          nvarchar(1000) NULL,
    khachHangDongY    varchar(20)   NOT NULL,  -- 'CO', 'KHONG', 'CHUA_XAC_NHAN'
    ngayTao           datetime2(3)  DEFAULT sysdatetime() NOT NULL,
    CONSTRAINT PK_VanDeKyThuat PRIMARY KEY (maVanDe),
    CONSTRAINT FK_VanDeKyThuat_PhieuTiepNhan
        FOREIGN KEY (maPhieuTiepNhan) REFERENCES PhieuTiepNhan(maPhieuTiepNhan) ON DELETE CASCADE,
    CONSTRAINT CK_VanDeKyThuat_MucDoUuTien
        CHECK (mucDoUuTien BETWEEN 1 AND 100)
);

-- ============================================================
-- TABLE: KhieuNai
-- ============================================================
CREATE TABLE KhieuNai (
    maKhieuNai       varchar(64)     NOT NULL,
    maKhachHang      varchar(20)     NOT NULL,
    maPhieuTiepNhan  varchar(64)     NOT NULL,
    noiDung          nvarchar(max) NOT NULL,
    trangThai        varchar(50)     NOT NULL,
    ngayTao          datetime2(3) DEFAULT sysdatetime() NOT NULL,
    CONSTRAINT PK_KhieuNai PRIMARY KEY (maKhieuNai),
    CONSTRAINT FK_KhieuNai_KhachHang      FOREIGN KEY (maKhachHang)     REFERENCES KhachHang(maKhachHang) ON DELETE CASCADE,
    CONSTRAINT FK_KhieuNai_PhieuTiepNhan  FOREIGN KEY (maPhieuTiepNhan) REFERENCES PhieuTiepNhan(maPhieuTiepNhan)
);