# E2E C — Phân quyền menu theo vai trò

- **Người test:** Nguyễn Minh Quốc (người xây `PhanQuyenService`/`LayoutHeader`) · **Nhánh:** `test/e2e-c-phan-quyen-menu`
- **Ngày chạy:** ________ · **Commit `develop`:** ________ · **DB:** sạch, migration `V0`–`V15` (gồm seed mới)
- **Màn phạm vi:** menu chính trên mọi nhóm trang (chỉ ẩn/hiện menu theo vai trò; không kiểm soát truy cập sâu hơn — đã chốt ở task 003).
- **Quy ước:** lane này không tạo dữ liệu nghiệp vụ; chỉ đăng nhập/xuất. Điền `PASS / NO` ngay sau mỗi kịch bản; `NO` thì bắt buộc mô tả lỗi theo mẫu 7 dòng trong file hướng dẫn.
- **Bảng quyền kỳ vọng** (theo `PhanQuyenService`, mã nguồn hiện hành):

| Vai trò | Trang được thấy trên menu |
|---|---|
| Quản lý (`admin`) | tất cả |
| Tiếp nhận (`tiepnhan01`) | Khách hàng, Xe, Danh mục xe, Tiếp nhận khách hàng, Quản lý phiếu tiếp nhận, Tiếp nhận khiếu nại, Quản lý khiếu nại, Lịch của tôi, Lịch của shop, Đổi mật khẩu |
| Thu ngân (`thungan01`) | Lập hóa đơn, Quản lý hóa đơn, Lịch của tôi, Đổi mật khẩu |
| Kỹ thuật (`kythuat01`) | Quản lý chẩn đoán, Lịch của tôi, Đổi mật khẩu |

**Lưu ý về 2 màn con** (không có menu dù `PhanQuyenService` vẫn cấp quyền): **Xem chẩn đoán** và **Bảng chẩn đoán** đều trả `getGroup() == null` nên `LayoutHeader` loại khỏi menu; chúng chỉ mở được từ trong màn cha (Quản lý phiếu tiếp nhận / Quản lý chẩn đoán). Vì vậy **không đánh `NO`** khi 2 màn này không xuất hiện trên menu; nếu muốn chúng có menu thì đó là đề xuất đổi mã nguồn (`MenuGroup.TIEP_NHAN`), ghi vào mô tả.

Các trang **chỉ Quản lý** được thấy: Dashboard, Nhân viên, Phụ tùng, Dịch vụ, Nhà cung cấp, Voucher, Phân công ca, Lập phiếu nhập, Quản lý phiếu nhập, Doanh thu, Tiêu thụ phụ tùng, Tiêu thụ dịch vụ, nhóm Tổng kết. Riêng **Quản lý chẩn đoán** nằm ở nhóm Tiếp nhận nhưng chỉ Quản lý + Kỹ thuật thấy.

---

### E2E-C-01 · Quản lý thấy đủ menu
**Màn:** menu chính
**Các bước:**
1. Đăng nhập `admin`.
2. Liệt kê toàn bộ nhóm menu và mục trong từng nhóm.
**Kết quả mong đợi:** đủ tất cả nhóm và mục trên menu theo danh sách trang có menu (`PageRegistry`), không thiếu, không trùng: các nhóm Tổ chức, Tiếp nhận, Kho, Thanh toán, Kế hoạch, Báo cáo, Tổng kết, Cài đặt — mỗi nhóm hiện đủ trang con tương ứng (trừ các màn con không có menu: Xem chẩn đoán, Bảng chẩn đoán).

**PASS / NO:** ________
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-C-02 · Thu ngân chỉ thấy nhóm Thanh toán + Cài đặt
**Màn:** menu chính
**Các bước:**
1. Đăng xuất, đăng nhập `thungan01`.
2. Liệt kê menu thấy được.
**Kết quả mong đợi:** chỉ có Lập hóa đơn, Quản lý hóa đơn, Lịch của tôi, Đổi mật khẩu; **không** có Dashboard, Nhân viên, Phụ tùng, Dịch vụ, Nhà cung cấp, Voucher, Phân công ca, phiếu nhập, báo cáo, Tổng kết, nhóm Tiếp nhận; nhóm không còn trang nào thì cả nhóm ẩn.

**PASS / NO:** ________
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-C-03 · Tiếp nhận thấy đúng nhóm của mình
**Màn:** menu chính
**Các bước:**
1. Đăng nhập `tiepnhan01`, liệt kê menu.
2. So với bảng quyền.
**Kết quả mong đợi:** thấy đúng **10 trang**: Khách hàng, Xe, Danh mục xe, Tiếp nhận khách hàng, Quản lý phiếu tiếp nhận, Tiếp nhận khiếu nại, Quản lý khiếu nại, Lịch của tôi, Lịch của shop, Đổi mật khẩu; nhóm Tiếp nhận vẫn hiện (4 mục: Tiếp nhận KH, QL phiếu tiếp nhận, Tiếp nhận KN, QL KN); **không** thấy Dashboard, Tổng kết, Nhân viên, Phụ tùng, Dịch vụ, Voucher, phiếu nhập, Lập hóa đơn, Quản lý hóa đơn, báo cáo, **Quản lý chẩn đoán**.

