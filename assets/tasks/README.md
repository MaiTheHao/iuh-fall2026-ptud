# Kế hoạch tuần 1 — Gara Ô tô Lúa

Tài liệu này tổng hợp kế hoạch công việc theo bốn nhóm phụ trách (P1–P4), các mốc tích hợp và quy ước chung. Nội dung tập trung vào mục tiêu, quy tắc nghiệp vụ, ranh giới trách nhiệm và tiêu chí nghiệm thu; không thay thế tài liệu thiết kế chi tiết hay sơ đồ phụ thuộc.

> Nguyên tắc triển khai: làm cho luồng chính hoạt động ổn định trước, sau đó mới hoàn thiện trải nghiệm và tối ưu hiệu năng. Bộ khung giao diện và tiện ích dùng chung (Wave 0) đã hoàn thành, không cần thực hiện lại.

## 1. Thành viên và phân công

| Nhóm | Người phụ trách | Vai trò | Nhánh |
|---|---|---|---|
| Tích hợp chung | Mai Thế Hào | Nhóm trưởng; review, tích hợp và xử lý xung đột ở tệp dùng chung | Tích hợp vào `develop` |
| P1 — Tổ chức | Nguyễn Minh Quốc | Dữ liệu nền | `feat/p1-to-chuc` |
| P2 — Tiếp nhận và khiếu nại | Trần Nguyễn Hữu Thắng | Tiếp nhận xe, chẩn đoán, khiếu nại | `feat/p2-tiep-nhan-khieu-nai` |
| P3 — Nhập phụ tùng và thanh toán | Nguyễn Trần Trọng Khang | Phiếu nhập, hóa đơn, đổi mật khẩu | `feat/p3-nhap-thanh-toan` |
| P4 — Kế hoạch và tổng kết | Đặng Thanh Bình | Phân công ca, lịch làm việc, báo cáo | `feat/p4-ke-hoach-tong-ket` |

## 2. Kế hoạch theo nhóm

| Kế hoạch | Phạm vi chính | Trạng thái theo tài liệu đầu vào |
|---|---|---|
| [P1 — Tổ chức](./2026-10-14-p1-to-chuc.md) | Khách hàng, xe, phụ tùng, dịch vụ, voucher, nhân viên | Màn Khách hàng đã có bản mẫu; các màn còn lại cần hoàn thiện/kiểm tra |
| [P2 — Tiếp nhận và khiếu nại](./2026-10-14-p2-tiep-nhan-khieu-nai.md) | Phiếu tiếp nhận, chẩn đoán kỹ thuật, khiếu nại | Chưa bắt đầu theo tài liệu đầu vào |
| [P3 — Nhập phụ tùng và thanh toán](./2026-10-14-p3-nhap-thanh-toan.md) | Phiếu nhập, tồn kho, hóa đơn, thanh toán, đổi mật khẩu | Chưa bắt đầu theo tài liệu đầu vào |
| [P4 — Kế hoạch và tổng kết](./2026-10-14-p4-ke-hoach-tong-ket.md) | Phân công ca, lịch tuần, báo cáo | Chưa bắt đầu theo tài liệu đầu vào |

Sơ đồ phụ thuộc và bản đồ màn hình chi tiết nằm trong [`DGRAPH.md`](./DGRAPH.md). Tài liệu này được giữ nguyên, không chỉnh sửa trong lần cập nhật này.

## 3. Nền tảng đã có

Các phần sau được xem là nền dùng chung đã hoàn thành, không đưa lại thành đầu việc riêng trong từng kế hoạch:

- Màn đăng nhập, trang chủ và khung điều hướng chính.
- `ManagementPanel` cho các màn quản lý danh sách.
- `EnumLabels` để hiển thị nhãn tiếng Việt cho giá trị enum.
- Các nhóm i18n dùng chung, DTO `PaginatedResult`, quy ước DAO trả `Optional` khi tìm một bản ghi và bộ chọn ngày `LGoodDatePicker`.
- Màn Khách hàng của P1 là mẫu tham chiếu về hành vi quản lý danh sách; không cần triển khai lại mẫu này.

## 4. Quy ước làm việc chung

### Kiến trúc

