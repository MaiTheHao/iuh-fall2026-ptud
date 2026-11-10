---
name: fe-convention
description: Enforce Gara Ô tô Lúa Java 17 Swing + FlatLaf frontend conventions (structure, theme, management screens, forms, dialogs, pickers, i18n, control naming) for all files under `src/iuh/fit/gui`. Use whenever creating, renaming, reading, reviewing, or refactoring UI screens, forms, dialogs, Theme usage, control names, or user-visible text, and before accepting any GUI diff.
---

# Frontend Convention — RULES (MUST / MUST NOT)

## 1. Structure & boundary

- MUST: GUI talks only to `iuh.fit.service.*` and `iuh.fit.dto.*`. MUST NOT import `dao.*` or `java.sql.*`, nor write SQL.
- MUST: every screen = `AbstractPage` subclass in `gui/page` with `render()` + `getTitleKey()`, registered in `PageRegistry` + `MenuRegistry`.
- MUST: navigate with `goTo(XxxPage.class)`; NEVER `new XxxPage()` in a listener.
- MUST: `render()` re-run calls `clearActionListeners(...)` before re-attaching.
- MUST: listener does only 3 things: read UI → wrap DTO → call SERVICE → show result via `ThongBao`. NO validation/calculation in listeners.
- Fixed desktop target: window `1280×800` (maximize on login); `LayoutHeader` 52px; row height `34` (`44` with action column, `72` with thumbnail). MUST NOT use percentage dividers, `getScreenSize`, `setResizeWeight`, or resize reflow.

## 2. Theme

- MUST: all color/font/border/inset via `Theme` / `Theme.palette()`.
- MUST NOT: `new Color(`, `new Font(`, hex literal, `BorderFactory.*` outside `gui/theme`.
- Use helpers: `applyPrimary/Secondary/Danger`, `applyPillButton`, `applyCardStyle`, `contentPanel(n)`, `vienNhat()`, `leDeu(n)`, `titledBorder(key)`, `fieldLabelFont()`, `fieldRowInsets()`, `applyReadOnly/applyEditable`, `styleTable`.
- States by token: hover `ACCENT_HOVER`, focus `FOCUS`, selection `TABLE_SELECTION_BG`, helper text `INK_MUTED`. `chuTrenAnh()` only over image/scrim; on light panel use `INK`.
- Keep `LoginPage`, `DashboardPage`, `PlaceholderPage`, `BaseFormDialog` free of inline color/font.

## 3. Management screen

- MUST: one full-width `ManagementPanel` (filter bar, `setAddAction`, optional `setBatchAction`, optional checkbox column, `addActionColumn`, pagination); `setRowDoubleClickListener` opens edit.
- MUST: `enableMultiSelection(true)` BEFORE `setHeaders(...)`; column 0 becomes checkbox → data columns +1; use index constants, no magic numbers.
- MUST NOT: add own filter debounce timer (ManagementPanel has 300ms); use `JSplitPane`/`ManagementFormPanel` on migrated screens (legacy only: `DoiMatKhauPage`, `LapHoaDonPage`).
- Action icons: `pencil` (edit), `trash-2` (delete). Icon PNG MUST exist in `src/resources/icons` (missing = silently invisible).
- Only `ManagementPanel`, `ChonSanPhamDialog`, `ComboTimKiem` may create `new Timer`.
- Batch: confirm once with count; run row by row in try/catch (a rejected row MUST NOT abort loop); all ok → `app.common.delete_succeed`, else `*_batch_*_result` with `success/total`.

## 4. Forms & dialogs

- MUST: create/update via `BaseFormDialog(new BaseFormDialogConfig(owner, titleKey, form, onSave, chieuCao[, chiDoc]))`; `showAndGet()` true → reload.
- Width fixed `760`; height = explicit page constants `CHIEU_CAO_TAO/SUA/CHI_TIET` (cap `800`, min `520×320`). MUST NOT compute height from field count. Wrap content in `Theme.contentPanel(12)`.
- `onSave` MUST return `false` (dialog stays open) on validation/service failure; `true` only after service success.
- One panel class per dialog: `XxxTaoFormPanel` / `XxxSuaFormPanel` extend abstract `XxxFormPanel` (fields, `napDuLieu`, `layDuLieu`, `validateInput`, hooks `dungBoCuc()`, `kiemTraRieng()`; shared rules in `kiemTraChung()`). NO mode flag, NO `if (mode == …)`, NEVER reuse a panel instance across dialogs/table.
- Sua panel: load record, immutable keys `Theme.applyReadOnly`, optional fields (e.g. new password) left blank.
- Read-only detail: `chiDoc = true`, `XxxChiTietFormPanel`, only `app.common.close`.
- Fields: ALWAYS `FormFieldPanel.addField(labelKey, comp)`; NEVER hand-layout label/field pairs. Number/currency fields `setHorizontalAlignment(JTextField.RIGHT)`.
- Validation: `FormValidationHelper.kiemTra/markError/clearError/autoClearOnInput`; validate ALL rules, focus first invalid, return `false`; NEVER throw for user input errors. Regex/ranges MUST equal Service rules.
- Dates: ONLY `BoChonNgayGio.taoBoChonNgay()/taoBoChonNgayGio()`; pass `LocalDate/LocalDateTime` to Service, never formatted text; `LGoodDatePicker` never leaks outside `iuh.fit.gui`.
- Enum combos: `BoHienThiI18n.tao()` with leading `null` item for "All" filters.
- Icons: `new LucideIcon(name, size, color)` with Theme color.

