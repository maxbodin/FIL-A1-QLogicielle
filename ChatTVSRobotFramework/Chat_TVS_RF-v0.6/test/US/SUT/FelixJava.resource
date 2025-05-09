#
# Ressource RobotFramework pour la manipulation du client chat Felix.
#
# Version pour l'implémentation Java de Felix.
#
# Fournit :
#      - les variables pour la configuration par défaut (IP/port)
#      - les variables pour les messages dans l'IHM
#      - les keywords pour le lancement et l'arrêt de Felix
#      - les keywords des points des critères d'acceptation des spécifications du Chat
#
# Les keywords sont implémentés avec l'API de la bibliothèque RemoteSwingLibrary.
# La construction de certaines chaînes de caractères est assurée par la bibliothèque String.
#
# @version 0.6
# @author Matthias Brun
# @author Maxime Bodin
#

*** Settings ***

Library    RemoteSwingLibrary
Library    String


*** Variables ***

${IP_DEFAUT}            127.0.0.1
${PORT_DEFAUT}          12345
${DELAI_CONNEXION}      3s

${MESSAGE_INFORMATION_CONNEXION}               Saisir l'adresse et le port du serveur chat.
${MESSAGE_INFORMATION_CONNEXION_IP_PORT}       Connexion au chat \@${EMPTY}{ip}:{port}
${MESSAGE_INFORMATION_CONNEXION_IMPOSSIBLE}    Connexion au chat \@${EMPTY}{ip}:{port} impossible.

${MESSAGE_ACCUEIL}      * Taper /h pour avoir de l'aide sur les commandes du chat.\n
${MESSAGE_ARRIVEE}      * Un nouvel utilisateur est dans le chat (place publique).\n
${MESSAGE_AIDE}         SEPARATOR=\n
...                     * Commandes disponibles :
...                     \ \ \ /n : changer de surnom
...                     \ \ \ /c : changer de canal
...                     \ \ \ /l : afficher les canaux
...                     \ \ \ /a : créer un canal
...                     \ \ \ /r : supprimer un canal
...                     \ \ \ /? : afficher ses informations
...                     \ \ \ /h : afficher l'aide\n

${MESSAGE_TEXTUEL}      {surnom} > {message}\n

${COMMANDE_AIDE}        /h


*** Keywords ***

#
# Actions (points des critères d'acceptation des US)
#

l'${utilisateur} lance le client chat
    Start Application    ${utilisateur}    java -Duser.language\=fr -Duser.country\=FR -jar SUT/Felix_Java-0.6.jar
    ...                  stdout=/dev/null    stderr=/dev/null
    Set Jemmy Timeouts    2
    Switch To Application    ${utilisateur}


l'${utilisateur} ferme le client chat
    Switch To Application    ${utilisateur}
    System Exit


plusieurs ${utilisateurs} ferment leur client chat
    FOR    ${utilisateur}    IN    @{utilisateurs}
        l'${utilisateur} ferme le client chat
    END

#
# TODO keywords pour modifier l'ip et le port, et demander à se connecter.
# TIPS : Utilisation de RemoteSwingLibrary
#

l'${utilisateur} saisit la commande d'aide
    l'${utilisateur} envoie ${COMMANDE_AIDE}


chaque ${utilisateur} envoie un ${message}
    l'${utilisateur} envoie ${message}


#
# Asserts - Résultats observables (points des critères d'acceptation des US)
#

la vue chat de l'${utilisateur} est affichée
    [Teardown]    Run Keyword If    '${KEYWORD STATUS}' == 'FAIL'    list windows
    Switch To Application    ${utilisateur}
    Window Should Be Open    Chat
    Select Window    Chat
    Component Should Be Visible    saisie
    Component Should Be Visible    messages
    # implicite
    le champ saisie est vide


#
# TODO keywords pour valider les affichages de vues et de messages.
# TIPS : Utilisation de RemoteSwingLibrary
#        Utilisation de BuiltIn (https://robotframework.org/robotframework/latest/libraries/BuiltIn.html)
#          -> Should be equal as strings, Should be equal as numbers, Should be equal, Sleep
#        Utilisation de String (https://robotframework.org/robotframework/latest/libraries/String.html)
#          -> Format String (avec MESSAGE_INFORMATION_CONNEXION_IP_PORT et MESSAGE_INFORMATION_CONNEXION_IMPOSSIBLE, cf. Variables)
#
#        + cf. variable DELAI_CONNEXION
#        + cf. keyword "l'${utilisateur} est informé ${message_attendu}" (Asserts utiles)
#


l'${utilisateur} reçoit un message d'accueil
    l'${utilisateur} reçoit ${MESSAGE_ACCUEIL}


tous les ${utilisateurs} reçoivent un message d'arrivée du nouvel utilisateur
    FOR    ${utilisateur}    IN    @{utilisateurs}
        l'${utilisateur} reçoit ${MESSAGE_ARRIVEE}
    END


l'${utilisateur} reçoit un message d'aide sur les commandes du chat
    l'${utilisateur} reçoit ${MESSAGE_AIDE}


tous les ${utilisateurs} reçoivent le ${message} transmis
    FOR    ${utilisateur}    IN    @{utilisateurs}
        ${message_attendu} =    Format String    ${MESSAGE_TEXTUEL}    surnom=?    message=${message}
        l'${utilisateur} reçoit ${message_attendu}
    END

#
# Actions utiles
#

l'${utilisateur} envoie ${message}
    [Tags]    robot:private
    Switch To Application    ${utilisateur}
    Select Window    Chat
    Focus To Component    saisie
    Type Into Text Field    saisie    ${message}
    Sleep    1s
    Type Into Text Field    saisie    \n


#
# Asserts utiles
#

l'${utilisateur} reçoit ${message_attendu}
    [Tags]    robot:private
    Wait Until Keyword Succeeds    2x    2s    l'${utilisateur} reçoit ${message_attendu} (sans délai)

l'${utilisateur} reçoit ${message_attendu} (sans délai)
    [Tags]    robot:private
    Switch To Application    ${utilisateur}
    Select Window    Chat
    ${messages} =    Get Text Field Value    messages
    Should End With    ${messages}    ${message_attendu}


le champ saisie est vide
    [Tags]    robot:private
    Select Window    Chat
    ${saisie} =    Get Text Field Value    saisie
    Should Be Empty    ${saisie}


l'${utilisateur} est informé ${message_attendu}
    [Tags]    robot:private
    Switch To Application    ${utilisateur}
    Select Window    Connexion
    ${message} =    Get Text Field Value    information
    Should Be Equal    ${message}    ${message_attendu}

