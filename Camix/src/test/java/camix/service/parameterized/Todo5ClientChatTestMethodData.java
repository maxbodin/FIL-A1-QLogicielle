package camix.service.parameterized;

import camix.communication.ProtocoleChat;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

/**
 * Classe fournissant des données de test pour les tests unitaires de ClientChat (MethodSource)
 */
@SuppressWarnings("unused")
public class Todo5ClientChatTestMethodData {

    /**
     * Fournit des données de test sous forme de flux d'arguments :
     * surnom initial, nouveau surnom, et message attendu
     *
     * @return Stream d'arguments pour les tests paramétrés
     */
    static Stream<Arguments> surnomTestDataProvider() {
        return Stream.of(
                Arguments.of(
                        "John",
                        "Johnny",
                        String.format(ProtocoleChat.MESSAGE_CHANGEMENT_SURNOM, "John", "Johnny")
                ),
                Arguments.of(
                        "Kate",
                        "Katie",
                        String.format(ProtocoleChat.MESSAGE_CHANGEMENT_SURNOM, "Kate", "Katie")
                ),
                Arguments.of(
                        "Sam",
                        "Samuel",
                        String.format(ProtocoleChat.MESSAGE_CHANGEMENT_SURNOM, "Sam", "Samuel")
                ),
                Arguments.of(
                        "User123",
                        "User456",
                        String.format(ProtocoleChat.MESSAGE_CHANGEMENT_SURNOM, "User123", "User456")
                ),
                Arguments.of(
                        "Guest",
                        "Member",
                        String.format(ProtocoleChat.MESSAGE_CHANGEMENT_SURNOM, "Guest", "Member")
                )
        );
    }
}