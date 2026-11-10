---
name: comment-convention
description: Add concise Vietnamese code comments using Intent-Based and Section-Based conventions to help students understand, explain, and defend their source code during academic evaluations.
---

# Academic Code Commenting

## Purpose

Add meaningful comments to source code to help students understand its implementation and explain technical decisions when questioned by lecturers.

The goal is **code comprehension and academic defense**, not extensive documentation.

## Core Convention

Use a combination of:

- **Intent-Based:** Explain why a particular implementation or technical decision exists.
- **Section-Based:** Divide methods into meaningful logical stages when necessary.

All generated source-code comments MUST be written in **Vietnamese with proper diacritics**.

Skill instructions and internal reasoning remain in English.

## Commenting Rules

### 1. Class-Level Comments

Briefly describe the class's primary responsibility and purpose.

Use Javadoc-style comments only when they provide meaningful context.

Example:

```java
/**
 * JPanel tùy chỉnh hiển thị nền và đường viền bo góc.
 */
```

Avoid documenting obvious inheritance or repeating the class name.

### 2. Section-Based Comments

Use short comments to identify distinct logical stages within a method.

Example:

```java
// Cấu hình chế độ hiển thị và khử răng cưa.

// Vẽ màu nền của component.

// Vẽ đường viền bo góc.
```

Rules:
- Use Vietnamese.
- Keep descriptions short and meaningful.
- Separate logical responsibilities, not individual statements.
- Do not number sections.
- Do not use decorative banners or ASCII separators.
- Do not create sections in short, straightforward methods.

### 3. Intent-Based Comments

Explain the reasoning behind non-obvious implementation decisions.

Prioritize:
- Why a particular API or mechanism is used.
- Why a specific execution order is necessary.
- Why a resource or object is created or released.
- Why a particular implementation avoids a problem.
- Important framework behaviors that are not immediately obvious.

Example:

```java
// Tạo Graphics context riêng để các thay đổi trạng thái vẽ
// không ảnh hưởng đến Graphics context ban đầu.
Graphics2D doHoa = (Graphics2D) g.create();
```

Avoid translating the implementation into natural language.

Bad:

```java
// Gán màu cho Graphics2D.
doHoa.setColor(Theme.palette().PANEL());
```

Good:

```java
// Lấy màu nền từ Theme để đồng bộ giao diện giữa các component.
doHoa.setColor(Theme.palette().PANEL());
```

Only use the second comment when the design intention is relevant and not already obvious from surrounding context.

### 4. Academic Comprehension

Comments should help answer these questions:

1. **What:** Đoạn code này thực hiện nhiệm vụ gì?
2. **Why:** Tại sao cần thực hiện thao tác này?
3. **How:** Cơ chế hoặc API nào được sử dụng?
4. **When:** Thao tác này diễn ra ở giai đoạn nào?
5. **Why this approach:** Tại sao lựa chọn cách triển khai này thay vì cách khác?

Not every comment needs to answer all five questions.

Prioritize Why and technical reasoning. Use What and How when they clarify complex code.

### 5. Comment Density

Follow these guidelines:

| Code situation | Comment strategy |
|---|---|
| Simple assignments | No comment |
| Self-explanatory methods | No comment |
| Non-obvious technical decisions | Intent comment |
| Multiple logical stages | Section comments |
| Complex algorithms | Explain the core reasoning |
| Framework-specific behavior | Explain relevant semantics |
| Workarounds or limitations | Explain the underlying reason |
| Resource lifecycle | Explain ownership or cleanup when non-obvious |

Keep comments proportional to code complexity.

## Language and Writing Style

- All source-code comments must be Vietnamese.
- Use proper Vietnamese diacritics.
- Prefer simple, direct, technically accurate sentences.
- Preserve standard technical terminology when appropriate, such as `Graphics2D`, `Antialiasing`, `Thread`, `Transaction`, and `Dependency Injection`.
- Avoid overly academic, verbose, or artificial wording.
- Avoid informal slang.
- Do not write comments as conversations or questions.
- Do not claim technical behavior that cannot be verified from the code.

## Code Preservation

When adding comments:

1. Preserve the original implementation.
2. Do not rename variables, methods, classes, or packages.
3. Do not refactor code unless explicitly requested.
4. Do not change execution order or business logic.
5. Do not introduce unnecessary abstractions.
6. Do not add comments to every line.
7. Do not remove existing meaningful comments.
8. Correct misleading existing comments when their actual behavior can be verified.

## Final Quality Check

Before completing the task, verify:

- [ ] All newly generated comments are in Vietnamese.
- [ ] Class-level comments describe actual responsibilities.
- [ ] Section comments represent meaningful logical stages.
- [ ] Intent comments explain non-obvious decisions.
- [ ] Comments do not merely repeat the code.
- [ ] Technical explanations are accurate.
- [ ] Comment density remains lightweight.
- [ ] Original code behavior is unchanged.

## Guiding Principle

**Code demonstrates the implementation. Comments explain the reasoning.**

Write comments so that a student can read the source code, understand the implementation, and explain why it works when questioned by a lecturer.

Do not turn source code into a textbook. Focus on the knowledge necessary to understand and defend the implementation.
