---
name: code-conversion
description: Use when creating, converting, refactoring, or reviewing any HTML/CSS screen or shared UI code for the Garage Ô tô Lúa desktop mockup. This skill contains the project's complete UI, screen, navigation, and data-display specification.
---

# Garage Ô tô Lúa — UI Conversion Skill

## 1. Purpose and non-negotiable constraints

This is a **self-contained specification** for the Garage Ô tô Lúa mockup. Follow it without relying on a separate context document. The mockup is a visual specification for a future Java Swing desktop application connected to MS SQL Server with role-based access control (RBAC); it is **not** the production application.

- Stack: HTML + Vanilla CSS, no build step or UI framework. Keep the existing shared JavaScript infrastructure only. Do not add business logic, DB access, authentication logic, persistence, CRUD handlers, or calculations to mockup pages. Render sample data and computed-looking totals/charts as static UI. Exceptions: existing shell/menu/icon infrastructure, column filtering, the SCR-04D view switch, and the SCR-12 Chart.js CDN chart with pre-rendered data.
- Fixed desktop/POS viewport: no responsive design, no `@media`, no media queries, no `sm:`/`md:`/`lg:` utilities. Use fixed desktop layouts, a 12-column grid, `flex-1`, and `min-h-0` where appropriate. `.swing-window` fills the viewport; do not restore the removed POS device frame (`pos-header`, `pos-screen-container`, `pos-stand`, `pos.css`).
- Use only UI content a real application would show. Do not display screen IDs, implementation annotations, dialog notes such as “(JDialog: …)”, version/ready labels, image ratios, developer instructions, validation explanations, or quick-navigation links in page content.
- Use Lucide icons, never emoji or decorative Unicode icons. Buttons with text labels have no icon. Icons are for icon-only actions, navigation/window controls, and other already-established icon-only UI.
- Reuse the existing tokens, CSS components, partials, naming, and patterns. Make scoped changes only; do not rename or duplicate shared infrastructure or refactor unrelated screens.

## 2. Project files and shared infrastructure

```text
css/base.css          Inter, reset, color tokens, inputs, flat buttons
css/components.css    swing-panel, swing-table, filter panel, badges/alerts,
                      image ratios, Lucide sizing, garage backdrop, KPI cards
css/layout.css        JFrame/window, titlebar, menubar, statusbar
css/utilities.css     fixed desktop grid, spacing, colors, typography
partials/chrome.js    load partials, keyboard shortcuts, Lucide, shell/menu,
                      image chooser mock and language toggle
partials/table-filter.js  shared table column filters and quick search
partials/view-switch.js   switch [data-view] sections for SCR-04D
partials/titlebar.html
partials/menubar.html
partials/statusbar.html
pages/scr-*.html      individual screens
```

A static HTTP server is needed for `fetch()`-loaded partials; opening via `file://` may fail because of browser CORS. Internet is used for Lucide, images, and Chart.js. Do not add dependencies unless explicitly requested.

## 3. Global visual system

- Look: classic Swing (`JFrame`, `JMenuBar`, `JTable`, `TitledBorder`) translated into clean HTML. Light neutral background; graphite borders/panels; Steel Blue accent. Panels/tables are white. Typography is Inter.
- Tokens in `css/base.css`:

| Token | Value | Use |
|---|---|---|
| `--swing-bg` | `#e9ecef` | app background |
| `--swing-panel` | `#ffffff` | panel/table face |
| `--swing-border` | `#adb5bd` | borders |
| `--swing-btn-bg` | `#e9ecef` | normal button background |
| `--swing-accent` | `#4682b4` | Steel Blue: titlebar underline, primary button, active menu, LED |
| `--swing-accent-strong` | `#38678f` | hover/emphasis border |
| `--swing-accent-soft` | `#eaf2f8` | selected row background |
| `--swing-info` / `-bg` / `-border` / `-text` | `#2563eb` / `#eff6ff` / `#93c5fd` / `#1d4ed8` | informational state |
| `--swing-success` / `-bg` / `-border` / `-text` | `#059669` / `#ecfdf5` / `#6ee7b7` / `#047857` | success state |
| `--swing-warning` / `-bg` / `-border` / `-text` | `#d97706` / `#fffbeb` / `#fcd34d` / `#b45309` | warning state |
| `--swing-danger` / `-bg` / `-border` / `-text` | `#dc2626` / `#fef2f2` / `#fca5a5` / `#b91c1c` | danger/error state |
| `--swing-ink` | `#111827` | primary text |

