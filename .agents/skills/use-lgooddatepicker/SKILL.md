---
name: use-lgooddatepicker
description: Rules for using the LGoodDatePicker library (v11.2.1) in Gara Ô tô Lúa Swing code under `src/iuh/fit`. Use whenever a screen needs to pick or display a date, date-time, or date range (DatePicker, TimePicker, DateTimePicker, DatePickerSettings), or whenever an entity, DAO, or service handles LocalDate/LocalDateTime values coming from or going to a picker. Always apply together with the `code-convention` skill, and use this skill even if the request only says "chọn ngày", "ô nhập ngày", "date field", or "lọc theo khoảng ngày".
---

# Use LGoodDatePicker — Gara Ô tô Lúa

Rules for adding and using LGoodDatePicker without breaking `code-convention`. If the two skills conflict, `code-convention` wins.

## 1. Dependency

- Library: `com.github.lgooddatepicker:LGoodDatePicker`, version **11.2.1** (latest "Standard" release; bug-fix only, Java 8+, based on `java.time`).
- Use the project's existing dependency mechanism: Maven/Gradle coordinate if the project builds with one, otherwise add `LGoodDatePicker-11.2.1.jar` from the release assets to the project's existing library folder.
- Do not add the Backports build, other date-picker libraries, or a second version.
- Do not upgrade the version unless asked.

## 2. Layer Responsibilities (agent routing)

The picker is a Swing component, so it exists in the GUI layer only. Dates cross layers as `java.time` types.

| Layer / agent | Allowed | Forbidden |
|---|---|---|
| GUI (`iuh.fit.gui`) | Create `DatePicker` / `DateTimePicker`, configure settings, read `getDate()`, call `setDate(...)`, pass values to services, display results/errors | Date business rules, range checks, "today"/deadline calculations, calling DAOs, `java.sql` types |
| SERVICE (`iuh.fit.service`) | Validate dates (null, past/future, `from <= to`, working-day rules), calculate durations/due dates, filter/sort by date | Importing `com.github.lgooddatepicker.*` or any Swing class |
| DAO (`iuh.fit.dao`) | Convert `java.sql.Date`/`Timestamp` ⇄ `LocalDate`/`LocalDateTime` while mapping rows; plain retrieval | `WHERE` date-range rules that encode business logic, library imports |
| Entity (`iuh.fit.entity`) | Private `LocalDate` / `LocalDateTime` fields with getters/setters | `java.sql.Date`, `java.util.Date`, library types, validation, formatting |

## 3. Data Types

- Entity fields: `LocalDate` for date-only, `LocalDateTime` for date + time. Never `java.util.Date`, `Calendar`, or `java.sql.*` in entities, services, or GUI.
- GUI → SERVICE: pass `LocalDate` / `LocalDateTime` (from `datePicker.getDate()` or `dateTimePicker.getDateTimeStrict()`), never the formatted text.
- DAO mapping examples:

```java
LocalDate ngayNhan = rs.getObject("ngay_nhan", LocalDate.class);
ps.setObject(1, entity.getNgayNhan());
```

- `getDate()` returns `null` when the field is empty or the typed text is invalid. The SERVICE decides whether `null` is allowed and returns the error; the GUI only displays it.

## 4. Business Rules Stay in SERVICE

- Do not put business logic inside a `DateVetoPolicy`, `DateHighlightPolicy`, or a `DateChangeListener`.
- If a veto/highlight policy is genuinely needed, it may only delegate to a service method (e.g. `this.phieuSuaChuaService.isNgayHopLe(date)`), with no condition of its own.
- Range validation (`from <= to`), "not in the past", max-days, etc. are service methods called when the user submits or when a `DateChangeListener` forwards the new value.
- Do not call `LocalDate.now()` for a business decision in the GUI. The service owns it. `datePicker.setDateToToday()` is allowed only for pre-filling a field.

## 5. i18n and Locale

All user-visible text resolves through `I18nReaderUtil` with keys present in both `src/resources/i18n_vn.json` and `src/resources/i18n_en.json`. Keep both files synchronized.

Keys to reuse or add (follow the existing key naming style in the JSON files):

| Purpose | Setting | Key (example) |
|---|---|---|
| "Today" button | `settings.setTranslationToday(...)` | `date.today` |
| "Clear" button | `settings.setTranslationClear(...)` | `date.clear` |
| Display/parse pattern | `settings.setFormatForDatesCommonEra(...)` | `date.format` (vn: `dd/MM/yyyy`) |

