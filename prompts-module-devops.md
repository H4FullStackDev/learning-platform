# Prompts découpés — Module DevOps complet (pour Claude Code)

Chaque bloc est un prompt indépendant à envoyer l'un après l'autre. L'idée : Claude Code
construit le module section par section dans le **même projet**, en ajoutant une page/section
à chaque étape, pour éviter la troncature sur les parties denses (Terraform, sécurité, archi finale).

Envoie le **Prompt 0** en premier pour poser la structure du projet, puis enchaîne.

---

## Prompt 0 — Setup du projet (à envoyer en premier)

```
Tu es un expert DevOps senior et formateur technique. On va construire ensemble, 
étape par étape, un module de formation DevOps complet sous forme d'application web.

STACK
- HTML + Tailwind CSS (CDN) + JS vanilla pour l'interactivité
- Design sombre (dark mode), moderne, inspiré Stripe/Vercel/Linear
- Une page principale avec sidebar sticky de navigation entre chapitres
- Sections en accordéon pour les sous-parties denses
- Blocs de code avec coloration syntaxique lisible (highlight.js ou équivalent en CDN)
- Responsive mobile + desktop
- Utilise les container queries Tailwind (@min-[...]) pour les composants complexes

STRUCTURE DU PROJET
Crée la structure suivante :
- index.html (shell principal avec sidebar + zone de contenu)
- /sections/*.html (une section par chapitre, chargée dynamiquement en JS 
  via fetch, ou injectée si plus simple)
- /assets/css/style.css (styles custom en complément de Tailwind)
- /assets/js/app.js (navigation, accordéons, progress bar de lecture)

Pour l'instant : mets en place uniquement le SHELL (sidebar avec la liste des 11 chapitres 
ci-dessous en placeholders vides, navigation fonctionnelle, dark mode, barre de progression 
de lecture). Pas de contenu de cours pour le moment, je te le donnerai chapitre par chapitre 
dans les prompts suivants.

LISTE DES 11 CHAPITRES (pour la sidebar)
1. Introduction au DevOps
2. Versioning & Collaboration (Git)
3. Intégration Continue (CI)
4. Conteneurisation (Docker)
5. Orchestration (Kubernetes)
6. Infrastructure as Code (Terraform)
7. GitOps & Déploiement Continu (CD)
8. Monitoring & Observabilité
9. Sécurité DevSecOps (API Gateway, OAuth2, OIDC)
10. Architecture de déploiement complète
11. Exemple pratique : déploiement complet sur DigitalOcean

Confirme-moi la structure créée avant qu'on attaque le chapitre 1.
```

---

## Prompt 1 — Introduction au DevOps

```
On continue le module DevOps. Remplis maintenant la section "1. Introduction au DevOps" 
dans la structure déjà en place.

CONTENU ATTENDU
- Définition du DevOps et son évolution historique (Dev vs Ops, pourquoi ce mur existait)
- Culture DevOps : le modèle CALMS (Culture, Automation, Lean, Measurement, Sharing) 
  expliqué avec un schéma visuel (HTML/CSS ou SVG, pas juste du texte)
- Différence DevOps vs SRE vs Platform Engineering (tableau comparatif)
- Le cycle de vie DevOps complet : Plan → Code → Build → Test → Release → Deploy → 
  Operate → Monitor, représenté par un diagramme circulaire ou en boucle (SVG animé si 
  possible en CSS)
- Un encart "Pourquoi c'est important" avec des cas concrets d'entreprises ayant réussi 
  leur transformation DevOps

FORMAT
- Explication claire + schéma visuel + encart "bonnes pratiques"
- Niveau intermédiaire (je connais déjà les bases du dev)

Intègre cette section dans le fichier correspondant, garde le style cohérent avec le shell 
déjà créé.
```

---

## Prompt 2 — Versioning & Collaboration (Git)