- Normal buttons: white fill, 1px grey border, square corners; no bevel or box shadow. No rounded buttons. `.swing-btn-primary` is Steel Blue with white text. Use red text for destructive/warning actions where already established. Inputs/tables may retain Swing-like 3D/beveled borders. The legacy class `.bg-\[\#ece9d8\]` remains in use but its rendered value is `#e9ecef`.
- Selected table rows use `.swing-table tr.selected`: `--swing-accent-soft` background plus `inset 3px 0 var(--swing-accent)` left marker.
- Use `.swing-badge-{info|success|warning|danger}` for semantic badges and `.swing-alert-{info|success|warning|error}` for alert blocks.
- Shared background: `.garage-backdrop` in `css/components.css`; place a garage photo behind a white translucent overlay `rgba(255,255,255,0.35)`. It should show mainly through gaps between panels. Suggested existing source is the garage JPEG from `gocheap.vn` (1200×699); preserve the current URL if one is already configured.
- Images stay in color. Use `.swing-image-ratio-1-1` for part photos (SCR-06) and service process photos (SCR-08); `.swing-image-ratio-3-4` for employee portrait photos (SCR-02 and SCR-11). User avatar is a chibi/anime-style DiceBear Adventurer SVG, e.g. `https://api.dicebear.com/10.x/adventurer/svg?seed=...`. Mock “Chọn Ảnh...” and “Gỡ” are normal non-primary illustrative buttons; show a sample image, do not show a filename, and do not implement a real chooser in the mockup.
- Lucide CDN: `https://unpkg.com/lucide@1.51.0/dist/umd/lucide.min.js`, initialized by `partials/chrome.js` via `initLucideIcons()`, `lucide.createIcons()`, and a `MutationObserver` for dynamically rendered icons. Markup pattern: `<i data-lucide="icon-name"></i>`. Keep icon sizing in `css/components.css`. Existing icon vocabulary: `users`, `car`, `search`, `check`, `refresh-cw`, `trash-2`, `save`, `clipboard-list`, `x`, `receipt`, `pencil`, `image`, `folder-open`, `triangle-alert`, `package`, `ticket`, `printer`, `user`, `lock`, `chevron-down`, `minus`, `square`, `plus`, `key-round`.

## 4. Shared shell, navigation, keyboard shortcuts

