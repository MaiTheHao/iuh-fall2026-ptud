# P3 — Nhập phụ tùng và thanh toán

## 1. Mục tiêu

Hoàn thiện lập/quản lý phiếu nhập phụ tùng, cập nhật tồn kho, lập/quản lý hóa đơn, xác nhận thanh toán và đổi mật khẩu cá nhân. Các luồng nghiệp vụ phải chạy qua `GUI → SERVICE → DAO`.

## 2. Phụ trách và phạm vi

- **Lane:** L3 — Nhập phụ tùng & Thanh toán
- **Phụ trách:** Nguyễn Trần Trọng Khang
- **Nhánh:** `feat/p3-nhap-thanh-toan`
- **Review và tích hợp:** Mai Thế Hào
- **Màn hình:** SCR-07 Lập phiếu nhập; SCR-07B Quản lý phiếu nhập; SCR-10 Lập hóa đơn; SCR-10B Quản lý hóa đơn; SCR-15 Đổi mật khẩu.

Ngoài phạm vi đợt này: xuất Excel, hủy hóa đơn kèm lý do trên giao diện, giảm giá nâng cao, badge màu, bộ lọc nâng cao trên mọi cột và hoàn thiện toàn bộ bản dịch tiếng Anh.

## 3. Cách giải quyết

### 3.1. Phiếu nhập phụ tùng

Người dùng lập phiếu với mô tả lô và một hoặc nhiều dòng phụ tùng. Mỗi dòng có phụ tùng, số lượng, đơn giá nhập và thành tiền; tổng chi phí là tổng thành tiền các dòng. Mã phiếu và thời điểm tạo do hệ thống quản lý.

Service kiểm tra mô tả không rỗng, số lượng lớn hơn 0 và đơn giá không âm. Khi lưu, phần đầu phiếu, các dòng chi tiết và thay đổi tồn kho phải được ghi nhận nhất quán trong cùng một transaction. Nếu thao tác thất bại, không để lại phiếu hoặc tồn kho ở trạng thái dở dang.

DAO phiếu nhập chịu trách nhiệm lưu phần đầu và chi tiết; không cần tạo DAO chi tiết riêng nếu cấu trúc hiện có đã quản lý chi tiết qua DAO cha. Việc tăng tồn được thực hiện thông qua nghiệp vụ tồn kho dùng chung của P1.

SCR-07B hiển thị danh sách phiếu đã lập và cho phép điều hướng về màn lập phiếu theo khả năng hiện có.

### 3.2. Hóa đơn và thanh toán

Hóa đơn được lập từ phiếu tiếp nhận. Người dùng chọn dịch vụ/phụ tùng, số lượng và đơn giá; có thể áp dụng voucher hợp lệ. Thành tiền dòng bằng số lượng nhân đơn giá; tổng tiền được tính từ các dòng và áp dụng mức giảm theo quy tắc voucher hiện hành.

Mỗi phiếu tiếp nhận chỉ có tối đa một hóa đơn. Nếu phiếu đã có hóa đơn, service phải trả về hóa đơn hiện có thay vì tạo bản ghi trùng. Khi tạo mới, trạng thái ban đầu là `CHO_THANH_TOAN`. Chỉ khi xác nhận thanh toán thành công, trạng thái mới chuyển sang `DA_THANH_TOAN` và ghi nhận thời điểm thanh toán. Các trạng thái hiển thị bằng nhãn tiếng Việt, không hiển thị mã enum thô.

Việc lưu hóa đơn và các dòng chi tiết phải dùng chung một transaction để tránh dữ liệu không nhất quán. DAO tập trung vào lưu/đọc dữ liệu; việc tính tổng và kiểm tra quy tắc thuộc service.

### 3.3. Đổi mật khẩu

SCR-15 yêu cầu mật khẩu hiện tại, mật khẩu mới và xác nhận mật khẩu mới. Service xác minh người dùng hiện tại, mật khẩu hiện tại, sự trùng khớp giữa hai trường mật khẩu mới và chính sách độ mạnh đang được `AuthService` áp dụng. Chỉ cập nhật khi toàn bộ kiểm tra hợp lệ. Thông báo thành công/lỗi dùng i18n; sau khi thành công, xóa nội dung các trường nhập.

