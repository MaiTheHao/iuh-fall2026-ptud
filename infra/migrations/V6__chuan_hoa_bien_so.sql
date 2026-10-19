-- ============================================================
-- MIGRATION: chuan hoa bien so cu ve dang khong dau, UPPERCASE
-- Chay SAU V4 (mock data). Khong the hoan tac tu dong: dau gach
-- ngang/ dau cham cua bien so goc khong the khoi phuc bang SQL.
--
-- DIEU KIEN TIEN QUYET (chay truy van nay TRUOC khi migrate):
-- Neu 2 bien so khac nhau chuan hoa ve cung mot gia tri thi
-- UPDATE se vi pham PK_Xe. Phai xu ly thu cong truoc.
--
--   SELECT UPPER(REPLACE(REPLACE(REPLACE(bienSo,'-',''),'.',''),' ','')) AS bienSoChuanHoa, COUNT(*)
--   FROM Xe GROUP BY UPPER(REPLACE(REPLACE(REPLACE(bienSo,'-',''),'.',''),' ','')) HAVING COUNT(*) > 1;
--
-- Bien so (PK cua Xe) bi 2 FK tham chieu, nen phai DROP FK
-- truoc khi UPDATE, roi ADD lai dung dinh nghia goc.
-- Bien the chuan hoa khop XeService.normalizeBienSo.
-- ============================================================

-- ============================================================
-- BUOC 1: go 2 FK tham chieu Xe(bienSo)
-- ============================================================
ALTER TABLE KhachHang_Xe DROP CONSTRAINT FK_KhachHang_Xe_Xe;
ALTER TABLE PhieuTiepNhan DROP CONSTRAINT FK_PhieuTiepNhan_Xe;

-- ============================================================
-- BUOC 2: chuan hoa bien so o ca 3 bang
-- ============================================================
UPDATE Xe
SET bienSo = UPPER(REPLACE(REPLACE(REPLACE(bienSo, '-', ''), '.', ''), ' ', ''));

UPDATE KhachHang_Xe
SET bienSo = UPPER(REPLACE(REPLACE(REPLACE(bienSo, '-', ''), '.', ''), ' ', ''));

UPDATE PhieuTiepNhan
SET bienSo = UPPER(REPLACE(REPLACE(REPLACE(bienSo, '-', ''), '.', ''), ' ', ''));

-- ============================================================
-- BUOC 3: khoi phuc 2 FK voi ON DELETE CASCADE
-- ============================================================
ALTER TABLE KhachHang_Xe ADD CONSTRAINT FK_KhachHang_Xe_Xe FOREIGN KEY (bienSo) REFERENCES Xe(bienSo) ON DELETE CASCADE;
ALTER TABLE PhieuTiepNhan ADD CONSTRAINT FK_PhieuTiepNhan_Xe FOREIGN KEY (bienSo) REFERENCES Xe(bienSo) ON DELETE CASCADE;
