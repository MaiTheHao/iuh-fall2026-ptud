# P2 — Tiếp nhận và khiếu nại

## 1. Mục tiêu

Hoàn thiện luồng tiếp nhận xe, quản lý phiếu tiếp nhận, lập/xem chẩn đoán kỹ thuật và tiếp nhận/quản lý khiếu nại. Toàn bộ nghiệp vụ chạy qua `GUI → SERVICE → DAO` và sử dụng dữ liệu nền do P1 cung cấp.

## 2. Phụ trách và phạm vi

- **Lane:** L2 — Tiếp nhận & Khiếu nại
- **Phụ trách:** Trần Nguyễn Hữu Thắng
- **Nhánh:** `feat/p2-tiep-nhan-khieu-nai`
- **Review và tích hợp:** Mai Thế Hào
- **Màn hình:** SCR-04 Tiếp nhận khách hàng; SCR-04B Quản lý phiếu tiếp nhận; SCR-04C Xem chẩn đoán; SCR-04D Lập chẩn đoán; SCR-16 Tiếp nhận khiếu nại; SCR-16B Quản lý khiếu nại.

Phạm vi gồm nghiệp vụ phiếu tiếp nhận, vấn đề kỹ thuật và khiếu nại; migration cho bảng khiếu nại; nhãn giao diện và kết nối các màn với service.

Ngoài phạm vi đợt này: đính kèm ảnh, bộ lọc nâng cao trên mọi cột, hiệu ứng chuyển màn, badge màu và hoàn thiện toàn bộ bản dịch tiếng Anh.

## 3. Cách giải quyết

### 3.1. Luồng tiếp nhận xe

Người dùng tra cứu khách hàng bằng số điện thoại và phương tiện bằng biển số. Giao diện lấy dữ liệu qua `KhachHangService` và `XeService` của P1, sau đó gửi thông tin hợp lệ đến `PhieuTiepNhanService`. Service kiểm tra các tham chiếu cần thiết, sinh mã phiếu theo quy ước hiện có và gán trạng thái ban đầu là `TIEP_NHAN`.

SCR-04B hiển thị danh sách phiếu và trạng thái bằng nhãn tiếng Việt. Không sao chép logic tra cứu khách hàng hoặc xe sang P2.

### 3.2. Chẩn đoán kỹ thuật

Chẩn đoán gắn với một phiếu tiếp nhận. Service chịu trách nhiệm kiểm tra nội dung và mức độ ưu tiên, tạo mã vấn đề và lưu thông tin đồng ý của khách hàng. Mức độ ưu tiên nằm trong khoảng 1–100; tiêu đề không được để trống. Nếu trạng thái đồng ý chưa được xác định, biểu diễn bằng `CHUA_XAC_NHAN`.

SCR-04D hỗ trợ chọn phiếu rồi lập chẩn đoán. SCR-04C chỉ dùng để xem các chẩn đoán của phiếu đã chọn; đây là màn phụ, không xuất hiện trong menu chính và chỉ mở từ thao tác tương ứng ở SCR-04B.

### 3.3. Khiếu nại

Bổ sung bảng `KhieuNai` bằng migration mới, có liên kết đến khách hàng và phiếu tiếp nhận. Nội dung khiếu nại là bắt buộc; trạng thái ban đầu là `CHUA_XU_LY`; ngày tạo do hệ thống ghi nhận.

`KhieuNaiService` xử lý tạo, tìm kiếm, cập nhật và xóa theo nhu cầu nghiệp vụ. SCR-16 dùng service P1 để chọn khách hàng và cho phép gắn khiếu nại với phiếu tiếp nhận. SCR-16B hiển thị danh sách và cho phép cập nhật trạng thái giữa `CHUA_XU_LY`, `DANG_XU_LY` và `DA_XU_LY`.

### 3.4. Ranh giới ba tầng

