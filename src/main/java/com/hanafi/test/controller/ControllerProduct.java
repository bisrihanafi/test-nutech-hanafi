/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hanafi.test.controller;

import com.hanafi.test.database.ProductRepo;
import com.hanafi.test.helper.JwtUtil;
import com.hanafi.test.model.response.ApiResponse;
import com.hanafi.test.model.response.Product;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Hanafi
 */
@RestController
@AllArgsConstructor
public class ControllerProduct {

    private final ProductRepo productRepo;
    private final JwtUtil jwtUtil;

    @GetMapping(path = "/services", produces = {"application/json"})
    public ApiResponse<?> products(@RequestHeader("Authorization") String authString) throws Exception {
        try {
            String email = "";
            if (!authString.startsWith("Bearer ")) {
                return ApiResponse.failed("108", "Header Authorization Tidak Valid");
            }

            try {
                String token = authString.substring(7);
                email = jwtUtil.extractUsername(token);
            } catch (ExpiredJwtException e) {
                return ApiResponse.failed("108", "Token Expired");
            } catch (JwtException e) {
                return ApiResponse.failed("108", "Token Tidak Valid");
            }

            List<Product> findAll = productRepo.findAll();
            return ApiResponse.sucess("Sukses", findAll);
        } catch (Exception er) {
            throw new Exception("Terjadi kesalahan Sistem");
        }
    }

}
