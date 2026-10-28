# Hướng dẫn task — Test E2E Gara Ô tô Lúa

**Ngày:** 24/10/2026 · **Phạm vi:** 25/25 màn P1–P4 · **Người sửa lỗi:** Mai Thế Hào

## 1. Phân công (mỗi người CHỈ làm file của mình)

| Người test | Lane | File kịch bản | Nhánh test |
|---|---|---|---|
| Nguyễn Minh Quốc | P1 — Tổ chức | `2026-10-24-e2e-p1-to-chuc.md` | `test/e2e-p1-to-chuc` |
| Trần Nguyễn Hữu Thắng | P2 — Tiếp nhận & Khiếu nại | `2026-10-24-e2e-p2-tiep-nhan-khieu-nai.md` | `test/e2e-p2-tiep-nhan-khieu-nai` |
| Nguyễn Trần Trọng Khang | P3 — Nhập phụ tùng & Thanh toán | `2026-10-24-e2e-p3-nhap-thanh-toan.md` | `test/e2e-p3-nhap-thanh-toan` |
| Đặng Thanh Bình | P4 — Kế hoạch & Tổng kết | `2026-10-24-e2e-p4-ke-hoach-tong-ket.md` | `test/e2e-p4-ke-hoach-tong-ket` |
| Mai Thế Hào | Người sửa lỗi + review | Không điền file test | `fix/e2e-<mã kịch bản>` |

**Quy tắc cứng:**

- Chỉ mở, sửa, commit file của lane mình. Không sửa file của người khác (tránh xung đột).
- Không sửa mã nguồn (`src/**`) trong task này. Thấy lỗi thì **ghi lại**, không tự sửa. Việc sửa là của Mai Thế Hào.
- Không commit trực tiếp lên `develop`/`master`.

## 2. Cấu trúc mỗi kịch bản

Mỗi kịch bản có: mã, màn hình, tiền điều kiện, các bước, kết quả mong đợi, và **2 chỗ để điền**:

- `PASS / NO:` điền đúng một trong hai từ **`PASS`** hoặc **`NO`**.
- `Mô tả thực tế / lỗi chi tiết:` chỗ trống để mô tả.

## 3. Cách điền

**PASS** — mọi kết quả mong đợi đều đúng. Mô tả: ghi ngắn dữ liệu đã dùng và kết quả thấy (1–2 dòng).

**NO** — có ít nhất một kết quả mong đợi sai/thiếu, ứng dụng văng lỗi, treo, hoặc dữ liệu DB sai. **Bắt buộc mô tả lỗi chi tiết**, theo mẫu:

```
Bước lỗi: <số bước trong kịch bản>
Dữ liệu đã nhập: <giá trị cụ thể, ví dụ sdt=0901234567, biển số=51A-12345>
Mong đợi: <điều đáng ra phải xảy ra>
Thực tế: <điều đã xảy ra, chép nguyên văn thông báo lỗi nếu có>
Tái hiện: <luôn luôn / thỉnh thoảng; đã thử mấy lần>
Bằng chứng: <đường dẫn ảnh chụp/log/stack trace, truy vấn SQL kiểm tra DB>
Mức độ: <Chặn luồng chính / Lỗi dữ liệu / Lỗi hiển thị / Nhỏ>
```

Một kịch bản `NO` mà thiếu mô tả theo mẫu sẽ bị trả lại để làm lại.

Không để trống ô `PASS / NO`. Kịch bản không chạy được vì phụ thuộc lane khác chưa sẵn sàng thì ghi `NO` và nêu rõ phụ thuộc nào bị thiếu.

## 4. Quy trình

1. `git switch develop && git pull`, tạo nhánh test của lane: `git switch -c test/e2e-<lane>`.
2. Build bản mới nhất, khởi động ứng dụng trên DB sạch (chạy migration `V0`–`V11` + dữ liệu mẫu). Ghi commit và ngày chạy vào đầu file.
3. Chạy lần lượt từng kịch bản từ trên xuống, điền kết quả **ngay sau mỗi kịch bản**.
4. Điền phần Tổng kết cuối file (số PASS / số NO).
5. Commit và push, mở Pull Request vào `develop`:
   ```
   git add assets/tasks/2026-10-24-e2e-<lane>.md
   git commit -m "test(e2e): hoàn thành kịch bản E2E <lane>"
   git push -u origin test/e2e-<lane>
   ```