```
Section "2. Versioning & Collaboration". 

CONTENU ATTENDU
- Rappel rapide des concepts de base Git (juste 2-3 lignes, on va vite car je connais déjà)
- Branching strategies en détail avec schémas visuels :
  * Git Flow (avec diagramme des branches main/develop/feature/release/hotfix)
  * Trunk-Based Development (avec diagramme)
  * Comparaison : quand utiliser l'un ou l'autre selon la taille de l'équipe
- Pull Requests / Merge Requests : bonnes pratiques de code review
- Protection de branches : règles typiques (required reviews, status checks, 
  signed commits) avec exemple de configuration GitHub/GitLab
- Conventional Commits (format, exemple, pourquoi c'est utile pour le CI/CD et le 
  changelog automatique)
- Encart "bonnes pratiques" : taille des PR, nommage des branches, squash vs merge commit

FORMAT identique aux sections précédentes : théorie + schéma + exemples de code réels 
(exemples de commandes git, exemple de fichier CODEOWNERS, exemple de config de 
protection de branche en YAML si applicable) + encart bonnes pratiques.
```

---

## Prompt 3 — Intégration Continue (CI)

```
Section "3. Intégration Continue (CI)".

CONTENU ATTENDU
- Concepts clés : qu'est-ce qu'un pipeline, stages, jobs, artifacts, cache
- Comparaison rapide des outils : GitHub Actions vs GitLab CI vs Jenkins (tableau : 
  facilité de setup, écosystème, hébergement, coût)
- Exemple COMPLET et commenté d'un pipeline GitHub Actions (.github/workflows/ci.yml) : 
  lint, tests unitaires, build, scan de sécurité basique, upload d'artifact
- Exemple équivalent en GitLab CI (.gitlab-ci.yml) pour comparaison
- Notion de cache et parallélisation des jobs pour accélérer le pipeline
- Encart "bonnes pratiques" : fail fast, pipelines rapides (<10min), séparation 
  build/test/deploy, secrets management dans les pipelines (ne jamais commit de secrets)

FORMAT : théorie + tableau comparatif + 2 exemples de fichiers YAML réels et commentés 
ligne par ligne + encart bonnes pratiques. Affiche le code dans des blocs avec coloration 
syntaxique YAML.
```

---

## Prompt 4 — Conteneurisation (Docker)

```
Section "4. Conteneurisation (Docker)".

CONTENU ATTENDU
- Rappel des concepts (image, conteneur, layer, registry) — rapide
- Dockerfile en détail avec un exemple COMPLET pour une app Node.js ou Python :
  * Explication de chaque instruction (FROM, WORKDIR, COPY, RUN, EXPOSE, CMD/ENTRYPOINT)
  * Multi-stage build expliqué et illustré (build stage + production stage minimal)
  * Bonnes pratiques : .dockerignore, image de base légère (alpine/distroless), 
    utilisateur non-root, ordre des layers pour optimiser le cache
- Docker Compose : exemple complet avec plusieurs services (app + base de données + 
  cache Redis + reverse proxy nginx), explication du fichier docker-compose.yml
- Schéma visuel du processus de build multi-stage (avant/après taille d'image)
- Encart "bonnes pratiques" : scan de vulnérabilités des images (Trivy), tagging des 
  images (jamais "latest" en prod), registre privé

FORMAT : théorie + schéma + Dockerfile complet commenté + docker-compose.yml complet 
commenté + encart bonnes pratiques.
```

---

## Prompt 5 — Orchestration (Kubernetes)

