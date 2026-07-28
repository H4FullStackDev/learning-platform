# ADR-0003 — Plan de domaines : sous-domaines séparés

- **Date :** 28 juillet 2026
- **Statut :** 🟡 Proposé — en attente d'achat du domaine

## Contexte

Le frontend sera hébergé séparément du backend, volontairement, pour ne pas mélanger les
apprentissages. L'API expose déjà `server.servlet.context-path=/api`.

Le domaine n'est pas encore acheté. Ce choix conditionne le certificat TLS, la configuration CORS
et le nombre d'enregistrements DNS — il doit être tranché avant l'achat.

## Options envisagées

**Option 1 — sous-domaines séparés**
```
api.tondomaine.com   ──►  Droplet / Load Balancer  ──►  API Spring
app.tondomaine.com   ──►  hébergement statique DO  ──►  frontend
```
Front et back totalement découplés : le frontend peut être déployé, remplacé ou migré sans toucher
au backend. Impose de configurer CORS explicitement, puisque ce sont deux origines distinctes.

**Option 2 — domaine unique, routage par chemin**
```
tondomaine.com/api/*  ──►  API
tondomaine.com/*      ──►  frontend
```
Pas de CORS (même origine), mais front et back deviennent solidaires : un reverse proxy commun, un
seul certificat, et le frontend ne peut plus être déployé indépendamment.

## Décision

**Option 1 — sous-domaines séparés.** L'option 2 annulerait précisément la séparation recherchée.
La contrainte CORS est vue comme un bénéfice : elle oblige à traiter le sujet explicitement plutôt
qu'à l'ignorer.

## Conséquences

- Deux enregistrements DNS, gérés par Terraform (chapitre 06).
- Configuration CORS explicite côté Spring Security — à traiter au palier 3.
- Deux certificats TLS, ou un certificat couvrant les deux sous-domaines.
- **Le domaine doit être acheté avant le 6 août** : la propagation DNS et l'émission du certificat
  prennent du temps. Choisir un registrar permettant de déléguer les nameservers à DigitalOcean.