- Titlebar (`partials/titlebar.html`): only three right-aligned Lucide window controls — minimize (`minus`), maximize (`square`), close (`x`). No app name or coffee icon. Accent underline: `border-bottom: 2px solid var(--swing-accent)`.
- Menubar (`partials/menubar.html`): plain Swing-style labels, no decorative icons, numbering, or underlines. Keep only dropdown chevrons, separators, and shortcut labels. Main dropdowns open on hover and close when the pointer leaves; click remains a fallback. Every navigation item uses the correct `data-screen` so `chrome.js` highlights the active screen.
- Menu structure and order:
  - **Trang chủ**: Dashboard (SCR-02).
  - **Tổ chức**: Khách hàng (SCR-03), Xe (SCR-03B); separator; Nhân viên (SCR-11, F10), Phụ tùng (SCR-06, F4), Dịch vụ (SCR-08, F6), Voucher (SCR-09, F7).
  - **Kế hoạch**: Phân công ca (SCR-05, F9), Lịch của tôi (SCR-05B), Lịch của shop (SCR-05C).
  - **Tiếp nhận**: Tiếp nhận khách hàng (SCR-04, F2), Quản lý phiếu tiếp nhận (SCR-04B, F3), Lập chẩn đoán (SCR-04D); separator; Tiếp nhận khiếu nại (SCR-16), Quản lý khiếu nại (SCR-16B). SCR-04C is hidden and must not appear in the menu.
  - **Nhập phụ tùng**: Lập phiếu (SCR-07, F5), Quản lý phiếu (SCR-07B).
  - **Thanh toán**: Lập hóa đơn (SCR-10, F8), Quản lý hóa đơn (SCR-10B).
  - **Tổng kết**: Doanh thu (SCR-12), Tiêu thụ phụ tùng (SCR-12B), Tiêu thụ dịch vụ (SCR-12C).
  - **Cài đặt** (right side): Ngôn ngữ, Đổi mật khẩu (SCR-15), Tài khoản (`quanly_lua`), Đăng xuất (SCR-01, Ctrl+Q).
- Keep in-page navigation links only for: SCR-01 link to SCR-02; “Sửa” action in SCR-07B to SCR-07 and SCR-10B to SCR-10; `!` action in SCR-04B to SCR-04C. Other page-body quick links to `scr-*.html` must be removed. The menubar is the main navigation location.
- Shortcuts: F1 → SCR-02; F2 → SCR-04; F3 → SCR-04B; F4 → SCR-06; F5 → SCR-07; F6 → SCR-08; F7 → SCR-09; F8 → SCR-10; F9 → SCR-05; F10 → SCR-11; F11 toggles language; Ctrl+Q logs out to SCR-01. Preserve existing shortcut handling in `chrome.js`.
- Statusbar (`partials/statusbar.html`): show only date/time, right-aligned (format example `29/09/2026 15:15`). Do not show “Trạng thái: Sẵn sàng” or “Đang trực: quanly_lua”. The legacy language-toggle branch that looks for `.swing-status-section` may remain guarded/no-op; do not reintroduce removed status fields.

## 5. Layout templates and common behavior

### Management form + table

Use this layout for SCR-03, SCR-03B, SCR-04B, SCR-06, SCR-08, SCR-09, SCR-11, and SCR-16B. SCR-04D reuses a related form/list split for its diagnosis step.

- Outer layout: `grid grid-cols-12 gap-2 flex-1 min-h-0`.
- Left `col-span-5`: form and integrated lookups. Right `col-span-7`: full-height list panel; make the table area `flex-1 min-h-0 overflow-auto`.
- Exactly three form-footer buttons, in a `border-t border-zinc-300` block: `Thêm`, `Lưu`, `Xóa Trắng`. No `Tìm` button on the form. `Lưu` is the established primary/destructive-red style as appropriate. Table lookup/filter lives on the right.
- Table action column is last and contains icon-only `Sửa` (`pencil`) and `Xóa` (`trash-2`). These are visual-only. SCR-11 additionally has `Đặt lại mật khẩu` (`key-round`) to represent resetting an employee password so they can change it themselves.
- Place a separate `.swing-filter-panel` above the table, not inside `<thead>`. It has labeled filters by column: text input for strings, select for enums, from–to number inputs for numbers/money and date inputs for dates, plus `Tìm nhanh`, a `data-filter-count` readout (`Hiển thị x/y`), and `Xóa lọc`.
- `partials/table-filter.js` provides instant filtering, Vietnamese diacritic-insensitive matching, and hides non-matching rows using `.hidden`. Preserve enum codes in `<option value>` and `data-value`; show Vietnamese labels in the table.
- Desktop lookup convention where specified: a match fills the form and highlights the row with `.selected`; no match shows an amber/orange warning and permits manual entry. Do not add a “Đã tìm thấy” message to SCR-04 or SCR-16.

