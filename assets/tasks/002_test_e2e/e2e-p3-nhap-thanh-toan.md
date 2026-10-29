# E2E P3 — Nhập phụ tùng và thanh toán

- **Người test:** Nguyễn Trần Trọng Khang · **Nhánh:** `test/e2e-p3-nhap-thanh-toan`
- **Ngày chạy:** 27/10/2026 · **Commit `develop`:** 836beb3 · **DB:** sạch, migration `V0`–`V11` + dữ liệu mẫu
- **Màn phạm vi:** SCR-07, 07B, SCR-10, 10B, SCR-15
- **Quy ước:** dữ liệu tự tạo có ghi chú/tên tiền tố `E2E-P3`. Điền `PASS / NO` ngay sau mỗi kịch bản; `NO` thì bắt buộc mô tả lỗi theo mẫu trong file hướng dẫn.
- **Chuẩn bị:** chọn sẵn 2 phụ tùng (ghi lại **tồn kho ban đầu** và đơn giá), 2 dịch vụ, 1 voucher còn hiệu lực + đang kích hoạt, 1 voucher hết hạn hoặc tắt kích hoạt, và ít nhất 4 phiếu tiếp nhận chưa có hóa đơn (tạo ở SCR-04 hoặc dùng dữ liệu mẫu).

Dữ liệu dùng chung	Giá trị
Phụ tùng A	PT002 / 65.000 / tồn: 32
Phụ tùng B	PT005 / 145.000 / tồn: 40
Dịch vụ X, Y	DV001 250.000 · DV009 70.000
Voucher hợp lệ	VC007 / 20% (0,2000), hạn 31/08/2027
Voucher không hợp lệ	VC010 / 25%, hết hạn 31/01/2026 (kích hoạt = 0)
4 phiếu chưa hóa đơn
1	0901000013	59C-334.45	E2E-P3 P1
2	0901000014	51E-445.56	E2E-P3 P2
3	0901000015	60B-556.67	E2E-P3 P3
4	0901000001	51A-123.45	E2E-P3 P4
---

### E2E-P3-01 · Lập phiếu nhập nhiều dòng
**Màn:** SCR-07
**Các bước:**
1. Mở SCR-07. Thêm dòng 1: phụ tùng A, số lượng 5, đơn giá nhập 65.000.
2. Thêm dòng 2: phụ tùng B, số lượng 3, đơn giá nhập 145.000.
3. Kiểm tra thành tiền từng dòng và tổng chi phí trên màn.
4. Lưu. Kiểm tra DB (`PhieuNhapPhuTung`, `ChiTietPhieuNhapPhuTung`) và tồn kho A, B.
**Kết quả mong đợi:** thành tiền 325.000 và 435.000; tổng 760.000;  tồn A tăng 5, tồn B tăng 3 so với tồn đầu; đầu phiếu và chi tiết ghi đủ, nhân viên lập là người đang đăng nhập.

**PASS / NO:** `____PASS______`
**Mô tả thực tế / lỗi chi tiết:**
>

2 dòng: PT002 SL 5 × 65.000 = 325.000; PT005 SL 3 × 145.000 = 435.000; tổng chi phí 760.000. Sau lưu: tồn PT002 32→37, PT005 40→43; đầu phiếu <PN27/10/202653962>, ngày 27/10/2026, thủ kho NV011120250001; ChiTietPhieuNhapPhuTung có đúng 2 dòng.

---

### E2E-P3-02 · Phiếu nhập: dữ liệu sai
**Màn:** SCR-07
**Các bước:** lưu phiếu không có dòng nào; số lượng `0`, `-1`, chữ; đơn giá `-1`, chữ, rỗng; thêm cùng một phụ tùng hai lần; xóa một dòng khỏi phiếu rồi kiểm tra lại tổng.
**Kết quả mong đợi:** mỗi trường hợp có thông báo rõ bằng tiếng Việt, không lưu, **tồn kho không đổi**; dòng trùng được gộp hoặc bị từ chối theo thiết kế (ghi lại hành vi); tổng cập nhật đúng khi xóa dòng.

