/* =====================================================================
   H4Techno Formations — catalog.js
   Source unique de vérité : la liste des MODULES et de leurs chapitres.
   Partagé par l'accueil (index.html) et le lecteur (app.html / app.js).
   Ajouter une formation = une entrée dans MODULES + un dossier modules/<id>/
   ===================================================================== */

'use strict';

/* Icônes SVG partagées (style Lucide, trait fin) */
const ICONS = {
  // Icônes de modules
  'infinity':   '<path d="M12 12c-2-2.67-4-4-6-4a4 4 0 1 0 0 8c2 0 4-1.33 6-4Zm0 0c2 2.67 4 4 6 4a4 4 0 0 0 0-8c-2 0-4 1.33-6 4Z"/>',
  'network':    '<circle cx="12" cy="5" r="2.5"/><circle cx="5" cy="19" r="2.5"/><circle cx="19" cy="19" r="2.5"/><path d="M12 7.5 6.5 16.8"/><path d="m12 7.5 5.5 9.3"/>',
  'heart-pulse':'<path d="M19 14c1.49-1.46 3-3.21 3-5.5A5.5 5.5 0 0 0 16.5 3c-1.76 0-3 .5-4.5 2-1.5-1.5-2.74-2-4.5-2A5.5 5.5 0 0 0 2 8.5c0 2.29 1.51 4.04 3 5.5l7 7Z"/><path d="M3.22 12H9.5l.5-1 2 4.5 2-7 1.5 3.5h5.27"/>',
  // Icônes de chapitres
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
  'compass':    '<circle cx="12" cy="12" r="10"/><polygon points="16.24 7.76 14.12 14.12 7.76 16.24 9.88 9.88 16.24 7.76"/>',
  'globe':      '<circle cx="12" cy="12" r="10"/><path d="M2 12h20"/><path d="M12 2a15.3 15.3 0 0 1 4 10 15.3 15.3 0 0 1-4 10 15.3 15.3 0 0 1-4-10 15.3 15.3 0 0 1 4-10z"/>',
  'server':     '<rect x="2" y="2" width="20" height="8" rx="2"/><rect x="2" y="14" width="20" height="8" rx="2"/><line x1="6" y1="6" x2="6.01" y2="6"/><line x1="6" y1="18" x2="6.01" y2="18"/>',
  'database':   '<ellipse cx="12" cy="5" rx="9" ry="3"/><path d="M3 5v14a9 3 0 0 0 18 0V5"/><path d="M3 12a9 3 0 0 0 18 0"/>',
  'zap':        '<polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/>',
  'inbox':      '<polyline points="22 12 16 12 14 15 10 15 8 12 2 12"/><path d="M5.45 5.11 2 12v6a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-6l-3.45-6.89A2 2 0 0 0 16.76 4H7.24a2 2 0 0 0-1.79 1.11z"/>',
  'share':      '<circle cx="18" cy="5" r="3"/><circle cx="6" cy="12" r="3"/><circle cx="18" cy="19" r="3"/><line x1="8.59" y1="13.51" x2="15.42" y2="17.49"/><line x1="15.41" y1="6.51" x2="8.59" y2="10.49"/>',
  'scale':      '<path d="m16 16 3-8 3 8c-.87.65-1.92 1-3 1s-2.13-.35-3-1Z"/><path d="m2 16 3-8 3 8c-.87.65-1.92 1-3 1s-2.13-.35-3-1Z"/><path d="M7 21h10"/><path d="M12 3v18"/><path d="M3 7h2c2 0 5-1 7-2 2 1 5 2 7 2h2"/>',
  'component':  '<path d="m12 2 3.5 3.5L12 9 8.5 5.5z"/><path d="M5.5 8.5 9 12l-3.5 3.5L2 12z"/><path d="M18.5 8.5 22 12l-3.5 3.5L15 12z"/><path d="m12 15 3.5 3.5L12 22l-3.5-3.5z"/>',
  'lightbulb':  '<path d="M15 14c.2-1 .7-1.7 1.5-2.5 1-.9 1.5-2.2 1.5-3.5A6 6 0 0 0 6 8c0 1 .2 2.2 1.5 3.5.7.7 1.3 1.5 1.5 2.5"/><path d="M9 18h6"/><path d="M10 22h4"/>',
  'target':     '<circle cx="12" cy="12" r="10"/><circle cx="12" cy="12" r="6"/><circle cx="12" cy="12" r="2"/>',
  'gauge':      '<path d="m12 14 4-4"/><path d="M3.34 19a10 10 0 1 1 17.32 0"/>',
  'bell':       '<path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9"/><path d="M10.3 21a1.94 1.94 0 0 0 3.4 0"/>',
  'alert':      '<path d="m21.73 18-8-14a2 2 0 0 0-3.48 0l-8 14A2 2 0 0 0 4 21h16a2 2 0 0 0 1.73-3Z"/><path d="M12 9v4"/><path d="M12 17h.01"/>',
  'clipboard':  '<rect x="8" y="2" width="8" height="4" rx="1"/><path d="M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2"/><path d="M9 12h6"/><path d="M9 16h6"/>',
  'terminal':   '<polyline points="4 17 10 11 4 5"/><line x1="12" y1="19" x2="20" y2="19"/>',
  'code':       '<polyline points="16 18 22 12 16 6"/><polyline points="8 6 2 12 8 18"/>',
  'check-circle':'<path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><path d="m9 11 3 3L22 4"/>',
  'chevron':    '<path d="m9 18 6-6-6-6"/>',
};

