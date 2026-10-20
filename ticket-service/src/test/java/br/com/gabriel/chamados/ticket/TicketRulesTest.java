package br.com.gabriel.chamados.ticket;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static br.com.gabriel.chamados.ticket.Ticket.*;
import static org.junit.jupiter.api.Assertions.*;

class TicketRulesTest {
    private final TicketRules rules = new TicketRules();

    @Test
    void securityTicketsAreAlwaysCritical() {
        assertEquals(Priority.CRITICAL, rules.calculatePriority(Category.SECURITY, Impact.LOW, Urgency.LOW));
    }

    @Test
    void highImpactAndUrgencyProduceFourHourSla() {
        Priority priority = rules.calculatePriority(Category.HARDWARE, Impact.HIGH, Urgency.HIGH);
        Instant created = Instant.parse("2026-01-01T10:00:00Z");
        assertEquals(Priority.CRITICAL, priority);
        assertEquals(created.plus(Duration.ofHours(4)), rules.calculateDueAt(priority, created));
    }

    @Test
    void lowImpactAndUrgencyProduceFortyEightHourSla() {
        Priority priority = rules.calculatePriority(Category.SOFTWARE, Impact.LOW, Urgency.LOW);
        assertEquals(Priority.LOW, priority);
        assertEquals(Duration.ofHours(48), Duration.between(Instant.EPOCH, rules.calculateDueAt(priority, Instant.EPOCH)));
    }

    @Test
    void ticketCannotBeClosedBeforeResolution() {
        assertThrows(TicketRules.BusinessRuleException.class,
                () -> rules.validateTransition(Status.IN_PROGRESS, Status.CLOSED));
    }

    @Test
    void resolvedTicketCanBeReopenedForFurtherWork() {
        assertDoesNotThrow(() -> rules.validateTransition(Status.RESOLVED, Status.IN_PROGRESS));
    }

    @Test
    void closedTicketIsTerminal() {
        assertThrows(TicketRules.BusinessRuleException.class,
                () -> rules.validateTransition(Status.CLOSED, Status.IN_PROGRESS));
    }
}
