# Hướng dẫn task — Test E2E Gara Ô tô Lúa (đợt 2: Thanh toán, In hóa đơn, Phân quyền)

**Ngày:** 10/11/2026 · **Phạm vi:** tính năng mới từ task 003 (dialog thanh toán QR/tiền mặt, in hóa đơn, xuất Excel, phân quyền menu) + retest các lỗi còn NO của đợt 1 · **Người sửa lỗi / review:** Mai Thế Hào

## 0. Vì sao có đợt test này

- Đợt 1 (`assets/tasks/002_test_e2e`) kết thúc với **16 mục NO**; các nhóm lỗi đã được sửa (thanh toán, hiển thị lỗi UI, hủy phiếu/hóa đơn, phân quyền).
- Task 003 đã hoàn thành 3 phần: dialog thanh toán QR/tiền mặt, in hóa đơn + xuất Excel, ẩn/hiện menu theo vai trò.
- DB đã nâng cấp lên migration `V0`–`V15` với **dữ liệu mẫu mới (mã chứng từ dạng `DV-20261020-000001`, `PT-20261020-000002`, `VC-20261020-000007`, …)**. Các mã cũ trong đợt 1 (`PT002`, `VC007`, …) **không còn** — không dùng lại trong kịch bản.

## 1. Phân công (mỗi người CHỈ làm file của mình)

Phân công theo nguyên tắc **người test đúng phần mình đã xây** (bảng phân công thực tế trong `assets/tasks/001_init_codebase/README.md` và task 003):

- **Quốc** xây dialog thanh toán QR/tiền mặt (003-P1) và phân quyền menu (003-P3) → nhận lane A + C.
- **Khang** xây và đã test luồng hóa đơn ở đợt 1 (SCR-07/07B/10/10B) → nhận lane B (in/xuất nằm trên màn SCR-10B của Khang).
- **Bình** giữ lane xuyên lane như đợt 1 (P4); D-06, D-07, D-09 là màn phân công/báo cáo của Bình → nhận lane D.
- **Thắng**: SCR-04/04B/04C/04D/16/16B không có tính năng mới trong đợt này nên không có lane riêng; hỗ trợ lane D bằng cách cung cấp/đối chiếu dữ liệu tiếp nhận và phiếu hủy (D-03, D-09 bước 2).

| Người test | Lane | File kịch bản | Nhánh test |
|---|---|---|---|
| Nguyễn Minh Quốc | A — Thanh toán QR / tiền mặt **và** C — Phân quyền menu theo vai trò | `e2e-a-thanh-toan.md`, `e2e-c-phan-quyen-menu.md` | `test/e2e-a-thanh-toan`, `test/e2e-c-phan-quyen-menu` |
| Nguyễn Trần Trọng Khang | B — In hóa đơn & Xuất Excel | `e2e-b-in-hoa-don-excel.md` | `test/e2e-b-in-hoa-don-excel` |
| Đặng Thanh Bình | D — Regression đợt 1 + luồng xuyên suốt | `e2e-d-regression-xuyen-suot.md` | `test/e2e-d-regression-xuyen-suot` |
| Trần Nguyễn Hữu Thắng | Không có lane riêng — hỗ trợ dữ liệu cho lane D (D-03, D-09) | Không điền file test | — |
| Mai Thế Hào | Người sửa lỗi + review | Không điền file test | `fix/e2e2-<mã kịch bản>` |

**Quy tắc cứng (giữ nguyên từ đợt 1):**

- Chỉ mở, sửa, commit file của lane mình. Không sửa file của người khác.
- Không sửa mã nguồn (`src/**`) trong task này. Thấy lỗi thì **ghi lại** theo mẫu, không tự sửa. Riêng 2 kịch bản A-06, A-07 được phép **sửa tạm file tài nguyên** `src/resources/qr.properties` trên máy local (không commit) để tạo tình huống lỗi QR — ghi rõ trong mô tả.
- Không commit trực tiếp lên `develop`/`master`.

## 2. Cấu trúc mỗi kịch bản

Giống hệt đợt 1: mã, màn hình, tiền điều kiện, các bước, kết quả mong đợi, và 2 chỗ để điền:

- `PASS / NO:` điền đúng một trong hai từ **`PASS`** hoặc **`NO`**.
- `Mô tả thực tế / lỗi chi tiết:` chỗ trống để mô tả.

