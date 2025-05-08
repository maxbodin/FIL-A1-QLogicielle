# FIL-A1-QLogicielle
**Author : BODIN Maxime**

## Tests Unitaires du Projet Camix

Ce document résume les tests unitaires JUnit5/Mockito implémentés pour le projet Camix, une application de chat.

### Test 1: Ajout d'un client dans un canal

Test unitaire validant le comportement de l'ajout d'un client dans un canal (`CanalChat`).

### Test 2: Scénario d'ajout avec classes imbriquées

Test structuré en utilisant des classes `@Nested` pour implémenter le scénario suivant selon l'approche GWT (Given-When-Then):
- **Given**: Un `CanalChat` vide à sa création
- **When**: Ajout d'un client
- **Then**: Le client est seul dans le canal
- **When**: Ajout à nouveau du même client
- **Then**: Le client est toujours seul dans le canal

### Test 3: Gestion de la dépendance hiérarchique

Extension de l'approche GWT avec la gestion de dépendance hiérarchique via `@SkipOnFailuresInEnclosingClass`.

### Test 4: Test d'exception

Test validant la levée d'exception lors d'une tentative de suppression du canal par défaut dans la classe `ServiceChat`.

### Test 5: Test paramétré pour le changement de surnom

Test paramétré utilisant `@EnumSource` et `@MethodSource` pour valider le changement de surnom d'un client (`ClientChat`) avec différentes entrées.