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

${IP_DEFAULT}            127.0.0.1
${PORT_DEFAULT}          12345
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
    la vue connexion est affichée


l'${utilisateur} ferme le client chat
    Switch To Application    ${utilisateur}
    System Exit


plusieurs ${utilisateurs} ferment leur client chat
    FOR    ${utilisateur}    IN    @{utilisateurs}
        l'${utilisateur} ferme le client chat
    END

#
# Keywords pour modifier l'ip et le port, et demander à se connecter.
#

l'${utilisateur} modifie l'adresse ${ip}
    Switch To Application    ${utilisateur}
    Select Window    Connexion
    Focus To Component    ip
    Clear Text Field    ip
    Type Into Text Field    ip    ${ip}

l'${utilisateur} modifie le numéro du port ${port}
    Switch To Application    ${utilisateur}
    Select Window    Connexion
    Focus To Component    port
    Clear Text Field    port
    Type Into Text Field    port    ${port}

l'${utilisateur} demande à se connecter
    Switch To Application    ${utilisateur}
    Select Window    Connexion
    Click On Component    connecter
    ${ip_value} =    Get Text Field Value    ip
    ${port_value} =    Get Text Field Value    port
    ${message_connexion} =    Format String    ${MESSAGE_INFORMATION_CONNEXION_IP_PORT}    ip=${ip_value}    port=${port_value}
    l'${utilisateur} est informé ${message_connexion}
    Sleep    ${DELAI_CONNEXION}


plusieurs ${utilisateurs} sont connectés au chat (canal par défaut)
    FOR    ${utilisateur}    IN    @{utilisateurs}
        l'${utilisateur} lance le client chat
        l'${utilisateur} demande à se connecter
    END


l'${utilisateur} saisit la commande d'aide
    l'${utilisateur} envoie ${COMMANDE_AIDE}


chaque ${utilisateur} envoie un ${message}
    l'${utilisateur} envoie ${message}


#
# Asserts - Résultats observables (points des critères d'acceptation des US)
#

la vue connexion est affichée
    Window Should Be Open    Connexion
    Select Window    Connexion
    Component Should Be Visible    ip
    Component Should Be Visible    port
    Component Should Be Visible    connecter
    Component Should Be Visible    information
    ${info_message} =    Get Text Field Value    information
    Should Be Equal    ${info_message}    ${MESSAGE_INFORMATION_CONNEXION}

la vue chat de l'${utilisateur} est affichée
    [Teardown]    Run Keyword If    '${KEYWORD STATUS}' == 'FAIL'    list windows
    Switch To Application    ${utilisateur}
    Window Should Be Open    Chat
    Select Window    Chat
    Component Should Be Visible    saisie
    Component Should Be Visible    messages
    # implicite
    le champ saisie est vide

la vue chat est affichée
    Window Should Be Open    Chat
    Select Window    Chat
    Component Should Be Visible    saisie
    Component Should Be Visible    messages
    # implicite
    le champ saisie est vide

la connexion est impossible @${ip}:${port}
    ${message_connexion_impossible} =    Format String    ${MESSAGE_INFORMATION_CONNEXION_IMPOSSIBLE}    ip=${ip}    port=${port}
    Wait Until Keyword Succeeds    ${DELAI_CONNEXION}    0.5s    l'utilisateur est informé ${message_connexion_impossible}
    Window Should Be Open    Connexion
    Window Should Not Be Open    Chat

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