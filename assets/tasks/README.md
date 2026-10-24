# Kế hoạch tuần 1 — Gara Ô tô Lúa

**Cập nhật:** 24/10/2026, đối chiếu trực tiếp với `src/iuh/fit`, `src/resources/i18n_*.json` và `infra/migrations`.

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
| [P2 — Tiếp nhận và khiếu nại](./2026-10-14-p2-tiep-nhan-khieu-nai.md) | 6 | `[DONE]` 6/6 | Không còn |
| [P3 — Nhập phụ tùng và thanh toán](./2026-10-14-p3-nhap-thanh-toan.md) | 5 | `[DONE]` 5/5 | Không còn |
| [P4 — Kế hoạch và tổng kết](./2026-10-14-p4-ke-hoach-tong-ket.md) | 6 | `[DONE]` 6/6 | Không còn |

Tổng: 25/25 màn đã có mã và đăng ký trong `PageRegistry`. P4 đối chiếu mã nguồn + `javac` ngày 24/10/2026; chạy lại end-to-end trên DB trước khi báo cáo.

Sơ đồ phụ thuộc và bản đồ màn hình: [`DGRAPH.md`](./DGRAPH.md).

## 3. Nền tảng đã có

`LoginPage`, `DashboardPage`, `Layout.goTo(Class)`; `ManagementPanel`, `ManagementFormPanel`, `ThaoTac`, `SuaDialog`, `ThongBao`; `I18nEnum` + `BoHienThiI18n` cho nhãn enum, `PaginatedResult`, `DanhSach*Criteria`; `TransactionScope`; `MaGeneratorUtil` + `MaRetryUtil`; `TextNormalizerUtil`, `NumberParseUtil`; `BoChonNgayGio`, `AnhField`, `ChonPhuTungDialog`, `ComboGoiY`; `jfreechart-1.5.6.jar` cho biểu đồ báo cáo; i18n `src/resources/i18n_vn.json` + `i18n_en.json` (600 key mỗi tệp, cùng tập key).

## 4. Quy ước chung

Luồng bắt buộc: `GUI → SERVICE → DAO → DB`. GUI không gọi JDBC/DAO; DAO không chứa quy tắc nghiệp vụ và trả `Optional` khi tìm một bản ghi; nghiệp vụ ghi qua nhiều DAO mở `TransactionScope` và truyền scope xuống DAO.

Giao diện dùng kit chung; văn bản lấy từ i18n; enum cài `I18nEnum` và hiển thị bằng `getI18NBusinessName()`/`BoHienThiI18n`; thêm key phải cập nhật đồng thời `i18n_vn.json` và `i18n_en.json`.

Mỗi nhóm làm trên nhánh riêng tách từ `develop`, commit theo Conventional Commits, PR ngược lại `develop`; không commit trực tiếp lên `develop`/`master`. Sửa tệp dùng chung (`PageRegistry`, `i18n_*.json`, `AuthService`) thì báo nhóm trưởng.

## 5. Phụ thuộc

- **P1 → P2/P3/P4:** `[DONE]` service dữ liệu nền đã sẵn sàng.
- **P2 → P3:** `[DONE]` `PhieuTiepNhanService` đã có; P3 không chờ phần chẩn đoán.
- **P3 → P4:** `[DONE]` phiếu nhập và hóa đơn đã có service; báo cáo của P4 (`DoanhThuService`, `TieuThuPhuTungService`, `TieuThuDichVuService` + DAO chỉ đọc) đọc thẳng `HoaDon`/`ChiTietHoaDon` nên số liệu phát sinh từ SCR-10 vào báo cáo ngay.

## 6. Việc còn lại

Không còn màn nào ở trạng thái `[TODO]`. Việc mở: chốt nghiệp vụ doanh thu gộp hay trừ voucher (`DoanhThuService` đang cộng gộp, `HoaDonService.tinhTongTien` thì trừ), và thống nhất thụt lề cho 25 tệp Java của P4 (đang dùng tab, phần còn lại của repo dùng 4 dấu cách).

## 7. Tiêu chí hoàn thành một màn

Đã đăng ký trong `PageRegistry` và mở được từ đúng menu; đọc/ghi DB qua đủ ba tầng; luồng chính chạy đầu-cuối; dữ liệu đầu vào được kiểm tra và báo lỗi rõ; văn bản từ i18n, enum hiển thị nhãn tiếng Việt; `javac` sạch và ứng dụng khởi động được.
