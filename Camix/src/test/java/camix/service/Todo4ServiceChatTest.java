package camix.service;

import org.junit.jupiter.api.*;
import camix.communication.ProtocoleChat;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@DisplayName("Tests unitaires de la méthode supprimeCanal dans ServiceChat")
class Todo4ServiceChatTest {

    @Nested
    @DisplayName("GIVEN a ServiceChat instance with a default channel")
    class GivenServiceChatInstance {
        // Use a default channel name for testing.
        final String defaultChannelName = "DefaultChannel";
        ServiceChat serviceChat;

        // Setup ServiceChat instance.
        {
            try {
                serviceChat = new ServiceChat(defaultChannelName);
            } catch (IOException e) {
                Assertions.fail("IOException lors de l'initialisation du ServiceChat : " + e.getMessage());
            }
        }

        @Test
        @DisplayName("WHEN attempting to remove the default channel THEN an exception is thrown")
        @Timeout(value = 2000, unit = TimeUnit.MILLISECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
        void supprimeCanal_DefaultChannelThrowsException() {
            // WHEN: Trying to remove the default channel.
            CanalChat.Exception thrown = Assertions.assertThrows(
                    CanalChat.Exception.class,
                    () -> serviceChat.supprimeCanal(defaultChannelName)
            );

            // THEN: The exception message should match the expected default channel message.
            String expectedMessage = String.format(ProtocoleChat.MESSAGE_SUPPRESSION_CANAL_PAR_DEFAUT, defaultChannelName);
            Assertions.assertEquals(expectedMessage, thrown.getMessage(),
                    "La tentative de suppression du canal par défaut doit lever une exception avec le bon message.");
        }

        @Test
        @DisplayName("WHEN attempting to remove a non-existent channel THEN an exception is thrown")
        @Timeout(value = 2000, unit = TimeUnit.MILLISECONDS, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
        void supprimeCanal_NonExistentChannelThrowsException() {
            // GIVEN: a channel name that does not exist.
            String nonExistentChannel = "CanalInexistant";

            // WHEN: Trying to remove a non-existent channel.
            CanalChat.Exception thrown = Assertions.assertThrows(
                    CanalChat.Exception.class,
                    () -> serviceChat.supprimeCanal(nonExistentChannel)
            );

            // THEN: The exception message should match the expected message for a non-existent channel.
            String expectedMessage = String.format(ProtocoleChat.MESSAGE_SUPPRESSION_CANAL_INEXISTANT, nonExistentChannel);
            Assertions.assertEquals(expectedMessage, thrown.getMessage(),
                    "La suppression d'un canal inexistant doit lever une exception avec le message approprié.");
        }
    }
}
