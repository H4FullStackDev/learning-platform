# Parcours DevOps — déploiement de A à Z sur DigitalOcean

> Document de référence du parcours. À lire au calme.
> Mis à jour au fil des paliers. Dernière révision : 28 juillet 2026.

---

## Pourquoi ce document existe

Tu déploies cette API pour **apprendre le DevOps, pas pour livrer un produit**. L'application
n'est utilisée par personne. C'est une chance : on peut tout casser exprès, et c'est même prévu
au programme (voir §7).

La règle du parcours : **rien n'est écrit avant que le schéma soit clair et la décision tranchée.**
Chaque palier suit quatre temps — schéma (T1), décision (T2), exécution (T3), preuve et rollback (T4).
Un palier n'est pas terminé tant que tu ne sais pas l'annuler.

---

## 1. L'architecture cible

C'est celle du chapitre 11 de ton cours, adaptée à Spring Boot.

```
   TOI                      GITHUB                          DIGITALOCEAN
   ───                      ──────                          ────────────

 feature/*  ──PR──►  main ──►  Actions (ch.03)
                                 │ mvn verify + Testcontainers
                                 │ docker build multi-stage (ch.04)
                                 │ Trivy HIGH/CRITICAL (ch.09)
                                 ▼
                        registry.digitalocean.com  ◄── image taguée :SHA (ch.04)
                                 │
                                 ▼
                   repo  requisition-gitops   ← la CI bump le tag Helm (ch.07)
                                 │                    JAMAIS de kubectl apply
                                 │  (pull)
                                 ▼
     ┌───────────────────  ArgoCD  ───────────────────┐  (ch.07)
     │                                                 │
     │   DOKS  ──── ns: staging ──── ns: prod          │  (ch.05)
     │            │                                    │
     │            ├── Job Flyway (pre-sync)  ◄── absent du cours
     │            ├── Deployment API (rolling, probes) │
     │            └── Service ──► Ingress               │
     │                              │                  │
     │   Prometheus + Grafana ◄─────┤ /actuator/prometheus  (ch.08)
     └──────────────────────────────┼──────────────────┘
                                    ▼
                   Load Balancer DO (TLS) ──► internet     (ch.06 / ch.09)
                                    │
                                    ▼  réseau privé VPC uniquement
                   PostgreSQL managée + firewall = cluster (ch.06)

   Le tout décrit dans  terraform/  ── state distant sur Spaces ── (ch.06)
```

**Les cinq questions auxquelles cette architecture répond** — tu dois savoir y répondre de mémoire
à la fin du parcours :

1. Où s'arrête le code et où commence l'artefact ? → *l'image, et elle est immuable*
2. Qu'est-ce qui a le droit de casser la prod ? → *les gates du pipeline, rien d'autre*
3. Comment revenir en arrière en 2 minutes ? → *`git revert` dans le repo GitOps*
4. Où vit un secret, à chaque étape ? → *dans un coffre et dans la RAM du processus. Nulle part ailleurs.*
5. Comment savoir que ça marche sans qu'un utilisateur le dise ? → *probes + métriques + alertes*

---

## 2. Pourquoi on ne commence PAS par Kubernetes

Décision actée : [ADR-0002](adr/0002-droplet-avant-kubernetes.md).

Si la première mise en production se fait directement sur DOKS et que l'app ne répond pas, tu as
**neuf suspects** : ton code, ton image, ton chart Helm, tes probes, le Service, l'Ingress, le Load
Balancer, la sync ArgoCD, le firewall de la base. Aucun moyen d'en éliminer huit.

Sur un Droplet unique avec Docker Compose, la même panne a **deux suspects** : ton conteneur ou ton
reverse proxy.

Mais le vrai bénéfice est ailleurs. **Kubernetes est une réponse.** Si tu l'apprends avant d'avoir
posé la question, tu retiens de la syntaxe. En phase A, tu vas rencontrer physiquement les problèmes
qu'il résout :

| Ce que tu vivras en phase A | La réponse en phase B |
|---|---|
| `docker compose up -d` coupe le service ~20 s à chaque déploiement | `RollingUpdate` avec `maxUnavailable: 0` |
| L'app crashe la nuit, tu le découvres le matin | `livenessProbe` + `restartPolicy` |
| Montée de charge, et tu ne peux rien faire | `replicas`, puis HPA |
| Tu bricoles un truc en SSH, tu oublies, ça diverge du git | `selfHeal` d'ArgoCD |
| Rollback = SSH + retrouver le tag précédent + relancer à la main | `git revert`, et c'est tout |

Le jour où tu écriras `maxUnavailable: 0`, tu ne liras pas du YAML : tu te souviendras des
20 secondes de coupure.

### La couture entre les deux phases

La phase A doit être un **sous-ensemble strict** de la phase B, jamais une impasse :

