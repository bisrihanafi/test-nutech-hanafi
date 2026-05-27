/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hanafi.test.database;

import com.hanafi.test.model.response.Product;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;
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
public class ProductRepo {

    private final JdbcTemplate jdbcTemplate;

    public List<Product> findAll() {
        String sql = "SELECT service_code,service_name,service_icon, service_tariff FROM product LIMIT 10";

        try {
            return jdbcTemplate.query(sql, (ResultSet rs, int rowNum) -> {
                return new Product(rs.getString("service_code"), rs.getString("service_name"), rs.getString("service_icon"), rs.getLong("service_tariff"));
            });
        } catch (DataAccessException e) {
            return List.of();
        }
    }

    public Optional<Product> findByCode(String code) {
        String sql = "SELECT service_code,service_name,service_icon, service_tariff FROM product WHERE service_code = ? LIMIT 1";

        try {
            Product product = jdbcTemplate.queryForObject(sql, new Object[]{code}, (ResultSet rs, int rowNum) -> {
                return new Product(rs.getString("service_code"), rs.getString("service_name"), rs.getString("service_icon"), rs.getLong("service_tariff"));
            });
            return Optional.ofNullable(product);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

}
