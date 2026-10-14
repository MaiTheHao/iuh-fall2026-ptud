SET XACT_ABORT ON;
BEGIN TRANSACTION;

-- ============================================================
-- DichVu (15)
-- ============================================================
INSERT INTO DichVu (maDichVu, tenDichVu, moTa, anhUrl, donGia) VALUES
('DV001', N'Thay dầu động cơ', N'Thay dầu động cơ và kiểm tra mức dầu', 'https://images.unsplash.com/photo-1580273916550-e323be2ae537?auto=format&fit=crop&w=200&q=80', 250000.00),
('DV002', N'Rửa xe cơ bản', N'Rửa thân xe, kính và vệ sinh bánh xe', 'https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=200&q=80', 80000.00),
('DV003', N'Rửa xe detailing', N'Vệ sinh kỹ ngoại thất và nội thất', 'https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&w=200&q=80', 450000.00),
('DV004', N'Kiểm tra hệ thống phanh', N'Kiểm tra má phanh, đĩa phanh và dầu phanh', 'https://images.unsplash.com/photo-1621007947382-bb3c3994e3fb?auto=format&fit=crop&w=200&q=80', 180000.00),
('DV005', N'Cân chỉnh góc đặt bánh xe', N'Cân chỉnh góc đặt bánh xe bằng thiết bị chuyên dụng', 'https://images.unsplash.com/photo-1619642751034-765dfdf7c58e?auto=format&fit=crop&w=200&q=80', 350000.00),
('DV006', N'Kiểm tra điều hòa', N'Kiểm tra áp suất gas và hiệu suất làm lạnh', 'https://images.unsplash.com/photo-1449965408869-eaa3f722e40d?auto=format&fit=crop&w=200&q=80', 150000.00),
('DV007', N'Nạp gas điều hòa', N'Nạp bổ sung gas điều hòa ô tô', 'https://images.unsplash.com/photo-1517524008697-84bbe3c3fd98?auto=format&fit=crop&w=200&q=80', 550000.00),
('DV008', N'Thay lọc gió động cơ', N'Thay lọc gió và vệ sinh hộp lọc', 'https://images.unsplash.com/photo-1511919884226-fd3cad34687c?auto=format&fit=crop&w=200&q=80', 120000.00),
('DV009', N'Kiểm tra ắc quy', N'Đo điện áp và kiểm tra khả năng khởi động', 'https://images.unsplash.com/photo-1494905998402-395d579af36f?auto=format&fit=crop&w=200&q=80', 70000.00),
('DV010', N'Bảo dưỡng định kỳ 10.000 km', N'Kiểm tra tổng quát theo mốc bảo dưỡng', 'https://images.unsplash.com/photo-1507136566006-cfc505b114fc?auto=format&fit=crop&w=200&q=80', 950000.00),
('DV011', N'Chẩn đoán lỗi ECU', N'Đọc mã lỗi và lập báo cáo chẩn đoán', 'https://images.unsplash.com/photo-1489824904134-891ab64532f1?auto=format&fit=crop&w=200&q=80', 300000.00),
('DV012', N'Thay má phanh trước', N'Tháo lắp và thay má phanh trước', 'https://images.unsplash.com/photo-1621607512214-68297480165e?auto=format&fit=crop&w=200&q=80', 280000.00),
('DV013', N'Thay bugi', N'Thay bugi và kiểm tra hệ thống đánh lửa', 'https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?auto=format&fit=crop&w=200&q=80', 220000.00),
('DV014', N'Sửa chữa hệ thống treo', N'Kiểm tra tiếng kêu và các chi tiết hệ thống treo', 'https://images.unsplash.com/photo-1502161254066-6c74afbf07aa?auto=format&fit=crop&w=200&q=80', 400000.00),
('DV015', N'Vệ sinh kim phun', N'Vệ sinh kim phun và kiểm tra hoạt động động cơ', 'https://images.unsplash.com/photo-1583121274602-3e2820c69888?auto=format&fit=crop&w=200&q=80', 600000.00);

