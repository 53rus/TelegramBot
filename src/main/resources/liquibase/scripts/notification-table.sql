-- liquibase formatted sql
-- changeset miliuschenko:1

CREATE TABLE notification_task (
	id serial ,
	chat_id bigint,
	message_text VARCHAR(255),
	date_time timestamp
);