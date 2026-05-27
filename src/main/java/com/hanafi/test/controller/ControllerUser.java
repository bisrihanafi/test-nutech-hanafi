/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hanafi.test.controller;

import com.hanafi.test.database.ImageRepo;
import com.hanafi.test.helper.JwtUtil;
import com.hanafi.test.model.response.User;
import com.hanafi.test.database.UserRepo;
import com.hanafi.test.helper.Validator;
import com.hanafi.test.model.request.Registration;
import com.hanafi.test.model.response.ApiResponse;
import com.hanafi.test.model.response.Image;
import com.hanafi.test.model.response.Login;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Hanafi
 */
@RestController
@AllArgsConstructor
public class ControllerUser {

    private final UserRepo userRepo;
    private final ImageRepo imageRepo;
    private final Validator validator;
    private final JwtUtil jwtUtil;

    @PostMapping(path = "/registration", consumes = {"application/json"}, produces = {"application/json"})
    public ApiResponse<?> registration(@RequestBody Registration requestRegistrasi) throws Exception {
        if (!validator.emailValidator(requestRegistrasi.getEmail())) {
            return ApiResponse.failed("102", "Format Email tidak sesuai");
        }

        if (!validator.passwrdValidation(requestRegistrasi.getPassword())) {
            return ApiResponse.failed("102", "Password minimal harus 8 karakter");
        }

        Optional<User> user = userRepo.findByEmail(requestRegistrasi.getEmail());
        if (!user.isEmpty()) {
            return ApiResponse.failed("101", "Email sudah terdaftar");
        }

        if (!userRepo.insertUser(requestRegistrasi.getEmail(), requestRegistrasi.getFirst_name(), requestRegistrasi.getLast_name(), requestRegistrasi.getPassword())) {
            throw new Exception("Terjadi Kesalahan Sistem");
        }

        return ApiResponse.sucess("Registrasi berhasil silahkan login", null);

    }

    @PostMapping(path = "/login", consumes = {"application/json"}, produces = {"application/json"})
    public ApiResponse<?> login(@RequestBody Registration requestRegistrasi) throws Exception {
        if (!validator.emailValidator(requestRegistrasi.getEmail())) {
            return ApiResponse.failed("102", "Format Email tidak sesuai");
        }

        if (!validator.passwrdValidation(requestRegistrasi.getPassword())) {
            return ApiResponse.failed("102", "Password minimal harus 8 karakter");
        }

        Optional<User> user = userRepo.findByEmail(requestRegistrasi.getEmail());
        if (user.isEmpty()) {
            return ApiResponse.failed("101", "User belum terdaftar");
        }

        User userGet = user.get();
        if (!userGet.getEmail().equalsIgnoreCase(requestRegistrasi.getEmail())
                || !userGet.getPassword().equals(validator.hashSHA256(requestRegistrasi.getPassword()))) {
            return ApiResponse.failed("103", "Kombinasi email dan password salah");
        }

        String token = jwtUtil.generateToken(requestRegistrasi.getEmail());

        return ApiResponse.sucess("Login Sukses", new Login(token));

    }

    @GetMapping(path = "/profile", produces = {"application/json"})
    public ApiResponse<?> profile(@RequestHeader("Authorization") String authString) throws Exception {
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

            Optional<com.hanafi.test.model.response.Profile> user = userRepo.findByEmailToPRofile(email);
            if (user.isEmpty()) {
                return ApiResponse.failed("101", "User belum terdaftar");
            }

            return ApiResponse.sucess("Sukses", user.get());
        } catch (Exception er) {
            throw new Exception("Terjadi kesalahan Sistem");
        }
    }

    @Transactional
    @PutMapping(path = "/profile/update", consumes = {"application/json"}, produces = {"application/json"})
    public ApiResponse<?> profileUpdate(@RequestHeader("Authorization") String authString, @RequestBody com.hanafi.test.model.request.Profile profile) throws Exception {
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

            if (!userRepo.updateUser(email, profile.getFirst_name(), profile.getLast_name())) {
                return ApiResponse.failed("107", "Gagal Update");
            }

            Optional<com.hanafi.test.model.response.Profile> user = userRepo.findByEmailToPRofile(email);
            if (user.isEmpty()) {
                return ApiResponse.failed("101", "User belum terdaftar");
            }

            return ApiResponse.sucess("Sukses", user.get());
        } catch (Exception er) {
            throw new Exception("Terjadi kesalahan Sistem");
        }
    }

    @PutMapping("/profile/image")
    public ApiResponse<?> profileImage(@RequestHeader("Authorization") String authString, @RequestParam("file") MultipartFile file) {

        String email = "";
        if (!authString.startsWith("Bearer ")) {
            return ApiResponse.failed("10", "Header Authorization Tidak Valid");
        }

        try {
            String token = authString.substring(7);
            email = jwtUtil.extractUsername(token);
        } catch (ExpiredJwtException e) {
            return ApiResponse.failed("10", "Token Expired");
        } catch (JwtException e) {
            return ApiResponse.failed("10", "Token Tidak Valid");
        }

        List<String> ALLOWED_TYPES = Arrays.asList("image/png", "image/jpeg", "image/jpg");

        if (file.isEmpty()) {
            return ApiResponse.failed("102", "Image Empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            return ApiResponse.failed("102", "Format Image tidak sesuai");
        }

        if (!imageRepo.insertUserImage(email, file, contentType)) {
            return ApiResponse.failed("102", "Gagal Upload Image");
        }

        if (!userRepo.updateImage(email, "/profile/image")) {
            return ApiResponse.failed("6", "Gagal Update URL");
        }

        Optional<com.hanafi.test.model.response.Profile> user = userRepo.findByEmailToPRofile(email);
        if (user.isEmpty()) {
            return ApiResponse.failed("02", "User belum terdaftar");
        }

        return ApiResponse.sucess("Sukses", user.get());
    }

    @GetMapping("/profile/image")
    public ResponseEntity<?> profileImageView(@RequestHeader("Authorization") String authString) {

        if (authString == null || !authString.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Header Authorization Tidak Valid");
        }

        String email = "";
        try {
            String token = authString.substring(7);
            email = jwtUtil.extractUsername(token);
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token Expired");
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token Tidak Valid");
        }

        Optional<Image> findByEmail = imageRepo.findByEmail(email);

        if (findByEmail.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Gambar profil tidak ditemukan");
        }

        Image userImage = findByEmail.get();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(userImage.getContentType()));

        return new ResponseEntity<>(userImage.getImageBytes(), headers, HttpStatus.OK);
    }

}
