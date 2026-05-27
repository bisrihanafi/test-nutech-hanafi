/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hanafi.test.database;

import com.hanafi.test.model.response.Image;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Hanafi
 */
@Repository
@AllArgsConstructor
public class ImageRepo {

    private final JdbcTemplate jdbcTemplate;

    public boolean insertUserImage(String email, MultipartFile file, String contentType) {
        String sql = "REPLACE INTO images (email, type, image, lastinsert) VALUES (?, ?, ?, NOW())";

        try {
            return jdbcTemplate.update(sql, email, contentType, file.getBytes()) > 0;

        } catch (IOException | DataAccessException e) {
            return false;
        }
    }

    public Optional<Image> findByEmail(String email) {
        String sql = "SELECT image,type FROM images WHERE email = ? LIMIT 1";

        try {
            Image m = jdbcTemplate.queryForObject(sql, new Object[]{email}, (ResultSet rs, int rowNum) -> {
                return new Image(rs.getString("type"), rs.getBytes("image"));
            });
            return Optional.ofNullable(m);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }
}
