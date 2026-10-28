# 03 — Components and Forms

## 1. ManagementPanel

The standard list and filter component for all management screens (full width, no side form column).

- **Quick Search**: Uses `oTimNhanh` (JTextField with Lucide `search` icon and placeholder).
- **Filter Bar**: Wrapped in `khungBoLoc` using `WrapLayout(FlowLayout.LEFT, 8, 6)`. Registered via `addTextFilter`, `addComboBoxFilter`, `addDateRangeFilter`, `addRangeFilter`. Keeps debounced listener (300ms) internally — do NOT add another timer.
- **Action Toolbar**: `setAddAction(labelKey, onAdd)` shows the primary Add button top-right above the table; `setBatchAction(labelKey, iconName, kieu, onBatch)` shows the batch button. The batch button is disabled until at least one row is selected and its label always carries the count, e.g. `Delete Selected (3)`.
- **Selection Column**: `enableMultiSelection(true)` MUST be called before `setHeaders(...)`. It inserts a `Boolean` checkbox column at model index `0`, enabling a `JCheckBox` Select-All header renderer and shifting every data column by one. Pages that never call it keep their original column indices.
- **Row Callbacks**: `setRowDoubleClickListener(Consumer<Integer> modelRow)` opens the edit dialog on double-click and skips the checkbox/action columns. Action callbacks (`ThaoTac`) also receive the model row.
- **Table**: `JTable` with row height `34px` (`44px` with an action column), no vertical grid lines, zebra striping via `prepareRenderer` using `Theme.palette().TABLE_ROW_ALT()`.
- **Action Column**: Added via `addActionColumn(headerKey, ThaoTac...)` and rendered with `ThaoTacTableCellRenderer` as compact pill buttons (36×32, 8px gap) with hover effect. Use `pencil` for Edit and `trash-2` for Delete; only use icon names that exist in `src/resources/icons`.
- **Pagination**: Positioned at bottom right on `Theme.palette().PANEL_ALT()` background with previous/next buttons and page count indicator.

## 2. BaseFormDialog & ManagementFormPanel

`BaseFormDialog` is the standard modal container for create/update:

- **Structure**: `Theme.contentPanel(12)` with a bold header label (`TitledBorder.font`), a scrollable body (borderless `JScrollPane`), and a right-aligned footer.
- **Footer**: Cancel (`app.common.cancel`) + Save (`app.common.save`); Save is the root default button (Enter), ESC closes the dialog.
- **Result**: `showAndGet()` blocks until the dialog closes and returns whether the save succeeded.
- **Size**: width is fixed at `760`; height is declared inline per page with two constants (`CHIEU_CAO_TAO` for create, `CHIEU_CAO_SUA` for update) and capped at `800` by the dialog. Do not compute height from field counts. Resizable, centered on the owner.

`ManagementFormPanel` (right-side card with Add/Save/Delete/Clear) is **legacy**: only use it on non-list workflow screens that have not migrated (`DoiMatKhauPage`, `LapHoaDonPage`). Every management/list screen uses the full-width `ManagementPanel` plus `BaseFormDialog`.

## 3. FormFieldPanel

Standard GridBagLayout helper for label-input pairs.

- Added via `addField(labelKey, component)`.
- Automatically enforces `Theme.fieldLabelFont()` and `Theme.fieldRowInsets()` (standard: `8, 10, 8, 10`).
- **Number & Currency fields**: Must be right-aligned:
  ```java
  field.setHorizontalAlignment(JTextField.RIGHT);
  ```
- **Read-only fields**: Apply `Theme.applyReadOnly(field)` (immutable keys, timestamps, stock managed by another flow).
- **Create/Update split**: each entity has one abstract `XxxFormPanel` holding fields, `napDuLieu`, `layDuLieu`, `validateInput()` and the two hooks `dungBoCuc()` / `kiemTraRieng()`. `XxxTaoFormPanel` and `XxxSuaFormPanel` only implement `dungBoCuc()` (and `kiemTraRieng()` when the rule is mode-specific). No `FormMode` flag and no `if (mode == ...)` branches.

## 4. Inline Validation (FormValidationHelper)

- `kiemTra(comp, hopLe, tooltipKey)` returns the boolean and marks/clears the FlatLaf error outline.
- `markError(comp, message)` / `clearError(comp)` set `FlatClientProperties.OUTLINE = "error"` and the tooltip.
- `autoClearOnInput(textComponent)` removes the error as soon as the user edits the field.
- `validateInput()` must validate every rule, focus the first invalid component, and return `false`. Never throw for user input errors.
- Validation rules must match the corresponding service rules (same regexes and ranges) so the form never accepts data the service will reject.

## 5. Date & Time Inputs

- Always instantiate date pickers via `BoChonNgayGio.taoBoChonNgay()` or `taoBoChonNgayGio()`.
- Reads values directly as `LocalDate` / `LocalDateTime`. Never pass formatted text to the service layer.
- `LGoodDatePicker` imports must NEVER leak outside `iuh.fit.gui`.

## 6. Lucide Icons

- Rendered via `new LucideIcon(iconName, size, color)`.
- Icon names correspond to PNG files in `src/resources/icons/<iconName>.png`.
- Missing PNGs are silently skipped by `LucideIcon`, which makes icon-only row buttons invisible — verify the asset exists before referencing a new icon name.
- Painted dynamically with bilinear anti-aliasing and tinted with the provided Theme color.