```
Section "5. Orchestration (Kubernetes)".

CONTENU ATTENDU (c'est une section dense, prends le temps de bien la détailler)
- Concepts fondamentaux avec schéma d'architecture Kubernetes (control plane : 
  API server, etcd, scheduler, controller manager ; worker nodes : kubelet, kube-proxy, 
  container runtime)
- Objets Kubernetes expliqués un par un avec exemple de manifest YAML pour chacun :
  * Pod
  * Deployment (avec replicas, rolling update strategy)
  * Service (ClusterIP, NodePort, LoadBalancer — différences expliquées)
  * Ingress (avec exemple de règles de routing)
  * ConfigMap et Secret (différence, exemple d'utilisation dans un Deployment)
  * PersistentVolume / PersistentVolumeClaim (brève explication)
- Helm : à quoi ça sert, structure d'un chart (Chart.yaml, values.yaml, templates/), 
  exemple de commande helm install/upgrade
- Tableau comparatif : Kubernetes vs Docker Swarm vs AWS ECS (complexité, scalabilité, 
  cas d'usage)
- Encart "bonnes pratiques" : resource limits/requests, liveness/readiness probes, 
  namespaces pour isoler les environnements

FORMAT : théorie + schéma d'architecture + manifests YAML réels et commentés pour 
chaque objet + tableau comparatif + encart bonnes pratiques.
```

---

## Prompt 6 — Infrastructure as Code (Terraform)

```
Section "6. Infrastructure as Code (Terraform)" — section clé, à détailler sérieusement.

CONTENU ATTENDU
- Qu'est-ce que l'IaC et pourquoi (déclaratif vs impératif, reproductibilité, 
  versioning de l'infra)
- Concepts Terraform expliqués un par un :
  * Providers (exemple avec provider "digitalocean" et provider "aws")
  * Resources (syntaxe de base)
  * Variables et outputs (exemple de fichiers variables.tf et outputs.tf)
  * State (terraform.tfstate) : à quoi ça sert, pourquoi le stocker à distance 
    (backend remote, ex: S3 + DynamoDB pour le lock, ou Terraform Cloud)
  * Modules : comment structurer un projet Terraform en modules réutilisables 
    (exemple d'arborescence de dossiers)
  * Workspaces : gérer plusieurs environnements (dev/staging/prod) avec le même code
- Workflow complet : terraform init / plan / apply / destroy — expliqué avec un schéma 
  du cycle de vie
- EXEMPLE CONCRET COMPLET : provisionner une infra sur DigitalOcean avec Terraform :
  * Un droplet (VM)
  * Un VPC
  * Une base de données managée (PostgreSQL)
  * Un load balancer
  Donne le code Terraform complet et commenté (main.tf, variables.tf, outputs.tf) 
  pour cet exemple.
- Comparaison rapide en tableau : Terraform vs Ansible vs Pulumi (approche, langage, 
  cas d'usage)
- Encart "bonnes pratiques" : ne jamais commit le .tfstate ni les secrets, utiliser 
  terraform fmt et validate, revue de plan avant apply, verrouillage de state en équipe

FORMAT : théorie + schémas + code Terraform réel et complet (plusieurs fichiers .tf 
affichés dans des blocs séparés et commentés) + tableau comparatif + encart bonnes pratiques.
```

---

## Prompt 7 — GitOps & Déploiement Continu (CD)

```
Section "7. GitOps & Déploiement Continu (CD)".

CONTENU ATTENDU
- Définition du GitOps : le Git comme source de vérité unique pour l'état désiré de 
  l'infra ET des applications (pas seulement du code applicatif)
- Différence CI/CD classique (push-based) vs GitOps (pull-based) — schéma comparatif clair :
  * CI/CD classique : le pipeline PUSH les changements vers le cluster (kubectl apply 
    depuis le pipeline)
  * GitOps : un agent (ArgoCD/Flux) dans le cluster TIRE en continu l'état désiré 
    depuis un repo Git et réconcilie automatiquement
- Les 4 principes du GitOps (declarative, versioned, pulled automatically, 
  continuously reconciled)
- ArgoCD en détail :
  * Architecture (Application CRD, ApplicationSet, sync policy)
  * Exemple de manifest Application ArgoCD complet et commenté
  * Notion de "drift detection" et auto-sync
- Alternative : Flux CD (mention rapide, différences avec ArgoCD)
- Stratégies de déploiement à intégrer avec le GitOps :
  * Rolling update (schéma)
  * Blue-Green deployment (schéma)
  * Canary deployment (schéma, avec mention Argo Rollouts)
- Structure typique d'un repo GitOps (séparation repo applicatif / repo de config 
  d'infra, structure de dossiers par environnement)
- Encart "bonnes pratiques" : jamais de kubectl apply manuel en prod, tout passe par 
  le repo Git, rollback = simple git revert

FORMAT : théorie + schémas comparatifs (push vs pull) + manifest ArgoCD réel commenté 
+ schémas des 3 stratégies de déploiement + encart bonnes pratiques.
```

