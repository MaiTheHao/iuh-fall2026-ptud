# 05 — Frontend Verification & Grep Checklist

Run these checks whenever you modify any file in `src/iuh/fit/gui/`:

## 1. No Database / DAO In Leakage

```bash
# Expect: empty (no DAO imports in GUI)
grep -rn 'import iuh\.fit\.dao\.' src/iuh/fit/gui

# Expect: empty (no raw SQL connection in GUI)
grep -rn 'java\.sql\.' src/iuh/fit/gui
```

## 2. No Hardcoded Colors or Inline Fonts (Anti-Drift)

```bash
# Expect: empty in newly updated pages/components
grep -rn 'new Color(' src/iuh/fit/gui/page/LoginPage.java src/iuh/fit/gui/page/DashboardPage.java src/iuh/fit/gui/component/BaseFormDialog.java

# Expect: no hex colors outside theme/
grep -rn '#[0-9A-Fa-f]\{6\}' src/iuh/fit/gui | grep -v '/theme/'
```

## 3. Layout Guards

```bash
# Expect: only ManagementPanel debounce timer
grep -rn 'new Timer' src/iuh/fit/gui

# Expect: empty for migrated management screens (full-width grid, no split pane)
grep -rn 'JSplitPane\|ManagementFormPanel' \
  src/iuh/fit/gui/page/{KhachHang,NhanVien,PhuTung,Voucher,Xe,DichVu}Page.java \
  src/iuh/fit/gui/page/{QuanLyKhieuNai,QuanLyPhieuTiepNhan,QuanLyHoaDon,QuanLyPhieuNhap,PhanCongCa,LapChanDoan,XemChanDoan}Page.java

# Expect: 6 hits — every migrated page wires the on-table Add action
grep -rln 'setAddAction' src/iuh/fit/gui/page

# Expect: empty (no dynamic screenSize hacks)
grep -rn 'getScreenSize\|setResizeWeight' src/iuh/fit/gui
```

## 4. i18n Parity

```bash
python3 - <<'PY'
import json
def flat(d, p=''):
    out = {}
    for k, v in d.items():
        key = f'{p}.{k}' if p else k
        out.update(flat(v, key)) if isinstance(v, dict) else out.setdefault(key, v)
    return out
vn = flat(json.load(open('src/resources/i18n_vn.json', encoding='utf-8')))
en = flat(json.load(open('src/resources/i18n_en.json', encoding='utf-8')))
print('missing in en:', sorted(set(vn) - set(en)))
print('missing in vn:', sorted(set(en) - set(vn)))
PY
# Expect: both lists empty
```

## 5. Compilation Verification

```bash
javac -d bin -cp "lib/*:src" $(find src -name "*.java")
# Expect: exit code 0
```
