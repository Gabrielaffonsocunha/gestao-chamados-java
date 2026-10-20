package br.com.gabriel.chamados.ticket;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tickets")
public class Ticket {
    public enum Category { ACCESS, HARDWARE, SOFTWARE, NETWORK, SECURITY, OTHER }
    public enum Impact { LOW, MEDIUM, HIGH }
    public enum Urgency { LOW, MEDIUM, HIGH }
    public enum Priority { LOW, MEDIUM, HIGH, CRITICAL }
    public enum Status { OPEN, IN_PROGRESS, WAITING_USER, RESOLVED, CLOSED, CANCELLED }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Version
    private long version;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 3000)
    private String description;

    @Column(nullable = false, length = 254)
    private String requesterEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Impact impact;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Urgency urgency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant dueAt;

    protected Ticket() { }

    public Ticket(String title, String description, String requesterEmail, Category category,
                  Impact impact, Urgency urgency, Priority priority, Instant createdAt, Instant dueAt) {
        this.title = title;
        this.description = description;
        this.requesterEmail = requesterEmail;
        this.category = category;
        this.impact = impact;
        this.urgency = urgency;
        this.priority = priority;
        this.status = Status.OPEN;
        this.createdAt = createdAt;
        this.dueAt = dueAt;
    }

    public void changeStatus(Status status) { this.status = status; }
    public UUID getId() { return id; }
    public long getVersion() { return version; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getRequesterEmail() { return requesterEmail; }
    public Category getCategory() { return category; }
    public Impact getImpact() { return impact; }
    public Urgency getUrgency() { return urgency; }
    public Priority getPriority() { return priority; }
    public Status getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getDueAt() { return dueAt; }
}