-- ============================================================
-- KhachHang (15)
-- ============================================================
INSERT INTO KhachHang (maKhachHang, sdt, hoTen, ngayTao) VALUES
('KH001', '0901000001', N'Nguyễn Minh Anh', '2026-01-05T08:15:00'),
('KH002', '0901000002', N'Trần Quốc Bảo', '2026-01-08T09:20:00'),
('KH003', '0901000003', N'Lê Hoàng Nam', '2026-01-12T10:05:00'),
('KH004', '0901000004', N'Phạm Thu Hà', '2026-01-18T14:10:00'),
('KH005', '0901000005', N'Võ Thành Đạt', '2026-01-22T11:30:00'),
('KH006', '0901000006', N'Đặng Ngọc Mai', '2026-02-02T08:45:00'),
('KH007', '0901000007', N'Bùi Đức Huy', '2026-02-10T13:00:00'),
('KH008', '0901000008', N'Đỗ Khánh Linh', '2026-02-16T16:25:00'),
('KH009', '0901000009', N'Huỳnh Gia Phúc', '2026-02-20T09:10:00'),
('KH010', '0901000010', N'Phan Thảo Vy', '2026-03-01T10:40:00'),
('KH011', '0901000011', N'Ngô Nhật Minh', '2026-03-08T15:15:00'),
('KH012', '0901000012', N'Dương Mỹ Duyên', '2026-03-12T08:35:00'),
('KH013', '0901000013', N'Đinh Tuấn Kiệt', '2026-03-20T12:20:00'),
('KH014', '0901000014', N'Vũ Bảo Ngọc', '2026-04-02T09:55:00'),
('KH015', '0901000015', N'Phạm Anh Khoa', '2026-04-10T14:45:00');

-- ============================================================
-- NhanVien (8; covers every VaiTro and both statuses)
-- ============================================================
INSERT INTO NhanVien (maNhanVien, tenDangNhap, matKhau, hoTen, anhDaiDien, vaiTro, trangThai, ngayTao) VALUES
('NV011120250001', 'admin', 'test@123', N'Administrator', NULL, 'NHAN_VIEN_QUAN_LY', 'HOAT_DONG', '2025-11-01T08:00:00'),
('NV031120250002', 'tiepnhan01', 'test@123', N'Nguyễn Thị Mỹ Quốc', NULL, 'NHAN_VIEN_TIEP_NHAN', 'HOAT_DONG', '2025-11-03T08:00:00'),
('NV051120250003', 'thungan01', 'test@123', N'Trần Thị Thanh Bình', NULL, 'NHAN_VIEN_THU_NGAN', 'HOAT_DONG', '2025-11-05T08:00:00'),
('NV071120250004', 'kythuat01', 'test@123', N'Phạm Nát Bét', NULL, 'NHAN_VIEN_KY_THUAT', 'HOAT_DONG', '2025-11-07T08:00:00'),
('NV101120250005', 'kythuat02', 'test@123', N'Võ Hoàng Long', NULL, 'NHAN_VIEN_KY_THUAT', 'HOAT_DONG', '2025-11-10T08:00:00'),
('NV121120250006', 'tiepnhan02', 'test@123', N'Đặng Thùy Dung', NULL, 'NHAN_VIEN_TIEP_NHAN', 'HOAT_DONG', '2025-11-12T08:00:00'),
('NV151120250007', 'thungan02', 'test@123', N'Bùi Thanh Tâm', NULL, 'NHAN_VIEN_THU_NGAN', 'DA_KHOA', '2025-11-15T08:00:00'),
('NV181120250008', 'quanly02', 'test@123', N'Đỗ Quốc Việt', NULL, 'NHAN_VIEN_QUAN_LY', 'DA_KHOA', '2025-11-18T08:00:00');