**PASS / NO:** ________
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-C-04 · Kỹ thuật thấy đúng nhóm chẩn đoán
**Màn:** menu chính
**Các bước:**
1. Đăng nhập `kythuat01`, liệt kê menu.
2. So với bảng quyền.
**Kết quả mong đợi:** thấy đúng **3 mục**: Quản lý chẩn đoán, Lịch của tôi, Đổi mật khẩu; không thấy trang nào khác. Bảng chẩn đoán và Xem chẩn đoán mở được **từ trong màn Quản lý chẩn đoán** (không có mục menu riêng).

**PASS / NO:** ________
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-C-05 · Đổi vai trò trong cùng phiên không sót menu
**Màn:** menu chính
**Các bước:**
1. Đang ở `admin`, đăng xuất, đăng nhập `thungan01`, chụp menu.
2. Tiếp tục đổi sang `tiepnhan01`, rồi `kythuat01`, rồi về `admin`; chụp menu mỗi lần.
**Kết quả mong đợi:** mỗi lần đăng nhập menu dựng lại đúng vai trò mới — **không sót mục** của vai trò trước, không thiếu mục của vai trò mới; không cần khởi động lại app.

**PASS / NO:** ________
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-C-06 · Điều hướng sau khi menu dựng lại
**Màn:** menu chính
**Các bước:**
1. Ở từng vai trò (ít nhất `thungan01` và `tiepnhan01`), bấm lần lượt từng mục menu còn lại.
2. Quan sát mục đang mở được đánh dấu, bấm qua lại giữa hai mục cùng nhóm.
**Kết quả mong đợi:** mọi mục bấm được và mở đúng trang, đúng tiêu đề; mục đang mở được đánh dấu; trang đang mở trước đó khi menu dựng lại không bị lỗi hiển thị.

**PASS / NO:** ________
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-C-07 · Tài khoản bị khóa
**Màn:** SCR-01 Đăng nhập
**Các bước:**
1. Đăng nhập `thungan02` (mật khẩu `test@123`).
2. Đăng nhập `quanly02`.
3. Đăng nhập sai mật khẩu `admin` một lần.
**Kết quả mong đợi:** (1)(2) bị từ chối, không vào được trang trong, **không lộ chi tiết kỹ thuật** (DB hiện lọc tài khoản `DA_KHOA` nên thông báo dùng chung với sai mật khẩu: "Tên đăng nhập hoặc mật khẩu không đúng" — ghi lại nguyên văn; **không đánh `NO`** chỉ vì thiếu thông báo riêng "tài khoản bị khóa", coi đó là đề xuất cải thiện); (3) báo sai mật khẩu cùng cơ chế; DB không đổi (`HOAT_DONG`/`DA_KHOA` giữ nguyên).

**PASS / NO:** ________
**Mô tả thực tế / lỗi chi tiết:**
>

---

### E2E-C-08 · Đổi mật khẩu ở vai trò thấp và i18n menu
**Màn:** SCR-15, menu chính
**Tiền điều kiện:** dùng đúng các tài khoản thử của lane này; **đổi về lại mật khẩu cũ** trước khi kết thúc (`test@123`) để không phá seed người khác. Chính sách mật khẩu: tối thiểu 8 ký tự, có chữ, số và ký tự đặc biệt — mật khẩu mới hợp lệ phải đạt cả 4 điều kiện (ví dụ `Test@1234`).
**Các bước:**
1. Đăng nhập `thungan01`, mở Đổi mật khẩu, thử trường hợp **xác nhận không khớp** (mật khẩu mới hợp lệ, ô xác nhận khác) rồi đổi sang mật khẩu mới hợp lệ.
2. Đăng xuất, đăng nhập bằng mật khẩu mới; rà menu ở cả tiếng Anh (chỉ các mục thực sự hiện trên menu).
3. Đổi lại mật khẩu về `test@123`.
**Kết quả mong đợi:** vai trò thu ngân vẫn truy cập được Đổi mật khẩu từ menu; trường hợp xác nhận không khớp bị chặn kèm thông báo riêng; đổi mật khẩu thành công và đăng nhập lại được; nhãn **nhóm menu + mục menu đang hiện** đổi theo ngôn ngữ, không lộ key i18n thô; mật khẩu trả về như cũ.

**PASS / NO:** ________
**Mô tả thực tế / lỗi chi tiết:**
>

---

## Tổng kết

- Số kịch bản: 8 · **PASS:** ____ · **NO:** ____
- Ghi chú thêm của người test:
>