**NO** bắt buộc mô tả theo mẫu 7 dòng (Bước lỗi / Dữ liệu đã nhập / Mong đợi / Thực tế / Tái hiện / Bằng chứng / Mức độ) — thiếu mẫu sẽ bị trả lại.

## 3. Tài khoản và dữ liệu nền (đã đổi so với đợt 1)

Đăng nhập bằng seed `V15`, mật khẩu chung **`test@123`**:

| Tài khoản | Vai trò | Trạng thái |
|---|---|---|
| `admin` | Quản lý | Hoạt động |
| `tiepnhan01` | Nhân viên tiếp nhận | Hoạt động |
| `thungan01` | Nhân viên thu ngân | Hoạt động |
| `kythuat01` | Nhân viên kỹ thuật | Hoạt động |
| `thungan02`, `quanly02` | Thu ngân / Quản lý | **Bị khóa** (chỉ dùng ở lane C) |

Dữ liệu mẫu hay dùng (mã mới, seed `V15`):

| Loại | Mã | Ghi chú |
|---|---|---|
| Phụ tùng | `PT-20261020-000002` Lọc dầu động cơ | 65.000 đ, tồn 32 |
| Dịch vụ | `DV-20261020-000009` Kiểm tra ắc quy | 70.000 đ |
| Voucher hợp lệ | `VC-20261020-000007` giảm 20% | hạn 31/08/2027, đang kích hoạt |
| Voucher vô hiệu | `VC-20261020-000010` giảm 25% | hết hạn 30/11/2026, tắt kích hoạt |

Quy ước biển số: khớp regex hệ thống `^[0-9]{2}[A-Z]{1,2}[0-9]{4,6}$` (ví dụ `59C12345`). **Không** dùng biển số có dấu `-` hay dấu `.`.

## 4. Thứ tự chạy và phụ thuộc

`A → B → D`, chạy **C xen kẽ** (C độc lập, cùng người với A nên Quốc làm A trước):

- **A** (Thanh toán, Quốc) tự tạo hóa đơn từ phiếu tiếp nhận — chạy được ngay, không cần lane khác.
- **B** (In/Xuất, Khang) cần hóa đơn **đã thanh toán** có dòng `ThanhToan` → **bắt đầu sau khi A xong ít nhất 2 hóa đơn** (một QR, một tiền mặt); có thể tự thanh toán thêm hóa đơn nếu A chưa xong.
- **C** (Phân quyền, Quốc) chỉ cần tài khoản seed — chạy sau A (hoặc xen kẽ khi chờ A), không phụ thuộc dữ liệu nghiệp vụ.
- **D** (Regression, Bình) chạy cuối, sau khi A–C đã có kết quả, vì gồm cả luồng xuyên suốt.

Mỗi người đặt tiền tố dữ liệu riêng (`E2E-A …`, `E2E-B …`, `E2E-D …`) để dễ nhận biết và dọn. Lane C không tạo dữ liệu nghiệp vụ. Thắng hỗ trợ Bình đối chiếu dữ liệu tiếp nhận/phiếu hủy cho D-03 và D-09 bước 2.

## 5. Quy trình (giữ nguyên từ đợt 1)

1. `git switch develop && git pull`, tạo nhánh: `git switch -c test/e2e-<lane>`.
2. Build bản mới nhất, chạy trên **DB sạch** (migration `V0`–`V15`, gồm seed mới). Ghi commit và ngày chạy vào đầu file.
3. Chạy lần lượt từ trên xuống, điền kết quả **ngay sau mỗi kịch bản**; `NO` chụp ảnh/log/SQL làm bằng chứng ngay tại chỗ.
4. Điền phần Tổng kết cuối file; commit, push, mở PR vào `develop`:
   ```
   git add assets/tasks/004_e2e_thanh_toan_in_hoa_don_phan_quyen/e2e-<lane>.md
   git commit -m "test(e2e2): hoàn thành kịch bản E2E <lane>"
   git push -u origin test/e2e-<lane>
   ```
5. Báo Mai Thế Hào kèm số lượng `NO`. Sửa lỗi trên nhánh `fix/e2e2-<mã kịch bản>`, mỗi lỗi một commit `fix(<lane>): ...`.
6. **Retest:** chạy lại đúng kịch bản, đổi `NO` → `PASS` khi có bằng chứng, thêm dòng `Retest dd/mm/yyyy: PASS (commit <mã>)`.
7. Hoàn tất khi file không còn `NO`; Mai Thế Hào review và merge.

