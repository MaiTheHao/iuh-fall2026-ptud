# DGRAPH — Sơ đồ phụ thuộc Gara Ô tô Lúa

Trạng thái đối chiếu mã nguồn ngày 21/10/2026. `[DONE]` đã chạy trên dữ liệu thật · `[PARTIAL]` một phần đã có mã · `[TODO]` mới có khung giữ chỗ · `[P1..P4]` lane phụ trách.

Nguồn: `assets/tasks/2026-10-14-p1..p4-*.md`, `assets/mockui/docs/screen-flow.md`, `src/iuh/fit/**`, `infra/migrations/**`.

---

## 1. Phụ thuộc giữa các lane

```mermaid
flowchart TD
    p1["P1 To chuc - Quoc<br/>6/6 man [DONE]"]
    p2["P2 Tiep nhan & Khieu nai - Thang<br/>4/6 man [PARTIAL]"]
    p3["P3 Nhap & Thanh toan - Khang<br/>2/5 man [PARTIAL]"]
    p4["P4 Ke hoach & Tong ket - Binh<br/>0/6 man [TODO]"]

    p1 -->|"KhachHang / Xe / NhanVien - da co"| p2
    p1 -->|"PhuTung / DichVu / Voucher / NhanVien - da co"| p3
    p1 -->|"NhanVien - da co"| p4
    p2 -->|"PhieuTiepNhan - da co"| p3
    p3 -->|"HoaDon / ChiTietHoaDon - con thieu"| p4
```

## 2. Contract service liên lane

Nút ghi `chua co` là phần chưa tồn tại trong mã nguồn.

```mermaid
flowchart LR
    subgraph sgP1["P1 cap service - da co"]
        khs["KhachHangService"]
        svcXe["XeService"]
        nvs["NhanVienService"]
        pts["PhuTungService<br/>+ increaseStock"]
        dvs["DichVuService"]
        vcs["VoucherService"]
    end

    subgraph sgP2["P2"]
        ptns["PhieuTiepNhanService"]
        vdks["VanDeKyThuatService - chua co"]
        kns["KhieuNaiService"]
    end

    subgraph sgP3["P3"]
        pnps["PhieuNhapPhuTungService"]
        hds["HoaDonService - chua co"]
    end

    subgraph sgP4["P4"]
        pcs["PhanCongService - chua co"]
        bcs["BaoCaoService - chua co"]
    end

    khs --> ptns
    svcXe --> ptns
    nvs --> pcs
    pts --> pnps
    dvs --> hds
    vcs --> hds
    ptns --> hds
    hds --> bcs
    pnps --> bcs
```

## 3. Kiến trúc 3 tầng

```mermaid
flowchart TD
    gui["iuh.fit.gui.page<br/>XxxPage extends AbstractPage"]
    comp["iuh.fit.gui.component<br/>ManagementPanel + BoHienThiI18n"]
    svc["iuh.fit.service<br/>XxxService"]
    dao["iuh.fit.dao<br/>XxxDAO extends BaseDao"]
    db["SQL Server<br/>iuh_ptud_db"]

    gui --> comp
    gui -->|"goi"| svc
    svc -->|"mo TransactionScope"| dao
    dao -->|"JDBC"| db

    ent["iuh.fit.entity (POJO)"]
    ent -.->|"duoc dung"| gui
    ent -.->|"duoc dung"| svc
    ent -.->|"duoc dung"| dao
    enumx["iuh.fit.enumeration"]
    enumx -.->|"nhan qua I18nEnum"| comp
```

## 4. Bản đồ màn hình theo nhóm menu

