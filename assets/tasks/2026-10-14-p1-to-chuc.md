# P1 — Tổ chức và dữ liệu nền

**Trạng thái:** `[DONE]` — 6/6 màn, cập nhật 21/10/2026.

- **Lane:** L1 — Tổ chức · **Phụ trách:** Nguyễn Minh Quốc · **Nhánh:** `feat/p1-to-chuc` (đã vào `develop`) · **Review:** Mai Thế Hào

## 1. Màn hình

| Màn | Trạng thái |
|---|---|
| SCR-03 Khách hàng | `[DONE]` |
| SCR-03B Xe | `[DONE]` |
| SCR-06 Phụ tùng | `[DONE]` |
| SCR-08 Dịch vụ | `[DONE]` |
| SCR-09 Voucher | `[DONE]` |
| SCR-11 Nhân viên | `[DONE]` |

Mỗi màn có danh sách, tìm kiếm/lọc, phân trang, biểu mẫu thêm/sửa, thao tác xóa và xóa lọc; dữ liệu đọc/ghi qua `GUI → SERVICE → DAO`.

Ngoài phạm vi đợt này: đặt lại mật khẩu từ dòng nhân viên, cảnh báo tồn kho thấp, tải ảnh, phân quyền menu, bộ lọc nâng cao mọi cột, hoàn thiện bản dịch tiếng Anh.

## 2. Hợp đồng service cho lane khác

Đây là phần P2/P3/P4 đang gọi. Giữ ổn định chữ ký và hành vi; muốn đổi thì trao đổi với lane đang dùng trước.

| Màn | Service và phương thức chính | Lane dùng |
|---|---|---|
| Khách hàng | `search(DanhSachKhachHangCriteria)`, `getBySdt`, `create` (có biến thể nhận `TransactionScope`), `update`, `delete` | P2 |
| Xe | `search`, `getByBienSo`, `existsByBienSo`, `create`, `update`, `delete`, `getDanhSachHangXe`, `getDanhSachDongXe` | P2 |
| Phụ tùng | `search`, `getAllForSelection`, `increaseStock(TransactionScope, maPhuTung, soLuong)`, `decreaseStock(...)` | P3 |
| Dịch vụ | `search(DanhSachDichVuCriteria)`, `create`, `update`, `delete` | P3 |
| Voucher | `search(DanhSachVoucherCriteria)`, `create`, `update`, `delete` | P3 |
| Nhân viên | `search(DanhSachNhanVienCriteria)`, `getById`, `create`, `update`, `delete` | P2, P4 |

`increaseStock`/`decreaseStock` chỉ nhận `TransactionScope`, buộc lane gọi nằm chung giao dịch với phiếu nhập hoặc hóa đơn.

## 3. Lưu ý

- DB hiện có migration `V0`–`V9`. `soDienThoai` của `NhanVien` do `V1__add_so_dien_thoai.sql` thêm (dạng `NULL`), còn `init.sql` ở thư mục gốc khai báo `NOT NULL`; `V8__unique_sdt.sql` thêm ràng buộc duy nhất cho `KhachHang.sdt`.
- Không refactor `AuthService` ngoài phạm vi; không ghi đè key i18n của lane khác.
