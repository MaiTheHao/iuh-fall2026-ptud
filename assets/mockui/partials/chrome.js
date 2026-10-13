// chrome.js — hạ tầng dùng chung cho mockup tĩnh:
//   1) nạp các partial (titlebar/menubar/statusbar/pos-*) vào [data-include]
//   2) mở/đóng menu JMenuBar + đánh dấu mục đang active
//   3) render icon Lucide (CDN) cho cả icon tĩnh lẫn icon trong partial vừa nạp
// KHÔNG chứa logic nghiệp vụ / tính toán của từng trang.

(() => {
  // Đường dẫn partial tính từ pages/, nên partials nằm ở ../partials/.
  function deriveScreenId() {
    const base = location.pathname.split('/').pop() || '';
    return base.replace(/\.[^.]+$/, '').toUpperCase();
  }

  function activateMenuItem() {
    const screenId = deriveScreenId();
    if (!screenId) return;

    document.querySelectorAll('#mainMenuBar .swing-menu-item').forEach((el) => {
      el.classList.toggle('active', el.getAttribute('data-screen') === screenId);
    });

    document.querySelectorAll('#mainMenuBar .swing-menu-dropdown-item').forEach((el) => {
      const match = el.getAttribute('data-screen') === screenId;
      el.classList.toggle('active', match);
      if (match) {
        const parentMenu = el.closest('.swing-menu');
        if (parentMenu) {
          const trigger = parentMenu.querySelector('.swing-menu-trigger');
          if (trigger) trigger.classList.add('font-bold', 'underline');
        }
      }
    });
  }

  function initMenuEvents() {
    const menus = document.querySelectorAll('#mainMenuBar .swing-menu');

    function closeAll(except) {
      menus.forEach((m) => {
        if (m !== except) m.classList.remove('open');
      });
    }

    menus.forEach((menu) => {
      const trigger = menu.querySelector('.swing-menu-trigger');
      if (!trigger) return;

      // Hover menu cha → hiện dropdown (không cần click).
      menu.addEventListener('mouseenter', () => {
        closeAll(menu);
        menu.classList.add('open');
      });
      menu.addEventListener('mouseleave', () => {
        menu.classList.remove('open');
      });

      // Click vẫn mở (dự phòng khi không dùng chuột) — không toggle-đóng.
      trigger.addEventListener('click', (e) => {
        e.stopPropagation();
        closeAll(menu);
        menu.classList.add('open');
      });
    });
  }

  async function loadPartials() {
    const placeholders = document.querySelectorAll('[data-include]');
    for (const placeholder of placeholders) {
      const name = placeholder.getAttribute('data-include');
      try {
        const res = await fetch('../partials/' + name + '.html');
        if (!res.ok) throw new Error('HTTP ' + res.status);
        const text = await res.text();
        const doc = new DOMParser().parseFromString(text, 'text/html');
        placeholder.replaceWith(...doc.body.childNodes);
        if (name === 'menubar') {
          activateMenuItem();
          initMenuEvents();
        }
      } catch (err) {
        console.warn('chrome.js: failed to load partial "' + name + '"', err);
      }
    }
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => {
      loadPartials();
      initLucideIcons();
    });
  } else {
    loadPartials();
    initLucideIcons();
  }
})();

// Lucide icon library (CDN) + auto-render for static icons and injected partials.
function initLucideIcons() {
  const render = () => {
    if (window.lucide && typeof window.lucide.createIcons === 'function') {
      window.lucide.createIcons();
    }
  };
  const script = document.createElement('script');
  script.src = 'https://unpkg.com/lucide@1.51.0/dist/umd/lucide.min.js';
  script.onload = () => {
    render();
    const observer = new MutationObserver(() => {
      if (document.querySelector('i[data-lucide]')) render();
    });
    observer.observe(document.body, { childList: true, subtree: true });
  };
  document.head.appendChild(script);
}
