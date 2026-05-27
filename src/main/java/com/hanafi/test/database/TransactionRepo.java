/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hanafi.test.database;

import com.hanafi.test.model.response.Transaction;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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
public class TransactionRepo {

    private final JdbcTemplate jdbcTemplate;

    public boolean insertTrx(String email, String code, String name, String invoice, long amount) {
        String sql = "INSERT INTO transactions (invoice,email,service_code,service_name,transaction_type,total_amount,lastinsert) VALUES (?,?,?,?,?,?,NOW())";
        try {
            return jdbcTemplate.update(sql, new Object[]{invoice, email, code, name, "PAYMENT", amount}) > 0;
        } catch (DataAccessException e) {
            return false;
        }

    }

    public Optional<Transaction> findByInvoice(String invoice) {
        String sql = "SELECT invoice,service_code,service_name,transaction_type,lastinsert,total_amount FROM transactions WHERE invoice = ? LIMIT 1";

        try {
            Transaction trx = jdbcTemplate.queryForObject(sql, new Object[]{invoice}, (ResultSet rs, int rowNum) -> {

                Timestamp ts = rs.getTimestamp("lastinsert");
                Instant instant = ts.toInstant();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC);
                String date = formatter.format(instant);

                return new Transaction(rs.getString("invoice"), rs.getString("service_code"), rs.getString("service_name"), rs.getString("transaction_type"), date, rs.getLong("total_amount"));
            });
            return Optional.ofNullable(trx);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Transaction> findAll(String email) {
        String sql = "SELECT invoice,service_code,service_name,transaction_type,lastinsert,total_amount FROM transactions WHERE email = ? ";

        try {
            return jdbcTemplate.query(sql, new Object[]{email}, (ResultSet rs, int rowNum) -> {

                Timestamp ts = rs.getTimestamp("lastinsert");
                Instant instant = ts.toInstant();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").withZone(ZoneId.of("Asia/Jakarta"));
                String date = formatter.format(instant);

                return new Transaction(rs.getString("invoice"), rs.getString("service_code"), rs.getString("service_name"), rs.getString("transaction_type"), date, rs.getLong("total_amount"));
            });
        } catch (DataAccessException e) {
            return List.of();
        }
    }

    public List<Transaction> findAll(String email, int offset, int limit) {
        String sql = "SELECT invoice,service_code,service_name,transaction_type,lastinsert,total_amount FROM transactions WHERE email = ? LIMIT ?, ?";

        try {
            return jdbcTemplate.query(sql, new Object[]{email, offset, limit}, (ResultSet rs, int rowNum) -> {

                Timestamp ts = rs.getTimestamp("lastinsert");
                Instant instant = ts.toInstant();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").withZone(ZoneId.of("Asia/Jakarta"));

                String date = formatter.format(instant);

                return new Transaction(rs.getString("invoice"), rs.getString("service_code"), rs.getString("service_name"), rs.getString("transaction_type"), date, rs.getLong("total_amount"));
            });
        } catch (DataAccessException e) {
            return List.of();
        }
    }
}
