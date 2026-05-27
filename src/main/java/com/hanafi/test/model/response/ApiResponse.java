/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hanafi.test.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Bisri Hanafi bisrihanafi@designjaya.com
 *
 * May 20, 2026 10:30:59 AM
 *
 * @author HP
 * @param <T>
 */
@AllArgsConstructor
public class ApiResponse<T> {

    @Getter
    private final String status;
    @Getter
    private final String message;
    @Getter
    private final T data;

    public static <T> ApiResponse<T> sucess(String message, T data) {
        return new ApiResponse<>("00", message, data);
    }

    public static <T> ApiResponse<T> failed(String code, String message) {
        return new ApiResponse<>(code, message, null);
    }

}
