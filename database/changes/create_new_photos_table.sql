--liquibase formatted sql

--changeset maksym.herbin:create_new_photos_table
create table cm_photo (
    object_key nvarchar(80) not null primary key,
    labels nvarchar(200),
    description nvarchar(200),
    cognito_username nvarchar(150),
    s3_object_key nvarchar(80) not null,
    created_datetime DATETIME DEFAULT now()
    );

--changeset maksym.herbin:grant_web_user_new_photos_table_access
GRANT SELECT, INSERT, UPDATE, DELETE on cm_photo to 'web_user';