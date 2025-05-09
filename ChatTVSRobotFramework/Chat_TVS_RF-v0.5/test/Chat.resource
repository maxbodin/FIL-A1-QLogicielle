*** Settings ***
Documentation     Base Chat resource file
Library           Dialogs
Library           RemoteSwingLibrary

*** Variables ***
${IP_DEFAULT}    127.0.0.1
${PORT_DEFAULT}    12345

${MESSAGE_ACCUEIL}    * Taper /h pour avoir de l'aide sur les commandes du chat.\n
${MESSAGE_ARRIVEE}    * Un nouvel utilisateur est dans le chat (place publique).\n

*** Keywords ***
le serveur chat est accessible @${IP}:${port}
    Execute Manual Step    Lancez le serveur Camix sur l'adresse ${IP} et le port ${port}

l'${utilisateur} lance le client chat
    Start Application    ${utilisateur}    java -Duser.language\=fr -Duser.country\=FR -jar SUT/Felix_Java-0.5.jar stdout=/dev/null    stderr=/dev/null
    Set Jemmy Timeouts  500ms
    Switch To Application    ${utilisateur}

la vue chat est affichée
    Window Should Be Open    Chat
    Select Window    Chat
    Component Should Be Visible    saisie
    Component Should Be Visible    messages

l'${utilisateur} reçoit ${message_attendu}
    [Tags]    robot:private
    Wait Until Keyword Succeeds    2x    2s    l'${utilisateur} reçoit ${message_attendu} (sans délai)

l'${utilisateur} reçoit ${message_attendu} (sans délai)
    [Tags]    robot:private
    Switch To Application    ${utilisateur}
    Select Window    Chat
    ${message} =   Get Text Field Value   messages
    Should End With   ${message}    ${message_attendu}

l'${utilisateur} reçoit un message d'accueil
    l'${utilisateur} reçoit ${MESSAGE_ACCUEIL}

plusieurs ${autres_utilisateurs} sont connectés au chat (canal par défaut)
    FOR    ${utilisateur}  IN    @{autres_utilisateurs}
        l'${utilisateur} lance le client chat
    END

tous les ${autres_utilisateurs} reçoivent un message d'arrivée du nouvel utilisateur
    FOR  ${utilisateur}  IN  @{autres_utilisateurs}
        l'${utilisateur} reçoit ${MESSAGE_ARRIVEE}
    END

plusieurs ${autres_utilisateurs} ferment leur client chat
    FOR    ${utilisateur}  IN    @{autres_utilisateurs}
        l'${utilisateur} ferme le client chat
    END

l'${utilisateur} ferme le client chat
    Switch To Application    ${utilisateur}
    System Exit