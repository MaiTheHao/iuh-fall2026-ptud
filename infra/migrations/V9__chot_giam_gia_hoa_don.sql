-- ============================================================
-- MIGRATION: chot muc giam gia tren hoa don
-- Them cot HoaDon.mucGiamApDung decimal(5,4) NULL.
--
-- LY DO:
-- Truoc day muc giam duoc doc lai tu Voucher moi lan tinh tong tien,
-- nen mot hoa don DA_THANH_TOAN se doi tong tien khi voucher het han
-- hoac bi doi muc giam, va doanh thu bao cao bi sai lech theo thoi gian.
-- Tu nay muc giam duoc chot tai thoi diem tao/cap nhat/thanh toan.
--
-- DU LIEU CU:
-- Hoa don tao truoc migration nay co mucGiamApDung = NULL.
-- Service van tinh dong nhu truoc (doc Voucher hien tai) khi gap NULL,
-- nen khong can backfill. Khong backfill de tranh ghi sai lich su
-- (voucher da bi doi muc giam hoac da het han).
--
-- DIEU KIEN TIEN QUYET: khong.
--
-- ROLLBACK: ALTER TABLE HoaDon DROP COLUMN mucGiamApDung;
-- Luu y: rollback se mat moc giam gia da chot.
-- ============================================================

IF NOT EXISTS (SELECT 1 FROM sys.columns
               WHERE object_id = OBJECT_ID(N'HoaDon') AND name = N'mucGiamApDung')
    ALTER TABLE HoaDon ADD mucGiamApDung decimal(5,4) NULL;
