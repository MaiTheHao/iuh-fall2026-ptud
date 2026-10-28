# 04 — Service and Business Validation

## Rules

R1. Every feature or entity group has exactly one Service class (e.g., `NhanVienService`, `HoaDonService`).
R2. All business validation lives in SERVICE:
    - Required field checks.
    - Format and regex validation (phone numbers, emails, citizen IDs).
    - Boundary and range checks (price > 0, stock availability).
    - Status transitions (e.g., a cancelled invoice cannot be modified).
R3. When validation fails, throw `IllegalArgumentException` or a specific domain exception with an **i18n message key**:
    ```java
    if (nv.getSoDienThoai() == null || !nv.getSoDienThoai().matches("^0\\d{9}$")) {
        throw new IllegalArgumentException("app.nhanVien.loiSoDienThoai");
    }
    ```
R4. Service methods transform entity models into UI-friendly DTOs (e.g., combining joins, calculating totals, formatting status labels).
R5. Paging and filtering logic are orchestrated in Service: constructing count queries and paginated list queries through DAO methods.

## Reference Code

- `src/iuh/fit/service/NhanVienService.java` — validation, criteria search, status change.
- `src/iuh/fit/service/HoaDonService.java` — inventory checks, price calculation, status guards.

## Anti-Patterns

- Validating phone number format inside a Swing document listener or button action listener.
- Throwing raw hardcoded Vietnamese error messages (`throw new IllegalArgumentException("Số điện thoại không hợp lệ");`). Always throw an i18n key (`app.nhanVien.loiSoDienThoai`).
- Business calculations performed directly in SQL stored procedures or queries when they belong in Java domain rules.