Trong phạm vi này, chỉ bổ sung nghiệp vụ đổi mật khẩu; không refactor luồng đăng nhập/đăng ký hiện có.

## 4. Tệp nằm trong phạm vi

| Nhóm | Tệp hoặc khu vực |
|---|---|
| Giao diện | `LapPhieuNhapPage.java`, `QuanLyPhieuNhapPage.java`, `LapHoaDonPage.java`, `QuanLyHoaDonPage.java`, `DoiMatKhauPage.java` |
| Nghiệp vụ | `PhieuNhapPhuTungService.java`, `HoaDonService.java`, phần bổ sung `AuthService.java` |
| Truy cập dữ liệu | `PhieuNhapPhuTungDAO.java`, `HoaDonDAO.java` |
| Ngôn ngữ | `i18n_vn.json`, `i18n_en.json` — chỉ bổ sung key thuộc P3 |

Các tệp Java giao diện nằm trong `src/iuh/fit/gui/page/`; service và DAO nằm trong các thư mục tương ứng dưới `src/iuh/fit/`.

## 5. Phụ thuộc và lưu ý tích hợp

- P1 cung cấp `PhuTungService`, `DichVuService`, `VoucherService`, `NhanVienService` và nghiệp vụ tăng tồn kho.
- P2 cung cấp `PhieuTiepNhanService` để lập hóa đơn.
- P4 dùng dữ liệu hóa đơn và phiếu nhập để tổng hợp báo cáo; cần thống nhất hợp đồng dữ liệu/service trước khi tích hợp.
- Trạng thái hóa đơn theo mô hình thật gồm `CHO_THANH_TOAN`, `DA_THANH_TOAN`, `DA_HUY`; không tự thay bằng tên trạng thái trong mockup.
- Việc thay đổi `AuthService` cần giới hạn trong phần đổi mật khẩu và được review vì đây là thành phần dùng chung.

## 6. Kiểm tra hoàn thành

- [ ] Lập phiếu nhập có nhiều dòng; số liệu thành tiền và tổng chi phí đúng.
- [ ] Sau khi lưu phiếu nhập, tồn kho tăng đúng theo tổng số lượng từng phụ tùng.
- [ ] SCR-07B hiển thị phiếu nhập mới.
- [ ] Lập hóa đơn từ phiếu tiếp nhận; tổng tiền và giảm giá voucher được tính đúng.
- [ ] Không tạo hóa đơn thứ hai cho cùng một phiếu tiếp nhận.
- [ ] Xác nhận thanh toán chuyển trạng thái sang `DA_THANH_TOAN` và SCR-10B hiển thị nhãn tiếng Việt.
- [ ] Đổi mật khẩu kiểm tra đúng mật khẩu hiện tại, xác nhận khớp và độ mạnh mật khẩu.
- [ ] Ghi phần đầu và chi tiết phiếu nhập/hóa đơn nhất quán trong transaction; không phát sinh DAO chi tiết riêng không cần thiết.
- [ ] Toàn bộ mã nguồn biên dịch được và ứng dụng khởi động không có lỗi ngoài dự kiến.

## 7. Rủi ro và cách xử lý

- **Thiếu hợp đồng từ P1/P2:** chốt chữ ký service và kiểu dữ liệu trước khi ghép giao diện; không sao chép nghiệp vụ từ lane phụ thuộc.
- **Ghi dữ liệu một phần:** gom phần đầu, chi tiết và cập nhật tồn liên quan vào transaction phù hợp; xử lý rollback khi có lỗi.
- **Thay đổi thành phần xác thực:** chỉ thêm chức năng đổi mật khẩu, không thay đổi ngoài phạm vi các luồng đăng nhập/đăng ký.
- **Tệp i18n dùng chung:** chỉ thêm key thuộc P3, giữ nguyên key của lane khác.
