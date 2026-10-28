# 02 — Theme and Visual Tokens

`iuh.fit.gui.theme.Theme` is the single source of truth for color, font, border, spacing, and state styling. Powered by **FlatLaf 3.5.4** Look & Feel initialized via `Theme.install()`.

## Two Access Paths

**Static fields** — common tokens:
`BG`, `PANEL`, `BORDER`, `ACCENT`, `INK`, `WARNING_TEXT`, `DANGER_TEXT`

**`Theme.palette()`** — full semantic `Palette` record:
- **Surfaces**: `BG (#F8FAFC)`, `PANEL (#FFFFFF)`, `PANEL_ALT (#F8FAFC)`, `BORDER (#E2E8F0)`, `BORDER_STRONG (#CBD5E1)`
- **Accent**: `ACCENT (#2563EB)`, `ACCENT_HOVER (#1D4ED8)`, `ACCENT_PRESSED (#1E40AF)`, `ACCENT_SOFT (#EFF6FF)`, `ON_ACCENT (#FFFFFF)`
- **Ink**: `INK (#111827)`, `INK_SECONDARY (#475569)`, `INK_MUTED (#64748B)`
- **Inputs**: `INPUT_BG (#FFFFFF)`, `INPUT_BORDER (#CBD5E1)`, `FOCUS (#60A5FA)`
- **Tables**: `TABLE_HEADER_BG (#F1F5F9)`, `TABLE_ROW_ALT (#F8FAFC)`, `TABLE_SELECTION_BG (#DBEAFE)`, `TABLE_SELECTION_FG (#1E3A8A)`
- **Feedback**: `INFO*`, `SUCCESS*`, `WARNING*`, `DANGER*`

## Helper API

| Requirement | Call |
|---|---|
| Primary button (Blue) | `applyPrimary(nut)` |
| Secondary button (Muted) | `applySecondary(nut)` |
| Danger button (Red) | `applyDanger(nut)` |
| Compact pill button | `applyPillButton(button, bg, fg)` |
| Card style container | `applyCardStyle(panel)` (white bg, subtle border, 16px padding) |
| Panel with border + padding | `contentPanel(padding)` |
| Standard subtle line border | `vienNhat()` |
| Uniform empty border | `leDeu(padding)` |
| Top / Bottom matte border | `vienTren()`, `vienDuoi()` |
| Titled border from i18n key | `titledBorder(key)` |
| Form label font & insets | `fieldLabelFont()`, `fieldRowInsets()` (8, 10, 8, 10) |
| Read-only / Editable field | `applyReadOnly(field)`, `applyEditable(field)` |
| Table styling | `styleTable(table)` |

## Rules

R1. No `new Color(...)`, `new Font(...)`, hex literal, or `BorderFactory.*` outside `gui/theme`. Use `Theme.vienNhat()`, `Theme.leDeu(n)`, `Theme.contentPanel(...)`, or `Theme.applyCardStyle(...)`.
R2. Express states with tokens: hover `ACCENT_HOVER`, focus `FOCUS`, selection `TABLE_SELECTION_BG`, muted/helper text `INK_MUTED`.
R3. Text over image or scrim uses `chuTrenAnh()`. Never use `chuTrenAnh()` on a light panel — use `INK`.
R4. FlatLaf configuration in `install()` sets global defaults: `Button.arc = 8`, `Component.arc = 8`, `ScrollBar.thumbArc = 999`, `Table.rowHeight = 34`, and enables system text anti-aliasing.
R5. `LoginPage`, `DashboardPage`, `PlaceholderPage`, and `SuaDialog` are fully clean of inline color/font drift; keep them clean.
