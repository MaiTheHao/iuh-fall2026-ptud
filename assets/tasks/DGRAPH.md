# DGRAPH — Sơ đồ phụ thuộc Gara Ô tô Lúa (Tuần 1: 14/10 – 19/10)

Tài liệu gom **nhiều góc nhìn phụ thuộc** của kế hoạch tuần 1. Mỗi sơ đồ trả lời một câu hỏi khác nhau.

- **Chú giải trạng thái:** `[DONE]` đã xong · `[TODO]` chưa làm · `[P1..P4]` lane phụ trách.
- Sơ đồ dùng mermaid mặc định (không hardcode màu) để tự tương thích theme sáng/tối của IDE.
- Nguồn: `assets/tasks/2026-10-14-p1..p4-*.md`, `assets/mockui/docs/screen-flow.md`, `src/iuh/fit/**`, `init.sql`.

---

## 1. Phụ thuộc giữa các plan (lane)

Góc nhìn: **lane nào chặn lane nào**. Kit dùng chung (Wave 0) đã xong nên không còn chặn lane; P1 là nền dữ liệu; P3 cần P2; P4 cần P3.

```mermaid
flowchart TD
    p1["P1 To chuc - Quoc<br/>6 man du lieu nen"]
    p2["P2 Tiep nhan & Khieu nai - Thang<br/>6 man"]
    p3["P3 Nhap & Thanh toan - Khang<br/>5 man"]
    p4["P4 Ke hoach & Tong ket - Binh<br/>6 man"]

    p1 -->|"KhachHang / Xe / NhanVien"| p2
    p1 -->|"PhuTung / DichVu / Voucher / NhanVien"| p3
    p1 -->|"NhanVien"| p4
    p2 -->|"PhieuTiepNhan + VanDeKyThuat"| p3
    p3 -->|"HoaDon / ChiTietHoaDon"| p4
```

---

## 2. Contract service liên lane (ai cấp, ai tiêu thụ)

Góc nhìn: **điểm chạm chéo lane cần chốt chữ ký sớm** để không bị block.

```mermaid
flowchart LR
    subgraph sgP1["P1 cap service"]
        khs["KhachHangService"]
        svcXe["XeService"]
        nvs["NhanVienService"]
        pts["PhuTungService"]
        dvs["DichVuService"]
        vcs["VoucherService"]
    end

    subgraph sgP2["P2"]
        ptns["PhieuTiepNhanService"]
        vdks["VanDeKyThuatService"]
        kns["KhieuNaiService"]
    end

    subgraph sgP3["P3"]
        pnps["PhieuNhapPhuTungService"]
        hds["HoaDonService"]
    end

    subgraph sgP4["P4"]
        pcs["PhanCongService"]
        bcs["BaoCaoService"]
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

---

## 3. Kiến trúc 3 tầng (GUI → SERVICE → DAO → DB)

Góc nhìn: **chiều phụ thuộc bắt buộc** theo code-convention; entity là POJO dùng chung.

```mermaid
flowchart TD
    gui["iuh.fit.gui.page<br/>XxxPage extends AbstractPage"]
    comp["iuh.fit.gui.component<br/>ManagementPanel + EnumLabels"]
    svc["iuh.fit.service<br/>XxxService"]
    dao["iuh.fit.dao<br/>XxxDAO extends BaseDao"]
    db["SQL Server<br/>iuh_ptud_db"]

    gui --> comp
    gui -->|"goi"| svc
    svc -->|"goi"| dao
    dao -->|"JDBC"| db

    ent["iuh.fit.entity (POJO)"]
    ent -.->|"duoc dung"| gui
    ent -.->|"duoc dung"| svc
    ent -.->|"duoc dung"| dao
    enumx["iuh.fit.enumeration"]
    enumx -.->|"nhan qua EnumLabels"| comp
```

---

## 4. Bản đồ màn hình theo nhóm menu + trạng thái

Góc nhìn: **25 màn mockup** ánh xạ sang nhóm menu và trạng thái hiện tại. `[DONE]` = đã thật; còn lại thuộc lane.

```mermaid
flowchart TD
    subgraph sgAuth["Trang chu & Xac thuc"]
        scr01["SCR-01 Dang nhap<br/>[DONE] LoginPage"]
        scr02["SCR-02 Trang chu<br/>[DONE] DashboardPage"]
    end

    subgraph sgOrg["To chuc - P1"]
        scr03["SCR-03 Khach hang"]
        scr03b["SCR-03B Xe"]
        scr06["SCR-06 Phu tung"]
        scr08["SCR-08 Dich vu"]
        scr09["SCR-09 Voucher"]
        scr11["SCR-11 Nhan vien"]
    end

    subgraph sgPlan["Ke hoach - P4"]
        scr05["SCR-05 Phan cong ca"]
        scr05b["SCR-05B Lich cua toi"]
        scr05c["SCR-05C Lich cua shop"]
    end

    subgraph sgIntake["Tiep nhan - P2"]
        scr04["SCR-04 Tiep nhan khach hang"]
        scr04b["SCR-04B Quan ly phieu tiep nhan"]
        scr04c["SCR-04C Chan doan<br/>an, khong vao menubar"]
        scr04d["SCR-04D Lap chan doan"]
        scr16["SCR-16 Tiep nhan khieu nai"]
        scr16b["SCR-16B Quan ly khieu nai"]
    end

    subgraph sgParts["Nhap phu tung - P3"]
        scr07["SCR-07 Lap phieu nhap"]
        scr07b["SCR-07B Quan ly phieu nhap"]
    end

    subgraph sgPay["Thanh toan - P3"]
        scr10["SCR-10 Lap hoa don"]
        scr10b["SCR-10B Quan ly hoa don"]
    end

    subgraph sgReport["Tong ket - P4"]
        scr12["SCR-12 Doanh thu"]
        scr12b["SCR-12B Tieu thu phu tung"]
        scr12c["SCR-12C Tieu thu dich vu"]
    end

    subgraph sgSet["Cai dat"]
        scr15["SCR-15 Doi mat khau<br/>P3"]
    end