## 6. Điểm mở đã biết (không tính là lỗi mới)

- Phân quyền menu **chỉ ẩn/hiện menu**, không phải kiểm soát truy cập: không test việc chặn trang qua đường khác (đã được chủ dự án chấp nhận ở task 003).
- **Doanh thu SCR-12 hiện có 2 ô**: ô **Tổng doanh thu** = thực thu (đã trừ voucher), ô **Tổng tiền gốc** = tổng gộp (`soLuong * donGia`). Đối chiếu cả hai, ghi rõ số nào; không đánh `NO` vì chênh lệch giữa 2 ô.
- Báo cáo tiêu thụ SCR-12B/12C chỉ hiện **Top 20** theo lượng xuất/lượt dùng: phụ tùng/dịch vụ lượng nhỏ có thể không có dòng — đối chiếu qua tổng hoặc DB, không đánh `NO`.
- **Xem chẩn đoán** và **Bảng chẩn đoán** là màn con (`getGroup() == null`): không có mục menu dù được cấp quyền — không đánh `NO`, chỉ ghi đề xuất nếu muốn có menu.
- Tài khoản **bị khóa** hiện bị từ chối bằng thông báo dùng chung với sai mật khẩu — không đánh `NO` vì thiếu thông báo riêng "tài khoản bị khóa"; ghi lại nguyên văn làm đề xuất.
- Khi thanh toán **tiền mặt**, trường ghi chú trong dialog **không được lưu** (chỉ áp dụng cho chuyển khoản) — đây là hành vi hiện tại, ghi lại nếu khác.
- Mã QR động phụ thuộc mạng và cấu hình `src/resources/qr.properties`; lỗi QR có fallback `qr.jpg` là hành vi thiết kế — chỉ đánh `NO` khi không thanh toán được hoặc app văng lỗi.
- Tồn kho phụ tùng hiện **không nhập tay** ở form thêm/sửa: mới tạo = 0, chỉ thay đổi qua phiếu nhập SCR-07 — đây là thiết kế đã chốt, không đánh `NO`.
- Mẫu in hóa đơn là một trang A4 cố định: hóa đơn **rất nhiều dòng** có thể bị tràn/cắt — ghi lại thực tế ở B-08 (dùng hóa đơn ≤ 15 dòng cho B-01), không đánh `NO` chỉ vì tràn trang.
- Nút **Xác Nhận Thanh Toán** bật/tắt theo trạng thái hóa đơn, không theo vai trò; `admin` (Quản lý) thấy mọi trang nên dùng `admin` cho lane A/B là hợp lệ.
- Ngoài phạm vi: cảnh báo tồn kho thấp, tải ảnh, giảm giá nâng cao, badge màu.

## 7. Bảng theo dõi chung (Mai Thế Hào cập nhật)

| Lane | Số kịch bản | NO ban đầu | NO còn lại | Trạng thái |
|---|---|---|---|---|
| A Thanh toán | 9 | | | |
| B In & Excel | 9 | | | |
| C Phân quyền | 8 | | | |
| D Regression | 9 | | | |

## 8. Bảng NO đợt 1 cần được đóng lại ở lane D

| Mã đợt 1 | Nội dung | Kịch bản retest |
|---|---|---|
| P1-05 / P1-06 | Lỗi validation phụ tùng/dịch vụ chỉ ở console; tồn kho lưu 0 dù UI cho nhập tay (nay đã chốt: tồn chỉ đổi qua phiếu nhập) | D-01, D-02 |
| P1-12 | SCR-05 không chọn được nhân viên mới tạo | D-06 |
| P2-06 | Hủy phiếu có hóa đơn không đồng bộ hóa đơn/DB | D-03 |
| P3-04 / P3-11 | Ô lọc tổng tiền/chi phí nhận chữ | D-08 |
| P3-06 | Thông báo vượt tồn sai ngữ cảnh | D-04 |
| P3-10 | Sau hủy hóa đơn, icon xóa dòng vẫn bấm được, báo sai | D-05 |
| P4-14 | Enum ca làm việc không đổi theo tiếng Anh | D-07 |