- **GUI:** điều phối tương tác, hiển thị lỗi và nhãn; không gọi JDBC/DAO.
- **Service:** kiểm tra dữ liệu, áp dụng quy tắc nghiệp vụ, phối hợp các service/DAO.
- **DAO:** đọc/ghi và ánh xạ dữ liệu; không tự chứa quy tắc nghiệp vụ.
- **Entity/enum:** phản ánh dữ liệu và trạng thái nghiệp vụ; nhãn hiển thị lấy từ i18n/`EnumLabels`.

## 4. Tệp nằm trong phạm vi

| Nhóm | Tệp hoặc khu vực |
|---|---|
| Giao diện | `TiepNhanKhachHangPage.java`, `QuanLyPhieuTiepNhanPage.java`, `QuanLyChanDoanPage.java`, `LapChanDoanPage.java`, `TiepNhanKhieuNaiPage.java`, `QuanLyKhieuNaiPage.java` trong `src/iuh/fit/gui/page/` |
| Nghiệp vụ | `PhieuTiepNhanService.java`, `VanDeKyThuatService.java`, `KhieuNaiService.java` |
| Truy cập dữ liệu | `PhieuTiepNhanDAO.java`, `VanDeKyThuatDAO.java`, `KhieuNaiDAO.java` |
| Mô hình | `KhieuNai.java`, `TrangThaiKhieuNai.java` |
| Cơ sở dữ liệu | `infra/migrations/V3__add_khieu_nai.sql` |
| Điều hướng và ngôn ngữ | `PageRegistry.java`; `i18n_vn.json`, `i18n_en.json` — chỉ bổ sung phần thuộc P2 |

## 5. Phụ thuộc và lưu ý tích hợp

- Cần các service P1: khách hàng, xe và nhân viên khi luồng tương ứng sử dụng.
- P3 cần `PhieuTiepNhanService` để lập hóa đơn từ phiếu tiếp nhận. Hợp đồng service cần được thống nhất trước khi tích hợp.
- SCR-04C phải được đăng ký để điều hướng nội bộ nhưng không được đưa vào menu chính.
- Trước khi triển khai DAO chẩn đoán, xác minh bảng `VanDeKyThuat` đã tồn tại và migration khởi tạo DB được áp dụng thành công.
- Migration khiếu nại phải được áp dụng ở môi trường DB trước khi kiểm thử nghiệp vụ.

## 6. Kiểm tra hoàn thành

- [ ] SCR-04 tra cứu được khách hàng/xe và lưu được phiếu tiếp nhận mới.
- [ ] SCR-04B hiển thị phiếu và trạng thái bằng nhãn tiếng Việt.
- [ ] Từ SCR-04B có thể mở SCR-04C để xem chẩn đoán của đúng phiếu; SCR-04C không xuất hiện trong menu.
- [ ] SCR-04D tạo được chẩn đoán với tiêu đề hợp lệ và mức ưu tiên từ 1–100; dữ liệu xem lại được ở SCR-04C.
- [ ] SCR-16 tạo được khiếu nại gắn với khách hàng và phiếu tiếp nhận.
- [ ] SCR-16B hiển thị khiếu nại và cập nhật được trạng thái hợp lệ.
- [ ] Migration tạo bảng `KhieuNai` thành công; i18n hai ngôn ngữ có cùng tập key.
- [ ] Toàn bộ mã nguồn biên dịch được và ứng dụng khởi động không có lỗi ngoài dự kiến.

## 7. Rủi ro và cách xử lý

- **Bảng chẩn đoán chưa tồn tại hoặc schema lỗi:** kiểm tra migration và cấu trúc DB trước khi kết luận lỗi thuộc DAO.
- **Khác biệt tên trường giữa mockup và mô hình thật:** ưu tiên entity/schema hiện có trong dự án; không tạo thêm tên trường chỉ để khớp mockup.
- **Mã chẩn đoán khác ví dụ trong mockup:** dùng cơ chế sinh mã đang được dự án thống nhất và ghi nhận mọi khác biệt cần quyết định sau.
- **Tệp dùng chung:** chỉ thêm phần đăng ký màn và key của P2; tránh ghi đè thay đổi của lane khác.
