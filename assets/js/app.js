/* =====================================================================
   Module DevOps — app.js
   Navigation par hash, chargement des sections (fetch), accordéons,
   barre de progression de lecture, drawer mobile, pager préc./suiv.
   ===================================================================== */

'use strict';

/* --------------------------------------------------------------------
   Source unique de vérité : la liste des chapitres.
   Pilote la sidebar, le routing et le mapping vers /sections/*.html
   -------------------------------------------------------------------- */
const CHAPTERS = [
  { id: 'intro',        title: 'Introduction au DevOps',                        icon: 'rocket' },
  { id: 'git',          title: 'Versioning & Collaboration (Git)',              icon: 'git-branch' },
  { id: 'ci',           title: 'Intégration Continue (CI)',                     icon: 'refresh' },
  { id: 'docker',       title: 'Conteneurisation (Docker)',                     icon: 'box' },
  { id: 'kubernetes',   title: 'Orchestration (Kubernetes)',                    icon: 'wheel' },
  { id: 'terraform',    title: 'Infrastructure as Code (Terraform)',            icon: 'layers' },
  { id: 'gitops',       title: 'GitOps & Déploiement Continu (CD)',             icon: 'git-merge' },
  { id: 'monitoring',   title: 'Monitoring & Observabilité',                    icon: 'activity' },
  { id: 'security',     title: 'Sécurité DevSecOps',                            icon: 'shield' },
  { id: 'architecture', title: 'Architecture de déploiement complète',          icon: 'grid' },
  { id: 'digitalocean', title: 'Pratique : déploiement sur DigitalOcean',       icon: 'cloud' },
  { id: 'scaling',      title: 'Scalabilité & Montée en charge',                icon: 'trending' },
];

/* Icônes SVG (style Lucide, trait fin) --------------------------------- */
const ICONS = {
  'rocket':     '<path d="M4.5 16.5c-1.5 1.26-2 5-2 5s3.74-.5 5-2c.71-.84.7-2.13-.09-2.91a2.18 2.18 0 0 0-2.91-.09z"/><path d="M12 15l-3-3a22 22 0 0 1 2-3.95A12.88 12.88 0 0 1 22 2c0 2.72-.78 7.5-6 11a22.35 22.35 0 0 1-4 2z"/><path d="M9 12H4s.55-3.03 2-4c1.62-1.08 5 0 5 0"/><path d="M12 15v5s3.03-.55 4-2c1.08-1.62 0-5 0-5"/>',
  'git-branch': '<line x1="6" y1="3" x2="6" y2="15"/><circle cx="18" cy="6" r="3"/><circle cx="6" cy="18" r="3"/><path d="M18 9a9 9 0 0 1-9 9"/>',
  'refresh':    '<path d="M3 12a9 9 0 0 1 9-9 9.75 9.75 0 0 1 6.74 2.74L21 8"/><path d="M21 3v5h-5"/><path d="M21 12a9 9 0 0 1-9 9 9.75 9.75 0 0 1-6.74-2.74L3 16"/><path d="M8 16H3v5"/>',
  'box':        '<path d="M21 8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16Z"/><path d="m3.3 7 8.7 5 8.7-5"/><line x1="12" y1="22" x2="12" y2="12"/>',
  'wheel':      '<circle cx="12" cy="12" r="8"/><circle cx="12" cy="12" r="2"/><path d="M12 4v4"/><path d="M12 16v4"/><path d="M4 12h4"/><path d="M16 12h4"/>',
  'layers':     '<path d="m12.83 2.18a2 2 0 0 0-1.66 0L2.6 6.08a1 1 0 0 0 0 1.83l8.58 3.91a2 2 0 0 0 1.66 0l8.58-3.9a1 1 0 0 0 0-1.83Z"/><path d="m22 12.18-9.17 4.16a2 2 0 0 1-1.66 0L2 12.18"/><path d="m22 17.18-9.17 4.16a2 2 0 0 1-1.66 0L2 17.18"/>',
  'git-merge':  '<circle cx="18" cy="18" r="3"/><circle cx="6" cy="6" r="3"/><path d="M6 21V9a9 9 0 0 0 9 9"/>',
  'activity':   '<path d="M22 12h-4l-3 9L9 3l-3 9H2"/>',
  'shield':     '<path d="M20 13c0 5-3.5 7.5-7.66 8.95a1 1 0 0 1-.67-.01C7.5 20.5 4 18 4 13V6a1 1 0 0 1 1-1c2 0 4.5-1.2 6.24-2.72a1.17 1.17 0 0 1 1.52 0C14.51 3.81 17 5 19 5a1 1 0 0 1 1 1z"/><path d="m9 12 2 2 4-4"/>',
  'grid':       '<rect x="3" y="3" width="7" height="9" rx="1"/><rect x="14" y="3" width="7" height="5" rx="1"/><rect x="14" y="12" width="7" height="9" rx="1"/><rect x="3" y="16" width="7" height="5" rx="1"/>',
  'cloud':      '<path d="M17.5 19H9a7 7 0 1 1 6.71-9h1.79a4.5 4.5 0 1 1 0 9Z"/>',
  'trending':   '<polyline points="22 7 13.5 15.5 8.5 10.5 2 17"/><polyline points="16 7 22 7 22 13"/>',
  'chevron':    '<path d="m9 18 6-6-6-6"/>',
};

