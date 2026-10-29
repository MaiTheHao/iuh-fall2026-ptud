# E2E P4 — Kế hoạch và tổng kết

- **Người test:** Đặng Thanh Bình · **Nhánh:** `test/e2e-p4-ke-hoach-tong-ket`
- **Ngày chạy:** 29/10/2026 · **Commit `develop`:** ________ · **DB:** sạch, migration `V0`–`V11` + dữ liệu mẫu
- **Màn phạm vi:** SCR-05, 05B, 05C, SCR-12, 12B, 12C
- **Quy ước:** dữ liệu tự tạo có ghi chú tiền tố `E2E-P4`. Điền `PASS / NO` ngay sau mỗi kịch bản; `NO` thì bắt buộc mô tả lỗi theo mẫu trong file hướng dẫn.
- **Lưu ý:** P4-13 là kịch bản chạy xuyên suốt cả bốn lane; chạy sau khi P1–P3 đã có kết quả. Điểm mở "doanh thu gộp hay trừ voucher" đã biết, xem P4-09.

---

### E2E-P4-01 · Phân công ca thành công
**Màn:** SCR-05
**Các bước:**
1. Mở SCR-05. Chọn một nhân viên, ngày trong tuần hiện tại, ca `CA_SANG`, ghi chú `E2E-P4`. Lưu.
2. Phân công tiếp cùng nhân viên cùng ngày nhưng ca `CA_CHIEU`; sau đó cùng nhân viên ngày khác ca `CA_NGAY`.
3. Kiểm tra DB bảng `PhanCong`.
**Kết quả mong đợi:** cả ba phân công lưu được, mã sinh tự động và không trùng; danh sách hiển thị đúng nhân viên, ngày, ca bằng nhãn tiếng Việt; lọc vai trò khi chọn nhân viên hoạt động đúng.

**PASS / NO:** `PASS`
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-P4-02 · Chặn phân công trùng nhân viên–ngày–ca
**Màn:** SCR-05
**Các bước:**
1. Phân công lại **đúng** nhân viên + ngày + ca của P4-01 bước 1.
2. Phân công nhân viên khác vào cùng ngày + ca đó.
3. Sửa một phân công thành tổ hợp trùng với phân công đã có.
**Kết quả mong đợi:** (1) bị từ chối với thông báo từ i18n, không tạo bản ghi (DB không có ràng buộc, service phải chặn); (2) được phép; (3) cũng bị chặn, bản ghi cũ giữ nguyên.

**PASS / NO:** `PASS`
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-P4-03 · Lọc, sửa, xóa phân công
**Màn:** SCR-05
**Các bước:**
1. Lọc theo mã phân công, tên nhân viên, khoảng ngày, ca; kết hợp nhiều tiêu chí; xóa lọc.
2. Sửa ghi chú và ca của một phân công `E2E-P4`. Lưu.
3. Xóa phân công `E2E-P4`.
**Kết quả mong đợi:** lọc đúng từng tiêu chí và khi kết hợp, biên khoảng ngày bao gồm cả ngày đầu/cuối; sửa lưu đúng; xóa thành công, DB khớp.

**PASS / NO:** `NO`
**Mô tả thực tế / lỗi chi tiết:**
>
1. không thể lọc phân công ở UI mới. ui đang bị lỗi không xóa các phân công có tên nhân viên quá dài do mất nút xóa
2. PASS
3. PASS
---

### E2E-P4-04 · Lịch của tôi
**Màn:** SCR-05B
**Các bước:**
1. Đăng nhập bằng nhân viên A đã được phân công ở tuần hiện tại. Mở SCR-05B.
2. Điều hướng tuần trước / tuần này / tuần sau.
3. Đăng nhập bằng nhân viên B (khác) và mở lại.
4. Mở với nhân viên chưa có phân công nào trong tuần.
**Kết quả mong đợi:** lịch thứ Hai → Chủ nhật, ba ca; chỉ hiện phân công của **người đang đăng nhập**; phân công đúng ô ngày × ca; điều hướng tuần đúng (kể cả qua ranh giới tháng/năm); tuần trống hiển thị lưới rỗng, không lỗi.

**PASS / NO:** `PASS`
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-P4-05 · Lịch của shop
**Màn:** SCR-05C
**Các bước:**
1. Tạo trước ít nhất 2 nhân viên trực cùng một ô (ngày + ca) ở SCR-05.
2. Mở SCR-05C tuần chứa các phân công. Điều hướng tuần.
3. So sánh với dữ liệu `PhanCong` trong DB.
**Kết quả mong đợi:** lưới ngày × ca; mỗi ô liệt kê đủ các nhân viên trực, không thiếu, không trùng; tuần khác hiển thị đúng; khớp DB.

