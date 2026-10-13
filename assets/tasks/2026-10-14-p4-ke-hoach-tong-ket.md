# P4 — Kế hoạch và tổng kết

## 1. Mục tiêu

Hoàn thiện chức năng phân công ca, xem lịch làm việc theo tuần và ba báo cáo: doanh thu, tiêu thụ phụ tùng, tiêu thụ dịch vụ. Dữ liệu phải lấy từ DB và đi qua `GUI → SERVICE → DAO`.

## 2. Phụ trách và phạm vi

- **Lane:** L4 — Kế hoạch & Tổng kết
- **Phụ trách:** Đặng Thanh Bình
- **Nhánh:** `feat/p4-ke-hoach-tong-ket`
- **Review và tích hợp:** Mai Thế Hào
- **Màn hình:** SCR-05 Phân công ca; SCR-05B Lịch của tôi; SCR-05C Lịch của shop; SCR-12 Doanh thu; SCR-12B Tiêu thụ phụ tùng; SCR-12C Tiêu thụ dịch vụ.

Trong đợt này, báo cáo tập trung vào bảng và số liệu. Biểu đồ, Top 20, lọc theo khoảng ngày tùy chọn, badge màu và hoàn thiện toàn bộ bản dịch tiếng Anh chưa thuộc phạm vi.

## 3. Cách giải quyết

### 3.1. Phân công ca

SCR-05 cho phép tìm nhân viên theo mã/tên, lọc theo vai trò, chọn tuần/ngày/ca, ghi chú và gán ca. Danh sách nhân viên lấy qua `NhanVienService` của P1.

Service kiểm tra trùng theo tổ hợp nhân viên, năm, tháng, ngày và ca làm việc. Nếu đã có phân công cùng tổ hợp, từ chối thao tác và trả thông báo nghiệp vụ dễ hiểu. Mã phân công được sinh theo quy ước hiện hành. Service cũng cung cấp dữ liệu theo tuần và theo nhân viên để các màn lịch dùng chung.

### 3.2. Lịch làm việc

- **Lịch của tôi (SCR-05B):** chỉ đọc lịch của nhân viên đang đăng nhập.
- **Lịch của shop (SCR-05C):** chỉ đọc lịch của toàn bộ nhân viên trong tuần được chọn.
- Cả hai màn dùng bố cục từ thứ Hai đến Chủ nhật, ba ca trong ngày, có điều hướng tuần trước/tuần này/tuần sau. Màn lịch của tôi hiển thị tổng số ca; lịch shop hiển thị tổng số phân công.

Các màn lịch không tự truy vấn DB. Dữ liệu được lấy qua `PhanCongService`.

### 3.3. Báo cáo

`BaoCaoDAO` cung cấp dữ liệu thô từ các bảng liên quan; `BaoCaoService` chịu trách nhiệm tổng hợp. DAO không thực hiện phép tổng hợp nghiệp vụ như tính doanh thu hay cộng lượng tiêu thụ.

- **Doanh thu (SCR-12):** chọn tháng/năm; hiển thị các hóa đơn thuộc kỳ và tổng doanh thu từ chi tiết của hóa đơn đã thanh toán. Có thể phân tách doanh thu dịch vụ và phụ tùng bằng số liệu.
- **Tiêu thụ phụ tùng (SCR-12B):** hiển thị toàn bộ phụ tùng cùng lượng nhập, lượng xuất theo hóa đơn, tồn hiện tại, đơn giá và doanh thu trong kỳ.
- **Tiêu thụ dịch vụ (SCR-12C):** hiển thị từng dịch vụ, số lần sử dụng, đơn giá và doanh thu trong kỳ.

Báo cáo phải nhất quán với trạng thái hóa đơn thật (`DA_THANH_TOAN`) và dữ liệu tồn kho hiện có. Trong phạm vi hiện tại không thêm thư viện biểu đồ; giao diện tập trung vào bảng và số liệu.

## 4. Tệp nằm trong phạm vi

| Nhóm | Tệp hoặc khu vực |
|---|---|
| Giao diện lịch | `PhanCongCaPage.java`, `LichCuaToiPage.java`, `LichCuaShopPage.java` |
| Giao diện báo cáo | `DoanhThuPage.java`, `TieuThuPhuTungPage.java`, `TieuThuDichVuPage.java` |
| Nghiệp vụ | `PhanCongService.java`, `BaoCaoService.java` |
| Truy cập dữ liệu | `PhanCongDAO.java`, `BaoCaoDAO.java` |
| Ngôn ngữ | `i18n_vn.json`, `i18n_en.json` — chỉ bổ sung key thuộc P4 |

Các tệp Java nằm dưới `src/iuh/fit/gui/page/`, `src/iuh/fit/service/` và `src/iuh/fit/dao/` theo đúng tầng.

## 5. Phụ thuộc và lưu ý tích hợp

- P1 cung cấp `NhanVienService`.
- P3 cung cấp dữ liệu phiếu nhập, hóa đơn và chi tiết hóa đơn. Hợp đồng dữ liệu cần được thống nhất trước khi tích hợp báo cáo.
- Có thể dùng `BaoCaoDAO` riêng để đọc dữ liệu báo cáo, tránh mở rộng phạm vi thay đổi DAO của P3. Việc đọc trùng một số bảng được chấp nhận trong đợt này, nhưng cần giữ truy vấn và phép tính nhất quán.
- `PhanCong` hiện chưa có ràng buộc duy nhất ở DB; kiểm tra trùng trong service là bắt buộc.
- Không bổ sung thư viện hoặc công cụ build mới chỉ để tạo biểu đồ.

## 6. Kiểm tra hoàn thành

- [ ] SCR-05 gán ca thành công và từ chối phân công trùng nhân viên/ngày/ca.
- [ ] SCR-05B hiển thị đúng lịch tuần của người đang đăng nhập.
- [ ] SCR-05C hiển thị lịch tuần của toàn shop và danh sách nhân viên theo từng ca.
- [ ] SCR-12 hiển thị doanh thu của tháng được chọn, chỉ tính hóa đơn đã thanh toán.
- [ ] SCR-12B hiển thị số liệu nhập/xuất/tồn và doanh thu phụ tùng; tồn khớp dữ liệu phụ tùng hiện tại.
- [ ] SCR-12C hiển thị số lần sử dụng và doanh thu theo dịch vụ.
- [ ] DAO chỉ cung cấp dữ liệu; tổng hợp báo cáo được thực hiện trong service.
- [ ] Enum hiển thị bằng nhãn tiếng Việt; i18n hai ngôn ngữ có cùng tập key.
- [ ] Toàn bộ mã nguồn biên dịch được và ứng dụng khởi động không có lỗi ngoài dự kiến.

## 7. Rủi ro và cách xử lý

- **Hợp đồng P3 thay đổi:** thống nhất dữ liệu đầu vào/đầu ra trước khi hoàn thiện báo cáo; kiểm tra kết quả với hóa đơn đã thanh toán.
- **Trùng phân công:** kiểm tra ở service trước khi ghi; cân nhắc ràng buộc DB trong một đợt riêng nếu cần bảo đảm đồng thời ở nhiều phiên.
- **SQL báo cáo trùng với P3:** giới hạn `BaoCaoDAO` ở nhu cầu đọc báo cáo, không chỉnh DAO của P3 nếu không có thỏa thuận tích hợp.
- **Chưa có thư viện biểu đồ:** giữ báo cáo dạng bảng/số liệu, không thêm phụ thuộc mới.