6. Báo Mai Thế Hào qua nhóm khi đã push, kèm số lượng `NO`.
7. Mai Thế Hào sửa lỗi trên nhánh `fix/e2e-<mã kịch bản>`, mỗi lỗi một commit (`fix(<lane>): ...`), báo lại người test.
8. **Retest:** người test chạy lại đúng kịch bản đó, đổi `NO` thành `PASS` nếu đạt và thêm một dòng vào mô tả: `Retest dd/mm/yyyy: PASS (commit <mã>)`. Nếu vẫn lỗi, giữ `NO` và ghi thêm điều còn sai.
9. Hoàn tất khi file không còn `NO`, Mai Thế Hào review và merge.

## 5. Thứ tự chạy và phụ thuộc dữ liệu

`P1 → P2 → P3 → P4`. P2 dùng khách hàng/xe/nhân viên của P1; P3 dùng phiếu tiếp nhận của P2 và phụ tùng/dịch vụ/voucher của P1; P4 đọc hóa đơn và phiếu nhập của P3.

- Người test sau có thể bắt đầu bằng dữ liệu mẫu (`V4__mock_data.sql`) mà không chờ người trước, trừ các kịch bản ghi chú "cần dữ liệu từ lane trước".
- Mỗi người **đặt tiền tố riêng** cho dữ liệu tự tạo (ví dụ tên khách `E2E-P2 ...`, ghi chú `E2E`) để dễ nhận biết và dọn.

## 6. Điểm mở đã biết (không tính là lỗi mới)

- Doanh thu SCR-12 là doanh thu gộp (`soLuong * donGia`), chưa trừ voucher, nên lệch tổng tiền hóa đơn SCR-10 khi hóa đơn có voucher. Kịch bản P4 có mục ghi nhận số liệu này; ghi lại số thực tế, đừng đánh `NO` chỉ vì sự chênh lệch này, trừ khi con số không khớp công thức gộp.
- Thụt lề tab/4 dấu cách của tệp Java P4: không thuộc phạm vi test E2E.
- Ngoài phạm vi của các lane (không test): đặt lại mật khẩu từ dòng nhân viên, cảnh báo tồn kho thấp, tải ảnh, phân quyền menu, đính kèm ảnh khiếu nại, giảm giá nâng cao, badge màu.

## 7. Bảng theo dõi chung (Mai Thế Hào cập nhật)

| Lane | Số kịch bản | NO ban đầu | NO còn lại | Trạng thái |
|---|---|---|---|---|
| P1 | 12 | | | |
| P2 | 14 | | | |
| P3 | 12 | | | |
| P4 | 14 | | | |

## 8. Phạm vi & điểm mở đã chốt

- Phân quyền menu ngoài phạm vi test (P1-01) -> tính là PASS.
- Biển số phải khớp `^[0-9]{2}[A-Z]{1,2}[0-9]{4,6}$`; dùng `52E10001` thay cho `E2E-P1-0001` (P1-04).
- Mỗi màn danh sách hiển thị 20 dòng/trang (P1-05(e), P1-09).
- Voucher đã hết hạn vẫn lưu được; việc chặn áp dụng nằm ở màn Lập hóa đơn (P1-07).
- SCR-16 để trống danh sách phiếu cho tới khi tra số điện thoại là đúng thiết kế; nội dung khiếu nại không giới hạn độ dài (P2-12).
- P3-03 không tái hiện được lỗi giữa chừng trên môi trường local: giữ `NO` kèm lý do, không phải bug.

## 9. Hợp đồng xóa lan / lan trạng thái

- Xóa Khách hàng / Xe / Nhân viên / Phụ tùng / Dịch vụ / Voucher bị **chặn** khi còn chứng từ tham chiếu (phiếu tiếp nhận, hóa đơn, phiếu nhập, phân công).
- Hủy phiếu tiếp nhận lan sang hóa đơn `CHO_THANH_TOAN` (hủy hóa đơn + hoàn tồn + hủy phiếu trong cùng một transaction).
- Hóa đơn đã thanh toán **không hủy được từ màn phiếu**; phải sang màn Hóa đơn hủy trước.
- Hóa đơn không bị xóa cứng: thao tác "xóa" ở SCR-10B nay là **Hủy hóa đơn** và cho phép cả hóa đơn `DA_THANH_TOAN`.
- Khóa nhân viên không xóa phân công hiện có; chỉ chặn phân ca mới.

## 10. Cần tester bổ sung bằng chứng trước khi đóng

- P1-12 (không chọn được nhân viên mới ở SCR-05 — cần ảnh/log).
- P3-02 (thông báo khi số lượng = 0 — cần bản build + commit).
- P4-13 (retest sau khi P3 xong, kèm bảng số liệu trước/sau).

## 11. Quy trình retest

- Mỗi mục `NO` điền đủ mẫu 7 dòng.
- Ghi commit + ngày.
- Kiểm tồn kho bằng SQL trước/sau.
- Đổi `NO` -> `PASS` chỉ khi đã có bằng chứng.