-- ============================================================
-- PhuTung (15)
-- ============================================================
INSERT INTO PhuTung (maPhuTung, tenPhuTung, moTa, anhUrl, donGiaNhap, soLuongTon) VALUES
('PT001', N'Dầu động cơ 5W-30 1L', N'Dầu tổng hợp dùng cho động cơ xăng', 'https://images.unsplash.com/photo-1486262715619-67b85e0b08d3?auto=format&fit=crop&w=200&q=80', 125000.00, 48),
('PT002', N'Lọc dầu động cơ', N'Lọc dầu thay thế phổ thông', 'https://images.unsplash.com/photo-1600790142055-619df03207e6?auto=format&fit=crop&w=200&q=80', 65000.00, 32),
('PT003', N'Lọc gió động cơ sedan', N'Lọc gió giấy xếp', 'https://images.unsplash.com/photo-1492144534655-ae79c964c9d7?auto=format&fit=crop&w=200&q=80', 75000.00, 24),
('PT004', N'Má phanh trước Toyota', N'Bộ má phanh trước tương thích một số dòng Toyota', 'https://images.unsplash.com/photo-1502877338535-766e1452684a?auto=format&fit=crop&w=200&q=80', 320000.00, 12),
('PT005', N'Bugi iridium', N'Bugi đánh lửa tuổi thọ cao', 'https://images.unsplash.com/photo-1493238792000-8113da705763?auto=format&fit=crop&w=200&q=80', 145000.00, 40),
('PT006', N'Ắc quy 12V 60Ah', N'Ắc quy kín khí dùng cho xe du lịch', 'https://images.unsplash.com/photo-1568605117036-5fe5e7bab0b7?auto=format&fit=crop&w=200&q=80', 1450000.00, 8),
('PT007', N'Gas điều hòa R134a', N'Gas nạp bổ sung điều hòa', 'https://images.unsplash.com/photo-1552519507-da3b142c6e3d?auto=format&fit=crop&w=200&q=80', 180000.00, 15),
('PT008', N'Nước làm mát 1L', N'Nước làm mát động cơ pha sẵn', 'https://images.unsplash.com/photo-1592198084033-aade902d1aae?auto=format&fit=crop&w=200&q=80', 85000.00, 30),
('PT009', N'Gạt mưa 16 inch', N'Lưỡi gạt mưa thay thế', 'https://images.unsplash.com/photo-1581092918056-0c4c3acd3789?auto=format&fit=crop&w=200&q=80', 95000.00, 18),
('PT010', N'Gạt mưa 24 inch', N'Lưỡi gạt mưa thay thế', 'https://images.unsplash.com/photo-1600661653561-629509216228?auto=format&fit=crop&w=200&q=80', 110000.00, 16),
('PT011', N'Dây curoa phụ trợ', N'Dây truyền động phụ trợ động cơ', 'https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=200&q=80', 210000.00, 10),
('PT012', N'Dung dịch vệ sinh kim phun', N'Dung dịch vệ sinh hệ thống nhiên liệu', 'https://images.unsplash.com/photo-1621007947382-bb3c3994e3fb?auto=format&fit=crop&w=200&q=80', 160000.00, 22),
('PT013', N'Dầu phanh DOT 4', N'Dầu phanh đạt tiêu chuẩn DOT 4', 'https://images.unsplash.com/photo-1619642751034-765dfdf7c58e?auto=format&fit=crop&w=200&q=80', 95000.00, 20),
('PT014', N'Bi moay-ơ trước', N'Vòng bi moay-ơ thay thế', 'https://images.unsplash.com/photo-1449965408869-eaa3f722e40d?auto=format&fit=crop&w=200&q=80', 480000.00, 6),
('PT015', N'Kẹp lọc dầu', N'Phụ kiện lắp đặt lọc dầu', 'https://images.unsplash.com/photo-1517524008697-84bbe3c3fd98?auto=format&fit=crop&w=200&q=80', 25000.00, 50);

-- ============================================================
-- Voucher (10)
-- mucGiam là tỷ lệ giảm: 0.1000 = 10%
-- ============================================================
INSERT INTO Voucher (maVoucher, tenChuongTrinh, moTa, mucGiam, thoiHanApDung, trangThaiKichHoat) VALUES
('VC001', N'Khách hàng mới', N'Giảm 10% cho lần sử dụng dịch vụ đầu tiên', 0.1000, '2027-12-31T23:59:59', 1),
('VC002', N'Bảo dưỡng mùa hè', N'Ưu đãi bảo dưỡng định kỳ', 0.1500, '2027-06-30T23:59:59', 1),
('VC003', N'Rửa xe cuối tuần', N'Giảm 5% dịch vụ rửa xe', 0.0500, '2027-03-31T23:59:59', 1),
('VC004', N'Tri ân khách hàng', N'Ưu đãi khách hàng thân thiết', 0.1200, '2027-12-31T23:59:59', 1),
('VC005', N'Tết an toàn', N'Ưu đãi kiểm tra xe dịp lễ', 0.0800, '2027-02-28T23:59:59', 0),
('VC006', N'Combo thay dầu', N'Ưu đãi khi thay dầu và lọc dầu', 0.1000, '2027-09-30T23:59:59', 1),
('VC007', N'Sinh nhật gara', N'Khuyến mãi kỷ niệm thành lập gara', 0.2000, '2027-08-31T23:59:59', 1),
('VC008', N'Khách hàng doanh nghiệp', N'Ưu đãi theo chương trình đối tác', 0.0700, '2027-12-31T23:59:59', 1),
('VC009', N'Kiểm tra điều hòa', N'Giảm giá kiểm tra điều hòa', 0.0500, '2026-12-31T23:59:59', 1),
('VC010', N'Ưu đãi hết hạn', N'Dữ liệu mẫu voucher đã ngừng kích hoạt', 0.2500, '2026-01-31T23:59:59', 0);

