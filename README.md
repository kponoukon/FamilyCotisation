# 📱 Famille Cotisation — Application Android

Application de gestion des cotisations et dépenses familiales avec export Excel.

---

## 📋 Fonctionnalités

- **👥 Membres** : Ajouter, modifier, supprimer des membres de la famille
- **💰 Cotisations** : Enregistrer les cotisations par membre, mois et année
- **📤 Dépenses** : Saisir les dépenses avec catégorie (Alimentation, Santé, Éducation…)
- **📊 Tableau de bord** : Solde en temps réel (Total cotisations − Total dépenses)
- **📥 Export Excel** : Génère un fichier `.xlsx` avec 4 feuilles :
  - **Résumé** — indicateurs clés et solde
  - **Membres** — liste complète
  - **Cotisations** — historique avec totaux
  - **Dépenses** — historique avec totaux

---

## 🛠️ Compilation de l'APK

### Prérequis
- **Android Studio** (version 2022.3 ou plus récente) — [Télécharger](https://developer.android.com/studio)
- **JDK 11 ou 17** (inclus avec Android Studio)
- Connexion internet (pour télécharger les dépendances Gradle la première fois)

### Étapes

1. **Ouvrir le projet**
   - Lancer Android Studio
   - `File > Open` → sélectionner le dossier `FamilyCotisation`
   - Attendre la synchronisation Gradle (quelques minutes la première fois)

2. **Compiler le Debug APK**
   - Menu : `Build > Build Bundle(s) / APK(s) > Build APK(s)`
   - Ou en ligne de commande :
     ```bash
     ./gradlew assembleDebug
     ```
   - L'APK sera généré dans :
     ```
     app/build/outputs/apk/debug/app-debug.apk
     ```

3. **Installer sur Android**
   - Connecter le téléphone en USB avec le **débogage USB activé**
   - Ou copier l'APK sur le téléphone et l'ouvrir (autoriser les sources inconnues)

### Compilation Release (APK signé)
```bash
./gradlew assembleRelease
```
> Nécessite une keystore. Voir : https://developer.android.com/studio/publish/app-signing

---

## 📂 Structure du projet

```
FamilyCotisation/
├── app/
│   ├── src/main/
│   │   ├── java/com/famille/cotisation/
│   │   │   ├── App.java                 # Application + DB init
│   │   │   ├── DatabaseHelper.java      # SQLite (membres, cotisations, dépenses)
│   │   │   ├── ExcelExporter.java       # Export .xlsx avec Apache POI
│   │   │   ├── MainActivity.java        # Tableau de bord
│   │   │   ├── MembresActivity.java     # Gestion membres
│   │   │   ├── CotisationsActivity.java # Gestion cotisations
│   │   │   ├── DepensesActivity.java    # Gestion dépenses
│   │   │   ├── RapportActivity.java     # Rapport + bouton export
│   │   │   ├── Membre.java / Cotisation.java / Depense.java
│   │   │   ├── MembresAdapter.java / CotisationsAdapter.java / DepensesAdapter.java
│   │   └── res/
│   │       ├── layout/                  # Tous les écrans XML
│   │       ├── values/                  # Couleurs, thème, strings
│   │       └── xml/file_paths.xml       # FileProvider (partage fichier)
│   └── build.gradle                     # Dépendances (Apache POI, Material UI…)
└── build.gradle / settings.gradle
```

---

## 📦 Dépendances principales

| Bibliothèque | Usage |
|---|---|
| `androidx.appcompat` | Compatibilité Android |
| `material` | Boutons, CardView, FAB |
| `recyclerview` | Listes scrollables |
| `apache poi 5.2.3` | Génération fichiers Excel .xlsx |
| `multidex` | Support des nombreuses méthodes (POI) |

---

## 📁 Où trouver le fichier Excel exporté ?

Le fichier `.xlsx` est sauvegardé dans :
```
Android/data/com.famille.cotisation/files/
```
Et partageable directement via WhatsApp, Gmail, Drive, etc.

---

## ✅ Compatibilité

- Android **7.0 (API 24)** et versions supérieures
- Testé jusqu'à Android 14 (API 34)
