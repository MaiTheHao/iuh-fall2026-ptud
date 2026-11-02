SET XACT_ABORT ON;
BEGIN TRANSACTION;

-- ============================================================
-- Gara Ô tô Lúa — dữ liệu mẫu khởi tạo hệ thống (mock.sql)
-- Chuẩn hóa mã theo MaService: PREFIX-yyyyMMdd-NNNNNN
-- Dải thời gian lịch sử: 20/10/2026 đến 30/12/2026
-- ============================================================

-- ============================================================
-- 1. TABLE: DichVu (15)
-- Format: DV-yyyyMMdd-NNNNNN
-- ============================================================
INSERT INTO DichVu (maDichVu, tenDichVu, moTa, anhUrl, donGia) VALUES
('DV-20261020-000001', N'Thay dầu động cơ', N'Thay dầu động cơ và kiểm tra mức dầu', 'https://images.unsplash.com/photo-1580273916550-e323be2ae537?auto=format&fit=crop&w=200&q=80', 250000.00),
('DV-20261020-000002', N'Rửa xe cơ bản', N'Rửa thân xe, kính và vệ sinh bánh xe', 'https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=200&q=80', 80000.00),
('DV-20261020-000003', N'Rửa xe detailing', N'Vệ sinh kỹ ngoại thất và nội thất', 'https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&w=200&q=80', 450000.00),
('DV-20261020-000004', N'Kiểm tra hệ thống phanh', N'Kiểm tra má phanh, đĩa phanh và dầu phanh', 'https://images.unsplash.com/photo-1621007947382-bb3c3994e3fb?auto=format&fit=crop&w=200&q=80', 180000.00),
('DV-20261020-000005', N'Cân chỉnh góc đặt bánh xe', N'Cân chỉnh góc đặt bánh xe bằng thiết bị chuyên dụng', 'https://images.unsplash.com/photo-1619642751034-765dfdf7c58e?auto=format&fit=crop&w=200&q=80', 350000.00),
('DV-20261020-000006', N'Kiểm tra điều hòa', N'Kiểm tra áp suất gas và hiệu suất làm lạnh', 'https://images.unsplash.com/photo-1449965408869-eaa3f722e40d?auto=format&fit=crop&w=200&q=80', 150000.00),
('DV-20261020-000007', N'Nạp gas điều hòa', N'Nạp bổ sung gas điều hòa ô tô', 'https://images.unsplash.com/photo-1517524008697-84bbe3c3fd98?auto=format&fit=crop&w=200&q=80', 550000.00),
('DV-20261020-000008', N'Thay lọc gió động cơ', N'Thay lọc gió và vệ sinh hộp lọc', 'https://images.unsplash.com/photo-1511919884226-fd3cad34687c?auto=format&fit=crop&w=200&q=80', 120000.00),
('DV-20261020-000009', N'Kiểm tra ắc quy', N'Đo điện áp và kiểm tra khả năng khởi động', 'https://images.unsplash.com/photo-1494905998402-395d579af36f?auto=format&fit=crop&w=200&q=80', 70000.00),
('DV-20261020-000010', N'Bảo dưỡng định kỳ 10.000 km', N'Kiểm tra tổng quát theo mốc bảo dưỡng', 'https://images.unsplash.com/photo-1507136566006-cfc505b114fc?auto=format&fit=crop&w=200&q=80', 950000.00),
('DV-20261020-000011', N'Chẩn đoán lỗi ECU', N'Đọc mã lỗi và lập báo cáo chẩn đoán', 'https://images.unsplash.com/photo-1489824904134-891ab64532f1?auto=format&fit=crop&w=200&q=80', 300000.00),
('DV-20261020-000012', N'Thay má phanh trước', N'Tháo lắp và thay má phanh trước', 'https://images.unsplash.com/photo-1621607512214-68297480165e?auto=format&fit=crop&w=200&q=80', 280000.00),
('DV-20261020-000013', N'Thay bugi', N'Thay bugi và kiểm tra hệ thống đánh lửa', 'https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?auto=format&fit=crop&w=200&q=80', 220000.00),
('DV-20261020-000014', N'Sửa chữa hệ thống treo', N'Kiểm tra tiếng kêu và các chi tiết hệ thống treo', 'https://images.unsplash.com/photo-1502161254066-6c74afbf07aa?auto=format&fit=crop&w=200&q=80', 400000.00),
('DV-20261020-000015', N'Vệ sinh kim phun', N'Vệ sinh kim phun và kiểm tra hoạt động động cơ', 'https://images.unsplash.com/photo-1583121274602-3e2820c69888?auto=format&fit=crop&w=200&q=80', 600000.00);

-- ============================================================
-- TABLE: NhaCungCap (4)
-- Format: NCC-yyyyMMdd-NNNNNN
-- ============================================================
INSERT INTO NhaCungCap (maNhaCungCap, tenNhaCungCap, sdt, email, diaChi, trangThai, ghiChu, ngayTao) VALUES
('NCC-20261020-000001', N'Công ty TNHH Phụ Tùng Sài Gòn', '0911000001', 'lienhe@phutungsaigon.vn', N'Quận 5, TP. Hồ Chí Minh', 'HOAT_DONG', NULL, '2026-10-20 08:00:00'),
('NCC-20261020-000002', N'Công ty CP Vật Tư Ô Tô Miền Nam', '0911000002', 'lienhe@vattuotosaigon.vn', N'Thủ Đức, TP. Hồ Chí Minh', 'HOAT_DONG', NULL, '2026-10-20 08:00:00'),
('NCC-20261020-000003', N'Cửa hàng Phụ Tùng Hưng Thịnh', '0911000003', NULL, N'Biên Hoà, Đồng Nai', 'NGUNG_HOP_TAC', NULL, '2026-10-20 08:00:00'),
('NCC-20261020-000004', N'Công ty TNHH Thiết Bị Gara Việt', '0911000004', 'lienhe@thietbigaraviet.vn', N'Bình Dương', 'HOAT_DONG', NULL, '2026-10-20 08:00:00');

