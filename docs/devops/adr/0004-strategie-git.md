# ADR-0004 — Stratégie Git : GitHub Flow, squash-merge, protection adaptée au solo

- **Date :** 30 juillet 2026
- **Statut :** ✅ Acté

## Contexte

Le chapitre 02 du cours décrit un dépôt d'équipe : deux approbations obligatoires, `CODEOWNERS`,
revue croisée. Ce dépôt a **un seul contributeur**. Appliquer le modèle tel quel rendrait tout merge
impossible — GitHub interdit d'approuver sa propre PR — et conduirait à désactiver la protection à
la main pour s'en sortir, c'est-à-dire à prendre l'habitude de contourner ses propres garde-fous.

Il faut donc transposer, et nommer ce qu'on perd en transposant.

## Décisions

### D1 — Stratégie de branches : GitHub Flow

`main` + branches courtes (`feat/*`, `fix/*`, `chore/*`) + PR. Pas de Git Flow.

Git Flow existe pour maintenir plusieurs versions en parallèle et figer des releases. Aucun des deux
ne s'applique ici. Ses branches `develop` et `release/*` de longue durée entreraient de plus en
conflit direct avec le modèle GitOps du palier 10B, où l'état désiré est défini par un unique
`main`. Le cours lui-même désigne GitHub Flow comme « excellent compromis pour démarrer ».

### D2 — Protection de `main` adaptée au solo

| Règle | Valeur | Motif |
|---|---|---|
| Require a pull request | ✅ | Tout changement passe par une PR, même seul |
| Required approvals | **0** | Impossible d'approuver sa propre PR |
| Require conversation resolution | ✅ | Gratuit, évite d'oublier une remarque |
| Require signed commits | ✅ | cf. D4 |
| Require linear history | ✅ | Cohérent avec le squash-merge |
| Do not allow bypassing (enforce admins) | ✅ | Une règle qu'on peut contourner n'est pas une règle |
| Allow force pushes / deletions | ❌ | — |
| Require status checks | **vide pour l'instant** | Aucune CI n'existe encore — à compléter au palier 7 |

**Ce qu'on perd, explicitement :** la revue humaine disparaît du circuit. La seule gate réelle sera
la CI. Cela déplace tout l'enjeu de la qualité vers le palier 7 : ce qui protégera `main`, ce sera
la couverture des tests, rien d'autre.

### D3 — Squash-merge exclusivement

Une PR = un commit sur `main`. Merge commits et rebase-merge désactivés.

Au-delà de la lisibilité, la raison opérationnelle est le rollback : si chaque commit de `main`
correspond exactement à une intention complète, `git revert <sha>` annule proprement une
fonctionnalité entière. C'est le mécanisme de retour arrière du palier 10B.

Conséquence pratique : en squash-merge, **le message qui atterrit sur `main` est le titre de la
PR**, pas les commits individuels. La convention Conventional Commits doit donc s'appliquer au
titre de PR en priorité.

### D4 — Commits signés en SSH

`gpg.format=ssh` avec la clé `id_perso` existante. Trois lignes de configuration, et le badge
*Verified* sur GitHub.

Sans signature, `user.name` et `user.email` sont du texte libre : n'importe qui peut produire un
commit à ton nom. La signature est la seule preuve d'authorat.

⚠️ Piège : sur GitHub, une clé d'**authentification** et une clé de **signature** sont deux entrées
distinctes, même pour la même clé. Il faut enregistrer `id_perso.pub` une seconde fois avec le type
`Signing Key`.

⚠️ À surveiller au palier 10B : `require signed commits` + `enforce_admins` s'appliquera aussi aux
commits automatiques. Sans effet ici, mais la CI ira committer dans le dépôt GitOps.

### D5 — Ordonnancement : réécriture avant protection

La réécriture d'historique (`git filter-repo`, palier 1) impose un `push --force`, que la protection
de branche interdit. Elle devait donc passer **avant** l'activation des règles. Fait le
30 juillet 2026.

## Conséquences

- Convention de nommage des branches : `<type>/<description-courte>` en minuscules avec tirets.
- Les messages suivent Conventional Commits : `feat`, `fix`, `docs`, `refactor`, `perf`, `test`,
  `build`, `ci`, `chore`. Le `!` ou un footer `BREAKING CHANGE:` signale une rupture.
- La validation automatique des messages est reportée au palier 7 : `commitlint` + `husky` est de
  l'outillage Node.js, absent de ce projet Java. On validera le **titre de PR** en CI, ce qui est
  de toute façon plus pertinent avec le squash-merge.
- Aucun contrôle automatique ne remplace la revue humaine perdue. C'est assumé, et documenté.