---

## Prompt 8 — Monitoring & Observabilité

```
Section "8. Monitoring & Observabilité".

CONTENU ATTENDU
- Les 3 piliers de l'observabilité expliqués avec schéma : Logs, Métriques, Traces
- Prometheus : architecture (scraping, exporters, PromQL basique), exemple de 
  configuration prometheus.yml
- Grafana : à quoi ça sert, exemple de dashboard type (capture conceptuelle en HTML/CSS 
  si possible, ou description des panels typiques : latence, taux d'erreur, throughput, 
  saturation — les "4 golden signals")
- Stack ELK/EFK (Elasticsearch/Fluentd/Kibana ou Logstash) pour la centralisation des logs 
  — schéma du flux de données
- Tracing distribué : notion de trace ID / span ID, exemple avec Jaeger ou OpenTelemetry
- Alerting : exemple de règle d'alerte Prometheus (Alertmanager) commentée
- Encart "bonnes pratiques" : SLI/SLO/SLA expliqués brièvement, alerting actionnable 
  (pas de fatigue d'alerte), dashboards par audience (dev vs business)

FORMAT : théorie + schémas + exemples de configuration réels (prometheus.yml, règle 
d'alerte YAML) + encart bonnes pratiques.
```

---

## Prompt 9 — Sécurité DevSecOps (API Gateway, OAuth2, OIDC)

```
Section "9. Sécurité DevSecOps" — section importante, à bien détailler.

CONTENU ATTENDU

A) API Gateway
- Rôle d'un API Gateway (point d'entrée unique, découplage des clients et des services)
- Fonctionnalités clés : routing, rate limiting, transformation de requêtes/réponses, 
  authentification centralisée, load balancing
- Comparaison rapide : Kong vs AWS API Gateway vs NGINX (tableau)
- Exemple de configuration Kong ou NGINX avec rate limiting (fichier de config commenté)

B) OAuth 2.0 — à détailler en profondeur
- Les acteurs : Resource Owner, Client, Authorization Server, Resource Server
- Les flows expliqués un par un AVEC SCHÉMA DE SÉQUENCE pour chacun :
  * Authorization Code Flow (le plus courant, avec redirection)
  * Authorization Code + PKCE (pourquoi c'est le standard recommandé aujourd'hui, 
    même pour les apps mobiles/SPA)
  * Client Credentials Flow (machine à machine)
  * (mentionner que le Implicit Flow est déprécié et pourquoi)
- Access Token vs Refresh Token : différence, durée de vie, bonnes pratiques de stockage
- Scopes : à quoi ça sert, exemple

C) OpenID Connect (OIDC)
- Différence fondamentale avec OAuth2 : OAuth2 = autorisation, OIDC = authentification 
  (ajoute une couche d'identité par-dessus OAuth2)
- ID Token : structure JWT (header, payload, signature), exemple de payload décodé
- Claims standards (sub, iss, aud, exp, iat, etc.)
- Endpoint /userinfo, discovery document (.well-known/openid-configuration)
- Schéma du flow complet OIDC avec un Identity Provider (ex: Keycloak, Auth0, Google)

D) Secrets Management
- Pourquoi ne jamais mettre de secrets en clair dans le code ou les fichiers de config
- HashiCorp Vault : concept de base (secrets engine, dynamic secrets)
- Alternative managée : AWS Secrets Manager / DigitalOcean equivalent

E) Scan de vulnérabilités
- SAST vs DAST (différence, à quel moment du pipeline chacun intervient)
- Exemples d'outils : Trivy (scan d'images Docker), Snyk (scan de dépendances)
- Où intégrer ça dans le pipeline CI (schéma)

FORMAT : cette section doit avoir BEAUCOUP de schémas visuels (surtout pour les flows 
OAuth2/OIDC, en diagrammes de séquence clairs avec les flèches entre acteurs), du code 
réel (JWT décodé en exemple, config Kong/NGINX, règle Vault) et un encart bonnes 
pratiques global sur la sécurité.
```