-- ============================================================
-- 2. TABLE: PhuTung (15)
-- Format: PT-yyyyMMdd-NNNNNN
-- ============================================================
INSERT INTO PhuTung (maPhuTung, tenPhuTung, moTa, anhUrl, donGiaNhap, soLuongTon, maNhaCungCap) VALUES
('PT-20261020-000001', N'Dầu động cơ 5W-30 1L', N'Dầu tổng hợp dùng cho động cơ xăng', 'https://images.unsplash.com/photo-1486262715619-67b85e0b08d3?auto=format&fit=crop&w=200&q=80', 125000.00, 48, 'NCC-20261020-000001'),
('PT-20261020-000002', N'Lọc dầu động cơ', N'Lọc dầu thay thế phổ thông', 'https://images.unsplash.com/photo-1600790142055-619df03207e6?auto=format&fit=crop&w=200&q=80', 65000.00, 32, 'NCC-20261020-000002'),
('PT-20261020-000003', N'Lọc gió động cơ sedan', N'Lọc gió giấy xếp', 'https://images.unsplash.com/photo-1492144534655-ae79c964c9d7?auto=format&fit=crop&w=200&q=80', 75000.00, 24, 'NCC-20261020-000003'),
('PT-20261020-000004', N'Má phanh trước Toyota', N'Bộ má phanh trước tương thích một số dòng Toyota', 'https://images.unsplash.com/photo-1502877338535-766e1452684a?auto=format&fit=crop&w=200&q=80', 320000.00, 12, 'NCC-20261020-000001'),
('PT-20261020-000005', N'Bugi iridium', N'Bugi đánh lửa tuổi thọ cao', 'https://images.unsplash.com/photo-1493238792000-8113da705763?auto=format&fit=crop&w=200&q=80', 145000.00, 40, 'NCC-20261020-000001'),
('PT-20261020-000006', N'Ắc quy 12V 60Ah', N'Ắc quy kín khí dùng cho xe du lịch', 'https://images.unsplash.com/photo-1568605117036-5fe5e7bab0b7?auto=format&fit=crop&w=200&q=80', 1450000.00, 8, 'NCC-20261020-000002'),
('PT-20261020-000007', N'Gas điều hòa R134a', N'Gas nạp bổ sung điều hòa', 'https://images.unsplash.com/photo-1552519507-da3b142c6e3d?auto=format&fit=crop&w=200&q=80', 180000.00, 15, 'NCC-20261020-000003'),
('PT-20261020-000008', N'Nước làm mát 1L', N'Nước làm mát động cơ pha sẵn', 'https://images.unsplash.com/photo-1592198084033-aade902d1aae?auto=format&fit=crop&w=200&q=80', 85000.00, 30, 'NCC-20261020-000001'),
('PT-20261020-000009', N'Gạt mưa 16 inch', N'Lưỡi gạt mưa thay thế', 'https://images.unsplash.com/photo-1581092918056-0c4c3acd3789?auto=format&fit=crop&w=200&q=80', 95000.00, 18, 'NCC-20261020-000001'),
('PT-20261020-000010', N'Gạt mưa 24 inch', N'Lưỡi gạt mưa thay thế', 'https://images.unsplash.com/photo-1600661653561-629509216228?auto=format&fit=crop&w=200&q=80', 110000.00, 16, 'NCC-20261020-000002'),
('PT-20261020-000011', N'Dây curoa phụ trợ', N'Dây truyền động phụ trợ động cơ', 'https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=200&q=80', 210000.00, 10, 'NCC-20261020-000003'),
('PT-20261020-000012', N'Dung dịch vệ sinh kim phun', N'Dung dịch vệ sinh hệ thống nhiên liệu', 'https://images.unsplash.com/photo-1621007947382-bb3c3994e3fb?auto=format&fit=crop&w=200&q=80', 160000.00, 22, 'NCC-20261020-000001'),
('PT-20261020-000013', N'Dầu phanh DOT 4', N'Dầu phanh đạt tiêu chuẩn DOT 4', 'https://images.unsplash.com/photo-1619642751034-765dfdf7c58e?auto=format&fit=crop&w=200&q=80', 95000.00, 20, 'NCC-20261020-000001'),
('PT-20261020-000014', N'Bi moay-ơ trước', N'Vòng bi moay-ơ thay thế', 'https://images.unsplash.com/photo-1449965408869-eaa3f722e40d?auto=format&fit=crop&w=200&q=80', 480000.00, 6, 'NCC-20261020-000002'),
('PT-20261020-000015', N'Kẹp lọc dầu', N'Phụ kiện lắp đặt lọc dầu', 'https://images.unsplash.com/photo-1517524008697-84bbe3c3fd98?auto=format&fit=crop&w=200&q=80', 25000.00, 50, 'NCC-20261020-000003');

-- ============================================================
-- 3. TABLE: Voucher (10)
-- Format: VC-yyyyMMdd-NNNNNN
-- ============================================================
INSERT INTO Voucher (maVoucher, tenChuongTrinh, moTa, mucGiam, thoiHanApDung, trangThaiKichHoat) VALUES
('VC-20261020-000001', N'Khách hàng mới', N'Giảm 10% cho lần sử dụng dịch vụ đầu tiên', 0.1000, '2027-12-31T23:59:59', 1),
('VC-20261020-000002', N'Bảo dưỡng mùa hè', N'Ưu đãi bảo dưỡng định kỳ', 0.1500, '2027-06-30T23:59:59', 1),
('VC-20261020-000003', N'Rửa xe cuối tuần', N'Giảm 5% dịch vụ rửa xe', 0.0500, '2027-03-31T23:59:59', 1),
('VC-20261020-000004', N'Tri ân khách hàng', N'Ưu đãi khách hàng thân thiết', 0.1200, '2027-12-31T23:59:59', 1),
('VC-20261020-000005', N'Tết an toàn', N'Ưu đãi kiểm tra xe dịp lễ', 0.0800, '2027-02-28T23:59:59', 0),
('VC-20261020-000006', N'Combo thay dầu', N'Ưu đãi khi thay dầu và lọc dầu', 0.1000, '2027-09-30T23:59:59', 1),
('VC-20261020-000007', N'Sinh nhật gara', N'Khuyến mãi kỷ niệm thành lập gara', 0.2000, '2027-08-31T23:59:59', 1),
('VC-20261020-000008', N'Khách hàng doanh nghiệp', N'Ưu đãi theo chương trình đối tác', 0.0700, '2027-12-31T23:59:59', 1),
('VC-20261020-000009', N'Kiểm tra điều hòa', N'Giảm giá kiểm tra điều hòa', 0.0500, '2026-12-31T23:59:59', 1),
('VC-20261020-000010', N'Ưu đãi hết hạn', N'Dữ liệu mẫu voucher đã ngừng kích hoạt', 0.2500, '2026-11-30T23:59:59', 0);

INSERT INTO HangXe (maHangXe, ten) VALUES
('HX-20000101-000001', N'Toyota'),
('HX-20000101-000002', N'Honda'),
('HX-20000101-000003', N'Hyundai'),
('HX-20000101-000004', N'Mazda'),
('HX-20000101-000005', N'Kia'),
('HX-20000101-000006', N'Ford'),
('HX-20000101-000007', N'Mitsubishi'),
('HX-20000101-000008', N'VinFast'),
('HX-20000101-000009', N'Suzuki');

