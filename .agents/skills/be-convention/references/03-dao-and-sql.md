# 03 — DAO and SQL Layer

## Rules

R1. Every DAO extends `BaseDao<T>` and handles exactly one table.
R2. Query methods return `Optional<T>` for single rows, `List<T>` for multiple rows. **NEVER return `null`**.
R3. All SQL statements must be Java 17 text blocks (`"""`), uppercase keywords (`SELECT`, `FROM`, `WHERE`, `JOIN`, `ORDER BY`), parameterized with `?`.
R4. Map result sets via private row mapper methods:
    ```java
    private NhanVien mapRow(ResultSet rs) throws SQLException {
        NhanVien nv = new NhanVien();
        nv.setMaNhanVien(rs.getString("maNhanVien"));
        nv.setHoTen(rs.getString("hoTen"));
        // ...
        return nv;
    }
    ```
R5. Every method that writes data accepts an optional `Connection` parameter to participate in transactions:
    ```java
    public boolean them(NhanVien nv) throws SQLException {
        return this.them(nv, null);
    }

    public boolean them(NhanVien nv, Connection conn) throws SQLException {
        // If conn is provided, use it without closing; otherwise open from DatabaseConfig with try-with-resources
    }
    ```
R6. DAO methods declare `throws SQLException`. DAO never catches and swallows exceptions, and **DAO NEVER commits**.

## Reference Code

- `src/iuh/fit/dao/BaseDao.java` — generic helper methods.
- `src/iuh/fit/dao/NhanVienDAO.java` — standard single-entity DAO.
- `src/iuh/fit/dao/PhieuTiepNhanDAO.java` — transaction-aware write methods.

## Anti-Patterns

- String concatenation in SQL: `sql = "SELECT * FROM NhanVien WHERE ma = '" + id + "'"` (Security Vulnerability / SQL Injection).
- Returning `null` instead of `Optional.empty()` or `Collections.emptyList()`.
- Calling `conn.commit()` inside a DAO method.
- DAO calling another DAO method.