---

## Prompt 10 — Architecture de déploiement complète (schéma final)

```
Section "10. Architecture de déploiement complète".

OBJECTIF
Produire UN SCHÉMA D'ARCHITECTURE COMPLET ET VISUEL (en SVG ou HTML/CSS, pas juste du 
texte) qui rassemble tous les concepts vus dans les sections précédentes, pour une 
application ayant : un frontend web, une API backend, et une app mobile qui consomme 
la même API.

LE SCHÉMA DOIT INCLURE (avec les flèches de flux de données/requêtes entre les blocs)
1. Utilisateurs : navigateur web + app mobile
2. CDN (pour les assets statiques du frontend, ex: Cloudflare)
3. Frontend web (SPA) hébergé sur le CDN ou un bucket de stockage statique
4. API Gateway en entrée (gère l'auth OAuth2/OIDC, le rate limiting, le routing)
5. Identity Provider (Keycloak/Auth0) branché à l'API Gateway pour l'authentification
6. Backend : cluster Kubernetes avec plusieurs microservices (ou monolithe modulaire, 
   au choix, explique les deux options)
7. Base de données SQL managée (PostgreSQL) + cache Redis
8. Message broker si pertinent (ex: RabbitMQ/Kafka) pour la communication asynchrone 
   entre services
9. Load balancer devant le cluster K8s
10. Pipeline CI/CD : Git repo → CI (build/test/scan) → Registry d'images Docker → 
    ArgoCD (GitOps) → déploiement sur le cluster K8s
11. Repo GitOps séparé contenant les manifests K8s / Helm charts
12. Stack monitoring : Prometheus + Grafana + Alertmanager, branchés sur le cluster
13. Stack logs centralisés (EFK) branchée sur le cluster
14. Terraform en amont de tout ça pour provisionner l'infra cloud (VPC, cluster K8s, 
    base de données managée, load balancer)

Légende claire avec un code couleur par catégorie (frontend, sécurité, backend, données, 
CI/CD, observabilité, infra).

Après le schéma, ajoute un tableau récapitulatif : "Composant → Rôle → Outil utilisé 
dans ce module" qui reprend TOUS les outils vus dans les sections précédentes 
(Docker, K8s, Terraform, ArgoCD, Prometheus, Grafana, Kong, OAuth2/OIDC provider, etc.)

FORMAT : le schéma doit être le point central de cette section, grand, lisible, avec 
zoom/scroll si besoin sur mobile. Le tableau récapitulatif en dessous.
```

---

## Prompt 11 — Exemple pratique : déploiement complet sur DigitalOcean

