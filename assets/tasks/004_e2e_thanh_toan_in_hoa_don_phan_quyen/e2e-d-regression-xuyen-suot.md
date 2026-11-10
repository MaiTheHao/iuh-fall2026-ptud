# E2E D — Regression đợt 1 và luồng xuyên suốt

- **Người test:** Đặng Thanh Bình · **Nhánh:** `test/e2e-d-regression-xuyen-suot`
- **Ngày chạy:** ________ · **Commit `develop`:** ________ · **DB:** sạch, migration `V0`–`V15` (gồm seed mới)
- **Màn phạm vi:** SCR-03, 03B, 06, 08, SCR-04B, SCR-05, SCR-07B, SCR-10, 10B, SCR-12/12B/12C
- **Quy ước:** dữ liệu tự tạo có tiền tố `E2E-D`. **Chạy sau khi lane A–C đã có kết quả.** Điền `PASS / NO` ngay sau mỗi kịch bản; `NO` thì bắt buộc mô tả lỗi theo mẫu 7 dòng trong file hướng dẫn.
- **Mục đích:** mỗi kịch bản dưới đây tái hiện một lỗi đã ghi `NO` ở đợt 1 (`assets/tasks/002_test_e2e/SUMMARY.md`) để xác nhận bản fix, hoặc xác nhận lại luồng xuyên suốt sau khi thêm tính năng mới. Đánh `PASS` chỉ khi đúng **kết quả mong đợi mới** (không phải kết quả đợt 1).

---

### E2E-D-01 · Regression P1-05: phụ tùng — tồn kho và lỗi lên UI
**Màn:** SCR-06
**Các bước:**
1. Thêm phụ tùng `E2E-D Phụ tùng X` với đơn giá hợp lệ. Quan sát ô tồn kho (read-only, hiện `0`). Lưu. Kiểm tra DB `soLuongTon`.
2. Sửa đơn giá; thử sửa ô tồn kho xem có nhập được không. Lưu. Kiểm tra lại DB.
3. Lần lượt nhập: đơn giá âm, đơn giá chữ, đơn giá rỗng, tên rỗng, bỏ trống nhà cung cấp.
4. Xóa phụ tùng.
**Kết quả mong đợi:** (1) tồn kho mới = **0** và ô tồn **không cho nhập tay** (đúng thiết kế: tồn chỉ thay đổi qua phiếu nhập SCR-07 — nếu app cho sửa tay thì mới là lỗi); (2) sửa đơn giá lưu đúng, tồn không đổi; (3) mỗi trường hợp bị chặn, lỗi hiện **ngay tại ô nhập** (viền đỏ + tooltip tiếng Việt), không chỉ ở console, không lưu; (4) xóa thành công.

**PASS / NO:** ________
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-D-02 · Regression P1-06: dịch vụ — lỗi lên UI
**Màn:** SCR-08
**Các bước:** thêm dịch vụ mới tên `E2E-D Dịch vụ Y` với lần lượt: đơn giá âm, đơn giá chữ, đơn giá rỗng, tên rỗng.
**Kết quả mong đợi:** mỗi trường hợp bị chặn, lỗi hiện **ngay tại ô nhập** (viền đỏ + tooltip tiếng Việt), không lưu, không văng lỗi.

**PASS / NO:** ________
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-D-03 · Regression P2-06: hủy phiếu có hóa đơn đồng bộ cả hai
**Màn:** SCR-04B, SCR-10B
**Tiền điều kiện:** tự tạo 2 phiếu tiếp nhận `E2E-D` và lập hóa đơn cho chúng ở SCR-10: một hóa đơn `CHO_THANH_TOAN` (có phụ tùng — ghi tồn trước), một hóa đơn `DA_THANH_TOAN` (thanh toán ở SCR-10).
**Các bước:**
1. Hủy phiếu có hóa đơn `CHO_THANH_TOAN`; thử **bỏ trống lý do** trước, sau đó nhập lý do `E2E-D hủy phiếu`. Kiểm tra DB: `PhieuTiepNhan`, `HoaDon`, tồn kho.
2. Thử hủy phiếu có hóa đơn `DA_THANH_TOAN`.
**Kết quả mong đợi:** (1) bỏ trống lý do bị chặn (`app.hoaDon.loiLyDoHuy`); nhập lý do thì phiếu **và hóa đơn cùng** chuyển `DA_HUY` trong DB, tồn kho hoàn lại đúng số lượng — một transaction, không để lệch trạng thái; SCR-10B hiện hóa đơn đã hủy; (2) bị **chặn** với thông báo rõ ràng (`app.phieuTiepNhan.loiHoaDonDaThanhToan`), phiếu không đổi, không văng lỗi.

