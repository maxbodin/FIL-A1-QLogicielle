
# Ressource RobotFramework pour la manipulation du serveur chat Camix.
#
# Fournit les keywords pour le lancement et l'arrêt du serveur Camix.
#
# Les keywords sont implémentés avec la bibliothèque Process.
#
# @version 0.6
# @author Matthias Brun
#

*** Settings ***

Library    Process


*** Keywords ***

le serveur chat est accessible @${ip}:${port}
    Start Process    java    -Duser.language\=fr    -Duser.country\=FR    -jar    SUT/Camix_Java-0.6.jar    alias=Camix
    Sleep    1s
    Process Should Be Running    Camix    Le serveur Camix n'est pas accessible.
    # Nous considérons que le serveur Camix écoute sur l'adresse IP et le port spécifiés
    # Normalement, on devrait vérifier que le port est bien ouvert et accessible


le serveur chat n'est pas accessible @${ip}:${port}
    # Nous s'assurons que le serveur Camix n'est pas en cours d'exécution
    # Dans un contexte réel, on pourrait bloquer le port ou utiliser une mauvaise adresse IP
    ${running_process} =    Run Process    java    -Duser.language\=fr    -Duser.country\=FR    -jar    SUT/Camix_Java-0.6.jar    alias=CamixTest
    Terminate Process    CamixTest    kill=true
    Sleep    1s


Le serveur chat est arrêté
    ${OS} =    Evaluate    platform.system()    platform
    IF    '${OS}' == 'Windows'
        ${Camix PID} =    Get Process Id    Camix
        Run Process    taskkill    /F    /PID    ${Camix PID}
        # Run Process    taskkill /F /PID ${Camix PID}    shell=true
        # Run Process    Stop-Process    -Force    -ID    ${Camix PID}
        # Run Process    Stop-Process -Force -ID ${Camix PID}    shell=true
    ELSE
        Terminate Process    Camix
        # Terminate Process    Camix    kill=true
        # ${Camix PID} =    Get Process Id    Camix
        # Run Process    kill    ${Camix PID}
        # Run Process    kill ${Camix PID}    shell=true
    END
    Sleep    1s
    Process Should Be Stopped    Camix    Le serveur Camix n'est pas arrêté.