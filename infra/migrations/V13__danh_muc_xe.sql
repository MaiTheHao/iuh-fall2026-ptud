-- ============================================================
-- V13: Danh muc xe 3 cap HangXe -> DongXe -> DoiXe
-- LY DO: thay 3 cot text (hangXe, dongXe, doiXe) cua Xe bang FK maDoiXe.
-- THU TU: chay TRUOC V14__mock; V14 nap danh muc xe va Xe tham chieu maDoiXe.
-- CLEAN QUICK: xoa toan bo du lieu nghiep vu cu. PHAI backup truoc khi chay.
--   Cot maDoiXe duoc them o dang NOT NULL nen bang Xe phai rong.
--   Xoa theo thu tu con -> cha.
--   FK_KhieuNai_PhieuTiepNhan la NO ACTION (tao o V3, sau V2) nen phai xoa tuong minh.
-- ROLLBACK (thu cong): xoa FK_Xe_DoiXe, cot Xe.maDoiXe, roi DROP DoiXe/DongXe/HangXe.
--   Rollback chi ha schema, KHONG khoi phuc 3 cot text cu.
-- CANH BAO: apply-once; khong sua file nay sau khi da apply.
-- ============================================================
DELETE FROM ChiTietHoaDon;
DELETE FROM HoaDon;
DELETE FROM VanDeKyThuat;
DELETE FROM KhieuNai;
DELETE FROM PhieuTiepNhan;
DELETE FROM KhachHang_Xe;
DELETE FROM Xe;

CREATE TABLE HangXe (
    maHangXe  varchar(20)   NOT NULL,
    ten       nvarchar(100) NOT NULL,
    CONSTRAINT PK_HangXe PRIMARY KEY (maHangXe),
    CONSTRAINT UQ_HangXe_ten UNIQUE (ten)
);

CREATE TABLE DongXe (
    maDongXe  varchar(20)   NOT NULL,
    maHangXe  varchar(20)   NOT NULL,
    ten       nvarchar(100) NOT NULL,
    CONSTRAINT PK_DongXe PRIMARY KEY (maDongXe),
    CONSTRAINT FK_DongXe_HangXe FOREIGN KEY (maHangXe) REFERENCES HangXe(maHangXe),
    CONSTRAINT UQ_DongXe_hang_ten UNIQUE (maHangXe, ten)
);

CREATE TABLE DoiXe (
    maDoiXe   varchar(20) NOT NULL,
    maDongXe  varchar(20) NOT NULL,
    namSX     int         NOT NULL,
    CONSTRAINT PK_DoiXe PRIMARY KEY (maDoiXe),
    CONSTRAINT FK_DoiXe_DongXe FOREIGN KEY (maDongXe) REFERENCES DongXe(maDongXe),
    CONSTRAINT UQ_DoiXe_dong_nam UNIQUE (maDongXe, namSX),
    CONSTRAINT CK_DoiXe_namSX CHECK (namSX >= 1886)
);

ALTER TABLE Xe DROP COLUMN hangXe;
ALTER TABLE Xe DROP COLUMN dongXe;
ALTER TABLE Xe DROP COLUMN doiXe;
ALTER TABLE Xe ADD maDoiXe varchar(20) NOT NULL;
ALTER TABLE Xe ADD CONSTRAINT FK_Xe_DoiXe FOREIGN KEY (maDoiXe) REFERENCES DoiXe(maDoiXe);
