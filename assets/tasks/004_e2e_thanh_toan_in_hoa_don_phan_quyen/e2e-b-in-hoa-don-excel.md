# E2E B — In hóa đơn và xuất Excel

- **Người test:** Nguyễn Trần Trọng Khang (chủ màn SCR-10/10B) · **Nhánh:** `test/e2e-b-in-hoa-don-excel`
- **Ngày chạy:** ________ · **Commit `develop`:** ________ · **DB:** sạch, migration `V0`–`V15` (gồm seed mới)
- **Màn phạm vi:** SCR-10B Quản lý hóa đơn (thanh nút In hóa đơn / Xuất Excel)
- **Quy ước:** dữ liệu tự tạo có tiền tố `E2E-B`; file Excel lưu trong thư mục `E2E-B-out`. Điền `PASS / NO` ngay sau mỗi kịch bản; `NO` thì bắt buộc mô tả lỗi theo mẫu 7 dòng trong file hướng dẫn.
- **Chuẩn bị:** đăng nhập `admin`. Cần ít nhất: 1 hóa đơn **đã thanh toán** có cả dịch vụ + phụ tùng + voucher (tự lập và thanh toán qua SCR-10 nếu lane A chưa xong, ghi mã hóa đơn vào bảng dưới), 1 hóa đơn **chưa thanh toán** có tên hàng dài (thêm phụ tùng/dịch vụ với tên dài ở SCR-06/08 trước khi lập hóa đơn), và hóa đơn `DA_HUY` (từ E2E-A-09 hoặc tự hủy) để kiểm tra bản in.

Bảng hóa đơn dùng chung	(Ghi khi tạo)
HĐ đã thanh toán (QR)	mã: ________
HĐ đã thanh toán (tiền mặt)	mã: ________
HĐ chưa thanh toán, tên hàng dài	mã: ________
HĐ đã hủy	mã: ________
---

### E2E-B-01 · In hóa đơn đã thanh toán
**Màn:** SCR-10B
**Các bước:**
1. Chọn hóa đơn đã thanh toán (QR). Bấm **In hóa đơn**.
2. Trong hộp thoại in, chọn "Microsoft Print to PDF", lưu file, mở bản PDF.
**Kết quả mong đợi:** bản in đúng **1 trang A4 dọc**, đủ 6 khối theo thứ tự: (1) tên cửa hàng + "HÓA ĐƠN THANH TOÁN", (2) mã hóa đơn + ngày tạo, (3) khách hàng + SĐT + biển số, (4) bảng 5 cột Loại/Tên/Số lượng/Đơn giá/Thành tiền, (5) tổng gốc / giảm giá / thực thu, (6) khối thanh toán có **phương thức + thời gian + nhân viên thu**; tiếng Việt không vỡ phông; số tiền có dấu phân cách nghìn; tổng khớp hóa đơn ở màn hình.

**PASS / NO:** ________
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-B-02 · In hóa đơn chưa thanh toán và hóa đơn đã hủy
**Màn:** SCR-10B
**Các bước:**
1. Chọn hóa đơn chưa thanh toán, bấm In hóa đơn, in ra PDF.
2. Chọn hóa đơn đã hủy, bấm In hóa đơn, in ra PDF.
**Kết quả mong đợi:** (1) khối thanh toán ghi "Chưa thanh toán", các khối còn lại đầy đủ; (2) bản in hóa đơn đã hủy hiển thị rõ ràng/không văng lỗi — **ghi lại thực tế** (có trạng thái hủy, hay vẫn in như thường, hay bị chặn).

**PASS / NO:** ________
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-B-03 · Hủy hộp thoại in
**Màn:** SCR-10B
**Các bước:**
1. Chọn một hóa đơn, bấm In hóa đơn, bấm **Hủy** trong hộp thoại in.
2. Bấm lại In hóa đơn với hóa đơn khác để chắc chắn nút vẫn dùng được.
**Kết quả mong đợi:** hủy hộp thoại thì im lặng, không thông báo lỗi, dữ liệu không đổi; nút In không bị kẹt ở trạng thái mờ (disabled) — lần in sau vẫn chạy được.

