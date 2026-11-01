-- ============================================================
-- MIGRATION: bang dem chi tieu theo ngay (DayMetric)
--
-- Moi dong la mot bo dem cua (ngay, target), trong do target la
-- ten LoaiMa: DV, KH, NV, PT, VC, PN, PTN, PC, HD, VD, KN.
-- amount chinh la so thu tu da cap trong ngay, dung de sinh ma
-- nghiep vu dang PREFIX-YYYYMMDD-NNNNNN.
--
-- QUY TAC CHI CONG, KHONG TRU:
--   - Huy hoa don / phieu / ... khong giam amount.
--   - Insert that bai hoac rollback co the de lai khoang trong.
--   - Khong tai su dung so da cap.
-- Vi vay amount la "so chung tu da phat sinh trong ngay",
-- khong phai "so ban ghi con hieu luc".
--
-- ROLLBACK: DROP TABLE DayMetric;
-- ============================================================

IF NOT EXISTS (SELECT 1 FROM sys.tables WHERE name = N'DayMetric')
BEGIN
    CREATE TABLE DayMetric (
        ngay    date        NOT NULL,
        target  varchar(20) NOT NULL,
        amount  bigint      NOT NULL CONSTRAINT DF_DayMetric_amount DEFAULT (0),
        CONSTRAINT PK_DayMetric PRIMARY KEY (ngay, target)
    );
END
