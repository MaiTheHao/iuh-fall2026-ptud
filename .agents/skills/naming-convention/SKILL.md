---
name: naming-convention
description: Enforce the Java Swing/AWT control naming convention (btn, lbl, txt, txa, chk, rad, cmb, lst, tbl, mnu, mni, frm, pnl, dlg) for every file under `src/iuh/fit/gui`. Use this skill whenever creating, renaming, reading, reviewing, or refactoring UI fields, local control variables, dialogs, forms, layouts, or pages, and before accepting any GUI diff.
---

# Control Naming Convention — Java Swing/AWT

This skill governs the names of every Swing/AWT **control** in `src/iuh/fit/gui`.
Names are `prefix` + `PascalCase`, where the suffix describes the control's job in
Vietnamese (no accents), e.g. `btnLuu`, `txtHoTen`, `lblThongBao`.

---

## 1. Required Prefixes

| Tiền tố | Loại Control (Java) | Ví dụ |
|---|---|---|
| `btn` | `JButton` | `btnLuu`, `btnHuy`, `btnThanhToan` |
| `lbl` | `JLabel` | `lblThongBao`, `lblTongTien` |
| `txt` | `JTextField` | `txtHoTen`, `txtGhiChu`, `txtBoLocMaHoaDon` |
| `txa` | `JTextArea` | `txaGhiChu`, `txaNoiDung` |
| `chk` | `JCheckBox` | `chkDongY`, `chkHienThi` |
| `rad` | `JRadioButton` | `radChuyenKhoan`, `radTienMat` |
| `cmb` | `JComboBox` / `ComboTimKiem` | `cmbVoucher`, `cmbPhieu` |
| `lst` | `JList` | `lstKetQua` |
| `tbl` | `JTable` | `tblDong`, `tblHoaDon` |
| `mnu` | `JMenuBar` (`JMenu` cũng dùng `mnu`) | `mnuSettings`, `mnuMain` |
| `mni` | `JMenuItem` | `mniSaoLuu`, `mniThoat` |
| `frm` | `JFrame` | `frmChinh`, `frmDangNhap` |
| `pnl` | `JPanel` | `pnlKhuVucQr`, `pnlThanhNut` |
| `dlg` | `JDialog` | `dlgThanhToan`, `dlgXemTruoc` |

`AbstractPage` subclasses are pages, not `JFrame`; do **not** prefix them with `frm`.

---

## 2. Rules

1. Every control **field** uses the prefix from the table.
   `private final JRadioButton radChuyenKhoan;` — not `rdoChuyenKhoan`, not `radio1`.
   New or modified control **parameters and local variables** follow the same rule;
   the repo-wide audit script below enforces field declarations.
2. Suffix is meaningful, in Vietnamese without accents, PascalCase after the prefix:
   `btnXoaDaChon`, `txtBoLocKhachHang`, `lblTenChuTaiKhoan`.
3. Do not stack type words: `btnButtonLuu`, `pnlPanelTop` are violations.
4. Non-control members keep plain camelCase and take **no** prefix: services, DAOs,
   DTOs, models, `SwingWorker`, timers, listeners, `ImageIcon` data holders.
   Example: `qrWorker`, `hoaDonService`, `lineModel`.
5. Custom components follow the control they extend:
   `AccountBox extends JPanel` → `pnlAccount`; `ComboTimKiem<T>` → `cmb...`.
6. Rename consistently at **every** occurrence: declaration, `new`, listener,
   enable/disable, add/remove. A half-renamed control is a build error or a bug.
7. `static final` controls follow the same prefix; constants that are not controls
   use `UPPER_SNAKE_CASE`.
8. A rename must not change behavior: same type, same initialization order, same
   listener wiring, same i18n keys.

---

## 3. Audit Before Accepting a GUI Diff

Run this scan from the repo root; it audits **control field declarations** and must
print `violations: 0`. Parameters/locals in new code are reviewed manually (rule 1).
Pre-existing param/local debt is not part of this gate.

```bash
python3 - <<'EOF'
import re, glob, subprocess
rules = {
 'JButton':'btn','JLabel':'lbl','JTextField':'txt','JTextArea':'txa',
 'JCheckBox':'chk','JRadioButton':'rad','JComboBox':'cmb','JList':'lst',
 'JTable':'tbl','JMenuBar':'mnu','JMenuItem':'mni','JFrame':'frm',
 'JPanel':'pnl','JDialog':'dlg'}
pat = re.compile(r'^\s*(?:private|protected|public)\s+'
                 r'(?:static\s+|final\s+|transient\s+|volatile\s+)*'
                 r'([A-Za-z0-9_.<>]+)\s+([a-zA-Z_][A-Za-z0-9_]*)\s*(?:=|;)')
bad = []
for f in glob.glob('src/iuh/fit/gui/**/*.java', recursive=True):
    for i, line in enumerate(open(f, encoding='utf-8'), 1):
        m = pat.match(line)
        if not m:
            continue
        base = m.group(1).split('<')[0].split('.')[-1]
        if base in rules and not m.group(2).startswith(rules[base]):
            bad.append(f'{f}:{i}  {base} {m.group(2)}')
print('violations:', len(bad))
print('\n'.join(bad))
EOF
```

Also grep for the legacy radio prefix, which must return nothing:

```bash
grep -rn "\brdo[A-Z]" src/iuh/fit/gui
```

---

## 4. Review Checklist

- [ ] Every control declaration matches the prefix table.
- [ ] Every reference of a renamed control was updated (`grep` the old name).
- [ ] Prefix scan reports `violations: 0`.
- [ ] Build is still `javac --release 17 -encoding UTF-8 -cp "lib/*"` → 0 error.
- [ ] No behavior, layout, listener, or i18n change was introduced by the rename.