- Use the project's current-language `Locale` for `new DatePickerSettings(locale)`. Check how existing screens obtain it. Never hard-code a `Locale` in a screen.
- Do not hard-code button text, date patterns, or error messages in source code.
- Library built-in translations may not cover every language. After setting the translation overrides above, check the month/weekday labels in the target language and report any gap instead of patching it with hard-coded strings.
- If the app switches language at runtime, rebuild the picker settings the same way existing screens refresh their i18n text. Do not add a new refresh mechanism.

## 6. Fixed 16:9 POS Layout

- Use the library defaults for the popup calendar. Never add scaling or responsive logic around it.
- Size the picker field with the same fixed-size constants/layout manager pattern the screen already uses. Do not compute sizes from window dimensions.
- Prefer `DatePicker` for date-only inputs. Use `DateTimePicker` only when the requirement needs both date and time. Do not use `TimePicker` alone unless asked.

## 7. Standard Configuration

Apply only the settings the requirement needs (KISS/YAGNI):

```java
DatePickerSettings settings = new DatePickerSettings(locale);
settings.setFormatForDatesCommonEra(I18nReaderUtil.get("date.format"));
settings.setTranslationToday(I18nReaderUtil.get("date.today"));
settings.setTranslationClear(I18nReaderUtil.get("date.clear"));
settings.setAllowEmptyDates(false);
this.dpNgayNhan = new DatePicker(settings);
```

Replace `I18nReaderUtil.get(...)` with the project's actual lookup method. Do not invent a new one.

- `setAllowEmptyDates(false)` for mandatory dates; leave the default for optional filters.
- `setAllowKeyboardEditing(false)` only if the requirement says "chọn từ lịch, không gõ tay".
- Reuse one screen-private builder method when the same screen creates 2+ pickers (e.g. từ ngày / đến ngày). Create a shared GUI helper class only when 2+ screens need identical settings and no helper exists yet.
- Reading and writing values:

```java
LocalDate tuNgay = this.dpTuNgay.getDate();
this.dpNgayNhan.setDate(entity.getNgayNhan());
```

- Date-range filters: the GUI reads both values and calls one service method (e.g. `this.phieuSuaChuaService.timTheoKhoangNgay(tuNgay, denNgay)`). The service validates the order, filters and sorts. The DAO only returns raw rows (`findAll()`).

## 8. Java Conventions Reminder

- Use `this.` for every instance field and method (`this.dpNgayNhan`, `this.initDatePicker()`).
- No method comments (including Javadoc). A class comment is optional, brief, and in Vietnamese.
- No unrelated refactoring while adding a picker.

## 9. Agent Team Workflow

1. **Planner:** list affected screens, entities, services, DAO methods; confirm date vs date-time per field.
2. **Entity agent:** switch/add `LocalDate`/`LocalDateTime` fields (POJO only).
3. **DAO agent:** map row ⇄ `LocalDate`/`LocalDateTime`; raw retrieval only.
4. **Service agent:** date validation, range checks, calculations, filtering, sorting.
5. **GUI agent:** create/configure the picker, read/set values, call the service, show the result via i18n text.
6. **i18n agent:** add/sync `date.*` keys in `i18n_vn.json` and `i18n_en.json`.
7. **Reviewer:** run the checklist below plus the `code-convention` checklist.

## Verification Checklist

- [ ] Library version is 11.2.1 and added through the project's existing dependency mechanism.
- [ ] `com.github.lgooddatepicker.*` imports appear only in `iuh.fit.gui`.
- [ ] Entities, services, and DAOs use only `LocalDate` / `LocalDateTime`; no `java.util.Date` or `java.sql` types outside DAO row mapping.
- [ ] No business rule inside veto/highlight policies or listeners; they only delegate to a service.
- [ ] `null` / invalid / range checks live in the service.
- [ ] Today/Clear labels and date pattern come from synchronized i18n keys; no hard-coded text or `Locale`.
- [ ] No responsive/scaling logic around pickers; fixed 16:9 layout kept.
- [ ] Only the requested pickers/settings were added; no speculative helpers.
- [ ] `this.` used, no method comments, class comments (if any) in Vietnamese.