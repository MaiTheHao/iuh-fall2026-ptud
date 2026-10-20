# 10 — Java 17 style

Language level, naming, comments, and file-level discipline for `src/iuh/fit`.

## Rules

### Language level

Target is Java 17. Use records, sealed types, switch expressions, text blocks, `instanceof` patterns, and `var`.

Not available — do not use, and do not raise the level:

| Forbidden | Added in |
|---|---|
| `List.getFirst()` / `getLast()`, `SequencedCollection`, `SequencedMap` | 21 |
| Pattern matching for `switch`, `case null`, record patterns | 21 |
| Virtual threads (`Thread.ofVirtual`, `newVirtualThreadPerTaskExecutor`) | 21 |
| Structured concurrency (`StructuredTaskScope`) | 21 (preview) |
| String templates (`STR."…"`) | 21 (preview) |
| Unnamed classes, instance `main` | 21 (preview) |

Never enable `--enable-preview`, and never lower the source level.

### Naming

R1. Classes `PascalCase`, methods and fields `camelCase`, constants `SCREAMING_SNAKE`, packages lowercase.

R2. Domain names stay Vietnamese: `PhieuTiepNhan`, `maPhieu`, `taiLai`, `khoiTaoForm`. Do not translate half a class.

R3. Prefix constants by role: `SQL_*` for statements, `ERROR_*` for i18n keys, `KEY_*` for dialog titles, `PAGE_SIZE` / `DO_RONG` for fixed metrics.

R4. Getters/setters follow the field: `getMaPhieu()`, `setMaPhieu(...)`. Boolean getters use `is`/`has` only for real predicates.

### Structure

R5. Reference instance fields and methods with `this.` — never rely on implicit access.

R6. A utility class is `final` with a private constructor that throws `AssertionError`, and holds only static members. Example: `Theme`, `ThongBao`, `MaRetryUtil`, `I18nReaderUtil`.

```java
public final class ThongBao {

    private ThongBao() {
        throw new AssertionError("Utility class");
    }
}
```

R7. Prefer records for value carriers (DTO, dialog result, filter handle). No setters, no mutable state in a record.

R8. `var` only when the type is obvious from the right-hand side; otherwise write the type, as the surrounding code does.

R9. One top-level type per file; nested types only when they are an implementation detail (`record KetQua`, `enum KieuThaoTac`).

### Comments

R10. No method comments, including Javadoc. Method names carry the intent.

R11. Class and record comments are optional, short, and in Vietnamese — only when they add information the name does not (`I18nReaderUtil`, `PaginatedResult`, `ThaoTac`, `ManagementFormPanel`).

R12. No commented-out code, no `TODO` without an owner, no unused imports, no dead private methods.

### Formatting

R13. Four spaces, no tabs. Braces on the same line. One statement per line. No column alignment.

R14. Match the file you are editing; do not reformat untouched blocks, reorder imports, or rename unrelated symbols.

R15. Line length roughly 120 characters; wrap long parameter lists one per line.

## Reference code

- `src/iuh/fit/util/MaRetryUtil.java` — final utility, nested functional interfaces, early return.
- `src/iuh/fit/gui/component/ThaoTac.java` — record with nested enum and Vietnamese class comment.
- `src/iuh/fit/dto/PaginatedResult.java` — record with static factories and short Vietnamese class comment.
- `src/iuh/fit/gui/theme/Theme.java` — nested record + private helpers.

## Anti-patterns

- `list.getFirst()` after a "small cleanup".
- Method Javadoc restating the signature.
- A `static` mutable field shared across screens.
- `catch (Exception e) { }`.
- Reformatting a whole file to fix one line.

## Check

```bash
grep -rn 'getFirst()\|getLast()\|SequencedCollection\|StructuredTaskScope\|ofVirtual\|STR\.' src/iuh/fit   # expect: none
grep -rlP '\t' src/iuh/fit --include='*.java'          # tabs: known drift in 3 files
```

Compile check (treat an already-red baseline as pre-existing, not as your regression):

```bash
mkdir -p /tmp/opencode/cc-build
javac -encoding UTF-8 -d /tmp/opencode/cc-build -cp "lib/*" $(find src -name '*.java')
```

## Related

- [02-entity-dto](02-entity-dto.md), [11-checklist](11-checklist.md).
