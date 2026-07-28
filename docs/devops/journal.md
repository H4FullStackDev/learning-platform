# Journal de bord

Une entrée par session. Ce qu'on a fait, ce qui a cassé, ce qu'on en a compris.

---

## 28 juillet 2026 — Palier 0 : état des lieux

### Ce qu'on a fait
Cartographie du dépôt, lecture des 12 chapitres du cours, arbitrage de l'architecture cible et du
découpage en deux phases.

### Ce qu'on a trouvé

**Critique — secrets exposés.** Le fichier `.env` est versionné et poussé sur GitHub
(commit `3f8916f`). Il contient en clair : mot de passe PostgreSQL Neon, mot de passe SMTP Brevo,
secret JWT. Aggravant : le même secret JWT figure dans `application.properties:18`, donc
**identique en dev et en prod** — qui lit ce fichier peut forger un token admin valide.

**Bugs réels, pas du style :**

| Où | Problème | Effet |
|---|---|---|
| `Dockerfile:30` | `ENTRYPOINT` en forme exec → pas de shell → `${PORT}` jamais substitué | Spring reçoit littéralement la chaîne `${PORT}`. L'argument est de toute façon redondant : `application-prod.properties:1` fait déjà le travail. |
| `pom.xml` | `spring-boot-starter-actuator` absent alors que les probes sont configurées | `/actuator/health` renvoie 404 → probes en échec → `CrashLoopBackOff` |
| `db/migration/` | `V1` et `V2` ne font que des `INSERT`, aucun DDL nulle part ; prod en `ddl-auto=none` | Le schéma de prod a été créé par le `ddl-auto=update` du dev. **Il n'est pas reconstruisible.** |
| `application.properties:12` | `ddl-auto=update` **et** Flyway actif | Deux autorités sur le schéma, résultat dépendant de l'ordre d'exécution |
| `Dockerfile` | Conteneur root, `layers` activé dans le pom mais jamais exploité, pas de `HEALTHCHECK` | Surface d'attaque + rebuilds lents |
| `pom.xml:215-230` | Dépôt `spring-snapshots` dans un build de prod | Build non reproductible |
| `src/test/` | Un seul test (chargement de contexte) | Un pipeline CI n'aurait rien à valider |

### Décisions
- [ADR-0001](adr/0001-cloud-et-ci.md) — DigitalOcean + GitHub Actions + PG managée + DOCR
- [ADR-0002](adr/0002-droplet-avant-kubernetes.md) — phase A sur Droplet, phase B sur DOKS
- [ADR-0003](adr/0003-plan-de-domaines.md) — sous-domaines séparés *(proposé)*

### Ce que j'ai compris
*(à compléter par toi — c'est la partie qui compte)*

---

## Palier 1 — Révocation des secrets (en cours)

### T1 — le circuit d'un secret

```
   NAISSANCE          STOCKAGE              TRANSPORT           USAGE              MORT
   ─────────          ────────              ─────────           ─────              ────
   généré par    ──►  coffre        ──►  injecté au      ──►  en RAM du     ──►  révoqué
   le fournisseur     (jamais git)        démarrage           processus          + remplacé
   (DO, Brevo…)         │                     │                   │                  │
              GitHub Secrets           variable d'env      jamais loggé      rotation régulière
              K8s Secret               ou fichier monté    jamais en 404      ou après fuite
              Vault (ch.09)            en lecture seule    jamais en réponse
```

**La règle unique dont tout découle :** un secret ne doit exister qu'en **deux** endroits — le
coffre qui le stocke, et la RAM du processus qui l'utilise. Tout point intermédiaire (git, un
ticket, une capture d'écran, un `.env` sur ton disque, les variables d'env d'une console de
déploiement) est une **copie non révocable**.

### Le point à retenir

> **Supprimer un secret de git ne le révoque pas.**

`git rm .env` retire le fichier du prochain commit. Il ne retire rien de l'historique, rien des
clones existants, rien de ce que GitHub a déjà servi. La seule opération qui a un effet réel sur le
monde, c'est la **révocation chez le fournisseur** : tu changes le mot de passe, et l'ancien devient
une chaîne de caractères sans pouvoir. Le nettoyage du dépôt vient après — c'est du confort, pas de
la sécurité.

Corollaire : même une réécriture d'historique ne garantit pas l'effacement. GitHub conserve les
objets orphelins et continue de servir un ancien commit par son SHA pendant un temps indéterminé
après un `push --force`. Ce qui te protège n'est pas le nettoyage, c'est la rotation.

### Ordre des opérations

```
   1. RÉVOQUER      chez chaque fournisseur
   2. SUPPRIMER     les plateformes qui détiennent des copies (Koyeb)
   3. VÉRIFIER      que l'ancienne valeur est refusée        ← la preuve (T4)
   4. NETTOYER      le dépôt (.gitignore, .env.example)
   5. DÉCIDER       réécriture de l'historique ou non
```

### T3 — état des actions

| Secret | Statut |
|---|---|
| Mot de passe PostgreSQL Neon | ✅ **Révoqué** — compte supprimé. La forme de révocation la plus complète : le serveur n'existe plus. |
| Mot de passe SMTP Brevo | 🔴 **Toujours actif** — permet d'envoyer des mails sous ta réputation d'expéditeur |
| Secret JWT | 🔴 **Toujours actif** — permet de forger un token admin valide |
| App Koyeb | ⬜ À vérifier et supprimer — elle détient une copie des trois secrets |

Commande pour le nouveau secret JWT :
```bash
openssl rand -base64 64 | tr -d '\n'
```
`rand -base64 64` produit 64 octets d'entropie cryptographique. Le secret actuel fait 128 caractères
mais est alphanumérique en minuscules — environ 5,2 bits par caractère au lieu de 6, et surtout on
ignore comment il a été généré. Ce qui compte pour un secret n'est pas sa longueur : c'est
**l'imprévisibilité de sa source**.

⚠️ Effet de bord à connaître : régénérer le secret JWT **invalide tous les tokens émis**. Tous les
utilisateurs connectés sont déconnectés. Ici c'est sans conséquence, mais en production réelle ça se
planifie — c'est le genre de détail qui transforme une rotation de secret en incident.

### T2 — décision en attente

Que fait-on de l'historique git ?

| Option | Coût | Bénéfice |
|---|---|---|
| **A** — rotation seule | zéro | Les valeurs du commit `3f8916f` deviennent des chaînes mortes. L'historique garde la trace de l'erreur. |
| **B** — rotation + `git filter-repo` + `push --force` | Tous les SHA changent. Nul ici : dépôt solo, une branche, aucun collaborateur. | Le fichier disparaît de l'historique. Ce dépôt est sous l'organisation qui héberge la plateforme de cours — un `.env` visible fait mauvais effet. |

**Recommandation :** les deux, séparés dans le temps. Rotation aujourd'hui (urgent, suffit à te
mettre en sécurité), réécriture au palier 2 quand on reprendra toute la configuration Git.

### T4 — preuve de sortie
- [ ] L'ancien mot de passe SMTP est refusé par Brevo
- [ ] Un token signé avec l'ancien secret JWT est rejeté par l'API
- [ ] `git ls-files` ne renvoie plus `.env`
- [ ] `.env.example` documente toutes les variables, sans aucune valeur
