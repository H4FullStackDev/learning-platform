# CLAUDE.md — contexte projet & état d'avancement

> Fichier de synchronisation entre machines. **À maintenir à jour à chaque fin de session.**
> Lecture obligatoire par Claude au démarrage de toute session sur ce dépôt.

---

## 1. Ce qu'est ce projet

API Spring Boot 3.4.4 (Java 17, PostgreSQL, Flyway, JWT, WebSocket, mail) de gestion de
réquisitions. **L'application n'est pas utilisée en production réelle** : elle sert de support à un
parcours d'apprentissage DevOps appliqué.

**Objectif réel : maîtriser le flux DevOps de bout en bout**, pas livrer un déploiement.
Le déploiement est le prétexte ; la compréhension est le livrable.

Cours de référence de l'utilisateur (12 chapitres HTML) :
`C:\Users\KERNEL\Documents\Codex\learning-platform\sections`

| # | Chapitre | # | Chapitre |
|---|---|---|---|
| 01 | intro | 07 | gitops |
| 02 | git | 08 | monitoring |
| 03 | ci | 09 | security |
| 04 | docker | 10 | architecture |
| 05 | kubernetes | 11 | digitalocean (pratique) |
| 06 | terraform | 12 | scaling (bonus) |

---

## 2. Méthode de travail — NON NÉGOCIABLE

L'utilisateur a explicitement demandé à **ne pas** recevoir du code prêt à l'emploi.
Chaque palier se déroule en 4 temps, dans cet ordre :

| Temps | Contenu | Règle |
|---|---|---|
| **T1** | Schéma & circuit | Aucun code écrit. On dessine le flux jusqu'à ce qu'il soit clair. |
| **T2** | Décision | 2-3 options avec coûts réels. **C'est lui qui tranche.** On acte en ADR. |
| **T3** | Exécution | Par défaut : *il tape, Claude explique*. Écrire des fichiers uniquement sur demande explicite. |
| **T4** | Preuve & rollback | Preuve observable + savoir annuler + exercice « casse-le » + questions de contrôle. |

**Interdits :**
- Écrire du code de déploiement avant que T1 et T2 soient validés.
- Introduire Kubernetes ou ArgoCD avant la fin complète de la phase A.
- Masquer un écart entre le cours et la réalité du projet — il faut le nommer.

**Documentation à tenir :** [docs/devops/journal.md](docs/devops/journal.md) et
[docs/devops/adr/](docs/devops/adr/) (une décision = un fichier).

---

## 3. Contraintes

| Contrainte | Valeur |
|---|---|
| Cloud | DigitalOcean — crédit 200 $ |
| **Expiration du crédit** | **12 septembre 2026 — aucun renouvellement prévu** |
| CI | GitHub Actions |
| Base de données | PostgreSQL managée DigitalOcean |
| Registry | DigitalOcean Container Registry (DOCR) |
| Jalon « première prod » | **vendredi 7 août 2026** |
| Domaine | à acheter (option sous-domaines retenue, cf. ADR-0003) |
| Frontend | hébergé sur DigitalOcean aussi, **après** le backend, volontairement séparé |

**Facturation horaire :** `terraform destroy` en fin de session, `terraform apply` au début de la
suivante. Le cluster à ~80 $/mois ne coûte que ~0,11 $/h. Ne jamais laisser tourner inutilement.
La base PostgreSQL, elle, reste allumée (stateful).

**Au 12 septembre : tout détruire.** `terraform destroy` final, y compris la base.

---

## 4. Décisions actées

| ADR | Décision | Statut |
|---|---|---|
| [0001](docs/devops/adr/0001-cloud-et-ci.md) | DigitalOcean + GitHub Actions + PG managée + DOCR | ✅ acté |
| [0002](docs/devops/adr/0002-droplet-avant-kubernetes.md) | Phase A sur Droplet, puis phase B sur DOKS | ✅ acté |
| [0003](docs/devops/adr/0003-plan-de-domaines.md) | Sous-domaines séparés `api.` / `app.` | 🟡 proposé |
| [0004](docs/devops/adr/0004-strategie-git.md) | GitHub Flow, squash-merge, protection adaptée solo, commits signés | ✅ acté |

---

## 5. État d'avancement

**Palier en cours : 2 — Git** (T3 : protection de branche et signature)

