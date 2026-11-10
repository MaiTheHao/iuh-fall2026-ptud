---
name: be-convention
description: Enforce Gara Ô tô Lúa Java 17 backend conventions for files under `src/iuh/fit/{service,dao,entity,dto,core,exception}`. Use whenever reading, writing, reviewing, or refactoring DAOs, Services, Entities, DTO records, SQL, transactions, or business validation.
---

# Backend Convention — RULES (MUST / MUST NOT)

## 1. Layering

- MUST: `GUI → SERVICE → DAO → DB`. Each layer imports only the layer below + `entity, dto, enumeration, exception, util, core`.
- MUST NOT: DAO→DAO call; SQL/`PreparedStatement`/`Connection` in SERVICE (only `TransactionScope` allowed); `javax.swing.*` in service/dao/entity/dto; JDBC connection opened outside `DatabaseConfig` / `TransactionScope`.
- MUST: utility classes `final`, private ctor `throw new AssertionError("Utility class")`, static methods only.

## 2. Entity & DTO

- Entity: logic-free POJO, 1 per table (fields, ctors, getters/setters, equals/hashCode/toString). MUST NOT hold validation/calculation/status logic.
- DTO (criteria, form input, view row, paging): MUST be immutable Java 17 `record` in `iuh.fit.dto`. Normalize via compact ctor or `from(Entity)`. MUST NOT hold mutable state or `Connection`.

## 3. DAO

- MUST: extend `BaseDao<T>`, 1 table per DAO; declare `throws SQLException`; never swallow exceptions.
- MUST: single row → `Optional<T>`; multi row → `List<T>`. NEVER `return null`.
- MUST: SQL in text blocks `"""`, UPPERCASE keywords, `?` bindings. NEVER concatenate variables into SQL.
- MUST: map rows via private `mapRow(ResultSet)`.
- MUST: every write has an overload taking `Connection conn`; if given → use it, do not close/commit; if `null` → open from `DatabaseConfig` with try-with-resources.
- DAO NEVER commits.

## 4. Service

- MUST: 1 Service per feature/entity group. Owns ALL validation (required, regex, range, status transition), filtering, sorting, paging, authorization, calculations. MUST NOT live in listeners, entities, or SQL.
- MUST: throw `IllegalArgumentException` / domain exception carrying an **i18n key** (e.g. `app.nhanVien.loiSoDienThoai`). NEVER hardcoded message text.
- MUST: return DTOs to GUI (joins, totals, labels computed in Service); build count + page queries via DAO.
- MUST NOT: catch `SQLException` then return null/continue; rethrow or map to domain exception.

## 5. Transaction

- MUST: multi-table write or atomic business-ID generation uses
  `try (TransactionScope scope = new TransactionScope())`, passes `scope.getConnection()` to every DAO call, calls `scope.commit()` once at the end.
- MUST NOT: sequential multi-DAO writes without `TransactionScope`; catch-and-continue inside the scope (must roll back).
- Business-ID duplicate key → retry inside the transaction, max 3.

## 6. Java 17 style

- `this.` for instance fields/methods; records for DTOs; pattern-matching `instanceof` / switch expressions where suitable.
- No method comments; class comments short, in Vietnamese.

## 7. Verify before accepting a diff (all EMPTY / exit 0)

```bash
grep -rn 'javax\.swing\.' src/iuh/fit/{service,dao,entity,dto}
grep -rn 'new [A-Za-z0-9]*DAO(' src/iuh/fit/dao
grep -rn 'PreparedStatement\|DriverManager\|Connection ' src/iuh/fit/service | grep -v TransactionScope
grep -rn 'SELECT \|INSERT \|UPDATE \|DELETE ' src/iuh/fit/service
grep -rn '\+ [a-zA-Z0-9_]* \+' src/iuh/fit/dao | grep -i 'SELECT\|INSERT\|UPDATE\|DELETE'
grep -rn 'return null;' src/iuh/fit/dao
javac --release 17 -encoding UTF-8 -d bin -cp "lib/*:src" $(find src -name "*.java")
```
