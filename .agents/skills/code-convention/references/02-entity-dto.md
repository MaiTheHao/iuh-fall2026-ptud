# 02 — Entities, DTOs, enums, exceptions

Shape rules for `iuh.fit.entity`, `iuh.fit.dto`, `iuh.fit.enumeration`, `iuh.fit.exception`.

## Rules

R1. Entity = table row POJO: private fields, no-argument constructor, full constructor, getters, setters. No validation, formatting, calculation, `java.sql` import, or app-layer import.

R2. No derived field on an entity to make a screen easier. Compute in SERVICE and return a DTO.

R3. `iuh.fit.dto` is records only. Name by purpose: `XxxCriteria` (filters + paging), `XxxFormData` (raw text from the form), `XxxView` (joined read model), `PaginatedResult` (page envelope).

R4. List criteria implement `DanhSachCriteria` and expose `static XxxCriteria macDinh()` returning page 1 with `PaginatedResult.DEFAULT_PAGE_SIZE`.

R5. Criteria values arrive unparsed (`String`, `LocalDate`). The SERVICE parses and validates; the criteria stays dumb.

R6. Paging goes through `PaginatedResult.of(list, criteria)`. Never slice a list in a page or a DAO.

R7. Enums live in `iuh.fit.enumeration` with SCREAMING_SNAKE constants. Implement `I18nEnum` when the label is shown, so `app.enum.<Enum>.<CONSTANT>` is the only label source.

R8. Exceptions live in `iuh.fit.exception`, extend `RuntimeException` (or `AuthException`), and carry no message; the GUI supplies the key. Input validation that is not a named domain failure uses `IllegalArgumentException(key)` thrown by SERVICE.

R9. A record used as a dialog result belongs next to the dialog (`ChonPhuTungDialog.KetQua`), not in `dto`; a record crossing SERVICE↔GUI belongs in `dto`.

## Reference code

- `src/iuh/fit/entity/NhanVien.java` — entity shape.
- `src/iuh/fit/dto/DanhSachNhanVienCriteria.java` — criteria + `macDinh()`.
- `src/iuh/fit/dto/DanhSachCriteria.java` — paging contract.
- `src/iuh/fit/dto/PaginatedResult.java` — envelope and slicing.
- `src/iuh/fit/dto/NhanVienFormData.java` — form record (all strings).
- `src/iuh/fit/enumeration/VaiTro.java`, `src/iuh/fit/enumeration/I18nEnum.java` — enum + label.
- `src/iuh/fit/exception/UserNameExistsException.java` — message-less domain failure.

## Anti-patterns

- `public String getTenHienThi()` on an entity, concatenating fields.
- Entity importing `java.sql.Timestamp` or `java.sql.ResultSet`.
- A `class` with setters inside `iuh.fit.dto`.
- Enum label hardcoded in Vietnamese/English instead of `app.enum.*`.
- `throw new RuntimeException("Số điện thoại không hợp lệ")` — raw text, wrong layer.

## Check

```bash
grep -rn 'java.sql' src/iuh/fit/entity src/iuh/fit/dto                    # expect: none
grep -rn 'iuh.fit.dao\|iuh.fit.service\|iuh.fit.gui' src/iuh/fit/entity   # expect: none
grep -rn 'public void set' src/iuh/fit/dto                                # expect: none
```

## Related

- [03-dao](03-dao.md) maps rows into entities, [04-service](04-service.md) owns validation.
