# SUMMARY — Kết quả E2E Gara Ô tô Lúa (P1–P4)

Tổng hợp từ 4 file lane + README. Chạy 26–27/10/2026.

## 1. Tổng quan (điền thẳng vào bảng §7 README)

| Lane | Người test | Kịch bản | PASS | NO ban đầu | Mã NO |
|---|---|---|---|---|---|
| P1 Tổ chức | Nguyễn Minh Quốc | 12 | 7 | **5** | 01, 04, 05, 06, 12 |
| P2 Tiếp nhận & Khiếu nại | Trần Nguyễn Hữu Thắng | 14 | 12 | **2** | 06, 12 |
| P3 Nhập phụ tùng & Thanh toán | Nguyễn Trần Trọng Khang | 12 | 6 | **6** | 02, 03, 04, 06, 10, 11 |
| P4 Kế hoạch & Tổng kết | Đặng Thanh Bình | 14 | 11 | **3** | 07, 13, 14 |
| **Tổng** | | **52** | **36** | **16** | |

Nhận xét nhanh: lõi nghiệp vụ/DB ổn (transaction, tồn kho, voucher, chống trùng hóa đơn, báo cáo doanh thu). Lỗi tập trung ở **validation/thông báo trên UI** và vài chỗ **lệch giữa kịch bản và thiết kế**.

## 2. Bảng NO chi tiết

| Mã | Tóm tắt lỗi | Mức độ (đề xuất) | Loại |
|---|---|---|---|
| P1-01 | Ghi "chưa có phân quyền" | — | **Ngoài phạm vi** (README §6: phân quyền menu không test) → nên xem lại, có thể đổi PASS |
| P1-04 | Biển số kịch bản `E2E-P1-0001` không khớp regex `^[0-9]{2}[A-Z]{1,2}[0-9]{4,6}$`; đời xe/km rỗng báo lỗi. Đổi sang `52E10001`, 2020, 5000 thì bước 2→5 PASS | Nhỏ | Lệch kịch bản/thiết kế |
| P1-05 | (a) Tồn kho nhập tay luôn set về 0 nhưng vẫn báo lưu thành công (cả thêm và sửa). (b) Mã vừa tự sinh vừa cho nhập. (c) UI cho nhập tồn nhưng tồn chỉ đổi qua phiếu nhập. (d) Lỗi đơn giá âm/tồn âm/tên rỗng chỉ hiện ở console. (e) Không rõ số dòng/trang | **Lỗi dữ liệu** (a); Lỗi hiển thị (d) | Bug |
| P1-06 | Đơn giá âm/chữ/rỗng, tên rỗng: lỗi chỉ ở console, không lên UI | Lỗi hiển thị | Bug |
| P1-12 | Nhân viên mới tạo không chọn được ở SCR-05. Khách/xe, phụ tùng, dịch vụ/voucher thì OK | Chặn luồng (liên lane) | Bug (cần xác minh: có thể do lọc vai trò) |
| P2-06 | Hủy phiếu đã có hóa đơn: SCR-04B hiện "đã hủy" nhưng hóa đơn và DB không đổi | **Lỗi dữ liệu** | Bug / cần chốt thiết kế |
| P2-12 | Danh sách phiếu rỗng cho tới khi tra sdt; nội dung khiếu nại rất dài vẫn lưu được | Nhỏ | Cần chốt thiết kế (giới hạn độ dài?) |
| P3-02 | SL=0 báo "Chưa chọn phụ tùng hợp lệ" (mong đợi "Số lượng nhập phải lớn hơn 0"); chữ chưa có lỗi riêng; nhập sai không tự về giá cũ; xóa dòng chưa có xác nhận (hỏi lại) | Lỗi hiển thị | Bug + câu hỏi |
| P3-03 | Không tái hiện được lỗi giữa chừng (local quá nhanh, `docker pause` không đứt kết nối) | — | **Không thực hiện được** (theo kịch bản đánh NO) |
| P3-04 | Ô lọc tổng chi phí nhận chữ; nhãn cột/tiêu đề bị cắt "Tổng chi…". Còn lại đạt (lọc biên, xóa hoàn tồn, dialog) | Nhỏ | Bug UI |
| P3-06 | Tồn DB đúng (14→12, vượt tồn bị chặn) nhưng thông báo sai: vượt tồn → "Số lượng phải lớn hơn 0"; số âm → "Phụ tùng chưa hợp lệ" | Lỗi hiển thị | Bug |
| P3-10 | Hủy hóa đơn: DB đúng (`DA_HUY`, lý do, tồn 12→14). Nhưng icon thùng rác (xóa dòng) vẫn bấm được và báo sai "Trạng thái hiện tại không cho thanh toán" | Lỗi hiển thị | Bug |
| P3-11 | Lọc/mở/xóa đúng, tồn hoàn, cảnh báo riêng khi xóa HĐ đã thanh toán. Chỉ còn ô lọc tổng tiền nhận chữ | Nhỏ | Bug UI |
| P4-07 | Không lọc được khi bỏ trống khoảng ngày (bắt buộc chọn đủ từ–đến); bước 2–4 PASS | Nhỏ | **Lệch kịch bản/thiết kế** (kịch bản mong đợi "tùy chọn") |
| P4-13 | "Chưa có đủ các bảng để test theo luồng" — chạy 26/10, trước khi P3 xong (27/10) | — | **Phụ thuộc lane khác → cần chạy lại** |
| P4-14 | Enum ca làm việc ở bảng dữ liệu luôn tiếng Việt, không đổi sang tiếng Anh (form CRUD thì đổi được) | Lỗi hiển thị | Bug i18n |

