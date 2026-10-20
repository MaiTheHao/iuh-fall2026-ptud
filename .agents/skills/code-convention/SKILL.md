---
name: code-convention
description: Enforce Gara Ô tô Lúa Java 17 conventions for every change under `src/iuh/fit` — three-tier layering, POJO entities and record DTOs, transaction-safe DAOs, theme-driven Swing screens, modal pickers, and i18n keys. Use this skill whenever you read, write, review, or refactor any Java file or `i18n_*.json` in this repository (DAO, Service, Entity, DTO, Page, Form, Panel, Dialog, Theme token, message key), even if the user only says "sửa nhanh", "fix bug", "review code", "dọn code", "thêm màn hình", or asks where a class belongs. Also use it before answering how the app is structured.
---

# Code Conventions — Gara Ô tô Lúa

The project is past feature build-out: every change is a stabilization change. Fix correctness first, optimize only measured hot paths, and never add features, screens, or dependencies while doing it.

These rules override conflicting local preferences and existing violations. Details live in `references/`; read only the refs the table below names.

## Non-negotiables

1. Dependencies flow one way: `GUI → SERVICE → DAO → Database`. No DAO-to-DAO calls, no JDBC or SQL in GUI/SERVICE. [01](references/01-architecture.md)
2. Entities are logic-free POJOs; payloads crossing a boundary are records in `iuh.fit.dto`. [02](references/02-entity-dto.md)
3. DAO returns `Optional` for one row, `List` for many, never `null`; SQL is a text block with `?` bindings. [03](references/03-dao.md)
4. Business rules — validation, filtering, sorting, paging, calculation, authorization — live in SERVICE, never in listeners, entities, or SQL. [04](references/04-service.md)
5. SERVICE owns the transaction: open `TransactionScope`, pass it down, commit once. DAO never commits. [05](references/05-transaction.md)
6. Every user-visible string is an i18n key present in both JSON files in the same change. [09](references/09-i18n.md)
7. Messages go through `ThongBao`; `IllegalArgumentException`/domain exception messages are keys, `SQLException` maps to `app.login.errorSystem`. [09](references/09-i18n.md)
8. Every color, font, border, and state comes from `Theme`. [07](references/07-theme.md)
9. The UI targets one fixed desktop size: hardcoded dimensions, dividers, and insets are the convention, not a defect. [06](references/06-swing-ui.md)
10. Picking one record from an unbounded list is a modal dialog returning `Optional<record>`, never a combo box or free-text code field. [08](references/08-dialogs.md)
11. Java 17 only; `this.` for instance members; no method comments; class comments short and in Vietnamese. [10](references/10-java-style.md)
12. Touch only what the request requires; report drift you notice instead of folding it into the change. [11](references/11-checklist.md)

## Rule index

| You are about to… | Read |
|---|---|
| add or move a class, decide which layer owns it | [01-architecture](references/01-architecture.md) |
| change an entity, criteria, form data, enum, exception | [02-entity-dto](references/02-entity-dto.md) |
| write or edit SQL, a DAO method, a row mapper | [03-dao](references/03-dao.md) |
| add validation, search, sort, paging, orchestration | [04-service](references/04-service.md) |
| write across tables, retry an id, commit | [05-transaction](references/05-transaction.md) |
| build or change a page, form, list, filter | [06-swing-ui](references/06-swing-ui.md) |
| choose a color, font, border, or visual state | [07-theme](references/07-theme.md) |
| build a picker or an edit dialog | [08-dialogs](references/08-dialogs.md) |
| add text, a message, a tooltip, or a key | [09-i18n](references/09-i18n.md) |
| write any Java at all | [10-java-style](references/10-java-style.md) |
| finish and report a change | [11-checklist](references/11-checklist.md) |

## Workflow

1. **Locate** — find the rule for the area you are touching; open only the refs the index names.
2. **Match** — read the reference implementation cited in that ref before writing code.
3. **Change** — make the smallest edit that satisfies the rule, inside the existing pattern.
4. **Prove** — run that ref's `## Check`, then the [11-checklist](references/11-checklist.md) sweep for the areas you touched, and report `Changed / Verified / Drift left`.

Line numbers and symbol names in the refs are a snapshot; if a citation does not match, `grep` the symbol and follow the code that exists now.
