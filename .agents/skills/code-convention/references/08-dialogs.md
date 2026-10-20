# 08 — Dialogs

Modal selection and edit windows in `iuh.fit.gui.component`.

## Rules

R1. Choosing one record from an unbounded list uses a modal dialog. Never a `JComboBox`, never an editable code cell, never a free-text code field.

R2. The dialog is a `JDialog` with `ModalityType.APPLICATION_MODAL`, owner from `SwingUtilities.getWindowAncestor(this)` at the call site, and a title from an i18n key.

R3. One dialog per job, `public final`, hardcoded `setSize`, plus `setMinimumSize` when resizable, and `setLocationRelativeTo(owner)`.

R4. The picker table is read-only (`isCellEditable` returns `false`), `SINGLE_SELECTION`, styled with `Theme.styleTable`, fixed row height. Column headers and status text come from i18n keys.

R5. Search filters on normalized code and name with `TextNormalizerUtil.normalize`, on both the keyword and the cell text. Never `equalsIgnoreCase`, never raw `contains`.

R6. A quantity, when required, is a `JSpinner` with `SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1)` next to the confirm button.

R7. The result is a nested `record KetQua(...)`; `showDialog()` returns `Optional<KetQua>` after `setVisible(true)`, and `Optional.empty()` means cancelled. Never return `null`.

R8. Confirm with the confirm button or a double-click. If nothing is selected, call `ThongBao.canhBao` and return without disposing, so the user can retry.

R9. The dialog never touches the database. The caller applies `KetQua` through SERVICE.

R10. All styling comes from `Theme`; all text from i18n keys. Escape closes, and the confirm button is the root pane's default button.

## Reference code

- `src/iuh/fit/gui/component/ChonPhuTungDialog.java` — picker: constructor, `showDialog`, `locDanhSach`, `chon`, `record KetQua`.
- `src/iuh/fit/gui/component/SuaDialog.java` — edit dialog: `FormFieldPanel` content, `datXuLyLuu(BooleanSupplier)`, `hienThiVaChoKetQua()`.
- `src/iuh/fit/gui/page/LapPhieuNhapPage.java` — caller applying a picker result.
- `src/iuh/fit/gui/page/DichVuPage.java` — caller of `SuaDialog`.

## Anti-patterns

- `JComboBox<PhuTung>` listing every part in a form.
- A dialog that calls `phuTungDAO.updateTon(...)` before closing.
- `showDialog()` returning `KetQua` and `null` on cancel.
- Exposing the internal `JTable` or `DefaultTableModel` getters.
- Adding a row to the table for "add new" instead of opening the edit dialog.

## Check

```bash
grep -rn 'ModalityType.APPLICATION_MODAL' src/iuh/fit/gui    # every dialog
grep -rn 'showDialog\|Optional<KetQua>' src/iuh/fit/gui/component
grep -rn 'setMinimumSize' src/iuh/fit/gui                    # resizable dialogs declare it
grep -rn 'iuh.fit.dao' src/iuh/fit/gui/component             # expect: none
```

## Related

- [06-swing-ui](06-swing-ui.md), [07-theme](07-theme.md), [09-i18n](09-i18n.md).
