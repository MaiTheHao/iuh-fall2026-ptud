# 05 — Transaction ownership

Who opens, commits, rolls back, and retries in this codebase.

## Rules

R1. SERVICE owns the transaction: `try (TransactionScope scope = TransactionScope.begin()) { … scope.commit(); return result; }`.

R2. Commit exactly once, after every step has succeeded, before leaving the block. Never commit inside a loop and never commit twice.

R3. `close()` rolls back when `commit()` was not reached. A business failure is allowed to escape the block — do not catch it just to call `rollback()`.

R4. DAO only reads `scope.getConnection()`. It never calls `setAutoCommit`, `commit`, or `rollback`; only `TransactionScope` touches those.

R5. Each retry attempt opens a brand-new scope and never reuses a failed one. `MaRetryUtil.thucHien` does exactly this: `SO_LAN_THU = 3`, retry only on duplicate-key `SQLException` (SQL Server 2627/2601), rethrow anything else immediately.

R6. Read-only lookups need no scope: `BaseDao.findOne`/`findAll` open and close their own connection.

R7. An entry point opens the scope and delegates to a `scope`-taking overload (`create(X)` → `create(TransactionScope, X)`). Keeps the boundary explicit and lets an outer transaction compose inner steps.

R8. Never store a `TransactionScope` in a field, share it across threads, or return it.

R9. Never open a second connection for work already inside a scope (including inside DAO helpers, `MaRetryUtil` callbacks, and validation queries).

## Statement shape

```java
public PhieuTiepNhan create(PhieuTiepNhan phieu) throws SQLException {
    try (TransactionScope scope = TransactionScope.begin()) {
        PhieuTiepNhan ketQua = this.create(scope, phieu);
        scope.commit();
        return ketQua;
    }
}
```

## Reference code

- `src/iuh/fit/dao/TransactionScope.java` — `begin`, `commit`, `rollback`, `close`.
- `src/iuh/fit/dao/BaseDao.java` — scope overloads for reads.
- `src/iuh/fit/service/PhieuTiepNhanService.java#create` — correct ownership.
- `src/iuh/fit/service/TiepNhanService.java` — multi-DAO write in one scope.
- `src/iuh/fit/util/MaRetryUtil.java` — retry contract.
- `src/iuh/fit/dao/PhieuTiepNhanDAO.java#insert` — DAO side of the contract.

## Known drift (do not extend)

These DAOs open and commit their own scope through a convenience overload: `KhachHangDAO`, `KhachHangXeDAO`, `KhieuNaiDAO`, `PhieuTiepNhanDAO`, `XeDAO`. They are the reason some services call `dao.insert(entity)` instead of `dao.insert(scope, entity)`.

When your task already touches that flow, move the commit into the SERVICE and call the scope overload. If the old overload has callers outside your task, leave it in place and report it — do not mass-refactor DAOs as a side effect.

## Anti-patterns

- A DAO convenience method that opens and commits its own scope (the known drift above).
- `catch (SQLException e) { scope.rollback(); throw e; }` — `close()` already rolls back.
- `scope.commit()` inside a loop over rows.
- Reusing a scope after it threw, or passing it to another thread.
- Returning `false` from a service because a write failed, instead of letting the exception escape.
- Opening a second `DatabaseConfig.getConnection()` while a scope is active.

## Check

```bash
grep -rn 'setAutoCommit\|\.commit()\|\.rollback()' src/iuh/fit \
  | grep -v TransactionScope                       # expect: known drift only
grep -rn 'TransactionScope.begin()' src/iuh/fit/dao   # expect: known drift only
grep -rn 'TransactionScope' src/iuh/fit/gui            # expect: none
```

## Related

- [03-dao](03-dao.md), [04-service](04-service.md), [11-checklist](11-checklist.md).
