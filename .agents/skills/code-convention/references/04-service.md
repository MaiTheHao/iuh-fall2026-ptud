# 04 — Service layer

Validation, business rules, filtering, sorting, paging, and orchestration in `iuh.fit.service`.

## Rules

R1. One `XxxService` per feature, holding `private final XxxDAO` fields initialized in place. No constructor ceremony, no static state.

R2. Validate before writing: null/blank, length, format, range, existence. Invalid input throws `IllegalArgumentException(KEY)`; named domain failures throw their `iuh.fit.exception` type.

R3. Declare message keys as constants at the top of the class (`private static final String ERROR_X = "app.<module>.<key>";`) and reuse them. Never inline a key twice.

R4. Filtering, sorting, and paging happen here: stream over `dao.findAll()`, filter with `TextNormalizerUtil.normalize` on both sides, sort with a stable `Comparator`, return `PaginatedResult.of(list, criteria)`.

R5. Never return `null`. A single lookup resolves with `orElseThrow(...)`; an empty `List` or empty `PaginatedResult` is a valid result, not an error.

R6. Multi-step writes are orchestrated here: open `TransactionScope`, pass it to every DAO call, commit once after the last step ([05-transaction](05-transaction.md)).

R7. The service returns and throws keys, never translations: no `I18nReaderUtil`, no `ThongBao`, no Swing import.

R8. Generated ids go through `MaGeneratorUtil`; retry on duplicate key goes through `MaRetryUtil`.

R9. Method names stay domain-consistent across services: `search(criteria)`, `getById(id)`, `create(entity)`, `update(entity)`, `delete(id)`. A new verb needs a reason.

R10. A service may call several DAOs but never another service for a write that must be atomic — move the shared logic into the orchestrating service instead.

## Reference code

- `src/iuh/fit/service/NhanVienService.java#search` — filter chain, normalization, paging.
- `src/iuh/fit/service/DichVuService.java#validate` — key-based validation order.
- `src/iuh/fit/service/PhieuTiepNhanService.java#create` — scope + orchestration.
- `src/iuh/fit/service/AuthService.java#login` — domain exception + `orElseThrow`.
- `src/iuh/fit/util/TextNormalizerUtil.java` — the only normalization helper.

## Anti-patterns

- `if (hoTen.isEmpty()) ThongBao.canhBao(...)` in a service.
- Resolving `I18nReaderUtil.get("app.x.y")` to compare user input against a label.
- `dao.findAll().stream()...` duplicated in two services instead of one search method.
- `return null;` when a record is missing.
- Catching `SQLException` to return `false`.

## Check

```bash
grep -rn 'javax.swing\|JOptionPane\|ThongBao' src/iuh/fit/service        # expect: none
grep -rn 'I18nReaderUtil' src/iuh/fit/service                            # expect: none
grep -rn 'java.sql.Connection\|PreparedStatement' src/iuh/fit/service    # expect: none
grep -rn 'return null' src/iuh/fit/service                               # expect: none
```

## Related

- [02-entity-dto](02-entity-dto.md), [03-dao](03-dao.md), [05-transaction](05-transaction.md), [09-i18n](09-i18n.md).
