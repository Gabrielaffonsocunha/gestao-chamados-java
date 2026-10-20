package br.com.gabriel.chamados.notification;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final ConcurrentLinkedDeque<Notification> notifications = new ConcurrentLinkedDeque<>();

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Notification receive(@Valid @RequestBody NotificationRequest request) {
        Notification notification = new Notification(UUID.randomUUID(), request.ticketId(),
                request.requesterEmail(), request.title(), request.status(), Instant.now());
        notifications.addFirst(notification);
        while (notifications.size() > 500) notifications.removeLast();
        return notification;
    }

    @GetMapping
    public List<Notification> list() { return List.copyOf(notifications); }

    public record NotificationRequest(@NotNull UUID ticketId, @NotBlank @Email String requesterEmail,
                                       @NotBlank String title, @NotBlank String status) { }

    public record Notification(UUID id, UUID ticketId, String requesterEmail, String title,
                               String status, Instant createdAt) { }
}
