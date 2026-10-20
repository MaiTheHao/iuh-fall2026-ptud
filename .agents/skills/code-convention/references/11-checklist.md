# 11 — Pre-submit checklist

Run this before reporting a change as done. Each line maps to a rule in refs 01–10; this file aggregates them, so it has no rules of its own.

## Scope

- [ ] The change only touches what the request needs; no unrelated refactor, rename, or reformat.
- [ ] No new feature, screen, dependency, abstraction, or configuration was introduced.
- [ ] Removed or renamed APIs update every caller in the same change.
- [ ] No dead code, unused import, orphan i18n key, or disabled control left behind.

## Layering

- [ ] GUI calls SERVICE only; SERVICE calls DAO only; DAO talks to the database only.
- [ ] No `java.sql` import in GUI or SERVICE; no Swing import in SERVICE; no DAO-to-DAO call.
- [ ] New classes sit in the existing package for their role ([01](01-architecture.md)).

## Data

- [ ] Entities are untouched POJOs; new payloads are records in `iuh.fit.dto` ([02](02-entity-dto.md)).
- [ ] DAO: `Optional`/`List`/`boolean`, no `null`, no `catch (SQLException)`, text-block SQL, `?` bindings ([03](03-dao.md)).
- [ ] SERVICE validates first, owns filter/sort/paging, never returns `null`, never resolves translations ([04](04-service.md)).
- [ ] Writes run inside a `TransactionScope` committed once by the SERVICE; no new self-committing DAO method ([05](05-transaction.md)).

## UI

- [ ] Screen reuses `ManagementPanel` / `ManagementFormPanel` / `FormFieldPanel`; no hand-built table or form grid ([06](06-swing-ui.md)).
- [ ] Filters come from the panel API and are read through the returned handle, not a shadow widget.
- [ ] Dates come from `BoChonNgayGio`; no typed date strings.
- [ ] Row actions use `ThaoTac` + `addActionColumn`, with Lucide icon and i18n tooltip.
- [ ] Every color, font, and border comes from `Theme`; no new literal ([07](07-theme.md)).
- [ ] A picker is a modal dialog returning `Optional<record>`; it never writes to the database ([08](08-dialogs.md)).

## Text

- [ ] Every visible string is a key in `i18n_vn.json` **and** `i18n_en.json`, added/removed in this change ([09](09-i18n.md)).
- [ ] Messages go through `ThongBao`; `xacNhan` result is honoured.
- [ ] Exception messages are keys, not sentences.

## Style

- [ ] Java 17 only; no 21+ API, no preview flag ([10](10-java-style.md)).
- [ ] `this.` for instance members; no method comments; Vietnamese class comments only when useful.
- [ ] 4-space indentation, no tabs in new or edited lines.

## Known drift (leave alone unless in scope)

| Where | Drift | Rule |
|---|---|---|
| `gui/page/LoginPage.java`, `gui/page/DashboardPage.java` | raw `Font`/`Color`, border literals | 07 |
| `gui/page/PlaceholderPage.java`, `gui/component/SuaDialog.java` | border literals | 07 |
| `dao/KhachHangDAO`, `KhachHangXeDAO`, `KhieuNaiDAO`, `PhieuTiepNhanDAO`, `XeDAO` | self-committing overloads | 05 |
| `dao/KhachHangDAO#findBySdt` | `ORDER BY` inside the DAO | 03 |
| `dao/NhanVienDAO#login` | multi-clause `+` chain instead of one text block | 03 |
| `service/*` | calling a DAO's self-committing overload | 05 |
| `gui/page/LapPhieuNhapPage.java`, `gui/component/ChonPhuTungDialog.java`, `service/PhieuNhapPhuTungService.java` | tab indentation | 10 |

## Check

```bash
grep -rn 'iuh.fit.dao' src/iuh/fit/gui                         # 01
grep -rn 'java.sql.Connection\|java.sql.PreparedStatement\|javax.swing' src/iuh/fit/service   # 01
grep -rn 'String.format\|\.formatted(' src/iuh/fit/dao         # 03
grep -rn 'catch (SQLException' src/iuh/fit/dao | grep -v TransactionScope   # 03
grep -rn 'ORDER BY\|^\s*+ "' src/iuh/fit/dao                   # 03
grep -rn 'return null' src/iuh/fit/service                     # 04
grep -rn 'TransactionScope.begin()' src/iuh/fit/dao            # 05
grep -rn 'new Color(\|new Font(' src/iuh/fit/gui | grep -v '/theme/'   # 07
grep -rn 'JOptionPane' src/iuh/fit/gui | grep -v ThongBao | grep -v AnhField   # 09
grep -rn 'getFirst()\|getLast()\|StructuredTaskScope\|ofVirtual' src/iuh/fit   # 10
```

Anything the sweep reports must be either fixed in this change or listed as drift in the report.

## Report format

Close every change with:

```
Changed: <path:line> — <what> (rule R#)
Verified: <command> → <result>
Drift left: <path#symbol> — <why out of scope>
```
