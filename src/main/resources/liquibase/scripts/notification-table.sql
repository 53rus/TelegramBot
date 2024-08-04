-- liquibase formatted sql
-- changeset miliuschenko:1

CREATE TABLE notification_table (
	id bigserial PRIMARY KEY,
	chat_id int8 NOT NULL,
	message VARCHAR(255) NOT NULL,
	date_time timestamp NOT NULL
);