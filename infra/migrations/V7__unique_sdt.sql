-- ============================================================
-- MIGRATION: unique customer phone number
-- KhachHang.sdt duoc dung lam khoa tra cuu nghiep vu
-- (KhachHangDAO.findBySdt). Neu 2 khach hang trung sdt thi
-- viec tra cuu khong xac dinh, phieu/khiieu nai co the gan nham.
--
-- DIEU KIEN TIEN QUYET: chay truy van nay TRUOC khi migrate.
--   SELECT sdt, COUNT(*) FROM KhachHang GROUP BY sdt HAVING COUNT(*) > 1;
-- Neu co ket qua, phai xu ly thu cong truoc.
--
-- ROLLBACK: ALTER TABLE KhachHang DROP CONSTRAINT UQ_KhachHang_sdt;
-- ============================================================

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'UQ_KhachHang_sdt')
    ALTER TABLE KhachHang ADD CONSTRAINT UQ_KhachHang_sdt UNIQUE (sdt);
