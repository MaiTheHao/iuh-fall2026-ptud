# E2E A — Thanh toán QR / tiền mặt

- **Người test:** Nguyễn Minh Quốc (người xây `ThanhToanDialog`) · **Nhánh:** `test/e2e-a-thanh-toan`
- **Ngày chạy:** 10/11/2026 · **Commit `develop`:** ________ · **DB:** sạch, migration `V0`–`V15` (gồm seed mới)
- **Màn phạm vi:** SCR-10 Lập hóa đơn, SCR-10B Quản lý hóa đơn
- **Quy ước:** dữ liệu tự tạo có tiền tố `E2E-A`. Điền `PASS / NO` ngay sau mỗi kịch bản; `NO` thì bắt buộc mô tả lỗi theo mẫu 7 dòng trong file hướng dẫn.
- **Chuẩn bị:** đăng nhập `admin`. Tạo trước ít nhất 3 phiếu tiếp nhận chưa có hóa đơn (SCR-04, biển số khớp regex, ví dụ `59C12345`), dùng để lập 3+ hóa đơn `CHO_THANH_TOAN` ở SCR-10. Một hóa đơn nên có dịch vụ + phụ tùng + voucher `VC-20261020-000007` (20%) để có "thực thu ≠ tổng gộp".

Dữ liệu dùng chung	Giá trị
Phụ tùng	`PT-20261020-000002` Lọc dầu động cơ / 65.000 / tồn 32
Dịch vụ	`DV-20261020-000009` Kiểm tra ắc quy / 70.000
Voucher hợp lệ	`VC-20261020-000007` / giảm 20%, hạn 31/08/2027
---

### E2E-A-01 · Bấm thanh toán mở dialog đúng nội dung
**Màn:** SCR-10
**Tiền điều kiện:** có hóa đơn `CHO_THANH_TOAN` với tổng thực thu đã biết (ví dụ: dịch vụ 70.000 + 2 × lọc dầu 130.000, voucher 20% → thực thu 200.000 × 0,8 = 160.000).
**Các bước:**
1. Mở hóa đơn, bấm **Xác Nhận Thanh Toán**.
2. Quan sát dialog vừa mở (không bấm gì).
**Kết quả mong đợi:** dialog hiện dạng modal; ô phương thức mặc định là **Chuyển khoản QR**; dòng số tiền đúng thực thu, định dạng `#,##0` + " đ"; khối thông tin hiện ngân hàng/BIN, số tài khoản, tên chủ tài khoản, số tiền, nội dung chuyển khoản dạng `TT <mã hóa đơn>` (chỉ ASCII, tối đa 25 ký tự); sau khi dialog hiện, vùng QR bắt đầu tải ("Đang tạo mã QR…").

**PASS / NO:** PASS
**Mô tả thực tế / lỗi chi tiết:**
>
qr.properties phai co thong tin
---

### E2E-A-02 · Thanh toán chuyển khoản QR thành công
**Màn:** SCR-10
**Tiền điều kiện:** tiếp tục từ A-01 hoặc dùng hóa đơn khác; mạng bình thường, `qr.properties` còn nguyên.
**Các bước:**
1. Chờ ảnh QR hiện trong dialog.
2. (Tùy chọn) Nhập ghi chú `E2E-A thanh toán QR`.
3. Bấm **Xác nhận**.
4. Kiểm tra DB: bảng `ThanhToan` và cột `trangThaiHoaDon` của `HoaDon`.
**Kết quả mong đợi:** ảnh QR hiển thị không lỗi; sau khi xác nhận có thông báo thành công, hóa đơn chuyển `DA_THANH_TOAN`, form khóa chỉnh sửa; DB có đúng 1 dòng `ThanhToan`: `phuongThuc = CHUYEN_KHOAN`, `soTien` = thực thu (không phải tổng gộp), `thoiGian` ≈ thời điểm xác nhận, `maNhanVien` = người đang đăng nhập, `maThanhToan` bắt đầu bằng `TT`, `ghiChu` lưu đúng.

**PASS / NO:** PASS
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-A-03 · Thanh toán tiền mặt
**Màn:** SCR-10
**Các bước:**
1. Mở hóa đơn `CHO_THANH_TOAN` thứ hai, bấm Xác Nhận Thanh Toán.
2. Chọn **Tiền mặt**. Quan sát.
3. Bấm **Xác nhận**. Kiểm tra DB bảng `ThanhToan`.
**Kết quả mong đợi:** vùng QR ẩn ngay khi chọn Tiền mặt (không có request tạo QR nào); sau xác nhận DB ghi dòng `phuongThuc = TIEN_MAT`, `soTien` đúng, hóa đơn `DA_THANH_TOAN`.

