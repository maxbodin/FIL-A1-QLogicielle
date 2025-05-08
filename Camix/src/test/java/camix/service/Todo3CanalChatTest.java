package camix.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.lenient;

@DisplayName("Tests unitaires de la classe CanalChat en GWT")
@ExtendWith(MockitoExtension.class)
class Todo3CanalChatTest {

    private CanalChat SUT;

    @Mock
    private ClientChat clientMock1;

    @Mock
    private ClientChat clientMock2;

    @BeforeEach
    void setUp() {
        SUT = new CanalChat("TestChannel");
        // Given: we configure the client behaviors
        lenient().when(clientMock1.donneId()).thenReturn("client1");
        lenient().when(clientMock2.donneId()).thenReturn("client2");
    }


    @Nested
    @DisplayName("GIVEN an empty CanalChat")
    class GivenEmptyCanalChat {
        @Test
        @DisplayName("WHEN a new client is added THEN it should be present")
        void ajoutClient_SuccessfullyAddsClient() {
            // GIVEN: CanalChat is empty.
            Assertions.assertEquals(0, SUT.donneNombreClients(), "Le canal doit être vide initialement");

            // WHEN: adding a new client.
            SUT.ajouteClient(clientMock1);

            // THEN: the client is present and count is updated.
            Assertions.assertTrue(SUT.estPresent(clientMock1), "Le clientMock1 devrait être présent dans le canal.");
            Assertions.assertEquals(1, SUT.donneNombreClients(), "Le canal devrait contenir un client");
            Mockito.verify(clientMock1, Mockito.atLeastOnce()).donneId();
        }


        @Nested
        @DisplayName("WHEN the same client is added twice")
        class WhenAddingSameClientTwice {

            @BeforeEach
            void addClientTwice() {
                // WHEN: adding the same client twice.
                SUT.ajouteClient(clientMock1);
                SUT.ajouteClient(clientMock1);
            }

            @Test
            @DisplayName("THEN the canal should contain only one instance of the client")
            void ajouteClient_AddingSameClientTwice_DoesNotDuplicate() {
                // THEN: client is present only once.
                Assertions.assertTrue(SUT.estPresent(clientMock1), "Le clientMock1 devrait être présent dans le canal.");
                Assertions.assertEquals(1, SUT.donneNombreClients(), "Le canal devrait contenir un client");
            }
        }

        @Nested
        @DisplayName("WHEN multiple different clients are added")
        class WhenAddingMultipleClients {

            @BeforeEach
            void addMultipleClients() {
                // WHEN: adding multiple clients.
                SUT.ajouteClient(clientMock1);
                SUT.ajouteClient(clientMock2);
            }

            @Test
            @DisplayName("THEN the canal should contain both clients")
            void ajouteClient_AddingMultipleClients_IncreasesCount() {
                // THEN: both clients are present.
                Assertions.assertTrue(SUT.estPresent(clientMock1), "clientMock1 doit être présent");
                Assertions.assertTrue(SUT.estPresent(clientMock2), "clientMock2 doit être présent");
                Assertions.assertEquals(2, SUT.donneNombreClients(), "Le canal devrait contenir deux clients");
            }
        }
    }
}