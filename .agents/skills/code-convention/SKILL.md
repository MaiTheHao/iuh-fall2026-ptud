---
name: code-convention
description: Enforce Gara Ô tô Lúa Java conventions for all code under `src/iuh/fit`. Use when creating, editing, refactoring, or reviewing entities, DAOs, services, Swing screens, or related code. Covers the completion/optimization phase (no new features, correctness and targeted optimization first), KISS/YAGNI, strict GUI → SERVICE → DAO layering, POJO entities, service-owned business logic, raw DAO data access, service-owned transactions via TransactionScope, i18n error keys and key lifecycle, ThongBao notifications/confirmations, modal selection dialogs, a fixed 16:9 POS layout, and strict Java comment conventions.
---

# Code Conventions — Gara Ô tô Lúa

Apply these rules to all Java code under `src/iuh/fit`. They override conflicting local preferences and existing violations. Preserve compliant project patterns and minimize unrelated changes.

## 0. Project Phase — Completion and Optimization

The project is past feature build-out. Treat every change as a stabilization change.

- **DO:** Fix correctness before optimizing, and optimize only measured hot paths (repeated queries, per-row database calls, redundant reloads).
- **DO:** Preserve existing public behavior and method signatures unless the task explicitly removes them.
- **DO:** When removing or renaming an API, find every caller and update it in the same change.
- **DO:** Prefer consistency with existing patterns over new abstractions.
- **DON'T:** Add new features, screens, dependencies, or speculative abstractions.
- **DON'T:** Refactor unrelated code, rename files, or reformat untouched blocks.
- **DON'T:** Leave dead code, unused imports, orphan i18n keys, or disabled controls behind.

Every change must compile and carry a targeted verification (see Verification Checklist).

## 1. KISS and YAGNI

Implement only the requested behavior using the simplest solution consistent with the existing codebase.

- **DO:** Reuse existing utilities and patterns. Keep implementations minimal.
- **DON'T:** Add speculative features, unnecessary abstractions, redundant configuration, or defensive branches for impossible scenarios.

## 2. Strict Three-Tier Architecture

Dependencies must follow one direction:

`GUI → SERVICE → DAO → Database`

| Layer | Package | Responsibility |
|---|---|---|
| GUI | `iuh.fit.gui` | Render Swing screens, collect input, invoke services, and display results or errors. |
| SERVICE | `iuh.fit.service` | Validate input, enforce business rules, calculate values, orchestrate operations, and transform data. |
| DAO | `iuh.fit.dao` | Execute database operations and map database rows to entities. |
| Entity | `iuh.fit.entity` | Hold application data through plain POJOs. |

Each layer may depend only on the layer directly below it. Entities must not depend on application layers.

- **DO:** Have GUI components call services and services call DAOs.
- **DON'T:** Access DAOs or JDBC from the GUI, access JDBC directly from services, or call DAOs from other DAOs.

Reference implementations: `iuh.fit.dao.NhanVienDAO`, `iuh.fit.entity.NhanVien`, and `iuh.fit.core.config.DatabaseConfig`. Inspect existing implementations before introducing new patterns.

## 3. Business Logic Belongs in SERVICE

All business rules, validation, calculations, authorization decisions, state transitions, filtering, sorting, and aggregation belong in the service layer.

- **DO:** Let services validate inputs, enforce rules, perform calculations, and return results the GUI can present.
- **DON'T:** Put business decisions in Swing listeners, entities, or DAOs.

GUI event handlers should only collect input, call the appropriate service, and present the outcome. Entity accessors must remain free of business logic.

## 4. Entities Must Remain Plain POJOs

Entities contain private fields, constructors, getters, and setters only.

- **DO:** Follow the existing `NhanVien` pattern, including a no-argument constructor, a full constructor, and standard accessors where appropriate.
- **DON'T:** Add validation, calculations, formatting, persistence, localization, or dependencies on DAO, SERVICE, GUI, or `java.sql` APIs.

## 5. DAO Must Return Raw Data

DAOs handle database access and row-to-entity mapping, not business-level data processing.

- **DO:** Provide straightforward retrieval methods such as `findAll()` and `findById(...)`, following existing project conventions.
- **DO:** Return `Optional<T>` from single-record lookups (`findOne`, `findById`, `findByTenDangNhap`, `login`) so "not found" is explicit; `findAll()` still returns `List<T>`.
- **DON'T:** Apply business filtering, sorting, or aggregation in DAO methods, or return business-specific precomputed totals.
- **DON'T:** Return `null` for a single-row lookup; never leak `null` upward to the service.

