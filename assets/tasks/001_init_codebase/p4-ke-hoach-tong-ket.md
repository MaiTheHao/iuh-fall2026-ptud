# P4 — Kế hoạch và tổng kết

**Trạng thái:** `[DONE]` — 6/6 màn, cập nhật 24/10/2026 theo mã nguồn sau khi gộp nhánh vào `develop`.

- **Lane:** L4 — Kế hoạch & Tổng kết · **Phụ trách:** Đặng Thanh Bình · **Nhánh:** `feat/p4-ke-hoach-tong-ket` · **Review:** Mai Thế Hào

## 1. Màn hình

| Màn | Trạng thái | Hiện có |
|---|---|---|
| SCR-05 Phân công ca | `[DONE]` | `PhanCongCaPage` + `PhanCongService`: lọc theo mã phân công/tên nhân viên/khoảng ngày/ca, gán ca, sửa, xóa; service tự chặn phân công trùng nhân viên–ngày–ca (DB không có ràng buộc duy nhất); mã sinh bằng `MaGeneratorUtil` + `MaRetryUtil` |
| SCR-05B Lịch của tôi | `[DONE]` | `LichCuaToiPage` + `PhanCongService.getLichCuaNhanVien(...)`: lịch thứ Hai → Chủ nhật, ba ca, nhân viên lấy từ `AppContext`, điều hướng tuần trước/tuần này/tuần sau |
| SCR-05C Lịch của shop | `[DONE]` | `LichCuaShopPage` + `PhanCongService.getLichCuaShop(ngayDauTuan)`: lưới ngày × ca, mỗi ô là danh sách nhân viên trực |
| SCR-12 Doanh thu | `[DONE]` | `DoanhThuPage` + `DoanhThuService.taoBaoCao(tuNgay, denNgay)`: lọc khoảng ngày tùy chọn, chỉ cộng hóa đơn `DA_THANH_TOAN`, tách doanh thu dịch vụ/phụ tùng, biểu đồ tròn (`jfreechart-1.5.6.jar`), in và xuất Excel |
| SCR-12B Tiêu thụ phụ tùng | `[DONE]` | `TieuThuPhuTungPage` + `TieuThuPhuTungService.taoBaoCao(YearMonth)`: lượng nhập/xuất/tồn, đơn giá, doanh thu, bảng Top 20, in và xuất Excel |
| SCR-12C Tiêu thụ dịch vụ | `[DONE]` | `TieuThuDichVuPage` + `TieuThuDichVuService.taoBaoCao(YearMonth)`: số lần sử dụng, tỉ trọng, doanh thu, Top 20, in và xuất Excel |

Nền dùng lại: entity `PhanCong`, `HoaDon`, `ChiTietHoaDon`, `ChiTietPhieuNhapPhuTung`, `PhuTung`, `DichVu`; enum `CaLamViec` (`CA_SANG`, `CA_CHIEU`, `CA_NGAY`) và `TrangThaiHoaDon` có nhãn i18n; DTO `BaoCaoDoanhThuDTO`, `BaoCaoTieuThuPhuTungDTO`, `BaoCaoTieuThuDichVuDTO`; DAO chỉ đọc `DoanhThuDAO`, `TieuThuPhuTungDAO`, `TieuThuDichVuDAO`; dữ liệu mẫu `PhanCong`, `HoaDon`, `ChiTietHoaDon` trong `V4__mock_data.sql` (= `mock.sql`).

Đã làm vượt phạm vi dự kiến ban đầu: biểu đồ, Top 20, lọc khoảng ngày tùy chọn, in và xuất Excel.

Còn hoãn: badge màu theo trạng thái ca trên lịch, hình ảnh minh họa trong báo cáo.

## 2. Đã làm theo kế hoạch

### Phân công ca và lịch