-- ============================================================
-- Xe (15)
-- ============================================================
INSERT INTO Xe (bienSo, hangXe, dongXe, doiXe, odo) VALUES
('51A-123.45', N'Toyota', N'Vios', 2021, 45200),
('51H-234.56', N'Honda', N'City', 2022, 31800),
('60A-345.67', N'Hyundai', N'Accent', 2020, 68500),
('51K-456.78', N'Mazda', N'Mazda 3', 2023, 19200),
('59A-567.89', N'Kia', N'Seltos', 2022, 36700),
('51G-678.90', N'Toyota', N'Corolla Cross', 2024, 8500),
('61A-789.01', N'Ford', N'Ranger', 2021, 73400),
('51B-890.12', N'Mitsubishi', N'Xpander', 2020, 81000),
('50H-901.23', N'VinFast', N'VF e34', 2023, 22500),
('51C-012.34', N'Honda', N'CR-V', 2019, 96500),
('60K-112.23', N'Toyota', N'Fortuner', 2018, 112000),
('51D-223.34', N'Mazda', N'CX-5', 2021, 54800),
('59C-334.45', N'Hyundai', N'Tucson', 2022, 29400),
('51E-445.56', N'Kia', N'Morning', 2017, 124000),
('60B-556.67', N'Suzuki', N'XL7', 2023, 17300);

-- ============================================================
-- KhachHang_Xe (15)
-- ============================================================
INSERT INTO KhachHang_Xe (maKhachHang, bienSo, ngayTao) VALUES
('KH001', '51A-123.45', '2026-01-05T08:30:00'),
('KH002', '51H-234.56', '2026-01-08T09:30:00'),
('KH003', '60A-345.67', '2026-01-12T10:15:00'),
('KH004', '51K-456.78', '2026-01-18T14:20:00'),
('KH005', '59A-567.89', '2026-01-22T11:40:00'),
('KH006', '51G-678.90', '2026-02-02T09:00:00'),
('KH007', '61A-789.01', '2026-02-10T13:15:00'),
('KH008', '51B-890.12', '2026-02-16T16:40:00'),
('KH009', '50H-901.23', '2026-02-20T09:25:00'),
('KH010', '51C-012.34', '2026-03-01T10:55:00'),
('KH011', '60K-112.23', '2026-03-08T15:30:00'),
('KH012', '51D-223.34', '2026-03-12T08:50:00'),
('KH013', '59C-334.45', '2026-03-20T12:35:00'),
('KH014', '51E-445.56', '2026-04-02T10:10:00'),
('KH015', '60B-556.67', '2026-04-10T15:00:00');

-- ============================================================
-- PhieuNhapPhuTung (10)
-- ============================================================
INSERT INTO PhieuNhapPhuTung (maPhieuNhap, ngayTao, maNhanVien, moTa) VALUES
('PN001', '2026-01-06T09:00:00', 'NV011120250001', N'Nhập dầu và lọc bảo dưỡng định kỳ'),
('PN002', '2026-01-20T10:15:00', 'NV011120250001', N'Bổ sung vật tư phanh'),
('PN003', '2026-02-03T14:00:00', 'NV011120250001', N'Nhập ắc quy và vật tư điện'),
('PN004', '2026-02-18T08:45:00', 'NV011120250001', N'Nhập vật tư vệ sinh và điều hòa'),
('PN005', '2026-03-02T11:30:00', 'NV011120250001', N'Nhập lọc gió và bugi'),
('PN006', '2026-03-15T13:20:00', 'NV011120250001', N'Nhập nước làm mát và dầu phanh'),
('PN007', '2026-04-01T09:40:00', 'NV011120250001', N'Nhập phụ tùng hệ thống treo'),
('PN008', '2026-04-12T15:10:00', 'NV011120250001', N'Bổ sung gạt mưa'),
('PN009', '2026-05-05T10:00:00', 'NV011120250001', N'Nhập phụ tùng thay thế thông dụng'),
('PN010', '2026-05-18T16:25:00', 'NV011120250001', N'Nhập hàng phục vụ mùa bảo dưỡng');

-- ============================================================
-- ChiTietPhieuNhapPhuTung (20)
-- ============================================================
INSERT INTO ChiTietPhieuNhapPhuTung (maPhieuNhap, maPhuTung, soLuong, donGiaNhap) VALUES
('PN001', 'PT001', 30, 125000.00),
('PN001', 'PT002', 20, 65000.00),
('PN002', 'PT004', 10, 320000.00),
('PN002', 'PT013', 15, 95000.00),
('PN003', 'PT006', 5, 1450000.00),
('PN003', 'PT009', 12, 95000.00),
('PN004', 'PT007', 10, 180000.00),
('PN004', 'PT012', 15, 160000.00),
('PN005', 'PT003', 20, 75000.00),
('PN005', 'PT005', 24, 145000.00),
('PN006', 'PT008', 18, 85000.00),
('PN006', 'PT013', 12, 95000.00),
('PN007', 'PT014', 6, 480000.00),
('PN007', 'PT011', 8, 210000.00),
('PN008', 'PT009', 10, 95000.00),
('PN008', 'PT010', 10, 110000.00),
('PN009', 'PT015', 40, 25000.00),
('PN009', 'PT002', 15, 65000.00),
('PN010', 'PT001', 25, 125000.00),
('PN010', 'PT008', 20, 85000.00);

