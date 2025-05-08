package camix.service.parameterized;

import camix.communication.ProtocoleChat;
import camix.service.CanalChat;
import camix.service.ClientChat;
import camix.service.ServiceChat;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@ExtendWith(MockitoExtension.class)
class Todo5ClientChatTest {

    private static final String METHOD_NAME = "changeSurnom";
    private static final String ATTRIBUTE_NAME = "surnom";

    private ClientChat clientChat;

    @Mock
    private ServiceChat serviceChat;

    @Mock
    private CanalChat canalChat;

    @BeforeEach
    void setUp() {
        // Configuration du mock pour la méthode envoieClients
        Mockito.doNothing().when(canalChat).envoieClients(ArgumentMatchers.anyString());
    }

    @Test
    @DisplayName("Test standard de changement de surnom")
    void testChangeSurnom() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        String ancienSurnom = "surnom";
        String newSurnom = "newSurnom";

        clientChat = new ClientChat(serviceChat, "id", ancienSurnom, canalChat);

        Field surnomValue = ClientChat.class.getDeclaredField(ATTRIBUTE_NAME);
        surnomValue.setAccessible(true);

        Assumptions.assumeTrue(surnomValue.get(clientChat) == ancienSurnom);

        Method method = ClientChat.class.getDeclaredMethod(METHOD_NAME, String.class);
        method.setAccessible(true);
        method.invoke(clientChat, newSurnom);

        Assertions.assertEquals(newSurnom, surnomValue.get(clientChat), "surnom change fail");

        String message = String.format(ProtocoleChat.MESSAGE_CHANGEMENT_SURNOM, ancienSurnom, newSurnom);
        Mockito.verify(canalChat, Mockito.atLeastOnce()).envoieClients(message);
    }

    @Nested
    @DisplayName("Tests paramétrés avec EnumSource")
    class ClientChatTestEnumSource {
        @ParameterizedTest
        @EnumSource
        @DisplayName("Test paramétré de changement de surnom avec EnumSource")
        void testChangeSurnomWithEnumSource(Todo5ClientChatTestEnumData params) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
            // Les données de test proviennent de l'enum.
            String initialSurnom = params.initialSurnom;
            String newSurnom = params.newSurnom;
            String expectedMessage = params.message;

            // Instanciation du SUT.
            clientChat = new ClientChat(serviceChat, "id", initialSurnom, canalChat);

            // Vérification de l'état initial.
            Field surnomValue = ClientChat.class.getDeclaredField(ATTRIBUTE_NAME);
            surnomValue.setAccessible(true);
            Assumptions.assumeTrue(surnomValue.get(clientChat).equals(initialSurnom));

            // Appel de la méthode à tester par réflexion.
            Method method = ClientChat.class.getDeclaredMethod(METHOD_NAME, String.class);
            method.setAccessible(true);
            method.invoke(clientChat, newSurnom);

            // Assertions sur le résultat.
            Assertions.assertEquals(newSurnom, surnomValue.get(clientChat), "Le changement de surnom a échoué");
            Mockito.verify(canalChat, Mockito.atLeastOnce()).envoieClients(expectedMessage);
        }
    }

    @Nested
    @DisplayName("Tests paramétrés avec MethodSource")
    class ClientChatTestMethodSource {
        @ParameterizedTest
        @MethodSource("camix.service.parameterized.Todo5ClientChatTestMethodData#surnomTestDataProvider")
        @DisplayName("Test paramétré de changement de surnom avec MethodSource")
        void testChangeSurnomWithMethodSource(String initialSurnom, String newSurnom, String expectedMessage) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
            // Les données de test proviennent de la méthode source.

            // Instanciation du SUT.
            clientChat = new ClientChat(serviceChat, "id", initialSurnom, canalChat);

            // Vérification de l'état initial.
            Field surnomValue = ClientChat.class.getDeclaredField(ATTRIBUTE_NAME);
            surnomValue.setAccessible(true);
            Assumptions.assumeTrue(surnomValue.get(clientChat).equals(initialSurnom));

            // Appel de la méthode à tester par réflexion.
            Method method = ClientChat.class.getDeclaredMethod(METHOD_NAME, String.class);
            method.setAccessible(true);
            method.invoke(clientChat, newSurnom);

            // Assertions sur le résultat.
            Assertions.assertEquals(newSurnom, surnomValue.get(clientChat), "Le changement de surnom a échoué");
            Mockito.verify(canalChat, Mockito.atLeastOnce()).envoieClients(expectedMessage);
        }
    }
}