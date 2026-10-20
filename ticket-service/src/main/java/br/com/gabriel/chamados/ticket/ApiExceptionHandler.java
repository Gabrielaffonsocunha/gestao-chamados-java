package br.com.gabriel.chamados.ticket;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.stream.Collectors;

import static br.com.gabriel.chamados.ticket.TicketRules.BusinessRuleException;
import static br.com.gabriel.chamados.ticket.TicketApplicationService.TicketNotFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(TicketNotFoundException.class)
    ProblemDetail notFound(TicketNotFoundException exception) {
        return problem(HttpStatus.NOT_FOUND, "Chamado não encontrado", exception.getMessage());
    }

    @ExceptionHandler(BusinessRuleException.class)
    ProblemDetail businessRule(BusinessRuleException exception) {
        return problem(HttpStatus.CONFLICT, "Regra de negócio", exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail validation(MethodArgumentNotValidException exception) {
        String details = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return problem(HttpStatus.BAD_REQUEST, "Dados inválidos", details);
    }

    private ProblemDetail problem(HttpStatus status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setType(URI.create("about:blank"));
        return problem;
    }
}
