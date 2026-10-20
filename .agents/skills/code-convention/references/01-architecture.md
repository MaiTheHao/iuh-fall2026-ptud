# 01 — Architecture and placement

Layer direction, package ownership, and where new code belongs in `src/iuh/fit`.

## Rules

R1. Dependencies point one way: `GUI → SERVICE → DAO → Database`. A layer imports only the layer directly below it, plus `entity`, `dto`, `enumeration`, `exception`, `util`, `core`. Why: one reversed edge (GUI→DAO, DAO→DAO) makes validation and transaction boundaries unrecoverable.

R2. GUI never opens a connection, writes SQL, or commits. If a screen needs data, add a SERVICE method.

R3. SERVICE never imports `java.sql.Connection`/`PreparedStatement`, never builds SQL, never imports `javax.swing`. Declaring `throws SQLException` is the DAO contract and stays.

R4. DAO never calls another DAO. Multi-table work is orchestration and belongs in SERVICE — `PhieuTiepNhanService` coordinates `PhieuTiepNhanDAO`, `KhachHangDAO`, `XeDAO`.

R5. Package map:

| Package | Holds | May import |
|---|---|---|
| `gui` | `GUI`, `layout`, `page`, `form`, `component`, `theme` | service, dto, entity, enumeration, util, theme |
| `service` | one class per feature | dao, dto, entity, enumeration, exception, util, core.context |
| `dao` | `BaseDao`, `TransactionScope`, one DAO per table | entity, core.config, util |
| `entity` | table-shaped POJOs | `java.*`, enumeration |
| `dto` | `record` criteria / form / view / paging models | entity, enumeration |
| `enumeration` | domain enums, `I18nEnum` | util |
| `exception` | shared and domain failures thrown by SERVICE | nothing |
| `util` | static helpers (i18n, normalize, id, date) | `java.*`, core |
| `core` | `config.DatabaseConfig`, `context.AppContext`, `env` | `java.*`, util |

R6. A feature is a vertical slice: `entity` + `dao` + `service` + `page`/`form`/dialog + i18n keys. Do not add a package for one screen; reuse `gui/page`, `gui/form`, `gui/component`.

R7. Utility classes are `final`, have a private constructor that throws `AssertionError("Utility class")`, and expose only static methods.

## Reference code

- `src/iuh/fit/gui/page/NhanVienPage.java` — page holding only `NhanVienService`.
- `src/iuh/fit/service/NhanVienService.java` — service holding only `NhanVienDAO`.
- `src/iuh/fit/service/PhieuTiepNhanService.java` — multi-DAO orchestration.
- `src/iuh/fit/dao/NhanVienDAO.java` — `extends BaseDao<NhanVien>`.
- `src/iuh/fit/core/config/DatabaseConfig.java` — the only connection factory.

## Anti-patterns

- `import iuh.fit.dao.X` inside a page, panel, or dialog.
- `new NhanVienDAO()` in a Swing listener.
- A DAO calling `KhachHangDAO` to validate a foreign key.
- New packages such as `business`, `manager`, `repository`, `controller`.

## Check

```bash
grep -rn 'iuh.fit.dao' src/iuh/fit/gui                      # expect: none
grep -rn 'java.sql.Connection\|java.sql.PreparedStatement\|java.sql.ResultSet\|javax.swing' src/iuh/fit/service   # expect: none
grep -rn 'iuh.fit.gui' src/iuh/fit/dao src/iuh/fit/service  # expect: none
grep -rn 'iuh.fit.dao' src/iuh/fit/entity src/iuh/fit/dto   # expect: none
```

## Related

- Layer contracts: [03-dao](03-dao.md), [04-service](04-service.md), [06-swing-ui](06-swing-ui.md).