INSERT INTO DongXe (maDongXe, maHangXe, ten) VALUES
('DX-20000101-000001', 'HX-20000101-000001', N'Vios'),
('DX-20000101-000002', 'HX-20000101-000001', N'Corolla Cross'),
('DX-20000101-000003', 'HX-20000101-000001', N'Fortuner'),
('DX-20000101-000004', 'HX-20000101-000002', N'City'),
('DX-20000101-000005', 'HX-20000101-000002', N'CR-V'),
('DX-20000101-000006', 'HX-20000101-000003', N'Accent'),
('DX-20000101-000007', 'HX-20000101-000003', N'Tucson'),
('DX-20000101-000008', 'HX-20000101-000004', N'Mazda 3'),
('DX-20000101-000009', 'HX-20000101-000004', N'CX-5'),
('DX-20000101-000010', 'HX-20000101-000005', N'Seltos'),
('DX-20000101-000011', 'HX-20000101-000005', N'Morning'),
('DX-20000101-000012', 'HX-20000101-000006', N'Ranger'),
('DX-20000101-000013', 'HX-20000101-000007', N'Xpander'),
('DX-20000101-000014', 'HX-20000101-000008', N'VF e34'),
('DX-20000101-000015', 'HX-20000101-000009', N'XL7');

INSERT INTO DoiXe (maDoiXe, maDongXe, namSX) VALUES
('DO-20000101-000001', 'DX-20000101-000001', 2021),
('DO-20000101-000002', 'DX-20000101-000004', 2022),
('DO-20000101-000003', 'DX-20000101-000006', 2020),
('DO-20000101-000004', 'DX-20000101-000008', 2023),
('DO-20000101-000005', 'DX-20000101-000010', 2022),
('DO-20000101-000006', 'DX-20000101-000002', 2024),
('DO-20000101-000007', 'DX-20000101-000012', 2021),
('DO-20000101-000008', 'DX-20000101-000013', 2020),
('DO-20000101-000009', 'DX-20000101-000014', 2023),
('DO-20000101-000010', 'DX-20000101-000005', 2019),
('DO-20000101-000011', 'DX-20000101-000003', 2018),
('DO-20000101-000012', 'DX-20000101-000009', 2021),
('DO-20000101-000013', 'DX-20000101-000007', 2022),
('DO-20000101-000014', 'DX-20000101-000011', 2017),
('DO-20000101-000015', 'DX-20000101-000015', 2023);

-- ============================================================
-- 4. TABLE: NhanVien (8)
-- Format: NV-yyyyMMdd-NNNNNN
-- ============================================================
INSERT INTO NhanVien (maNhanVien, tenDangNhap, matKhau, hoTen, anhDaiDien, soDienThoai, vaiTro, trangThai, ngayTao) VALUES
('NV-20261020-000001', 'admin', 'test@123', N'Administrator', NULL, '0900000001', 'NHAN_VIEN_QUAN_LY', 'HOAT_DONG', '2026-10-20T08:00:00'),
('NV-20261020-000002', 'tiepnhan01', 'test@123', N'Nguyễn Thị Mỹ Quốc', NULL, '0900000002', 'NHAN_VIEN_TIEP_NHAN', 'HOAT_DONG', '2026-10-20T08:00:00'),
('NV-20261020-000003', 'thungan01', 'test@123', N'Trần Thị Thanh Bình', NULL, '0900000003', 'NHAN_VIEN_THU_NGAN', 'HOAT_DONG', '2026-10-20T08:00:00'),
('NV-20261020-000004', 'kythuat01', 'test@123', N'Phạm Nát Bét', NULL, '0900000004', 'NHAN_VIEN_KY_THUAT', 'HOAT_DONG', '2026-10-20T08:00:00'),
('NV-20261020-000005', 'kythuat02', 'test@123', N'Võ Hoàng Long', NULL, '0900000005', 'NHAN_VIEN_KY_THUAT', 'HOAT_DONG', '2026-10-20T08:00:00'),
('NV-20261020-000006', 'tiepnhan02', 'test@123', N'Đặng Thùy Dung', NULL, '0900000006', 'NHAN_VIEN_TIEP_NHAN', 'HOAT_DONG', '2026-10-20T08:00:00'),
('NV-20261020-000007', 'thungan02', 'test@123', N'Bùi Thanh Tâm', NULL, '0900000007', 'NHAN_VIEN_THU_NGAN', 'DA_KHOA', '2026-10-20T08:00:00'),
('NV-20261020-000008', 'quanly02', 'test@123', N'Đỗ Quốc Việt', NULL, '0900000008', 'NHAN_VIEN_QUAN_LY', 'DA_KHOA', '2026-10-20T08:00:00');

-- ============================================================
-- 5. TABLE: KhachHang (15)
-- Format: KH-yyyyMMdd-NNNNNN
-- ============================================================
INSERT INTO KhachHang (maKhachHang, sdt, hoTen, ngayTao) VALUES
('KH-20261020-000001', '0901000001', N'Nguyễn Minh Anh', '2026-10-20T08:15:00'),
('KH-20261020-000002', '0901000002', N'Trần Quốc Bảo', '2026-10-20T09:20:00'),
('KH-20261022-000001', '0901000003', N'Lê Hoàng Nam', '2026-10-22T10:05:00'),
('KH-20261022-000002', '0901000004', N'Phạm Thu Hà', '2026-10-22T14:10:00'),
('KH-20261025-000001', '0901000005', N'Võ Thành Đạt', '2026-10-25T11:30:00'),
('KH-20261025-000002', '0901000006', N'Đặng Ngọc Mai', '2026-10-25T08:45:00'),
('KH-20261101-000001', '0901000007', N'Bùi Đức Huy', '2026-11-01T13:00:00'),
('KH-20261101-000002', '0901000008', N'Đỗ Khánh Linh', '2026-11-01T16:25:00'),
('KH-20261105-000001', '0901000009', N'Huỳnh Gia Phúc', '2026-11-05T09:10:00'),
('KH-20261105-000002', '0901000010', N'Phan Thảo Vy', '2026-11-05T10:40:00'),
('KH-20261110-000001', '0901000011', N'Ngô Nhật Minh', '2026-11-10T15:15:00'),
('KH-20261110-000002', '0901000012', N'Dương Mỹ Duyên', '2026-11-10T08:35:00'),
('KH-20261115-000001', '0901000013', N'Đinh Tuấn Kiệt', '2026-11-15T12:20:00'),
('KH-20261115-000002', '0901000014', N'Vũ Bảo Ngọc', '2026-11-15T09:55:00'),
('KH-20261115-000003', '0901000015', N'Phạm Anh Khoa', '2026-11-15T14:45:00');

