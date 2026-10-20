package br.com.gabriel.chamados.ticket;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

import static br.com.gabriel.chamados.ticket.Ticket.*;

public final class TicketDtos {
    private TicketDtos() { }

    public record CreateRequest(
            @NotBlank @Size(max = 120) String title,
            @NotBlank @Size(max = 3000) String description,
            @NotBlank @Email @Size(max = 254) String requesterEmail,
            @NotNull Category category,
            @NotNull Impact impact,
            @NotNull Urgency urgency) { }

    public record ChangeStatusRequest(@NotNull Status status) { }

    public record TicketResponse(UUID id, String title, String description, String requesterEmail,
                                 Category category, Impact impact, Urgency urgency, Priority priority,
                                 Status status, Instant createdAt, Instant dueAt, long version) {
        public static TicketResponse from(Ticket ticket) {
            return new TicketResponse(ticket.getId(), ticket.getTitle(), ticket.getDescription(),
                    ticket.getRequesterEmail(), ticket.getCategory(), ticket.getImpact(), ticket.getUrgency(),
                    ticket.getPriority(), ticket.getStatus(), ticket.getCreatedAt(), ticket.getDueAt(), ticket.getVersion());
        }
    }

    public record NotificationRequest(UUID ticketId, String requesterEmail, String title, String status) { }
}