| Brique | Phase A (Droplet) | Phase B (DOKS) | Réutilisé |
|---|---|---|---|
| Image Docker | multi-stage, non-root, tag `:SHA` | **identique** | 100 % |
| Terraform | Droplet, PG, DOCR, firewall, DNS | on **ajoute** DOKS + LB | on étend |
| Registry DOCR | oui | oui | 100 % |
| Config 12-factor + actuator | oui | oui | 100 % |
| Migrations Flyway | étape de déploiement dédiée | Job Helm `pre-sync` | même concept |
| CI GitHub Actions | test → build → Trivy → push | **identique**, seul le dernier step change | ~90 % |
| Prometheus / Grafana | dans le Compose | `kube-prometheus-stack` | config réécrite |
| **Déploiement** | `docker compose` via SSH | Helm + ArgoCD | **jeté (~40 lignes)** |

Point non négociable : **Terraform dès la phase A.** Ne crée pas ce Droplet à la main dans
l'interface web. Sinon la phase B devient « apprendre l'IaC *et* Kubernetes en même temps », ce
qu'on cherche précisément à éviter.

---

## 3. Le calendrier — 46 jours

Du 28 juillet au 12 septembre 2026. Le crédit ne sera pas renouvelé, donc **la phase B doit être
faite avant le 12 septembre** : c'est la partie chère.

### Sprint 1 — première mise en production le vendredi 7 août

| Jour | Palier | Livrable |
|---|---|---|
| Mer 29/07 | 1 Secrets + 2 Git | Secrets révoqués, `main` protégée |
| Jeu 30/07 | 3 App déployable | `/actuator/health` répond `UP` |
| Ven 31/07 | 4 Docker | Image < 250 Mo, non-root, démarre sans `.env` |
| Sam-Dim 01-02/08 | 5 Compose + 6 Tests | `docker compose up` = app complète, Testcontainers vert |
| Lun 03/08 | 7 CI | Une PR cassée est bloquée avant merge |
| Mar-Mer 04-05/08 | 8A Terraform | `destroy` puis `apply` → infra identique |
| Jeu 06/08 | 9A Déploiement + TLS | HTTPS, migrations, rollback manuel testé |
| **Ven 07/08** | — | 🎯 **PREMIÈRE PROD** |

### Suite

| Semaine | Contenu |
|---|---|
| 10 → 16/08 | 10A Observabilité sur Droplet · 13 Frontend (phase A) |
| 17 → 23/08 | 8B DOKS · 9B Helm, probes, Job Flyway |
| 24 → 30/08 | 10B ArgoCD · 11B kube-prometheus-stack |
| 31/08 → 06/09 | 12 Scaling, HPA, durcissement · frontend en cluster |
| 07 → 12/09 | Checklist 11·E · rétrospective · **`terraform destroy` final** |

---

## 4. L'argent — et pourquoi c'est une compétence DevOps

Ordres de grandeur mensuels (à revérifier sur la page pricing au moment du `terraform apply`) :

| Ressource | Cours (défaut) | Notre dimensionnement |
|---|---|---|
| Control plane DOKS | gratuit | gratuit |
| Nœuds worker | 3 × `s-2vcpu-4gb` ≈ 72 $ | 2 × `s-2vcpu-4gb` ≈ 48 $ |
| PostgreSQL managée | `db-s-1vcpu-2gb` ≈ 30 $ | `db-s-1vcpu-1gb` ≈ 15 $ |
| Load Balancer | ≈ 12 $ | ≈ 12 $ |
| Container Registry | Basic ≈ 5 $ | Starter (gratuit) |
| Spaces (state Terraform) | — | ≈ 5 $ |
| **Total** | **≈ 119 $/mois** | **≈ 80 $/mois** |

Le dimensionnement du cours brûlerait les 200 $ en sept semaines. Le nôtre tient jusqu'au bout.

On ne descend pas les nœuds à 2 Go : entre les pods système, ArgoCD et la stack Prometheus, tu
serais en `Pending` faute de mémoire — et déboguer un manque de RAM cluster n'est pas la leçon
recherchée. Le dimensionnement réel se fera au palier 9B, en écrivant les `requests/limits`.

**Le réflexe qui change tout.** DigitalOcean facture à l'heure. Le cluster et le Load Balancer sont
*stateless* : Terraform les recrée à l'identique en quelques minutes.

> `terraform destroy` en fin de session · `terraform apply` au début de la suivante.

80 $/mois ≈ **0,11 $/heure**. Trois sessions de 4 h par semaine ≈ **5 $/mois** au lieu de 80 $.

La base PostgreSQL, elle, n'est **pas** détruite : elle est *stateful*, et elle ne coûte que 15 $.
Savoir distinguer ce qui est jetable de ce qui est précieux, c'est la distinction stateless/stateful
du chapitre 05 — apprise sur ta facture plutôt que dans un paragraphe.

---

## 5. Le plan de domaines

