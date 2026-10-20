# 06 — Swing screens

Page, form, list, filters, and the fixed-size desktop target of `iuh.fit.gui`.

## Rules

R1. Every screen is an `AbstractPage` subclass in `gui/page`, implementing `render()` (build the UI) and `getTitleKey()`, registered in `PageRegistry` with its menu item in `MenuRegistry`. Navigate with `goTo(XxxPage.class)`; never `new XxxPage()` from a listener, so page state survives navigation.

R2. A management screen is `ManagementPanel` (list, quick search, filters, paging, action column) plus `ManagementFormPanel` (form + add/save/delete/clear) inside a `JSplitPane` with `setDividerLocation(480)`.

R3. Form fields are added with `FormFieldPanel.addField(labelKey, component)`, so label font, color, and insets come from `Theme`. Do not lay out label/field pairs by hand.

R4. Filters are registered through `ManagementPanel.addTextFilter / addComboBoxFilter / addDateRangeFilter / addRangeFilter`, which return a handle (`BoLocChon`, `BoLocNgay`, `BoLocKhoang`) or the field. Keep that handle in the page and read values from it. The panel already debounces changes; do not add another `Timer` or a second change listener.

R5. Every date or date-time input comes from `BoChonNgayGio.taoBoChonNgay()` / `taoBoChonNgayGio()`, which already applies the project locale and `app.date.*` labels. Read values as `LocalDate` / `LocalDateTime` and pass those to SERVICE; never pass formatted text, and never let a `com.github.lgooddatepicker` import appear outside `iuh.fit.gui`.

R6. Row actions use `addActionColumn(headerKey, ThaoTac...)` with a Lucide icon name, an i18n tooltip key, and `KieuThaoTac.THUONG | NGUY_HIEM`. No buttons or editable cells inside data cells.

R7. An event handler does three things: read input, call SERVICE, show the outcome with `ThongBao`. No validation, no SQL, no calculation, no `dao` import.

R8. Re-rendering a page calls `clearActionListeners(...)` before attaching again, so a second render does not stack listeners on a reused instance.

R9. The target is one fixed desktop size: `GUI` opens `1280×800`, the split divider is `480`, form column `480`, dialogs declare `setMinimumSize`, row height `34`, icon sizes are literals. No responsive reflow, no percentage dividers, no resize-driven recalculation.

R10. Enum-valued combo filters are built with `BoHienThiI18n.tao()` and a leading `null` ("all") item, so the label follows the locale and the value stays an enum.

## Reference code

- `src/iuh/fit/gui/page/NhanVienPage.java` — management screen skeleton.
- `src/iuh/fit/gui/component/ManagementPanel.java#addComboBoxFilter`, `#addDateRangeFilter`, `#addActionColumn`, `#setRows`, `#setPage`.
- `src/iuh/fit/gui/component/ManagementFormPanel.java` — form + button bar.
- `src/iuh/fit/gui/component/FormFieldPanel.java#addField`.
- `src/iuh/fit/gui/component/BoChonNgayGio.java` — picker factories.
- `src/iuh/fit/gui/page/AbstractPage.java` — `render`, `getTitleKey`, `goTo`, `clearActionListeners`.
- `src/iuh/fit/gui/page/PageRegistry.java`, `src/iuh/fit/gui/layout/MenuRegistry.java` — registration.

## Anti-patterns

- `JTable` built inside a page instead of using `ManagementPanel`.
- Reading a filter by column index when `add*Filter` returned a handle.
- A `Timer` in a page duplicating the panel's debounce.
- Typing dates into a `JTextField`.
- `setPreferredSize` computed from `Toolkit.getScreenSize()`.
- A listener calling `new XxxDAO()` or catching `SQLException` locally with a custom dialog.

## Check

```bash
grep -rn 'new Timer' src/iuh/fit/gui              # expect: ManagementPanel debounce only
grep -rn 'setDividerLocation' src/iuh/fit/gui | grep -v 'setDividerLocation(480)'   # expect: none
grep -rn 'getScreenSize\|setResizeWeight' src/iuh/fit/gui   # expect: none
```

## Related

- [07-theme](07-theme.md), [08-dialogs](08-dialogs.md), [09-i18n](09-i18n.md).