-- ============================================================
-- 6. TABLE: Xe (15)
-- Biển số chuẩn hóa: viết hoa, không dấu gạch/dấu chấm/khoảng trắng
-- ============================================================
INSERT INTO Xe (bienSo, maDoiXe, odo) VALUES
('51A12345', 'DO-20000101-000001', 45200),
('51H23456', 'DO-20000101-000002', 31800),
('60A34567', 'DO-20000101-000003', 68500),
('51K45678', 'DO-20000101-000004', 19200),
('59A56789', 'DO-20000101-000005', 36700),
('51G67890', 'DO-20000101-000006', 8500),
('61A78901', 'DO-20000101-000007', 73400),
('51B89012', 'DO-20000101-000008', 81000),
('50H90123', 'DO-20000101-000009', 22500),
('51C01234', 'DO-20000101-000010', 96500),
('60K11223', 'DO-20000101-000011', 112000),
('51D22334', 'DO-20000101-000012', 54800),
('59C33445', 'DO-20000101-000013', 29400),
('51E44556', 'DO-20000101-000014', 124000),
('60B55667', 'DO-20000101-000015', 17300);

-- ============================================================
-- 7. TABLE: KhachHang_Xe (15)
-- ============================================================
INSERT INTO KhachHang_Xe (maKhachHang, bienSo, ngayTao) VALUES
('KH-20261020-000001', '51A12345', '2026-10-20T08:30:00'),
('KH-20261020-000002', '51H23456', '2026-10-20T09:30:00'),
('KH-20261022-000001', '60A34567', '2026-10-22T10:15:00'),
('KH-20261022-000002', '51K45678', '2026-10-22T14:20:00'),
('KH-20261025-000001', '59A56789', '2026-10-25T11:40:00'),
('KH-20261025-000002', '51G67890', '2026-10-25T09:00:00'),
('KH-20261101-000001', '61A78901', '2026-11-01T13:15:00'),
('KH-20261101-000002', '51B89012', '2026-11-01T16:40:00'),
('KH-20261105-000001', '50H90123', '2026-11-05T09:25:00'),
('KH-20261105-000002', '51C01234', '2026-11-05T10:55:00'),
('KH-20261110-000001', '60K11223', '2026-11-10T15:30:00'),
('KH-20261110-000002', '51D22334', '2026-11-10T08:50:00'),
('KH-20261115-000001', '59C33445', '2026-11-15T12:35:00'),
('KH-20261115-000002', '51E44556', '2026-11-15T10:10:00'),
('KH-20261115-000003', '60B55667', '2026-11-15T15:00:00');

-- ============================================================
-- 8. TABLE: PhieuNhapPhuTung (10)
-- Format: PN-yyyyMMdd-NNNNNN
-- ============================================================
INSERT INTO PhieuNhapPhuTung (maPhieuNhap, ngayTao, maNhanVien, moTa) VALUES
('PN-20261021-000001', '2026-10-21T09:00:00', 'NV-20261020-000001', N'Nhập dầu và lọc bảo dưỡng định kỳ'),
('PN-20261028-000001', '2026-10-28T10:15:00', 'NV-20261020-000001', N'Bổ sung vật tư phanh'),
('PN-20261103-000001', '2026-11-03T14:00:00', 'NV-20261020-000001', N'Nhập ắc quy và vật tư điện'),
('PN-20261112-000001', '2026-11-12T08:45:00', 'NV-20261020-000001', N'Nhập vật tư vệ sinh và điều hòa'),
('PN-20261120-000001', '2026-11-20T11:30:00', 'NV-20261020-000001', N'Nhập lọc gió và bugi'),
('PN-20261201-000001', '2026-12-01T13:20:00', 'NV-20261020-000001', N'Nhập nước làm mát và dầu phanh'),
('PN-20261208-000001', '2026-12-08T09:40:00', 'NV-20261020-000001', N'Nhập phụ tùng hệ thống treo'),
('PN-20261215-000001', '2026-12-15T15:10:00', 'NV-20261020-000001', N'Bổ sung gạt mưa'),
('PN-20261222-000001', '2026-12-22T10:00:00', 'NV-20261020-000001', N'Nhập phụ tùng thay thế thông dụng'),
('PN-20261228-000001', '2026-12-28T16:25:00', 'NV-20261020-000001', N'Nhập hàng phục vụ mùa bảo dưỡng');

-- ============================================================
-- 9. TABLE: ChiTietPhieuNhapPhuTung (20)
-- ============================================================
INSERT INTO ChiTietPhieuNhapPhuTung (maPhieuNhap, maPhuTung, soLuong, donGiaNhap) VALUES
('PN-20261021-000001', 'PT-20261020-000001', 30, 125000.00),
('PN-20261021-000001', 'PT-20261020-000002', 20, 65000.00),
('PN-20261028-000001', 'PT-20261020-000004', 10, 320000.00),
('PN-20261028-000001', 'PT-20261020-000013', 15, 95000.00),
('PN-20261103-000001', 'PT-20261020-000006', 5, 1450000.00),
('PN-20261103-000001', 'PT-20261020-000009', 12, 95000.00),
('PN-20261112-000001', 'PT-20261020-000007', 10, 180000.00),
('PN-20261112-000001', 'PT-20261020-000012', 15, 160000.00),
('PN-20261120-000001', 'PT-20261020-000003', 20, 75000.00),
('PN-20261120-000001', 'PT-20261020-000005', 24, 145000.00),
('PN-20261201-000001', 'PT-20261020-000008', 18, 85000.00),
('PN-20261201-000001', 'PT-20261020-000013', 12, 95000.00),
('PN-20261208-000001', 'PT-20261020-000014', 6, 480000.00),
('PN-20261208-000001', 'PT-20261020-000011', 8, 210000.00),
('PN-20261215-000001', 'PT-20261020-000009', 10, 95000.00),
('PN-20261215-000001', 'PT-20261020-000010', 10, 110000.00),
('PN-20261222-000001', 'PT-20261020-000015', 40, 25000.00),
('PN-20261222-000001', 'PT-20261020-000002', 15, 65000.00),
('PN-20261228-000001', 'PT-20261020-000001', 25, 125000.00),
('PN-20261228-000001', 'PT-20261020-000008', 20, 85000.00);

