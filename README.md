# H4Techno Formations

Plateforme de formation tech — **statique, sans build**, pensée pour la pratique.
Schémas clairs, code réel, du débutant à l'avancé.

**3 formations · 32 chapitres**

| # | Formation | Angle | Chapitres |
|---|-----------|-------|-----------|
| 01 | **DevOps** | Livrer & opérer | 12 |
| 02 | **System Design** | Concevoir | 10 |
| 03 | **SRE** | Fiabiliser | 10 |

---

## Lancer en local

Le site charge les chapitres via `fetch()` : il faut un **petit serveur HTTP** (l'ouvrir en `file://` bloque les requêtes).

```bash
python -m http.server 5173
# puis ouvrir http://localhost:5173

# alternative Node :
npx serve .
```

> Après avoir modifié `style.css` / `app.js` / `catalog.js`, fais un rechargement forcé
> (`Ctrl+Shift+R`) ou incrémente le `?v=` dans les balises de `index.html` et `app.html`.

---

## Structure du projet

```
.
├── index.html            → Accueil : galerie des formations
├── app.html              → Lecteur de module (sidebar + chapitres)
├── robots.txt
├── assets/
│   ├── css/style.css     → design system complet (thème clair/sombre, ~30 composants)
│   ├── js/catalog.js     → LE catalogue : modules + chapitres + couleurs (source de vérité)
│   ├── js/app.js         → lecteur : routing, accordéons, progression, thème
│   ├── favicon.svg       → logo H4Techno
│   └── og.svg            → image de partage (source ; voir « Avant de déployer »)
└── modules/
    ├── devops/*.html         (12 chapitres)
    ├── system-design/*.html  (10 chapitres)
    └── sre/*.html            (10 chapitres)
```

**Routage** : `app.html?m=<module>#<chapitre>` (ex. `app.html?m=devops#docker`).
Ce sont de vrais fichiers + query/hash → **aucune règle de rewrite SPA nécessaire**, les liens profonds fonctionnent partout.

---

## Déployer (100 % statique, zéro build)

N'importe quel hébergeur de fichiers statiques convient. On publie **le dossier tel quel**.

- **Netlify** — glisser-déposer le dossier sur app.netlify.com, ou `netlify deploy --prod`.
- **Vercel** — `vercel` (framework: *Other*, pas de build command).
- **Cloudflare Pages / GitHub Pages** — pointer sur le dépôt, build command *vide*, output = racine.
- **DigitalOcean App Platform** — composant *Static Site*, pas de build.

Aucune étape de compilation : pas de bundler, pas de `node_modules`.

### Avant de déployer — checklist

1. **Générer l'image de partage** (PNG 1200×630 à partir de la source SVG) :
   ```bash
   npx svgexport assets/og.svg assets/og.png 1200:630
   # ou : ouvrir assets/og.svg dans un navigateur → « Enregistrer en PNG »
   # ou : un convertisseur en ligne (cloudconvert, etc.)
   ```
2. **Passer les URL Open Graph en absolu** dans `index.html` et `app.html`
   (`og:image` et un `og:url` avec ton domaine), sinon les aperçus de partage
   (LinkedIn, Slack, X…) n'affichent pas l'image.
3. *(optionnel)* Ajouter un `favicon.ico` 32×32 à la racine pour les très vieux
   navigateurs / bots — le `favicon.svg` suffit aux navigateurs modernes.

---

## Ajouter du contenu

Tout est piloté par **`assets/js/catalog.js`**.

**Ajouter un chapitre** à un module existant :
1. Créer `modules/<module>/<id>.html` (partir d'un chapitre existant comme gabarit).
2. Ajouter une entrée dans le tableau `chapters` du module :
   ```js
   { id: 'mon-chapitre', title: 'Mon titre', icon: 'rocket', level: 'debutant' }
   ```
   (`level` optionnel : `debutant` | `intermediaire` | `avance` → pastille dans la sidebar).

**Ajouter une formation** :
1. Créer le dossier `modules/<mon-module>/`.
2. Ajouter un objet dans `MODULES` (`id`, `title`, `subtitle`, `level`, `accent`,
   `icon`, `path`, `status: 'available'`, `chapters: [...]`).
   Elle apparaît automatiquement sur l'accueil et devient navigable.

---

## Design system

Le fichier `assets/css/style.css` contient toutes les briques réutilisables
(callouts, diagrammes `arch`, blocs de code, tableaux, cartes, diagrammes de
séquence, dashboard, etc.) et les variables de thème (clair/sombre). Écrire un
chapitre = assembler ces classes en HTML, **sans toucher au CSS**.
