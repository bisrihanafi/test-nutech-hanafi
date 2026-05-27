/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hanafi.test.controller;

import com.hanafi.test.database.BannerRepo;
import com.hanafi.test.model.response.ApiResponse;
import com.hanafi.test.model.response.Banners;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Hanafi
 */
@RestController
@AllArgsConstructor
public class ControllerBanner {

    private final BannerRepo bannerRapo;

    @GetMapping(path = "/banner", produces = {"application/json"})
    public ApiResponse<?> banners() throws Exception {
        try {
            List<Banners> findAll = bannerRapo.findAll();
            return ApiResponse.sucess("Sukses", findAll);
        } catch (Exception er) {
            throw new Exception("Terjadi kesalahan Sistem");
        }
    }

}
