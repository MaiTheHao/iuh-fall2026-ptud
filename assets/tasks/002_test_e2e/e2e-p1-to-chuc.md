# E2E P1 — Tổ chức và dữ liệu nền

- **Người test:** Nguyễn Minh Quốc · **Nhánh:** `test/e2e-p1-to-chuc`
- **Ngày chạy:** 27/10/2026 · **Commit `develop`:** ________ · **DB:** sạch, migration `V0`–`V11` + dữ liệu mẫu
- **Màn phạm vi:** SCR-03 Khách hàng, SCR-03B Xe, SCR-06 Phụ tùng, SCR-08 Dịch vụ, SCR-09 Voucher, SCR-11 Nhân viên
- **Quy ước:** dữ liệu tự tạo có tiền tố `E2E-P1`. Điền `PASS / NO` ngay sau mỗi kịch bản; `NO` thì bắt buộc mô tả lỗi theo mẫu trong file hướng dẫn.

---

### E2E-P1-01 · Đăng nhập và mở các màn Tổ chức
**Màn:** SCR-01, SCR-02, 6 màn P1
**Tiền điều kiện:** ứng dụng khởi động được, có tài khoản quản lý.
**Các bước:**
1. Đăng nhập bằng tài khoản hợp lệ.
2. Từ Trang chủ, mở lần lượt menu Tổ chức: Khách hàng, Xe, Phụ tùng, Dịch vụ, Voucher, Nhân viên.
**Kết quả mong đợi:** cả 6 màn mở được, hiển thị dữ liệu mẫu từ DB (không rỗng, không văng lỗi), tiêu đề/nhãn là tiếng Việt.

**PASS / NO:** `NO`
**Mô tả thực tế / lỗi chi tiết:**
>
	 chưa có phân quyền
---

### E2E-P1-02 · Khách hàng: thêm, tìm, sửa, xóa
**Màn:** SCR-03
**Các bước:**
1. Thêm khách `E2E-P1 Khách A`, sdt `0900000001`. Lưu.
2. Tìm theo sdt `0900000001`, rồi theo một phần tên.
3. Sửa tên thành `E2E-P1 Khách A2`. Lưu.
4. Xóa khách này.
**Kết quả mong đợi:** (1) khách xuất hiện trong danh sách; (2) tìm ra đúng 1 bản ghi; (3) tên mới hiển thị, DB cập nhật; (4) khách biến mất khỏi danh sách và DB, có thông báo thành công.

**PASS / NO:** `PASS`
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-P1-03 · Khách hàng: trùng số điện thoại và dữ liệu sai
**Màn:** SCR-03
**Các bước:**
1. Thêm khách `E2E-P1 Khách B`, sdt `0900000002`. Lưu.
2. Thêm khách khác với **cùng** sdt `0900000002`.
3. Thêm khách với sdt rỗng; sdt chứa chữ (`09abc`); sdt quá ngắn; tên rỗng.
**Kết quả mong đợi:** (2) bị từ chối, thông báo rõ ràng (ràng buộc duy nhất `V8`), không tạo bản ghi mới, ứng dụng không văng lỗi; (3) mỗi trường hợp có thông báo lỗi riêng từ i18n, không lưu.

**PASS / NO:** `PASS`
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-P1-04 · Xe: thêm, tìm, lọc hãng/dòng xe, sửa, xóa
**Màn:** SCR-03B
**Các bước:**
1. Thêm xe biển số `E2E-P1-0001`, chọn hãng xe và dòng xe. Lưu.
2. Thêm xe thứ hai trùng biển số `E2E-P1-0001`.
3. Tìm theo biển số; lọc theo hãng xe, rồi chọn dòng xe.
4. Sửa xe (đổi dòng xe). Lưu.
5. Xóa xe.
**Kết quả mong đợi:** (1) lưu thành công; (2) bị từ chối với thông báo rõ; (3) danh sách hãng/dòng xe đúng, dòng xe thu hẹp theo hãng, kết quả lọc đúng; (4) thay đổi được lưu; (5) xe biến mất.