**PASS / NO:** ________
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-D-04 · Regression P3-06: thông báo vượt tồn đúng ngữ cảnh
**Màn:** SCR-10
**Các bước:**
1. Ghi lại tồn hiện có của một phụ tùng (dùng seed `PT-20261020-000002`, tồn 32 — **không sửa tay**; nếu cần phụ tùng riêng thì tạo `E2E-D …` và lập phiếu nhập SCR-07 để có tồn).
2. Ở SCR-10, chọn phụ tùng qua dialog chọn phụ tùng rồi nhập số lượng **lớn hơn tồn**. Đọc thông báo.
3. Nhập số lượng âm (cùng đường chọn qua dialog). Đọc thông báo.
**Kết quả mong đợi:** (2) bị chặn kèm thông báo nói **tồn không đủ** (`app.hoaDon.loiTonKho` = "Số lượng tồn không đủ") — **không** phải "Số lượng phải lớn hơn 0"; không tạo hóa đơn, tồn không đổi; (3) thông báo là lỗi số lượng (`app.hoaDon.loiSoLuong`), không phải "Phụ tùng chưa hợp lệ".

**PASS / NO:** ________
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-D-05 · Regression P3-10: hủy hóa đơn xong toàn bộ form khóa
**Màn:** SCR-10
**Các bước:**
1. Hủy một hóa đơn `CHO_THANH_TOAN` có phụ tùng, kèm lý do.
2. Sau khi hủy thành công, lần lượt thử: sửa dòng chi tiết, bấm icon thùng rác (xóa dòng), bấm Xác nhận thanh toán, bấm Lưu.
**Kết quả mong đợi:** các nút Lưu / Xác nhận thanh toán / thêm dòng bị **disable** (không bấm được) sau khi hủy; nếu bấm được icon thùng rác (xóa dòng) thì thông báo đúng ngữ cảnh "Hóa đơn đã hủy" (`app.hoaDon.loiDaHuy`) — **không** còn "Trạng thái hiện tại không cho thanh toán". Ghi riêng thực tế của nút **Xóa trang** (nếu còn bấm được) vì hiện có thể vẫn dùng thông báo trạng thái chung. Tồn kho hoàn lại đúng; DB: `DA_HUY` + lý do.

**PASS / NO:** ________
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-D-06 · Regression P1-12: nhân viên mới chọn được ở phân công
**Màn:** SCR-11, SCR-05
**Các bước:**
1. Tạo nhân viên `E2E-D NV` với đủ trường bắt buộc, chọn vai trò **khác Quản lý** (ví dụ tiếp nhận) và trạng thái Hoạt động. Lưu.
2. Mở SCR-05, tạo phân công cho nhân viên vừa tạo (ngày hiện tại, ca bất kỳ).
**Kết quả mong đợi:** nhân viên mới **xuất hiện ngay** trong danh sách chọn của SCR-05, chọn và lưu được phân công; kiểm tra DB `PhanCong` có dòng mới. (Quy tắc lọc của màn: chỉ nhân viên Hoạt động và **không phải Quản lý** — nhân viên vai trò Quản lý không xuất hiện là đúng thiết kế.)

**PASS / NO:** ________
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-D-07 · Regression P4-14: enum ca làm việc theo ngôn ngữ
**Màn:** SCR-05 (form + lưới), SCR-05B, SCR-05C
**Các bước:**
1. Ở tiếng Việt, rà nhãn ca trong **form phân công** (SCR-05) và trong **lưới lịch tuần** (SCR-05, SCR-05B, SCR-05C).
2. Đổi sang tiếng Anh, rà lại đúng 3 màn đó.
**Kết quả mong đợi:** form hiển thị Ca sáng / Ca chiều / Ca ngày (EN: Morning shift / Afternoon shift / Full-day shift); lưới lịch hiển thị nhãn ca theo ngôn ngữ đang chọn ở cả 3 màn; không lộ key i18n thô.

