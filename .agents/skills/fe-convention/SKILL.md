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
3. **Fixed Desktop Target**: The desktop window is sized for `1280×800` (minimized/restored) and maximized upon login. Standard split panes have divider location at `480px`. Forms have a fixed column width of `480px`. Table row height is `34px`. No dynamic percentage scaling or custom responsive reflow.
4. **Standard Management Pattern**: Management screens consist of `ManagementPanel` (left/main: search bar, debounced filters, table, pagination, action column) and `ManagementFormPanel` (right: form card + 4 standard buttons `min 100×36`) embedded in a `JSplitPane`.
5. **Standard Form Fields**: Always use `FormFieldPanel.addField(labelKey, component)`. Never lay out label/field pairs manually. Currency and numeric inputs must be right-aligned (`setHorizontalAlignment(JTextField.RIGHT)`).
6. **Modal Pickers**: Selecting a record from an unbounded dataset (customers, spare parts, vehicles) must use a modal dialog returning `Optional<T>`, never an unpaged combo box.
7. **Strict i18n Synchronization**: All visible labels, tooltips, placeholders, and dialog messages must be i18n keys present in both `src/resources/i18n_vi.json` and `src/resources/i18n_en.json`.
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
│   ├── ManagementPanel.java    # Search bar (Lucide search icon), debounced filters, JTable (zebra, 34px), pagination
│   ├── ManagementFormPanel.java# Card container, titled border, 4 action buttons (add, save, delete, clear)
│   ├── FormFieldPanel.java     # Standardized GridBag form layout with uniform insets & label fonts
│   ├── BoChonNgayGio.java      # Date/DateTime pickers with locale & clean popup styling
│   ├── BoHienThiI18n.java      # Localized combo box renderers for domain enums
│   ├── LucideIcon.java         # Vector-quality Lucide icons loaded from PNG and tinted via Theme
│   ├── ThaoTacTableCellRenderer.java # Table row action buttons rendered as pill badges with hover states
│   ├── ChonPhuTungDialog.java  # Modal picker for spare parts
│   ├── SuaDialog.java          # Modal edit dialog adhering to Theme.contentPanel
│   └── ThongBao.java           # Standard alert dialogs (info, success, warning, error, confirm)
├── form/                       # Feature-specific form panels (e.g., NhanVienFormPanel, XeFormPanel)
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
- Wrap `ManagementPanel` (left) and `ManagementFormPanel` (right) in a `JSplitPane`:
  ```java
  JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.managementPanel, this.formPanel);
  splitPane.setDividerLocation(480);
  splitPane.setResizeWeight(1.0);
  ```
- Clear listeners before reattaching on subsequent `render()` calls (`clearActionListeners(...)`).

### 4.2. Input Formatting
- **Dates**: Always use `BoChonNgayGio.taoBoChonNgay()` / `taoBoChonNgayGio()`. Read as `LocalDate` / `LocalDateTime`.
- **Numbers & Currency**: Right-align the field: `field.setHorizontalAlignment(JTextField.RIGHT);`.
- **Enum Combos**: Use `BoHienThiI18n.tao()` with a leading `null` item for "All" filters.

### 4.3. Modern Shell Header (`LayoutHeader`)
- Fixed height: `52px`.
- Left: Navigation items with Lucide icons. Current page receives an active underline (`veChiBaoDuoi`).
- Right: User badge (Full name + Role), language toggle pill (shows VI/EN, triggers on click or F11), and logout icon button.

---

## 5. References Index

| Topic | Reference Document |
|---|---|
| Full UI Architecture & Navigation lifecycle | [01-swing-architecture.md](references/01-swing-architecture.md) |
| FlatLaf 3.5.4 setup & Theme Palette tokens | [02-theme-tokens.md](references/02-theme-tokens.md) |
| ManagementPanel, Forms & Field styling | [03-components-and-forms.md](references/03-components-and-forms.md) |
| Dialogs, Modal Pickers & Confirmation | [04-dialogs-and-pickers.md](references/04-dialogs-and-pickers.md) |
| Frontend Verification & Grep Checklist | [05-fe-checklist.md](references/05-fe-checklist.md) |
