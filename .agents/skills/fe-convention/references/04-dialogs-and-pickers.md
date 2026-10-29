# 04 — Dialogs and Pickers

## 1. Unbounded Record Selection (Pickers)

- Never use a raw combo box or unpaged dropdown when selecting from an unbounded collection (e.g., thousands of customers, vehicles, or spare parts).
- Use a modal picker dialog extending `ChonSanPhamDialog<T>`; its single public entry point `showDialog()` returns `Optional<KetQuaChon<T>>` (`sanPham()`, `soLuong()`). One instance per open — a second `showDialog()` throws `IllegalStateException`.
- The picker takes the service plus the feature `DanhSach...Criteria` and loads through `search(criteria)` (returns `PaginatedResult`), then uses `.items()` for its result table. Never pass a preloaded `List` from the page.
- Search must cover code, name and description, use `TextNormalizerUtil.normalize` (case/diacritics-insensitive) and debounce 250 ms. Parts also offer the "in stock only" filter.
- Full-load pickers pass `PaginatedResult.PAGE_SIZE_TAI_TOAN_BO` (10_000) as `pageSize` with `page = 1`. The cap bounds how many rows are built on the UI; it is not an overflow guard.
- Put every filter the query can express into the feature `DanhSach...Criteria` (`taoCriteria(tuKhoa)`): the keyword plus the base filters the page hands over. Exception: a filter that depends on the picker's display mapping (e.g. "in stock only", where available = DB stock + reserved − already selected) filters the mapped rows in `taiDuLieu`, because filtering on the DB value would hide parts still available on the current invoice. Report truncation from `PaginatedResult` only (`tongSoKetQuaCanBao`), never from a pre-filter total.
- `ChonSanPhamDialog` is the base; `ChonPhuTungDialog` and `ChonDichVuDialog` are the reference subclasses.
- Approved exception — `ComboTimKiem<T>`: allowed only for a quick lookup by a unique key (phone number, licence plate) inside a data-entry form. `NguonTim<T>` returns `List<T>` from `search(criteria)` with `page = 1` and `pageSize = 10`; `nhanHienThi` renders each popup row; `giaTriNhap` writes the editor text when a row is picked. It debounces 300 ms, shows at most 10 rows, and selection fills the related fields. Never use it for a catalogue that needs multi-step browsing or filtering — those still require a modal picker.

## 2. Create / Update Modal Dialogs

- Use `BaseFormDialog` for every create/update form, configured through the `iuh.fit.dto.BaseFormDialogConfig` record:
  ```java
  BaseFormDialog dialog = new BaseFormDialog(new BaseFormDialogConfig(
          SwingUtilities.getWindowAncestor(this),
          "app.khachHang.themTitle", form, () -> this.luu(form), CHIEU_CAO_TAO));
  if (dialog.showAndGet()) { this.taiLai(1); }
  ```
- `BaseFormDialogConfig` carries `owner`, `titleKey`, `noiDungForm`, `xuLyLuu`, `chieuCao` and `chiDoc`. Width is fixed at `760`; the page declares `CHIEU_CAO_TAO` / `CHIEU_CAO_SUA` / `CHIEU_CAO_CHI_TIET` explicitly and the dialog caps height at `800`. No height formula, no default height.
- Read-only detail dialogs pass `chiDoc = true` (6-arg constructor): no Save button, the remaining button is `app.common.close`, ESC closes. Use `CHIEU_CAO_CHI_TIET` for the page constant.
- `SuaDialog` no longer exists. Every edit popup goes through `BaseFormDialog`; when the save lambda needs a parent window, use `SwingUtilities.getWindowAncestor(form)` instead of the not-yet-constructed dialog.
- All GUI DTOs, including this config record, live in `src/iuh/fit/dto`.
- The dialog owns the header (`TitledBorder.font`), the borderless scrollable body, the Save default button (Enter) and ESC-to-close.
- One panel class per dialog: `new XxxTaoFormPanel()` for create and `new XxxSuaFormPanel()` for update, both sharing the abstract `XxxFormPanel` base. Never pass a mode flag; never share one panel between the table and the dialog.
- The `onSave` supplier must return `false` on validation/service failure so the dialog stays open, and `true` only after the service call succeeds.
- Do NOT keep a separate edit-dialog class; `BaseFormDialog` is the single host for every create/update popup.
- Wrap content in `Theme.contentPanel(12)`; do NOT build custom `BorderFactory` borders or hardcode colors.

## 3. Alerts & Confirmations (`ThongBao`)

- All user messages and dialog popups must use `iuh.fit.gui.component.ThongBao`:
  - `ThongBao.thongTin(parent, key)` / `thongTinDinhDang(parent, key, args...)`
  - `ThongBao.canhBao(parent, key)` / `canhBaoDinhDang(parent, key, args...)`
  - `ThongBao.loi(parent, key)` / `loi(parent, exception)`
  - `ThongBao.xacNhan(parent, messageKey, titleKey)` -> `boolean`
  - `ThongBao.xacNhanDinhDang(parent, messageKey, titleKey, args...)` -> `boolean` (for counted batch confirms)
- Keys must exist in both `src/resources/i18n_vn.json` and `src/resources/i18n_en.json`.
- Catch domain exceptions (`IllegalArgumentException`, `IllegalStateException`) and pass their localized message key to `ThongBao.loi`.

## 4. Batch Actions

- Batch deletes/toggles confirm once with the selection count (`app.common.batch_delete_confirm`, `app.common.batch_toggle_confirm`).
- Execute row by row inside `try/catch`; a row rejected by a foreign key or business rule must not abort the loop.
- If every row succeeds show `app.common.delete_succeed`; otherwise show the aggregate report (`app.common.batch_delete_result` / `app.voucher.batch_toggle_result`) with `success/total`.
