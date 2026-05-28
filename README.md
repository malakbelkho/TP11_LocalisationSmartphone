# GeoPulse Tracker – TP 11

Application Android permettant de récupérer la localisation d’un smartphone ou d’un émulateur, d’afficher les coordonnées détectées, puis de les envoyer vers un serveur distant PHP/MySQL à l’aide d’une requête HTTP POST.

## Objectif:

Le but de ce laboratoire est de :

- Récupérer la latitude et la longitude d’un appareil Android
- Comprendre l’utilisation des permissions de localisation
- Envoyer des données depuis Android vers un serveur PHP
- Enregistrer les coordonnées dans une base de données MySQL
- Structurer un mini-projet mobile connecté à un backend
- Tester le serveur avec REST Client avant la connexion avec l’application mobile

## Description de l’application:

L’application **GeoPulse Tracker** permet de détecter une position GPS et de synchroniser les informations vers une base de données MySQL.

L’application affiche :

- La latitude
- La longitude
- L’altitude
- La précision GPS
- La date de capture
- L’identifiant de l’appareil
- L’état de synchronisation avec le serveur

Les données sont ensuite envoyées vers un script PHP qui les insère dans la table `position`.

## Fonctionnalités:

- Récupération de la position GPS avec `LocationManager`
- Demande des permissions Android au démarrage
- Affichage dynamique des coordonnées dans l’interface
- Génération d’un identifiant d’appareil basé sur `ANDROID_ID`
- Envoi des données vers le serveur avec Volley
- Réception d’une réponse serveur après synchronisation
- Insertion des coordonnées dans MySQL
- Test du backend avec REST Client
- Interface personnalisée avec :
  - Fond en dégradé
  - Carte arrondie pour les coordonnées
  - Badge d’état GPS
  - Bouton de synchronisation moderne

## Technologies utilisées:

- Android Studio
- Java
- XML
- Volley
- PHP
- MySQL
- XAMPP
- phpMyAdmin
- REST Client
- API minimum : 24

## Architecture générale:<img width="748" height="544" alt="create-database" src="https://github.com/user-attachments/assets/38ad0efc-8a92-4fb1-b352-6bddb3511b75" />


Le projet est divisé en deux parties principales :

### Partie mobile

L’application Android :

- Demande l’accès à la localisation
- Récupère les coordonnées GPS
- Affiche les informations détectées
- Prépare une requête HTTP POST
- Envoie les données vers le serveur PHP

### Partie serveur

Le serveur PHP/MySQL :

- Reçoit les données envoyées par l’application
- Crée un objet représentant la position
- Utilise une classe de connexion PDO
- Insère les données dans la table `position`

## Aperçu de l’application:

▶️ Une démonstration vidéo complète est disponible dans le dossier **Demo** du repository.

⚠️ En cas de problème de lecture :