## 5. Pickers

- Unbounded dataset (customer, part, vehicle) MUST use modal picker extending `ChonSanPhamDialog<T>`; `showDialog()` → `Optional<KetQuaChon<T>>`; one instance per open (2nd call → `IllegalStateException`). NEVER a combo box.
- Picker loads via `service.search(criteria)` → `.items()`; NEVER pass a preloaded `List`. Search covers code+name+description, `TextNormalizerUtil.normalize`, debounce 250ms. Put every DB-expressible filter in `DanhSach…Criteria`; only display-mapped filters (e.g. in-stock-only) filter in `taiDuLieu`. Full-load uses `PaginatedResult.PAGE_SIZE_TAI_TOAN_BO`, `page = 1`. Truncation notice only from `PaginatedResult.tongSoKetQuaCanBao`.
- Sole exception: `ComboTimKiem<T>` for quick lookup by a unique key (phone, plate) inside a data-entry form: `page=1,pageSize=10`, debounce 300ms, max 10 rows, selection fills related fields. NEVER for catalogues.

## 6. Messages & i18n

- ALL user messages via `ThongBao` (`thongTin`, `canhBao`, `loi(parent,key|exception)`, `xacNhan`, `*Dinh Dang` variants). Catch `IllegalArgumentException`/`IllegalStateException` → pass key to `ThongBao.loi`.
- Every label, tooltip, placeholder, dialog message MUST be an i18n key present in BOTH `src/resources/i18n_vn.json` and `i18n_en.json` (identical key sets).

## 7. Control naming

- Format: `prefix` + PascalCase Vietnamese-no-accent suffix (`btnLuu`, `txtHoTen`).
- Prefixes: `btn` JButton · `lbl` JLabel · `txt` JTextField · `txa` JTextArea · `chk` JCheckBox · `rad` JRadioButton · `cmb` JComboBox/ComboTimKiem · `lst` JList · `tbl` JTable · `mnu` JMenuBar/JMenu · `mni` JMenuItem · `frm` JFrame · `pnl` JPanel · `dlg` JDialog.
- MUST: apply to fields, `static final` controls, and new/modified params & locals. Custom component follows its base (`AccountBox extends JPanel` → `pnlAccount`).
- MUST NOT: `rdo` prefix; stacked type words (`btnButtonLuu`); prefix on `AbstractPage` (not `frm`); prefix on non-controls (services, DAOs, DTOs, workers, timers, listeners, icons → plain camelCase). Non-control constants → `UPPER_SNAKE_CASE`.
- Rename MUST hit every occurrence (decl, `new`, listeners, enable/disable, add/remove) with no behavior/layout/i18n change.

## 8. Verify before accepting a diff (all EMPTY / exit 0)

```bash
grep -rn 'import iuh\.fit\.dao\.\|java\.sql\.' src/iuh/fit/gui
grep -rn '#[0-9A-Fa-f]\{6\}' src/iuh/fit/gui | grep -v '/theme/'
grep -rn 'new Color(\|new Font(' src/iuh/fit/gui | grep -v '/theme/'
grep -rn 'getScreenSize\|setResizeWeight' src/iuh/fit/gui
grep -rn '\brdo[A-Z]' src/iuh/fit/gui
grep -rn 'new Timer' src/iuh/fit/gui   # only ManagementPanel, ChonSanPhamDialog, ComboTimKiem
javac --release 17 -encoding UTF-8 -d bin -cp "lib/*:src" $(find src -name "*.java")
```

- i18n parity: flatten `i18n_vn.json` vs `i18n_en.json` → both "missing" lists MUST be empty.
- Naming audit: scan control field declarations (type → prefix table above) → `violations: 0`.