-- ============================================================
-- 10. TABLE: PhieuTiepNhan (15)
-- Format: PTN-yyyyMMdd-NNNNNN
-- ============================================================
INSERT INTO PhieuTiepNhan (maPhieuTiepNhan, ngayTao, moTa, trangThai, maNhanVien, maKhachHang, bienSo) VALUES
('PTN-20261022-000001', '2026-10-22T08:10:00', N'Bảo dưỡng định kỳ, thay dầu động cơ', 'HOAN_THANH', 'NV-20261020-000002', 'KH-20261020-000001', '51A12345'),
('PTN-20261025-000001', '2026-10-25T09:00:00', N'Xe rung khi phanh ở tốc độ cao', 'HOAN_THANH', 'NV-20261020-000002', 'KH-20261020-000002', '51H23456'),
('PTN-20261102-000001', '2026-11-02T08:35:00', N'Điều hòa làm lạnh yếu', 'HOAN_THANH', 'NV-20261020-000006', 'KH-20261022-000001', '60A34567'),
('PTN-20261108-000001', '2026-11-08T10:20:00', N'Kiểm tra tiếng kêu ở bánh trước', 'DANG_XU_LY', 'NV-20261020-000002', 'KH-20261022-000002', '51K45678'),
('PTN-20261114-000001', '2026-11-14T13:15:00', N'Rửa xe và vệ sinh nội thất', 'HOAN_THANH', 'NV-20261020-000006', 'KH-20261025-000001', '59A56789'),
('PTN-20261120-000001', '2026-11-20T08:50:00', N'Đèn báo lỗi động cơ bật sáng', 'DANG_XU_LY', 'NV-20261020-000002', 'KH-20261025-000002', '51G67890'),
('PTN-20261125-000001', '2026-11-25T09:30:00', N'Xe khó khởi động vào buổi sáng', 'HOAN_THANH', 'NV-20261020-000006', 'KH-20261101-000001', '61A78901'),
('PTN-20261201-000001', '2026-12-01T14:10:00', N'Bảo dưỡng sau chuyến đi dài', 'TIEP_NHAN', 'NV-20261020-000002', 'KH-20261101-000002', '51B89012'),
('PTN-20261205-000001', '2026-12-05T11:05:00', N'Kiểm tra hệ thống điện xe điện', 'DANG_XU_LY', 'NV-20261020-000006', 'KH-20261105-000001', '50H90123'),
('PTN-20261210-000001', '2026-12-10T08:25:00', N'Kiểm tra phanh và thay dầu phanh', 'HOAN_THANH', 'NV-20261020-000002', 'KH-20261105-000002', '51C01234'),
('PTN-20261215-000001', '2026-12-15T10:45:00', N'Xe hao nhiên liệu hơn bình thường', 'DANG_XU_LY', 'NV-20261020-000006', 'KH-20261110-000001', '60K11223'),
('PTN-20261220-000001', '2026-12-20T15:00:00', N'Thay gạt mưa và kiểm tra kính', 'DA_HUY', 'NV-20261020-000002', 'KH-20261110-000002', '51D22334'),
('PTN-20261224-000001', '2026-12-24T09:15:00', N'Bảo dưỡng định kỳ 30.000 km', 'TIEP_NHAN', 'NV-20261020-000006', 'KH-20261115-000001', '59C33445'),
('PTN-20261228-000001', '2026-12-28T13:40:00', N'Động cơ có tiếng gõ nhẹ', 'DANG_XU_LY', 'NV-20261020-000002', 'KH-20261115-000002', '51E44556'),
('PTN-20261230-000001', '2026-12-30T08:05:00', N'Kiểm tra tổng quát trước chuyến đi', 'TIEP_NHAN', 'NV-20261020-000006', 'KH-20261115-000003', '60B55667');

-- ============================================================
-- 11. TABLE: PhanCong (15)
-- Format: PC-yyyyMMdd-NNNNNN
-- ============================================================
INSERT INTO PhanCong (maPhanCong, maNhanVien, ngay, thang, nam, caLamViec, ghiChu) VALUES
('PC-20261022-000001', 'NV-20261020-000002', 22, 10, 2026, 'CA_SANG', N'Tiếp nhận đầu ngày'),
('PC-20261025-000001', 'NV-20261020-000006', 25, 10, 2026, 'CA_CHIEU', N'Tiếp nhận ca chiều'),
('PC-20261102-000001', 'NV-20261020-000004', 2, 11, 2026, 'CA_NGAY', N'Kỹ thuật tổng quát'),
('PC-20261108-000001', 'NV-20261020-000005', 8, 11, 2026, 'CA_SANG', N'Chuyên hệ thống điện'),
('PC-20261114-000001', 'NV-20261020-000003', 14, 11, 2026, 'CA_CHIEU', N'Thu ngân ca chiều'),
('PC-20261120-000001', 'NV-20261020-000002', 20, 11, 2026, 'CA_SANG', N'Tiếp nhận khách hẹn'),
('PC-20261125-000001', 'NV-20261020-000004', 25, 11, 2026, 'CA_CHIEU', N'Bảo dưỡng định kỳ'),
('PC-20261201-000001', 'NV-20261020-000005', 1, 12, 2026, 'CA_NGAY', N'Chẩn đoán lỗi động cơ'),
('PC-20261205-000001', 'NV-20261020-000003', 5, 12, 2026, 'CA_SANG', N'Thu ngân ca sáng'),
('PC-20261210-000001', 'NV-20261020-000002', 10, 12, 2026, 'CA_CHIEU', N'Tiếp nhận cuối tuần'),
('PC-20261215-000001', 'NV-20261020-000004', 15, 12, 2026, 'CA_SANG', N'Sửa chữa hệ thống phanh'),
('PC-20261220-000001', 'NV-20261020-000005', 20, 12, 2026, 'CA_CHIEU', N'Kiểm tra điều hòa'),
('PC-20261224-000001', 'NV-20261020-000006', 24, 12, 2026, 'CA_SANG', N'Đối soát hóa đơn'),
('PC-20261228-000001', 'NV-20261020-000004', 28, 12, 2026, 'CA_CHIEU', N'Sửa chữa tổng quát'),
('PC-20261230-000001', 'NV-20261020-000002', 30, 12, 2026, 'CA_SANG', N'Tiếp nhận cuối năm');

