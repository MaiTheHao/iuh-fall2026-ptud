# Mẫu Management Panel — Khách Hàng (P1 U2 · phần KháchHang) Walkthrough

## Summary
Chuyển `KhachHangPage` từ `PlaceholderPage` sang trang quản lý thật: bảng có **bộ lọc theo cột + tìm nhanh** và **điều hướng phân trang**, form nhập + 3 nút `Thêm / Lưu / Xóa Trắng`, chọn dòng để đổ dữ liệu lên form. Lọc và phân trang xử lý **toàn bộ ở service** qua DTO `DanhSachKhachHangCriteria` và `PaginatedResult`. Đây là **mẫu tham chiếu** để nhân bản cho Xe, Phụ tùng, Dịch vụ, Voucher, Nhân viên.

## Changes Made
| File | Status | Description |
| :--- | :--- | :--- |
| `src/iuh/fit/dto/PaginatedResult.java` | CREATED | `items/page/pageSize/totalItems` + `totalPages/hasNext/hasPrevious/fromIndex/toIndex` |
| `src/iuh/fit/dto/DanhSachKhachHangCriteria.java` | CREATED | Tiêu chí lọc (mã/SĐT/họ tên/ngày tạo/tìm nhanh) + `page/pageSize` |
| `src/iuh/fit/service/KhachHangService.java` | CREATED | `search(criteria)` lọc bỏ dấu + phân trang; `create/update/delete`; validate + sinh mã `KH` |
| `src/iuh/fit/dao/KhachHangDAO.java` | MODIFIED | Thêm `insert/update/delete` |
| `src/iuh/fit/gui/component/ManagementPanel.java` | MODIFIED | Bộ lọc theo cột + tìm nhanh + đếm + `Xóa lọc` + `‹ Trước / Trang x/y / Sau ›` + khoảng ngày bằng **LGoodDatePicker** |
| `src/iuh/fit/gui/page/KhachHangPage.java` | MODIFIED | Wiring lọc/phân trang, nạp `search(criteria)`, CRUD |
| `src/resources/i18n_vn.json`, `i18n_en.json` | MODIFIED | `app.khachHang.*` + `app.common.{clearFilter,prev,next,pageLabel,count}` + `app.date.{format,today,clear}` |

## Công thức nhân bản cho trang quản lý khác
1. **i18n**: thêm nhóm `app.<tenTrang>.*` (mã, các cột, thông báo lỗi) cho cả VN và EN; giữ parity.
2. **Criteria DTO**: tạo `DanhSach<Ten>Criteria` (các trường lọc theo cột + `page/pageSize`) trong `iuh.fit.dto`.
3. **DAO**: thêm `insert(T)`, `update(T)`, `delete(id)` cạnh `findAll()/findById()` hiện có.
4. **Service**: `search(criteria)` trả `PaginatedResult<T>` — lấy raw từ DAO, lọc ở service, rồi để **`PaginatedResult.of(items, page, pageSize)`** tự tính `totalPages/clampPage/slice`; `create/update/delete`; validate + sinh mã ở service. **Mọi method service đặt tên tiếng Anh**. `PaginatedResult` giữ `DEFAULT_PAGE_SIZE` và các static helper (`of`, `totalPages`, `clampPage`).
5. **Page**: `render()` dựng `ManagementPanel`, `setHeaders(...)`, add field vào `getFormPanel()`, `addTextFilter(key, col)`/`addDateRangeFilter(key, col)`, `setFilterChangeListener(() -> taiLai(panel, 1))`, wiring `getPrevButton()/getNextButton()/getClearFilterButton()`, sau mỗi lần tải gọi `setRows`/`setCount`/`setPage`.
6. **Quy ước**: mã hệ thống sinh nên `setEnabled(false)`; lọc ngày dùng **LGoodDatePicker** (không nhập chuỗi); lỗi nghiệp vụ ném `IllegalArgumentException(<key i18n>)`; DAO tìm một bản ghi trả `Optional`, service ném exception thay vì trả `null`.

