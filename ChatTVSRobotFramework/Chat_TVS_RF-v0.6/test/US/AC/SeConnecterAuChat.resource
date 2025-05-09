#
# Ressource RobotFramework pour le test de l'U.S. Se connecter au chat (Felix/Camix).
#
# Les critères d'acceptation (acceptance criteria) sont fournis sous formes de keywords.
# Les points de ces keywords (également des keywords) sont définis dans les
# ressources propres aux technologies d'implémentation (java ou android)
# (cf. SUT/).
#
# Fournit également les keywords nécessaires à d'autres critères d'acceptation
# dont les pré-conditions (given) dépendent de l'U.S. "Se connecter au chat".
#
# @version 0.6
# @author Matthias Brun
# @author Maxime Bodin
#

*** Settings ***

Resource    ../SUT/SUT.resource


*** Variables ***
@{autres_utilisateurs}  premier  deuxième

*** Keywords ***

### Critères d'acceptation ###

*** Test Cases ***

Afficher vue chat
    Given le serveur chat est accessible @${IP_DEFAULT}:${PORT_DEFAULT}
    When l'utilisateur lance le client chat
    Then la vue chat est affichée
    [Teardown]    Run Keywords  l'utilisateur ferme le client chat

Connexion au chat
    Given le serveur chat est accessible @${IP_DEFAULT}:${PORT_DEFAULT}
    And plusieurs ${autres_utilisateurs} sont connectés au chat (canal par défaut)
    When l'utilisateur lance le client chat
    Then l'utilisateur reçoit un message d'accueil
    And tous les ${autres_utilisateurs} reçoivent un message d'arrivée du nouvel utilisateur
    [Teardown]    Run Keywords  L'utilisateur Ferme Le Client Chat
    ...                    AND  Plusieurs ${autres_utilisateurs} Ferment Leur Client Chat
    [Arguments]    ${autres_utilisateurs}
