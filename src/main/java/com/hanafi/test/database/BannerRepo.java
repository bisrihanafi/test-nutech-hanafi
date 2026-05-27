/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hanafi.test.database;

import com.hanafi.test.model.response.Banners;
import java.sql.ResultSet;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Hanafi
 */
@Repository
@AllArgsConstructor
public class BannerRepo {

    private final JdbcTemplate jdbcTemplate;

    public List<Banners> findAll() {
        String sql = "SELECT banner_name,banner_image,description FROM banners LIMIT 10";

        try {
            return jdbcTemplate.query(sql, (ResultSet rs, int rowNum) -> {
                return new Banners(rs.getString("banner_name"), rs.getString("banner_image"), rs.getString("description"));
            });
        } catch (DataAccessException e) {
            return List.of();
        }
    }

}
