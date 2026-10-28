# 04 — Dialogs and Pickers

## 1. Unbounded Record Selection (Pickers)

- Never use a raw combo box or unpaged dropdown when selecting from an unbounded collection (e.g., thousands of customers, vehicles, or spare parts).
- Use a modal picker dialog extending `JDialog` that returns `Optional<RecordDTO>`:
  ```java
  public Optional<PhuTungView> chon(Component parent) {
      this.setLocationRelativeTo(parent);
      this.setVisible(true);
      return Optional.ofNullable(this.ketQua);
  }
  ```
- Reference: `src/iuh/fit/gui/component/ChonPhuTungDialog.java`.

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
