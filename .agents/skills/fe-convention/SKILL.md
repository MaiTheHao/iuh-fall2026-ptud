---
name: fe-convention
description: Enforce Gara Ô tô Lúa Java 17 Swing & FlatLaf frontend conventions for all files under `src/iuh/fit/gui` (theme, component, layout, page, form, dialog, renderer, i18n). Use this skill whenever reading, creating, modifying, reviewing, or refactoring UI screens, forms, layouts, dialogs, FlatLaf styling, Theme tokens, Lucide icons, or user-visible text keys.
---

# Frontend Conventions — Gara Ô tô Lúa (Java 17 Swing + FlatLaf)

This skill governs all user interface development in `src/iuh/fit/gui`. The UI is built on standard **Java 17 Swing** powered by **FlatLaf 3.5.4** as the Look & Feel engine and centralized through `iuh.fit.gui.theme.Theme`.

---

## 1. Non-Negotiables for Frontend

1. **Strict Layer Boundary**: GUI only communicates with `iuh.fit.service.*` and `iuh.fit.dto.*`. **NEVER** import or reference `dao`, `java.sql.*`, or execute SQL queries in GUI code.
2. **Single Source of Visual Styling**: All colors, fonts, borders, insets, and visual states come from `Theme.java` and `Theme.palette()`. **NEVER** use `new Color(...)`, `new Font(...)`, hex literals, or raw `BorderFactory` calls in pages or forms.
3. **Fixed Desktop Target**: The desktop window is sized for `1280×800` (minimized/restored) and maximized upon login. Management screens use a full-width data grid (no side form column). Modal form dialogs open at `760×800` (minimum `520×320`). Table row height is `34px` (`44px` when an action column is present, `72px` when a thumbnail column is present). No dynamic percentage scaling or custom responsive reflow.
4. **Standard Management Pattern**: Management screens consist of a single full-width `ManagementPanel` containing the filter bar, an on-table action toolbar (`setAddAction` / `setBatchAction`), a `JTable` with an optional selection checkbox column, an action column (`addActionColumn`), and pagination. Create/update uses `BaseFormDialog` (modal) hosting a feature form panel; the legacy `ManagementFormPanel` + `JSplitPane` layout is only allowed on screens not yet migrated.
5. **Standard Form Fields**: Always use `FormFieldPanel.addField(labelKey, component)`. Never lay out label/field pairs manually. Currency and numeric inputs must be right-aligned (`setHorizontalAlignment(JTextField.RIGHT)`). Inline validation uses `FormValidationHelper` (`FlatClientProperties.OUTLINE = "error"`) and must focus the first invalid field.
6. **Modal Pickers**: Selecting a record from an unbounded dataset (customers, spare parts, vehicles) must use a modal dialog returning `Optional<T>`, never an unpaged combo box. The only approved exception is `ComboTimKiem<T>` for a quick lookup by a unique key (phone number, licence plate): 300 ms debounce, at most 10 rows, and selecting a row fills the related fields.
7. **Strict i18n Synchronization**: All visible labels, tooltips, placeholders, and dialog messages must be i18n keys present in both `src/resources/i18n_vn.json` and `src/resources/i18n_en.json`.
8. **Event Handler Simplicity**: Listeners must only do 3 things: (1) read and wrap UI inputs into DTOs, (2) call SERVICE, and (3) display the outcome via `ThongBao`. No validation logic or calculations inside listeners.

---

## 2. Architecture & File Structure (`src/iuh/fit/gui`)

