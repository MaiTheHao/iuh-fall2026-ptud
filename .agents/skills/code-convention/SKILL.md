---
name: code-convention
description: Enforce Gara Ô tô Lúa Java conventions for all code under `src/iuh/fit`. Use when creating, editing, refactoring, or reviewing entities, DAOs, services, Swing screens, or related code. Requires KISS/YAGNI, strict GUI → SERVICE → DAO layering, POJO entities, service-owned business logic, raw DAO data access, i18n-ready UI text, a fixed 16:9 POS layout, and strict Java comment conventions.
---

# Code Conventions — Gara Ô tô Lúa

Apply these rules to all Java code under `src/iuh/fit`. They override conflicting local preferences and existing violations. Preserve compliant project patterns and minimize unrelated changes.

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

Services must perform filtering, sorting, and aggregation on retrieved data. Do not encode business rules in SQL `WHERE`, `ORDER BY`, or aggregate expressions. Services must not return `null`: resolve single lookups with `orElseThrow(...)` or throw a domain exception (`iuh.fit.exception.*`); an empty list is a valid result (e.g. an empty `PaginatedResult`), not an error.

## 6. Swing UI: i18n and Fixed 16:9 Layout

All user-visible text must be resolved through the project's internationalization resources. The POS interface targets a fixed 16:9 screen, not a responsive layout.

- **DO:** Use `I18nReaderUtil` with valid keys from `src/resources/i18n_vn.json` and `src/resources/i18n_en.json`. Keep both language resources synchronized when adding or changing UI text. Follow existing Swing layout conventions for the fixed target dimensions and aspect ratio.
- **DO:** Use the bundled `LGoodDatePicker` (`lib/LGoodDatePicker-11.2.1.jar`), e.g. `ManagementPanel.addDateRangeFilter(...)`, for any date input, so users pick dates instead of typing them.
- **DON'T:** Hard-code user-visible labels, duplicate translated text in source code, or introduce responsive/adaptive reflow and scaling logic.
- **DON'T:** Ask users to type or hand-parse date strings when a date picker is available; build against `lib/*` (the wildcard includes every jar in `lib/`).

Reuse existing translation keys where appropriate.

## 7. Java Conventions

Method comments are prohibited. Class comments are optional and must be written in Vietnamese when present.

- **DO:** Write a brief Vietnamese class comment describing the class's primary responsibility when clarification is useful.
- **DON'T:** Add comments to methods, including method-level Javadoc. Do not write class comments in another language.

Comments are not mandatory. Prefer clear naming and straightforward code over unnecessary documentation.

Must use "this." to reference instance variables and methods.

## Verification Checklist

Before completing a change, verify:

- [ ] Only the requested scope was implemented.
- [ ] Dependencies follow GUI → SERVICE → DAO.
- [ ] Business rules and data transformations reside in SERVICE.
- [ ] Entities remain plain POJOs.
- [ ] DAOs perform database access and row mapping without business-level filtering, sorting, or aggregation.
- [ ] DAO single-record lookups return `Optional`, never `null`; services throw domain exceptions instead of returning `null`.
- [ ] Date inputs use `LGoodDatePicker` (`lib/LGoodDatePicker-11.2.1.jar`), not free-text date strings.
- [ ] All user-visible strings use valid i18n resources, with both languages synchronized.
- [ ] Swing layouts target a fixed 16:9 POS screen.
- [ ] No method comments exist.
- [ ] Any class comments are optional, concise, and written in Vietnamese to describe the class's responsibility.
- [ ] No unrelated refactoring or speculative abstractions were introduced.