```

---

## 5. Quan hệ điều hướng giữa màn hình

Góc nhìn: **luồng mở màn** — từ Trang chủ tới các màn, và các cặp "thao tác ↔ quản lý".

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
    scr04b -.->|"nut !"| scr04c["Chan doan (an)"]
    scr16 --- scr16b
    scr07 --- scr07b
    scr10 --- scr10b
    scr05 --- scr05b
    scr05 --- scr05c
```

---

## 6. Mô hình dữ liệu — quan hệ thực thể

Góc nhìn: **khóa ngoại / bản số** giữa các bảng. `KhieuNai` là bảng **mới** (P2).

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

### 6b. Cụm hóa đơn — thuộc tính & trạng thái

Góc nhìn: **giá trị trạng thái thật trong DB** (khác nhãn mockup).

```mermaid
erDiagram
    HoaDon {
        string maHoaDon PK
        string maPhieuTiepNhan FK
        string maVoucher FK
        string trangThaiHoaDon
        string lyDoHuy
        datetime ngayTao
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

`trangThaiHoaDon` nhận giá trị thật trong DB: `CHO_THANH_TOAN`, `DA_THANH_TOAN`, `DA_HUY` (nhãn hiển thị: Chưa thanh toán / Đã thanh toán / Đã hủy).

---

## 7. Vòng đời một TaskUnit

Góc nhìn: **định nghĩa done của tuần** — khi nào một màn được coi là xong.

```mermaid
stateDiagram-v2
    [*] --> ChuaBatDau
    ChuaBatDau --> DangCode : bat dau unit
    DangCode --> DaBuild : javac khong loi
    DaBuild --> DaChayThat : mo man + CRUD chay tren DB
    DaChayThat --> DaTichHop : ghep service lien lane
    DaTichHop --> NghiemThu : demo 19/10
    NghiemThu --> [*]
    DangCode --> Loi : stacktrace / sai nghiep vu
    Loi --> DangCode : sua tai cho
    DaTichHop --> Loi : vo contract lien lane
```

---

## 8. Mốc thời gian tuần 1 (14/10 – 19/10)

Góc nhìn: **lịch thực thi** theo ngày, tôn trọng thứ tự phụ thuộc ở mục 1.

```mermaid
gantt
    title Tuan 1 - 14/10 den 19/10 (functionality first)
    dateFormat YYYY-MM-DD
    axisFormat %d/%m

    section P1 To chuc
    Service nen + 6 man :p1, 2026-10-14, 4d

    section P2 Tiep nhan
    KhieuNai + tiep nhan + chan doan :p2, 2026-10-15, 3d

    section P3 Nhap Thanh toan
    Phieu nhap + hoa don + doi mat khau :p3, 2026-10-15, 3d

    section P4 Ke hoach Tong ket
    Phan cong ca + bao cao so lieu :p4, 2026-10-15, 3d

    section Tich hop
    Ghep lien lane + sua loi :int, 2026-10-18, 1d
    Freeze + demo + walkthrough :demo, 2026-10-19, 1d
```

---

## 9. Trong tuần làm gì / hoãn gì

Góc nhìn: **ranh giới scope** theo "first make it work, then make it right".

```mermaid
flowchart LR
    subgraph sgIn["Trong tuan - make it work"]
        in1["Man hien du lieu that tu DB"]
        in2["CRUD / luong chinh chay end-to-end"]
        in3["Dung chung kit + i18n + EnumLabels"]
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

---

## 10. Quy trình nhánh Git

Góc nhìn: **mỗi lane tách nhánh từ `develop`** rồi PR ngược lại `develop`.

```mermaid
flowchart LR
    dev["develop<br/>nhanh goc - da co"]
    b1["feat/p1-to-chuc"]
    b2["feat/p2-tiep-nhan-khieu-nai"]
    b3["feat/p3-nhap-thanh-toan"]
    b4["feat/p4-ke-hoach-tong-ket"]

    dev -->|"git switch -c"| b1
    dev -->|"git switch -c"| b2
    dev -->|"git switch -c"| b3
    dev -->|"git switch -c"| b4
    b1 -.->|"rebase + Pull Request"| dev
    b2 -.->|"rebase + Pull Request"| dev
    b3 -.->|"rebase + Pull Request"| dev
    b4 -.->|"rebase + Pull Request"| dev
```

---

## Cách đọc nhanh

| Sơ đồ | Trả lời |
| :--- | :--- |
| 1 | Lane nào phụ thuộc lane nào, ai phải xong trước |
| 2 | Service nào cấp cho lane nào (điểm chốt contract) |
| 3 | Chiều phụ thuộc code bắt buộc |
| 4 | 25 màn thuộc nhóm nào, lane nào, đã xong chưa |
| 5 | Điều hướng menu và cặp thao tác ↔ quản lý |
| 6 | Khóa ngoại / bản số dữ liệu |
| 7 | Định nghĩa "xong" của một unit trong tuần |
| 8 | Lịch 14–19/10 |
| 9 | Việc làm trong tuần vs việc hoãn |
| 10 | Mỗi lane tách nhánh từ `develop` và PR ngược lại |
