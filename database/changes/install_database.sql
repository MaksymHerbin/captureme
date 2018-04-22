--liquibase formatted sql

--changeset maksym.herbin:create_photo_table
create table photo (
    object_key nvarchar(80) not null primary key,
    labels nvarchar(200),
    description nvarchar(200),
    cognito_username nvarchar(150),
    created_datetime DATETIME DEFAULT now()
    );

--changeset maksym.herbin:create_web_user
CREATE USER 'web_user' IDENTIFIED BY 'password';

--changeset maksym.herbin:grant_web_user_photos_access
GRANT SELECT, INSERT, UPDATE, DELETE on photo to 'web_user';

