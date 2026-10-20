# P4 — Kế hoạch và tổng kết

**Trạng thái:** `[TODO]` — 0/6 màn, cập nhật 21/10/2026.

- **Lane:** L4 — Kế hoạch & Tổng kết · **Phụ trách:** Đặng Thanh Bình · **Nhánh:** `feat/p4-ke-hoach-tong-ket` · **Review:** Mai Thế Hào

## 1. Màn hình

| Màn | Trạng thái | Hiện có |
|---|---|---|
| SCR-05 Phân công ca | `[TODO]` | Trang giữ chỗ; `PhanCongDAO` mới có `findById`, `findAll` |
| SCR-05B Lịch của tôi | `[TODO]` | Trang giữ chỗ |
| SCR-05C Lịch của shop | `[TODO]` | Trang giữ chỗ |
| SCR-12 Doanh thu | `[TODO]` | Trang giữ chỗ; chưa có `BaoCaoService`, `BaoCaoDAO` |
| SCR-12B Tiêu thụ phụ tùng | `[TODO]` | Trang giữ chỗ |
| SCR-12C Tiêu thụ dịch vụ | `[TODO]` | Trang giữ chỗ |

Nền đã có: entity `PhanCong`, `HoaDon`, `ChiTietHoaDon`; enum `CaLamViec` (`CA_SANG`, `CA_CHIEU`, `CA_NGAY`) và `TrangThaiHoaDon` đã có nhãn i18n; dữ liệu mẫu `PhanCong`, `HoaDon`, `ChiTietHoaDon` trong `V4__mock_data.sql`.

Ngoài phạm vi: biểu đồ, Top 20, lọc khoảng ngày tùy chọn, badge màu, hoàn thiện bản dịch tiếng Anh.

## 2. Việc cần làm

### Phân công ca và lịch

1. **`PhanCongDAO`** — bổ sung `insert(TransactionScope, PhanCong)`, `exists(maNhanVien, ngay, thang, nam, caLamViec)`, `findByTuan(...)`, `findByNhanVien(...)`. `DashboardService` đang dùng `findAll()` nên giữ nguyên chữ ký cũ.
2. **`PhanCongService`** — kiểm tra trùng theo nhân viên/ngày/tháng/năm/ca; DB **không có ràng buộc duy nhất** nên kiểm tra trong service là bắt buộc. Mã phân công sinh bằng `MaGeneratorUtil` + `MaRetryUtil`.
3. **SCR-05** — tìm nhân viên theo mã/tên qua `NhanVienService`, lọc vai trò, chọn tuần/ngày/ca, ghi chú, gán ca; từ chối phân công trùng với thông báo rõ ràng.
4. **SCR-05B / SCR-05C** — lịch thứ Hai → Chủ nhật, ba ca, điều hướng tuần trước/tuần này/tuần sau; dữ liệu qua `PhanCongService`, không truy vấn DB trong GUI; SCR-05B lấy nhân viên từ `AppContext`.

### Báo cáo

5. **`BaoCaoDAO`** — chỉ đọc dữ liệu thô từ `HoaDon`, `ChiTietHoaDon`, `PhieuNhapPhuTung`, `ChiTietPhieuNhapPhuTung`, `PhuTung`, `DichVu`; không tổng hợp nghiệp vụ.
6. **`BaoCaoService`** — tổng hợp:
   - **SCR-12 Doanh thu:** chọn tháng/năm, chỉ tính hóa đơn `DA_THANH_TOAN` có `thoiGianThanhToan` trong kỳ; tách doanh thu theo `loaiSanPham` (`DICH_VU`/`PHU_TUNG`).
   - **SCR-12B Tiêu thụ phụ tùng:** lượng nhập (`ChiTietPhieuNhapPhuTung`), lượng xuất (`ChiTietHoaDon` với `PHU_TUNG`), tồn hiện tại, đơn giá, doanh thu trong kỳ.
   - **SCR-12C Tiêu thụ dịch vụ:** số lần sử dụng (`ChiTietHoaDon` với `DICH_VU`), đơn giá, doanh thu trong kỳ.
7. **Công thức doanh thu:** `HoaDon` không lưu tổng tiền hay tiền giảm, nên phải cộng `soLuong * donGia` của `ChiTietHoaDon` rồi áp `Voucher.mucGiam` (phân số 0–1) — dùng đúng công thức P3 sẽ áp dụng trong `HoaDonService` để số liệu khớp.
8. **i18n** — chưa có nhóm key cho phân công, lịch và báo cáo; bổ sung đồng thời `i18n_vn.json` và `i18n_en.json`.

## 3. Phụ thuộc

- P1: `NhanVienService` — đã có.
- P3: phiếu nhập đã xong; hóa đơn mới có DAO đọc. P4 vẫn làm được ngay trên dữ liệu mẫu `V4__mock_data.sql`; chỉ khi cần số liệu phát sinh từ SCR-10 mới phải chờ P3.
- Dùng `BaoCaoDAO` riêng, không sửa DAO của P3; không sửa `DashboardService` ngoài phạm vi.
- Không thêm thư viện biểu đồ hay phụ thuộc build mới.

## 4. Kiểm tra hoàn thành

- [ ] SCR-05 gán ca thành công và từ chối phân công trùng.
- [ ] SCR-05B hiển thị đúng lịch tuần của người đang đăng nhập.
- [ ] SCR-05C hiển thị lịch tuần toàn shop theo từng ca.
- [ ] SCR-12 hiển thị doanh thu tháng đã chọn, chỉ tính hóa đơn đã thanh toán.
- [ ] SCR-12B có số liệu nhập/xuất/tồn và doanh thu phụ tùng khớp dữ liệu hiện tại.
- [ ] SCR-12C có số lần sử dụng và doanh thu theo dịch vụ.
- [ ] Tổng hợp nằm trong service, DAO chỉ cung cấp dữ liệu.
- [ ] Enum hiển thị nhãn tiếng Việt; i18n hai tệp cùng tập key.