-- ============================================================
-- PhieuTiepNhan (15)
-- ============================================================
INSERT INTO PhieuTiepNhan
(maPhieuTiepNhan, ngayTao, moTa, trangThai, maNhanVien, maKhachHang, bienSo) VALUES
('TN001', '2026-05-20T08:10:00', N'Bảo dưỡng định kỳ, thay dầu động cơ', 'HOAN_THANH', 'NV031120250002', 'KH001', '51A-123.45'),
('TN002', '2026-05-20T09:00:00', N'Xe rung khi phanh ở tốc độ cao', 'HOAN_THANH', 'NV031120250002', 'KH002', '51H-234.56'),
('TN003', '2026-05-21T08:35:00', N'Điều hòa làm lạnh yếu', 'DANG_XU_LY', 'NV121120250006', 'KH003', '60A-345.67'),
('TN004', '2026-05-21T10:20:00', N'Kiểm tra tiếng kêu ở bánh trước', 'DANG_XU_LY', 'NV031120250002', 'KH004', '51K-456.78'),
('TN005', '2026-05-22T13:15:00', N'Rửa xe và vệ sinh nội thất', 'HOAN_THANH', 'NV121120250006', 'KH005', '59A-567.89'),
('TN006', '2026-05-23T08:50:00', N'Đèn báo lỗi động cơ bật sáng', 'DANG_XU_LY', 'NV031120250002', 'KH006', '51G-678.90'),
('TN007', '2026-05-24T09:30:00', N'Xe khó khởi động vào buổi sáng', 'HOAN_THANH', 'NV121120250006', 'KH007', '61A-789.01'),
('TN008', '2026-05-25T14:10:00', N'Bảo dưỡng sau chuyến đi dài', 'TIEP_NHAN', 'NV031120250002', 'KH008', '51B-890.12'),
('TN009', '2026-05-26T11:05:00', N'Kiểm tra hệ thống điện xe điện', 'DANG_XU_LY', 'NV121120250006', 'KH009', '50H-901.23'),
('TN010', '2026-05-27T08:25:00', N'Kiểm tra phanh và thay dầu phanh', 'HOAN_THANH', 'NV031120250002', 'KH010', '51C-012.34'),
('TN011', '2026-05-28T10:45:00', N'Xe hao nhiên liệu hơn bình thường', 'DANG_XU_LY', 'NV121120250006', 'KH011', '60K-112.23'),
('TN012', '2026-05-29T15:00:00', N'Thay gạt mưa và kiểm tra kính', 'DA_HUY', 'NV031120250002', 'KH012', '51D-223.34'),
('TN013', '2026-05-30T09:15:00', N'Bảo dưỡng định kỳ 30.000 km', 'TIEP_NHAN', 'NV121120250006', 'KH013', '59C-334.45'),
('TN014', '2026-05-31T13:40:00', N'Động cơ có tiếng gõ nhẹ', 'DANG_XU_LY', 'NV031120250002', 'KH014', '51E-445.56'),
('TN015', '2026-06-01T08:05:00', N'Kiểm tra tổng quát trước chuyến đi', 'TIEP_NHAN', 'NV121120250006', 'KH015', '60B-556.67');

-- ============================================================
-- PhanCong (15)
-- ============================================================
INSERT INTO PhanCong (maPhanCong, maNhanVien, ngay, thang, nam, caLamViec, ghiChu) VALUES
('PC001', 'NV031120250002', 20, 5, 2026, 'CA_SANG', N'Tiếp nhận đầu ngày'),
('PC002', 'NV121120250006', 20, 5, 2026, 'CA_CHIEU', N'Tiếp nhận ca chiều'),
('PC003', 'NV071120250004', 20, 5, 2026, 'CA_NGAY', N'Kỹ thuật tổng quát'),
('PC004', 'NV101120250005', 21, 5, 2026, 'CA_SANG', N'Chuyên hệ thống điện'),
('PC005', 'NV051120250003', 21, 5, 2026, 'CA_CHIEU', N'Thu ngân ca chiều'),
('PC006', 'NV031120250002', 22, 5, 2026, 'CA_SANG', N'Tiếp nhận khách hẹn'),
('PC007', 'NV071120250004', 22, 5, 2026, 'CA_CHIEU', N'Bảo dưỡng định kỳ'),
('PC008', 'NV101120250005', 23, 5, 2026, 'CA_NGAY', N'Chẩn đoán lỗi động cơ'),
('PC009', 'NV051120250003', 23, 5, 2026, 'CA_SANG', N'Thu ngân ca sáng'),
('PC010', 'NV121120250006', 24, 5, 2026, 'CA_CHIEU', N'Tiếp nhận cuối tuần'),
('PC011', 'NV071120250004', 25, 5, 2026, 'CA_SANG', N'Sửa chữa hệ thống phanh'),
('PC012', 'NV101120250005', 26, 5, 2026, 'CA_NGAY', N'Kiểm tra điều hòa'),
('PC013', 'NV051120250003', 27, 5, 2026, 'CA_CHIEU', N'Đối soát hóa đơn'),
('PC014', 'NV031120250002', 28, 5, 2026, 'CA_SANG', N'Tiếp nhận lịch hẹn'),
('PC015', 'NV071120250004', 29, 5, 2026, 'CA_NGAY', N'Bảo dưỡng tổng quát');