```
Section "11. Exemple pratique : déploiement complet sur DigitalOcean" — dernière 
section, la plus concrète, celle qui rassemble tout ce qu'on a vu appliqué à un cas réel.

CONTEXTE DE L'EXEMPLE
On déploie une application composée de : un frontend web (React/Angular), une API 
backend (Node.js ou équivalent), une base de données PostgreSQL, et cette même API 
sert aussi une application mobile.

CONTENU ATTENDU

A) Organigramme d'utilisation des outils (étape par étape, du code à la prod)
Fais un ORGANIGRAMME VISUEL (flowchart, en SVG ou HTML/CSS avec des boîtes reliées par 
des flèches) qui montre le parcours complet dans l'ORDRE CHRONOLOGIQUE :
1. Dev écrit du code → push sur Git (branche feature)
2. Pull Request → code review → merge sur main (voir chapitre 2)
3. Déclenchement du pipeline CI (GitHub Actions) : lint → tests → build → scan Trivy 
   (voir chapitre 3 et 9)
4. Build de l'image Docker → push sur le Container Registry DigitalOcean (voir chapitre 4)
5. Mise à jour du repo GitOps (nouveau tag d'image dans le manifest Helm/K8s) (voir 
   chapitre 7)
6. ArgoCD détecte le changement dans le repo GitOps → synchronise automatiquement 
   sur le cluster DOKS (DigitalOcean Kubernetes) (voir chapitre 5 et 7)
7. Kubernetes fait un rolling update des pods (voir chapitre 5)
8. Requêtes utilisateurs → passent par le Load Balancer DO → API Gateway (auth 
   OAuth2/OIDC) → services K8s (voir chapitre 9)
9. Monitoring en continu : Prometheus scrape les métriques, Grafana affiche les 
   dashboards, alertes si anomalie (voir chapitre 8)
Chaque étape de l'organigramme doit avoir une micro-annotation indiquant à quel 
chapitre du module elle correspond (pour que ce soit pédagogique et que je puisse 
relier la théorie à la pratique).

B) Infra Terraform complète pour DigitalOcean
Donne le code Terraform COMPLET pour provisionner :
- Un cluster DOKS (DigitalOcean Kubernetes)
- Une base de données managée PostgreSQL
- Un Container Registry DigitalOcean
- Un Load Balancer
- Un VPC dédié
- Les variables et outputs nécessaires
Fichiers : main.tf, variables.tf, outputs.tf, terraform.tfvars.example — tous commentés.

C) Setup GitOps avec ArgoCD sur ce cluster
- Commandes d'installation d'ArgoCD sur le cluster DOKS
- Exemple de manifest Application ArgoCD pointant vers le repo GitOps du projet
- Structure du repo GitOps (arborescence de dossiers avec les Helm charts pour 
  frontend, backend, et les values par environnement dev/staging/prod)

D) Pipeline CI/CD complet
Fichier .github/workflows/deploy.yml complet et commenté, qui va du build jusqu'à 
la mise à jour du repo GitOps (pas de déploiement direct, on respecte le pattern 
GitOps du chapitre 7).

E) Checklist finale de mise en prod
Une checklist visuelle (cases à cocher stylées en CSS) reprenant les points de 
sécurité, monitoring et bonnes pratiques essentiels avant d'aller en prod, avec 
référence au chapitre correspondant pour chaque point.

FORMAT : organigramme visuel en premier (le clou de cette section), puis le code 
Terraform complet, puis ArgoCD, puis le pipeline CI/CD, puis la checklist. Cette 
section doit donner l'impression de "boucler la boucle" avec tout le reste du module.
```

---

## Notes d'utilisation

- Envoie les prompts **dans l'ordre**, un par un, en attendant que Claude Code termine 
  chaque section avant de passer à la suivante — ça évite les sections tronquées ou 
  bâclées sur la fin.
- Si une section ressort trop légère (ex: Terraform ou OAuth2/OIDC qui sont denses), 
  tu peux relancer avec : *"Développe davantage la partie [X], ajoute plus d'exemples 
  concrets et un schéma supplémentaire."*
- À la fin, demande à Claude Code de faire une passe de cohérence visuelle globale 
  (mêmes couleurs, mêmes espacements, mêmes styles de blocs de code) sur les 11 sections.
