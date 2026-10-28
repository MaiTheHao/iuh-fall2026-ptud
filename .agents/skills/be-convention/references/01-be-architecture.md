# 01 — Backend Architecture & Layering

## Rules

R1. Dependencies point strictly one way: `SERVICE → DAO → Database`.
R2. A layer imports only the layer directly below it, plus `entity`, `dto`, `enumeration`, `exception`, `util`, and `core`.
R3. SERVICE never imports `java.sql.Connection` or `PreparedStatement`, never constructs SQL queries, and never imports `javax.swing.*`. Declaring `throws SQLException` is the DAO interface contract and stays.
R4. DAO never calls another DAO. Cross-table operations are orchestration and belong exclusively in SERVICE (e.g., `PhieuTiepNhanService` coordinates `PhieuTiepNhanDAO`, `KhachHangDAO`, `XeDAO`).
R5. No JDBC connection opening outside `DatabaseConfig` or `TransactionScope`.
R6. Utility classes are `final`, have a private constructor that throws `AssertionError("Utility class")`, and expose only static methods.

## Reference Code

- `src/iuh/fit/service/NhanVienService.java` — single-DAO service.
- `src/iuh/fit/service/PhieuTiepNhanService.java` — multi-DAO transaction orchestration.
- `src/iuh/fit/dao/NhanVienDAO.java` — `extends BaseDao<NhanVien>`.
- `src/iuh/fit/core/config/DatabaseConfig.java` — central connection factory.

## Anti-Patterns

- A DAO instantiating another DAO: `new KhachHangDAO().timTheoMa(id)` inside `PhieuTiepNhanDAO`.
- `import javax.swing.*` in a Service.
- Catching `SQLException` in a Service and returning null instead of throwing it or mapping to a domain exception.
