// view-switch.js — chuyển giữa các khối [data-view] trong cùng 1 trang (generic, không nghiệp vụ).
//   - Nút [data-view-to="<name>"] (button/a) -> hiện [data-view="<name>"], ẩn mọi [data-view] khác.
//   - Khối mặc định: không có class .hidden trong HTML (các khối khác có .hidden).

(() => {
  function show(name) {
    document.querySelectorAll('[data-view]').forEach((view) => {
      view.classList.toggle('hidden', view.getAttribute('data-view') !== name);
    });
  }

  function init() {
    document.querySelectorAll('[data-view-to]').forEach((el) => {
      el.addEventListener('click', (e) => {
        e.preventDefault();
        show(el.getAttribute('data-view-to'));
      });
    });
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