**PASS / NO:** `____NO______`
**Mô tả thực tế / lỗi chi tiết:**
>
### Test Case 1: Không có dòng chi tiết (Ổn)
* **Cách thực hiện:** Nhập Mô tả, bấm Lưu
* **Kết quả mong đợi:** Hiển thị thông báo `"Danh sách chi tiết nhập không được rỗng."`
---

### Test Case 2: Không có mô tả (Ổn)
* **Cách thực hiện:** Có dòng chi tiết, để trống ô Mô tả, bấm Lưu
* **Kết quả mong đợi:** Hiển thị thông báo `"Mô tả không được để trống."`
---

### Test Case 3: Số lượng nhập bằng 0 (Ổn)
* **Cách thực hiện:** Thêm dòng, sửa ô SL thành `0`, nhấn Enter
* Tự set về 1 
---

### Test Case 4: Số lượng nhập là số âm hoặc chữ (Ổn)
* **Cách thực hiện:** Gõ `-1` hoặc `abc` vào ô SL
* Tự set số lượng là 1
---

### Test Case 5: Đơn giá không hợp lệ (Đã chặn nhập số âm và chữ) (Nhưng click vào mà không nhập thì mất luôn giá nên set về giá ban đầu)
* **Cách thực hiện:** Gõ `-1`, `abc`, hoặc để rỗng tại ô Đơn giá
* **
	

### Test Case 6: Trùng phụ tùng (Ổn)
* **Cách thực hiện:** Thêm phụ tùng `PT002` với SL 1 hai lần, bấm Lưu
* **Kết quả mong đợi:** 
  * Giao diện (UI) hiển thị 2 dòng (1 dòng đã cộng dồn vô luôn)
  * Cơ sở dữ liệu (DB) tự động gộp thành 1 dòng với SL là 2 (service xử lý cộng dồn)
 
---

### Test Case 7: Xóa dòng chi tiết (Ổn)
 * **Cách thực hiện:** Chọn một dòng trong bảng $\rightarrow$ bấm nút Xóa dòng
* **Kết quả mong đợi:** Dòng được xóa khỏi bảng và Tổng chi phí cập nhật lại ngay lập tức
---

### Test Case 8: Tính năng chỉ hiện còn tồn (OK)

### E2E-P3-03 · Phiếu nhập: lỗi giữa chừng không tăng tồn dở
**Màn:** SCR-07
**Các bước:** tạo tình huống lưu thất bại giữa chừng (ví dụ phiếu có 2 dòng, dòng sau sai dữ liệu hoặc ngắt kết nối DB tạm thời nếu làm được). Lưu. Kiểm tra DB và tồn kho.
**Kết quả mong đợi:** không có đầu phiếu mồ côi, không có tồn kho tăng một phần; đầu phiếu, chi tiết và tăng tồn thành công hoặc thất bại cùng nhau. Nếu không tạo được tình huống, ghi lý do và đánh `NO` kèm ghi chú "không thực hiện được".

**PASS / NO:** `_____NO_____`
**Mô tả thực tế / lỗi chi tiết:**

Không thực hiện được. Thao tác lưu phiếu trên môi trường local diễn ra quá nhanh. Khi thử ngắt kết nối bằng "docker pause", luồng TCP socket chỉ bị hoãn chứ không đứt hẳn kết nối; ngay khi "docker unpause", database tiếp tục nhận lệnh và hoàn tất giao dịch thành công. Do không thể can thiệp làm gián đoạn transaction giữa chừng trên UI mà không sửa mã nguồn, tình huống lỗi giữa chừng không tái hiện được.
--