Luồng xử lý thống nhất là `GUI → SERVICE → DAO → DB`.

- **GUI** nhận thao tác và hiển thị dữ liệu; không gọi JDBC hoặc DAO trực tiếp.
- **Service** xử lý quy tắc nghiệp vụ, xác thực dữ liệu, điều phối các DAO và service liên quan.
- **DAO** thực hiện truy vấn, ghi dữ liệu và ánh xạ bản ghi; không chứa phép tính hoặc tổng hợp nghiệp vụ.
- Khi tìm một bản ghi, DAO trả `Optional`; không dùng `null` để biểu diễn trường hợp không tìm thấy. Danh sách không có dữ liệu là trạng thái hợp lệ.

### Giao diện và ngôn ngữ

- Tận dụng kit dùng chung thay vì tự tạo thành phần trùng chức năng.
- Nội dung giao diện và thông báo lấy từ i18n; các giá trị enum hiển thị bằng nhãn thân thiện, không hiển thị tên hằng thô.
- Chỉ bổ sung key thuộc phạm vi của nhóm đang làm trong `i18n_vn.json` và `i18n_en.json`. Sau khi chỉnh sửa, kiểm tra hai tệp có cùng tập key.
- Các tệp dùng chung như `PageRegistry.java`, `i18n_*.json` và `AuthService.java` cần được chỉnh sửa có giới hạn, trao đổi trước khi thay đổi hợp đồng mà nhóm khác đang sử dụng.

### Git và tích hợp

Mỗi nhóm làm trên nhánh riêng, tách từ `develop`; không commit trực tiếp lên `develop` hoặc `master`.

```bash
git switch develop
git pull
git switch -c feat/p1-to-chuc
# Thực hiện thay đổi trong phạm vi nhóm
git add -A
git commit -m "feat(org): quan ly du lieu nen"
git push -u origin feat/p1-to-chuc
```

Thay tên nhánh và nội dung commit theo nhóm/thay đổi thực tế. Trước khi mở Pull Request, đồng bộ với `develop` theo quy trình của nhóm. Nhóm trưởng phụ trách tích hợp và xử lý xung đột tại các tệp dùng chung. Commit theo Conventional Commits, ví dụ `feat`, `fix`, `refactor`, `docs`, `test` kèm phạm vi phù hợp.

## 5. Phụ thuộc giữa các nhóm

- **P1 → P2:** khách hàng, xe và nhân viên.
- **P1 → P3:** phụ tùng, dịch vụ, voucher và nhân viên.
- **P1 → P4:** nhân viên.
- **P2 → P3:** phiếu tiếp nhận.
- **P3 → P4:** hóa đơn, chi tiết hóa đơn và dữ liệu phiếu nhập.

Các nhóm cần thống nhất hợp đồng service và kiểu dữ liệu trước khi tích hợp. Không tự viết lại nghiệp vụ đã được nhóm phụ thuộc cung cấp.

## 6. Mốc triển khai

| Thời gian | Trọng tâm |
|---|---|
| 14/10 | Dựng/hoàn thiện khung màn hình trên nền kit dùng chung; P1 ưu tiên cung cấp service sớm |
| 15–17/10 | Hoàn thiện các luồng nghiệp vụ chính và kiểm tra dữ liệu thật |
| 18/10 | Tích hợp liên nhóm, sửa lỗi và xác minh hợp đồng service |
| 19/10 | Đóng băng thay đổi, chạy demo đầu-cuối và ghi nhận kết quả |

## 7. Tiêu chí hoàn thành một màn

- Không còn là trang giữ chỗ và có thể mở từ đúng vị trí điều hướng.
- Hiển thị dữ liệu thật từ DB qua đúng ba tầng.
- Luồng nghiệp vụ chính hoạt động đầu-cuối, bao gồm lưu và đọc lại dữ liệu khi phù hợp.
- Dữ liệu đầu vào được kiểm tra; lỗi được thông báo rõ ràng.
- Văn bản lấy từ i18n; enum hiển thị bằng nhãn tiếng Việt.
- Có kiểm tra build và chạy ứng dụng, đồng thời kiểm tra nghiệp vụ quan trọng bằng dữ liệu DB.