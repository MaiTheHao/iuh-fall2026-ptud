# P2 — Tiếp nhận và khiếu nại

**Trạng thái:** `[PARTIAL]` — 4/6 màn, cập nhật 21/10/2026.

- **Lane:** L2 — Tiếp nhận & Khiếu nại · **Phụ trách:** Trần Nguyễn Hữu Thắng · **Nhánh:** `feat/p2-tiep-nhan-khieu-nai` (đã vào `develop`) · **Review:** Mai Thế Hào

## 1. Màn hình

| Màn | Trạng thái | Ghi chú |
|---|---|---|
| SCR-04 Tiếp nhận khách hàng | `[DONE]` | `TiepNhanKhachHangPage` + `TiepNhanService`: tạo/tái sử dụng khách hàng, xe, liên kết và phiếu tiếp nhận trong một transaction |
| SCR-04B Quản lý phiếu tiếp nhận | `[DONE]` | Danh sách, lọc, phân trang, sửa, hủy phiếu |
| SCR-04C Xem chẩn đoán | `[TODO]` | Chưa có page, chưa đăng ký |
| SCR-04D Lập chẩn đoán | `[PARTIAL]` | Chỉ có màn chọn phiếu tiếp nhận; chưa lập được chẩn đoán |
| SCR-16 Tiếp nhận khiếu nại | `[DONE]` | `TiepNhanKhieuNaiPage`: tra khách hàng theo số điện thoại, chọn phiếu, nhập nội dung |
| SCR-16B Quản lý khiếu nại | `[DONE]` | Danh sách, lọc, phân trang, cập nhật trạng thái `CHUA_XU_LY`/`DANG_XU_LY`/`DA_XU_LY`, xóa |

Ngoài phạm vi: đính kèm ảnh, bộ lọc nâng cao mọi cột, hiệu ứng chuyển màn, badge màu, hoàn thiện bản dịch tiếng Anh.

## 2. Việc còn lại: chẩn đoán kỹ thuật

Đã có: entity `VanDeKyThuat`, enum `TrangThaiVanDeKyThuat` (`CO`, `KHONG`, `CHUA_XAC_NHAN`), bảng `VanDeKyThuat` trong `V0` với `CK_VanDeKyThuat_MucDoUuTien` (1–100), dữ liệu mẫu `VD001`–`VD015`, đường đọc lồng trong `PhieuTiepNhanDAO`.

Chưa có đường ghi và hai màn hình. Cần làm:

1. **`VanDeKyThuatDAO`** — `insert(TransactionScope, VanDeKyThuat)`, `findByPhieuTiepNhan(maPhieuTiepNhan)`; không tính toán nghiệp vụ.
2. **`VanDeKyThuatService`** — lập chẩn đoán cho phiếu đã tồn tại; tiêu đề không rỗng; mức ưu tiên 1–100; mặc định `khachHangDongY = CHUA_XAC_NHAN`; sinh mã bằng `MaGeneratorUtil` + `MaRetryUtil`, tiền tố `VD`.
3. **SCR-04D** — thêm biểu mẫu lập chẩn đoán cho phiếu đang chọn. Màn hiện tại tải toàn bộ phiếu rồi lọc và tra tên khách hàng ngay trong GUI; chuyển sang `PhieuTiepNhanService.search(DanhSachPhieuTiepNhanCriteria)`.
4. **SCR-04C** — màn phụ chỉ đọc chẩn đoán của phiếu đã chọn; đăng ký trong `PageRegistry` nhưng không thêm vào menu; mở từ SCR-04B. `goTo(Class)` không mang tham số nên cần chỗ lưu phiếu đang chọn (ví dụ `AppContext`) trước khi điều hướng.
5. **i18n** — bổ sung key cho hai màn mới trong cả `i18n_vn.json` và `i18n_en.json`.

## 3. Phụ thuộc

- P1: `KhachHangService`, `XeService`, `NhanVienService` — đã có.
- P3: lập hóa đơn chỉ cần `PhieuTiepNhanService` (đã có), không chờ phần chẩn đoán.
- Bảng `VanDeKyThuat` đã tồn tại và đã cascade từ `PhieuTiepNhan` (`V2`); vấn đề là đường ghi, không phải schema.

## 4. Kiểm tra hoàn thành

- [x] SCR-04 lưu được phiếu tiếp nhận mới.
- [x] SCR-04B hiển thị phiếu và trạng thái bằng nhãn tiếng Việt.
- [x] SCR-16 tạo được khiếu nại gắn khách hàng và phiếu tiếp nhận.
- [x] SCR-16B cập nhật được trạng thái khiếu nại.
- [ ] SCR-04D tạo được chẩn đoán có tiêu đề hợp lệ, mức ưu tiên 1–100.
- [ ] SCR-04C xem được chẩn đoán của đúng phiếu, không xuất hiện trong menu.
