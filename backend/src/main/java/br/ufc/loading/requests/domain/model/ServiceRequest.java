package br.ufc.loading.requests.domain.model;

import br.ufc.loading.requests.domain.enums.Priority;
import br.ufc.loading.requests.domain.enums.RequestStatus;
import br.ufc.loading.requests.domain.exception.DomainValidationException;
import br.ufc.loading.requests.domain.exception.InvalidStatusTransitionException;

import java.time.Instant;
import java.util.UUID;

public class ServiceRequest {

    public static final int TITLE_MAX_LENGTH = 120;
    public static final int DESCRIPTION_MAX_LENGTH = 2000;
    public static final int CATEGORY_MAX_LENGTH = 60;

    private final UUID id;
    private String title;
    private String description;
    private String category;
    private Priority priority;
    private RequestStatus status;
    private final UUID ownerId;
    private final String ownerName;
    private final Instant createdAt;
    private Instant updatedAt;

    private ServiceRequest(UUID id, String title, String description, String category, Priority priority,
                           RequestStatus status, UUID ownerId, String ownerName,
                           Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.status = status;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ServiceRequest open(String title, String description, String category, Priority priority,
                                      UUID ownerId, String ownerName, Instant now) {
        return new ServiceRequest(
                UUID.randomUUID(),
                TextRules.requireText(title, "Título", TITLE_MAX_LENGTH),
                TextRules.requireText(description, "Descrição", DESCRIPTION_MAX_LENGTH),
                TextRules.requireText(category, "Categoria", CATEGORY_MAX_LENGTH),
                requirePriority(priority),
                RequestStatus.OPEN,
                ownerId,
                ownerName,
                now,
                now);
    }

    public static ServiceRequest restore(UUID id, String title, String description, String category,
                                         Priority priority, RequestStatus status, UUID ownerId,
                                         String ownerName, Instant createdAt, Instant updatedAt) {
        return new ServiceRequest(id, title, description, category, priority, status,
                ownerId, ownerName, createdAt, updatedAt);
    }

    public void edit(String newTitle, String newDescription, String newCategory, Priority newPriority,
                     Instant now) {
        this.title = TextRules.requireText(newTitle, "Título", TITLE_MAX_LENGTH);
        this.description = TextRules.requireText(newDescription, "Descrição", DESCRIPTION_MAX_LENGTH);
        this.category = TextRules.requireText(newCategory, "Categoria", CATEGORY_MAX_LENGTH);
        this.priority = requirePriority(newPriority);
        this.updatedAt = now;
    }

    public RequestStatus moveTo(RequestStatus target, Instant now) {
        if (target == null) {
            throw new DomainValidationException("Status é obrigatório.");
        }
        if (!status.canTransitionTo(target)) {
            throw new InvalidStatusTransitionException(status, target);
        }
        RequestStatus previous = status;
        this.status = target;
        this.updatedAt = now;
        return previous;
    }

    public boolean isOwnedBy(UUID userId) {
        return ownerId.equals(userId);
    }

    private static Priority requirePriority(Priority priority) {
        if (priority == null) {
            throw new DomainValidationException("Prioridade é obrigatória.");
        }
        return priority;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public Priority getPriority() {
        return priority;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
