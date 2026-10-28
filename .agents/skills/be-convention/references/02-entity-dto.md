# 02 — Entities and DTOs

## 1. Entities (`iuh.fit.entity.*`)

- Entities are logic-free POJOs mirroring database tables.
- They contain only:
  - Private fields matching table columns.
  - No-arg constructor, full-args constructor, copy constructor (if needed).
  - Getters, setters, `equals`, `hashCode`, `toString`.
- **NO business logic** in entity methods (no validation, calculation, or status transitions).
- Primary keys are `String` (custom business code) or `Integer`/`Long` (identity).

## 2. DTOs (`iuh.fit.dto.*`)

- All DTOs are immutable **Java 17 records**.
- Types of DTOs:
  - **Criteria / Filter**: e.g., `NhanVienCriteria(String tuKhoa, TrangThaiNhanVien trangThai, int trang, int kichThuoc)`.
  - **Form Input**: Data submitted from forms before validation and conversion to entities.
  - **View / Table Row**: Data formatted specifically for UI rendering (e.g., joins flattened into display fields).
  - **Paging Result**: `KetQuaPhanTrang<T>(List<T> danhSach, int tongSoDong, int trangHienTai, int tongSoTrang)`.
- Use factory methods `from(Entity)` or compact constructors for normalization (e.g., trimming strings, setting defaults).
- Records must not hold mutable state or database connection references.
