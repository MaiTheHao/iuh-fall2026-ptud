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
grep -rn 'new Color(' src/iuh/fit/gui/page/LoginPage.java src/iuh/fit/gui/page/DashboardPage.java src/iuh/fit/gui/component/SuaDialog.java

# Expect: no hex colors outside theme/
grep -rn '#[0-9A-Fa-f]\{6\}' src/iuh/fit/gui | grep -v '/theme/'
```

## 3. Fixed Window & SplitPane Layout

```bash
# Expect: only ManagementPanel debounce timer
grep -rn 'new Timer' src/iuh/fit/gui

# Expect: split divider must be 480
grep -rn 'setDividerLocation' src/iuh/fit/gui | grep -v 'setDividerLocation(480)'

# Expect: empty (no dynamic screenSize hacks)
grep -rn 'getScreenSize\|setResizeWeight' src/iuh/fit/gui
```

## 4. Compilation Verification

```bash
find src -name "*.java" > /tmp/sources.txt && javac -d bin -cp "lib/*:bin" @/tmp/sources.txt
# Expect: exit code 0
```
