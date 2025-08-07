--insert-dashboard_permission
INSERT INTO permission (name, category, label) VALUES ('DASHBOARD', 'Dashboard', 'Tableau de bord');

--insert-Account_permission
INSERT INTO permission (name, category, label) VALUES ('ACCOUNT', 'Compte', 'Compte');

--insert-other_permission
INSERT INTO permission (name, category, label) VALUES ('USER_LIST', 'Utilisateur', 'Lister');
INSERT INTO permission (name, category, label) VALUES ('USER_CREATE', 'Utilisateur', 'Ajouter');
INSERT INTO permission (name, category, label) VALUES ('USER_UPDATE', 'Utilisateur', 'Modifier');
INSERT INTO permission (name, category, label) VALUES ('USER_DELETE', 'Utilisateur', 'Supprimer');

INSERT INTO permission (name, category, label) VALUES ('PERMISSION_LIST', 'Permission', 'Lister');
INSERT INTO permission (name, category, label) VALUES ('PERMISSION_CREATE', 'Permission', 'Ajouter');
INSERT INTO permission (name, category, label) VALUES ('PERMISSION_UPDATE', 'Permission', 'Modifier');
INSERT INTO permission (name, category, label) VALUES ('PERMISSION_DELETE', 'Permission', 'Supprimer');

INSERT INTO permission (name, category, label) VALUES ('ROLE_LIST', 'Role', 'Lister');
INSERT INTO permission (name, category, label) VALUES ('ROLE_CREATE', 'Role', 'Ajouter');
INSERT INTO permission (name, category, label) VALUES ('ROLE_UPDATE', 'Role', 'Modifier');
INSERT INTO permission (name, category, label) VALUES ('ROLE_DELETE', 'Role', 'Supprimer');
INSERT INTO permission (name, category, label) VALUES ('ROLE_PERMISSION', 'Role', 'Attribuer Permission');

INSERT INTO permission (name, category, label) VALUES ('PAGE', 'PAGE', 'Page');
INSERT INTO permission (name, category, label) VALUES ('STOCK', 'STOCK', 'Stock');


INSERT INTO permission (name, category, label) VALUES ('ARTICLE_LIST', 'Article', 'Lister');
INSERT INTO permission (name, category, label) VALUES ('ARTICLE_CREATE', 'Article', 'Ajouter');
INSERT INTO permission (name, category, label) VALUES ('ARTICLE_UPDATE', 'Article', 'Modifier');
INSERT INTO permission (name, category, label) VALUES ('ARTICLE_DELETE', 'Article', 'Supprimer');
INSERT INTO permission (name, category, label) VALUES ('ARTICLE_LOW', 'Article', 'Stock Faible');
INSERT INTO permission (name, category, label) VALUES ('ARTICLE_LARGE', 'Article', 'Stock Suffisant');
INSERT INTO permission (name, category, label) VALUES ('ARTICLE_OUTOF', 'Article', 'Stock en rupture');

INSERT INTO permission (name, category, label) VALUES ('SUPPLIER_LIST', 'Fournissseur', 'Lister');
INSERT INTO permission (name, category, label) VALUES ('SUPPLIER_CREATE', 'Fournissseur', 'Ajouter');
INSERT INTO permission (name, category, label) VALUES ('SUPPLIER_UPDATE', 'Fournissseur', 'Modifier');
INSERT INTO permission (name, category, label) VALUES ('SUPPLIER_DELETE', 'Fournissseur', 'Supprimer');
INSERT INTO permission (name, category, label) VALUES ('SUPPLIER_ARTICLE', 'Fournissseur', 'Voir Article');

INSERT INTO permission (name, category, label) VALUES ('REQUISITION_LIST', 'Requisition', 'Lister');
INSERT INTO permission (name, category, label) VALUES ('REQUISITION_CREATE', 'Requisition', 'Ajouter');
INSERT INTO permission (name, category, label) VALUES ('REQUISITION_UPDATE', 'Requisition', 'Modifier');
INSERT INTO permission (name, category, label) VALUES ('REQUISITION_DELETE', 'Requisition', 'Supprimer');

INSERT INTO permission (name, category, label) VALUES ('REQUISITION_SUBMIT', 'Requisition', 'Soumettre');
INSERT INTO permission (name, category, label) VALUES ('REQUISITION_VALIDATE', 'Requisition', 'Valider');
INSERT INTO permission (name, category, label) VALUES ('REQUISITION_REJECT', 'Requisition', 'Rejéter');
INSERT INTO permission (name, category, label) VALUES ('REQUISITION_DRAFT', 'Requisition', 'Brouiller');
INSERT INTO permission (name, category, label) VALUES ('REQUISITION_HISTORY', 'Requisition', 'Historique');
INSERT INTO permission (name, category, label) VALUES ('REQUISITION_CANCELED', 'Requisition', 'Annuler');
INSERT INTO permission (name, category, label) VALUES ('REQUISITION_IN_PROGRESS', 'Requisition', 'En cour');
INSERT INTO permission (name, category, label) VALUES ('REQUISITION_DELIVERED', 'Requisition', 'Livrer');

INSERT INTO permission (name, category, label) VALUES ('REQUISITION_SUBMIT_VIEW', 'Requisition', 'Voir Soumis');
INSERT INTO permission (name, category, label) VALUES ('REQUISITION_VALIDATE_VIEW', 'Requisition', 'Voir valider');
INSERT INTO permission (name, category, label) VALUES ('REQUISITION_REJECT_VIEW', 'Requisition', 'Voir Rejéter');
INSERT INTO permission (name, category, label) VALUES ('REQUISITION_DRAFT_VIEW', 'Requisition', 'Voir Brouiller');
INSERT INTO permission (name, category, label) VALUES ('REQUISITION_CANCELED_VIEW', 'Requisition', 'Voir Annuler');
INSERT INTO permission (name, category, label) VALUES ('REQUISITION_IN_PROGRESS_VIEW', 'Requisition', 'Voir En cour');
INSERT INTO permission (name, category, label) VALUES ('REQUISITION_DELIVERED_VIEW', 'Requisition', 'Voir Livrer');

INSERT INTO permission (name, category, label) VALUES ('DELIVERY_LIST', 'Livraison', 'Lister');
INSERT INTO permission (name, category, label) VALUES ('DELIVERY_UPDATE', 'Livraison', 'Modifier');
INSERT INTO permission (name, category, label) VALUES ('DELIVERY_DELETE', 'Livraison', 'Supprimer');

INSERT INTO permission (name, category, label) VALUES ('DELIVERY_IN_TRANSIT', 'Livraison', 'En route');
INSERT INTO permission (name, category, label) VALUES ('DELIVERY_DELIVERED', 'Livraison', 'Livrer');

INSERT INTO permission (name, category, label) VALUES ('DELIVERY_IN_TRANSIT_VIEW', 'Livraison', 'Voir En route');
INSERT INTO permission (name, category, label) VALUES ('DELIVERY_PREPARATION_VIEW', 'Livraison', 'Voir En Préparation');
INSERT INTO permission (name, category, label) VALUES ('DELIVERY_DELIVERED_VIEW', 'Livraison', 'Voir Livrer');


