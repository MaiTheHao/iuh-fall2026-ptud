// table-filter.js — component lọc & tìm kiếm đặt TRÊN bảng (không nhúng trong <thead>).
//   - Mỗi .swing-filter-panel áp cho bảng trong cùng .swing-panel.
//   - Mỗi control có [data-col] = chỉ số cột <td> cần so khớp.
//     • mặc định            : string — khớp chứa, bỏ dấu tiếng Việt, không phân biệt hoa/thường.
//     • [data-type=number]  : from-to — so số (bỏ dấu phân cách nghìn).
//     • [data-type=date]    : from-to — so ngày (ô dd/MM/yyyy, input yyyy-mm-dd).
//   - Dòng hành động: [data-filter-quick] (mọi cột), [data-filter-count] (Hiển thị x/y),
//     [data-filter-clear] (xóa mọi ô lọc). Lọc tức thì khi gõ/đổi.
// KHÔNG chứa logic nghiệp vụ của từng trang.

(() => {
  function normalize(value) {
    return (value || '')
      .toString()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '')
      .replace(/đ/g, 'd')
      .replace(/Đ/g, 'D')
      .toLowerCase()
      .trim();
  }

  function parseNumber(text) {
    const cleaned = (text || '').replace(/[^\d.-]/g, '');
    if (!cleaned || cleaned === '-' || cleaned === '.') return null;
    const value = parseFloat(cleaned);
    return Number.isNaN(value) ? null : value;
  }

  // Ô ngày dạng dd/MM/yyyy (có thể kèm giờ) -> Date; input date yyyy-mm-dd -> Date.
  function parseDate(text) {
    const match = (text || '').match(/(\d{1,2})\/(\d{1,2})\/(\d{4})/);
    if (match) return new Date(+match[3], +match[2] - 1, +match[1]);
    const iso = (text || '').match(/^(\d{4})-(\d{2})-(\d{2})$/);
    if (iso) return new Date(+iso[1], +iso[2] - 1, +iso[3]);
    return null;
  }

  function matches(cell, control) {
    const raw = (control.value || '').trim();
    if (raw === '') return true;

    const type = control.getAttribute('data-type') || 'text';
    const cellText = cell ? (cell.getAttribute('data-value') || cell.textContent) : '';

    if (type === 'number') {
      const wanted = parseFloat(raw);
      const actual = parseNumber(cellText);
      if (Number.isNaN(wanted)) return true;
      if (actual === null) return false;
      return control.getAttribute('data-range') === 'min' ? actual >= wanted : actual <= wanted;
    }

    if (type === 'date') {
      const wanted = parseDate(raw);
      const actual = parseDate(cellText);
      if (!wanted) return true;
      if (!actual) return false;
      const diff = actual.getTime() - wanted.getTime();
      return control.getAttribute('data-range') === 'min' ? diff >= 0 : diff <= 0;
    }

    return normalize(cellText).includes(normalize(raw));
  }

  function initPanel(panel) {
    const scope = panel.closest('.swing-panel') || document;
    const table = scope.querySelector('table.swing-table');
    if (!table) return;

    const controls = Array.from(panel.querySelectorAll('[data-col]'));
    const rows = Array.from(table.querySelectorAll('tbody > tr'));
    const quick = panel.querySelector('[data-filter-quick]');
    const countEl = panel.querySelector('[data-filter-count]');
    const clearBtn = panel.querySelector('[data-filter-clear]');
    const total = rows.length;

    function apply() {
      const quickTerm = quick ? normalize(quick.value) : '';
      let visible = 0;

      rows.forEach((row) => {
        const cells = row.children;
        let ok = true;

        for (const control of controls) {
          const col = parseInt(control.getAttribute('data-col'), 10);
          if (!matches(cells[col], control)) {
            ok = false;
            break;
          }
        }

        if (ok && quickTerm && !normalize(row.textContent).includes(quickTerm)) {
          ok = false;
        }

        row.classList.toggle('hidden', !ok);
        if (ok) visible++;
      });

      if (countEl) countEl.textContent = 'Hiển thị ' + visible + '/' + total;
    }

    controls.forEach((control) => {
      control.addEventListener('input', apply);
      control.addEventListener('change', apply);
    });

    if (quick) quick.addEventListener('input', apply);

    if (clearBtn) {
      clearBtn.addEventListener('click', () => {
        controls.forEach((control) => {
          control.value = '';
        });
        if (quick) quick.value = '';
        apply();
      });
    }

    apply();
  }

  function init() {
    document.querySelectorAll('.swing-filter-panel').forEach(initPanel);
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