**PASS / NO:** `PASS`
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-P4-06 · Doanh thu chỉ tính hóa đơn đã thanh toán
**Màn:** SCR-12
**Tiền điều kiện:** có đủ 3 hóa đơn trong cùng khoảng ngày: một `DA_THANH_TOAN`, một `CHO_THANH_TOAN`, một `DA_HUY` (tạo ở SCR-10 hoặc dùng dữ liệu mẫu; ghi mã từng hóa đơn).
**Các bước:**
1. Mở SCR-12 với khoảng ngày chứa cả 3 hóa đơn.
2. So tổng doanh thu và danh sách hóa đơn trong kỳ với DB.
**Kết quả mong đợi:** chỉ hóa đơn `DA_THANH_TOAN` được cộng và liệt kê; hai hóa đơn còn lại không có mặt trong tổng.

**PASS / NO:** `PASS`
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-P4-07 · Doanh thu: lọc khoảng ngày
**Màn:** SCR-12
**Các bước:**
1. Mở không chọn khoảng ngày (tùy chọn).
2. Chọn khoảng ngày hẹp chỉ chứa 1 hóa đơn thanh toán; kiểm tra biên: ngày đầu và ngày cuối trùng ngày thanh toán.
3. Chọn khoảng ngày không có hóa đơn.
4. Chọn "từ ngày" lớn hơn "đến ngày".
**Kết quả mong đợi:** (1) tính tất cả; (2) đúng một hóa đơn, biên được bao gồm; (3) tổng bằng 0 và biểu đồ/bảng rỗng không lỗi; (4) có thông báo rõ hoặc xử lý hợp lý, không văng lỗi.

**PASS / NO:** `PASS`
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-P4-08 · Doanh thu: tách dịch vụ / phụ tùng và biểu đồ tròn
**Màn:** SCR-12
**Các bước:**
1. Với kỳ có hóa đơn đã thanh toán chứa cả dịch vụ và phụ tùng, đọc doanh thu dịch vụ, doanh thu phụ tùng, tổng.
2. Tính tay từ `ChiTietHoaDon` (`soLuong * donGia`, nhóm theo `loaiSanPham`) và so sánh.
3. Kiểm tra biểu đồ tròn: tỉ lệ, nhãn, kỳ chỉ có một loại.
**Kết quả mong đợi:** dịch vụ + phụ tùng = tổng; khớp phép tính tay; biểu đồ phản ánh đúng tỉ lệ, hiển thị được (thư viện `jfreechart-1.5.6.jar` nạp bình thường) kể cả khi một phần bằng 0.

**PASS / NO:** `PASS`
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-P4-09 · Đối chiếu doanh thu với hóa đơn có voucher (điểm mở đã biết)
**Màn:** SCR-12 ↔ SCR-10B
**Các bước:**
1. Chọn hóa đơn `DA_THANH_TOAN` có voucher. Ghi tổng tiền ở SCR-10/10B (đã giảm).
2. Chọn kỳ chỉ chứa hóa đơn đó trên SCR-12. Ghi doanh thu hiển thị.
3. Tính `tổng soLuong * donGia` từ `ChiTietHoaDon`.
**Kết quả mong đợi:** doanh thu SCR-12 bằng tổng gộp ở bước 3 (chưa trừ voucher) và **lớn hơn** tổng ở bước 1. Đây là điểm mở đã biết: ghi cả 3 con số vào mô tả. Chỉ đánh `NO` nếu SCR-12 không khớp công thức gộp.

**PASS / NO:** `PASS`
**Mô tả thực tế / lỗi chi tiết (ghi 3 con số):**
>

---

### E2E-P4-10 · Doanh thu: in và xuất Excel
**Màn:** SCR-12
**Các bước:**
1. Với kỳ có dữ liệu, bấm in (xem bản xem trước hoặc in ra PDF).
2. Bấm xuất Excel, lưu file, mở lại.
3. Thử xuất khi kỳ không có dữ liệu.
**Kết quả mong đợi:** bản in và file Excel mở được, tiếng Việt không vỡ phông, số liệu khớp màn hình; kỳ rỗng không văng lỗi (xuất file rỗng hoặc thông báo rõ).