-- ============================================================
-- HoaDon (15)
-- ============================================================
INSERT INTO HoaDon
(maHoaDon, maPhieuTiepNhan, maVoucher, trangThaiHoaDon, lyDoHuy, ghiChu, ngayTao, thoiGianThanhToan) VALUES
('HD001', 'TN001', 'VC001', 'DA_THANH_TOAN', NULL, N'Đã thanh toán tiền mặt', '2026-05-20T08:15:00', '2026-05-20T10:10:00'),
('HD002', 'TN002', 'VC004', 'DA_THANH_TOAN', NULL, N'Thanh toán chuyển khoản', '2026-05-20T09:05:00', '2026-05-20T11:30:00'),
('HD003', 'TN003', 'VC009', 'CHO_THANH_TOAN', NULL, N'Chờ khách xác nhận chi phí', '2026-05-21T08:40:00', NULL),
('HD004', 'TN004', NULL, 'CHO_THANH_TOAN', NULL, N'Đang kiểm tra phụ tùng', '2026-05-21T10:25:00', NULL),
('HD005', 'TN005', 'VC003', 'DA_THANH_TOAN', NULL, N'Khách thanh toán tại quầy', '2026-05-22T13:20:00', '2026-05-22T14:10:00'),
('HD006', 'TN006', 'VC006', 'CHO_THANH_TOAN', NULL, N'Chờ hoàn tất chẩn đoán', '2026-05-23T08:55:00', NULL),
('HD007', 'TN007', NULL, 'DA_THANH_TOAN', NULL, N'Thanh toán thẻ', '2026-05-24T09:35:00', '2026-05-24T11:00:00'),
('HD008', 'TN008', NULL, 'CHO_THANH_TOAN', NULL, N'Phiếu mới tiếp nhận', '2026-05-25T14:15:00', NULL),
('HD009', 'TN009', 'VC008', 'CHO_THANH_TOAN', NULL, N'Chờ kiểm tra kỹ thuật', '2026-05-26T11:10:00', NULL),
('HD010', 'TN010', 'VC004', 'DA_THANH_TOAN', NULL, N'Đã thanh toán chuyển khoản', '2026-05-27T08:30:00', '2026-05-27T10:50:00'),
('HD011', 'TN011', NULL, 'CHO_THANH_TOAN', NULL, N'Chờ khách duyệt phương án sửa', '2026-05-28T10:50:00', NULL),
('HD012', 'TN012', NULL, 'DA_HUY', N'Khách đổi lịch và hủy dịch vụ', N'Hóa đơn bị hủy theo yêu cầu khách', '2026-05-29T15:05:00', NULL),
('HD013', 'TN013', 'VC002', 'CHO_THANH_TOAN', NULL, N'Chờ kiểm tra xe', '2026-05-30T09:20:00', NULL),
('HD014', 'TN014', NULL, 'CHO_THANH_TOAN', NULL, N'Chờ khách đồng ý sửa chữa', '2026-05-31T13:45:00', NULL),
('HD015', 'TN015', 'VC007', 'CHO_THANH_TOAN', NULL, N'Chờ lập báo giá', '2026-06-01T08:10:00', NULL);

