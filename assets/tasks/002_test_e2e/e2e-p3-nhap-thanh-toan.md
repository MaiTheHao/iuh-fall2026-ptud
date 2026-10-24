# E2E P3 — Nhập phụ tùng và thanh toán

- **Người test:** Nguyễn Trần Trọng Khang · **Nhánh:** `test/e2e-p3-nhap-thanh-toan`
- **Ngày chạy:** ____/____/2026 · **Commit `develop`:** ________ · **DB:** sạch, migration `V0`–`V11` + dữ liệu mẫu
- **Màn phạm vi:** SCR-07, 07B, SCR-10, 10B, SCR-15
- **Quy ước:** dữ liệu tự tạo có ghi chú/tên tiền tố `E2E-P3`. Điền `PASS / NO` ngay sau mỗi kịch bản; `NO` thì bắt buộc mô tả lỗi theo mẫu trong file hướng dẫn.
- **Chuẩn bị:** chọn sẵn 2 phụ tùng (ghi lại **tồn kho ban đầu** và đơn giá), 2 dịch vụ, 1 voucher còn hiệu lực + đang kích hoạt, 1 voucher hết hạn hoặc tắt kích hoạt, và ít nhất 4 phiếu tiếp nhận chưa có hóa đơn (tạo ở SCR-04 hoặc dùng dữ liệu mẫu).

| Dữ liệu dùng chung | Giá trị (điền trước khi chạy) |
|---|---|
| Phụ tùng A (mã / đơn giá / tồn đầu) | |
| Phụ tùng B (mã / đơn giá / tồn đầu) | |
| Dịch vụ X, Y (mã / đơn giá) | |
| Voucher hợp lệ (mã / mức giảm) | |
| Voucher không hợp lệ (mã / lý do) | |

---

### E2E-P3-01 · Lập phiếu nhập nhiều dòng
**Màn:** SCR-07
**Các bước:**
1. Mở SCR-07. Thêm dòng 1: phụ tùng A, số lượng 5, đơn giá nhập 100.000.
2. Thêm dòng 2: phụ tùng B, số lượng 3, đơn giá nhập 200.000.
3. Kiểm tra thành tiền từng dòng và tổng chi phí trên màn.
4. Lưu. Kiểm tra DB (`PhieuNhapPhuTung`, `ChiTietPhieuNhapPhuTung`) và tồn kho A, B.
**Kết quả mong đợi:** thành tiền 500.000 và 600.000; tổng 1.100.000; tồn A tăng 5, tồn B tăng 3 so với tồn đầu; đầu phiếu và chi tiết ghi đủ, nhân viên lập là người đang đăng nhập.

**PASS / NO:** `__________`
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-P3-02 · Phiếu nhập: dữ liệu sai
**Màn:** SCR-07
**Các bước:** lưu phiếu không có dòng nào; số lượng `0`, `-1`, chữ; đơn giá `-1`, chữ, rỗng; thêm cùng một phụ tùng hai lần; xóa một dòng khỏi phiếu rồi kiểm tra lại tổng.
**Kết quả mong đợi:** mỗi trường hợp có thông báo rõ bằng tiếng Việt, không lưu, **tồn kho không đổi**; dòng trùng được gộp hoặc bị từ chối theo thiết kế (ghi lại hành vi); tổng cập nhật đúng khi xóa dòng.

**PASS / NO:** `__________`
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-P3-03 · Phiếu nhập: lỗi giữa chừng không tăng tồn dở
**Màn:** SCR-07
**Các bước:** tạo tình huống lưu thất bại giữa chừng (ví dụ phiếu có 2 dòng, dòng sau sai dữ liệu hoặc ngắt kết nối DB tạm thời nếu làm được). Lưu. Kiểm tra DB và tồn kho.
**Kết quả mong đợi:** không có đầu phiếu mồ côi, không có tồn kho tăng một phần; đầu phiếu, chi tiết và tăng tồn thành công hoặc thất bại cùng nhau. Nếu không tạo được tình huống, ghi lý do và đánh `NO` kèm ghi chú "không thực hiện được".

