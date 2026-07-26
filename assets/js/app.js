/* =====================================================================
   Module DevOps — app.js
   Navigation par hash, chargement des sections (fetch), accordéons,
   barre de progression de lecture, drawer mobile, pager préc./suiv.
   ===================================================================== */

'use strict';

/* --------------------------------------------------------------------
   Module courant : déterminé par ?m=<id> dans l'URL.
   Le catalogue (catalog.js) fournit MODULES, getModule, ICONS, iconSvg.
   -------------------------------------------------------------------- */
const MODULE_ID = new URLSearchParams(location.search).get('m') || MODULES[0].id;
const MODULE = getModule(MODULE_ID);
const CHAPTERS = MODULE.chapters;
const SECTION_BASE = MODULE.path;

/* Le helper d'icônes vit dans catalog.js (partagé avec l'accueil). */
const svg = (name, cls) => iconSvg(name, cls);
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

/* Progression persistée par module (localStorage) */
const DONE_KEY = 'h4-done-' + MODULE.id;
function loadDone() {
  try { return new Set(JSON.parse(localStorage.getItem(DONE_KEY) || '[]')); }
  catch (e) { return new Set(); }
}
const done = loadDone();
function saveDone() {
  try { localStorage.setItem(DONE_KEY, JSON.stringify([...done])); } catch (e) { /* ignore */ }
}
function markDone(id) {
  if (!id || done.has(id)) return;
  done.add(id);
  saveDone();
  updateChaptersProgress();
  markSidebarDone();
}

/* --------------------------------------------------------------------
   1. Construction de la sidebar
   -------------------------------------------------------------------- */
const LEVEL_LABEL = { debutant: 'Débutant', intermediaire: 'Intermédiaire', avance: 'Avancé' };

function buildSidebar() {
  els.nav.innerHTML = CHAPTERS.map((ch, i) => `
    <button class="nav-item" data-id="${ch.id}" data-index="${i}">
      <span class="nav-item__icon">${svg(ch.icon)}</span>
      <span class="nav-item__body">
        <span class="nav-item__num">CHAPITRE ${pad2(i + 1)}${ch.level ? `<span class="lvl-dot lvl-${ch.level}" title="${LEVEL_LABEL[ch.level]}"></span>` : ''}</span>
        <span class="nav-item__title">${ch.title}</span>
      </span>
      <span class="nav-item__check" aria-hidden="true" title="Terminé">✓</span>
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

  markSidebarDone();
}

/* Coche les chapitres terminés dans la sidebar */
function markSidebarDone() {
  els.nav.querySelectorAll('.nav-item').forEach((btn) => {
    btn.classList.toggle('is-done', done.has(btn.dataset.id));
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
    const res = await fetch(`${SECTION_BASE}/${id}.html`, { cache: 'no-cache' });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const html = await res.text();
    els.content.innerHTML = html;

    afterLoad();
  } catch (err) {
    renderError(err);
  }

  updateChaptersProgress();
  updatePager(index);
  window.scrollTo({ top: 0, behavior: 'auto' });
  updateReadingProgress();
  document.title = `${pad2(index + 1)}. ${CHAPTERS[index].title} — ${MODULE.title} · H4Techno Formations`;
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
  // Contenu plus court que l'écran -> considéré comme lu
  const pct = scrollable > 0 ? (doc.scrollTop / scrollable) * 100 : 100;
  els.progressBar.style.width = `${Math.min(100, Math.max(0, pct))}%`;
  // Chapitre lu à 90 % -> marqué terminé
  if (pct >= 90 && currentIndex >= 0) markDone(CHAPTERS[currentIndex].id);
}

/* Progression globale (chapitres terminés, persistée) */
function updateChaptersProgress() {
  const n = done.size;
  els.chaptersCount.textContent = `${n} / ${CHAPTERS.length}`;
  els.chaptersFill.style.width = `${(n / CHAPTERS.length) * 100}%`;
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

/* Affiche le nom du module courant dans le shell */
function setModuleBranding() {
  const t = document.getElementById('module-title');
  if (t) t.textContent = MODULE.title;
  const mt = document.getElementById('mobile-title');
  if (mt) mt.textContent = MODULE.title;
  const cnt = document.getElementById('sidebar-count');
  if (cnt) cnt.textContent = `${CHAPTERS.length} chapitres · ${MODULE.level}`;
  const pr = document.getElementById('module-prereq');
  if (pr) {
    if (MODULE.prereq) {
      const pm = getModule(MODULE.prereq);
      pr.innerHTML = `Prérequis recommandé&nbsp;: <a href="app.html?m=${pm.id}">${pm.title}</a>`;
      pr.hidden = false;
    } else {
      pr.hidden = true;
    }
  }
}

/* Synchronise le thème + branche les boutons de bascule */
function initTheme() {
  applyTheme(currentTheme());
  document.querySelectorAll('[data-theme-toggle]').forEach((btn) => {
    btn.addEventListener('click', toggleTheme);
  });
}

function init() {
  setModuleBranding();
  initTheme();

  // Module encore vide (formation à venir) : message et on s'arrête là.
  if (!CHAPTERS.length) {
    els.content.innerHTML = '<div class="fetch-error"><h2>Formation bientôt disponible</h2>'
      + '<p>Ce module est en préparation. <a href="index.html">← Retour aux formations</a></p></div>';
    if (els.pager) els.pager.hidden = true;
    els.menuToggle.addEventListener('click', toggleSidebar);
    els.backdrop.addEventListener('click', closeSidebar);
    return;
  }

  registerTerraformLanguage();
  buildSidebar();
  updateChaptersProgress();

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