function svg(name, cls) {
  return `<svg width="18" height="18" class="${cls || ''}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${ICONS[name] || ''}</svg>`;
}

const pad2 = (n) => String(n).padStart(2, '0');

/* --------------------------------------------------------------------
   Références DOM
   -------------------------------------------------------------------- */
const els = {
  nav:            document.getElementById('chapter-nav'),
  content:        document.getElementById('content'),
  sidebar:        document.getElementById('sidebar'),
  backdrop:       document.getElementById('backdrop'),
  menuToggle:     document.getElementById('menu-toggle'),
  progressBar:    document.getElementById('reading-progress-bar'),
  chaptersCount:  document.getElementById('chapters-count'),
  chaptersFill:   document.getElementById('chapters-progress'),
  pager:          document.getElementById('pager'),
  pagerPrev:      document.getElementById('pager-prev'),
  pagerNext:      document.getElementById('pager-next'),
};

let currentIndex = -1;
const visited = new Set();

/* --------------------------------------------------------------------
   1. Construction de la sidebar
   -------------------------------------------------------------------- */
function buildSidebar() {
  els.nav.innerHTML = CHAPTERS.map((ch, i) => `
    <button class="nav-item" data-id="${ch.id}" data-index="${i}">
      <span class="nav-item__icon">${svg(ch.icon)}</span>
      <span class="nav-item__body">
        <span class="nav-item__num">CHAPITRE ${pad2(i + 1)}</span>
        <span class="nav-item__title">${ch.title}</span>
      </span>
    </button>
  `).join('');

  els.nav.querySelectorAll('.nav-item').forEach((btn) => {
    btn.addEventListener('click', () => {
      const id = btn.dataset.id;
      if (location.hash === '#' + id) loadChapter(id);
      else location.hash = id;      // déclenche hashchange -> loadChapter
      closeSidebar();
    });
  });
}

function markActive(index) {
  els.nav.querySelectorAll('.nav-item').forEach((btn) => {
    btn.classList.toggle('is-active', Number(btn.dataset.index) === index);
  });
}

/* --------------------------------------------------------------------
   2. Chargement d'un chapitre (fetch de /sections/{id}.html)
   -------------------------------------------------------------------- */
async function loadChapter(id) {
  const index = CHAPTERS.findIndex((c) => c.id === id);
  if (index === -1) return loadChapter(CHAPTERS[0].id);

  currentIndex = index;
  markActive(index);
  els.content.innerHTML = '<div class="loader"><div class="loader__spinner"></div><p>Chargement…</p></div>';

  try {
    const res = await fetch(`sections/${id}.html`, { cache: 'no-cache' });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const html = await res.text();
    els.content.innerHTML = html;

    afterLoad();
  } catch (err) {
    renderError(err);
  }

  visited.add(id);
  updateChaptersProgress();
  updatePager(index);
  window.scrollTo({ top: 0, behavior: 'auto' });
  updateReadingProgress();
  document.title = `${pad2(index + 1)}. ${CHAPTERS[index].title} — Module DevOps`;
}

