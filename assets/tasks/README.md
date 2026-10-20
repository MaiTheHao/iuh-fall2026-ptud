# Kế hoạch tuần 1 — Gara Ô tô Lúa

**Cập nhật:** 21/10/2026, đối chiếu trực tiếp với `src/iuh/fit`, `src/resources/i18n_*.json` và `infra/migrations`.

**Chú giải:** `[DONE]` đã chạy trên dữ liệu thật · `[PARTIAL]` một phần đã có mã · `[TODO]` mới có khung giữ chỗ.

## 1. Thành viên và phân công

| Nhóm | Người phụ trách | Vai trò | Nhánh |
|---|---|---|---|
| Tích hợp chung | Mai Thế Hào | Nhóm trưởng; review, tích hợp, xử lý xung đột tệp dùng chung | `develop` |
| P1 — Tổ chức | Nguyễn Minh Quốc | Dữ liệu nền | `feat/p1-to-chuc` |
| P2 — Tiếp nhận và khiếu nại | Trần Nguyễn Hữu Thắng | Tiếp nhận xe, chẩn đoán, khiếu nại | `feat/p2-tiep-nhan-khieu-nai` |
| P3 — Nhập phụ tùng và thanh toán | Nguyễn Trần Trọng Khang | Phiếu nhập, hóa đơn, đổi mật khẩu | `feat/p3-nhap-thanh-toan` |
| P4 — Kế hoạch và tổng kết | Đặng Thanh Bình | Phân công ca, lịch làm việc, báo cáo | `feat/p4-ke-hoach-tong-ket` |

## 2. Trạng thái theo nhóm

| Kế hoạch | Màn | Trạng thái | Việc còn lại |
|---|---|---|---|
| [P1 — Tổ chức](./2026-10-14-p1-to-chuc.md) | 6 | `[DONE]` 6/6 | Không còn |
| [P2 — Tiếp nhận và khiếu nại](./2026-10-14-p2-tiep-nhan-khieu-nai.md) | 6 | `[PARTIAL]` 4/6 | SCR-04C, phần lập chẩn đoán của SCR-04D |
| [P3 — Nhập phụ tùng và thanh toán](./2026-10-14-p3-nhap-thanh-toan.md) | 5 | `[PARTIAL]` 2/5 | SCR-10, SCR-10B, SCR-15 |
| [P4 — Kế hoạch và tổng kết](./2026-10-14-p4-ke-hoach-tong-ket.md) | 6 | `[TODO]` 0/6 | Toàn bộ 6 màn |

Sơ đồ phụ thuộc và bản đồ màn hình: [`DGRAPH.md`](./DGRAPH.md).

## 3. Nền tảng đã có

`LoginPage`, `DashboardPage`, `Layout.goTo(Class)`; `ManagementPanel`, `ManagementFormPanel`, `ThaoTac`, `SuaDialog`, `ThongBao`; `I18nEnum` + `BoHienThiI18n` cho nhãn enum, `PaginatedResult`, `DanhSach*Criteria`; `TransactionScope`; `MaGeneratorUtil` + `MaRetryUtil`; `TextNormalizerUtil`, `NumberParseUtil`; `BoChonNgayGio`, `AnhField`, `ChonPhuTungDialog`; i18n `src/resources/i18n_vn.json` + `i18n_en.json` (402 key mỗi tệp, cùng tập key).

## 4. Quy ước chung

Luồng bắt buộc: `GUI → SERVICE → DAO → DB`. GUI không gọi JDBC/DAO; DAO không chứa quy tắc nghiệp vụ và trả `Optional` khi tìm một bản ghi; nghiệp vụ ghi qua nhiều DAO mở `TransactionScope` và truyền scope xuống DAO.

Giao diện dùng kit chung; văn bản lấy từ i18n; enum cài `I18nEnum` và hiển thị bằng `getI18NBusinessName()`/`BoHienThiI18n`; thêm key phải cập nhật đồng thời `i18n_vn.json` và `i18n_en.json`.

Mỗi nhóm làm trên nhánh riêng tách từ `develop`, commit theo Conventional Commits, PR ngược lại `develop`; không commit trực tiếp lên `develop`/`master`. Sửa tệp dùng chung (`PageRegistry`, `i18n_*.json`, `AuthService`) thì báo nhóm trưởng.

## 5. Phụ thuộc

- **P1 → P2/P3/P4:** `[DONE]` service dữ liệu nền đã sẵn sàng.
- **P2 → P3:** `[DONE]` `PhieuTiepNhanService` đã có; P3 không chờ phần chẩn đoán.
- **P3 → P4:** `[PARTIAL]` phiếu nhập đã xong, hóa đơn chưa có service; P4 vẫn làm được trên dữ liệu mẫu `V4__mock_data.sql`.

## 6. Việc còn lại

| Lane | Việc | Chặn ai |
|---|---|---|
| P2 | `VanDeKyThuatDAO` + `VanDeKyThuatService`; biểu mẫu lập chẩn đoán cho SCR-04D; SCR-04C + điều hướng từ SCR-04B | Không chặn lane khác |
| P3 | `HoaDonService` + phần ghi của `HoaDonDAO`; SCR-10, SCR-10B; SCR-15 đổi mật khẩu | Chặn báo cáo doanh thu của P4 |
| P4 | Phân công ca, hai màn lịch, ba báo cáo | — |

## 7. Tiêu chí hoàn thành một màn

Đã đăng ký trong `PageRegistry` và mở được từ đúng menu; đọc/ghi DB qua đủ ba tầng; luồng chính chạy đầu-cuối; dữ liệu đầu vào được kiểm tra và báo lỗi rõ; văn bản từ i18n, enum hiển thị nhãn tiếng Việt; `javac` sạch và ứng dụng khởi động được.
