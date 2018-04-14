package com.herbinm.edx.captureme.gateway.service.storage;

import com.herbinm.edx.captureme.gateway.domain.Photo;
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
import static com.herbinm.edx.captureme.gateway.domain.Photo.aPhoto;

@Repository
public class DatabasePhotoDetailsStorage implements PhotoDetailsStorage {

    private final JdbcTemplate jdbcTemplate;

    @Inject
    public DatabasePhotoDetailsStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Photo photo) {
        jdbcTemplate.update(
                "INSERT INTO photo(object_key, labels) VALUES (?, ?)",
                photo.getObjectKey(),
                photo.getLabels().stream().collect(Collectors.joining(","))
        );
    }

    @Override
    public Photo load(String objectKey) {
        return jdbcTemplate.queryForObject(
                "SELECT object_key, labels, created_datetime FROM photo WHERE cognito_username is null and object_key = ?",
                new String[]{objectKey},
                new PhotoRowMapper()
        );
    }

    @Override
    public List<Photo> allPhotos() {
        return jdbcTemplate.query(
                "SELECT object_key, labels, created_datetime FROM photo WHERE cognito_username is null order by created_datetime DESC",
                new PhotoRowMapper()
        );
    }

    private class PhotoRowMapper implements RowMapper<Photo> {

        @Override
        public Photo mapRow(ResultSet resultSet, int i) throws SQLException {

            return aPhoto(resultSet.getString("object_key"))
                    .labels(newArrayList(resultSet.getString("labels").split(",")))
                    .uploadedAt(new Date(resultSet.getTimestamp("created_datetime").getTime()))
                    .build();
        }
    }

}