function iconSvg(name, cls, size) {
  size = size || 18;
  return `<svg width="${size}" height="${size}" class="${cls || ''}" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round">${ICONS[name] || ''}</svg>`;
}

/* --------------------------------------------------------------------
   Le catalogue des formations.
   status : 'available' (cliquable) | 'soon' (à venir)
   -------------------------------------------------------------------- */
const MODULES = [
  {
    id: 'fondamentaux',
    title: 'Fondamentaux Tech',
    subtitle: 'Terminal, Git, Python, web, bases de données — le socle pour tout ce qui suit.',
    level: 'Débutant → Intermédiaire',
    accent: '#f59e0b',
    icon: 'terminal',
    path: 'modules/fondamentaux',
    status: 'available',
    chapters: [
      { id: 'terminal',       title: 'Le terminal & Linux',             icon: 'terminal',     level: 'debutant' },
      { id: 'git',            title: 'Git & le versioning',             icon: 'git-branch',   level: 'debutant' },
      { id: 'python',         title: 'Premier langage : Python',        icon: 'code',         level: 'debutant' },
      { id: 'web',            title: 'Comment fonctionne le web',       icon: 'globe',        level: 'debutant' },
      { id: 'databases',      title: 'Bases de données',                icon: 'database',     level: 'debutant' },
      { id: 'mini-api',       title: 'Projet fil rouge : une mini-API', icon: 'server',       level: 'intermediaire' },
      { id: 'network',        title: 'Réseau pour développeurs',        icon: 'share',        level: 'intermediaire' },
      { id: 'environnements', title: 'Environnements & dépendances',    icon: 'box',          level: 'intermediaire' },
      { id: 'tests',          title: 'Tests & qualité de code',         icon: 'check-circle', level: 'intermediaire' },
      { id: 'vers-devops',    title: 'Passerelle vers le DevOps',       icon: 'infinity',     level: 'intermediaire' },
    ],
  },
  {
    id: 'devops',
    title: 'DevOps',
    subtitle: 'De la culture à la mise en production sur un cloud réel.',
    level: 'Intermédiaire → Avancé',
    accent: '#7c6bf9',
    icon: 'infinity',
    path: 'modules/devops',
    prereq: 'fondamentaux',
    status: 'available',
    chapters: [
      { id: 'intro',        title: 'Introduction au DevOps',                  icon: 'rocket' },
      { id: 'git',          title: 'Versioning & Collaboration (Git)',        icon: 'git-branch' },
      { id: 'ci',           title: 'Intégration Continue (CI)',               icon: 'refresh' },
      { id: 'docker',       title: 'Conteneurisation (Docker)',               icon: 'box' },
      { id: 'kubernetes',   title: 'Orchestration (Kubernetes)',              icon: 'wheel' },
      { id: 'terraform',    title: 'Infrastructure as Code (Terraform)',      icon: 'layers' },
      { id: 'gitops',       title: 'GitOps & Déploiement Continu (CD)',       icon: 'git-merge' },
      { id: 'monitoring',   title: 'Monitoring & Observabilité',              icon: 'activity' },
      { id: 'security',     title: 'Sécurité DevSecOps',                      icon: 'shield' },
      { id: 'architecture', title: 'Architecture de déploiement complète',    icon: 'grid' },
      { id: 'digitalocean', title: 'Pratique : déploiement sur DigitalOcean', icon: 'cloud' },
      { id: 'scaling',      title: 'Scalabilité & Montée en charge',          icon: 'trending' },
    ],
  },
  {
    id: 'system-design',
    title: 'System Design',
    subtitle: 'Concevoir des systèmes distribués qui tiennent la charge.',
    level: 'Débutant → Avancé',
    accent: '#38bdf8',
    icon: 'network',
    path: 'modules/system-design',
    status: 'available',
    chapters: [
      { id: 'intro',         title: 'Introduction au System Design',         icon: 'compass',   level: 'debutant' },
      { id: 'web-basics',    title: 'Les briques du web',                    icon: 'globe',     level: 'debutant' },
      { id: 'scaling',       title: 'Scalabilité & performance : les bases', icon: 'server',    level: 'debutant' },
      { id: 'databases',     title: 'Bases de données en profondeur',        icon: 'database',  level: 'intermediaire' },
      { id: 'caching',       title: 'Caching',                               icon: 'zap',       level: 'intermediaire' },
      { id: 'messaging',     title: 'Files & traitement asynchrone',         icon: 'inbox',     level: 'intermediaire' },
      { id: 'communication', title: 'Communication entre services',          icon: 'share',     level: 'intermediaire' },
      { id: 'consistency',   title: 'Cohérence & théorème CAP',              icon: 'scale',     level: 'avance' },
      { id: 'patterns',      title: "Patterns d'architecture",               icon: 'component', level: 'avance' },
      { id: 'case-studies',  title: 'Études de cas',                         icon: 'lightbulb', level: 'avance' },
    ],
  },
  {
    id: 'sre',
    title: 'SRE — Site Reliability Engineering',
    subtitle: 'Fiabilité, incidents, error budgets, chaos engineering.',
    level: 'Intermédiaire → Avancé',
    accent: '#10b981',
    icon: 'heart-pulse',
    path: 'modules/sre',
    status: 'available',
    chapters: [
      { id: 'intro',        title: 'Introduction au SRE',                   icon: 'compass',   level: 'debutant' },
      { id: 'slo',          title: 'SLI / SLO / SLA',                       icon: 'target',    level: 'intermediaire' },
      { id: 'error-budget', title: 'Error budgets',                         icon: 'gauge',     level: 'intermediaire' },
      { id: 'monitoring',   title: 'Monitoring & 4 golden signals',         icon: 'activity',  level: 'intermediaire' },
      { id: 'alerting',     title: 'Alerting & astreinte (on-call)',        icon: 'bell',      level: 'intermediaire' },
      { id: 'incidents',    title: 'Gestion des incidents',                 icon: 'alert',     level: 'avance' },
      { id: 'postmortems',  title: 'Postmortems blameless',                 icon: 'clipboard', level: 'avance' },
      { id: 'toil',         title: 'Toil & automatisation',                 icon: 'refresh',   level: 'avance' },
      { id: 'chaos',        title: 'Résilience & Chaos Engineering',        icon: 'zap',       level: 'avance' },
      { id: 'capacity',     title: 'Capacity planning & déploiements sûrs', icon: 'rocket',    level: 'avance' },
    ],
  },
];

function getModule(id) {
  return MODULES.find((m) => m.id === id) || MODULES[0];
}
