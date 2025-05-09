#
# Test RobotFramework du l'U.S. Se connecter au chat (Felix/Camix).
#
# @version 0.6
# @author Matthias Brun
# @author Maxime Bodin
#

*** Settings ***

Documentation      User Story : Se connecter au chat
...
...                [En tant qu'] utilisateur du chat
...                [Je dois pouvoir] saisir une adresse IP et un numéro de port
...                [Et] me connecter au chat
...                [Afin d'] entrer dans le chat à l'adresse IP et au port mentionnés
...
...                Business Rules :
...                Le délai de connexion est de 3 secondes (configuration).

Resource    AC/SeConnecterAuChat.resource


*** Variables ***

@{0 autres utilisateurs}    @{EMPTY}
@{2 autres utilisateurs}    premier    deuxième


*** Test Cases ***

AC - Afficher vue chat
    Given le serveur chat est accessible @${IP_DEFAULT}:${PORT_DEFAULT}
    When l'utilisateur lance le client chat
    Then la vue chat est affichée
    [Teardown]    Run Keywords    l'utilisateur ferme le client chat

AC - Connexion au chat
    Given le serveur chat est accessible @${IP_DEFAULT}:${PORT_DEFAULT}
    And plusieurs ${2 autres utilisateurs} sont connectés au chat (canal par défaut)
    When l'utilisateur lance le client chat
    And l'utilisateur demande à se connecter
    Then l'utilisateur reçoit un message d'accueil
    And tous les ${2 autres utilisateurs} reçoivent un message d'arrivée du nouvel utilisateur
    [Teardown]    Run Keywords    l'utilisateur ferme le client chat
    ...                      AND    plusieurs ${2 autres utilisateurs} ferment leur client chat

AC - Connexion au chat avec modification IP/port
    Given le serveur chat est accessible @${IP_DEFAULT}:${PORT_DEFAULT}
    And plusieurs ${2 autres utilisateurs} sont connectés au chat (canal par défaut)
    When l'utilisateur lance le client chat
    And l'utilisateur modifie l'adresse ${IP_DEFAULT}
    And l'utilisateur modifie le numéro du port ${PORT_DEFAULT}
    And l'utilisateur demande à se connecter
    Then l'utilisateur reçoit un message d'accueil
    And tous les ${2 autres utilisateurs} reçoivent un message d'arrivée du nouvel utilisateur
    [Teardown]    Run Keywords    l'utilisateur ferme le client chat
    ...                      AND    plusieurs ${2 autres utilisateurs} ferment leur client chat

AC - Connexion impossible
    Given le serveur chat n'est pas accessible @${IP_DEFAULT}:${PORT_DEFAULT}
    When l'utilisateur lance le client chat
    And l'utilisateur demande à se connecter
    Then la connexion est impossible @${IP_DEFAULT}:${PORT_DEFAULT}
    [Teardown]    Run Keywords    l'utilisateur ferme le client chat