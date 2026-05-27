/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hanafi.test.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * @author Hanafi
 */
@AllArgsConstructor
@Getter
public class Product {

    private final String service_code,service_name,service_icon;
    
    private final long service_tariff;
}