/* Post-traitement après injection de contenu */
function afterLoad() {
  // Coloration syntaxique
  if (window.hljs) {
    els.content.querySelectorAll('pre code').forEach((block) => {
      try { window.hljs.highlightElement(block); } catch (e) { /* langage non chargé */ }
    });
  }
  initAccordions(els.content);
  initCopyButtons(els.content);
}

function renderError(err) {
  const isFileProtocol = location.protocol === 'file:';
  els.content.innerHTML = `
    <div class="fetch-error">
      <h2>Impossible de charger ce chapitre</h2>
      ${isFileProtocol
        ? `<p>Le chargement dynamique via <code>fetch()</code> est bloqué par le navigateur
             quand la page est ouverte en <code>file://</code>. Lance un petit serveur local :</p>
           <pre><code class="hljs">python -m http.server 5173
# puis ouvre  http://localhost:5173

# alternative Node :
npx serve .</code></pre>`
        : `<p>Détail technique : <code>${String(err.message || err)}</code></p>`}
    </div>`;
}

/* --------------------------------------------------------------------
   3. Accordéons (délégation, réutilisable pour tout le contenu)
   Markup attendu :
     <div class="accordion">
       <button class="accordion__trigger">Titre <svg .../></button>
       <div class="accordion__panel"><div class="accordion__panel-inner">…</div></div>
     </div>
   -------------------------------------------------------------------- */
function initAccordions(root) {
  root.querySelectorAll('.accordion__trigger').forEach((trigger) => {
    // injecte le chevron s'il n'est pas déjà présent
    if (!trigger.querySelector('.accordion__chevron')) {
      trigger.insertAdjacentHTML('beforeend', svg('chevron', 'accordion__chevron'));
    }
    trigger.addEventListener('click', () => {
      trigger.closest('.accordion').classList.toggle('is-open');
    });
  });
}

/* --------------------------------------------------------------------
   4. Boutons "copier" sur les blocs de code
   -------------------------------------------------------------------- */
function initCopyButtons(root) {
  root.querySelectorAll('.copy-btn').forEach((btn) => {
    btn.addEventListener('click', async () => {
      const pre = btn.closest('pre') || btn.closest('.code-block')?.querySelector('pre');
      const code = pre?.innerText || '';
      try {
        await navigator.clipboard.writeText(code);
        btn.classList.add('is-copied');
        const original = btn.textContent;
        btn.textContent = 'Copié ✓';
        setTimeout(() => { btn.classList.remove('is-copied'); btn.textContent = original; }, 1600);
      } catch (e) { /* clipboard indisponible */ }
    });
  });
}

/* --------------------------------------------------------------------
   5. Barre de progression de lecture (scroll de la page)
   -------------------------------------------------------------------- */
function updateReadingProgress() {
  const doc = document.documentElement;
  const scrollable = doc.scrollHeight - doc.clientHeight;
  const pct = scrollable > 0 ? (doc.scrollTop / scrollable) * 100 : 0;
  els.progressBar.style.width = `${Math.min(100, Math.max(0, pct))}%`;
}

/* Progression globale (chapitres visités) */
function updateChaptersProgress() {
  const done = visited.size;
  els.chaptersCount.textContent = `${done} / ${CHAPTERS.length}`;
  els.chaptersFill.style.width = `${(done / CHAPTERS.length) * 100}%`;
}

/* --------------------------------------------------------------------
   6. Pager précédent / suivant
   -------------------------------------------------------------------- */
function updatePager(index) {
  els.pager.hidden = false;
  const prev = CHAPTERS[index - 1];
  const next = CHAPTERS[index + 1];

  els.pagerPrev.disabled = !prev;
  els.pagerNext.disabled = !next;
  els.pagerPrev.querySelector('.pager__label').textContent = prev ? prev.title : '—';
  els.pagerNext.querySelector('.pager__label').textContent = next ? next.title : '—';

  els.pagerPrev.onclick = () => prev && (location.hash = prev.id);
  els.pagerNext.onclick = () => next && (location.hash = next.id);
}