**PASS / NO:** PASS
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-A-04 · Hủy dialog không làm đổi trạng thái
**Màn:** SCR-10
**Các bước:**
1. Mở hóa đơn `CHO_THANH_TOAN` thứ ba, bấm Xác Nhận Thanh Toán.
2. Bấm **Hủy**. Mở lại dialog rồi bấm **ESC** (hoặc đóng cửa sổ).
3. Kiểm tra DB bảng `ThanhToan` và trạng thái hóa đơn.
**Kết quả mong đợi:** cả hai lần dialog đóng, hóa đơn vẫn `CHO_THANH_TOAN`, form vẫn sửa được, DB không có dòng thanh toán mới, không lỗi.

**PASS / NO:** PASS
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-A-05 · Ghi chú theo phương thức
**Màn:** SCR-10
**Các bước:**
1. Thanh toán một hóa đơn bằng **Chuyển khoản** kèm ghi chú `E2E-A ghi chú QR`.
2. Thanh toán một hóa đơn bằng **Tiền mặt** kèm ghi chú `E2E-A ghi chú TM`.
3. Kiểm tra cột `ghiChu` của 2 dòng `ThanhToan`.
**Kết quả mong đợi:** dòng QR lưu ghi chú đã nhập; dòng tiền mặt ghi chú rỗng/NULL (hành vi hiện tại — ghi lại thực tế nếu khác); không văng lỗi.

**PASS / NO:** PASS
**Mô tả thực tế / lỗi chi tiết:**
>
	người dùng phải tự nhập ghi chú hệ thôngs không sinhh
---

### E2E-A-06 · Fallback khi thiếu cấu hình QR
**Màn:** SCR-10
**Tiền điều kiện:** được phép sửa tạm `src/resources/qr.properties` trên máy local (đổi tên hoặc xóa trắng nội dung — **không commit**).
**Các bước:**
1. Đổi tên `src/resources/qr.properties`, build lại, chạy app.
2. Thanh toán một hóa đơn bằng Chuyển khoản.
3. Trả lại file như cũ, build lại (ghi lại trong mô tả).
**Kết quả mong đợi:** dialog không gọi API, hiện ngay ảnh `qr.jpg` + khối text; nút Xác nhận vẫn hoạt động; thanh toán vẫn ghi được `CHUYEN_KHOAN` vào DB; app không văng lỗi.

**PASS / NO:** PASS
**Mô tả thực tế / lỗi chi tiết:**
>
	nhưng nội dung như tên và stk bin = "" 
---

### E2E-A-07 · Mất mạng khi tạo QR
**Màn:** SCR-10
**Các bước:**
1. Ngắt mạng máy (rút cáp/tắt Wi-Fi) sau khi dialog đã mở ở bước Chuyển khoản.
2. Đợi kết quả tải QR; thanh toán vẫn bằng nút Xác nhận.
3. Bật lại mạng.
**Kết quả mong đợi:** sau timeout (đợi tối đa ~60 giây, UI vẫn thao tác được trong lúc chờ), dialog hiện ảnh dự phòng `qr.jpg`/thông báo lỗi QR + khối text, không treo giao diện; thanh toán vẫn thành công và ghi đúng DB.

**PASS / NO:** PASS
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-A-08 · Không thanh toán hai lần cho cùng hóa đơn
**Màn:** SCR-10 / SCR-10B
**Các bước:**
1. Mở lại hóa đơn đã thanh toán ở A-02, thử bấm Xác Nhận Thanh Toán.
2. Mở hóa đơn đó ở SCR-10B, kiểm tra nhãn trạng thái và nút.
**Kết quả mong đợi:** form đã khóa (nút thanh toán không bấm được, hoặc nếu bấm được thì bị từ chối với thông báo rõ ràng từ i18n, không lộ lỗi SQL trùng khóa `UQ_ThanhToan_maHoaDon`); DB vẫn chỉ 1 dòng `ThanhToan` cho hóa đơn này.

**PASS / NO:** PASS
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-A-09 · Hủy hóa đơn đã thanh toán — dòng ThanhToan ra sao
**Màn:** SCR-10B
**Tiền điều kiện:** hóa đơn `DA_THANH_TOAN` có phụ tùng (dùng hóa đơn ở A-03); ghi tồn phụ tùng trước.
**Các bước:**
1. Ở SCR-10B, hủy hóa đơn đã thanh toán, nhập lý do `E2E-A hủy HĐ đã thu`.
2. Kiểm tra DB: `HoaDon`, `ThanhToan`, tồn kho phụ tùng.
3. Chọn hóa đơn đã hủy đó, thử In hóa đơn (chi tiết hơn ở lane B).
**Kết quả mong đợi:** hóa đơn chuyển `DA_HUY` kèm lý do, tồn kho được hoàn lại; hành vi với dòng `ThanhToan` (giữ nguyên hay xóa) và nội dung bản in sau hủy — **ghi lại đúng thực tế**; mọi thông báo rõ ràng, không văng lỗi.

**PASS / NO:** PASS
**Mô tả thực tế / lỗi chi tiết:**
>
	
---

## Tổng kết

- Số kịch bản: 9 · **PASS:** ____ · **NO:** ____
- Ghi chú thêm của người test:
>