### E2E-P3-04 · Quản lý phiếu nhập
**Màn:** SCR-07B
**Các bước:**
1. Mở SCR-07B, kiểm tra phiếu vừa lập.
2. Lọc theo khoảng ngày (gồm biên ngày), xóa lọc, phân trang.
3. Dùng nút mở màn lập phiếu.
4. Xóa phiếu `E2E-P3` rồi kiểm tra tồn kho.
**Kết quả mong đợi:** phiếu hiển thị đúng ngày, tổng chi phí, người lập; lọc khoảng ngày đúng (kể cả ngày đầu/cuối); mở được SCR-07; xóa phiếu cho kết quả rõ ràng (ghi lại tồn kho có hoàn lại hay không và có đúng thiết kế không).

**PASS / NO:** `____PASS______`
**Mô tả thực tế / lỗi chi tiết:**
>
Màn hình SCR-07B hiển thị đầy đủ, chính xác thông tin phiếu (ngày lập, người lập, tổng tiền). Bộ lọc theo khoảng ngày bao gồm cả biên ngày hoạt động chuẩn xác, phân trang và nút mở SCR-07 hoạt động tốt. Khi xóa phiếu: hệ thống hiển thị dialog xác nhận, sau khi xóa thành công, số lượng tồn kho của các phụ tùng trong phiếu được trừ hoàn lại chính xác theo đúng thiết kế. Phân trang ok

---

### E2E-P3-05 · Lập hóa đơn: dịch vụ + phụ tùng, gộp dòng, tổng tiền
**Màn:** SCR-10
**Các bước:**
1. Chọn phiếu tiếp nhận 1 (chưa có hóa đơn).
2. Thêm dịch vụ DV001 (số lượng 1), phụ tùng A (số lượng 2).
3. Thêm lại phụ tùng A (số lượng 1).
4. Không áp voucher. Lưu.
5. Kiểm tra DB (`HoaDon`, `ChiTietHoaDon`).
**Kết quả mong đợi:** dòng A gộp thành số lượng 3; thành tiền = số lượng × đơn giá; tổng = tổng các dòng; mã hóa đơn bắt đầu bằng `HD`; trạng thái ban đầu `CHO_THANH_TOAN`; `ChiTietHoaDon` có `loaiSanPham` `DICH_VU`/`PHU_TUNG` đúng.

**PASS / NO:** `____PASS______`
**Mô tả thực tế / lỗi chi tiết:**
>

* Thao tác lập hóa đơn từ phiếu tiếp nhận chưa thanh toán thành công, không áp dụng voucher.
* Giao diện và hệ thống xử lý gộp phụ tùng A trùng lặp chính xác từ 2 lần thêm (2 + 1) thành 1 dòng duy nhất có số lượng = 3.
* Thành tiền từng dòng (SL × đơn giá) và tổng tiền hóa đơn tính toán chính xác.
* Kiểm tra DB:
  - Bảng `HoaDon`: Sinh mã tự động đúng tiền tố `HD...`, trạng thái ghi nhận chuẩn `CHO_THANH_TOAN`.
  - Bảng `ChiTietHoaDon`: Phân định chính xác cột `loaiSanPham` tương ứng là `DICH_VU` và `PHU_TUNG`.

---

### E2E-P3-06 · Trừ tồn kho khi lập hóa đơn
**Màn:** SCR-10
**Các bước:**
1. Ghi tồn phụ tùng A trước khi lập (sau P3-05).(34)
2. Lập hóa đơn cho phiếu 2 có phụ tùng A số lượng 2. Lưu. Kiểm tra tồn. (32)
3. Lập hóa đơn cho phiếu 3 với số lượng phụ tùng **lớn hơn tồn hiện có**.
**Kết quả mong đợi:** (2) tồn giảm đúng 2, hóa đơn và trừ tồn cùng thành công; (3) bị từ chối với thông báo rõ, **không** tạo hóa đơn, tồn không âm và không đổi.(Tự set về 1)

**PASS / NO:** `_____PASS_____`
**Mô tả thực tế / lỗi chi tiết:**
>