/* --------------------------------------------------------------------
   7. Drawer mobile
   -------------------------------------------------------------------- */
function openSidebar() {
  els.sidebar.classList.add('is-open');
  els.backdrop.classList.add('is-visible');
  els.menuToggle.setAttribute('aria-expanded', 'true');
}
function closeSidebar() {
  els.sidebar.classList.remove('is-open');
  els.backdrop.classList.remove('is-visible');
  els.menuToggle.setAttribute('aria-expanded', 'false');
}
function toggleSidebar() {
  els.sidebar.classList.contains('is-open') ? closeSidebar() : openSidebar();
}

/* --------------------------------------------------------------------
   8. Thème clair / sombre
   -------------------------------------------------------------------- */
function currentTheme() {
  return document.documentElement.classList.contains('dark') ? 'dark' : 'light';
}

function applyTheme(theme) {
  const isDark = theme === 'dark';
  document.documentElement.classList.toggle('dark', isDark);
  document.documentElement.setAttribute('data-theme', theme);

  // Bascule le thème de coloration du code
  const darkCss = document.getElementById('hljs-dark');
  const lightCss = document.getElementById('hljs-light');
  if (darkCss) darkCss.disabled = !isDark;
  if (lightCss) lightCss.disabled = isDark;

  // Libellé accessible cohérent
  document.querySelectorAll('[data-theme-toggle]').forEach((btn) => {
    btn.setAttribute('aria-label', isDark ? 'Passer en thème clair' : 'Passer en thème sombre');
  });

  try { localStorage.setItem('devops-theme', theme); } catch (e) { /* stockage indisponible */ }
}

function toggleTheme() {
  applyTheme(currentTheme() === 'dark' ? 'light' : 'dark');
}

/* --------------------------------------------------------------------
   9. Routing par hash
   -------------------------------------------------------------------- */
function routeFromHash() {
  const id = location.hash.replace(/^#/, '') || CHAPTERS[0].id;
  loadChapter(id);
}

/* --------------------------------------------------------------------
   Init
   -------------------------------------------------------------------- */
/* Coloration HCL / Terraform (absente du bundle highlight.js commun) */
function registerTerraformLanguage() {
  if (!window.hljs || hljs.getLanguage('hcl')) return;
  hljs.registerLanguage('hcl', function (hl) {
    return {
      name: 'HCL',
      aliases: ['terraform', 'tf'],
      keywords: {
        keyword: 'resource provider variable output module data terraform locals backend for_each count depends_on source',
        literal: 'true false null',
      },
      contains: [
        hl.COMMENT('#', '$'),
        hl.COMMENT('//', '$'),
        hl.COMMENT('/\\*', '\\*/'),
        hl.NUMBER_MODE,
        {
          className: 'string',
          begin: '"', end: '"',
          contains: [{ className: 'subst', begin: '\\$\\{', end: '\\}' }],
        },
        { className: 'attr', begin: /[a-zA-Z_][\w-]*(?=\s*=)/ },
      ],
    };
  });
}

function init() {
  registerTerraformLanguage();
  buildSidebar();
  updateChaptersProgress();

  // Synchronise l'état visuel du thème (défini par le script anti-flash du <head>)
  applyTheme(currentTheme());
  document.querySelectorAll('[data-theme-toggle]').forEach((btn) => {
    btn.addEventListener('click', toggleTheme);
  });

  els.menuToggle.addEventListener('click', toggleSidebar);
  els.backdrop.addEventListener('click', closeSidebar);
  window.addEventListener('hashchange', routeFromHash);
  window.addEventListener('scroll', updateReadingProgress, { passive: true });
  window.addEventListener('resize', updateReadingProgress, { passive: true });

  // navigation clavier entre chapitres
  document.addEventListener('keydown', (e) => {
    if (e.target.matches('input, textarea')) return;
    if (e.key === 'ArrowRight' && currentIndex < CHAPTERS.length - 1) location.hash = CHAPTERS[currentIndex + 1].id;
    if (e.key === 'ArrowLeft'  && currentIndex > 0)                    location.hash = CHAPTERS[currentIndex - 1].id;
  });

  routeFromHash();
}

document.addEventListener('DOMContentLoaded', init);