**PASS / NO:** `PASS`
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-P4-11 · Tiêu thụ phụ tùng theo tháng
**Màn:** SCR-12B
**Các bước:**
1. Chọn tháng có cả phiếu nhập và hóa đơn. Đọc nhập / xuất / tồn / đơn giá / doanh thu từng phụ tùng.
2. Đối chiếu một phụ tùng cụ thể: lượng nhập từ `ChiTietPhieuNhapPhuTung`, lượng xuất từ `ChiTietHoaDon`, tồn từ `PhuTung`.
3. Kiểm tra bảng Top 20 (thứ tự, tối đa 20 dòng).
4. Chọn tháng không có dữ liệu; tháng đầu năm (tháng 1).
5. In và xuất Excel.
**Kết quả mong đợi:** số liệu khớp DB; Top 20 sắp đúng thứ tự và không quá 20 dòng; tháng trống không lỗi; in/Excel khớp màn hình, không lỗi phông.

**PASS / NO:** `PASS`
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-P4-12 · Tiêu thụ dịch vụ theo tháng
**Màn:** SCR-12C
**Các bước:**
1. Chọn tháng có hóa đơn dịch vụ. Đọc số lần sử dụng, tỉ trọng, doanh thu từng dịch vụ.
2. Đối chiếu một dịch vụ với `ChiTietHoaDon` (`loaiSanPham = DICH_VU`).
3. Kiểm tra tổng tỉ trọng (≈ 100%) và Top 20.
4. Chọn tháng trống; in; xuất Excel.
**Kết quả mong đợi:** số liệu khớp DB; tỉ trọng cộng lại xấp xỉ 100%; Top 20 đúng thứ tự; tháng trống không lỗi; in/Excel đúng.

**PASS / NO:** `PASS`
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-P4-13 · Luồng xuyên suốt: tiếp nhận → hóa đơn → thanh toán → báo cáo
**Màn:** SCR-03/04 → SCR-07 → SCR-10 → SCR-12/12B/12C
**Các bước:**
1. Ghi số liệu nền: doanh thu tháng hiện tại (SCR-12), tiêu thụ 1 phụ tùng P (SCR-12B), 1 dịch vụ D (SCR-12C), tồn P.
2. Tiếp nhận khách + xe mới `E2E-P4` ở SCR-04.
3. Lập phiếu nhập tăng tồn P thêm số lượng N ở SCR-07.
4. Lập hóa đơn cho phiếu mới với dịch vụ D (1) và phụ tùng P (2), không voucher ở SCR-10. Lưu, **chưa thanh toán**. Mở lại ba báo cáo.
5. Xác nhận thanh toán. Mở lại ba báo cáo.
6. Lập hóa đơn thứ hai (phiếu khác) cùng dịch vụ D rồi **hủy** nó. Mở lại báo cáo.
**Kết quả mong đợi:** (4) doanh thu chưa đổi vì hóa đơn chưa thanh toán; (5) doanh thu tăng đúng tổng dòng của hóa đơn, doanh thu dịch vụ/phụ tùng tăng đúng phần, tiêu thụ D và P tăng đúng, nhập P tăng N; (6) hóa đơn hủy không làm tăng doanh thu hay tiêu thụ, tồn phụ tùng hoàn lại. Ghi các con số trước/sau.

**PASS / NO:** `PASS`
**Mô tả thực tế / lỗi chi tiết (kèm bảng số liệu trước/sau):**
>

---

### E2E-P4-14 · Ngôn ngữ, nhãn enum và điều hướng
**Màn:** cả 6 màn P4, Trang chủ
**Các bước:**
1. Rà nhãn cột, nút, thông báo, ca làm việc (`CA_SANG`, `CA_CHIEU`, `CA_NGAY`) ở tiếng Việt.
2. Đổi sang tiếng Anh (nếu có công tắc) và rà lại.
3. Từ Trang chủ mở từng màn P4 qua menu; chuyển qua lại giữa SCR-05 ↔ 05B ↔ 05C.
4. Chạy 5–10 phút liên tục chuyển màn và đổi tuần/tháng.
**Kết quả mong đợi:** không lộ key i18n thô; enum hiện nhãn tiếng Việt; điều hướng đúng; không treo, không rò rỉ lỗi trên màn, không phát sinh ngoại lệ trong console.

**PASS / NO:** `PASS`
**Mô tả thực tế / lỗi chi tiết:**
>
Các enum về ca làm việc hiện tại chỉ có thể hiển thị tiếng việt ở bảng dữ liệu không thể đổi sang tiếng anh khi chuyển ngôn ngữ(form CRUD vẫn chuyển đổi anh-việt được)
---

## Tổng kết

- Số kịch bản: 14 · **PASS:** _13_ · **NO:** _1_
- Ghi chú thêm của người test:
>