**PASS / NO:** `__________`
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-P3-04 · Quản lý phiếu nhập
**Màn:** SCR-07B
**Các bước:**
1. Mở SCR-07B, kiểm tra phiếu vừa lập.
2. Lọc theo khoảng ngày (gồm biên ngày), xóa lọc, phân trang.
3. Dùng nút mở màn lập phiếu.
4. Xóa phiếu `E2E-P3` rồi kiểm tra tồn kho.
**Kết quả mong đợi:** phiếu hiển thị đúng ngày, tổng chi phí, người lập; lọc khoảng ngày đúng (kể cả ngày đầu/cuối); mở được SCR-07; xóa phiếu cho kết quả rõ ràng (ghi lại tồn kho có hoàn lại hay không và có đúng thiết kế không).

**PASS / NO:** `__________`
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-P3-05 · Lập hóa đơn: dịch vụ + phụ tùng, gộp dòng, tổng tiền
**Màn:** SCR-10
**Các bước:**
1. Chọn phiếu tiếp nhận 1 (chưa có hóa đơn).
2. Thêm dịch vụ X (số lượng 1), phụ tùng A (số lượng 2).
3. Thêm lại phụ tùng A (số lượng 1).
4. Không áp voucher. Lưu.
5. Kiểm tra DB (`HoaDon`, `ChiTietHoaDon`).
**Kết quả mong đợi:** dòng A gộp thành số lượng 3; thành tiền = số lượng × đơn giá; tổng = tổng các dòng; mã hóa đơn bắt đầu bằng `HD`; trạng thái ban đầu `CHO_THANH_TOAN`; `ChiTietHoaDon` có `loaiSanPham` `DICH_VU`/`PHU_TUNG` đúng.

**PASS / NO:** `__________`
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-P3-06 · Trừ tồn kho khi lập hóa đơn
**Màn:** SCR-10
**Các bước:**
1. Ghi tồn phụ tùng A trước khi lập (sau P3-05).
2. Lập hóa đơn cho phiếu 2 có phụ tùng A số lượng 2. Lưu. Kiểm tra tồn.
3. Lập hóa đơn cho phiếu 3 với số lượng phụ tùng **lớn hơn tồn hiện có**.
**Kết quả mong đợi:** (2) tồn giảm đúng 2, hóa đơn và trừ tồn cùng thành công; (3) bị từ chối với thông báo rõ, **không** tạo hóa đơn, tồn không âm và không đổi.

**PASS / NO:** `__________`
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-P3-07 · Áp voucher
**Màn:** SCR-10
**Các bước:**
1. Lập hóa đơn cho phiếu 3 (số lượng hợp lệ) có áp voucher hợp lệ. Ghi tổng trước và sau giảm.
2. Thử chọn voucher hết hạn / voucher tắt kích hoạt cho hóa đơn khác.
3. Kiểm tra DB: `maVoucher`, `mucGiamApDung`.
**Kết quả mong đợi:** tổng sau giảm = tổng các dòng × (1 − mức giảm), làm tròn hợp lý; `mucGiamApDung` được chốt vào hóa đơn; voucher không hợp lệ không được áp (ẩn khỏi danh sách hoặc báo lỗi rõ).

**PASS / NO:** `__________`
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-P3-08 · Không tạo hóa đơn thứ hai cho cùng một phiếu
**Màn:** SCR-10
**Các bước:**
1. Chọn lại phiếu đã có hóa đơn (phiếu 1) và lập hóa đơn mới.
2. Kiểm tra DB đếm hóa đơn theo `maPhieuTiepNhan`.
**Kết quả mong đợi:** không tạo bản ghi thứ hai (ràng buộc `UQ_HoaDon_maPhieuTiepNhan`); ứng dụng trả/mở hóa đơn đã có hoặc báo rõ ràng, không văng lỗi SQL 2627/2601 ra giao diện; tồn kho không bị trừ lần nữa.

