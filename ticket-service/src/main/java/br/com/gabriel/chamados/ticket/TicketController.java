package br.com.gabriel.chamados.ticket;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static br.com.gabriel.chamados.ticket.TicketDtos.*;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    private final TicketApplicationService service;

    public TicketController(TicketApplicationService service) { this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse create(@Valid @RequestBody CreateRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<TicketResponse> list() { return service.list(); }

    @GetMapping("/{id}")
    public TicketResponse get(@PathVariable UUID id) { return service.get(id); }

    @PatchMapping("/{id}/status")
    public TicketResponse changeStatus(@PathVariable UUID id, @Valid @RequestBody ChangeStatusRequest request) {
        return service.changeStatus(id, request.status());
    }
}
