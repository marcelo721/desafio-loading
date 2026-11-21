CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT ck_users_role CHECK (role IN ('ADMIN', 'USER'))
);

CREATE TABLE service_requests (
    id UUID PRIMARY KEY,
    title VARCHAR(120) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    category VARCHAR(60) NOT NULL,
    priority SMALLINT NOT NULL,
    status SMALLINT NOT NULL,
    owner_id UUID NOT NULL,
    owner_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_service_requests_owner FOREIGN KEY (owner_id) REFERENCES users (id),
    CONSTRAINT ck_service_requests_priority CHECK (priority BETWEEN 0 AND 2),
    CONSTRAINT ck_service_requests_status CHECK (status BETWEEN 0 AND 2)
);

CREATE INDEX idx_service_requests_status ON service_requests (status);
CREATE INDEX idx_service_requests_priority ON service_requests (priority);
CREATE INDEX idx_service_requests_owner ON service_requests (owner_id);
CREATE INDEX idx_service_requests_created_at ON service_requests (created_at);

CREATE TABLE status_history (
    id UUID PRIMARY KEY,
    request_id UUID NOT NULL,
    from_status SMALLINT,
    to_status SMALLINT NOT NULL,
    changed_by UUID NOT NULL,
    changed_by_name VARCHAR(100) NOT NULL,
    changed_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_status_history_request FOREIGN KEY (request_id)
        REFERENCES service_requests (id) ON DELETE CASCADE
);

CREATE INDEX idx_status_history_request ON status_history (request_id, changed_at);

CREATE TABLE audit_log (
    id UUID PRIMARY KEY,
    entity_type VARCHAR(30) NOT NULL,
    entity_id UUID NOT NULL,
    operation VARCHAR(30) NOT NULL,
    user_id UUID NOT NULL,
    user_name VARCHAR(100) NOT NULL,
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL,
    details VARCHAR(500)
);

CREATE INDEX idx_audit_log_occurred_at ON audit_log (occurred_at);
CREATE INDEX idx_audit_log_entity ON audit_log (entity_type, entity_id);