-- ============================================================
-- 12. TABLE: HoaDon (15)
-- Format: HD-yyyyMMdd-NNNNNN
-- Unique maPhieuTiepNhan constraint enforced
-- ============================================================
INSERT INTO HoaDon (maHoaDon, maPhieuTiepNhan, maVoucher, trangThaiHoaDon, lyDoHuy, ghiChu, ngayTao, thoiGianThanhToan, mucGiamApDung) VALUES
('HD-20261022-000001', 'PTN-20261022-000001', 'VC-20261020-000001', 'DA_THANH_TOAN', NULL, N'Đã thanh toán tiền mặt', '2026-10-22T08:15:00', '2026-10-22T10:10:00', 0.1000),
('HD-20261025-000001', 'PTN-20261025-000001', 'VC-20261020-000004', 'DA_THANH_TOAN', NULL, N'Thanh toán chuyển khoản', '2026-10-25T09:05:00', '2026-10-25T11:30:00', 0.1200),
('HD-20261102-000001', 'PTN-20261102-000001', 'VC-20261020-000009', 'DA_THANH_TOAN', NULL, N'Đã thanh toán tại quầy', '2026-11-02T08:40:00', '2026-11-02T11:00:00', 0.0500),
('HD-20261108-000001', 'PTN-20261108-000001', NULL, 'CHO_THANH_TOAN', NULL, N'Đang kiểm tra phụ tùng', '2026-11-08T10:25:00', NULL, NULL),
('HD-20261114-000001', 'PTN-20261114-000001', 'VC-20261020-000003', 'DA_THANH_TOAN', NULL, N'Khách thanh toán tại quầy', '2026-11-14T13:20:00', '2026-11-14T14:10:00', 0.0500),
('HD-20261120-000001', 'PTN-20261120-000001', 'VC-20261020-000006', 'CHO_THANH_TOAN', NULL, N'Chờ hoàn tất chẩn đoán', '2026-11-20T08:55:00', NULL, 0.1000),
('HD-20261125-000001', 'PTN-20261125-000001', NULL, 'DA_THANH_TOAN', NULL, N'Thanh toán thẻ', '2026-11-25T09:35:00', '2026-11-25T11:00:00', NULL),
('HD-20261201-000001', 'PTN-20261201-000001', NULL, 'CHO_THANH_TOAN', NULL, N'Phiếu mới tiếp nhận', '2026-12-01T14:15:00', NULL, NULL),
('HD-20261205-000001', 'PTN-20261205-000001', 'VC-20261020-000008', 'CHO_THANH_TOAN', NULL, N'Chờ kiểm tra kỹ thuật', '2026-12-05T11:10:00', NULL, 0.0700),
('HD-20261210-000001', 'PTN-20261210-000001', 'VC-20261020-000004', 'DA_THANH_TOAN', NULL, N'Đã thanh toán chuyển khoản', '2026-12-10T08:30:00', '2026-12-10T10:50:00', 0.1200),
('HD-20261215-000001', 'PTN-20261215-000001', NULL, 'CHO_THANH_TOAN', NULL, N'Chờ khách duyệt phương án sửa', '2026-12-15T10:50:00', NULL, NULL),
('HD-20261220-000001', 'PTN-20261220-000001', NULL, 'DA_HUY', N'Khách đổi lịch và hủy dịch vụ', N'Hóa đơn bị hủy theo yêu cầu khách', '2026-12-20T15:05:00', NULL, NULL),
('HD-20261224-000001', 'PTN-20261224-000001', 'VC-20261020-000002', 'CHO_THANH_TOAN', NULL, N'Chờ kiểm tra xe', '2026-12-24T09:20:00', NULL, 0.1500),
('HD-20261228-000001', 'PTN-20261228-000001', NULL, 'CHO_THANH_TOAN', NULL, N'Chờ khách đồng ý sửa chữa', '2026-12-28T13:45:00', NULL, NULL),
('HD-20261230-000001', 'PTN-20261230-000001', 'VC-20261020-000007', 'CHO_THANH_TOAN', NULL, N'Chờ lập báo giá', '2026-12-30T08:10:00', NULL, 0.2000);

-- ============================================================
-- 13. TABLE: ChiTietHoaDon (30)
-- maSanPham liên kết đúng mã DichVu và PhuTung chuẩn mới
-- ============================================================
INSERT INTO ChiTietHoaDon (maHoaDon, loaiSanPham, maSanPham, soLuong, donGia) VALUES
('HD-20261022-000001', 'DICH_VU', 'DV-20261020-000001', 1, 250000.00),
('HD-20261022-000001', 'PHU_TUNG', 'PT-20261020-000001', 4, 155000.00),
('HD-20261025-000001', 'DICH_VU', 'DV-20261020-000004', 1, 180000.00),
('HD-20261025-000001', 'PHU_TUNG', 'PT-20261020-000004', 1, 390000.00),
('HD-20261102-000001', 'DICH_VU', 'DV-20261020-000006', 1, 150000.00),
('HD-20261102-000001', 'DICH_VU', 'DV-20261020-000007', 1, 550000.00),
('HD-20261108-000001', 'DICH_VU', 'DV-20261020-000014', 1, 400000.00),
('HD-20261108-000001', 'PHU_TUNG', 'PT-20261020-000014', 1, 590000.00),
('HD-20261114-000001', 'DICH_VU', 'DV-20261020-000003', 1, 450000.00),
('HD-20261114-000001', 'DICH_VU', 'DV-20261020-000002', 1, 80000.00),
('HD-20261120-000001', 'DICH_VU', 'DV-20261020-000011', 1, 300000.00),
('HD-20261120-000001', 'PHU_TUNG', 'PT-20261020-000003', 1, 95000.00),
('HD-20261125-000001', 'DICH_VU', 'DV-20261020-000009', 1, 70000.00),
('HD-20261125-000001', 'PHU_TUNG', 'PT-20261020-000006', 1, 1650000.00),
('HD-20261201-000001', 'DICH_VU', 'DV-20261020-000010', 1, 950000.00),
('HD-20261201-000001', 'PHU_TUNG', 'PT-20261020-000002', 1, 85000.00),
('HD-20261205-000001', 'DICH_VU', 'DV-20261020-000011', 1, 300000.00),
('HD-20261205-000001', 'DICH_VU', 'DV-20261020-000009', 1, 70000.00),
('HD-20261210-000001', 'DICH_VU', 'DV-20261020-000004', 1, 180000.00),
('HD-20261210-000001', 'PHU_TUNG', 'PT-20261020-000013', 1, 125000.00),
('HD-20261215-000001', 'DICH_VU', 'DV-20261020-000015', 1, 600000.00),
('HD-20261215-000001', 'PHU_TUNG', 'PT-20261020-000012', 1, 195000.00),
('HD-20261220-000001', 'DICH_VU', 'DV-20261020-000002', 1, 80000.00),
('HD-20261220-000001', 'PHU_TUNG', 'PT-20261020-000009', 2, 120000.00),
('HD-20261224-000001', 'DICH_VU', 'DV-20261020-000010', 1, 950000.00),
('HD-20261224-000001', 'PHU_TUNG', 'PT-20261020-000008', 2, 110000.00),
('HD-20261228-000001', 'DICH_VU', 'DV-20261020-000013', 4, 220000.00),
('HD-20261228-000001', 'PHU_TUNG', 'PT-20261020-000005', 4, 175000.00),
('HD-20261230-000001', 'DICH_VU', 'DV-20261020-000009', 1, 70000.00),
('HD-20261230-000001', 'DICH_VU', 'DV-20261020-000005', 1, 350000.00);