**PASS / NO:** `__________`
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-P3-09 · Xác nhận thanh toán
**Màn:** SCR-10 / SCR-10B
**Các bước:**
1. Mở hóa đơn `CHO_THANH_TOAN` (phiếu 1). Xác nhận thanh toán.
2. Kiểm tra DB: `trangThaiHoaDon`, `thoiGianThanhToan`.
3. Mở lại hóa đơn đã thanh toán, thử sửa/thanh toán lần nữa.
**Kết quả mong đợi:** chuyển `DA_THANH_TOAN`, ghi `thoiGianThanhToan` ngay thời điểm xác nhận; SCR-10B hiện nhãn tiếng Việt; hóa đơn đã thanh toán không thanh toán lại được và không sửa được tùy tiện (ghi lại hành vi thực tế).

**PASS / NO:** `__________`
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-P3-10 · Hủy hóa đơn chờ thanh toán
**Màn:** SCR-10
**Các bước:**
1. Mở hóa đơn `CHO_THANH_TOAN` có phụ tùng (phiếu 2). Ghi tồn phụ tùng hiện tại.
2. Hủy không nhập lý do.
3. Hủy kèm lý do `E2E-P3 hủy thử`.
4. Kiểm tra tồn, DB, trạng thái form.
**Kết quả mong đợi:** (2) bị chặn, yêu cầu lý do; (3) chuyển `DA_HUY`, lưu `lyDoHuy`, **hoàn tồn phụ tùng đúng số lượng**; form giữ nguyên dữ liệu và khóa lại, không sửa tiếp được.

**PASS / NO:** `__________`
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-P3-11 · Quản lý hóa đơn: lọc, mở, xóa
**Màn:** SCR-10B
**Các bước:**
1. Lọc theo trạng thái / khoảng ngày / mã; xóa lọc; phân trang.
2. Mở một hóa đơn từ danh sách.
3. Xóa hóa đơn chưa hủy (phiếu 3, có phụ tùng): ghi tồn trước/sau.
4. Xóa hóa đơn đã thanh toán (phiếu 1).
**Kết quả mong đợi:** lọc/phân trang đúng, nhãn tiếng Việt; mở đúng hóa đơn; (3) hóa đơn bị xóa khỏi danh sách và DB, tồn được hoàn; (4) có **cảnh báo riêng** trước khi xóa, và hành vi sau xác nhận đúng thiết kế; không văng lỗi.

**PASS / NO:** `__________`
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-P3-12 · Đổi mật khẩu
**Màn:** SCR-15
**Tiền điều kiện:** dùng **tài khoản thử riêng**, không dùng tài khoản của người khác; ghi lại mật khẩu cũ để đổi lại.
**Các bước:**
1. Nhập sai mật khẩu hiện tại.
2. Nhập đúng hiện tại, mật khẩu mới và xác nhận không khớp.
3. Mật khẩu mới yếu: dưới 8 ký tự; thiếu chữ; thiếu số; thiếu ký tự đặc biệt.
4. Mật khẩu mới hợp lệ, xác nhận khớp. Lưu.
5. Kiểm tra các trường sau khi thành công; đăng xuất và đăng nhập bằng mật khẩu cũ rồi mật khẩu mới.
6. Đổi lại mật khẩu ban đầu.
**Kết quả mong đợi:** (1)(2)(3) mỗi trường hợp báo lỗi riêng, không đổi mật khẩu; (4) thành công; (5) 3 trường được xóa trắng, mật khẩu cũ không còn đăng nhập được, mật khẩu mới đăng nhập được; mật khẩu không hiển thị dạng thô trong thông báo.

**PASS / NO:** `__________`
**Mô tả thực tế / lỗi chi tiết:**
>

---

## Tổng kết

- Số kịch bản: 12 · **PASS:** ____ · **NO:** ____
- Ghi chú thêm của người test:
>
