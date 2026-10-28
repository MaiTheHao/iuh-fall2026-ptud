# 01 — Swing Architecture & Navigation

## Rules

R1. Every screen is an `AbstractPage` subclass in `gui/page`, implementing `render()` (build the UI) and `getTitleKey()`, registered in `PageRegistry` with its menu item in `MenuRegistry`.
R2. Navigate with `goTo(XxxPage.class)`; never `new XxxPage()` from a listener, so page state survives navigation.
R3. Re-rendering a page must call `clearActionListeners(...)` before attaching listeners again to prevent duplicate handlers on reused page instances.
R4. Dependencies flow one way: `GUI → SERVICE → DTO`. GUI never calls `dao`, never touches `java.sql.*`, and never writes SQL queries.
R5. Target resolution is fixed desktop: `GUI` opens at `1280×800` (maximized upon login), management screens use a full-width data grid, modal form dialogs are `760×800` (min `520×320`), and table row height is `34px` (`44px` with an action column, `72px` with a thumbnail column). No percentage dividers or resize recalculation.
R6. Shell & Header: `LayoutHeader` is fixed `52px` height. Top-level menus display Lucide icons and an active underline indicator (`veChiBaoDuoi`). Right side holds user badge (name + role), language toggle pill (VI/EN with F11 shortcut), and danger logout button.

## Reference Code

- `src/iuh/fit/gui/layout/Layout.java` — CardLayout container and background painting.
- `src/iuh/fit/gui/layout/LayoutHeader.java` — 52px app header with user badge & language toggle.
- `src/iuh/fit/gui/page/AbstractPage.java` — base page contract (`render`, `goTo`, `clearActionListeners`, `taoNhanTrang`).
- `src/iuh/fit/gui/page/PageRegistry.java`, `src/iuh/fit/gui/layout/MenuRegistry.java` — dynamic screen registration.

## Anti-Patterns

- Calling `new XxxPage()` in a listener instead of `goTo(XxxPage.class)`.
- Importing `iuh.fit.dao.*` or `java.sql.*` in any GUI class.
- Dynamic layout calculations using `Toolkit.getDefaultToolkit().getScreenSize()`.
