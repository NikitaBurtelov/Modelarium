set search_path to users_schema;

CREATE TABLE follows
(
    id            UUID NOT NULL,
    user_id       UUID,
    subscriber_id UUID,
    created_at    TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_follows PRIMARY KEY (id)
);