Services must perform filtering, sorting, and aggregation on retrieved data. Do not encode business rules in SQL `WHERE`, `ORDER BY`, or aggregate expressions. Services must not return `null`: resolve single lookups with `orElseThrow(...)` or throw an exception; an empty list is a valid result (e.g. an empty `PaginatedResult`), not an error.

Exception types:

- **DO:** Use `iuh.fit.exception.*` for shared or domain-level failures that callers may need to distinguish (authentication, not-found, conflicts).
- **DO:** Use `IllegalArgumentException` for validation failures, with the message set to an i18n key.
- **DON'T:** Put raw user-facing text in an exception message; the message must be an i18n key that the GUI resolves through `ThongBao`.
- **DON'T:** Swallow an exception or replace it with a generic one; let the GUI map `IllegalArgumentException` to a business message and `SQLException` to `app.login.errorSystem`.

SQL statements must use Java Text Blocks so queries stay readable and line-broken by clause.

- **DO:** Write every SQL literal as a text block (`"""…"""`); keep one statement per block, break lines by clause (`SELECT` → `FROM`/`JOIN` → `WHERE` → `ORDER BY`), and keep the opening `"""` after the `=`, the content indented one level deeper, and the closing `"""` on its own line.
- **DO:** Merge several static `+` fragments of one statement into a single text block, and pass every value through `?` placeholders.
- **DO:** When a query reuses a shared constant (e.g. `SQL_SELECT`), keep the composition form `SQL_SELECT + " WHERE maX = ?"` — the constant is a text block, the appended clause fragment stays a plain string.
- **DON'T:** Build a multi-line SQL statement by concatenating string literals, use `String.format(...)`/`.formatted(...)` for SQL, put a text block on a single line, or use manual `\n` escapes inside a text block.

## 6. Transaction and Connection Ownership

SERVICE owns the transaction lifecycle; DAO executes inside the scope it receives.

- **DO:** Open `TransactionScope` in the service, pass it to DAO methods, and call `scope.commit()` only after every step succeeds.
- **DO:** Give DAO methods an overload that accepts `TransactionScope` and uses `scope.getConnection()`.
- **DO:** Open a new `TransactionScope` for each retry attempt (for example inside `MaRetryUtil`); never reuse a scope after a failed attempt.
- **DO:** Let a business failure propagate out of the scope so `close()` rolls the transaction back.
- **DON'T:** Call `setAutoCommit`, `commit`, or `rollback` inside a DAO, or open a second connection for an operation already inside a scope.
- **DON'T:** Treat a DAO write result as persisted state; only `scope.commit()` finalizes the transaction.

Read-only lookups use the `BaseDao` helpers (`findAll`, `findOne`) with their own connection and need no transaction.

## 7. Swing UI: i18n and Fixed 16:9 Layout

All user-visible text must be resolved through the project's internationalization resources. The POS interface targets a fixed 16:9 screen, not a responsive layout.

- **DO:** Use `I18nReaderUtil` with valid keys from `src/resources/i18n_vn.json` and `src/resources/i18n_en.json`. Keep both language resources synchronized when adding or changing UI text. Follow existing Swing layout conventions for the fixed target dimensions and aspect ratio.
- **DO:** Use the bundled `LGoodDatePicker` (`lib/LGoodDatePicker-11.2.1.jar`), e.g. `ManagementPanel.addDateRangeFilter(...)`, for any date input, so users pick dates instead of typing them.
- **DON'T:** Hard-code user-visible labels, duplicate translated text in source code, or introduce responsive/adaptive reflow and scaling logic.
- **DON'T:** Ask users to type or hand-parse date strings when a date picker is available; build against `lib/*` (the wildcard includes every jar in `lib/`).

Reuse existing translation keys where appropriate.

## 8. Notifications and Confirmations (`ThongBao`)

`iuh.fit.gui.component.ThongBao` is the single entry point for user-facing messages.

- **DO:** Use `ThongBao.thongTin` for success and information, `ThongBao.canhBao` for input warnings, `ThongBao.loi` for errors, and `ThongBao.xacNhan` for yes/no confirmation.
- **DO:** Pass i18n keys, not literal text. If a raw string must be shown, resolve it with `I18nReaderUtil` first.
- **DO:** Confirm before destructive or irreversible actions: deleting a record, clearing a form with unsaved changes, or finalizing an operation that changes stock or money.
- **DO:** Honor the boolean returned by `ThongBao.xacNhan`; stop the action immediately when it is `false`.
- **DON'T:** Build `JOptionPane` directly for messages or confirmations. Only input dialogs that return a typed value (e.g. `showInputDialog` in `AnhField`) may use `JOptionPane` directly.
- **DON'T:** Show a warning and then continue with the rejected or stale value; a rejected input must abort the current action.

