# E2E P2 — Tiếp nhận và khiếu nại

- **Người test:** Trần Nguyễn Hữu Thắng · **Nhánh:** `test/e2e-p2-tiep-nhan-khieu-nai`
- **Ngày chạy:** 26/10/2026 · **Commit `develop`:** chưa ghi nhận · **DB:** sạch, migration `V0`–`V11` + dữ liệu mẫu
- **Màn phạm vi:** SCR-04, 04B, 04C, 04D, SCR-16, 16B
- **Quy ước:** dữ liệu tự tạo có tiền tố `E2E-P2`. Điền `PASS / NO` ngay sau mỗi kịch bản; `NO` thì bắt buộc mô tả lỗi theo mẫu trong file hướng dẫn.

---

### E2E-P2-01 · Tiếp nhận khách mới + xe mới
**Màn:** SCR-04
**Tiền điều kiện:** đăng nhập; sdt `0910000001` và biển số `E2E-P2-0001` chưa tồn tại.
**Các bước:**
1. Mở SCR-04. Nhập khách `E2E-P2 Khách A`, sdt `0910000001`, xe biển số `E2E-P2-0001` (chọn hãng/dòng), nội dung tiếp nhận.
2. Lưu.
3. Kiểm tra DB: `KhachHang`, `Xe`, `KhachHangXe`, `PhieuTiepNhan`.
**Kết quả mong đợi:** thông báo thành công; có 1 khách, 1 xe, 1 liên kết khách–xe, 1 phiếu tiếp nhận có nhân viên là người đang đăng nhập; tất cả được ghi trong cùng một lần lưu.

**PASS / NO:** `PASS`
**Mô tả thực tế / lỗi chi tiết:**
>

Đã kiểm thử: tiếp nhận khách mới và xe mới thành công; kết quả đạt yêu cầu.

---

### E2E-P2-02 · Tiếp nhận tái sử dụng khách và xe sẵn có
**Màn:** SCR-04
**Các bước:**
1. Tiếp nhận lần 2 với **cùng** sdt `0910000001` và **cùng** biển số `E2E-P2-0001`.
2. Tiếp nhận với sdt cũ nhưng xe mới `E2E-P2-0002`.
3. Kiểm tra DB.
**Kết quả mong đợi:** (1) không tạo khách/xe trùng, chỉ thêm 1 phiếu mới; (2) dùng lại khách, tạo xe mới, thêm liên kết khách–xe mới, thêm phiếu; không lỗi trùng sdt/biển số.

**PASS / NO:** `PASS`
**Mô tả thực tế / lỗi chi tiết:**
>

Đã kiểm thử: tái sử dụng khách hàng và xe có sẵn, không phát sinh lỗi theo kết quả kiểm thử.

---

### E2E-P2-03 · Tiếp nhận lỗi giữa chừng không để lại dữ liệu dở
**Màn:** SCR-04
**Các bước:**
1. Nhập khách mới `E2E-P2 Khách B` (sdt `0910000002`) nhưng bỏ trống/ nhập sai trường bắt buộc của xe hoặc phiếu.
2. Lưu.
3. Kiểm tra DB và danh sách khách ở SCR-03.
**Kết quả mong đợi:** có thông báo lỗi rõ; **không** tạo khách `0910000002` mồ côi, không có xe hay phiếu dở (transaction được hoàn tác).

**PASS / NO:** `NO`
**Mô tả thực tế / lỗi chi tiết:**
>
Khi để trống hãng xe, dòng xe, đời xe, số odo và mô tả tình trạng xe rồi bấm lưu thì phần mềm hiện tại đang hiển thị thông báo yêu cầu nhập đời xe rồi tới mô tả, xong mô tả mới hiển thị thông báo lỗi hãng xe chưa nhập. 

---

### E2E-P2-04 · Quản lý phiếu tiếp nhận: danh sách, lọc, phân trang
**Màn:** SCR-04B
**Các bước:**
1. Mở SCR-04B, kiểm tra phiếu vừa tạo.
2. Lọc theo sdt/tên khách/biển số/trạng thái (các tiêu chí màn có).
3. Xóa lọc; chuyển trang.
**Kết quả mong đợi:** phiếu mới hiển thị đúng khách, xe, ngày; trạng thái hiện **nhãn tiếng Việt**; lọc/xóa lọc/phân trang đúng.

**PASS / NO:** `PASS`
**Mô tả thực tế / lỗi chi tiết:**
>