-- ============================================================
-- 14. TABLE: VanDeKyThuat (15)
-- Format: VD-yyyyMMdd-NNNNNN
-- ============================================================
INSERT INTO VanDeKyThuat (maVanDe, maPhieuTiepNhan, tieuDe, moTa, mucDoUuTien, giaiPhap, khachHangDongY, ngayTao) VALUES
('VD-20261022-000001', 'PTN-20261022-000001', N'Dầu động cơ đã xuống cấp', N'Dầu cũ đổi màu, đến hạn bảo dưỡng', 30, N'Thay dầu động cơ và kiểm tra rò rỉ', 'CO', '2026-10-22T08:30:00'),
('VD-20261025-000001', 'PTN-20261025-000001', N'Rung khi phanh', N'Đĩa phanh có dấu hiệu mòn không đều', 75, N'Kiểm tra độ đảo đĩa và thay má phanh nếu cần', 'CO', '2026-10-25T09:20:00'),
('VD-20261102-000001', 'PTN-20261102-000001', N'Điều hòa làm lạnh yếu', N'Áp suất gas thấp hơn mức khuyến nghị', 60, N'Kiểm tra rò rỉ trước khi nạp gas', 'CO', '2026-11-02T09:00:00'),
('VD-20261108-000001', 'PTN-20261108-000001', N'Tiếng kêu bánh trước', N'Phát hiện tiếng rít khi xe đi qua gờ giảm tốc', 70, N'Kiểm tra bi moay-ơ và các khớp hệ thống treo', 'CHUA_XAC_NHAN', '2026-11-08T10:45:00'),
('VD-20261114-000001', 'PTN-20261114-000001', N'Nội thất bám bụi', N'Bụi bẩn tập trung ở thảm sàn và khe gió', 20, N'Vệ sinh nội thất và khử mùi nhẹ', 'CO', '2026-11-14T13:30:00'),
('VD-20261120-000001', 'PTN-20261120-000001', N'Đèn báo lỗi động cơ', N'Mã lỗi cần được đọc bằng máy chẩn đoán', 85, N'Đọc mã lỗi ECU và kiểm tra cảm biến liên quan', 'CHUA_XAC_NHAN', '2026-11-20T09:10:00'),
('VD-20261125-000001', 'PTN-20261125-000001', N'Ắc quy yếu', N'Điện áp khởi động thấp sau khi xe để qua đêm', 65, N'Test tải ắc quy và kiểm tra hệ thống sạc', 'CO', '2026-11-25T09:45:00'),
('VD-20261201-000001', 'PTN-20261201-000001', N'Đến hạn bảo dưỡng', N'Odo gần mốc bảo dưỡng định kỳ', 35, N'Kiểm tra tổng quát theo danh mục bảo dưỡng', 'CHUA_XAC_NHAN', '2026-12-01T14:25:00'),
('VD-20261205-000001', 'PTN-20261205-000001', N'Cảnh báo hệ thống điện', N'Cần kiểm tra thông báo lỗi trên bảng điều khiển', 90, N'Chẩn đoán theo quy trình dành cho xe điện', 'KHONG', '2026-12-05T11:20:00'),
('VD-20261210-000001', 'PTN-20261210-000001', N'Dầu phanh cần thay', N'Dầu phanh đã sử dụng lâu và cần kiểm tra độ ẩm', 55, N'Thay dầu phanh DOT 4 và xả gió hệ thống', 'CO', '2026-12-10T08:45:00'),
('VD-20261215-000001', 'PTN-20261215-000001', N'Hao nhiên liệu', N'Khách hàng phản ánh mức tiêu thụ tăng', 60, N'Kiểm tra lọc gió, bugi và hệ thống phun nhiên liệu', 'CHUA_XAC_NHAN', '2026-12-15T11:00:00'),
('VD-20261220-000001', 'PTN-20261220-000001', N'Yêu cầu thay gạt mưa', N'Khách hàng hủy lịch trước khi thực hiện', 15, N'Chưa thực hiện; đóng hồ sơ theo yêu cầu hủy', 'KHONG', '2026-12-20T15:15:00'),
('VD-20261224-000001', 'PTN-20261224-000001', N'Bảo dưỡng mốc 30.000 km', N'Cần kiểm tra lọc gió, dầu và các hạng mục định kỳ', 40, N'Lập danh sách hạng mục và báo giá trước khi làm', 'CHUA_XAC_NHAN', '2026-12-24T09:30:00'),
('VD-20261228-000001', 'PTN-20261228-000001', N'Tiếng gõ động cơ', N'Tiếng gõ nhẹ xuất hiện khi tăng tốc', 80, N'Kiểm tra dầu, bugi và chẩn đoán động cơ trước khi tháo lắp', 'CHUA_XAC_NHAN', '2026-12-28T14:00:00'),
('VD-20261230-000001', 'PTN-20261230-000001', N'Kiểm tra trước chuyến đi', N'Khách hàng muốn rà soát các hạng mục an toàn', 25, N'Kiểm tra lốp, phanh, đèn, nước làm mát và ắc quy', 'CHUA_XAC_NHAN', '2026-12-30T08:20:00');

