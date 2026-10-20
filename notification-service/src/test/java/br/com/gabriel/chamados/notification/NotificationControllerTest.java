package br.com.gabriel.chamados.notification;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NotificationControllerTest {
    @Test
    void acceptsAndListsNotification() {
        NotificationController controller = new NotificationController();
        UUID ticketId = UUID.randomUUID();
        var result = controller.receive(new NotificationController.NotificationRequest(
                ticketId, "gabriel@example.com", "VPN indisponível", "OPEN"));

        assertEquals(ticketId, result.ticketId());
        assertEquals(1, controller.list().size());
        assertEquals("OPEN", controller.list().getFirst().status());
    }
}
