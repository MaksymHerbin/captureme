package com.herbinm.edx.captureme.gateway.photos.service.storage.info;

import com.amazonaws.xray.spring.aop.XRayEnabled;
import com.herbinm.edx.captureme.gateway.photos.domain.Photo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.herbinm.edx.captureme.gateway.photos.domain.Photo.aPhoto;

@Repository
@XRayEnabled
public class DatabasePhotoDetailsStorage implements PhotoDetailsStorage {

    private static final String SELECT_BASE = "SELECT object_key, s3_object_key, labels, created_datetime, cognito_username FROM cm_photo";
    private final JdbcTemplate jdbcTemplate;

    @Inject
    public DatabasePhotoDetailsStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Photo photo, String userName) {
        jdbcTemplate.update(
                "INSERT INTO cm_photo(object_key, s3_object_key, labels, cognito_username) VALUES (?,?,?,?)",
                photo.getObjectKey(),
                photo.getS3ObjectKey(),
                photo.getLabels().stream().collect(Collectors.joining(",")),
                userName
        );
    }

    @Override
    public List<Photo> allPhotos(String userId) {
        return jdbcTemplate.query(
                SELECT_BASE + " WHERE cognito_username = ? order by created_datetime DESC",
                new Object[]{userId},
                new PhotoRowMapper()
        );
    }

    @Override
    public Photo loadPhoto(String photoId) {
        return jdbcTemplate.queryForObject(
                SELECT_BASE + " WHERE object_key = ?",
                new Object[]{photoId},
                new PhotoRowMapper()
        );
    }

    @Override
    public void delete(String objectKey, String userId) {
        jdbcTemplate.update(
                "DELETE FROM cm_photo where object_key=? and  cognito_username = ?",
                objectKey,
                userId
        );
    }

    private class PhotoRowMapper implements RowMapper<Photo> {

        @Override
        public Photo mapRow(ResultSet resultSet, int i) throws SQLException {

            return aPhoto(resultSet.getString("object_key"), resultSet.getString("s3_object_key"))
                    .labels(newArrayList(resultSet.getString("labels").split(",")))
                    .uploadedAt(new Date(resultSet.getTimestamp("created_datetime").getTime()))
                    .userId(resultSet.getString("cognito_username"))
                    .build();
        }
    }

}
