-- ============================================================
-- MIGRATION: mo rong cot bang KhieuNai
-- maKhieuNai: varchar(20) -> varchar(64)
-- noiDung:    nvarchar(1000) -> nvarchar(max)
--
-- LY DO:
-- Ma khieu nai = "KN" + ma nhan vien + duoi thoi gian.
--   "KN"                 =  2 ky tu
--   ma nhan vien ("NV" + sdt 10 + dd/MM/yyyy 10 + giay 5) = 27 ky tu
--   duoi thoi gian (dd/MM/yyyy 10 + giay 5)               = 15 ky tu
--   => toi da 44 ky tu, vuot varchar(20).
-- noiDung co y de khong gioi han chat (dac ta): nvarchar(max).
--
-- Khong co FK nao tham chieu KhieuNai, nen co the ALTER COLUMN
-- truc tiep, khong can DROP/ADD CONSTRAINT.
--
-- ROLLBACK: khong hoan tac tu dong. Quay lai varchar(20) /
-- nvarchar(1000) se THAT BAI neu da co du lieu dai hon gioi han cu.
-- ============================================================

ALTER TABLE KhieuNai ALTER COLUMN maKhieuNai varchar(64) NOT NULL;
ALTER TABLE KhieuNai ALTER COLUMN noiDung nvarchar(max) NOT NULL;
