/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hanafi.test.database;

import com.hanafi.test.model.response.User;
import com.hanafi.test.model.response.Profile;
import com.hanafi.test.helper.Validator;
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
public class UserRepo {

    private final JdbcTemplate jdbcTemplate;
    private final Validator validator;

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT email,password,first_name,last_name,status FROM users WHERE email = ?";

        try {
            User user = jdbcTemplate.queryForObject(sql, new Object[]{email}, (ResultSet rs, int rowNum) -> {
                return new User(rs.getString("email"), rs.getString("password"), rs.getString("first_name"), rs.getString("last_name"), rs.getInt("status"));
            });
            return Optional.ofNullable(user);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Profile> findByEmailToPRofile(String email) {
        String sql = "SELECT email,first_name,last_name,profile_image FROM users WHERE email = ?";

        try {
            Profile user = jdbcTemplate.queryForObject(sql, new Object[]{email}, (ResultSet rs, int rowNum) -> {
                return new Profile(rs.getString("email"), rs.getString("first_name"), rs.getString("last_name"), rs.getString("profile_image"));
            });
            return Optional.ofNullable(user);
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean insertUser(String email, String firstName, String lastName, String passsword) {
        String sql = "INSERT INTO users (email,password,first_name,last_name,status,lastinsert) VALUE (?,?,?,?,?, NOW())";
        try {
            passsword = validator.hashSHA256(passsword);
            if (jdbcTemplate.update(sql, new Object[]{email, passsword, firstName, lastName, 1}) > 0) {
                sql = "INSERT INTO balance (email,balance) VALUE (?,?)";
                try {
                    return jdbcTemplate.update(sql, new Object[]{email, 0}) > 0;//saldo diisi default hanya untuk demo
                } catch (DataAccessException e) {
                    return false;
                }
            } else {
                return false;
            }
        } catch (DataAccessException e) {
            return false;
        }

    }
    public boolean updateUser(String email, String firstName, String lastName) {
        String sql = "UPDATE users SET first_name = ?, last_name = ?  WHERE email = ?";
        try {
            return jdbcTemplate.update(sql, new Object[]{firstName, lastName, email}) > 0;
        } catch (DataAccessException e) {
            return false;
        }

    }
    public boolean updateImage(String email, String image) {
        String sql = "UPDATE users SET profile_image = ?  WHERE email = ?";
        try {
            return jdbcTemplate.update(sql, new Object[]{image, email}) > 0;
        } catch (DataAccessException e) {
            return false;
        }

    }
}