Message arguments follow the same rule as exceptions: the key is the contract, the text lives in the two JSON files.

## 9. Modal Selection Dialogs

When the user must choose one record from a list, use a modal selection dialog instead of a `JComboBox`, an inline table editor, or a free-text code field.

Reference implementation: `iuh.fit.gui.component.ChonPhuTungDialog`.

- **DO:** Use a `JDialog` with `ModalityType.APPLICATION_MODAL` and the owner from `SwingUtilities.getWindowAncestor(this)`.
- **DO:** Build a read-only `JTable` with `SINGLE_SELECTION`, a search field that filters by normalized code and name (`TextNormalizerUtil.normalize`), and a `JSpinner` when a quantity is required.
- **DO:** Return `Optional<Result>` from `showDialog()` with a `record` result; `Optional.empty()` means the user cancelled.
- **DO:** Confirm with a double-click or a confirm button; if nothing is selected, warn with `ThongBao.canhBao` and keep the dialog open.
- **DO:** Reuse the existing dialog shape for new pickers: search + table + optional quantity + `record KetQua` + confirm/cancel.
- **DON'T:** Return `null`, expose mutable dialog internals, or let a dialog write to the database; the caller applies the result through SERVICE.
- **DON'T:** Use a `JComboBox` or an editable code cell to pick from an unbounded list.

## 10. i18n Key Lifecycle

These rules are binding for every change that touches UI text.

- **Key shape:** `app.<module>.<camelCase>`, where `<module>` matches the feature area (`phieuNhap`, `phuTung`, `common`, …). Shared errors go under `app.common`.
- **Parity:** every key exists in both `i18n_vn.json` and `i18n_en.json`, with identical key sets.
- **Add:** add the key to both files in the same change, inside the matching module object.
- **Remove:** delete the keys of a removed flow from both files in the same change; no orphan keys.
- **Rename:** update every reference in the same change.
- **One meaning:** never reuse a key for different text, and never duplicate a key inside one file.
- **No raw text:** UI labels, warnings, confirmations, and exception messages use keys only.
- **Formatting:** keep the file's existing indentation and ordering style; do not reformat unrelated lines.
- **Verification:** before completing the change, parse both files, compare their flattened key sets, and confirm every key referenced in `src` exists.

## 11. Java Conventions

Method comments are prohibited. Class comments are optional and must be written in Vietnamese when present.

- **DO:** Write a brief Vietnamese class comment describing the class's primary responsibility when clarification is useful.
- **DON'T:** Add comments to methods, including method-level Javadoc. Do not write class comments in another language.

Comments are not mandatory. Prefer clear naming and straightforward code over unnecessary documentation.

Must use "this." to reference instance variables and methods.

## Verification Checklist

Before completing a change, verify:

- [ ] Only the requested scope was implemented; no new features or speculative abstractions.
- [ ] Dependencies follow GUI → SERVICE → DAO.
- [ ] Business rules and data transformations reside in SERVICE.
- [ ] Entities remain plain POJOs.
- [ ] DAOs perform database access and row mapping without business-level filtering, sorting, or aggregation.
- [ ] DAO single-record lookups return `Optional`, never `null`; services throw instead of returning `null`.
- [ ] Service owns `TransactionScope`; DAO contains no `setAutoCommit`, `commit`, or `rollback`.
- [ ] Exception messages are i18n keys; GUI maps them through `ThongBao`.
- [ ] Destructive or unsaved actions go through `ThongBao.xacNhan`, and `false` aborts the action.
- [ ] List selection uses a modal dialog, not a combo box or free-text code field.
- [ ] DAOs write SQL as text blocks — one statement per block, clauses broken by line — never as concatenated multi-line string literals.
- [ ] Date inputs use `LGoodDatePicker` (`lib/LGoodDatePicker-11.2.1.jar`), not free-text date strings.
- [ ] All user-visible strings use i18n keys; both language files parse, have identical key sets, no duplicates, and no orphan keys.
- [ ] Every key referenced in `src` exists in both JSON files.
- [ ] Swing layouts target a fixed 16:9 POS screen.
- [ ] No method comments exist.
- [ ] Any class comments are optional, concise, and written in Vietnamese to describe the class's responsibility.
- [ ] The project compiles: `javac -encoding UTF-8 -cp "lib/*" -d <tmp> $(find src -name '*.java')`.
- [ ] Removed or renamed APIs have no remaining callers (`grep` shows none).
- [ ] Untracked IDE files (`.classpath`, `.project`) stay untracked and still exist locally.
- [ ] No test framework or new dependency was added without explicit approval; verification is compile plus a targeted runtime check.
