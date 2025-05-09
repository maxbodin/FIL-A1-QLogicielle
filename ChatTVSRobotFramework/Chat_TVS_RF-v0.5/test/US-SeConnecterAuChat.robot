*** Settings ***
Documentation     Tests for "Se connecter au chat" user story
Resource          SeConnecterAuChat.resource

*** Variables ***
@{autres_utilisateurs}  premier  deuxième
@{0_autres_utilisateurs}  @{EMPTY}
@{2_autres_utilisateurs}  premier  second

*** Test Cases ***
AC - Afficher vue chat
    Given le serveur chat est accessible @${IP_DEFAULT}:${PORT_DEFAULT}
    When l'utilisateur lance le client chat
    Then la vue chat est affichée
    [Teardown]    l'utilisateur ferme le client chat

AC - Se connecter au chat
    Given le serveur chat est accessible @${IP_DEFAULT}:${PORT_DEFAULT}
    And plusieurs ${autres_utilisateurs} sont connectés au chat (canal par défaut)
    When l'utilisateur lance le client chat
    Then l'utilisateur reçoit un message d'accueil
    And tous les ${autres_utilisateurs} reçoivent un message d'arrivée du nouvel utilisateur
    [Teardown]    Run Keywords    l'utilisateur ferme le client chat
        ...    AND    plusieurs ${autres_utilisateurs} ferment leur client chat

SMOKE - Afficher vue chat
    [Tags]    smoke
    SeConnecterAuChat.Afficher vue chat