### Other templates

- SCR-07B and SCR-10B: full-width list; no left form. Keep filters above table. Their `Sửa` action navigates to the corresponding editing screen.
- SCR-02: special dashboard, not the management template.
- SCR-04, SCR-07 and SCR-10 are transaction screens; use their specific controls below, not the generic three-button footer.
- SCR-05 is a shift assignment form, not the management CRUD template.
- SCR-04C is read-only.
- SCR-05B and SCR-05C are read-only schedule views.

## 6. Screen registry — required UI per file

| Screen / file | Required purpose and distinctive rules |
|---|---|
| **SCR-01** `pages/scr-01.html` | Compact login dialog. Fields `tenDangNhap` and `matKhau`; login only, no remember-session and no forgot-password function. Only in-page link is to SCR-02. Ctrl+Q returns/logs out to SCR-01. |
| **SCR-02** `pages/scr-02.html` | Dashboard: one full-height `.swing-panel` aligned right, exactly 40% viewport width; left 60% exposes the garage backdrop. Four horizontal sections separated by `border-t`: greeting/account, current shift, exactly five evenly distributed KPI rows (`.dash-kpi-*`), and bottom utility row with Đăng xuất, Đổi ngôn ngữ, Đổi mật khẩu. Do not restore panels for shift/staff or recent intake slips. Avatar portrait ratio 3:4. |
| **SCR-03** `pages/scr-03.html` | Customer management. Table columns must match `KhachHang`: Mã, SĐT, Họ tên, Ngày tạo. No avatar and no derived “Số Xe Liên Hệ” column. Column filters: text + date from–to. Standard management template. |
| **SCR-03B** `pages/scr-03b.html` | Vehicle management/lookup. Table columns must match `Xe`: Biển số, Hãng, Dòng, Đời, ODO. Do not add derived “Khách Hàng Liên Hệ” column. The related customer/contact can appear only as read-only detail, not as an extra vehicle table column. Filters: text, selection, and range. Standard management template. |
| **SCR-04** `pages/scr-04.html` | New intake slip. One panel; top two side-by-side groups `Thông Tin Khách Hàng` and `Phương Tiện`; lookup customer by phone and vehicle by plate. Make and model are separate fields. Below: condition/request description; actions at bottom: `Làm Mới (Xóa Trắng)` and `Lưu Phiếu Tiếp Nhận [Ctrl+S]`, no “Thao tác phiếu” heading. Do not show generated slip ID, time/date, or staff fields. |
| **SCR-04B** `pages/scr-04b.html` | Intake-slip management using the standard management template and column filters. Allowed state codes only: `MOI`, `KIEM_TRA`, `DANG_TRIEN_KHAI`, `CHO_BAN_GIAO`, `HOAN_THANH`. Every row has `!` action to hidden SCR-04C. Keep the specified route toward invoice creation where already present. |
| **SCR-04C** `pages/scr-04c.html` | Hidden read-only diagnosis view for one intake slip; accessible only through `!` on SCR-04B, never from menubar. Datatable of `VanDeKyThuat`: code, title, description, priority 1–100, customer agreement (radio Có/Không), solution. No add-diagnosis button. Because the schema allows NULL, represent the unanswered state as `Chưa xác nhận` where needed. |
| **SCR-04D** `pages/scr-04d.html` | Create technical diagnoses, master-detail. Step 1: choose intake slip with search by slip code/plate/customer plus state filter. Step 2: hide intake list and show selected slip's diagnosis list plus row-creation form. Use `partials/view-switch.js` and `[data-view]`. This is the technician workflow and must not require opening SCR-04B first. |
| **SCR-05** `pages/scr-05.html` | Assign shifts. Left employee list/search by code or name, filter by role, datatable style. Right side: choose week → weekday → shift with Swing-style radios; optional description and `Gán Ca`; include table of assignments for the week. Duplicate assignment key is `(maNhanVien, nam, thang, ngay, caLamViec)`. Mockup remains visual-only. Shortcut F9. |
| **SCR-05B** `pages/scr-05b.html` | Read-only schedule for current employee by week. Columns Monday–Sunday; rows `Ca sáng`, `Ca chiều`, `Ca ngày`. Navigation `‹ Tuần`, `Tuần ›`, `Tuần này`; include shift hours and total shifts. Panel height follows table content. |
| **SCR-05C** `pages/scr-05c.html` | Read-only shop-wide weekly schedule. Each shift cell lists assigned staff; include week navigation and total assignments. |
| **SCR-06** `pages/scr-06.html` | Parts/inventory management. 1:1 color product image, per-column filtering, low-stock warning when stock `<= 5`, standard management template. Shortcut F4. |
| **SCR-07** `pages/scr-07.html` | Create parts receipt. Include batch description `moTa`, item-detail rows and visually represented line/overall totals. No supplier field. Receipt code/date are generated and not editable/displayed as inputs. Shortcut F5. Do not add JS business calculations to the static mockup. |
| **SCR-07B** `pages/scr-07b.html` | Full-width parts receipt list; column filters; `Sửa` navigates to SCR-07. No left form. |
| **SCR-08** `pages/scr-08.html` | Garage service price list: labor price, 1:1 color service-process image, per-column filters, standard management template. Shortcut F6. |
| **SCR-09** `pages/scr-09.html` | Voucher management: discount code, percentage discount, expiry, active/inactive state; per-column filters including state/effectiveness; standard management template. Shortcut F7. |
| **SCR-10** `pages/scr-10.html` | Invoice creation, two columns. Left: invoice info and payment block; bottom `Lưu Hóa Đơn` and `Xác Nhận Thanh Toán [Enter]` aligned horizontally. Right: item table and totals. Row controls: `+ Thêm Dịch Vụ`, `+ Thêm Phụ Tùng`, `Xóa Dòng`. Invoice controls: `Làm Mới`, `Hủy Hóa Đơn`, `Lưu Hóa Đơn`, `Xác Nhận Thanh Toán`. No `Lưu Dự Toán`, no `In Báo Giá`. Print/export only `In Hóa Đơn` and `Xuất Excel`. Hide generated invoice code, creation date, and payment timestamp. One invoice per intake slip; saving creates one if none exists (business rule for the eventual app, not JS mockup logic). Shortcut F8. |
| **SCR-10B** `pages/scr-10b.html` | Full-width invoice list with per-column filters for code, intake slip, customer, state, etc. No left form. `Sửa` navigates to SCR-10. |
| **SCR-11** `pages/scr-11.html` | Employee account and permission management: employee data, 3:4 portrait image, roles, lock/activate states; column filters; standard management template. Row action includes reset password (`key-round`). Shortcut F10. |
| **SCR-12** `pages/scr-12.html` | Revenue report with period filter by day/month/year or custom date range; pie chart for service vs parts share using Chart.js CDN; invoice table and print/export report controls. Chart data is pre-rendered; no business calculations in page JS. |
| **SCR-12B** `pages/scr-12b.html` | Monthly parts consumption: per part show quantity In/Out, current Stock, Unit Price, Revenue, and consumption; include Top 20 most-used parts. Derived report data only; do not create storage fields for it. |
| **SCR-12C** `pages/scr-12c.html` | Monthly service consumption: count, unit price, revenue, share by service; include Top 20 most-used services. Derived report data only. |
| **SCR-15** `pages/scr-15.html` | Change password dialog for logged-in employee. Fields `matKhauHienTai`, `matKhauMoi`, `xacNhanMatKhau`; represent matching/new-password validation and update controls, without meta annotations. |
| **SCR-16** `pages/scr-16.html` | Complaint intake: customer, related intake slip, complaint content. Do not show “Đã tìm thấy” text. |
| **SCR-16B** `pages/scr-16b.html` | Complaint management using standard management template; per-column filters for code, customer, intake slip, content, and state; represent state updates. |