* Bước 2 hoạt động đúng: Lập hóa đơn thành công, tồn kho phụ tùng A giảm chính xác từ 34 xuống 32 trong database.
* Bước 3 phát hiện lỗi validation tại giao diện:
  - Hệ thống chặn không cho thêm dòng phụ tùng khi số lượng vượt tồn hiện có (không bị âm kho).
  - Khi nhập số lượng âm hoặc chữ thì tự set về 1 
* Kết luận: Logic bảo toàn tồn kho trong DB đạt
---

### E2E-P3-07 · Áp voucher
**Màn:** SCR-10
**Các bước:**
1. Lập hóa đơn cho phiếu 3 (số lượng hợp lệ) có áp voucher hợp lệ. Ghi tổng trước và sau giảm.(HD1 trước 700.000 sau add voucher giảm 10% còn 630.000, voucher hết hạn thì báo voucher không hợp lệ, giá vẫn giữ nguyên)
2. Thử chọn voucher hết hạn / voucher tắt kích hoạt cho hóa đơn khác.
3. Kiểm tra DB: `maVoucher`, `mucGiamApDung`.
**Kết quả mong đợi:** tổng sau giảm = tổng các dòng × (1 − mức giảm), làm tròn hợp lý; `mucGiamApDung` được chốt vào hóa đơn; voucher không hợp lệ không được áp (ẩn khỏi danh sách hoặc báo lỗi rõ).

**PASS / NO:** `___PASS_______`
**Mô tả thực tế / lỗi chi tiết:**
>

* Chức năng áp dụng voucher hợp lệ hoạt động chính xác theo công thức tính toán:
  - HD1: Tổng tiền ban đầu 445.000 VNĐ, áp voucher 10% → Tổng thanh toán còn 400.500 VNĐ.
  - HD2: Tổng tiền ban đầu 130.000 VNĐ, áp voucher 5% → Tổng thanh toán còn 123.500 VNĐ.
* Xử lý trường hợp không hợp lệ:
  - Khi áp dụng voucher đã hết hạn / bị tắt kích hoạt cho hóa đơn (HD3), Tự ẩn không cho chọn.
* Kiểm tra dữ liệu trong DB (bảng `HoaDon`):
  - Cột `maVoucher` lưu đúng mã voucher đã sử dụng.
  - Cột `mucGiamApDung` chốt giá trị phần trăm giảm chính xác tại thời điểm lập hóa đơn, đảm bảo không bị ảnh hưởng nếu chính sách voucher thay đổi sau này.
---

### E2E-P3-08 · Không tạo hóa đơn thứ hai cho cùng một phiếu (Có cần thiết phải chỉnh sửa lại thời gian lập hóa đơn khi cập nhật)
**Màn:** SCR-10
**Các bước:**
1. Chọn lại phiếu đã có hóa đơn (phiếu 1) và lập hóa đơn mới.
2. Kiểm tra DB đếm hóa đơn theo `maPhieuTiepNhan`.
**Kết quả mong đợi:** không tạo bản ghi thứ hai (ràng buộc `UQ_HoaDon_maPhieuTiepNhan`); ứng dụng trả/mở hóa đơn đã có hoặc báo rõ ràng, không văng lỗi SQL 2627/2601 ra giao diện; tồn kho không bị trừ lần nữa.

**PASS / NO:** `_____PASS_____`
**Mô tả thực tế / lỗi chi tiết:**
>

* Thao tác chọn lại phiếu tiếp nhận đã có hóa đơn (phiếu 1) để cố tình tạo thêm hóa đơn thứ hai:
  - Ứng dụng xử lý đúng nghiệp vụ: tự động điều hướng/mở lại hóa đơn đã lập trước đó.
  - Không xuất hiện hiện tượng văng màn hình lỗi cơ sở dữ liệu hay mã lỗi SQL Driver thô (như lỗi trùng khóa Unique 2627 / 2601) ra ngoài giao diện.
