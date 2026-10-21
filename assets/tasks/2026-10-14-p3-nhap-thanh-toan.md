# P3 — Nhập phụ tùng và thanh toán

**Trạng thái:** `[PARTIAL]` — 4/5 màn, cập nhật 21/10/2026.

- **Lane:** L3 — Nhập phụ tùng & Thanh toán · **Phụ trách:** Nguyễn Trần Trọng Khang · **Nhánh:** `feat/p3-nhap-thanh-toan` · **Review:** Mai Thế Hào

## 1. Màn hình

| Màn | Trạng thái | Ghi chú |
|---|---|---|
| SCR-07 Lập phiếu nhập | `[DONE]` | `LapPhieuNhapPage` + `PhieuNhapPhuTungService`: nhiều dòng, chọn phụ tùng, tổng chi phí; ghi đầu phiếu, chi tiết và tăng tồn trong cùng `TransactionScope` |
| SCR-07B Quản lý phiếu nhập | `[DONE]` | Danh sách, lọc, khoảng ngày, phân trang, xóa, mở màn lập phiếu |
| SCR-10 Lập hóa đơn | `[DONE]` | `LapHoaDonPage` + `HoaDonService.create/capNhat`: chọn phiếu, thêm dịch vụ/phụ tùng, gộp dòng trùng, áp voucher, trừ tồn trong `TransactionScope`, tính lại tổng tiền |
| SCR-10B Quản lý hóa đơn | `[DONE]` | `QuanLyHoaDonPage`: danh sách + lọc + phân trang, mở hóa đơn, hủy hóa đơn chờ thanh toán kèm lý do; dùng `HoaDonService.search` + `HoaDonView` |
| SCR-15 Đổi mật khẩu | `[TODO]` | Trang giữ chỗ; `AuthService.updatePassword` chưa kiểm tra mật khẩu hiện tại |

Ngoài phạm vi: xuất Excel, giảm giá nâng cao, badge màu, bộ lọc nâng cao mọi cột, hoàn thiện bản dịch tiếng Anh.

## 2. Việc còn lại: hóa đơn và thanh toán

Schema đã có: `HoaDon(maHoaDon, maPhieuTiepNhan, maVoucher NULL, trangThaiHoaDon, lyDoHuy, ghiChu, ngayTao, thoiGianThanhToan NULL)` và `ChiTietHoaDon(maHoaDon, loaiSanPham, maSanPham, soLuong, donGia)`; `loaiSanPham` nhận `DICH_VU`/`PHU_TUNG`; `Voucher.mucGiam` là phân số 0–1 (`decimal(5,4)`). `HoaDonDAO` hiện chỉ đọc (`findById`, `findAll`, `findAllWithDetails`).

Cần làm:

1. **`HoaDonDAO`** — `insert(TransactionScope, HoaDon, List<ChiTietHoaDon>)`, `findByPhieuTiepNhan(maPhieuTiepNhan)`, cập nhật trạng thái + `thoiGianThanhToan`; DAO không tính tổng tiền.
2. **`HoaDonService`** — lập hóa đơn từ phiếu tiếp nhận qua `PhieuTiepNhanService`; thành tiền dòng = số lượng × đơn giá, tổng = tổng các dòng rồi áp `mucGiam` của voucher còn hiệu lực và đang kích hoạt; trạng thái ban đầu `CHO_THANH_TOAN`.
3. **Chặn hóa đơn thứ hai** — DB không có ràng buộc duy nhất trên `maPhieuTiepNhan`; nếu phiếu đã có hóa đơn thì trả về hóa đơn hiện có, không tạo bản ghi mới. Kiểm tra trong service là bắt buộc.
4. **Thanh toán** — chỉ chuyển `DA_THANH_TOAN` và ghi `thoiGianThanhToan` khi xác nhận thành công; lưu đầu + chi tiết trong cùng một `TransactionScope`.
5. **SCR-10/10B** — thay trang giữ chỗ bằng `ManagementPanel`/`ManagementFormPanel`; trạng thái hiển thị nhãn tiếng Việt (`CHO_THANH_TOAN`, `DA_THANH_TOAN`, `DA_HUY` đã có key); nhóm key `app.hoaDon` chưa tồn tại nên phải bổ sung cả hai tệp ngôn ngữ. Mã hóa đơn dùng `MaGeneratorUtil` + `MaRetryUtil`, tiền tố `HD`.

## 3. Việc còn lại: đổi mật khẩu

`AuthService.updatePassword(NhanVien)` mới kiểm tra độ mạnh (≥ 8 ký tự, có chữ, số, ký tự đặc biệt) rồi ghi; **chưa xác minh mật khẩu hiện tại**.

- Service nhận mật khẩu hiện tại, mật khẩu mới, xác nhận; lấy người dùng từ `AppContext`; so khớp mật khẩu hiện tại (qua `NhanVienDAO.login` hoặc tương đương); kiểm tra hai trường mới trùng nhau; dùng lại chính sách độ mạnh đang có; chỉ ghi khi mọi kiểm tra hợp lệ.
- SCR-15: ba trường nhập, thông báo qua i18n (nhóm `app.doiMatKhau` chưa có), xóa nội dung sau khi thành công.
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
- [ ] Đổi mật khẩu kiểm tra đúng mật khẩu hiện tại và độ mạnh.
