-- ============================================================
-- MIGRATION: mo rong cot bang PhieuTiepNhan
-- moTa: nvarchar(500) -> nvarchar(max)
--
-- LY DO:
-- Mo ta phieu tiep nhan khong con gioi han do dai (dac ta).
-- Service chi yeu cau moTa khong duoc de trong, khong gioi han
-- so ky tu, nen nvarchar(500) la qua chat.
--
-- Khong co FK nao tham chieu cot moTa, nen co the ALTER COLUMN
-- truc tiep, khong can DROP/ADD CONSTRAINT.
--
-- ROLLBACK: khong hoan tac tu dong. Quay lai nvarchar(500) se
-- THAT BAI neu da co du lieu dai hon 500 ky tu.
-- ============================================================

ALTER TABLE PhieuTiepNhan ALTER COLUMN moTa nvarchar(max) NOT NULL;
