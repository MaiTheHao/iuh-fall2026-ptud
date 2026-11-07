# 003 — Thanh toán QR, in hóa đơn, phân quyền menu

**Trạng thái:** ĐÃ DUYỆT (2026-11-07) · **Dành cho:** Quốc · **Không có phần tự động**

Ba việc, làm đúng thứ tự dưới đây. P1 cần phần lõi thanh toán đã có sẵn trong repo (bảng `ThanhToan`, `ThanhToanService`, `VietQrClient`).

| # | File | Việc | Phụ thuộc |
|---|---|---|---|
| P1 | `p1-thanh-toan-dialog.md` | Dialog chọn QR / tiền mặt khi bấm thanh toán | lõi thanh toán (đã có) |
| P2 | `p2-in-hoa-don-xlsx.md` | In một hóa đơn + xuất một hóa đơn ra Excel | nên làm sau P1 |
| P3 | `p3-phan-quyen-menu.md` | Ẩn/hiện menu theo vai trò | không |

## Điều kiện chung

- JDK 17, build bằng `javac --release 17 -encoding UTF-8 -cp "lib/*"`.
- Chạy được app và kết nối SQL Server (`docker compose -f infra/docker-compose.yml up -d mssql`).
- Có dữ liệu seed: 15 hóa đơn (6 đã thanh toán), 4 tài khoản vai trò (mật khẩu `test@123`).
- Tài khoản seed: `admin`, `tiepnhan01`, `thungan01`, `kythuat01`.

## Nguyên tắc chung

- Mọi chữ hiển thị đi qua i18n, thêm key vào **cả** `src/resources/i18n_vn.json` và `src/resources/i18n_en.json`.
- Tiền tính qua `TinhTienUtil` — không tự nhân chia trong UI.
- Không sửa `src/iuh/fit/dao/TransactionScope.java` (thay đổi riêng của chủ repo).
- Sau mỗi phần: build sạch, chạy app, làm đúng danh sách "Kiểm thử tay".

## Thứ tự đề nghị

1. P3 (nhỏ nhất, 2 file) để làm quen.
2. P1 (1 dialog + nối nút).
3. P2 (3 file mới + thanh nút).
