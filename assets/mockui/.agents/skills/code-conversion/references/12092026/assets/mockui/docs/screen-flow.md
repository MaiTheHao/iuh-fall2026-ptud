# Garage Ô tô Lúa — Sơ đồ luồng màn hình (Screen Flow)

Tài liệu mô tả **luồng điều hướng và quan hệ giữa các màn hình** của mockup.
Nguồn đối chiếu: `partials/menubar.html` (điều hướng menu), liên kết trong trang
và đặc tả trong skill `code-conversion`.

## Chú giải

- **Hướng:** `LR` (trái → phải), bắt đầu từ entry point **Trang chủ**.
- **Mỗi page là một box chữ nhật duy nhất**, chỉ hiển thị **tên page**; màu nền thể
  hiện **nhóm menu**:

| Nhóm menu | Màn hình (theo tên) | Màu |
|---|---|---|
| Trang chủ & Xác thực | Trang chủ, Đăng nhập | Xanh dương |
| Tổ chức | Khách hàng, Xe, Phụ tùng, Dịch vụ, Voucher, Nhân viên | Xanh lá |
| Kế hoạch | Phân công ca, Lịch của tôi, Lịch của shop | Tím |
| Tiếp nhận | Tiếp nhận khách hàng, Quản lý phiếu tiếp nhận, Lập chẩn đoán, Chẩn đoán, Tiếp nhận khiếu nại, Quản lý khiếu nại | Cam |
| Nhập phụ tùng | Lập phiếu nhập, Quản lý phiếu nhập | Xanh ngọc |
| Thanh toán | Lập hóa đơn, Quản lý hóa đơn | Đỏ hồng |
| Tổng kết | Doanh thu, Tiêu thụ phụ tùng, Tiêu thụ dịch vụ | Chàm |
| Cài đặt | Đổi mật khẩu | Xám |

- **Đường nối:** tất cả là **line liền, không mũi tên, không nhãn** (`---`), biểu diễn
  quan hệ giữa hai page (điều hướng từ Trang chủ tới các màn hình, và các cặp
  thao tác ↔ quản lý).
- Màn hình **Chẩn đoán** là màn hình ẩn, chỉ liên kết với **Quản lý phiếu tiếp nhận**,
  không xuất hiện trong menubar.

## Sơ đồ

```mermaid
flowchart LR
    scr02["Trang chủ"]
    scr01["Đăng nhập"]
    scr03["Khách hàng"]
    scr03b["Xe"]
    scr06["Phụ tùng"]
    scr08["Dịch vụ"]
    scr09["Voucher"]
    scr11["Nhân viên"]

    scr05["Phân công ca"]
    scr05b["Lịch của tôi"]
    scr05c["Lịch của shop"]

    scr04["Tiếp nhận khách hàng"]
    scr04b["Quản lý phiếu tiếp nhận"]
    scr04c["Chẩn đoán"]
    scr04d["Lập chẩn đoán"]
    scr16["Tiếp nhận khiếu nại"]
    scr16b["Quản lý khiếu nại"]

    scr07["Lập phiếu nhập"]
    scr07b["Quản lý phiếu nhập"]

    scr10["Lập hóa đơn"]
    scr10b["Quản lý hóa đơn"]

    scr12["Doanh thu"]
    scr12b["Tiêu thụ phụ tùng"]
    scr12c["Tiêu thụ dịch vụ"]

    scr15["Đổi mật khẩu"]

    %% Entry point: Trang chủ liên kết tới các màn hình
    scr02 --- scr01
    scr02 --- scr03
    scr02 --- scr03b
    scr02 --- scr06
    scr02 --- scr08
    scr02 --- scr09
    scr02 --- scr11
    scr02 --- scr05
    scr02 --- scr05b
    scr02 --- scr05c
    scr02 --- scr04
    scr02 --- scr04b
    scr02 --- scr04d
    scr02 --- scr16
    scr02 --- scr16b
    scr02 --- scr07
    scr02 --- scr07b
    scr02 --- scr10
    scr02 --- scr10b
    scr02 --- scr12
    scr02 --- scr12b
    scr02 --- scr12c
    scr02 --- scr15

    %% Các cặp thao tác ↔ quản lý
    scr04 --- scr04b
    scr04b --- scr04c
    scr16 --- scr16b
    scr07 --- scr07b
    scr10 --- scr10b
    scr05 --- scr05b
    scr05 --- scr05c

    %% Màu theo nhóm menu
    classDef auth fill:#e0ecff,stroke:#2563eb,color:#0b2a5b
    classDef org fill:#e6f6ec,stroke:#15803d,color:#0f3d22
    classDef plan fill:#f3e8ff,stroke:#7e22ce,color:#3b0764
    classDef intake fill:#fff2e0,stroke:#c2410c,color:#5a1f04
    classDef parts fill:#e0f7f7,stroke:#0e7490,color:#083344
    classDef pay fill:#ffe4e6,stroke:#be123c,color:#4c0519
    classDef report fill:#e0e7ff,stroke:#4338ca,color:#1e1b4b
    classDef settings fill:#f1f3f5,stroke:#495057,color:#212529

    class scr01,scr02 auth
    class scr03,scr03b,scr06,scr08,scr09,scr11 org
    class scr05,scr05b,scr05c plan
    class scr04,scr04b,scr04c,scr04d,scr16,scr16b intake
    class scr07,scr07b parts
    class scr10,scr10b pay
    class scr12,scr12b,scr12c report
    class scr15 settings
```