```mermaid
flowchart TD
    subgraph sgAuth["Trang chu & Xac thuc"]
        scr01["SCR-01 Dang nhap<br/>[DONE]"]
        scr02["SCR-02 Trang chu<br/>[DONE]"]
    end

    subgraph sgOrg["To chuc - P1 (6/6)"]
        scr03["SCR-03 Khach hang<br/>[DONE]"]
        scr03b["SCR-03B Xe<br/>[DONE]"]
        scr06["SCR-06 Phu tung<br/>[DONE]"]
        scr08["SCR-08 Dich vu<br/>[DONE]"]
        scr09["SCR-09 Voucher<br/>[DONE]"]
        scr11["SCR-11 Nhan vien<br/>[DONE]"]
    end

    subgraph sgIntake["Tiep nhan - P2 (4/6)"]
        scr04["SCR-04 Tiep nhan khach hang<br/>[DONE]"]
        scr04b["SCR-04B Quan ly phieu tiep nhan<br/>[DONE]"]
        scr04c["SCR-04C Xem chan doan<br/>[TODO] - an, khong vao menubar"]
        scr04d["SCR-04D Lap chan doan<br/>[PARTIAL] - moi chon phieu"]
        scr16["SCR-16 Tiep nhan khieu nai<br/>[DONE]"]
        scr16b["SCR-16B Quan ly khieu nai<br/>[DONE]"]
    end

    subgraph sgParts["Nhap phu tung - P3 (2/2)"]
        scr07["SCR-07 Lap phieu nhap<br/>[DONE]"]
        scr07b["SCR-07B Quan ly phieu nhap<br/>[DONE]"]
    end

    subgraph sgPay["Thanh toan - P3 (0/2)"]
        scr10["SCR-10 Lap hoa don<br/>[TODO]"]
        scr10b["SCR-10B Quan ly hoa don<br/>[TODO]"]
    end

    subgraph sgPlan["Ke hoach - P4 (0/6)"]
        scr05["SCR-05 Phan cong ca<br/>[TODO]"]
        scr05b["SCR-05B Lich cua toi<br/>[TODO]"]
        scr05c["SCR-05C Lich cua shop<br/>[TODO]"]
    end

    subgraph sgReport["Tong ket - P4 (0/3)"]
        scr12["SCR-12 Doanh thu<br/>[TODO]"]
        scr12b["SCR-12B Tieu thu phu tung<br/>[TODO]"]
        scr12c["SCR-12C Tieu thu dich vu<br/>[TODO]"]
    end

    subgraph sgSet["Cai dat"]
        scr15["SCR-15 Doi mat khau<br/>[TODO] - P3"]
    end
```

## 5. Điều hướng giữa các màn

Điều hướng nội bộ dùng `goTo(Class)` của `AbstractPage`.

```mermaid
flowchart LR
    scr02["Trang chu"]
    scr01["Dang nhap"]

    scr02 --- scr03["Khach hang"]
    scr02 --- scr03b["Xe"]
    scr02 --- scr06["Phu tung"]
    scr02 --- scr08["Dich vu"]
    scr02 --- scr09["Voucher"]
    scr02 --- scr11["Nhan vien"]
    scr02 --- scr05["Phan cong ca"]
    scr02 --- scr05b["Lich cua toi"]
    scr02 --- scr05c["Lich cua shop"]
    scr02 --- scr04["Tiep nhan khach hang"]
    scr02 --- scr04b["Quan ly phieu tiep nhan"]
    scr02 --- scr04d["Lap chan doan"]
    scr02 --- scr16["Tiep nhan khieu nai"]
    scr02 --- scr16b["Quan ly khieu nai"]
    scr02 --- scr07["Lap phieu nhap"]
    scr02 --- scr07b["Quan ly phieu nhap"]
    scr02 --- scr10["Lap hoa don"]
    scr02 --- scr10b["Quan ly hoa don"]
    scr02 --- scr12["Doanh thu"]
    scr02 --- scr12b["Tieu thu phu tung"]
    scr02 --- scr12c["Tieu thu dich vu"]
    scr02 --- scr15["Doi mat khau"]
    scr02 --- scr01

    scr04 --- scr04b
    scr04b -.->|"can truyen phieu dang chon"| scr04c["Xem chan doan (an)"]
    scr16 --- scr16b
    scr07 --- scr07b
    scr10 --- scr10b
    scr05 --- scr05b
    scr05 --- scr05c
```

## 6. Mô hình dữ liệu

Toàn bộ bảng đã tồn tại trong DB (`V0` + `V3`).

```mermaid
erDiagram
    KhachHang ||--o{ KhachHangXe : "lien he"
    Xe ||--o{ KhachHangXe : "duoc tham chieu"
    KhachHang ||--o{ PhieuTiepNhan : "cua khach"
    Xe ||--o{ PhieuTiepNhan : "xe tiep nhan"
    NhanVien ||--o{ PhieuTiepNhan : "nhan vien tiep nhan"
    PhieuTiepNhan ||--o{ VanDeKyThuat : "chan doan"
    PhieuTiepNhan ||--o| HoaDon : "toi da 1 hoa don"
    HoaDon ||--o{ ChiTietHoaDon : "gom dong"
    Voucher ||--o{ HoaDon : "ap dung"
    NhanVien ||--o{ PhieuNhapPhuTung : "lap phieu"
    PhieuNhapPhuTung ||--o{ ChiTietPhieuNhapPhuTung : "gom dong"
    PhuTung ||--o{ ChiTietPhieuNhapPhuTung : "duoc nhap"
    NhanVien ||--o{ PhanCong : "duoc phan ca"
    KhachHang ||--o{ KhieuNai : "khieu nai"
    PhieuTiepNhan ||--o{ KhieuNai : "lien quan"
```

### 6b. Cụm hóa đơn

`HoaDon` không có cột tổng tiền hay tiền giảm; doanh thu phải tính từ `ChiTietHoaDon` rồi áp `Voucher.mucGiam`.