1. **`PhanCongDAO`** — `findById`, `findAll`, `findByDateRange(tuNgay, denNgay)`, `insert`, `update`, `delete`; các lệnh ghi nhận `TransactionScope` từ service. `DashboardService` vẫn dùng `findAll()` như cũ.
2. **`PhanCongService`** — `search(...)`, `kiemTraDuLieuTao(...)`, `create`, `update`, `delete`, `getLichCuaNhanVien(...)`, `getLichCuaShop(...)`; kiểm tra trùng nhân viên/ngày/ca trong service và sinh mã bằng `MaGeneratorUtil` + `MaRetryUtil`.
3. **SCR-05** — chọn nhân viên qua `NhanVienService`, lọc vai trò, chọn ngày/ca, ghi chú; từ chối phân công trùng với thông báo lấy từ i18n.
4. **SCR-05B / SCR-05C** — lịch tuần thứ Hai → Chủ nhật, ba ca; GUI không truy vấn DB, chỉ gọi service; SCR-05B lấy nhân viên đang đăng nhập từ `AppContext`.

### Báo cáo

5. **DAO chỉ đọc** — `DoanhThuDAO.findAllChiTietDoanhThu()`, `TieuThuPhuTungDAO.findAll(thang)`, `TieuThuDichVuDAO.findAll(thang)`; không tổng hợp nghiệp vụ.
6. **Service tổng hợp** — `DoanhThuService` (doanh thu theo kỳ, tách dịch vụ/phụ tùng, danh sách hóa đơn trong kỳ), `TieuThuPhuTungService` (nhập/xuất/tồn/doanh thu + Top 20), `TieuThuDichVuService` (số lần dùng/tỉ trọng/doanh thu + Top 20).
7. **Công thức doanh thu** — doanh thu tính bằng `soLuong * donGia` của `ChiTietHoaDon`; **khác biệt cần chốt:** báo cáo hiện là doanh thu gộp, chưa trừ `HoaDon.mucGiamApDung`/`Voucher.mucGiam` như `HoaDonService.tinhTongTien`, nên tổng trên SCR-12 lớn hơn tổng tiền hóa đơn ở SCR-10 khi hóa đơn có voucher.
8. **i18n** — nhóm `app.schedule.*` (45 key) và `app.report.*` (71 key) đã có đủ ở cả `i18n_vn.json` và `i18n_en.json`; hai tệp cùng tập 600 key.

## 3. Phụ thuộc

- P1: `NhanVienService` — `[DONE]`.
- P3: `PhieuNhapPhuTungService`, `HoaDonService` — `[DONE]`; báo cáo đọc thẳng `HoaDon`/`ChiTietHoaDon` nên số liệu phát sinh từ SCR-10 vào báo cáo ngay.
- Dùng DAO riêng của P4, không sửa DAO của P3; không sửa `DashboardService`.
- Có thêm phụ thuộc build: `lib/jfreechart-1.5.6.jar` cho biểu đồ tròn.

## 4. Kiểm tra hoàn thành

Đối chiếu mã nguồn và `javac` ngày 24/10/2026. Chưa chạy lại end-to-end trên DB sau khi gộp nhánh — cần chạy lại trước khi báo cáo.

- [x] SCR-05 gán ca thành công và từ chối phân công trùng (kiểm tra trong `PhanCongService.kiemTraDuLieuTao`).
- [x] SCR-05B hiển thị đúng lịch tuần của người đang đăng nhập.
- [x] SCR-05C hiển thị lịch tuần toàn shop theo từng ca.
- [x] SCR-12 hiển thị doanh thu theo khoảng ngày đã chọn, chỉ cộng hóa đơn đã thanh toán.
- [x] SCR-12B có số liệu nhập/xuất/tồn và doanh thu phụ tùng.
- [x] SCR-12C có số lần sử dụng và doanh thu theo dịch vụ.
- [x] Tổng hợp nằm trong service, DAO chỉ cung cấp dữ liệu.
- [x] Enum hiển thị nhãn tiếng Việt; i18n hai tệp cùng tập key.