-- ============================================================
-- 15. TABLE: KhieuNai (15)
-- Format: KN-yyyyMMdd-NNNNNN
-- ============================================================
INSERT INTO KhieuNai (maKhieuNai, maKhachHang, maPhieuTiepNhan, noiDung, trangThai, ngayTao) VALUES
('KN-20261023-000001', 'KH-20261020-000001', 'PTN-20261022-000001', N'Thời gian chờ đợi lâu, chưa được thông báo tiến độ', 'DA_XU_LY', '2026-10-23T09:00:00'),
('KN-20261026-000001', 'KH-20261020-000002', 'PTN-20261025-000001', N'Hóa đơn tính phí cao hơn báo giá ban đầu', 'DA_XU_LY', '2026-10-26T10:30:00'),
('KN-20261103-000001', 'KH-20261022-000001', 'PTN-20261102-000001', N'Điều hòa sau khi nạp gas vẫn chưa mát', 'DA_XU_LY', '2026-11-03T14:15:00'),
('KN-20261109-000001', 'KH-20261022-000002', 'PTN-20261108-000001', N'Tiếng kêu ở bánh trước vẫn còn sau khi kiểm tra', 'DANG_XU_LY', '2026-11-09T08:45:00'),
('KN-20261115-000001', 'KH-20261025-000001', 'PTN-20261114-000001', N'Vệ sinh nội thất chưa sạch, xe vẫn còn mùi', 'DA_XU_LY', '2026-11-15T09:20:00'),
('KN-20261121-000001', 'KH-20261025-000002', 'PTN-20261120-000001', N'Chưa được tư vấn rõ ràng về mã lỗi động cơ', 'CHUA_XU_LY', '2026-11-21T11:10:00'),
('KN-20261126-000001', 'KH-20261101-000001', 'PTN-20261125-000001', N'Xe vẫn khó khởi động sau khi thay ắc quy', 'DANG_XU_LY', '2026-11-26T15:40:00'),
('KN-20261202-000001', 'KH-20261101-000002', 'PTN-20261201-000001', N'Thời gian bảo dưỡng vượt quá hẹn', 'DA_XU_LY', '2026-12-02T08:30:00'),
('KN-20261206-000001', 'KH-20261105-000001', 'PTN-20261205-000001', N'Phụ tùng thay không đúng loại đã báo', 'CHUA_XU_LY', '2026-12-06T13:05:00'),
('KN-20261211-000001', 'KH-20261105-000002', 'PTN-20261210-000001', N'Dầu phanh bị tràn ra ngoài trong quá trình thay', 'DANG_XU_LY', '2026-12-11T10:15:00'),
('KN-20261216-000001', 'KH-20261110-000001', 'PTN-20261215-000001', N'Tình trạng hao nhiên liệu vẫn chưa được khắc phục', 'CHUA_XU_LY', '2026-12-16T09:50:00'),
('KN-20261221-000001', 'KH-20261110-000002', 'PTN-20261220-000001', N'Đã hủy dịch vụ nhưng vẫn bị tính phí', 'DA_XU_LY', '2026-12-21T14:25:00'),
('KN-20261225-000001', 'KH-20261115-000001', 'PTN-20261224-000001', N'Danh mục bảo dưỡng thực hiện thiếu so với báo giá', 'DANG_XU_LY', '2026-12-25T08:40:00'),
('KN-20261229-000001', 'KH-20261115-000002', 'PTN-20261228-000001', N'Tiếng gõ động cơ chưa được xử lý dứt điểm', 'CHUA_XU_LY', '2026-12-29T16:00:00'),
('KN-20261230-000001', 'KH-20261115-000003', 'PTN-20261230-000001', N'Chưa nhận được báo giá trước khi thực hiện dịch vụ', 'DANG_XU_LY', '2026-12-30T11:35:00');

-- ============================================================
-- 16. TABLE: DayMetric
-- ============================================================
INSERT INTO DayMetric (ngay, target, amount) VALUES
('2026-10-20', 'DV',  15),
('2026-10-20', 'PT',  15),
('2026-10-20', 'VC',  10),
('2026-10-20', 'NV',   8),
('2026-10-20', 'KH',   2),
('2026-10-20', 'NCC',  4),
('2026-10-21', 'PN',   1),
('2026-10-22', 'KH',   2),
('2026-10-22', 'PTN',  1),
('2026-10-22', 'VD',   1),
('2026-10-22', 'HD',   1),
('2026-10-22', 'PC',   1),
('2026-10-23', 'KN',   1),
('2026-10-25', 'KH',   2),
('2026-10-25', 'PTN',  1),
('2026-10-25', 'VD',   1),
('2026-10-25', 'HD',   1),
('2026-10-25', 'PC',   1),
('2026-10-26', 'KN',   1),
('2026-10-28', 'PN',   1),
('2026-11-01', 'KH',   2),
('2026-11-02', 'PTN',  1),
('2026-11-02', 'VD',   1),
('2026-11-02', 'HD',   1),
('2026-11-02', 'PC',   1),
('2026-11-03', 'PN',   1),
('2026-11-03', 'KN',   1),
('2026-11-05', 'KH',   2),
('2026-11-08', 'PTN',  1),
('2026-11-08', 'VD',   1),
('2026-11-08', 'HD',   1),
('2026-11-08', 'PC',   1),
('2026-11-09', 'KN',   1),
('2026-11-10', 'KH',   2),
('2026-11-12', 'PN',   1),
('2026-11-14', 'PTN',  1),
('2026-11-14', 'VD',   1),
('2026-11-14', 'HD',   1),
('2026-11-14', 'PC',   1),
('2026-11-15', 'KH',   3),
('2026-11-15', 'KN',   1),
('2026-11-20', 'PN',   1),
('2026-11-20', 'PTN',  1),
('2026-11-20', 'VD',   1),
('2026-11-20', 'HD',   1),
('2026-11-20', 'PC',   1),
('2026-11-21', 'KN',   1),
('2026-11-25', 'PTN',  1),
('2026-11-25', 'VD',   1),
('2026-11-25', 'HD',   1),
('2026-11-25', 'PC',   1),
('2026-11-26', 'KN',   1),
('2026-12-01', 'PN',   1),
('2026-12-01', 'PTN',  1),
('2026-12-01', 'VD',   1),
('2026-12-01', 'HD',   1),
('2026-12-01', 'PC',   1),
('2026-12-02', 'KN',   1),
('2026-12-05', 'PTN',  1),
('2026-12-05', 'VD',   1),
('2026-12-05', 'HD',   1),
('2026-12-05', 'PC',   1),
('2026-12-06', 'KN',   1),
('2026-12-08', 'PN',   1),
('2026-12-10', 'PTN',  1),
('2026-12-10', 'VD',   1),
('2026-12-10', 'HD',   1),
('2026-12-10', 'PC',   1),
('2026-12-11', 'KN',   1),
('2026-12-15', 'PN',   1),
('2026-12-15', 'PTN',  1),
('2026-12-15', 'VD',   1),
('2026-12-15', 'HD',   1),
('2026-12-15', 'PC',   1),
('2026-12-16', 'KN',   1),
('2026-12-20', 'PTN',  1),
('2026-12-20', 'VD',   1),
('2026-12-20', 'HD',   1),
('2026-12-20', 'PC',   1),
('2026-12-21', 'KN',   1),
('2026-12-22', 'PN',   1),
('2026-12-24', 'PTN',  1),
('2026-12-24', 'VD',   1),
('2026-12-24', 'HD',   1),
('2026-12-24', 'PC',   1),
('2026-12-25', 'KN',   1),
('2026-12-28', 'PN',   1),
('2026-12-28', 'PTN',  1),
('2026-12-28', 'VD',   1),
('2026-12-28', 'HD',   1),
('2026-12-28', 'PC',   1),
('2026-12-29', 'KN',   1),
('2026-12-30', 'PTN',  1),
('2026-12-30', 'VD',   1),
('2026-12-30', 'HD',   1),
('2026-12-30', 'KN',   1),
('2026-12-30', 'PC',   1);

COMMIT TRANSACTION;