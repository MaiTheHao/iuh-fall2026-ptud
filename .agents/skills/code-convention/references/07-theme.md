# 07 — Theme and visual tokens

`iuh.fit.gui.theme.Theme` is the only source of color, font, border, spacing, and state styling.

## Two access paths

**Static fields** — the seven used on most screens:

`BG`, `PANEL`, `BORDER`, `ACCENT`, `INK`, `WARNING_TEXT`, `DANGER_TEXT`

**`Theme.palette()`** — the full `Palette` record, for everything else:

| Group | Tokens |
|---|---|
| Surfaces | `BG`, `PANEL`, `PANEL_ALT`, `BORDER`, `BORDER_STRONG` |
| Accent | `ACCENT`, `ACCENT_HOVER`, `ACCENT_PRESSED`, `ACCENT_SOFT`, `ON_ACCENT` |
| Ink | `INK`, `INK_SECONDARY`, `INK_MUTED` |
| Inputs | `INPUT_BG`, `INPUT_BORDER`, `FOCUS` |
| Tables | `TABLE_HEADER_BG`, `TABLE_ROW_ALT`, `TABLE_SELECTION_BG`, `TABLE_SELECTION_FG` |
| Info | `INFO`, `INFO_BG`, `INFO_BORDER`, `INFO_TEXT` |
| Success | `SUCCESS`, `SUCCESS_BG`, `SUCCESS_BORDER`, `SUCCESS_TEXT` |
| Warning | `WARNING`, `WARNING_BG`, `WARNING_BORDER`, `WARNING_TEXT` |
| Danger | `DANGER`, `DANGER_BG`, `DANGER_BORDER`, `DANGER_TEXT` |

There is no `TABLE_SELECTION_*` or `ACCENT_HOVER` static field — those require `palette()`.

## Helper API

| Need | Call |
|---|---|
| Primary / secondary / danger button | `applyPrimary(nut)`, `applySecondary(nut)`, `applyDanger(nut)` |
| Panel with border + padding | `contentPanel(padding)` |
| Titled border from an i18n key | `titledBorder(key)` |
| Form label font | `fieldLabelFont()` |
| Form row insets | `fieldRowInsets()` |
| Read-only field / editable field | `applyReadOnly(field)`, `applyEditable(field)` |
| Table colors | `styleTable(table)` |
| Text over the background image | `chuTrenAnh()`; overlay `scrim()`, card `phuSangAnh()` |
| Install UIManager defaults | `install()`, once at startup |

## Rules

R1. No `new Color(...)`, `new Font(...)`, hex literal, or `BorderFactory.createLineBorder/createEmptyBorder/createCompoundBorder` outside `gui/theme`.

R2. Missing token: add it to the `Palette` record and the `LIGHT` instance first, then reuse it. Promote it to a static field only when three or more screens need it.

R3. Express state with tokens: hover `ACCENT_HOVER`, pressed `ACCENT_PRESSED`, focus `FOCUS`, selection `TABLE_SELECTION_BG`/`TABLE_SELECTION_FG`, helper/disabled text `INK_MUTED`, secondary text `INK_SECONDARY`, danger `DANGER*`, feedback `INFO*`, `SUCCESS*`, `WARNING*`.

R4. `chuTrenAnh()` is only for text over the background image or scrim. Never on a light panel — use `INK`.

R5. One widget role, one token: list title and form title both use `titledBorder(key)`; both primary actions use `applyPrimary`.

R6. `Theme` is not a utility grab-bag — no domain constants, no layout sizes for individual screens.

## Reference code

- `src/iuh/fit/gui/theme/Theme.java` — `Palette`, `LIGHT`, static fields, helpers, `install`.
- `src/iuh/fit/gui/component/ManagementFormPanel.java` — `titledBorder` + `applyPrimary/applyDanger/applySecondary`.
- `src/iuh/fit/gui/component/ManagementPanel.java#buildRangeItem` — `palette().INK_MUTED()`.
- `src/iuh/fit/gui/component/FormFieldPanel.java#addField` — `fieldLabelFont()` + `INK`.

## Anti-patterns

- `new Color(0x25, 0x63, 0xEB)` or `new Font("SansSerif", Font.BOLD, 14)` in a page.
- `BorderFactory.createEmptyBorder(8, 8, 8, 8)` written per screen.
- `chuTrenAnh()` on a light panel.
- Styling one widget role with two tokens (list title `INK` vs form title `ACCENT`).
- Adding a static field for a color used once.
- Hardcoding a font size instead of deriving from the UIManager default.

## Known drift (do not extend)

`src/iuh/fit/gui/page/LoginPage.java` and `src/iuh/fit/gui/page/DashboardPage.java` still build raw `Font`/`Color` values for the artwork and charts. `LoginPage`, `DashboardPage`, `PlaceholderPage`, and `gui/component/SuaDialog.java` also build `BorderFactory` borders inline. Treat them as legacy: add no new literals, and convert the values to tokens when your task already touches that screen.

## Check

```bash
grep -rn 'new Color(\|new Font(' src/iuh/fit/gui | grep -v '/theme/'        # expect: known drift only
grep -rn 'createLineBorder\|createEmptyBorder\|createCompoundBorder' src/iuh/fit/gui | grep -v '/theme/'   # expect: known drift only
grep -rn '#[0-9A-Fa-f]\{6\}' src/iuh/fit/gui | grep -v '/theme/'            # expect: none
```

## Related

- [06-swing-ui](06-swing-ui.md), [08-dialogs](08-dialogs.md).
