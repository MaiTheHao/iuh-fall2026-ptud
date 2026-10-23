-- ============================================================
-- MIGRATION: moi phieu tiep nhan chi co toi da mot hoa don
-- HoaDon.maPhieuTiepNhan duoc dung lam khoa nghiep vu
-- (HoaDonDAO.findByPhieuTiepNhan). Truoc day chi service kiem tra
-- "da co hoa don chua" roi moi insert, ma hai cua so lam viec co the
-- cung doc thay "chua co" va cung insert. Rang buoc UNIQUE dua viec
-- nay ve tang DB, service bat loi 2627/2601 va tra ve hoa don da co.
--
-- DIEU KIEN TIEN QUYET: chay truy van nay TRUOC khi migrate.
--   SELECT maPhieuTiepNhan, COUNT(*) FROM HoaDon
--   GROUP BY maPhieuTiepNhan HAVING COUNT(*) > 1;
-- Neu co ket qua, phai xu ly thu cong truoc (huy hoac xoa ban ghi trung),
-- neu khong cau ALTER TABLE ben duoi se that bai.
--
-- ROLLBACK: ALTER TABLE HoaDon DROP CONSTRAINT UQ_HoaDon_maPhieuTiepNhan;
-- ============================================================

IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'UQ_HoaDon_maPhieuTiepNhan')
    ALTER TABLE HoaDon ADD CONSTRAINT UQ_HoaDon_maPhieuTiepNhan UNIQUE (maPhieuTiepNhan);
