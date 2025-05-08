package camix.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("Tests unitaires de la classe CanalChat")
@ExtendWith(MockitoExtension.class)
class Todo1Todo2CanalChatTest {

    private CanalChat SUT;

    @Mock
    private ClientChat clientMock1;

    @Mock
    private ClientChat clientMock2;

    @BeforeEach
    public void setUp() {
        SUT = new CanalChat("TestChannel");

        // Mocking client behavior.
        Mockito.lenient().when(clientMock1.donneId()).thenReturn("client1");
        Mockito.lenient().when(clientMock2.donneId()).thenReturn("client2");
    }

    @Test
    @DisplayName("Ajout client non présent")
    void ajouteClient_SuccessfullyAddsClient() {
        Assertions.assertEquals(0, SUT.donneNombreClients());
        SUT.ajouteClient(clientMock1);

        // Verify that the client has been added.
        Assertions.assertTrue(SUT.estPresent(clientMock1), "Le clientMock1 devrait être présent dans le canal.");
        Assertions.assertEquals(1, SUT.donneNombreClients(), "Le canal devrait contenir un client");

        Mockito.verify(clientMock1, Mockito.atLeastOnce()).donneId();
    }

    @Test
    @DisplayName("Ajout client déjà présent")
    void ajouteClient_AddingSameClientTwice_DoesNotDuplicate() {
        Assertions.assertEquals(0, SUT.donneNombreClients());
        SUT.ajouteClient(clientMock1);
        Assertions.assertTrue(SUT.estPresent(clientMock1), "Le clientMock1 devrait être présent dans le canal.");

        SUT.ajouteClient(clientMock1);
        // Verify client count remains 1.
        Assertions.assertEquals(1, SUT.donneNombreClients(), "Le canal devrait contenir un client");
    }

    @Test
    @DisplayName("Ajout plusieurs clients non présent")
    void ajouteClient_AddingMultipleClients_IncreasesCount() {
        SUT.ajouteClient(clientMock1);
        SUT.ajouteClient(clientMock2);

        // Verify both clients are present.
        Assertions.assertTrue(SUT.estPresent(clientMock1));
        Assertions.assertTrue(SUT.estPresent(clientMock2));
        Assertions.assertEquals(2, SUT.donneNombreClients());
    }
}