**PASS / NO:** ________
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-D-08 · Regression P3-04/P3-11: ô lọc tổng nhận đúng kiểu số
**Màn:** SCR-07B, SCR-10B
**Các bước:**
1. Ở ô lọc tổng chi phí (SCR-07B) gõ chữ `abc`. Ở ô lọc tổng tiền (SCR-10B) gõ chữ `abc`.
2. Gõ số hợp lệ, lọc; xóa lọc.
**Kết quả mong đợi:** ô **không nhận** ký tự chữ (bị chặn ngay, im lặng — đây là hành vi đã chốt, không đánh `NO` vì không có thông báo); số hợp lệ lọc đúng, xóa lọc trả lại danh sách.

**PASS / NO:** ________
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-D-09 · Luồng xuyên suốt: tiếp nhận → hóa đơn → thanh toán → in → báo cáo
**Màn:** SCR-03/04 → SCR-10 → SCR-12/12B/12C → SCR-10B
**Tiền điều kiện:** đăng nhập `admin`; chốt trước **P = `PT-20261020-000002`** (65.000, tồn 32 seed) và **D = `DV-20261020-000009`** (70.000); voucher dùng `VC-20261020-000007` (giảm 20%, còn hạn). Biển số mới phải khớp `^[0-9]{2}[A-Z]{1,2}[0-9]{4,6}$`, ví dụ `59C12345`. Ghi lại số liệu nền vào bảng dưới (đối chiếu DB để biết seed tháng này đã có hóa đơn thanh toán hay chưa).
**Các bước:**
1. Ghi số liệu nền: ô **Tổng doanh thu** và **Tổng tiền gốc** tháng hiện tại (SCR-12), tiêu thụ P (SCR-12B), tiêu thụ D (SCR-12C), tồn P.
2. Tiếp nhận khách + xe mới `E2E-D` ở SCR-04.
3. Lập hóa đơn: dịch vụ D (1) + phụ tùng P (2), áp voucher `VC-20261020-000007`. Ghi tổng gộp và thực thu (dự kiến: gộp 70.000 + 2×65.000 = 200.000; giảm 20% = 40.000; thực thu = 160.000).
4. Bấm Xác Nhận Thanh Toán → chọn **Tiền mặt** (để không phụ thuộc mạng/QR — QR đã có lane A) → xác nhận.
5. In hóa đơn ra PDF và xuất Excel.
6. Mở lại ba báo cáo, ghi số liệu sau.
7. Lập hóa đơn thứ hai cho một phiếu `E2E-D` khác với dịch vụ D (1) + phụ tùng P (1), **không thanh toán**, ghi tồn P; hủy hóa đơn đó. Mở lại báo cáo.
**Kết quả mong đợi:** (4) DB có dòng `ThanhToan` `TIEN_MAT` với `soTien` = 160.000, hóa đơn `DA_THANH_TOAN`; (5) bản in/file Excel khớp hóa đơn, có khối thanh toán; (6) ô **Tổng doanh thu** (SCR-12, = thực thu) tăng 160.000 và ô **Tổng tiền gốc** tăng 200.000; tiêu thụ D +1; tiêu thụ P +2 **quan sát qua tổng / DB** (bảng SCR-12B chỉ hiện Top 20 theo lượng xuất nên dòng P lượng nhỏ có thể không xuất hiện — không đánh `NO` vì việc này); tồn P −2; (7) lập hóa đơn thứ hai làm tồn P −1 nhưng hủy thì hoàn lại +1 (tồn về như sau bước 6), doanh thu/tiêu thụ không đổi. Điền bảng số liệu trước/sau bên dưới.

| Chỉ số | Trước (bước 1) | Sau thanh toán (bước 4–6) | Sau bước 7 |
|---|---|---|---|
| Tổng doanh thu SCR-12 (thực thu) | | | |
| Tổng tiền gốc SCR-12 | | | |
| Tiêu thụ P (SCR-12B: tổng/DB) | | | |
| Tiêu thụ D (SCR-12C) | | | |
| Tồn P | | | |

**PASS / NO:** ________
**Mô tả thực tế / lỗi chi tiết (kèm bảng số liệu trước/sau):**
>

---

## Tổng kết

- Số kịch bản: 9 · **PASS:** ____ · **NO:** ____
- Ghi chú thêm của người test:
>