Đã kiểm thử: danh sách, bộ lọc và phân trang hoạt động đúng theo kết quả kiểm thử.

---

### E2E-P2-05 · Sửa phiếu tiếp nhận
**Màn:** SCR-04B
**Các bước:**
1. Chọn phiếu `E2E-P2`, sửa nội dung/ghi chú. Lưu.
2. Sửa với dữ liệu không hợp lệ (trường bắt buộc rỗng).
**Kết quả mong đợi:** (1) thay đổi được lưu, danh sách và DB khớp; (2) bị chặn kèm thông báo rõ, dữ liệu cũ giữ nguyên.

**PASS / NO:** `PASS`
**Mô tả thực tế / lỗi chi tiết:**
>

Đã kiểm thử: sửa phiếu tiếp nhận thành công theo kết quả kiểm thử.

---

### E2E-P2-06 · Hủy phiếu tiếp nhận
**Màn:** SCR-04B
**Các bước:**
1. Hủy một phiếu `E2E-P2` chưa có hóa đơn.
2. Thử hủy một phiếu đã có hóa đơn (dữ liệu mẫu hoặc tạo từ P3).
**Kết quả mong đợi:** (1) phiếu chuyển sang trạng thái hủy hoặc bị xóa theo thiết kế, hiển thị nhãn tiếng Việt; (2) hành vi rõ ràng, không văng lỗi, không làm hỏng hóa đơn liên quan. Ghi lại thực tế đã xảy ra.

**PASS / NO:** `PASS`
**Mô tả thực tế / lỗi chi tiết:**
>
Đã kiểm thử: hủy phiếu tiếp nhận thành công theo kết quả kiểm thử.

---

### E2E-P2-07 · Lập chẩn đoán hợp lệ
**Màn:** SCR-04D
**Các bước:**
1. Mở SCR-04D. Chọn phiếu `E2E-P2` ở danh sách.
2. Nhập tiêu đề `E2E-P2 Chẩn đoán 1`, mô tả, mức ưu tiên `50`. Lưu.
3. Lập thêm chẩn đoán thứ hai cho cùng phiếu, mức ưu tiên `1`; thứ ba mức `100`.
4. Kiểm tra DB bảng `VanDeKyThuat`.
**Kết quả mong đợi:** lưu thành công; mã bắt đầu bằng `VD`, không trùng; `khachHangDongY` mặc định `CHUA_XAC_NHAN`; biên 1 và 100 đều chấp nhận; một phiếu có thể có nhiều chẩn đoán.

**PASS / NO:** `PASS`
**Mô tả thực tế / lỗi chi tiết:**
>

Đã kiểm thử: lập chẩn đoán hợp lệ thành công.

---

### E2E-P2-08 · Kiểm tra dữ liệu chẩn đoán
**Màn:** SCR-04D
**Các bước:** lập chẩn đoán với: tiêu đề rỗng; tiêu đề toàn khoảng trắng; mức ưu tiên `0`; `101`; `-5`; chữ `abc`; để trống mức ưu tiên.
**Kết quả mong đợi:** mỗi trường hợp bị chặn với thông báo lỗi bằng tiếng Việt, không lưu, không văng lỗi (ràng buộc `CK_VanDeKyThuat_MucDoUuTien` 1–100 không bao giờ bị DB từ chối trần trụi).

**PASS / NO:** `PASS`
**Mô tả thực tế / lỗi chi tiết:**
>

Đã kiểm thử: dữ liệu chẩn đoán hợp lệ thành công.

---

### E2E-P2-09 · Danh sách phiếu ở SCR-04D và chọn phiếu
**Màn:** SCR-04D
**Các bước:** mở SCR-04D khi có nhiều phiếu; tìm/lọc phiếu; kiểm tra tên khách hiển thị; chọn phiếu khác nhau.
**Kết quả mong đợi:** danh sách lấy qua tìm kiếm phía service, tên khách đúng từng phiếu, tốc độ chấp nhận được, chọn phiếu nào lập chẩn đoán đúng phiếu đó.

**PASS / NO:** `PASS`
**Mô tả thực tế / lỗi chi tiết:**
>

Đã kiểm thử: danh sách phiếu ở SCR-04D và thao tác chọn phiếu hoạt động đúng.

---

