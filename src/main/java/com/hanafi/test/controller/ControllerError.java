/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hanafi.test.controller;

import com.hanafi.test.model.response.ApiResponse;
import com.hanafi.test.model.response.BadRequestHandling;
import com.hanafi.test.model.response.NotFoundHandling;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Bisri Hanafi bisrihanafi@designjaya.com
 *
 * May 20, 2026 10:57:14 AM
 *
 * @author HP
 */
@ControllerAdvice
public class ControllerError {

    @ExceptionHandler(BadRequestHandling.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ApiResponse<?> badRequestHandle(BadRequestHandling error) {
        return ApiResponse.failed("97", error.getMessage());
    }

    @ExceptionHandler(NotFoundHandling.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ApiResponse<?> notFoundHandle(NotFoundHandling error) {
        return ApiResponse.failed("98", error.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ApiResponse<?> genHandle(Exception error) {
        return ApiResponse.failed("99", error.getMessage());
    }


}