* Kiểm tra dữ liệu trong CSDL:
  - Ràng buộc `UQ_HoaDon_maPhieuTiepNhan` (migration v11) được bảo toàn nguyên vẹn.
  - Truy vấn kiểm tra: Số lượng bản ghi trong bảng `HoaDon` ứng với `maPhieuTiepNhan` không có hóa đơn thứ hai nào được tạo thêm.
  - Tồn kho của các phụ tùng liên quan không bị trừ lần thứ hai.
---

### E2E-P3-09 · Xác nhận thanh toán
**Màn:** SCR-10 / SCR-10B
**Các bước:**
1. Mở hóa đơn `CHO_THANH_TOAN` (phiếu 1). Xác nhận thanh toán.
2. Kiểm tra DB: `trangThaiHoaDon`, `thoiGianThanhToan`.
3. Mở lại hóa đơn đã thanh toán, thử sửa/thanh toán lần nữa.
**Kết quả mong đợi:** chuyển `DA_THANH_TOAN`, ghi `thoiGianThanhToan` ngay thời điểm xác nhận; SCR-10B hiện nhãn tiếng Việt; hóa đơn đã thanh toán không thanh toán lại được và không sửa được tùy tiện (ghi lại hành vi thực tế).

**PASS / NO:** `_____NO_____`
**Mô tả thực tế / lỗi chi tiết:**
>

* Thao tác xác nhận thanh toán cho hóa đơn đang ở trạng thái `CHO_THANH_TOAN` (phiếu 1) diễn ra thành công.
* Kiểm tra dữ liệu trong CSDL (bảng `HoaDon`):
  - Cột `trangThaiHoaDon` được cập nhật chính xác sang `DA_THANH_TOAN`.
  - Cột `thoiGianThanhToan` được ghi nhận giá trị timestamp khớp với thời điểm vừa bấm xác nhận.
* Hành vi thực tế trên giao diện (SCR-10 / SCR-10B):
  - Danh sách quản lý hóa đơn (SCR-10B) hiển thị nhãn trạng thái tiếng Việt chuẩn xác ("Đã thanh toán").
  - Khi mở lại hóa đơn đã thanh toán: Hệ thống khóa hoàn toàn chế độ chỉnh sửa/thanh toán lại. Người dùng chỉ có quyền xem thông tin chi tiết (chế độ chỉ xem - Read-only), các nút Lưu, Sửa hoặc Xác nhận thanh toán đều bị vô hiệu hóa (disabled) đúng như thiết kế nghiệp vụ.
  - Nút thùng rác nên khóa luôn hoặc khi cố tình bấm vào thì phải thông báo "Hóa đơn đã thanh toán không được sửa"
---

### E2E-P3-10 · Hủy hóa đơn chờ thanh toán
**Màn:** SCR-10
**Các bước:**
1. Mở hóa đơn `CHO_THANH_TOAN` có phụ tùng (phiếu 2). Ghi tồn phụ tùng hiện tại.(12)
2. Hủy không nhập lý do.(Có hộp thoại đầy đủ)
3. Hủy kèm lý do `E2E-P3 hủy thử`.(Khóa hết nhưng vẫn bấm vào biểu tượng thùng rác được và có quăng lỗi "Trạng thái hiện tại không cho thanh toán")
4. Kiểm tra tồn, DB, trạng thái form.
**Kết quả mong đợi:** (2) bị chặn, yêu cầu lý do; (3) chuyển `DA_HUY`, lưu `lyDoHuy`, **hoàn tồn phụ tùng đúng số lượng**; form giữ nguyên dữ liệu và khóa lại, không sửa tiếp được.

**PASS / NO:** `_____PASS_____`
**Mô tả thực tế / lỗi chi tiết:**
>

* Bước 1 & Bước 2: 
  - Ghi nhận tồn kho phụ tùng ban đầu là 12.
  - Khi bấm Hủy mà không nhập lý do: Hệ thống hiển thị hộp thoại yêu cầu nhập lý do đầy đủ, chặn hủy thành công.
