--liquibase formatted sql

--changeset maksym.herbin:add_s3_key_column
ALTER TABLE photo ADD COLUMN s3_object_key nvarchar(80) not null;