**PASS / NO:** `NO`
**Mô tả thực tế / lỗi chi tiết:**
>
	NO
	1.1 biển số phải "^[0-9]{2}[A-Z]{1,2}[0-9]{4,6}$" không như này E2E-P1-0001
	1.2 lỗi đời xe rỗng và km rỗng
	1.3 đổi mã xe thành 52E10001 và đời xe 2020 và km 5000
	PASS 2 -> 5
	

---

### E2E-P1-05 · Phụ tùng: CRUD và phân trang
**Màn:** SCR-06
**Các bước:**
1. Thêm phụ tùng `E2E-P1 Lọc dầu` với đơn giá, tồn kho hợp lệ. Lưu.
2. Tìm theo tên; sửa đơn giá; ghi lại tồn kho.
3. Nhập đơn giá âm, số lượng tồn âm, tên rỗng.
4. Chuyển trang kế tiếp / trước nếu danh sách nhiều hơn một trang.
5. Xóa phụ tùng vừa tạo.
**Kết quả mong đợi:** (1)(2) lưu và tìm đúng; (3) bị từ chối với thông báo rõ; (4) phân trang hiển thị đúng số trang, không trùng/mất dòng; (5) xóa thành công.

**PASS / NO:** `NO`
**Mô tả thực tế / lỗi chi tiết:**
>
	1.1 mâu thuẫn có sinh mã lại cho phép người dùng nhập mã 
	1.2 mâu thuẫn việc thêm số lượng mặc định sl > 0 mà ui không cho ng dùng nhập ( lỗi vì sl chỉ thay đổi khi thêm từ phiếu nhập )
	1.3 không nhập mã cho nhập sl 
	1.4 lỗi cho nhập tồn kho luôn set về không 0( vẫn báo khách hàng thêm tc)
	
	2.1 lỗi ghi lại tồn kho luôn set về không ( vẫn báo khách hàng lưu tc)0	
	3.1 lỗi những lỗi đơn giá âm, số lượng tồn âm, tên rỗng không hiện lên trên ui mà hiện trong console	
	
	4.1 lỗi không biết 1 trang có sl item bao nhiêu để test
	
	5 pass
---

### E2E-P1-06 · Dịch vụ: CRUD và kiểm tra dữ liệu
**Màn:** SCR-08
**Các bước:**
1. Thêm dịch vụ `E2E-P1 Thay dầu` với đơn giá hợp lệ.
2. Tìm, sửa đơn giá, lưu.
3. Thử đơn giá âm / chữ / rỗng; tên rỗng.
4. Xóa dịch vụ.
**Kết quả mong đợi:** thêm/sửa/xóa đúng; dữ liệu sai bị chặn với thông báo i18n; DB khớp giao diện.

**PASS / NO:** `NO`
**Mô tả thực tế / lỗi chi tiết:**
>
	3.1 âm / chữ / rỗng; tên rỗng không hiện lên trên ui mà hiện trong console	
	
	PASS 1 2 4
---

### E2E-P1-07 · Voucher: mức giảm, thời hạn, kích hoạt
**Màn:** SCR-09
**Các bước:**
1. Thêm voucher mức giảm hợp lệ (ví dụ 10%), thời hạn tương lai, kích hoạt bật.
2. Thêm voucher mức giảm ngoài khoảng cho phép (âm, trên 100%).
3. Thêm voucher thời hạn đã qua.
4. Sửa: tắt kích hoạt. Lưu.
5. Xóa voucher.
**Kết quả mong đợi:** (1) lưu đúng, mức giảm lưu dạng phân số 0–1 trong DB (10% → `0.1000`); (2) bị từ chối; (3) hành vi nhất quán với quy định (ghi rõ nếu cho lưu); (4) trạng thái đổi đúng; (5) xóa thành công.

**PASS / NO:** `PASS`
**Mô tả thực tế / lỗi chi tiết:**
>
	3. Thêm voucher thời hạn đã qua vẫn thêm đc
---