-- ============================================================
-- ChiTietHoaDon (30)
-- maSanPham stores the service/part code according to loaiSanPham.
-- ============================================================
INSERT INTO ChiTietHoaDon (maHoaDon, loaiSanPham, maSanPham, soLuong, donGia) VALUES
('HD001', 'DICH_VU', 'DV001', 1, 250000.00),
('HD001', 'PHU_TUNG', 'PT001', 4, 155000.00),
('HD002', 'DICH_VU', 'DV004', 1, 180000.00),
('HD002', 'PHU_TUNG', 'PT004', 1, 390000.00),
('HD003', 'DICH_VU', 'DV006', 1, 150000.00),
('HD003', 'DICH_VU', 'DV007', 1, 550000.00),
('HD004', 'DICH_VU', 'DV014', 1, 400000.00),
('HD004', 'PHU_TUNG', 'PT014', 1, 590000.00),
('HD005', 'DICH_VU', 'DV003', 1, 450000.00),
('HD005', 'DICH_VU', 'DV002', 1, 80000.00),
('HD006', 'DICH_VU', 'DV011', 1, 300000.00),
('HD006', 'PHU_TUNG', 'PT003', 1, 95000.00),
('HD007', 'DICH_VU', 'DV009', 1, 70000.00),
('HD007', 'PHU_TUNG', 'PT006', 1, 1650000.00),
('HD008', 'DICH_VU', 'DV010', 1, 950000.00),
('HD008', 'PHU_TUNG', 'PT002', 1, 85000.00),
('HD009', 'DICH_VU', 'DV011', 1, 300000.00),
('HD009', 'DICH_VU', 'DV009', 1, 70000.00),
('HD010', 'DICH_VU', 'DV004', 1, 180000.00),
('HD010', 'PHU_TUNG', 'PT013', 1, 125000.00),
('HD011', 'DICH_VU', 'DV015', 1, 600000.00),
('HD011', 'PHU_TUNG', 'PT012', 1, 195000.00),
('HD012', 'DICH_VU', 'DV002', 1, 80000.00),
('HD012', 'PHU_TUNG', 'PT009', 2, 120000.00),
('HD013', 'DICH_VU', 'DV010', 1, 950000.00),
('HD013', 'PHU_TUNG', 'PT008', 2, 110000.00),
('HD014', 'DICH_VU', 'DV013', 4, 220000.00),
('HD014', 'PHU_TUNG', 'PT005', 4, 175000.00),
('HD015', 'DICH_VU', 'DV009', 1, 70000.00),
('HD015', 'DICH_VU', 'DV005', 1, 350000.00);

-- ============================================================
-- VanDeKyThuat (15)
-- ============================================================
INSERT INTO VanDeKyThuat
(maVanDe, maPhieuTiepNhan, tieuDe, moTa, mucDoUuTien, giaiPhap, khachHangDongY, ngayTao) VALUES
('VD001', 'TN001', N'Dầu động cơ đã xuống cấp', N'Dầu cũ đổi màu, đến hạn bảo dưỡng', 30, N'Thay dầu động cơ và kiểm tra rò rỉ', 'CO', '2026-05-20T08:30:00'),
('VD002', 'TN002', N'Rung khi phanh', N'Đĩa phanh có dấu hiệu mòn không đều', 75, N'Kiểm tra độ đảo đĩa và thay má phanh nếu cần', 'CO', '2026-05-20T09:20:00'),
('VD003', 'TN003', N'Điều hòa làm lạnh yếu', N'Áp suất gas thấp hơn mức khuyến nghị', 60, N'Kiểm tra rò rỉ trước khi nạp gas', 'CHUA_XAC_NHAN', '2026-05-21T09:00:00'),
('VD004', 'TN004', N'Tiếng kêu bánh trước', N'Phát hiện tiếng rít khi xe đi qua gờ giảm tốc', 70, N'Kiểm tra bi moay-ơ và các khớp hệ thống treo', 'CHUA_XAC_NHAN', '2026-05-21T10:45:00'),
('VD005', 'TN005', N'Nội thất bám bụi', N'Bụi bẩn tập trung ở thảm sàn và khe gió', 20, N'Vệ sinh nội thất và khử mùi nhẹ', 'CO', '2026-05-22T13:30:00'),
('VD006', 'TN006', N'Đèn báo lỗi động cơ', N'Mã lỗi cần được đọc bằng máy chẩn đoán', 85, N'Đọc mã lỗi ECU và kiểm tra cảm biến liên quan', 'CHUA_XAC_NHAN', '2026-05-23T09:10:00'),
('VD007', 'TN007', N'Ắc quy yếu', N'Điện áp khởi động thấp sau khi xe để qua đêm', 65, N'Test tải ắc quy và kiểm tra hệ thống sạc', 'CO', '2026-05-24T09:45:00'),
('VD008', 'TN008', N'Đến hạn bảo dưỡng', N'Odo gần mốc bảo dưỡng định kỳ', 35, N'Kiểm tra tổng quát theo danh mục bảo dưỡng', 'CHUA_XAC_NHAN', '2026-05-25T14:25:00'),
('VD009', 'TN009', N'Cảnh báo hệ thống điện', N'Cần kiểm tra thông báo lỗi trên bảng điều khiển', 90, N'Chẩn đoán theo quy trình dành cho xe điện', 'KHONG', '2026-05-26T11:20:00'),
('VD010', 'TN010', N'Dầu phanh cần thay', N'Dầu phanh đã sử dụng lâu và cần kiểm tra độ ẩm', 55, N'Thay dầu phanh DOT 4 và xả gió hệ thống', 'CO', '2026-05-27T08:45:00'),
('VD011', 'TN011', N'Hao nhiên liệu', N'Khách hàng phản ánh mức tiêu thụ tăng', 60, N'Kiểm tra lọc gió, bugi và hệ thống phun nhiên liệu', 'CHUA_XAC_NHAN', '2026-05-28T11:00:00'),
('VD012', 'TN012', N'Yêu cầu thay gạt mưa', N'Khách hàng hủy lịch trước khi thực hiện', 15, N'Chưa thực hiện; đóng hồ sơ theo yêu cầu hủy', 'KHONG', '2026-05-29T15:15:00'),
('VD013', 'TN013', N'Bảo dưỡng mốc 30.000 km', N'Cần kiểm tra lọc gió, dầu và các hạng mục định kỳ', 40, N'Lập danh sách hạng mục và báo giá trước khi làm', 'CHUA_XAC_NHAN', '2026-05-30T09:30:00'),
('VD014', 'TN014', N'Tiếng gõ động cơ', N'Tiếng gõ nhẹ xuất hiện khi tăng tốc', 80, N'Kiểm tra dầu, bugi và chẩn đoán động cơ trước khi tháo lắp', 'CHUA_XAC_NHAN', '2026-05-31T14:00:00'),
('VD015', 'TN015', N'Kiểm tra trước chuyến đi', N'Khách hàng muốn rà soát các hạng mục an toàn', 25, N'Kiểm tra lốp, phanh, đèn, nước làm mát và ắc quy', 'CHUA_XAC_NHAN', '2026-06-01T08:20:00');

