/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hanafi.test.helper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/**
 *
 * @author Hanafi
 */
@Component
public class Validator {

    private final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";//patern berasal dari referensi internet
    private final Pattern PATTERN = Pattern.compile(EMAIL_REGEX);

    public boolean emailValidator(String email) {

        if (email == null) {
            return false;
        }
        return PATTERN.matcher(email).matches();

    }

    public boolean passwrdValidation(String password) {
        return password.length() >= 8;
    }

    public String hashSHA256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
