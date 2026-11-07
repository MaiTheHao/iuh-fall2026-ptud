# P3 — Ẩn/hiện menu theo vai trò

**Dành cho:** Quốc · **Trạng thái:** ĐÃ DUYỆT · **Ước lượng:** nửa ngày

## Mục tiêu

Menu chỉ hiện những trang thuộc vai trò đang đăng nhập. `NHAN_VIEN_QUAN_LY` thấy mọi thứ; ba vai trò còn lại thấy đúng nhóm của mình.

## Phạm vi (đúng như đã chốt)

- **Chỉ ẩn/hiện menu.** Không chặn `goTo`, không sửa service/DAO, không ẩn từng nút trong trang.
- Chuyển hướng giữa các trang trong cùng luồng nghiệp vụ là hợp lệ, không xử lý.

## Hiện trạng

- `VaiTro` có 4 giá trị, hiện chỉ dùng để hiển thị và xếp ca.
- `LayoutHeader` dựng menu **một lần** trong constructor từ `PageRegistry.all()`.
- `Layout.render()` gọi `header.refreshText()` **mỗi lần điều hướng**, kể cả ngay sau khi đăng nhập.
- Khi chưa đăng nhập, thanh menu bị gỡ khỏi frame.

## Việc cần làm

### 1. Tạo `src/iuh/fit/gui/permission/VaiTroQuyen.java`

- Một method public duy nhất: `static boolean duocHien(VaiTro vaiTro, Class<? extends AbstractPage> trang)`.
- Bảng quyền:

| Vai trò | Trang được hiện |
|---|---|
| `NHAN_VIEN_QUAN_LY` | tất cả trang có menu (trừ Login) |
| `NHAN_VIEN_TIEP_NHAN` | Khách hàng, Xe, Danh mục xe, Tiếp nhận khách hàng, Quản lý phiếu tiếp nhận, Xem chẩn đoán, Tiếp nhận khiếu nại, Quản lý khiếu nại, Lịch của tôi, Lịch của shop, Đổi mật khẩu |
| `NHAN_VIEN_THU_NGAN` | Lập hóa đơn, Quản lý hóa đơn, Lịch của tôi, Đổi mật khẩu |
| `NHAN_VIEN_KY_THUAT` | Quản lý chẩn đoán, Bảng chẩn đoán, Xem chẩn đoán, Lịch của tôi, Đổi mật khẩu |
| `null` | không trang nào |

- Dashboard, Nhân viên, Phụ tùng, Dịch vụ, Nhà cung cấp, Voucher, Phân công ca, Nhập phụ tùng, Tổng kết: **chỉ** `NHAN_VIEN_QUAN_LY`.
- Không rải `if (vaiTro == ...)` ra ngoài class này.

### 2. Sửa `src/iuh/fit/gui/layout/LayoutHeader.java`

- Thêm field nhớ vai trò của lần dựng menu gần nhất.
- Trong `refreshText()`: so vai trò hiện tại với field đó; **khác** thì gỡ toàn bộ menu và dựng lại chỉ với những trang `duocHien` trả true.
- Nhóm không còn trang nào thì không thêm nhóm đó.
- Sau khi dựng lại: `revalidate()` + `repaint()`.
- Giữ nguyên logic đánh dấu trang đang mở và `setMenuVisible`.
- **Không** sửa `Layout.java`.

## Kiểm thử tay

1. Đăng nhập `admin` → đủ 8 nhóm như trước khi sửa.
2. Đăng xuất, đăng nhập `thungan01` → chỉ còn nhóm Thanh toán và Cài đặt.
3. Đăng nhập `tiepnhan01` → thấy nhóm Tiếp nhận, Tổ chức (khách/xe/danh mục xe), Kế hoạch, Cài đặt; **không** thấy Dashboard, Tổng kết, Nhân viên.
4. Đăng nhập `kythuat01` → thấy nhóm liên quan chẩn đoán + Lịch của tôi + Đổi mật khẩu.
5. Đổi tài khoản trong cùng phiên (logout → login) → menu đổi đúng, không sót mục của vai trò trước.
6. Bấm một mục sau khi menu dựng lại → điều hướng đúng, mục đang mở được đánh dấu.

## Bẫy cần tránh

- Dựng lại menu mỗi lần render sẽ giật và mất trạng thái; chỉ dựng khi **vai trò đổi**.
- `AppContext` giữ snapshot lúc đăng nhập: đổi vai trò của chính mình trong DB không có hiệu lực tới lần đăng nhập sau.
- Đây **không phải** kiểm soát truy cập: người dùng vẫn mở được trang qua đường khác. Đã được chủ dự án chấp nhận.

## Xong khi

- 6 mục kiểm thử trên đều đạt, build 0 lỗi, không có page nào bị sửa.
