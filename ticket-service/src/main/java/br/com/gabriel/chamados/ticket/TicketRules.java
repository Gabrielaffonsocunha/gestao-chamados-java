package br.com.gabriel.chamados.ticket;

import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

import static br.com.gabriel.chamados.ticket.Ticket.*;

/** Centraliza as regras de prioridade, SLA e ciclo de vida dos chamados. */
public class TicketRules {
    private static final Map<Priority, Duration> SLA = new EnumMap<>(Priority.class);
    private static final Map<Status, EnumSet<Status>> TRANSITIONS = new EnumMap<>(Status.class);

    static {
        SLA.put(Priority.CRITICAL, Duration.ofHours(4));
        SLA.put(Priority.HIGH, Duration.ofHours(8));
        SLA.put(Priority.MEDIUM, Duration.ofHours(24));
        SLA.put(Priority.LOW, Duration.ofHours(48));

        TRANSITIONS.put(Status.OPEN, EnumSet.of(Status.IN_PROGRESS, Status.CANCELLED));
        TRANSITIONS.put(Status.IN_PROGRESS, EnumSet.of(Status.WAITING_USER, Status.RESOLVED, Status.CANCELLED));
        TRANSITIONS.put(Status.WAITING_USER, EnumSet.of(Status.IN_PROGRESS, Status.CANCELLED));
        TRANSITIONS.put(Status.RESOLVED, EnumSet.of(Status.CLOSED, Status.IN_PROGRESS));
        TRANSITIONS.put(Status.CLOSED, EnumSet.noneOf(Status.class));
        TRANSITIONS.put(Status.CANCELLED, EnumSet.noneOf(Status.class));
    }

    public Priority calculatePriority(Category category, Impact impact, Urgency urgency) {
        if (category == Category.SECURITY) return Priority.CRITICAL;
        if (impact == Impact.HIGH && urgency == Urgency.HIGH) return Priority.CRITICAL;
        if (impact == Impact.HIGH || urgency == Urgency.HIGH) return Priority.HIGH;
        if (impact == Impact.LOW && urgency == Urgency.LOW) return Priority.LOW;
        return Priority.MEDIUM;
    }

    public Instant calculateDueAt(Priority priority, Instant createdAt) {
        return createdAt.plus(SLA.get(priority));
    }

    public void validateTransition(Status current, Status target) {
        if (current == target) return;
        if (!TRANSITIONS.get(current).contains(target)) {
            throw new BusinessRuleException("Transição de " + current + " para " + target + " não permitida.");
        }
    }

    public static class BusinessRuleException extends RuntimeException {
        public BusinessRuleException(String message) { super(message); }
    }
}