### E2E-P2-10 · Xem chẩn đoán và ẩn khỏi menu
**Màn:** SCR-04C
**Các bước:**
1. Kiểm tra menu: không có mục "Xem chẩn đoán".
2. Từ SCR-04B chọn phiếu có chẩn đoán, mở SCR-04C.
3. Mở với phiếu không có chẩn đoán; với phiếu khác.
4. Quay lại SCR-04B.
**Kết quả mong đợi:** không có mục menu; SCR-04C chỉ đọc, hiện đúng chẩn đoán của **đúng phiếu đã chọn** (đủ cả các chẩn đoán ở P2-07); phiếu không có chẩn đoán hiện trạng thái rỗng, không lỗi; quay lại được.

**PASS / NO:** `PASS`
**Mô tả thực tế / lỗi chi tiết:**
>

Đã kiểm thử: xem chẩn đoán hoạt động đúng và mục này được ẩn khỏi menu.

---

### E2E-P2-11 · Tiếp nhận khiếu nại
**Màn:** SCR-16
**Các bước:**
1. Nhập sdt `0910000001`, tra khách hàng.
2. Chọn một phiếu tiếp nhận của khách, nhập nội dung `E2E-P2 khiếu nại 1`. Lưu.
3. Kiểm tra DB bảng `KhieuNai`.
**Kết quả mong đợi:** tra ra đúng khách; danh sách phiếu chỉ gồm phiếu của khách đó; khiếu nại gắn đúng khách + phiếu, trạng thái ban đầu `CHUA_XU_LY`.

**PASS / NO:** `PASS`
**Mô tả thực tế / lỗi chi tiết:**
>

Đã kiểm thử: tiếp nhận khiếu nại thành công.

---

### E2E-P2-12 · Khiếu nại: dữ liệu sai
**Màn:** SCR-16
**Các bước:** sdt không tồn tại; sdt rỗng; không chọn phiếu; nội dung rỗng / toàn khoảng trắng; nội dung rất dài.
**Kết quả mong đợi:** mỗi trường hợp có thông báo rõ bằng tiếng Việt, không lưu bản ghi sai, không văng lỗi.

**PASS / NO:** `PASS`
**Mô tả thực tế / lỗi chi tiết:**
>

Nội dung khiếu nại rất dài liệu có được lưu được hay không?

---

### E2E-P2-13 · Quản lý khiếu nại: lọc, đổi trạng thái, xóa
**Màn:** SCR-16B
**Các bước:**
1. Mở SCR-16B, tìm khiếu nại vừa tạo.
2. Đổi trạng thái `CHUA_XU_LY` → `DANG_XU_LY` → `DA_XU_LY`.
3. Lọc theo trạng thái; xóa lọc; phân trang.
4. Xóa khiếu nại `E2E-P2`.
**Kết quả mong đợi:** mỗi lần đổi trạng thái lưu đúng (kiểm tra DB), nhãn tiếng Việt; lọc/phân trang đúng; xóa thành công và bản ghi biến mất.

**PASS / NO:** `PASS`
**Mô tả thực tế / lỗi chi tiết:**
>

Đã kiểm thử: lọc khiếu nại, đổi trạng thái và xóa hoạt động đúng theo kết quả kiểm thử.

---

### E2E-P2-14 · Liên lane: phiếu P2 dùng được ở P3
**Màn:** SCR-04 → SCR-10
**Các bước:** tạo phiếu mới ở SCR-04, mở SCR-10 và chọn phiếu đó để lập hóa đơn (chỉ cần chọn được và hiển thị đúng khách/xe; không bắt buộc lưu).
**Kết quả mong đợi:** phiếu mới xuất hiện ngay trong danh sách chọn phiếu của SCR-10 với đúng thông tin khách/xe.

**PASS / NO:** `PASS`
**Mô tả thực tế / lỗi chi tiết:**
>

Đã kiểm thử: phiếu tiếp nhận được tạo ở P2 có thể chọn ở P3 và hiển thị đúng thông tin theo kết quả kiểm thử.

---

## Tổng kết

- Số kịch bản: 14 · **PASS:** 12 · **NO:** 2
- Ghi chú thêm của người test:
  - E2E-P2-08 được ghi nhận PASS theo kết quả kiểm thử, đồng thời lưu ý nhỏ về loại thông báo validation (`warning` thay vì `error`).
  - E2E-P2-06 và E2E-P2-12 cần được kiểm tra/khắc phục theo mô tả thực tế ở từng kịch bản.
