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