Décision proposée : [ADR-0003](adr/0003-plan-de-domaines.md).

L'API a déjà `server.servlet.context-path=/api`.

```
   api.tondomaine.com   ──►  Droplet / Load Balancer  ──►  API Spring
   app.tondomaine.com   ──►  hébergement statique DO  ──►  frontend
```

Sous-domaines séparés : le front peut être déployé sans toucher au back. Contrepartie — il faut
configurer CORS explicitement, ce qui est une bonne chose : ça t'oblige à traiter le sujet au lieu
de l'ignorer.

**Achète le domaine cette semaine.** La propagation DNS et l'émission du certificat prennent du
temps, et tu en as besoin le 6 août. Prends-le chez un registrar qui permet de déléguer les
nameservers à DigitalOcean, pour que Terraform gère tes enregistrements DNS.

---

## 6. Les écarts entre le cours et ce projet

Le chapitre 11 prend un backend Node.js. Ces trois sujets devront être **conçus**, pas copiés :

| Écart | Le problème | Palier |
|---|---|---|
| **Migrations Flyway** | 3 pods qui démarrent = 3 Flyway en concurrence sur la même base | 9B — Job Helm `pre-sync` |
| **WebSocket** | Une connexion WS est collante à un pod ; le chapitre 12 raisonne stateless | 9B & 12 |
| **Métriques** | Spring n'expose rien nativement | 3 & 11B — actuator + micrometer |

Le plus urgent : **`spring-boot-starter-actuator` est absent du `pom.xml`** alors que les probes
liveness/readiness sont déjà configurées dans `application-prod.properties`. Sans lui,
`/actuator/health` renvoie 404 → les probes échouent → `CrashLoopBackOff`, et deux heures perdues
à chercher pourquoi. Traité au palier 3.

---

## 7. Les exercices « casse-le »

C'est ce qui sépare « j'ai suivi un tutoriel » de « je maîtrise ». À chaque palier, en T4, tu
provoques la panne **avant** qu'elle ne t'arrive.

| Palier | Exercice | Ce que tu apprends |
|---|---|---|
| 3 | Démarrer l'app sans `SPRING_DATASOURCE_URL` | Lire une stacktrace de démarrage Spring |
| 4 | Lancer l'image sans variables d'env | Distinguer erreur de config et erreur de code |
| 5 | Couper le conteneur Postgres pendant que l'API tourne | Comportement du pool Hikari, timeouts |
| 7 | Ouvrir une PR avec un test qui échoue | Vérifier que la gate bloque vraiment |
| 8A | `terraform destroy` puis `apply` | Prouver la reproductibilité de l'infra |
| 9A | Déployer une image cassée, puis revenir en arrière | Chronométrer ton rollback |
| 9B | `kubectl delete pod` en pleine requête | Self-healing, et ce que voit le client |
| 10B | Modifier le cluster à la main | Voir ArgoCD corriger le drift tout seul |
| 11B | Saturer le CPU de l'app | Voir l'alerte partir avant que tu ne le remarques |
| 12 | Test de charge jusqu'à la rupture | Trouver ta vraie limite, pas celle que tu supposes |

---

## 8. Les paliers

| # | Palier | Chapitre | Phase |
|---|---|---|---|
| 1 | Révocation des secrets & hygiène du dépôt | 09·D | — |
| 2 | Git : branches, PR, protection, commits conventionnels | 02 | — |
| 3 | App déployable : 12-factor, actuator, probes, config | 10 | — |
| 4 | Docker : multi-stage, layered jar, non-root | 04 | — |
| 5 | Compose local : API + Postgres + SMTP factice | 04 | — |
| 6 | Tests : Testcontainers | 03 | — |
| 7 | CI : test → build → Trivy → push DOCR | 03, 09·E | — |
| 8A | Terraform : Droplet, PG, DOCR, firewall, DNS | 06 | A |
| 9A | Déploiement Compose distant, migrations, TLS, rollback | 04 | A |
| 10A | Observabilité : Prometheus + Grafana sur Droplet | 08 | A |
| 8B | Terraform : ajout DOKS + Load Balancer | 06, 11·B | B |
| 9B | Kubernetes : Helm, probes, resources, Job Flyway | 05 | B |
| 10B | GitOps : ArgoCD, repo de config, rollback `git revert` | 07, 11·C/D | B |
| 11B | Observabilité K8s : kube-prometheus-stack, alertes, SLO | 08 | B |
| 12 | Scaling & durcissement : HPA, rate limiting, checklist | 12, 09 | B |
| 13 | Frontend, hébergé séparément | — | A puis B |

Les 12 chapitres du cours sont couverts. Aucun n'est sauté : ils sont **réordonnés pour que chaque
nouveauté arrive seule**.

---

## 9. Où on en est

**Palier 1 — Révocation des secrets.** Voir [journal.md](journal.md) pour le détail et les actions
restantes.
