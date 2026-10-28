# 03 — Components and Forms

## 1. ManagementPanel

The standard list and filter component for all management screens.

- **Quick Search**: Uses `oTimNhanh` (JTextField with Lucide `search` icon and placeholder).
- **Filter Bar**: Wrapped in `khungBoLoc` using `WrapLayout(FlowLayout.LEFT, 8, 6)`. Registered via `addTextFilter`, `addComboBoxFilter`, `addDateRangeFilter`, `addRangeFilter`. Keeps debounced listener (300ms) internally — do NOT add another timer.
- **Table**: `JTable` with row height `34px`, no vertical grid lines, zebra striping via `prepareRenderer` using `Theme.palette().TABLE_ROW_ALT()`.
- **Action Column**: Added via `addActionColumn(headerKey, ThaoTac...)` and rendered with `ThaoTacTableCellRenderer` as compact pill buttons (36×32, 8px gap) with hover effect.
- **Pagination**: Positioned at bottom right on `Theme.palette().PANEL_ALT()` background with previous/next buttons and page count indicator.

## 2. ManagementFormPanel

The standard right-side form container.

- **Structure**: Uses `Theme.applyCardStyle(this)` and `Theme.titledBorder(titleKey)`.
- **Button Bar**: Contains 4 standard action buttons (Add, Save, Delete, Clear) with Lucide icons (`plus`, `save`, `trash-2`, `rotate-ccw`), styled via `applyPrimary`, `applyDanger`, `applySecondary`, with minimum size `100×36px`.

## 3. FormFieldPanel

Standard GridBagLayout helper for label-input pairs.

- Added via `addField(labelKey, component)`.
- Automatically enforces `Theme.fieldLabelFont()` and `Theme.fieldRowInsets()` (standard: `8, 10, 8, 10`).
- **Number & Currency fields**: Must be right-aligned:
  ```java
  field.setHorizontalAlignment(JTextField.RIGHT);
  ```
- **Read-only fields**: Apply `Theme.applyReadOnly(field)`.

## 4. Date & Time Inputs

- Always instantiate date pickers via `BoChonNgayGio.taoBoChonNgay()` or `taoBoChonNgayGio()`.
- Reads values directly as `LocalDate` / `LocalDateTime`. Never pass formatted text to the service layer.
- `LGoodDatePicker` imports must NEVER leak outside `iuh.fit.gui`.

## 5. Lucide Icons

- Rendered via `new LucideIcon(iconName, size, color)`.
- Icon names correspond to PNG files in `src/resources/icons/<iconName>.png`.
- Painted dynamically with bilinear anti-aliasing and tinted with the provided Theme color.
