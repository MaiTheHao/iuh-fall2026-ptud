-- ============================================================
-- V16: Seed ThanhToan cho 6 hoa don DA_THANH_TOAN co san (V14).
-- LY DO: mock.sql / init.sql khong duoc runtime nap; DB chay bang
-- Flyway can du lieu thanh toan mau de kiem chung U2/U4.
-- PHAM VI: chi INSERT khi chua ton tai; KHONG sua V0..V14.
-- ROLLBACK (thu cong):
--   DELETE FROM ThanhToan WHERE maThanhToan LIKE 'TT-2026%';
--   DELETE FROM DayMetric WHERE target = 'TT';
-- CANH BAO: V16 la apply-once: khong sua file nay sau khi da apply.
-- ============================================================
SET XACT_ABORT ON;
BEGIN TRANSACTION;

IF NOT EXISTS (SELECT 1 FROM ThanhToan WHERE maThanhToan = N'TT-20261022-000001')
    INSERT INTO ThanhToan (maThanhToan, maHoaDon, phuongThuc, soTien, thoiGian, maNhanVien, ghiChu) VALUES
    (N'TT-20261022-000001', N'HD-20261022-000001', N'TIEN_MAT', 783000.00, '2026-10-22T10:10:00', N'NV-20261020-000002', NULL);

IF NOT EXISTS (SELECT 1 FROM ThanhToan WHERE maThanhToan = N'TT-20261025-000001')
    INSERT INTO ThanhToan (maThanhToan, maHoaDon, phuongThuc, soTien, thoiGian, maNhanVien, ghiChu) VALUES
    (N'TT-20261025-000001', N'HD-20261025-000001', N'CHUYEN_KHOAN', 501600.00, '2026-10-25T11:30:00', N'NV-20261020-000002', NULL);

IF NOT EXISTS (SELECT 1 FROM ThanhToan WHERE maThanhToan = N'TT-20261102-000001')
    INSERT INTO ThanhToan (maThanhToan, maHoaDon, phuongThuc, soTien, thoiGian, maNhanVien, ghiChu) VALUES
    (N'TT-20261102-000001', N'HD-20261102-000001', N'CHUYEN_KHOAN', 665000.00, '2026-11-02T11:00:00', N'NV-20261020-000006', NULL);

IF NOT EXISTS (SELECT 1 FROM ThanhToan WHERE maThanhToan = N'TT-20261114-000001')
    INSERT INTO ThanhToan (maThanhToan, maHoaDon, phuongThuc, soTien, thoiGian, maNhanVien, ghiChu) VALUES
    (N'TT-20261114-000001', N'HD-20261114-000001', N'TIEN_MAT', 503500.00, '2026-11-14T14:10:00', N'NV-20261020-000006', NULL);

IF NOT EXISTS (SELECT 1 FROM ThanhToan WHERE maThanhToan = N'TT-20261125-000001')
    INSERT INTO ThanhToan (maThanhToan, maHoaDon, phuongThuc, soTien, thoiGian, maNhanVien, ghiChu) VALUES
    (N'TT-20261125-000001', N'HD-20261125-000001', N'CHUYEN_KHOAN', 1720000.00, '2026-11-25T11:00:00', N'NV-20261020-000006', NULL);

IF NOT EXISTS (SELECT 1 FROM ThanhToan WHERE maThanhToan = N'TT-20261210-000001')
    INSERT INTO ThanhToan (maThanhToan, maHoaDon, phuongThuc, soTien, thoiGian, maNhanVien, ghiChu) VALUES
    (N'TT-20261210-000001', N'HD-20261210-000001', N'CHUYEN_KHOAN', 268400.00, '2026-12-10T10:50:00', N'NV-20261020-000002', NULL);

IF NOT EXISTS (SELECT 1 FROM DayMetric WHERE ngay = '2026-10-22' AND target = N'TT')
    INSERT INTO DayMetric (ngay, target, amount) VALUES ('2026-10-22', N'TT', 1);

IF NOT EXISTS (SELECT 1 FROM DayMetric WHERE ngay = '2026-10-25' AND target = N'TT')
    INSERT INTO DayMetric (ngay, target, amount) VALUES ('2026-10-25', N'TT', 1);

IF NOT EXISTS (SELECT 1 FROM DayMetric WHERE ngay = '2026-11-02' AND target = N'TT')
    INSERT INTO DayMetric (ngay, target, amount) VALUES ('2026-11-02', N'TT', 1);

IF NOT EXISTS (SELECT 1 FROM DayMetric WHERE ngay = '2026-11-14' AND target = N'TT')
    INSERT INTO DayMetric (ngay, target, amount) VALUES ('2026-11-14', N'TT', 1);

IF NOT EXISTS (SELECT 1 FROM DayMetric WHERE ngay = '2026-11-25' AND target = N'TT')
    INSERT INTO DayMetric (ngay, target, amount) VALUES ('2026-11-25', N'TT', 1);

IF NOT EXISTS (SELECT 1 FROM DayMetric WHERE ngay = '2026-12-10' AND target = N'TT')
    INSERT INTO DayMetric (ngay, target, amount) VALUES ('2026-12-10', N'TT', 1);

COMMIT TRANSACTION;