```
src/iuh/fit/gui/
├── GUI.java                    # Window lifecycle, anti-aliasing text settings, root frame
├── theme/
│   └── Theme.java              # FlatLaf setup, semantic Palette record, style helpers, UIManager defaults
├── layout/
│   ├── Layout.java             # Main application shell with CardLayout & background art
│   ├── LayoutHeader.java       # Modern 52px app header (top menus, user badge, VI/EN toggle, logout)
│   └── MenuRegistry.java       # Menu tracking, active underline indicators, permission filtering
├── component/
│   ├── ManagementPanel.java    # Full-width grid: search, filters, action toolbar (add/batch), checkbox + action columns, pagination
│   ├── ManagementFormPanel.java# LEGACY right-side form card (non-migrated screens only)
│   ├── BaseFormDialog.java     # Standard modal dialog for create/update (header, scrollable body, ESC/Enter footer)
│   ├── FormValidationHelper.java # Inline FlatLaf error outline helpers + focus/auto-clear
│   ├── FormFieldPanel.java     # Standardized GridBag form layout with uniform insets & label fonts
│   ├── BoChonNgayGio.java      # Date/DateTime pickers with locale & clean popup styling
│   ├── BoHienThiI18n.java      # Localized combo box renderers for domain enums
│   ├── LucideIcon.java         # Vector-quality Lucide icons loaded from PNG and tinted via Theme
│   ├── ThaoTacTableCellRenderer.java # Table row action buttons rendered as pill badges with hover states
│   ├── ChonSanPhamDialog.java  # Abstract modal picker base (search, 250 ms debounce, quantity)
│   ├── ComboTimKiem.java       # Editable combo: 300 ms debounced lookup by unique key, max 10 rows
│   ├── ChonPhuTungDialog.java  # Modal picker for spare parts
│   ├── ChonDichVuDialog.java   # Modal picker for services
│   └── ThongBao.java           # Standard alert dialogs (info, success, warning, error, confirm)
├── form/                       # Feature form panels: XxxFormPanel (shared base) + XxxTao/XxxSuaFormPanel per dialog
└── page/                       # Screen implementations extending AbstractPage (LoginPage, DashboardPage, etc.)
```

---

## 3. Theme & Visual Tokens Quick Reference

Access tokens via static fields or `Theme.palette()`:

```java
// Common static fields
Theme.BG;               // Slate-50 (#F8FAFC) - Background for screens and shell
Theme.PANEL;            // White (#FFFFFF) - Panel and card backgrounds
Theme.BORDER;           // Cool Gray (#E2E8F0) - Standard subtle borders
Theme.ACCENT;           // Electric Blue (#2563EB) - Primary actions, active highlights
Theme.INK;              // Slate-900 (#111827) - Primary text
Theme.DANGER_TEXT;      // Red (#DC2626) - Destructive text/alerts

// Key Helper Methods
Theme.applyCardStyle(panel);            // Makes panel white with Theme.vienNhat() and 16px padding
Theme.applyPrimary(button);             // Accent blue button, white text, focus painted false
Theme.applySecondary(button);           // Light gray button, dark text
Theme.applyDanger(button);              // Red danger button
Theme.applyPillButton(button, bg, fg);  // Compact rounded action button
Theme.contentPanel(padding);            // White panel with subtle border and specified padding
Theme.titledBorder("i18n.key");         // Standard titled border with Theme font and color
Theme.vienNhat();                       // Subtle 1px line border
Theme.leDeu(padding);                   // Empty padding border
Theme.fieldRowInsets();                 // Standard insets: (8, 10, 8, 10)
Theme.fieldLabelFont();                 // Bold label font
Theme.applyReadOnly(textField);         // Soft accent background for read-only fields
Theme.applyEditable(textField);         // Standard input background
```

---

## 4. UI Patterns & Screen Building

### 4.1. Building a Management Screen
Every management screen extends `AbstractPage`:
- Register in `PageRegistry` and its menu in `MenuRegistry`.
- Override `render()` to build or refresh the UI.
- Use a single full-width `ManagementPanel`:
  ```java
  this.panel = new ManagementPanel();
  this.panel.setListTitle("app.khachHang.danhSachTitle");
  this.panel.enableMultiSelection(true);        // call BEFORE setHeaders
  this.panel.setHeaders(...);
  this.panel.addActionColumn("app.common.actions",
          new ThaoTac("pencil", "app.common.edit", ThaoTac.KieuThaoTac.THUONG, this::moDialogSua),
          new ThaoTac("trash-2", "app.common.delete", ThaoTac.KieuThaoTac.NGUY_HIEM, this::xoa));
  this.panel.setAddAction("app.common.add_new", this::moDialogThem);
  this.panel.setBatchAction("app.common.batch_delete", "trash-2",
          ThaoTac.KieuThaoTac.NGUY_HIEM, this::xoaNhieu);
  this.panel.setRowDoubleClickListener(this::moDialogSua);
  this.add(this.panel, BorderLayout.CENTER);
  ```