## Verification Evidence
```bash
$ javac -cp "lib/*" -d bin $(find src -name '*.java') && cp -r src/resources bin/ && echo BUILD_OK
BUILD_OK
$ python3 (parity i18n)
PARITY_OK

# Service: phân trang + lọc (DB thật 15 KH)
p1 items=10 total=15 pages=2 hasPrev=false hasNext=true from=1 to=10
p2 items=5 hasPrev=true hasNext=false
ma KH01=6
hoTen nguyen=1                 # lọc bỏ dấu: "nguyen" khớp "Nguyễn"
sdt 0901=15
quick 0901=15
empty total=0 pages=0 page=1 items=0 from=0 to=0
clamp page=2 items=5           # xin trang 99 -> kẹp về trang cuối

# GUI render + nút phân trang (GUI → SERVICE → DAO)
trang1 rows=10 prev=false next=true
sau khi next rows=5 prev=true next=false
sau khi prev rows=10 prev=false

# App chạy sạch
$ timeout 8 java -cp "bin:lib/*" iuh.fit.Main
exit=124
```

## Key Decisions & Notes
- **Phân trang 1-based**, mặc định **10 dòng/trang**; trang được kẹp trong `[1, totalPages]`; kết quả rỗng trả `page=1, totalPages=0`.
- **Lọc bỏ dấu** dùng `Normalizer` NFD + thay `đ→d`, khớp `contains`, không phân biệt hoa/thường (giống `mockui/partials/table-filter.js`).
- **Lọc ở service**, phân trang do `PaginatedResult.of`; DAO chỉ trả raw (`findAll()` → `List`, tìm một bản ghi → `Optional`).
- Lọc ngày dùng **LGoodDatePicker** (`lib/LGoodDatePicker-11.2.1.jar`) qua `ManagementPanel.addDateRangeFilter`; ô ngày **chỉ chọn từ lịch** (`setAllowKeyboardEditing(false)`), không gõ tay. `getDateFilterFrom/To` trả `LocalDate` (bao gồm hai đầu mút), bỏ trống = không lọc. Format + nút Today/Clear lấy từ `app.date.*`; `Locale` lấy theo `AppContext.getLocale()` (không hard-code).
- Đổi bất kỳ ô lọc/tìm nhanh → tự tải **trang 1**; `Prev/Next` tải trang ±1.
- `xoa/delete` đã có ở DAO/service theo plan nhưng **chưa gắn UI** (nút xóa dòng thuộc hành động bảng chưa làm).
- `ManagementPanel` (kit dùng chung) được **mở rộng** thêm lọc + phân trang; `getTimKiemField()` cũ đổi thành `getQuickFilter()`.

## Out-of-Scope Observations
- (note) `Xe` và 5 trang còn lại của P1 chưa làm.
- (note) Chưa có hành động `Sửa`/`Xóa` dạng icon trên bảng (chưa làm).
- (note) Lọc hiện chạy client-side-after-fetch (service đọc hết rồi lọc) — đúng quy ước "DAO không lọc"; nếu dữ liệu lớn sẽ chuyển sang phân trang SQL sau.

## Acceptance Checklist
- [x] Trang mở từ menu `Tổ chức → Khách hàng`, hiển thị dữ liệu thật từ DB theo trang (10 dòng/trang, 15 KH → 2 trang).
- [x] Lọc theo Mã/SĐT/Họ tên/tìm nhanh và khoảng Ngày tạo hoạt động, khớp bỏ dấu; `Xóa lọc` trả về đầy đủ.
- [x] `‹ Trước`/`Sau ›` chuyển trang, nút bật/tắt đúng theo `hasPrevious/hasNext`, nhãn `Trang x/y` và đếm `Hiển thị a-b / tổng`.
- [x] `Lưu` thêm mới sinh mã `KH016`; cập nhật khi chọn dòng; validate SĐT/họ tên trả key i18n.
- [x] Build `javac` không lỗi; i18n parity; app chạy không stacktrace.
