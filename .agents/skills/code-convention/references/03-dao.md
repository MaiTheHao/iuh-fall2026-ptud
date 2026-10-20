# 03 — DAO layer

SQL access, statement formatting, and row mapping in `iuh.fit.dao`.

## Rules

R1. A DAO is `public final class XxxDAO extends BaseDao<Xxx>`, implements `mapRow(ResultSet)`, and exposes `findAll()` as `return findAll(SQL_SELECT)`.

R2. Declare `private static final String SQL_SELECT` as a text block listing columns in the same order `mapRow` reads them, and reuse it for every read of that table.

R3. A query that reuses `SQL_SELECT` keeps the composition form `SQL_SELECT + " WHERE maX = ?"`: the constant is a text block, the appended clause is a plain string. A tail longer than one clause becomes its own text block instead of `+` chains.

R4. Format every text block the same way: opening `"""` right after `=`, SQL indented one level deeper, one clause per line (`SELECT` → `FROM`/`JOIN` → `WHERE` → `ORDER BY`), closing `"""` on its own line. See the project convention below for the exact shape.

R5. Bind every value with `?`. Identifiers never come from user input. Never build SQL with `+` on a variable, `String.format`, `.formatted`, or `\n` escapes.

R6. Return types: `Optional<X>` for a single row, `List<X>` for many, `boolean` for exists/insert/update/delete. Never `null` for a missing row.

R7. Map rows only in `mapRow` — no formatting, translation, or arithmetic. A joined column becomes a view DTO, not a computed entity field.

R8. No business filtering, sorting, or paging in SQL. `WHERE` may only locate a row by key or uniqueness (`maX = ?`, `sdt = ?`, `tenDangNhap = ?`). Filtering by UI criteria happens in SERVICE.

R9. Writes take a scope: `insert(TransactionScope, X)`, `update(TransactionScope, X)`, `delete(TransactionScope, id)`. Opening, committing, and rolling back belong to the caller ([05-transaction](05-transaction.md)).

R10. Let `SQLException` propagate unchanged. No `catch`, no wrapping, no `printStackTrace`, no logging that swallows the cause.

## Statement shape

```java
private static final String SQL_SELECT = """
        SELECT maNhanVien, tenDangNhap, hoTen
        FROM NhanVien
        """;
```

The opening `"""` follows the `=` on the same line, the SQL body is indented one level deeper than the declaration, and the closing `"""` sits alone on its own line aligned with the body's parent. A one-line text block (`"""SELECT 1"""`) is a violation.

## Reference code

- `src/iuh/fit/dao/NhanVienDAO.java` — `SQL_SELECT`, `findById`, `findByTenDangNhap`.
- `src/iuh/fit/dao/BaseDao.java` — `findOne`, `findAll`, and their `TransactionScope` overloads.
- `src/iuh/fit/dao/PhieuTiepNhanDAO.java` — `findById(scope, …)` and `insert(scope, …)`.

## Anti-patterns

- `"SELECT * FROM X WHERE ma = '" + ma + "'"`.
- `String.format("SELECT … WHERE ma = '%s'", ma)`.
- `try { … } catch (SQLException e) { return null; }`.
- A DAO returning `Map<String, Long>` totals for a dashboard.
- Adds `ORDER BY` or `TOP` to make a screen look right.

## Check

```bash
grep -rn 'String.format\|\.formatted(' src/iuh/fit/dao        # expect: none
grep -rn 'catch (SQLException' src/iuh/fit/dao | grep -v TransactionScope   # expect: none
grep -rn 'ORDER BY\|OFFSET\|TOP ' src/iuh/fit/dao             # expect: known drift only
grep -rn '^\s*+ "' src/iuh/fit/dao                            # expect: known drift only
```

## Related

- [05-transaction](05-transaction.md), [04-service](04-service.md), [11-checklist](11-checklist.md).
