# ADR-0001 — DigitalOcean, GitHub Actions, PostgreSQL managée, DOCR

- **Date :** 28 juillet 2026
- **Statut :** ✅ Acté

## Contexte

L'objectif est d'appliquer le module DevOps du cours personnel (12 chapitres) sur une application
réelle. Le chapitre 11 du cours décrit précisément un déploiement DigitalOcean : Terraform →
DOKS + PostgreSQL managée + Container Registry + Load Balancer, CI GitHub Actions, GitOps ArgoCD,
observabilité Prometheus/Grafana.

Un crédit d'inscription DigitalOcean de 200 $ est disponible, **expirant le 12 septembre 2026**,
sans renouvellement prévu.

## Options envisagées

| Option | Pour | Contre |
|---|---|---|
| **DigitalOcean** | Correspond exactement au chapitre 11 ; crédit disponible ; tarification simple et lisible | Écosystème plus restreint qu'AWS |
| AWS | Standard du marché | Crédit inexistant ; tarification opaque ; le cours ne le couvre pas |
| VPS générique (Hetzner, OVH) | Le moins cher | Pas de Kubernetes managé, pas de registry, pas de base managée — hors sujet du chapitre 11 |

Pour la CI : GitHub Actions, parce que le dépôt est déjà sur GitHub et que le cours en fournit
l'exemple complet (chapitre 03).

## Décision

**DigitalOcean** pour toute l'infrastructure. **GitHub Actions** pour la CI. **PostgreSQL managée
DigitalOcean** pour la base. **DigitalOcean Container Registry** pour les images.

## Conséquences

- Toute l'infrastructure est décrite en Terraform avec le provider `digitalocean`.
- La facturation est horaire : `terraform destroy` en fin de session divise le coût par ~15.
- **Le 12 septembre 2026, tout est détruit.** Le parcours doit être complet avant cette date.
- Le frontend sera également hébergé sur DigitalOcean, pour consommer le crédit.
