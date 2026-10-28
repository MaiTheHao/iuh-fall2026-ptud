# 06 — Backend Verification & Grep Checklist

Run these checks whenever modifying files in `src/iuh/fit/service/`, `dao/`, `entity/`, `dto/`:

## 1. No Reverse Layering (GUI/Swing in Backend)

```bash
# Expect: empty (no Swing imports in Service or DAO)
grep -rn 'javax\.swing\.' src/iuh/fit/service src/iuh/fit/dao src/iuh/fit/entity src/iuh/fit/dto

# Expect: empty (no DAO calling another DAO)
grep -rn 'new [A-Za-z0-9]*DAO(' src/iuh/fit/dao
```

## 2. No SQL In Service Layer

```bash
# Expect: empty (no PreparedStatement or raw SQL strings in Service)
grep -rn 'PreparedStatement\|DriverManager\|Connection ' src/iuh/fit/service | grep -v 'TransactionScope'
grep -rn 'SELECT \|INSERT \|UPDATE \|DELETE ' src/iuh/fit/service
```

## 3. SQL Injection Prevention

```bash
# Expect: empty (no string concatenation with variables inside SQL strings in DAO)
grep -rn '\+ [a-zA-Z0-9_]* \+' src/iuh/fit/dao | grep -i 'SELECT\|INSERT\|UPDATE\|DELETE'
```

## 4. Null Safety in DAO Queries

```bash
# Verify that DAO query methods return Optional or List, not bare null
grep -rn 'return null;' src/iuh/fit/dao
```

## 5. Compilation Verification

```bash
find src -name "*.java" > /tmp/sources.txt && javac -d bin -cp "lib/*:bin" @/tmp/sources.txt
# Expect: exit code 0
```
