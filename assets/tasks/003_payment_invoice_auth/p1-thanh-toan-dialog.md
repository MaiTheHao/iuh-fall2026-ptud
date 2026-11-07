# P1 — Dialog thanh toán QR / tiền mặt

**Dành cho:** Quốc · **Trạng thái:** ĐÃ DUYỆT · **Ước lượng:** 1 ngày

## Mục tiêu

Bấm "Xác Nhận Thanh Toán" ở màn Lập hóa đơn sẽ mở dialog cho chọn **Chuyển khoản QR** (hiện mã VietQR động) hoặc **Tiền mặt**, rồi ghi nhận thanh toán đúng phương thức đã chọn.

## Hiện trạng (đã có, không cần làm lại)

- `ThanhToanService.thanhToan(String maHoaDon, PhuongThucThanhToan phuongThuc, String ghiChu)` — ghi thanh toán trong một transaction, đã đóng dấu nhân viên và mã `TT-...`.
- `VietQrClient.taoQr(long soTien, String noiDung)` — trả ảnh PNG hoặc `null`, tự thử lại 1 lần, timeout 5s/8s.
- `QrConfig.bin()/soTk()/tenTk()` — đọc `src/resources/qr.properties`; **trả chuỗi rỗng** khi thiếu.
- `PhuongThucThanhToan` có `CHUYEN_KHOAN`, `TIEN_MAT` và key i18n `app.thanhToan.phuongThuc.*` (JSON chưa có).
- `LapHoaDonPage.thanhToan()` đang: lưu/cập nhật hóa đơn → `ThongBao.xacNhan` → `HoaDonService.thanhToan` (hardcode `TIEN_MAT`).

## Việc cần làm

### 1. Tạo `src/iuh/fit/gui/component/ThanhToanDialog.java`

- Kế thừa `JDialog` (modal), hoặc dùng `BaseFormDialog` nếu muốn có sẵn nút Lưu/Hủy. Nếu dùng `BaseFormDialog`: truyền `BooleanSupplier` thực hiện thanh toán, trả `false` khi lỗi để dialog không đóng.
- Constructor: `ThanhToanDialog(Window owner, long soTien, String maHoaDon)`.
- Hai method public: `boolean duocXacNhan()` và `PhuongThucThanhToan phuongThuc()`.
- Nội dung:
  - Hai radio: **Chuyển khoản QR** (mặc định) và **Tiền mặt**.
  - Dòng số tiền, định dạng `#,##0` + " đ".
  - Vùng ảnh 280×280 cho QR; dưới ảnh là khối text: tên ngân hàng/BIN, số tài khoản, số tiền, nội dung.
  - Nút **Xác nhận** luôn bật, kể cả khi QR lỗi. Nút **Hủy** đóng dialog.

### 2. Tải QR trong nền

- Khi dialog hiện và đang chọn Chuyển khoản: hiện "Đang tạo mã QR…", chạy `SwingWorker` gọi `VietQrClient.taoQr(soTien, "TT " + maHoaDon)`.
- Trong `done()`: có ảnh thì gắn `ImageIcon`; `null` thì hiện `qr.jpg` + khối text (fallback).
- **Kiểm tra trước khi gọi API:** nếu một trong ba giá trị `QrConfig.bin()/soTk()/tenTk()` rỗng thì bỏ qua API, hiện fallback ngay.
- Nội dung chuyển khoản: `TT <maHoaDon>`, chỉ ASCII, tối đa 25 ký tự.
- Không gọi mạng trên EDT.

### 3. Nối vào `LapHoaDonPage`

- Giữ nguyên bước `luuHoaDon()` / `capNhatHoaDon(false)`.
- Bỏ `ThongBao.xacNhan(...)` và lời gọi `HoaDonService.thanhToan(...)`.
- Mở `ThanhToanDialog`; nếu `duocXacNhan()` thì gọi `ThanhToanService.thanhToan(maHoaDonHienTai, dialog.phuongThuc(), ghiChu)`.
- Thành công: gán `trangThaiHienTai`, gọi `capNhatQuyen()`, `ThongBao.thongTin("app.common.update_succeed")`.
- Hủy: không đổi gì, hóa đơn ở lại `CHO_THANH_TOAN`.
- Bắt `IllegalArgumentException`/`IllegalStateException` → `ThongBao.loi(e)`; `SQLException` → `ThongBao.loi("app.login.errorSystem")`.

### 4. Key i18n cần thêm (cả vn và en)

`app.thanhToan.dialogTitle`, `.phuongThuc`, `.soTien`, `.noiDung`, `.dangTaoQr`, `.qrLoi`, `.xacNhan`, `.huy`, và **hai key enum đang thiếu**: `app.thanhToan.phuongThuc.chuyenKhoan`, `app.thanhToan.phuongThuc.tienMat`.

## Kiểm thử tay

1. Tạo hóa đơn mới, bấm Xác Nhận Thanh Toán → dialog hiện, mặc định Chuyển khoản.
2. Chọn QR, xác nhận → `SELECT phuongThuc FROM ThanhToan` có dòng `CHUYEN_KHOAN`.
3. Chọn Tiền mặt, xác nhận → dòng mới là `TIEN_MAT`, không có request QR.
4. Mở dialog rồi bấm Hủy → hóa đơn vẫn `CHO_THANH_TOAN`, không có dòng thanh toán mới.
5. Để trống `src/resources/qr.properties`, build lại → dialog hiện `qr.jpg` + text, vẫn xác nhận được.
6. Bấm xác nhận 2 lần liên tiếp → lần 2 báo lỗi trạng thái, không có dòng thứ hai.

## Bẫy cần tránh

- **Không** gọi `HoaDonService.thanhToan` trong dialog: method đó hardcode `TIEN_MAT`, khách chọn QR vẫn bị ghi tiền mặt.
- `QrConfig` trả `""` chứ không phải `null`, nên chỉ kiểm tra `taoQr == null` là chưa đủ.
- `qr.jpg` phải có trong `bin/resources` sau khi build; nếu thiếu thì fallback chỉ còn khối text.
- Đóng dialog khi worker đang chạy: bỏ qua kết quả, không lỗi.

## Xong khi

- 6 mục kiểm thử trên đều đạt, build 0 lỗi, không còn chỗ nào gọi `ThongBao.xacNhan` cho bước thanh toán.
