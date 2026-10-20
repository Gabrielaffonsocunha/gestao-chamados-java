package br.com.gabriel.chamados.ticket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static br.com.gabriel.chamados.ticket.Ticket.*;
import static br.com.gabriel.chamados.ticket.TicketDtos.*;

@Service
public class TicketApplicationService {
    private static final Logger LOGGER = Logger.getLogger(TicketApplicationService.class.getName());
    private final TicketRepository repository;
    private final TicketRules rules;
    private final RestClient restClient;
    private final String notificationServiceUrl;
    private final Clock clock;

    public TicketApplicationService(TicketRepository repository, RestClient restClient,
                                    @Value("${services.notification.url:http://localhost:8082}") String notificationServiceUrl) {
        this(repository, restClient, notificationServiceUrl, Clock.systemUTC());
    }

    TicketApplicationService(TicketRepository repository, RestClient restClient,
                             String notificationServiceUrl, Clock clock) {
        this.repository = repository;
        this.restClient = restClient;
        this.notificationServiceUrl = notificationServiceUrl;
        this.clock = clock;
        this.rules = new TicketRules();
    }

    @Transactional
    public TicketResponse create(CreateRequest request) {
        Instant now = clock.instant();
        Priority priority = rules.calculatePriority(request.category(), request.impact(), request.urgency());
        Instant dueAt = rules.calculateDueAt(priority, now);
        Ticket ticket = repository.save(new Ticket(request.title().trim(), request.description().trim(),
                request.requesterEmail().trim().toLowerCase(), request.category(), request.impact(),
                request.urgency(), priority, now, dueAt));
        TicketResponse response = TicketResponse.from(ticket);
        notify(response);
        return response;
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> list() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream().map(TicketResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public TicketResponse get(UUID id) {
        return TicketResponse.from(find(id));
    }

    @Transactional
    public TicketResponse changeStatus(UUID id, Status target) {
        Ticket ticket = find(id);
        rules.validateTransition(ticket.getStatus(), target);
        ticket.changeStatus(target);
        TicketResponse response = TicketResponse.from(repository.save(ticket));
        notify(response);
        return response;
    }

    private Ticket find(UUID id) {
        return repository.findById(id).orElseThrow(() -> new TicketNotFoundException(id));
    }

    private void notify(TicketResponse ticket) {
        try {
            restClient.post().uri(notificationServiceUrl + "/api/notifications")
                    .body(new NotificationRequest(ticket.id(), ticket.requesterEmail(), ticket.title(), ticket.status().name()))
                    .retrieve().toBodilessEntity();
        } catch (RestClientException ex) {
            LOGGER.log(Level.WARNING, "Notificação indisponível para o chamado {0}: {1}",
                    new Object[]{ticket.id(), ex.getMessage()});
        }
    }

    public static class TicketNotFoundException extends RuntimeException {
        public TicketNotFoundException(UUID id) { super("Chamado não encontrado: " + id); }
    }
}