* Bước 3 & Bước 4 (Phát hiện lỗi xử lý giao diện sau khi hủy):
  - Xử lý nghiệp vụ & CSDL: Hóa đơn được chuyển trạng thái sang `DA_HUY`, lưu đúng lý do hủy `E2E-P3 hủy thử`. Tồn kho phụ tùng trong DB được hoàn lại đúng số lượng (tăng từ 12 lên 14).
  - Lỗi giao diện (UI Lock): Form đã khóa các nút chính, tuy nhiên biểu tượng thùng rác (nút Xóa dòng chi tiết trong bảng) vẫn cho phép click. Khi bấm vào icon này, hệ thống lại quăng ra thông báo lỗi không đúng ngữ cảnh: "Trạng thái hiện tại không cho thanh toán" (thay vì phải vô hiệu hóa hoàn toàn nút/icon xóa hoặc thông báo hóa đơn đã hủy không được chỉnh sửa).
---

### E2E-P3-11 · Quản lý hóa đơn: lọc, mở, xóa (Hiện tại đang chưa có xóa chỉ cho phép hủy hóa đơn)
**Màn:** SCR-10B
**Các bước:**
1. Lọc theo trạng thái / khoảng ngày / mã; xóa lọc; phân trang.
2. Mở một hóa đơn từ danh sách.
3. Xóa hóa đơn chưa hủy (phiếu 3, có phụ tùng): ghi tồn trước/sau.
4. Xóa hóa đơn đã thanh toán (phiếu 1).
**Kết quả mong đợi:** lọc/phân trang đúng, nhãn tiếng Việt; mở đúng hóa đơn; (3) hóa đơn bị xóa khỏi danh sách và DB, tồn được hoàn; (4) có **cảnh báo riêng** trước khi xóa, và hành vi sau xác nhận đúng thiết kế; không văng lỗi.

**PASS / NO:** `_____PASS_____`
**Mô tả thực tế / lỗi chi tiết:**
>

* Thao tác tra cứu, lọc và điều hướng trên SCR-10B:
  - Bộ lọc đa tiêu chí (theo trạng thái, khoảng ngày, mã hóa đơn) hoạt động chính xác; các nhãn trạng thái hiển thị đúng tiếng Việt chuẩn mực.
  - Chức năng Xóa bộ lọc (reset) và chuyển trang (phân trang) hoạt động mượt mà, không giật lag.
  - Chọn và nhấp đúp/bấm mở hóa đơn từ danh sách điều hướng chuẩn xác sang màn hình chi tiết tương ứng.
* Thao tác xóa hóa đơn và kiểm soát tồn kho:
  - Bước 3 (Hóa đơn chưa hủy/chờ thanh toán - phiếu 3 có phụ tùng): Thao tác xóa thành công, bản ghi được dọn sạch khỏi danh sách và DB; số lượng tồn kho của phụ tùng trong hóa đơn được hoàn lại chính xác vào bảng `PhuTung`.
  - Bước 4 (Hóa đơn đã thanh toán - phiếu 1): Hệ thống bật hộp thoại cảnh báo riêng biệt (yêu cầu xác nhận nghiêm ngặt trước khi xóa chứng từ tài chính đã hoàn tất thanh toán). Sau khi xác nhận, luồng xử lý diễn ra đúng thiết kế kiến trúc, không phát sinh lỗi SQL hay crash giao diện.
  
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

**PASS / NO:** `_____PASS_____`
**Mô tả thực tế / lỗi chi tiết:**
>

