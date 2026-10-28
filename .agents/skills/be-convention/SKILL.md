---
name: be-convention
description: Enforce Gara Ô tô Lúa Java 17 backend conventions for all files under `src/iuh/fit/service`, `src/iuh/fit/dao`, `src/iuh/fit/entity`, `src/iuh/fit/dto`, `src/iuh/fit/core`, `src/iuh/fit/exception`. Use this skill whenever reading, writing, reviewing, or refactoring DAOs, Services, Entities, DTO records, SQL queries, transactions, or business validation logic.
---

# Backend Conventions — Gara Ô tô Lúa (Java 17 Service & DAO)

This skill governs all server-side and business logic development under `src/iuh/fit/service`, `dao`, `entity`, `dto`, `core`, and `exception`.

---

## 1. Non-Negotiables for Backend

1. **Unidirectional Layering**: `GUI → SERVICE → DAO → Database`.
   - `SERVICE` orchestrates one or more `DAO`s.
   - `DAO` communicates only with the Database via JDBC.
   - **NO DAO-to-DAO calls**: Multi-table operations belong in SERVICE.
   - **NO SQL/JDBC in SERVICE or GUI**: Only `dao` packages hold SQL or `PreparedStatement`.
2. **Entities vs. DTOs**:
   - Entities (`iuh.fit.entity.*`) are logic-free POJOs mapped directly to database tables.
   - Payloads crossing layer boundaries (search criteria, form input, table views, paging) MUST be immutable Java 17 records in `iuh.fit.dto.*`.
3. **DAO Safety & Null-Free Contracts**:
   - DAO query methods return `Optional<T>` for single rows, and `List<T>` for multiple rows.
   - **NEVER return `null`** from a DAO query method.
   - All SQL statements must be Java 17 text blocks (`"""`) with `?` parameter bindings.
   - **NO SQL injection**: Never concatenate variables into SQL strings.
   - DAO never commits transactions.
4. **Service Owns Business Rules & Validation**:
   - Validation, filtering, sorting, paging calculations, authorization, and business computations live exclusively in `SERVICE`, never in listeners, entities, or SQL.
   - Validation failures throw `IllegalArgumentException` or domain exceptions with i18n message keys.
5. **Service Owns Transactions**:
   - Multi-step writes must be wrapped in `TransactionScope`.
   - Pass the shared `Connection` down to DAO methods. Commit once atomically upon success. Rollback automatically on failure.
6. **Java 17 Language Idioms**:
   - Use `this.` for instance fields and methods.
   - Use records for DTOs.
   - Use pattern matching for `instanceof` and switch expressions where appropriate.
   - No method comments; class comments short and in Vietnamese.

---

## 2. Package Map (`src/iuh/fit`)

```
src/iuh/fit/
├── core/
│   ├── config/DatabaseConfig.java  # The ONLY factory for Database connections
│   └── context/AppContext.java     # Session state (currentUser, locale, currentPage)
├── entity/                         # Logic-free POJOs (one per table, getters/setters/constructors)
├── dto/                            # Immutable records for criteria, views, paging, form inputs
├── dao/
│   ├── BaseDao.java                # Common CRUD helpers
│   ├── TransactionScope.java       # Atomic transaction boundary manager
│   └── XxxDAO.java                 # Data Access Object per entity/table
├── service/
│   └── XxxService.java             # Business validation, calculations, transaction orchestration
└── exception/                      # Domain exceptions carrying i18n keys
```

---

## 3. Quick Rule Index

| Topic | Reference Document |
|---|---|
| Layer Direction, Package Rules & Imports | [01-be-architecture.md](references/01-be-architecture.md) |
| Entities (POJOs) and DTOs (Records) | [02-entity-dto.md](references/02-entity-dto.md) |
| DAO Methods, SQL Text Blocks & RowMappers | [03-dao-and-sql.md](references/03-dao-and-sql.md) |
| Service Logic, Validation & Error Keys | [04-service-validation.md](references/04-service-validation.md) |
| TransactionScope & Multi-table Writes | [05-transaction-scope.md](references/05-transaction-scope.md) |
| Backend Verification & Grep Checklist | [06-be-checklist.md](references/06-be-checklist.md) |