## 7. Data model and business constraints to reflect in the UI

Only show fields that belong to the relevant screen/table. Do not invent derived columns or editable fields. The following schemas are canonical:

- `KhachHang(maKhachHang, sdt, hoTen, ngayTao)`. No customer avatar.
- `Xe(bienSo, hangXe, dongXe, doiXe, odo)`.
- `KhachHang_Xe(maKhachHang, bienSo, ngayTao)` is an N:N **contact association**, not ownership or authorization. The association UI (SCR-03C) is removed. Do not show the association on the customer/vehicle table. SCR-03B may show associated customer information read-only in detail. Reference data (not shown as UI): `KH00000108` → `51F12345`, `51A99821`; `KH00000042` → `51G88912`; `KH00000015` → `60A33211`.
- `HoaDon(maHoaDon, maPhieuTiepNhan, maVoucher, trangThaiHoaDon, lyDoHuy, ghiChu, ngayTao, thoiGianThanhToan)`. `trangThaiHoaDon ∈ {CHUA_THANH_TOAN, DA_THANH_TOAN, DA_HUY}`. `lyDoHuy` is required if state is `DA_HUY`. `maHoaDon`, `ngayTao`, `thoiGianThanhToan` are system-generated and must not be editable input fields. Each `PhieuTiepNhan` has at most one invoice; when saving an invoice, create one if absent.
- `ChiTietHoaDon(maHoaDon, loaiSanPham, maSanPham, soLuong, donGia)`. `loaiSanPham ∈ {PHUTUNG, DICHVU}`; `soLuong > 0`; `donGia >= 0` and is a price snapshot. Total amounts are derived from detail rows and `Voucher`; do not model a redundant stored total.
- `PhieuNhapPhuTung(maPhieuNhap, ngayTao, maNhanVien, moTa)`. Code format `PN` + `YYYYMMDD` + four digits (example `PN202609270001`); code/date are system-generated. `maNhanVien` is the confirming stock clerk; `moTa` is batch note (`NVARCHAR(255)`). There is no supplier column.
- `ChiTietPhieuNhapPhuTung(maPhieuNhap, maPhuTung, soLuong, donGiaNhap)`. Composite primary key `(maPhieuNhap, maPhuTung)`; `soLuong > 0`; `donGiaNhap >= 0`; deleting a receipt cascades to its details. Line total (`Thành Tiền`) and receipt total (`Tổng Chi Phí`) are derived, not stored.
- Monthly part report: In = `SUM(ChiTietPhieuNhapPhuTung.soLuong)` joined to `PhieuNhapPhuTung.ngayTao`; Out/consumption = `SUM(ChiTietHoaDon.soLuong)` where `loaiSanPham='PHUTUNG'`, joined to `HoaDon.ngayTao`; Stock = current `PhuTung.soLuongTon`.
- Monthly service report: quantity = `SUM(ChiTietHoaDon.soLuong)` where `loaiSanPham='DICHVU'`; revenue = `SUM(soLuong * donGia)`. Consumption reports are month-based and show Top 20; they are derived, not separately stored.
- `VanDeKyThuat(maVanDeKyThuat, maPhieuTiepNhan, tieuDe, moTa, mucDoUuTien, khachHangDongY, giaiPhap)`. Code format `VD` + `YYYYMMDD` + four digits. Priority 1–100 (100 highest). `khachHangDongY` is nullable BIT: UI states Có / Không / Chưa xác nhận. `giaiPhap` may be null.
- `KhieuNai(maKhieuNai, maKhachHang, maPhieuTiepNhan, noiDung, trangThai, ngayTao)`. Code format `KN` + `YYYYMMDD` + four digits (example `KN202609270001`). `trangThai ∈ {CHUA_XU_LY, DANG_XU_LY, DA_XU_LY}`. `maKhachHang` references `KhachHang`; `maPhieuTiepNhan` references `PhieuTiepNhan`; `noiDung` is long text (`NVARCHAR(MAX)`).

