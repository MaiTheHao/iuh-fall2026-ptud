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

## 2. Edit Modal Dialogs

- For quick inline editing or detail popups, use `SuaDialog` or equivalent modal dialogs.
- Wrap content in `Theme.contentPanel(12)`.
- Use `Theme.titledBorder(...)` and standard buttons (`applyPrimary`, `applySecondary`).
- Do NOT build custom `BorderFactory` borders or hardcode colors.

## 3. Alerts & Confirmations (`ThongBao`)

- All user messages and dialog popups must use `iuh.fit.gui.component.ThongBao`:
  - `ThongBao.thongTin(parent, key, args...)`
  - `ThongBao.thanhCong(parent, key, args...)`
  - `ThongBao.canhBao(parent, key, args...)`
  - `ThongBao.loi(parent, key, args...)`
  - `ThongBao.xacNhan(parent, key, args...)` -> returns `boolean`
- Keys must exist in both `i18n_vi.json` and `i18n_en.json`.
- Catch domain exceptions (`IllegalArgumentException`, `IllegalStateException`) and pass their localized message key to `ThongBao.loi`.