- With multi-selection enabled, model column `0` is the selection checkbox; data columns shift by one (`COT_MA = 1`, …). Define index constants instead of magic numbers.
- Batch deletes run row by row and report a partial result (`app.common.batch_delete_result`) when foreign keys reject some rows.
- Clear listeners before reattaching on subsequent `render()` calls (`clearActionListeners(...)`).

### 4.2. Input Formatting
- **Dates**: Always use `BoChonNgayGio.taoBoChonNgay()` / `taoBoChonNgayGio()`. Read as `LocalDate` / `LocalDateTime`.
- **Numbers & Currency**: Right-align the field: `field.setHorizontalAlignment(JTextField.RIGHT);`.
- **Enum Combos**: Use `BoHienThiI18n.tao()` with a leading `null` item for "All" filters.

### 4.3. Modern Shell Header (`LayoutHeader`)
- Fixed height: `52px`.
- Left: Navigation items with Lucide icons. Current page receives an active underline (`veChiBaoDuoi`).
- Right: User badge (Full name + Role), language toggle pill (shows VI/EN, triggers on click or F11), and logout icon button.

### 4.4. Create / Update Modal Dialogs
- Use `BaseFormDialog` with `BaseFormDialogConfig(owner, titleKey, formContent, onSave, chieuCao)`; it wraps content in a scroll pane, wires Save as default button and ESC to close, and returns `showAndGet()`.
- Width is fixed at `760`. Height is declared explicitly by the page with two constants (`CHIEU_CAO_TAO` / `CHIEU_CAO_SUA`) and capped at `800` by the dialog. Never compute height from field counts.
- One panel class per dialog: `XxxTaoFormPanel` for create, `XxxSuaFormPanel` for update. Both extend the shared `XxxFormPanel` base (fields, `napDuLieu`, `layDuLieu`, validation). Never pass a mode flag into a panel, and never reuse a panel instance across dialogs.
- Read-only detail dialogs set `chiDoc = true`: no Save, only `app.common.close`. They use a dedicated `XxxChiTietFormPanel` and a page `CHIEU_CAO_CHI_TIET` constant.
- `XxxTaoFormPanel` only lays out create fields; `XxxSuaFormPanel` lays out update fields, loads the record, marks immutable keys read-only (`Theme.applyReadOnly`), and leaves optional fields such as a new password blank.
- Mode-specific rules override `kiemTraRieng()` on the panel that needs them; shared rules live in `kiemTraChung()`.
- `onSave` must return `false` and leave the dialog open when validation or the service call fails.
- Validation lives in `form.validateInput()`: mark fields with `FormValidationHelper.kiemTra(...)` / `markError(...)`, focus the first invalid component, and rely on `autoClearOnInput(...)` for text fields.

---

## 5. References Index

| Topic | Reference Document |
|---|---|
| Full UI Architecture & Navigation lifecycle | [01-swing-architecture.md](references/01-swing-architecture.md) |
| FlatLaf 3.5.4 setup & Theme Palette tokens | [02-theme-tokens.md](references/02-theme-tokens.md) |
| ManagementPanel, Forms & Field styling | [03-components-and-forms.md](references/03-components-and-forms.md) |
| Dialogs, Modal Pickers & Confirmation | [04-dialogs-and-pickers.md](references/04-dialogs-and-pickers.md) |
| Frontend Verification & Grep Checklist | [05-fe-checklist.md](references/05-fe-checklist.md) |