**PASS / NO:** ________
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-B-04 · Xuất Excel hóa đơn đã thanh toán
**Màn:** SCR-10B
**Các bước:**
1. Chọn hóa đơn đã thanh toán (QR), bấm **Xuất Excel**.
2. Lưu file với tên mặc định, mở bằng Excel hoặc LibreOffice.
3. Đối chiếu từng khối với hóa đơn ở SCR-10 và DB (`ChiTietHoaDon`, `ThanhToan`, `HoaDon`).
**Kết quả mong đợi:** tên file mặc định `HoaDon-<mã>.xlsx`; sheet tên `HoaDon-<mã>` (đã lọc ký tự cấm, tối đa 31 ký tự); đủ khối: tiêu đề merge, thông tin khách/biển số/nhân viên, bảng dòng hàng, khối tổng (tổng gốc/giảm giá/thực thu), khối thanh toán (phương thức/thời gian/nhân viên); ô tiền là **số** định dạng `#,##0` (căn phải, cộng lại được), ngày giờ đúng định dạng; tổng khớp màn hình.

**PASS / NO:** ________
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-B-05 · Xuất Excel hóa đơn chưa thanh toán
**Màn:** SCR-10B
**Các bước:**
1. Chọn hóa đơn chưa thanh toán, bấm Xuất Excel, mở file.
**Kết quả mong đợi:** khối thanh toán ghi "Chưa thanh toán" (hoặc tương đương), không ô trống sai, không lỗi; tổng khớp màn hình.

**PASS / NO:** ________
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-B-06 · File Excel đang mở rồi xuất đè
**Màn:** SCR-10B
**Các bước:**
1. Xuất một hóa đơn ra file, **mở file đó trong Excel** và giữ mở.
2. Xuất lại cùng hóa đơn ra cùng tên file.
**Kết quả mong đợi:** (theo đặc tả 003-P2) có thông báo **riêng** yêu cầu đóng file rồi thử lại — nếu app chỉ hiện thông báo lỗi chung (`app.hoaDon.xuatLoi`) thì đánh `NO` và chép nguyên văn thông báo; không crash, không ghi file dở; sau khi đóng file, xuất lại thì thành công.

**PASS / NO:** ________
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-B-07 · Chưa chọn dòng nào
**Màn:** SCR-10B
**Các bước:**
1. Mở SCR-10B, không chọn dòng nào, bấm **In hóa đơn**.
2. Bấm **Xuất Excel**.
**Kết quả mong đợi:** cả hai lần chỉ hiện cảnh báo ("chưa chọn dòng"), không mở hộp thoại in/lưu, không văng lỗi.

**PASS / NO:** ________
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-B-08 · Nội dung biên bản in: tên dài và số liệu biên
**Màn:** SCR-10B
**Các bước:**
1. In ra PDF hóa đơn chứa **tên hàng rất dài** (tạo ở bước chuẩn bị).
2. Kiểm tra dòng hàng của tên dài; kiểm tra hóa đơn chỉ có 1 dòng hàng (1 × dịch vụ); hóa đơn có nhiều dòng.
**Kết quả mong đợi:** tên dài được cắt bằng dấu ba chấm (`...` hoặc `…`) trong đúng một dòng, không đẩy lệch cột khác; hóa đơn 1 dòng vẫn có đủ khối tổng; nhiều dòng thì các dòng không trùng/mất, cột thẳng hàng.

**PASS / NO:** ________
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-B-09 · Ngôn ngữ của thanh nút và file xuất
**Màn:** SCR-10B
**Các bước:**
1. Ở tiếng Việt, rà nhãn hai nút **In hóa đơn / Xuất Excel**, nhãn dialog lưu file, thông báo cảnh báo.
2. Đổi sang tiếng Anh, rà lại toàn bộ các chữ trên; xuất thêm một file Excel.
**Kết quả mong đợi:** không có key i18n thô (`app.xxx.yyy`); bản tiếng Anh đổi nhãn nút/dialog/thông báo (tên file và sheet giữ nguyên cấu trúc); không lỗi phông.

**PASS / NO:** ________
**Mô tả thực tế / lỗi chi tiết:**
>

---

## Tổng kết

- Số kịch bản: 9 · **PASS:** ____ · **NO:** ____
- Ghi chú thêm của người test:
>
