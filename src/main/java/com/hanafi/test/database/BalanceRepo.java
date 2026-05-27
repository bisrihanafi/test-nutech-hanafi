/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hanafi.test.database;

import com.hanafi.test.model.response.Balance;
import java.sql.ResultSet;
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
public class BalanceRepo {

    private final JdbcTemplate jdbcTemplate;

    public Optional<Balance> findByEmail(String email) {
        String sql = "SELECT balance FROM balance WHERE email = ?";

        try {
            Balance m = jdbcTemplate.queryForObject(sql, new Object[]{email}, (ResultSet rs, int rowNum) -> {
                return new Balance(rs.getLong("balance"));
            });
            return Optional.ofNullable(m);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean plusBalance(String email, long balance) {
        String sql = "UPDATE balance SET balance = balance + ? WHERE email = ?";
        try {
            return jdbcTemplate.update(sql, new Object[]{balance, email}) > 0;
        } catch (DataAccessException e) {
            return false;
        }

    }
    public boolean reduceBalance(String email, long reducer) {
        String sql = "UPDATE balance SET balance = balance - ? WHERE email = ?";
        try {
            return jdbcTemplate.update(sql, new Object[]{reducer, email}) > 0;
        } catch (DataAccessException e) {
            return false;
        }

    }

}
