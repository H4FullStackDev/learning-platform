# ADR-0002 — Déployer sur un Droplet avant d'introduire Kubernetes

- **Date :** 28 juillet 2026
- **Statut :** ✅ Acté

## Contexte

Le chapitre 11 du cours va directement sur DOKS + ArgoCD. L'objectif étant la **maîtrise** et non la
livraison, la question s'est posée de réduire la complexité initiale.

Argument décisif : le nombre de suspects lors d'une panne.

- **Sur DOKS d'emblée**, une app qui ne répond pas a **neuf causes possibles** : le code, l'image,
  le chart Helm, les probes, le Service, l'Ingress, le Load Balancer, la sync ArgoCD, le firewall
  de la base. Aucun moyen d'en éliminer huit.
- **Sur un Droplet avec Compose**, la même panne a **deux causes possibles** : le conteneur ou le
  reverse proxy.

Argument pédagogique : Kubernetes est une *réponse*. Appris avant d'avoir posé la question, il ne
laisse que de la syntaxe en mémoire.

## Options envisagées

| Option | Pour | Contre |
|---|---|---|
| **Deux phases : Droplet puis DOKS** | Une seule nouveauté à la fois ; les problèmes que K8s résout sont vécus avant d'être résolus ; premier déploiement atteignable en 10 jours | ~40 lignes de configuration jetées à la bascule |
| DOKS directement | Pas de travail jeté | Débogage impossible à isoler ; risque réel d'abandon ; 3 semaines avant la première prod, sur un crédit de 46 jours |
| Droplet uniquement | Le plus simple et le moins cher | Les chapitres 05, 07, 11 et 12 du cours resteraient théoriques |

## Décision

**Phase A** — Droplet unique + Docker Compose + reverse proxy TLS + PostgreSQL managée + DOCR,
le tout provisionné par Terraform. Cible : première production le **7 août 2026**.

**Phase B** — DOKS + Helm + ArgoCD + kube-prometheus-stack, avant le 12 septembre 2026.

## Condition impérative

La phase A doit être un **sous-ensemble strict** de la phase B, jamais une impasse. Concrètement :

| Brique | Réutilisé en phase B |
|---|---|
| Image Docker (multi-stage, non-root, tag `:SHA`) | 100 % — identique |
| Terraform | on étend, on ne réécrit pas |
| DOCR | 100 % |
| Config 12-factor + actuator/probes | 100 % |
| Migrations Flyway en étape dédiée | même concept, `Job` Helm `pre-sync` |
| CI GitHub Actions | ~90 % — seul le dernier step change |
| Prometheus/Grafana | concepts identiques, configuration réécrite |
| Déploiement (`docker compose` via SSH) | **jeté — ~40 lignes** |

**Non négociable : Terraform dès la phase A.** Créer le Droplet à la main dans l'interface web
transformerait la phase B en « apprendre l'IaC *et* Kubernetes en même temps », ce que cette
décision cherche précisément à éviter.

## Conséquences

- Chaque douleur de la phase A est documentée dans le journal, puis reliée à la réponse Kubernetes
  correspondante en phase B. C'est le cœur pédagogique de ce découpage.
- Risque à surveiller : s'arrêter en phase A parce que « ça marche ». Mitigation : la phase B est
  datée, et le crédit expire.