## 3. PASS nhưng có ghi chú — nên soi lại

| Mã | Ghi chú | Gợi ý |
|---|---|---|
| P1-07 | Voucher thời hạn đã qua vẫn thêm được | Chốt quy định rồi ghi rõ |
| P1-08 | Thêm nhân viên không sdt **"văng lỗi"** | Kịch bản yêu cầu *không* văng lỗi → có thể phải là NO |
| P1-09 | Không biết số dòng/trang nên chưa test bước 4 đầy đủ | Nêu page size cho tester |
| P2-08 | Nhập `abc` vào mức ưu tiên → thông báo kiểu `warning` thay vì `error` | Nhỏ |
| P4-09 | PASS nhưng **không ghi 3 con số** (bắt buộc theo kịch bản) | Bổ sung số liệu |
| P3-07 | Dùng voucher 10% và 5%; chưa thấy VC007 (20%) trong mô tả | Nhỏ, tùy ý |

## 4. Việc cần Mai Thế Hào chốt (quyết định thiết kế)

1. **P4-07**: báo cáo doanh thu có bắt buộc khoảng ngày không? (đổi code hay sửa kịch bản)
2. **P1-04**: giữ regex biển số hiện tại → sửa dữ liệu mẫu trong kịch bản (không dùng dấu `-`).
3. **P1-07**: có chặn voucher hạn đã qua khi tạo không?
4. **P1-08**: nhân viên không sdt: cho lưu hay chặn (migration `V1` NULL vs `init.sql` NOT NULL) và không được văng lỗi.
5. **P2-06**: hủy phiếu đã có hóa đơn thì hóa đơn/DB phải thế nào?
6. **P2-12**: có giới hạn độ dài nội dung khiếu nại? Danh sách phiếu SCR-16 có phải rỗng đến khi tra sdt (có vẻ đúng thiết kế)?
7. **P3-08**: cập nhật hóa đơn có cần cập nhật lại thời gian lập? (ghi chú của Khang bị cụt câu, hỏi lại)
8. **P3-02**: có cần popup xác nhận khi xóa dòng chi tiết phiếu nhập?
9. **P1-01**: xác nhận ngoài phạm vi (phân quyền) → đổi PASS.

## 5. Gom lỗi theo hướng sửa (gợi ý nhánh fix)

| Nhóm | Mã liên quan | Ý chính |
|---|---|---|
| Lỗi dữ liệu | P1-05, P2-06 | Tồn kho ghi 0 khi thêm/sửa phụ tùng; hủy phiếu có hóa đơn không đồng bộ |
| Validation chỉ ra console | P1-05, P1-06 | Hiển thị lỗi lên UI qua i18n |
| Thông báo sai ngữ cảnh | P3-02, P3-06, P3-10 | Sửa message vượt tồn / SL ≤ 0 / hóa đơn đã hủy; khóa icon xóa dòng |
| Ô số nhận chữ + nhãn cắt | P3-04, P3-11 | Chặn ký tự chữ ở ô lọc tổng; mở rộng cột/nhãn |
| i18n | P4-14 | Enum ca trong bảng theo ngôn ngữ |
| Liên lane | P1-12 | SCR-05 không chọn được nhân viên mới tạo |

## 6. Hồ sơ test còn thiếu (README bắt buộc)

- Mọi mục `NO` đều **chưa theo mẫu 7 dòng** (Bước lỗi / Dữ liệu / Mong đợi / Thực tế / Tái hiện / Bằng chứng / Mức độ) → theo README sẽ bị trả lại. Nặng nhất: P1, P2, P4.
- Ô **Commit `develop`** trống ở P1, P4 (P3 ghi `836beb3`, P2 ghi "chưa ghi nhận").
- **P4-09** thiếu 3 con số; **P4-13** cần retest và có bảng số liệu trước/sau.
- Thứ tự chạy P1→P4 chưa được tuân thủ: P4 chạy 26/10, trước P3 (27/10).
- Chưa có bằng chứng (ảnh/log/SQL) ở hầu hết mục NO.

## 7. Bước tiếp theo

1. Chốt §4 → phân loại từng NO thành *bug* hay *sửa kịch bản*.
2. Yêu cầu tester bổ sung mẫu mô tả cho các NO còn lại.
3. Sửa lỗi theo §5, mỗi lỗi một commit `fix(<lane>): ...` trên `fix/e2e-<mã>`.
4. Retest, ưu tiên P4-13 sau khi P1–P3 đã ổn.