```mermaid
erDiagram
    HoaDon {
        string maHoaDon PK
        string maPhieuTiepNhan FK
        string maVoucher FK
        string trangThaiHoaDon
        string lyDoHuy
        datetime ngayTao
        datetime thoiGianThanhToan
    }
    ChiTietHoaDon {
        string maHoaDon PK
        string loaiSanPham PK
        string maSanPham PK
        int soLuong
        decimal donGia
    }
    Voucher {
        string maVoucher PK
        decimal mucGiam
        datetime thoiHanApDung
        bit trangThaiKichHoat
    }
    HoaDon ||--o{ ChiTietHoaDon : "gom"
    Voucher ||--o{ HoaDon : "giam gia"
```

`trangThaiHoaDon`: `CHO_THANH_TOAN`, `DA_THANH_TOAN`, `DA_HUY`. `loaiSanPham`: `DICH_VU`, `PHU_TUNG`. DB không có ràng buộc duy nhất trên `maPhieuTiepNhan` hay tổ hợp nhân viên/ngày/ca của `PhanCong`; service phải kiểm tra trùng.

## 7. Việc còn lại

```mermaid
flowchart TD
    subgraph sgP2["P2 con lai"]
        p2a["VanDeKyThuatDAO + Service<br/>[TODO]"]
        p2b["SCR-04D bieu mau lap chan doan<br/>[PARTIAL]"]
        p2c["SCR-04C xem chan doan + dieu huong<br/>[TODO]"]
        p2a --> p2b --> p2c
    end

    subgraph sgP3["P3 con lai"]
        p3a["HoaDonDAO ghi + HoaDonService<br/>[TODO]"]
        p3b["SCR-10 + SCR-10B<br/>[TODO]"]
        p3c["SCR-15 doi mat khau<br/>[TODO]"]
        p3a --> p3b
    end

    subgraph sgP4["P4 con lai"]
        p4a["PhanCongDAO ghi + PhanCongService<br/>[TODO]"]
        p4b["SCR-05 + SCR-05B + SCR-05C<br/>[TODO]"]
        p4c["BaoCaoDAO + BaoCaoService<br/>[TODO]"]
        p4d["SCR-12 + SCR-12B + SCR-12C<br/>[TODO]"]
        p4a --> p4b
        p4c --> p4d
    end

    p3a -.->|"chot cong thuc doanh thu"| p4c
```

## 8. Trong tuần làm gì / hoãn gì

```mermaid
flowchart LR
    subgraph sgIn["Trong tuan - make it work"]
        in1["Man hien du lieu that tu DB"]
        in2["CRUD / luong chinh chay end-to-end"]
        in3["Dung chung kit + i18n + nhan enum"]
        in4["Contract service lien lane"]
    end

    subgraph sgOut["Hoan - make it right"]
        hn1["Badge mau ngu nghia"]
        hn2["Filter day du moi cot"]
        hn3["Bieu do + Top 20 (Java2D)"]
        hn4["Dich EN day du"]
        hn5["Lam dep / anh"]
    end

    in1 -.->|"sau khi xong"| hn1
    in2 -.-> hn2
    in3 -.-> hn4
    in1 -.-> hn3
    in3 -.-> hn5
```

## 9. Quy trình nhánh Git

```mermaid
flowchart LR
    dev["develop<br/>nhanh goc - da co"]
    b1["feat/p1-to-chuc<br/>[DONE] da gop"]
    b2["feat/p2-tiep-nhan-khieu-nai<br/>[PARTIAL] da gop mot phan"]
    b3["feat/p3-nhap-thanh-toan<br/>[PARTIAL] dang lam"]
    b4["feat/p4-ke-hoach-tong-ket<br/>[TODO]"]

    dev -->|"git switch -c"| b1
    dev -->|"git switch -c"| b2
    dev -->|"git switch -c"| b3
    dev -->|"git switch -c"| b4
    b1 -.->|"rebase + Pull Request"| dev
    b2 -.->|"rebase + Pull Request"| dev
    b3 -.->|"rebase + Pull Request"| dev
    b4 -.->|"rebase + Pull Request"| dev
```

## Cách đọc nhanh

| Sơ đồ | Trả lời |
| :--- | :--- |
| 1 | Lane nào phụ thuộc lane nào, ai phải xong trước |
| 2 | Service nào cấp cho lane nào (điểm chốt contract) |
| 3 | Chiều phụ thuộc code bắt buộc |
| 4 | 25 màn thuộc nhóm nào, lane nào, đã xong chưa |
| 5 | Điều hướng menu và cặp thao tác ↔ quản lý |
| 6 | Khóa ngoại / bản số dữ liệu |
| 7 | Việc còn lại của P2, P3, P4 và thứ tự làm |
| 8 | Việc làm trong tuần vs việc hoãn |
| 9 | Mỗi lane tách nhánh từ `develop` và PR ngược lại |