👉 [▶️ Voir la démo sur Google Drive](https://)



## Captures du serveur et des tests:

Les captures suivantes montrent la création de la base de données, la vérification de la table MySQL et le test du script PHP avec REST Client.

<p align="center">
  <img width="30%" alt="Création de la base de données" src="https://github.com/user-attachments/assets/23821d12-c88c-4666-84ad-f476b9c137d3" />
  <img width="30%" alt="Base localisation dans phpMyAdmin" src="https://github.com/user-attachments/assets/c6b32431-4ea9-4452-8252-6bfb03156f65" />
  <img width="30%" alt="Test avec REST Client" src="https://github.com/user-attachments/assets/cce8d6da-2298-4fad-b161-986c3af04a16" />
</p>

### Création de la base de données

La base de données `localisation` est créée avec une table `position` destinée à stocker les coordonnées envoyées par l’application Android.

### Vérification dans phpMyAdmin

La table `position` contient les champs suivants :

- `id`
- `latitude`
- `longitude`
- `date_position`
- `imei`

Chaque nouvelle synchronisation depuis l’application ajoute une nouvelle ligne dans cette table.

### Test avec REST Client

Avant de connecter l’application Android au serveur, le script PHP est testé avec REST Client afin de vérifier que les paramètres POST sont bien reçus et enregistrés dans MySQL.

## Structure du projet serveur:

```txt
geopulse_api/
│
├── model/
│   └── GeoPoint.php
│
├── config/
│   └── DatabaseLink.php
│
├── contract/
│   └── CrudContract.php
│
├── repository/
│   └── GeoPointRepository.php
│
├── syncPosition.php
└── test_geopulse.http
```
## Description des fichiers Android:

### AndroidManifest.xml

Déclare les permissions nécessaires au fonctionnement de l’application :

- `ACCESS_FINE_LOCATION`
- `ACCESS_COARSE_LOCATION`
- `INTERNET`

Il active aussi le trafic HTTP local avec :

```xml
android:usesCleartextTraffic="true"
```

Cette configuration est nécessaire pour communiquer avec un serveur local XAMPP en HTTP.

### MainActivity.java

Classe principale de l’application.

Elle permet de :

- Initialiser l’interface
- Demander les permissions GPS
- Récupérer la position avec `LocationManager`
- Afficher les coordonnées détectées
- Préparer les données à envoyer au serveur
- Envoyer la requête HTTP POST avec Volley
- Afficher l’état de synchronisation
- Recevoir et afficher la réponse du serveur

### activity_main.xml

Interface principale de l’application.

Elle contient :

- Le titre de l’application
- Un badge d’état GPS
- Une carte affichant les coordonnées
- Un bouton de synchronisation
- Une zone affichant la réponse serveur

### colors.xml

Contient la palette de couleurs utilisée pour personnaliser l’application.

La palette repose sur des tons modernes :

- Bleu nuit
- Indigo
- Bleu électrique
- Vert aqua
- Blanc doux
- Texte gris clair

### res/drawable

Contient les fichiers de design XML utilisés pour rendre l’interface plus moderne :

- `bg_geopulse_screen.xml` : fond principal en dégradé
- `bg_location_card.xml` : carte arrondie pour les coordonnées
- `bg_status_chip.xml` : badge d’état GPS
- `bg_sync_button.xml` : bouton de synchronisation en dégradé

## Configuration de l’URL serveur:

Pour tester avec REST Client sur le PC :

```txt
http://localhost/geopulse_api/syncPosition.php
```

Pour tester depuis l’émulateur Android :

```java
private static final String SERVER_URL =
        "http://10.0.2.2/geopulse_api/syncPosition.php";
```

Pour tester depuis un vrai téléphone connecté au même Wi-Fi que le PC :

```java
private static final String SERVER_URL =
        "http://ADRESSE_IP_DU_PC/geopulse_api/syncPosition.php";
```

Exemple :

```java
private static final String SERVER_URL =
        "http://192.168.1.20/geopulse_api/syncPosition.php";
```

## Étapes de test:

### 1. Lancer le serveur local

Démarrer XAMPP :

- Apache
- MySQL

### 2. Vérifier la base de données

Ouvrir phpMyAdmin :

```txt
http://localhost/phpmyadmin
```

Vérifier que la base `localisation` et la table `position` existent.

### 3. Tester le script PHP

Envoyer une requête POST avec REST Client vers :

```txt
http://localhost/geopulse_api/syncPosition.php
```

Vérifier que la réponse contient :

```json
"success": true
```

### 4. Lancer l’application Android

Exécuter l’application sur un émulateur ou un smartphone.

### 5. Simuler ou récupérer une position GPS

Sur émulateur, utiliser :

```txt
Extended Controls > Location
```

Exemple de coordonnées :

```txt
Latitude : 31.6295
Longitude : -7.9811
```

### 6. Synchroniser les données

Cliquer sur :

```txt
Synchroniser maintenant
```

### 7. Vérifier l’insertion

Retourner dans phpMyAdmin et exécuter :

```sql
SELECT * FROM `position` ORDER BY `id` DESC;
```

Une nouvelle ligne doit apparaître avec les coordonnées envoyées depuis l’application.

## Résultat obtenu:

À la fin du TP, l’application permet de :

- Détecter une position GPS
- Afficher les coordonnées sur l’écran
- Envoyer les données au serveur PHP
- Recevoir une réponse de confirmation
- Enregistrer la position dans MySQL

## Remarques:

- Sur l’émulateur Android, l’adresse du PC est `10.0.2.2`
- Sur un vrai téléphone, il faut utiliser l’adresse IP locale du PC
- Le téléphone et le PC doivent être connectés au même réseau Wi-Fi
- Apache et MySQL doivent être lancés avant le test
- Le GPS doit être activé pour récupérer une position réelle
- REST Client permet de vérifier le backend avant de tester l’application mobile

## Conclusion:

Ce laboratoire permet de réaliser une application mobile connectée à un backend PHP/MySQL.

Il combine plusieurs notions importantes :

- Permissions Android
- Localisation GPS
- Communication HTTP avec Volley
- Traitement de données côté serveur
- Connexion PDO à MySQL
- Insertion dans une base de données
- Test d’API avec REST Client
- Interface Android personnalisée

GeoPulse Tracker montre ainsi le fonctionnement complet d’un mini-système de géolocalisation connecté, depuis la récupération des coordonnées sur Android jusqu’à leur stockage dans une base de données distante.