| # | Palier | Chapitre | Phase | Statut |
|---|---|---|---|---|
| 0 | État des lieux & architecture cible | 01, 10 | — | ✅ |
| 1 | Révocation des secrets & hygiène du dépôt | 09·D | — | ✅ |
| 2 | Git : branches, PR, protection, commits conventionnels | 02 | — | 🔄 T3 en cours |
| 3 | App déployable : 12-factor, actuator, probes | 10 | — | ⬜ |
| 4 | Docker : multi-stage, layered jar, non-root | 04 | — | ⬜ |
| 5 | Compose local : API + Postgres + SMTP factice | 04 | — | ⬜ |
| 6 | Tests : Testcontainers | 03 | — | ⬜ |
| 7 | CI : test → build → Trivy → push DOCR | 03, 09·E | — | ⬜ |
| 8A | Terraform : Droplet, PG, DOCR, firewall, DNS | 06 | A | ⬜ |
| 9A | Déploiement Compose distant, migrations, TLS, rollback | 04 | A | ⬜ |
| 10A | Observabilité : Prometheus + Grafana sur Droplet | 08 | A | ⬜ |
| 8B | Terraform : ajout DOKS + Load Balancer | 06, 11·B | B | ⬜ |
| 9B | Kubernetes : Helm, probes, resources, Job Flyway | 05 | B | ⬜ |
| 10B | GitOps : ArgoCD, repo de config, rollback `git revert` | 07, 11·C/D | B | ⬜ |
| 11B | Observabilité K8s : kube-prometheus-stack, alertes, SLO | 08 | B | ⬜ |
| 12 | Scaling & durcissement : HPA, rate limiting, checklist | 12, 09 | B | ⬜ |
| 13 | Frontend | — | A puis B | ⬜ |

### Palier 1 — terminé le 30 juillet 2026
- [x] Base Neon supprimée (compte clos) → credential révoqué
- [x] Clé SMTP Brevo régénérée
- [x] Secret JWT régénéré, stocké hors du dépôt (à câbler au palier 3)
- [x] Aucune app Koyeb résiduelle
- [x] `.env` retiré du suivi git, `.gitignore` durci, `.env.example` créé
- [x] Historique réécrit (`git filter-repo`) et force-push — **tous les SHA du dépôt ont changé**

### Palier 2 — reste à faire
- [x] Réécriture de l'historique (faite avant la protection, l'ordre était impératif)
- [ ] Signature SSH des commits (clé à enregistrer **une 2ᵉ fois** sur GitHub en `Signing Key`)
- [ ] Protection de `main` : PR obligatoire, 0 approbation, historique linéaire, pas de force-push
- [ ] Merge en squash uniquement, suppression auto des branches fusionnées
- [ ] PR de test prouvant que le push direct sur `main` est refusé
- [ ] `required_status_checks` : **à compléter au palier 7**, quand la CI existera

---

## 6. Dettes connues du projet (à traiter à leur palier)

| Fichier | Problème | Palier |
|---|---|---|
| ~~`.env`~~ | ~~Versionné avec secrets vivants~~ — **résolu au palier 1** : secrets révoqués, fichier retiré, historique réécrit | ✅ |
| [application.properties:18](src/main/resources/application.properties#L18) | Secret JWT en dur, **identique à la prod** | 3 |
| [application.properties:12](src/main/resources/application.properties#L12) | `ddl-auto=update` + Flyway actif = deux autorités sur le schéma | 3 |
| [pom.xml](pom.xml) | `spring-boot-starter-actuator` absent alors que les probes sont configurées → 404 | 3 |
| [pom.xml:215-230](pom.xml#L215-L230) | Dépôt `spring-snapshots` dans un build de prod = non reproductible | 3 |
| `db/migration/` | Aucun DDL, uniquement des `INSERT` → schéma non reconstruisible | 3 puis 9 |
| [Dockerfile:30](Dockerfile#L30) | `ENTRYPOINT` exec : `${PORT}` jamais substitué (pas de shell) | 4 |
| [Dockerfile](Dockerfile) | Conteneur root, `layers` activé mais inexploité, pas de `HEALTHCHECK` | 4 |
| `src/test/` | Un seul test (chargement de contexte) → rien à valider en CI | 6 |

---

## 7. Écarts entre le cours et ce projet

Le chapitre 11 prend un backend Node.js. Trois sujets ne sont donc **pas** couverts et devront être
conçus, pas copiés :

1. **Migrations Flyway en Kubernetes** — N pods = N Flyway en concurrence. Solution : Job Helm
   `pre-sync`, jamais au démarrage de l'application.
2. **WebSocket** — connexion collante à un pod. Impacte l'affinité de session et le scaling.
3. **Métriques** — Spring n'expose rien nativement. Nécessite actuator + micrometer-prometheus.

---

## 8. Conventions

- **Commits** : Conventional Commits (`feat:`, `fix:`, `docs:`, `chore:`, `ci:`…) — formalisé au palier 2.
- **Branches** : `feature/*` → PR → `main`. Push direct sur `main` bloqué à partir du palier 2.
- **Images** : taguées par SHA de commit. **Jamais `latest` en production.**
- **Secrets** : jamais dans git. Coffre → variable d'environnement → RAM du processus. Rien d'autre.
