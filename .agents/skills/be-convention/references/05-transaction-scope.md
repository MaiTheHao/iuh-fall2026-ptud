# 05 — Transactions and Multi-Table Writes

## Rules

R1. The SERVICE layer owns the transaction boundary.
R2. Any operation modifying more than one table, or requiring atomic generation of business IDs, must use `TransactionScope`:
    ```java
    try (TransactionScope scope = new TransactionScope()) {
        Connection conn = scope.getConnection();

        // 1. Write parent record
        this.hoaDonDAO.them(hoaDon, conn);

        // 2. Write child lines
        for (ChiTietHoaDon ct : chiTietList) {
            this.chiTietDAO.them(ct, conn);
        }

        // 3. Update inventory
        this.phuTungDAO.giamTonKho(ct.getMaPhuTung(), ct.getSoLuong(), conn);

        // 4. Commit explicitly
        scope.commit();
    }
    // If an exception occurs before scope.commit(), autoCloseable rolls back automatically.
    ```
R3. The DAO never opens its own connection or commits when a `Connection` is passed in from `TransactionScope`.
R4. ID Retry Logic: For business codes generated from sequences or counters (e.g., `HD-20261028-001`), retry on duplicate key violations inside the transaction block up to a maximum limit (typically 3 attempts).

## Reference Code

- `src/iuh/fit/dao/TransactionScope.java` — `AutoCloseable` transaction manager.
- `src/iuh/fit/service/PhieuTiepNhanService.java#tiepNhan` — multi-table write orchestration with rollback.
- `src/iuh/fit/service/HoaDonService.java#lapHoaDon` — atomic invoice creation and stock deduction.

## Anti-Patterns

- Executing multiple DAO insert/update calls sequentially without wrapping in `TransactionScope`.
- Catching an exception inside a multi-step write and continuing instead of allowing the transaction to roll back.