## 8. Enum display and semantic colors

Never show raw enum codes in table cells. Keep codes as data values (database values, `<option value>`, `data-value`) and render the Vietnamese label inside a semantic badge, except shifts, which use plain text without a badge.

| Enum group | Stored code | UI label | Style |
|---|---|---|---|
| Invoice | `CHUA_THANH_TOAN` | Chưa thanh toán | warning |
| Invoice | `DA_THANH_TOAN` | Đã thanh toán | success |
| Invoice | `DA_HUY` | Đã hủy | danger |
| Intake progress | `MOI` | Mới | info |
| Intake progress | `KIEM_TRA` | Đang kiểm tra | info |
| Intake progress | `DANG_TRIEN_KHAI` | Đang triển khai | warning |
| Intake progress | `CHO_BAN_GIAO` | Chờ bàn giao | warning |
| Intake progress | `HOAN_THANH` | Hoàn thành | success |
| Complaint | `CHUA_XU_LY` | Chưa xử lý | warning |
| Complaint | `DANG_XU_LY` | Đang xử lý | info |
| Complaint | `DA_XU_LY` | Đã xử lý | success |
| Employee account | `DANG_LAM_VIEC` | Đang làm việc | success |
| Employee account | `TAM_KHOA` | Tạm khóa | warning |
| Employee account | `DA_NGHI_VIEC` | Đã nghỉ việc | danger |
| Role | `QUAN_LY` | Quản lý | info |
| Role | `KY_THUAT_VIEN` | Kỹ thuật viên | info |
| Role | `THU_KHO` | Thủ kho | info |
| Role | `NHAN_VIEN_TIEP_NHAN` | Nhân viên tiếp nhận | info |
| Role | `THU_NGAN` | Thu ngân | info |
| Product type | `DICHVU` | Dịch vụ | info |
| Product type | `PHUTUNG` | Phụ tùng | success |
| Shift | `SANG` | Ca sáng | no badge |
| Shift | `CHIEU` | Ca chiều | no badge |
| Shift | `CA_NGAY` | Ca ngày | no badge |