### E2E-P1-08 · Nhân viên: CRUD và số điện thoại
**Màn:** SCR-11
**Các bước:**
1. Thêm nhân viên `E2E-P1 NV` với đủ trường bắt buộc, có sdt.
2. Tìm theo tên và theo vai trò.
3. Sửa sdt và vai trò. Lưu.
4. Thêm nhân viên không có sdt.
5. Xóa nhân viên.
**Kết quả mong đợi:** (1)(2)(3) đúng; (4) hành vi rõ ràng và nhất quán (lưu ý `soDienThoai` là `NULL` ở migration `V1` nhưng `NOT NULL` ở `init.sql`: ghi rõ ứng dụng cho hay chặn, và có văng lỗi hay không); (5) xóa thành công.

**PASS / NO:** `PASS`
**Mô tả thực tế / lỗi chi tiết:**
>
	4. văn lỗi 
---

### E2E-P1-09 · Bộ lọc, xóa lọc và phân trang trên cả 6 màn
**Màn:** SCR-03, 03B, 06, 08, 09, 11
**Các bước:**
1. Trên mỗi màn, nhập điều kiện lọc cho ra 0 kết quả.
2. Nhập điều kiện lọc cho ra vài kết quả.
3. Nhấn xóa lọc.
4. Chuyển trang, đổi điều kiện lọc khi đang ở trang 2.
**Kết quả mong đợi:** 0 kết quả hiển thị bảng rỗng không lỗi; xóa lọc trả lại toàn bộ danh sách và về trang 1; đổi lọc khi ở trang 2 không hiện trang trống sai.

**PASS / NO:** `PASS`
**Mô tả thực tế / lỗi chi tiết:**
>
	4 không biết sl item 1 trang là bao nhiêu để test
---

### E2E-P1-10 · Xóa bản ghi đang được tham chiếu
**Màn:** SCR-03, 03B, 06, 08, 09, 11
**Tiền điều kiện:** dùng dữ liệu mẫu đang có liên kết (khách có phiếu tiếp nhận, phụ tùng/dịch vụ có trong hóa đơn, voucher đã áp, nhân viên có phân công).
**Các bước:** thử xóa từng loại bản ghi đang được tham chiếu.
**Kết quả mong đợi:** ứng dụng báo lỗi dễ hiểu bằng tiếng Việt (hoặc cascade đúng thiết kế); không văng stack trace, không treo, không để dữ liệu mồ côi hoặc xóa dở.

**PASS / NO:** `PASS`
**Mô tả thực tế / lỗi chi tiết:**
>
	có hiện thông báo không tìm thấy phụ tùng trong hóa đơn 
	có hiện thông báo khi xe khác có liên kết
---

### E2E-P1-11 · Ngôn ngữ và nhãn enum
**Màn:** cả 6 màn
**Các bước:**
1. Chạy ở tiếng Việt, rà nhãn cột, nút, thông báo, enum.
2. Đổi sang tiếng Anh (nếu có công tắc), rà lại.
**Kết quả mong đợi:** không có key i18n thô (dạng `app.xxx.yyy`) hiển thị; enum hiện nhãn tiếng Việt; bản tiếng Anh có thể chưa dịch hết nhưng không lộ key thiếu hay lỗi.

**PASS / NO:** `PASS`
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-P1-12 · Hợp đồng liên lane: dữ liệu P1 dùng được ở lane khác
**Màn:** SCR-04, SCR-07, SCR-10, SCR-05
**Tiền điều kiện:** dữ liệu `E2E-P1` đã tạo, **chưa xóa** (tạo lại nếu đã xóa ở kịch bản trước).
**Các bước:**
1. Tạo khách + xe mới, mở SCR-04 và tra bằng sdt/biển số.
2. Tạo phụ tùng mới, mở SCR-07 và chọn phụ tùng đó.
3. Tạo dịch vụ + voucher kích hoạt, mở SCR-10 và chọn.
4. Tạo nhân viên mới, mở SCR-05 và chọn nhân viên.
**Kết quả mong đợi:** mọi dữ liệu vừa tạo xuất hiện ngay ở màn lane khác, không cần khởi động lại.

**PASS / NO:** `NO`
**Mô tả thực tế / lỗi chi tiết:**
>
	Tạo nhân viên mới, mở SCR-05 và chọn nhân viên. không có chọn nhân viên
	pass 123
---

## Tổng kết

- Số kịch bản: 12 · **PASS:** 7 · **NO:** 5
- Ghi chú thêm của người test:
>
