package camix.service.parameterized;

import camix.communication.ProtocoleChat;

/**
 * Classe fournissant des données de test pour les tests unitaires de ClientChat (EnumSource)
 */
@SuppressWarnings("unused")
public enum Todo5ClientChatTestEnumData {
    TestData0("Arthur", "Kévin"),
    TestData1("Nicolas", "Jean"),
    TestData2("Jean", "Nicolas"),
    TestData3("LeSuperSurnom", "LeSurnomSuper");

    final String initialSurnom;
    final String newSurnom;
    final String message;

    Todo5ClientChatTestEnumData(String initialSurnom, String newSurnom) {
        this.initialSurnom = initialSurnom;
        this.newSurnom = newSurnom;
        this.message = String.format(ProtocoleChat.MESSAGE_CHANGEMENT_SURNOM, initialSurnom, newSurnom);
    }

    public String getInitialSurnom() {
        return initialSurnom;
    }

    public String getNewSurnom() {
        return newSurnom;
    }

    public String getMessage() {
        return message;
    }
}