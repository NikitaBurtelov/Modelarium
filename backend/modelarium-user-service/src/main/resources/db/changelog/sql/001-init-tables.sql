set search_path to users_schema;

create table users
(
    id               uuid not null default gen_random_uuid() primary key,

    username         varchar(255) not null unique,
    email            varchar(255) not null unique,

    password_hash    varchar(255) not null,

    display_name     varchar(255),

    bio              text,

    avatar_key       varchar(255),

    email_verified   boolean not null default false,

    created_at       timestamptz not null default now(),
    updated_at       timestamptz not null default now(),

    sequence_id      bigint generated always as identity unique,

    popularity_index integer not null default 0
);

create index idx_users_email
    on users (email);