State emphasis: invoice paid=success, unpaid=warning, canceled=danger; intake inspection=info, in progress/awaiting handover=warning; low stock=warning. Official role groups described for the application are (1) Nhân viên tiếp nhận — intake, customer and vehicle profiles; (2) Nhân viên kỹ thuật — workshop progress and services; (3) Nhân viên kho — inventory and receipts; (4) Nhân viên quản lý — administration, scheduling, accounts. The display mapping also includes `THU_NGAN` (Thu ngân); preserve existing screen/schema values rather than silently deleting or inventing a role if this distinction appears in code.

## 9. Implementation workflow and acceptance check

1. Identify the target screen by its ID and file path in this skill. Inspect that page, the shared CSS/partials it uses, and the closest matching existing screen.
2. Match its exact screen-registry rules and template. If this specification does not resolve a detail, preserve the current established implementation and call out the ambiguity instead of inventing UI/business rules.
3. Make the smallest complete edit. Reuse existing tokens/components/partials and keep screen-specific exceptions intact.
4. Review fields and table columns against the data model; verify enum values/labels/colors, navigation, shortcut mapping, icon conventions, image ratio, filter placement, and absence of responsive CSS or business JS.
5. Inspect the diff for duplicated CSS/JS, unrelated changes, dead links, accidental columns, metadata text, broken relative paths, and unsupported controls.
6. When possible, open the page through a local static HTTP server to verify `fetch()` partials, shell, filters, icons and layout. Do not claim tests that were not performed.

Before reporting completion, state the files changed, what changed, checks actually performed, and any remaining uncertainty. Never claim that dynamic business behavior works: this is a static visual mockup.