-- ============================================================
-- KhieuNai (15)
-- trangThai: CHUA_XU_LY, DANG_XU_LY, DA_XU_LY
-- ============================================================
INSERT INTO KhieuNai (maKhieuNai, maKhachHang, maPhieuTiepNhan, noiDung, trangThai, ngayTao) VALUES
('KN001', 'KH001', 'TN001', N'Thời gian chờ đợi lâu, chưa được thông báo tiến độ', 'CHUA_XU_LY', '2026-05-21T09:00:00'),
('KN002', 'KH002', 'TN002', N'Hóa đơn tính phí cao hơn báo giá ban đầu', 'DANG_XU_LY', '2026-05-22T10:30:00'),
('KN003', 'KH003', 'TN003', N'Điều hòa sau khi nạp gas vẫn chưa mát', 'CHUA_XU_LY', '2026-05-23T14:15:00'),
('KN004', 'KH004', 'TN004', N'Tiếng kêu ở bánh trước vẫn còn sau khi kiểm tra', 'DANG_XU_LY', '2026-05-24T08:45:00'),
('KN005', 'KH005', 'TN005', N'Vệ sinh nội thất chưa sạch, xe vẫn còn mùi', 'DA_XU_LY', '2026-05-25T09:20:00'),
('KN006', 'KH006', 'TN006', N'Chưa được tư vấn rõ ràng về mã lỗi động cơ', 'CHUA_XU_LY', '2026-05-26T11:10:00'),
('KN007', 'KH007', 'TN007', N'Xe vẫn khó khởi động sau khi thay ắc quy', 'DANG_XU_LY', '2026-05-27T15:40:00'),
('KN008', 'KH008', 'TN008', N'Thời gian bảo dưỡng vượt quá hẹn', 'DA_XU_LY', '2026-05-28T08:30:00'),
('KN009', 'KH009', 'TN009', N'Phụ tùng thay không đúng loại đã báo', 'CHUA_XU_LY', '2026-05-29T13:05:00'),
('KN010', 'KH010', 'TN010', N'Dầu phanh bị tràn ra ngoài trong quá trình thay', 'DANG_XU_LY', '2026-05-30T10:15:00'),
('KN011', 'KH011', 'TN011', N'Tình trạng hao nhiên liệu vẫn chưa được khắc phục', 'CHUA_XU_LY', '2026-05-31T09:50:00'),
('KN012', 'KH012', 'TN012', N'Đã hủy dịch vụ nhưng vẫn bị tính phí', 'DA_XU_LY', '2026-06-01T14:25:00'),
('KN013', 'KH013', 'TN013', N'Danh mục bảo dưỡng thực hiện thiếu so với báo giá', 'DANG_XU_LY', '2026-06-02T08:40:00'),
('KN014', 'KH014', 'TN014', N'Tiếng gõ động cơ chưa được xử lý dứt điểm', 'CHUA_XU_LY', '2026-06-03T16:00:00'),
('KN015', 'KH015', 'TN015', N'Chưa nhận được báo giá trước khi thực hiện dịch vụ', 'DANG_XU_LY', '2026-06-04T11:35:00');

COMMIT TRANSACTION;
