// Small script injected into Swagger UI to auto-fill the Authorization header
// after a successful login request to /api/v1/auth/token. It listens for
// fetch responses, and when it sees a JSON with a 'token' field from the
// token endpoint, it stores the token in sessionStorage and sets the
// Authorization input of the Swagger UI automatically.
(function () {
  const TOKEN_KEY = 'crm.swagger.token';
  const AUTH_ENDPOINT = '/api/v1/auth/token';

  function setSwaggerToken(token) {
    try {
      // store in sessionStorage for the session
      sessionStorage.setItem(TOKEN_KEY, token);
    } catch (e) {
      console.warn('Could not store token in sessionStorage', e);
    }

    // If Swagger UI global is present, programmatically set the auth
    if (window.ui && window.ui.initOAuth) {
      // try to use the authorize API if available (Swagger UI 3+)
      try {
        const auth = {
          bearerAuth: {
            name: 'bearerAuth',
            schema: { type: 'http', scheme: 'bearer', bearerFormat: 'JWT' },
            value: 'Bearer ' + token
          }
        };
        // Some Swagger UI builds expose a 'authActions' on the UI instance
        if (window.ui.authActions && window.ui.authActions.authorize) {
          window.ui.authActions.authorize({ bearerAuth: { name: 'bearerAuth', schema: { type: 'http' }, value: 'Bearer ' + token } });
        } else if (window.ui.preauthorizeApiKey) {
          window.ui.preauthorizeApiKey('bearerAuth', token);
        }
      } catch (e) {
        // not fatal
      }
    }
  }

  // Replace the fetch function to intercept responses from the auth endpoint
  const originalFetch = window.fetch;
  window.fetch = function () {
    return originalFetch.apply(this, arguments).then(async (resp) => {
      try {
        const url = (arguments[0] && arguments[0].url) || arguments[0];
        if (typeof url === 'string' && url.endsWith(AUTH_ENDPOINT)) {
          const cloned = resp.clone();
          const ct = cloned.headers.get('content-type') || '';
          if (ct.indexOf('application/json') !== -1) {
            const body = await cloned.json();
            if (body && body.token) {
              setSwaggerToken(body.token);
            }
          }
        }
      } catch (e) {
        // ignore
      }
      return resp;
    });
  };

  // On load, if a token exists in sessionStorage, try to preauthorize
  window.addEventListener('load', function () {
    try {
      const t = sessionStorage.getItem(TOKEN_KEY);
      if (t) setSwaggerToken(t);
    } catch (e) {
      // ignore
    }
    // Also try to pre-fill the request body for the auth endpoint so the
    // "Try it out" form is populated with a convenient example.
    tryPrefillAuthExample();
  });

  function tryPrefillAuthExample() {
    const example = JSON.stringify({ email: 'admin@saas.com', password: '123456' }, null, 2);

    // Try multiple times until the Swagger UI has rendered the DOM for the auth op
    let attempts = 0;
    const max = 20;
    const iv = setInterval(() => {
      attempts++;
      // Common container class for operations in Swagger UI is 'opblock'
      const ops = Array.from(document.querySelectorAll('.opblock, .opblock-tag-section'));
      let target = null;

      for (const op of ops) {
        if (op.textContent && op.textContent.includes('/api/v1/auth/token')) {
          target = op;
          break;
        }
      }

      if (!target) {
        if (attempts >= max) clearInterval(iv);
        return;
      }

      // Expand the operation if collapsed
      const summary = target.querySelector('.opblock-summary, .opblock-summary-control');
      if (summary) {
        try { summary.scrollIntoView({ behavior: 'smooth', block: 'center' }); } catch (e) {}
        const isCollapsed = target.classList.contains('is-collapsed') || target.classList.contains('opblock-collapsed');
        if (isCollapsed) {
          const btn = summary.querySelector('button, a');
          if (btn) btn.click();
        }
      }

      // Click "Try it out" if present
      const tryBtn = target.querySelector('button.try-out__btn, button[aria-label="Try it out"], button.btn.try-out');
      if (tryBtn && !tryBtn.disabled) {
        tryBtn.click();
      }

      // Find a textarea or code editor to set the body
      const textareaSelectors = [
        'textarea',
        '.body-param__text',
        '.opblock-body .microlight',
        '.opblock .body-param__text',
        '.CodeMirror textarea',
      ];

      let filled = false;
      for (const sel of textareaSelectors) {
        const ta = target.querySelector(sel);
        if (ta) {
          // CodeMirror uses a hidden textarea; try to set value and dispatch events
          try {
            if (ta.tagName === 'TEXTAREA' || ta.tagName === 'TEXTAREA') {
              ta.value = example;
              ta.dispatchEvent(new Event('input', { bubbles: true }));
              filled = true;
              break;
            } else {
              // fallback: set textContent
              ta.textContent = example;
              filled = true;
              break;
            }
          } catch (e) {
            // ignore and try next
          }
        }
      }

      // If there is a CodeMirror instance, try to find and set its content
      try {
        const cmEls = target.querySelectorAll('.CodeMirror');
        if (!filled && cmEls.length > 0) {
          cmEls.forEach((el) => {
            try {
              // the CodeMirror instance may be attached to the DOM node
              const cm = (el as any).__cm || (el as any).CodeMirror || null;
              if (cm && typeof cm.setValue === 'function') cm.setValue(example);
            } catch (e) {}
          });
          filled = true;
        }
      } catch (e) {}

      // If filled, stop trying
      if (filled) clearInterval(iv);
      if (attempts >= max) clearInterval(iv);
    }, 500);
  }
})();