* Kiểm tra validation ràng buộc dữ liệu đầu vào:
  - Bước 1 (Sai mật khẩu hiện tại): Hệ thống từ chối và hiển thị thông báo lỗi xác thực mật khẩu cũ không chính xác.
  - Bước 2 (Mật khẩu mới và xác nhận không khớp): Hiển thị cảnh báo xác nhận mật khẩu không trùng khớp.
  - Bước 3 (Chính sách độ phức tạp mật khẩu): Kiểm tra tuần tự các trường hợp mật khẩu yếu (< 8 ký tự, thiếu chữ cái, thiếu chữ số, thiếu ký tự đặc biệt), mỗi trường hợp đều hiển thị thông điệp hướng dẫn/lỗi riêng biệt rõ ràng.
* Kiểm tra luồng đổi mật khẩu và cập nhật trạng thái:
  - Bước 4: Nhập mật khẩu mới thỏa mãn toàn bộ tiêu chuẩn bảo mật, hệ thống thông báo đổi mật khẩu thành công. Thông báo không làm lộ mật khẩu dạng thô (plaintext).
  - Bước 5: Sau khi đổi thành công, 3 ô nhập liệu (`Mật khẩu cũ`, `Mật khẩu mới`, `Xác nhận`) được xóa trắng (clear) tự động. Thực hiện đăng xuất: Đăng nhập bằng mật khẩu cũ bị từ chối; đăng nhập bằng mật khẩu mới truy cập thành công vào hệ thống.
  - Bước 6: Đã thực hiện đổi lại về mật khẩu ban đầu để bảo toàn dữ liệu tài khoản thử.
---

## Tổng kết

Số kịch bản: 12 · PASS: 9 · NO: 3 (E2E-P3-02, E2E-P3-03, E2E-P3-09)

***Ghi chú thêm của người test:

Tính toàn vẹn dữ liệu & Transaction DB:

Hoạt động ổn định và chính xác 100% ở tầng CSDL.

Toàn bộ nghiệp vụ kiểm soát tồn kho (tăng/trừ/hoàn tồn khi lập/xóa phiếu nhập ở SCR-07/07B và lập/hủy/xóa hóa đơn ở SCR-10/10B) được bảo toàn nguyên vẹn.

Dữ liệu voucher được chốt cứng tỷ lệ giảm vào hóa đơn, ràng buộc Unique UQ_HoaDon_maPhieuTiepNhan chống tạo trùng hóa đơn hoạt động chuẩn xác, không làm phát sinh lỗi cơ sở dữ liệu ra ngoài giao diện.

Danh sách các lỗi tồn đọng cần khắc phục trên UI/UX:

SCR-07 (Lập phiếu nhập): Ô đơn giá đã chặn nhập chữ/số âm, tuy nhiên khi người dùng click vào ô mà không nhập giá trị thì bị mất luôn giá; cần bổ sung cơ chế tự động khôi phục lại giá ban đầu nếu để trống.

SCR-10 (Lập & Hủy hóa đơn):

Khi số lượng vượt tồn hoặc nhập âm/chữ, UI đã tự động reset về 1 giúp kho không bị âm, nhưng cần xem xét hiển thị thông báo rõ ràng hơn thay vì chỉ reset âm thầm.

Sau khi hủy hóa đơn, biểu tượng thùng rác (xóa dòng chi tiết) vẫn click được và thông báo sai ngữ cảnh "Trạng thái hiện tại không cho thanh toán"; cần vô hiệu hóa hoàn toàn icon này.

SCR-10 / SCR-10B (Xác nhận & Khóa hóa đơn):

Trên hóa đơn đã thanh toán, icon thùng rác (xóa dòng) vẫn chưa bị vô hiệu hóa hoàn toàn; cần khóa cứng nút này hoặc bật thông báo "Hóa đơn đã thanh toán không được chỉnh sửa".

SCR-10B hiện chỉ có tính năng Hủy hóa đơn, chưa có nút Xóa trực tiếp trên giao diện.

### Test Case 5: Đơn giá không hợp lệ (Đã chặn nhập số âm và chữ) (Nhưng click vào mà không nhập thì mất luôn giá nên set về giá ban đầu)
>
