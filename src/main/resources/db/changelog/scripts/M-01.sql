-- liquibase formatted sql

-- changeset dsiliukov:${changeset.id.sequence}

-- Создание таблицы для хранения информации о пользователе

CREATE TABLE Users (
    id BIGSERIAL PRIMARY KEY,
    chat_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    UNIQUE(chat_id)
);