# P2 — In hóa đơn và xuất Excel

**Dành cho:** Quốc · **Trạng thái:** ĐÃ DUYỆT · **Ước lượng:** 1,5 ngày

## Mục tiêu

Chọn một hóa đơn trong màn Quản lý hóa đơn và bấm **In hóa đơn** (ra giấy, 1 trang A4) hoặc **Xuất Excel** (1 file có bố cục: tiêu đề, thông tin, dòng hàng, tổng, thanh toán).

## Hiện trạng và khoảng trống

- `HoaDonService.getById(maHoaDon)` trả hóa đơn **kèm dòng hàng**, nhưng header là stub: `PhieuTiepNhan`/`Voucher` chỉ có mã. Muốn in tên khách, SĐT, biển số, nhân viên thì phải nạp thêm.
- `ThanhToanDAO` **chỉ có insert**, chưa có truy vấn theo hóa đơn ⇒ phương thức/thời gian/nhân viên thu chưa lấy được.
- `QuanLyHoaDonPage` chỉ `add(panel, CENTER)`, chưa có thanh nút.
- `XlsxExportUtil.write(...)` chỉ xuất bảng phẳng; hóa đơn cần nhiều khối.
- Không có thư viện PDF; in qua `java.awt.print`.

## Việc cần làm

### 1. Gom dữ liệu — `src/iuh/fit/util/HoaDonInData.java`

- Record gồm: `HoaDon`, `PhieuTiepNhan` (khách hàng, biển số, nhân viên), danh sách dòng in (tên, loại, số lượng, đơn giá, thành tiền), tổng gốc, mức giảm, thực thu, `Optional<ThanhToan>`.
- Static factory `tao(String maHoaDon)`:
  - Lấy hóa đơn bằng `HoaDonService.getById`.
  - Nạp phiếu bằng `PhieuTiepNhanService.getById` để có khách/biển số/nhân viên.
  - Lấy tên dòng bằng đúng service mà `LapHoaDonPage.napChiTiet` đang dùng (PhuTungService / DichVuService).
  - Tính tiền bằng `TinhTienUtil` (tổng gốc, mức giảm, thực thu).
  - Lấy thanh toán bằng `ThanhToanService.timTheoHoaDon`.

### 2. Truy vấn thanh toán

- `ThanhToanDAO`: thêm `findByHoaDon(String maHoaDon)`, join `NhanVien` để lấy họ tên.
- `ThanhToanService`: thêm `Optional<ThanhToan> timTheoHoaDon(String maHoaDon)`.
- Hóa đơn chưa thanh toán → `Optional.empty()`, không ném.

### 3. Renderer — `src/iuh/fit/util/HoaDonRenderer.java`

- A4 dọc, lề 15 mm, vẽ theo `Graphics2D`, toạ độ point.
- Sáu khối theo thứ tự: (1) tên cửa hàng + "HÓA ĐƠN THANH TOÁN", (2) mã hóa đơn + ngày tạo, (3) khách hàng + SĐT + biển số, (4) bảng 5 cột Loại/Tên/Số lượng/Đơn giá/Thành tiền, (5) tổng gốc / giảm giá / thực thu, (6) khối thanh toán.
- Không có thanh toán → khối 6 ghi "Chưa thanh toán".
- Tên hàng dài: cắt ellipsis trong một dòng, không đẩy sang cột khác.
- Font logical `Dialog`/`SansSerif`; chỉ đen và xám đậm; số tiền có dấu phân cách nghìn.
- Thêm `xemTruoc(HoaDonInData)` trả ảnh để tự kiểm tra nhanh.

### 4. In — `src/iuh/fit/util/InHoaDonUtil.java`

- `PrinterJob` + `printDialog`; người dùng hủy thì im lặng.
- `Printable` trả `PAGE_EXISTS` ở trang 0 và `NO_SUCH_PAGE` với mọi `pageIndex > 0`.
- `PrinterException` hoặc không có máy in → `ThongBao.loi` với key lỗi in.

### 5. Excel — `src/iuh/fit/util/HoaDonXlsxUtil.java`

- Một sheet tên `HoaDon-<maHoaDon>` (đã lọc ký tự cấm, tối đa 31 ký tự).
- Dùng fastexcel trực tiếp: tiêu đề merge, khối thông tin, header bảng, dòng hàng, khối tổng, khối thanh toán.
- Ô tiền ghi kiểu số, định dạng `#,##0`; ngày giờ dùng `DateFormats`.
- `IOException` khi file đang mở → ném lên UI để báo "đóng file rồi thử lại".

### 6. Thanh nút trong `QuanLyHoaDonPage`

- Thêm `JPanel` FlowLayout.RIGHT ở SOUTH với hai nút **In hóa đơn** / **Xuất Excel**, theo đúng kiểu `DoanhThuPage.taoThanhCongCu`. Không sửa `ManagementPanel`.
- Dòng đang chọn: `panel.getSelectedRow()`; `-1` hoặc vượt `danhSachHienTai` → `ThongBao.canhBao` và dừng.
- Nạp dữ liệu: `HoaDonInData.tao(danhSachHienTai.get(dong).maHoaDon())`.
- Lỗi: `IllegalArgumentException` → `ThongBao.loi(e)`; `SQLException` → `app.login.errorSystem`.
- Xuất Excel: `JFileChooser` mặc định `HoaDon-<maHoaDon>.xlsx`, tự thêm đuôi `.xlsx`.
- Key i18n mới: `app.hoaDon.inHoaDon`, `.xuatHoaDon`, `.chuaChonDong`, `.inLoi`, `.xuatThanhCong`, `.xuatLoi`.

## Kiểm thử tay

1. Chọn hóa đơn **đã thanh toán** → In → "Microsoft Print to PDF" → đúng 1 trang, đủ 6 khối, có phương thức + thời gian + nhân viên.
2. Chọn hóa đơn **chưa thanh toán** → In → khối thanh toán ghi "Chưa thanh toán".
3. Hủy hộp thoại in → không có thông báo lỗi, dữ liệu không đổi.
4. Xuất Excel → mở file, ô tiền căn phải, tổng khớp hóa đơn.
5. Mở file Excel rồi xuất lại → báo "đóng file rồi thử lại", app không crash.
6. Chưa chọn dòng nào → bấm In/Xuất → chỉ hiện cảnh báo.

## Bẫy cần tránh

- Chỉ số dòng lấy từ bảng là **view index**; nếu sau này bật `RowSorter` thì phải `convertRowIndexToModel` như `ThaoTacTableCellRenderer`.
- `HoaDonService.getById` không có tên khách/nhân viên — quên nạp phiếu là bản in ra trống.
- Đừng dùng `JTable.print` cho hóa đơn: chỉ ra bảng thô, không có tiêu đề/tổng.
- Font thiếu glyph tiếng Việt → ô vuông; thử bản in thật, không chỉ xem preview.

## Xong khi

- 6 mục kiểm thử trên đều đạt, build 0 lỗi, file Excel mở được trên Excel hoặc LibreOffice.
