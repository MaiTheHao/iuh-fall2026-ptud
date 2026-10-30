# P3 — Nhập phụ tùng và thanh toán

**Trạng thái:** `[DONE]` — 5/5 màn, cập nhật 23/10/2026.

- **Lane:** L3 — Nhập phụ tùng & Thanh toán · **Phụ trách:** Nguyễn Trần Trọng Khang · **Nhánh:** `feat/p3-nhap-thanh-toan` · **Review:** Mai Thế Hào

## 1. Màn hình

| Màn | Trạng thái | Ghi chú |
|---|---|---|
| SCR-07 Lập phiếu nhập | `[DONE]` | `LapPhieuNhapPage` + `PhieuNhapPhuTungService`: nhiều dòng, chọn phụ tùng, tổng chi phí; ghi đầu phiếu, chi tiết và tăng tồn trong cùng `TransactionScope` |
| SCR-07B Quản lý phiếu nhập | `[DONE]` | Danh sách, lọc, khoảng ngày, phân trang, xóa, mở màn lập phiếu |
| SCR-10 Lập hóa đơn | `[DONE]` | `LapHoaDonPage` + `HoaDonService.create/capNhat`: chọn phiếu, thêm dịch vụ/phụ tùng, gộp dòng trùng, áp voucher, trừ tồn trong `TransactionScope`, tính lại tổng tiền |
| SCR-10B Quản lý hóa đơn | `[DONE]` | `QuanLyHoaDonPage`: danh sách + lọc + phân trang, mở hóa đơn, xóa hẳn hóa đơn khỏi danh sách (hoàn tồn cho hóa đơn chưa hủy, cảnh báo riêng với hóa đơn đã thanh toán); dùng `HoaDonService.search` + `HoaDonView` |
| SCR-15 Đổi mật khẩu | `[DONE]` | `DoiMatKhauPage`: 3 trường mật khẩu, `AuthService.updatePassword(hienTai, moi, xacNhan)` xác minh mật khẩu hiện tại qua `NhanVienDAO.login`, kiểm tra trùng khớp + độ mạnh, xóa trắng sau khi thành công |

Ngoài phạm vi: xuất Excel, giảm giá nâng cao, badge màu, bộ lọc nâng cao mọi cột, hoàn thiện bản dịch tiếng Anh.

## 2. Hóa đơn và thanh toán (đã xong)

Schema đã có: `HoaDon(maHoaDon, maPhieuTiepNhan, maVoucher NULL, trangThaiHoaDon, lyDoHuy, ghiChu, ngayTao, thoiGianThanhToan NULL, mucGiamApDung decimal(5,4) NULL)` và `ChiTietHoaDon(maHoaDon, loaiSanPham, maSanPham, soLuong, donGia)`; `loaiSanPham` nhận `DICH_VU`/`PHU_TUNG`; `Voucher.mucGiam` là phân số 0–1 (`decimal(5,4)`). `HoaDonDAO` đã có đường ghi (`insert`, `insertChiTiet`, `updateThongTin`, cập nhật thanh toán, `delete`); không còn `findAll`.

Đã làm:

1. [x] **`HoaDonDAO`** — `insert(TransactionScope, HoaDon, List<ChiTietHoaDon>)`, `findByPhieuTiepNhan(maPhieuTiepNhan)`, cập nhật trạng thái + `thoiGianThanhToan`; DAO không tính tổng tiền.
2. [x] **`HoaDonService`** — lập hóa đơn từ phiếu tiếp nhận qua `PhieuTiepNhanService`; thành tiền dòng = số lượng × đơn giá, tổng = tổng các dòng rồi áp `mucGiam` của voucher còn hiệu lực và đang kích hoạt; trạng thái ban đầu `CHO_THANH_TOAN`.
3. [x] **Chặn hóa đơn thứ hai** — DB đã có ràng buộc duy nhất `UQ_HoaDon_maPhieuTiepNhan` trên `maPhieuTiepNhan` (V11); service bắt lỗi 2627/2601 và trả hoá đơn đã có, không tạo bản ghi mới cho cùng một phiếu.
4. [x] **Thanh toán** — chỉ chuyển `DA_THANH_TOAN` và ghi `thoiGianThanhToan` khi xác nhận thành công; lưu đầu + chi tiết trong cùng một `TransactionScope`.
5. [x] **SCR-10/10B** — thay trang giữ chỗ bằng `ManagementPanel`/`ManagementFormPanel`; trạng thái hiển thị nhãn tiếng Việt (`CHO_THANH_TOAN`, `DA_THANH_TOAN`, `DA_HUY` đã có key); nhóm key `app.hoaDon` (44 key) đã bổ sung cả hai tệp ngôn ngữ. Mã hóa đơn dùng `MaGeneratorUtil` + `MaRetryUtil`, tiền tố `HD`.

## 3. Đổi mật khẩu (đã xong)

`AuthService.updatePassword(hienTai, moi, xacNhan)` kiểm tra độ mạnh (≥ 8 ký tự, có chữ, số, ký tự đặc biệt) và đã xác minh mật khẩu hiện tại qua `NhanVienDAO.login` trước khi ghi.

- Service nhận mật khẩu hiện tại, mật khẩu mới, xác nhận; lấy người dùng từ `AppContext`; so khớp mật khẩu hiện tại (qua `NhanVienDAO.login` hoặc tương đương); kiểm tra hai trường mới trùng nhau; dùng lại chính sách độ mạnh đang có; chỉ ghi khi mọi kiểm tra hợp lệ.
- SCR-15: ba trường nhập, thông báo qua i18n (nhóm `app.doiMatKhau` đã có 8 key), xóa nội dung sau khi thành công.
- Không refactor luồng đăng nhập/đăng ký.

## 4. Phụ thuộc

- P1: `PhuTungService` (kèm `increaseStock`), `DichVuService`, `VoucherService`, `NhanVienService` — đã có.
- P2: `PhieuTiepNhanService` — đã có.
- P4: báo cáo doanh thu cần hóa đơn của P3; chốt công thức doanh thu (tổng chi tiết trừ giảm giá voucher) với P4 trước khi hoàn thiện.
- `AuthService` là tệp dùng chung: chỉ thêm phần đổi mật khẩu và báo nhóm trưởng khi review.

## 5. Kiểm tra hoàn thành

- [x] Lập phiếu nhập nhiều dòng, thành tiền và tổng chi phí đúng.
- [x] Tồn kho tăng đúng sau khi lưu phiếu nhập.
- [x] SCR-07B hiển thị phiếu nhập mới.
- [x] Lập hóa đơn từ phiếu tiếp nhận, tổng tiền và giảm giá đúng.
- [x] Không tạo hóa đơn thứ hai cho cùng một phiếu.
- [x] Hủy hóa đơn chờ thanh toán kèm lý do, hoàn tồn phụ tùng; form giữ nguyên và khóa lại.
- [x] Xác nhận thanh toán chuyển `DA_THANH_TOAN`, SCR-10B hiển thị nhãn tiếng Việt.
- [x] Đổi mật khẩu kiểm tra đúng mật khẩu hiện tại và độ mạnh.
